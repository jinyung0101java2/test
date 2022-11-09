package org.paasta.container.terraman.api.common.terramanproc;

import com.jcraft.jsch.*;
import org.apache.commons.lang3.StringUtils;
import org.paasta.container.terraman.api.common.constants.Constants;
import org.paasta.container.terraman.api.common.constants.TerramanConstant;
import org.paasta.container.terraman.api.common.service.CommandService;
import org.paasta.container.terraman.api.common.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class CommandProcess {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandService.class);

    private Session session;
    private ChannelExec channelExec;
    private ChannelSftp channelSftp;
    private Channel channel;

    /**
     * SSH Connect
     *
     * @param host the host
     * @return the void
     */
    public void sshConnect(String host, String idRsa, String userName) throws JSchException {
        int port = 22;
        JSch jsch = new JSch();
        jsch.addIdentity(idRsa);
        session = jsch.getSession(userName, host, port);
        session.setConfig("StrictHostKeyChecking", "no");       // 호스트 정보를 검사하지 않도록 설정
        session.connect();
    }

    /**
     * 디렉토리( or 파일) 존재 여부
     * @param path the path
     * @return the boolean
     */
    public boolean exists(String path) {
        Vector res = null;
        try {
            res = channelSftp.ls(path);
        } catch (SftpException e) {
            if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                return false;
            }
        }
        return res != null && !res.isEmpty();
    }

    /**
     * SSH Disconnect
     *
     * @return the void
     */
    public void disConnectSSH() {
        if (session != null) session.disconnect();
        if (channelExec != null) channelExec.disconnect();
        if (channelSftp != null) channelSftp.quit();
        if (channel != null) channel.disconnect();
    }

    /**
     * SSH 파일 업로드
     *
     * @param dir the dir
     * @param host the host
     * @param idRsa the idRsa
     * @param uploadFile the uploadFile
     * @return the String
     */
    public String sshFileUpload(String dir, String host, String idRsa, File uploadFile, String userName) {
        String resultCommand = Constants.RESULT_STATUS_FAIL;

        FileInputStream in = null;
        try {
            sshConnect(host, idRsa, userName);
            channel = session.openChannel("sftp");
            channel.connect();
            channelSftp = (ChannelSftp) channel;
            in = new FileInputStream(uploadFile);
            channelSftp.cd(dir);
            channelSftp.put(in, uploadFile.getName());
            // 업로드했는지 확인
            if (this.exists(dir +"/"+uploadFile.getName())) {
                resultCommand = Constants.RESULT_STATUS_SUCCESS;
            }
            in.close();
        } catch (SftpException | JSchException | IOException e) {
            LOGGER.error("Exception : {}", CommonUtils.loggerReplace(e.getMessage()));
        } finally {
            try {
                if(in != null) in.close();
            } catch (IOException ie) {
                LOGGER.error(CommonUtils.loggerReplace(ie.getMessage()));
            }
            this.disConnectSSH();
        }
        return resultCommand;
    }

    /**
     * SSH 파일 다운로드
     *
     * @param dir the dir
     * @param host the localDir
     * @param fileName the fileName
     * @param host the host
     * @param idRsa the idRsa
     * @return the void
     */
    public void sshFileDownload(String dir, String localDir, String fileName, String host, String idRsa, String userName){
        // 원하는 경로에 파일 생성
        InputStream is = null;
        FileOutputStream out = null;
        try {
            sshConnect(host, idRsa, userName);
            channel = session.openChannel("sftp");
            channel.connect();
            channelSftp = (ChannelSftp) channel;
            // 경로 이동
            channelSftp.cd(dir);
            is = channelSftp.get(fileName);
            // 원하는 경로에 파일 생성
            File localFile = new File(localDir);

            out = new FileOutputStream(localFile);

            int readCount = 0;
            while( (readCount = is.read()) > 0 ){
                out.write(readCount);
            }

            is.close();
            out.close();
        } catch (SftpException se) {
            LOGGER.error("SftpException : {}", CommonUtils.loggerReplace(se.getMessage()));
        } catch ( Exception e){
            LOGGER.error("Exception : {}", CommonUtils.loggerReplace(e.getMessage()));
        } finally {
            try {
                if(is != null) is.close();
                if(out != null) out.close();
            } catch (IOException ie) {
                LOGGER.error(CommonUtils.loggerReplace(ie.getMessage()));
            }
            this.disConnectSSH();
        }
    }

    /**
     * SSH Command Line Response
     *
     * @param command the command
     * @param dir the dir
     * @param host the host
     * @param idRsa the idRsa
     * @return the String
     */
    public String getSSHResponse(String command, String dir, String host, String idRsa, String userName) {
        String resultCommand = Constants.RESULT_STATUS_FAIL;
        String execCommand = "";
        StringBuilder response = new StringBuilder();
        try {
            sshConnect(host, idRsa, userName);
            channelExec = (ChannelExec) session.openChannel("exec");
            if(!StringUtils.equals(dir, "")) {
                execCommand = "cd " + dir + " && ";
            }
            execCommand += TerramanConstant.COMMAND_SWITCH(command);
            LOGGER.info("Command Str :: {}", execCommand);
            channelExec.setCommand(execCommand);
            InputStream inputStream = channelExec.getInputStream();
            channelExec.connect();

            byte[] buffer = new byte[8192];
            int decodedLength;
            while ((decodedLength = inputStream.read(buffer, 0, buffer.length)) > 0) {
                response.append(new String(buffer, 0, decodedLength));
            }
            resultCommand = response.toString();

        } catch (Exception e) {
            if(e.getMessage().contains("timed out")) {
                resultCommand = Constants.RESULT_STATUS_TIME_OUT;
            }
            LOGGER.error("JSchException : {}", CommonUtils.loggerReplace(e.getMessage()));
        } finally {
            this.disConnectSSH();
        }
        return resultCommand;
    }

    /**
     * Command Line Response
     *
     * @param command the command
     * @param dir the dir
     * @return the String
     */
    public String getResponse(String command, String dir) {
        String resultOutput = Constants.RESULT_STATUS_FAIL;
        List<String> cmd = new ArrayList<>();
        cmd.add(TerramanConstant.LINUX_BASH);
        cmd.add(TerramanConstant.LINUX_BASH_C);
        cmd.add(TerramanConstant.COMMAND_SWITCH(command));

        StringBuilder sb = new StringBuilder(1024);
        String s = null;
        ProcessBuilder prsbld = null;
        Process prs = null;

        try {
            prsbld = new ProcessBuilder(cmd);
            // 디렉토리 이동
            if(!StringUtils.equals(dir, "")) {
                prsbld.directory(new File(dir));
            }

            // 프로세스 수행시작
            prs = prsbld.start();
        } catch (IOException e) {
            LOGGER.error(CommonUtils.loggerReplace(e.getMessage()));
        }

        if(prs != null) {
            try (BufferedReader stdInput = new BufferedReader(new InputStreamReader(prs.getInputStream()));){
                while ((s = stdInput.readLine()) != null)
                {
                    sb.append(s + System.getProperty("line.separator"));
                }
                resultOutput = sb.toString();
                prs.getErrorStream().close();
                prs.getInputStream().close();
                prs.getOutputStream().close();

                // 종료까지 대기
                prs.waitFor();

            } catch (Exception e1) {
                if(e1.getMessage().contains("timed out")) {
                    resultOutput = Constants.RESULT_STATUS_TIME_OUT;
                } else {
                    resultOutput = Constants.RESULT_STATUS_FAIL;
                }
                LOGGER.error(CommonUtils.loggerReplace(e1.getMessage()));
            } finally {
                if(prs != null) {
                    try {
                        prs.destroy();
                    } catch(Exception e2) {
                        resultOutput = Constants.RESULT_STATUS_FAIL;
                        LOGGER.error(CommonUtils.loggerReplace(e2.getMessage()));
                    }
                }
            }
        }

        return resultOutput;
    }
}
