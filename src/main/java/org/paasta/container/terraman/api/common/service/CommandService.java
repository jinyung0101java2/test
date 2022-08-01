package org.paasta.container.terraman.api.common.service;

import org.apache.commons.lang3.StringUtils;
import org.paasta.container.terraman.api.common.constants.Constants;
import org.paasta.container.terraman.api.common.constants.TerramanConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class CommandService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandService.class);

    /**
     * Command Line 실행 (String)
     *
     * @param command, dir
     * @return String
     */
    public String execCommandOutput(String command, String dir) {
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
            LOGGER.info("command :: " + prsbld.command());

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

}
