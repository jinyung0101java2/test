package org.paasta.container.terraman.api.common.service;

import org.apache.commons.lang3.StringUtils;
import org.paasta.container.terraman.api.common.terramanproc.CommandProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;


@Service
public class CommandService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandService.class);

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
        return new CommandProcess().sshFileUpload(dir, host, idRsa, uploadFile, userName);
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
        new CommandProcess().sshFileDownload(dir, localDir, fileName, host, idRsa, userName);
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
        return new CommandProcess().getSSHResponse(command, dir, host, idRsa, userName);
    }

    /**
     * Command Line Response
     *
     * @param command the command
     * @param dir the dir
     * @return the String
     */
    public String getResponse(String command, String dir) {
        return new CommandProcess().getResponse(command, dir);
    }

    /**
     * Command Line Excute
     *
     * @param command the command
     * @param dir the dir
     * @param host the host
     * @param idRsa the idRsa
     * @return the String
     */
    public String execCommandOutput(String command, String dir, String host, String idRsa, String userName) {
        String response = "";
        if(!StringUtils.isBlank(idRsa) && !StringUtils.isBlank(host)) {
            response = getSSHResponse(command, dir, host, idRsa, userName);
        } else {
            response = getResponse(command, dir);
        }
        return response;
    }
}
