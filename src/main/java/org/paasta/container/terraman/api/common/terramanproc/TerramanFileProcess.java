package org.paasta.container.terraman.api.common.terramanproc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FilenameUtils;
import org.paasta.container.terraman.api.common.constants.Constants;
import org.paasta.container.terraman.api.common.constants.TerramanConstant;
import org.paasta.container.terraman.api.common.model.FileModel;
import org.paasta.container.terraman.api.common.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@edu.umd.cs.findbugs.annotations.SuppressFBWarnings("PATH_TRAVERSAL_IN")
public class TerramanFileProcess {
    private static final Logger LOGGER = LoggerFactory.getLogger(TerramanFileProcess.class);

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

    public String createTfFileDiv(FileModel fileModel, String clusterId, String processGb, String provider) {
        String resultCode = Constants.RESULT_STATUS_FAIL;
        boolean fileFlag = true;
        if(fileModel != null) {
            try {
                File file = new File(TerramanConstant.FILE_PATH(TerramanConstant.MOVE_DIR_CLUSTER(clusterId))); // File객체 생성
                if(!file.exists()){ // 파일이 존재하지 않으면
                    fileFlag = file.createNewFile(); // 신규생성
                }

                if(fileFlag) {
                    switch(provider.toUpperCase()) {
                        case Constants.UPPER_AWS :
                            resultCode = this.tfCreateWithWriteAws(fileModel, file);
                            break;
                        case Constants.UPPER_GCP :
                            LOGGER.error("{} is Cloud not supported.", CommonUtils.loggerReplace(provider));
                            break;
                        case Constants.UPPER_VSPHERE :
                            resultCode = this.tfCreateWithWriteVSphere(fileModel, file);
                            break;
                        case Constants.UPPER_OPENSTACK :
                            resultCode = this.tfCreateWithWriteOpenstack(fileModel, file);
                            break;
                        default :
                            LOGGER.error("{} is Cloud not supported.", CommonUtils.loggerReplace(provider));
                            break;
                    }
                }
            }
            catch (IOException e) {
                resultCode = Constants.RESULT_STATUS_FAIL;
                LOGGER.error(CommonUtils.loggerReplace(e.getMessage()));
            }
        }
        return resultCode;
    }
}
