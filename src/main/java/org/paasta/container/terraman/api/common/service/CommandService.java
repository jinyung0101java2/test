package org.paasta.container.terraman.api.common.service;

import com.jcraft.jsch.*;
import org.apache.commons.lang3.StringUtils;
import org.paasta.container.terraman.api.common.constants.Constants;
import org.paasta.container.terraman.api.common.constants.TerramanConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

@Service
public class CommandService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandService.class);

    private Session session;
    private ChannelExec channelExec;
    private ChannelSftp channelSftp;
    private Channel channel;

    private void SSHConnect(String host, String idRsa) throws JSchException {
        String userName = "ubuntu";
        int port = 22;
        JSch jsch = new JSch();
        jsch.addIdentity(idRsa);
        session = jsch.getSession(userName, host, port);
        session.setConfig("StrictHostKeyChecking", "no");       // 호스트 정보를 검사하지 않도록 설정
        session.connect();
    }

    private void disConnectSSH() {
        if (session != null) session.disconnect();
        if (channelExec != null) channelExec.disconnect();
        if (channelSftp != null) channelSftp.quit();
        if (channel != null) channel.disconnect();
    }

    /**
     * 디렉토리( or 파일) 존재 여부
     * @param path 디렉토리 (or 파일)
     * @return
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
     * 파일 업로드
     *
     * @param dir 저장할 디렉토리
     * @param host ssh host
     * @param idRsa ssh key
     * @return 업로드 여부
     */
    public String SSHFileUpload(String dir, String host, String idRsa, File uploadFile) {
        String resultCommand = Constants.RESULT_STATUS_FAIL;
        SftpATTRS attrs;
        FileInputStream in = null;
        try {
            SSHConnect(host, idRsa);
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
        } catch (SftpException | JSchException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            this.disConnectSSH();
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultCommand;
    }

    /**
     * 파일 다운로드
     *
     * @param dir 저장할 디렉토리
     * @param host ssh host
     * @param idRsa ssh key
     * @return 업로드 여부
     */
    public void fileDownload(String dir, String localDir, String fileName, String host, String idRsa){
        InputStream is = null;
        FileOutputStream out = null;
        try {
            SSHConnect(host, idRsa);
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
            se.printStackTrace();
        } catch ( Exception e){
            e.printStackTrace();
        } finally {
            this.disConnectSSH();
        }
    }

    private String getSSHResponse(String command, String dir, String host, String idRsa) {
        String resultCommand = Constants.RESULT_STATUS_FAIL;
        StringBuilder response = new StringBuilder();
        try {
            SSHConnect(host, idRsa);
            channelExec = (ChannelExec) session.openChannel("exec");
            if(!StringUtils.equals(dir, "")) {
                command = "cd " + dir + " && " + command;
            }
            channelExec.setCommand(command);
            InputStream inputStream = channelExec.getInputStream();
            channelExec.connect();

            byte[] buffer = new byte[8192];
            int decodedLength;
            while ((decodedLength = inputStream.read(buffer, 0, buffer.length)) > 0) {
                response.append(new String(buffer, 0, decodedLength));
            }
            resultCommand = response.toString();

        } catch (Exception e) {
            LOGGER.error("JSchException : " + e.getMessage());
        } finally {
            this.disConnectSSH();
        }
        return resultCommand;
    }

    private String getResponse(String command, String dir) {
        String resultOutput = "";
        List<String> cmd = new ArrayList<String>();
        cmd.add("/bin/bash");
        cmd.add("-c");
        cmd.add(command);

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

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(prs.getInputStream()));
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

        }catch (Exception e1) {
            resultOutput = Constants.RESULT_STATUS_FAIL;
            LOGGER.error(e1.getMessage());
        }
        finally
        {
            if(prs != null) {
                try {
                    prs.destroy();
                } catch(Exception e2) {
                    resultOutput = Constants.RESULT_STATUS_FAIL;
                    LOGGER.error(e2.getMessage());
                }
            }
        }
        return resultOutput;
    }

    /**
     * Command Line 실행 (String)
     *
     * @param command, dir
     * @return String
     */
    public String execCommandOutput(String command, String dir, String host, String idRsa) {
        String response = "";
        if(!StringUtils.isBlank(idRsa) && !StringUtils.isBlank(host)) {
            response = getSSHResponse(command, dir, host, idRsa);
        } else {
            response = getResponse(command, dir);
        }
        return response;
    }



}
