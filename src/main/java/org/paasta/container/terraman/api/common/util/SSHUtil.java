package org.paasta.container.terraman.api.common.util;

import com.jcraft.jsch.*;
import org.paasta.container.terraman.api.common.constants.Constants;
import org.paasta.container.terraman.api.common.constants.TerramanConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

public class SSHUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(SSHUtil.class);
    private Session session;
    private ChannelExec channelExec;
    private Channel channel;

    private void SSHConnect(String host) throws JSchException {
        String userName = "ubuntu";
        int port = 22;
        JSch jsch = new JSch();
        jsch.addIdentity("/home/ubuntu/.ssh/id_rsa");
        session = jsch.getSession(userName, host, port);
        session.setConfig("StrictHostKeyChecking", "no");       // 호스트 정보를 검사하지 않도록 설정
        session.connect();
    }

    private void disConnectSSH() {
        if (session != null) session.disconnect();
        if (channelExec != null) channelExec.disconnect();
    }

    public String getSSHResponse(String command, String host) {
        String resultCommand = Constants.RESULT_STATUS_FAIL;
        StringBuilder response = new StringBuilder();
        try {
            SSHConnect(host);
            channelExec = (ChannelExec) session.openChannel("exec");
            channelExec.setCommand(command);
            LOGGER.info("command input :: " + command);
            InputStream inputStream = channelExec.getInputStream();
            channelExec.connect();

            byte[] buffer = new byte[8192];
            int decodedLength;
            while ((decodedLength = inputStream.read(buffer, 0, buffer.length)) > 0) {
                response.append(new String(buffer, 0, decodedLength));
            }
            resultCommand = response.toString();
            LOGGER.info("command output :: " + response.toString());

        } catch (Exception e) {
            LOGGER.error("JSchException : " + e.getMessage());
        } finally {
            this.disConnectSSH();
        }
        return resultCommand;
    }
}
