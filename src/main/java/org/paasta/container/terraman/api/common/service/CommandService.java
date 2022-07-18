package org.paasta.container.terraman.api.common.service;

import org.apache.commons.lang3.StringUtils;
import org.paasta.container.terraman.api.common.constants.Constants;
import org.paasta.container.terraman.api.common.constants.TerramanConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class CommandService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandService.class);

    /**
     * Command Line 실행 (String)
     *
     * @param command
     * @return String
     */
    public String execCommandOutput(String command) {
        StringBuffer output = new StringBuffer();
        Process process = null;
        BufferedReader br = null;
        Runtime runtime = Runtime.getRuntime();
        String osName = System.getProperty("os.name");

        // windows
        LOGGER.info("command :: " + command);
        if(osName.contains("Windows")) {
            if(StringUtils.equals(command, TerramanConstant.DIRECTORY_COMMAND)) {
                command = "cd";
            }
            command = "cmd /c " + command;
        }

        try {
            process = runtime.exec(command);

            // write process input
            br = new BufferedReader(new InputStreamReader(process.getInputStream(), "euc-kr"));
            String msg = null;
            while((msg=br.readLine()) != null) {
                output.append(msg + System.getProperty("line.separator"));
            }

            br.close();
        }
        catch (IOException e) {
            LOGGER.error(e.getMessage());
            output.append(Constants.RESULT_STATUS_FAIL);
        } finally {
            process.destroy();
            try {
                if(br != null) {
                    br.close();
                }
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
        }
        return output.toString();
    }
}
