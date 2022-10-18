package org.paasta.container.terraman.api.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.paasta.container.terraman.api.common.constants.Constants;
import org.paasta.container.terraman.api.common.constants.TerramanConstant;
import org.paasta.container.terraman.api.common.model.FileModel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TerramanFileUtils {

    /**
     * terraform 파일 생성 및 작성 (String)
     *
     * @param fileModel the fileModel
     * @param file the file
     * @return the String
     */
    public String tfCreateWithWriteAws(FileModel fileModel, File file) {
        String resultCode = "";
        // BufferedWriter 생성 및 쓰기설정(파일 덮어쓰기 - false)
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));) {
            // 파일 쓰기
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonString = gson.toJson(fileModel);
            jsonString = jsonString.replaceAll("[,]", "");
            jsonString = jsonString.replace("\"awsRegion\":", "region =");
            jsonString = jsonString.replace("\"awsAccessKey\":", "access_key =");
            jsonString = jsonString.replace("\"awsSecretKey\":", "secret_key =");

            writer.write("provider \"aws\" " + jsonString);

            // 버퍼 및 스트림 뒷정리
            writer.flush(); // 버퍼의 남은 데이터를 모두 쓰기
            resultCode = Constants.RESULT_STATUS_SUCCESS;
        } catch (IOException e1) {
            resultCode = Constants.RESULT_STATUS_FAIL;
        }

        return resultCode;
    }

    /**
     * terraform 파일 생성 및 작성 (String)
     *
     * @param fileModel the fileModel
     * @param file the file
     * @return the String
     */
    public String tfCreateWithWriteOpenstack(FileModel fileModel, File file) {
        String resultCode = Constants.RESULT_STATUS_SUCCESS;
        // BufferedWriter 생성 및 쓰기설정(파일 덮어쓰기 - false)
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));) {
            // 파일 쓰기
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonString = gson.toJson(fileModel);
            jsonString = jsonString.replaceAll("[,]", "");
            jsonString = jsonString.replace("\"openstackTenantName\":", "tenant_name =");
            jsonString = jsonString.replace("\"openstackPassword\":", "password =");
            jsonString = jsonString.replace("\"openstackAuthUrl\":", "auth_url =");
            jsonString = jsonString.replace("\"openstackUserName\":", "user_name =");
            jsonString = jsonString.replace("\"openstackRegion\":", "region =");

            writer.write(TerramanConstant.PREFIX_PROVIDER_OPENSTACK + "\n\n" + "provider \"openstack\" " + jsonString);

            // 버퍼 및 스트림 뒷정리
            writer.flush(); // 버퍼의 남은 데이터를 모두 쓰기
            resultCode = Constants.RESULT_STATUS_SUCCESS;
        } catch (IOException e1) {
            resultCode = Constants.RESULT_STATUS_FAIL;
        }
        return resultCode;
    }

    /**
     * terraform 파일 생성 및 작성 (String)
     *
     * @param fileModel the fileModel
     * @param file the file
     * @return the String
     */
    public String tfCreateWithWriteVSphere(FileModel fileModel, File file) {
        String resultCode = Constants.RESULT_STATUS_SUCCESS;
        // BufferedWriter 생성 및 쓰기설정(파일 덮어쓰기 - false)
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));) {
            // 파일 쓰기
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonString = gson.toJson(fileModel);
            jsonString = jsonString.replaceAll("[,]", "");
            jsonString = jsonString.replace("\"vSphereUser\":", "user =");
            jsonString = jsonString.replace("\"vSpherePassword\":", "password =");
            jsonString = jsonString.replace("\"vSphereServer\":", "vsphere_server =");

            writer.write("provider \"vsphere\" " + jsonString);

            // 버퍼 및 스트림 뒷정리
            writer.flush(); // 버퍼의 남은 데이터를 모두 쓰기
            resultCode = Constants.RESULT_STATUS_SUCCESS;
        } catch (IOException e1) {
            resultCode = Constants.RESULT_STATUS_FAIL;
        }
        return resultCode;
    }
}
