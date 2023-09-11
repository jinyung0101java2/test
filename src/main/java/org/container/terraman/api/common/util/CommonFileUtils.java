package org.container.terraman.api.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.bcel.Const;
import org.container.terraman.api.common.constants.Constants;
import org.container.terraman.api.common.constants.TerramanConstant;
import org.container.terraman.api.common.model.AccountModel;
import org.container.terraman.api.common.model.FileModel;
import org.container.terraman.api.common.model.VaultModel;
import org.container.terraman.api.common.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashMap;

@edu.umd.cs.findbugs.annotations.SuppressFBWarnings({"PATH_TRAVERSAL_IN", "PATH_TRAVERSAL_IN", "PATH_TRAVERSAL_IN"})
@Service
public class CommonFileUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonFileUtils.class);

    /**
     * terraform 파일 생성 및 작성 (String)
     *
     * @param path
     * @param contents
     * @return String
     */
    public String createWithWrite(String path, String contents) {
        String resultCode = Constants.RESULT_STATUS_SUCCESS;
        try {
            File file = new File(path); // File객체 생성
            if(!file.exists()){ // 파일이 존재하지 않으면
                file.createNewFile(); // 신규생성
            }

            // BufferedWriter 생성 및 쓰기설정(파일 덮어쓰기 - false)
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));

            // 파일 쓰기
            writer.write(contents);

            // 버퍼 및 스트림 뒷정리
            writer.flush(); // 버퍼의 남은 데이터를 모두 쓰기
            writer.close(); // 스트림 종료
        }
        catch (IOException e) {
            resultCode = Constants.RESULT_STATUS_FAIL;
            LOGGER.error(CommonUtils.loggerReplace(e.getMessage()));
        }

        return resultCode;
    }



    /**
     * 파일 읽기 (String)
     *
     * @param fName
     * @return JsonObject
     */
    public JsonObject fileRead(String fName){
        JsonObject obj = new JsonObject();
        try{
            // FileReader 생성
            Reader reader = new FileReader(fName);

            // Json 파일 읽어서, Lecture 객체로 변환
            Gson gson = new Gson();
            obj = gson.fromJson(reader, JsonObject.class);

        } catch (FileNotFoundException e) {
            LOGGER.error(CommonUtils.loggerReplace(e.getMessage()));
        }
        return obj;
    }

    /**
     * 파일 삭제 (String)
     *
     * @param fName
     * @return String
     */
    public String fileDelete(String fName){
        String resultCode = Constants.RESULT_STATUS_FAIL;
        File file = new File(fName);

        try{
            if( file.exists() ){
                boolean result = file.delete();
                if(result){
                    resultCode = Constants.RESULT_STATUS_SUCCESS;
                }
            }
        } catch (Exception e) {
            LOGGER.error(CommonUtils.loggerReplace(e.getMessage()));
        }
        return resultCode;
    }
}
