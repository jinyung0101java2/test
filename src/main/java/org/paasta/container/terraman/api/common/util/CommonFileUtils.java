package org.paasta.container.terraman.api.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.paasta.container.terraman.api.common.model.FileModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class CommonFileUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonFileUtils.class);
    private static final String filePath = "provider.tf";

    /**
     * terraform 파일 생성 및 작성 (String)
     *
     * @param path
     * @param contents
     * @return void
     */
    public String createWithWrite(String path, String contents) {
        String resultCode = "200";
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
            resultCode = "500";
            LOGGER.error(e.getMessage());
        }

        return resultCode;
    }

    /**
     * terraform 파일 생성 및 작성 (String)
     *
     * @param fileModel
     * @return void
     */
    public String tfCreateWithWrite(FileModel fileModel) {
        String resultCode = "200";
        try {
            File file = new File(filePath); // File객체 생성
            if(!file.exists()){ // 파일이 존재하지 않으면
                file.createNewFile(); // 신규생성
            }

            // BufferedWriter 생성 및 쓰기설정(파일 덮어쓰기 - false)
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));

            // 파일 쓰기
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonString = gson.toJson(fileModel);
            jsonString = jsonString.replaceAll(":", " =");
            jsonString = jsonString.replaceAll(",", "");
            writer.write("provider \"openstack\" " + jsonString);

            // 버퍼 및 스트림 뒷정리
            writer.flush(); // 버퍼의 남은 데이터를 모두 쓰기
            writer.close(); // 스트림 종료
        }
        catch (IOException e) {
            resultCode = "500";
            LOGGER.error(e.getMessage());
        }
        return resultCode;
    }

    /**
     * terraform 파일 읽기 (String)
     *
     * @param fName
     * @return JsonObject
     */
    public JsonObject tfFileRead(String fName){
        JsonObject obj = new JsonObject();
        try{
            // FileReader 생성
            Reader reader = new FileReader(fName);

            // Json 파일 읽어서, Lecture 객체로 변환
            Gson gson = new Gson();
            obj = gson.fromJson(reader, JsonObject.class);

        } catch (FileNotFoundException e) {
            LOGGER.error(e.getMessage());
        }
        return obj;
    }

    /**
     * terraform 파일 읽기 (String)
     *
     * @param fName
     * @return JsonObject
     */
    public String tfFileDelete(String fName){
        String resultCode = "500";
        File file = new File(fName);

        try{
            if( file.exists() ){
                boolean result = file.delete();
                if(result){
                    resultCode = "200";
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return resultCode;
    }
}
