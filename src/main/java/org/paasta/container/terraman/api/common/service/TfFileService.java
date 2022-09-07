package org.paasta.container.terraman.api.common.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.StringUtils;
import org.paasta.container.terraman.api.common.constants.Constants;
import org.paasta.container.terraman.api.common.constants.TerramanConstant;
import org.paasta.container.terraman.api.common.model.AccountModel;
import org.paasta.container.terraman.api.common.model.FileModel;
import org.paasta.container.terraman.api.common.util.CommonFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

@Service
public class TfFileService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TfFileService.class);

    private final VaultService vaultService;
    private final AccountService accountService;
    private final CommandService commandService;
    private final PropertyService propertyService;

    @Autowired
    public TfFileService(
            VaultService vaultService
            , CommandService commandService
            , AccountService accountService
            , PropertyService propertyService
    ) {
        this.vaultService = vaultService;
        this.commandService = commandService;
        this.accountService = accountService;
        this.propertyService = propertyService;
    }

    /**
     * terraform provider.tf 파일 생성 및 작성 (String)
     *
     * @param clusterId
     * @param provider
     * @param seq
     * @param pod
     * @return String
     */
    public String createProviderFile(String clusterId, String provider, int seq, String pod, String host, String idRsa, String processGb) {
        String resultCode = Constants.RESULT_STATUS_FAIL;
        try {
            String path = propertyService.getVaultBase() + provider.toUpperCase() + "/" + seq;
            HashMap res = vaultService.read(path, new HashMap().getClass());
            AccountModel account = accountService.getAccountInfo(seq);
            FileModel fileModel = new FileModel();
            String resultFile = "";
            if(res != null) {
                if(StringUtils.equals(Constants.UPPER_AWS, provider.toUpperCase())) {
                    fileModel.setAwsAccessKey(String.valueOf(res.get("access_key")));
                    fileModel.setAwsSecretKey(String.valueOf(res.get("secret_key")));
                    fileModel.setAwsRegion(account.getRegion());
                    resultFile = this.tfCreateWithWriteAws(fileModel, clusterId, processGb);
                } else if(StringUtils.equals(Constants.UPPER_GCP, provider.toUpperCase())) {

                } else if(StringUtils.equals(Constants.UPPER_VSPHERE, provider.toUpperCase())) {
                    fileModel.setVSphereUser(String.valueOf(res.get("uesr")));
                    fileModel.setVSpherePassword(String.valueOf(res.get("password")));
                    fileModel.setVSphereServer(String.valueOf(res.get("vsphere_server")));
                    resultFile = this.tfCreateWithWriteVSphere(fileModel, clusterId, processGb);
                } else if(StringUtils.equals(Constants.UPPER_OPENSTACK, provider.toUpperCase())) {
                    // 파일 생성 및 쓰기
                    fileModel.setOpenstackTenantName(account.getProject());
                    fileModel.setOpenstackPassword(String.valueOf(res.get("password")));
                    fileModel.setOpenstackAuthUrl(String.valueOf(res.get("auth_url")));
                    fileModel.setOpenstackUserName(String.valueOf(res.get("user_name")));
                    fileModel.setOpenstackRegion(account.getRegion());
                    resultFile = this.tfCreateWithWriteOpenstack(fileModel, clusterId, processGb);
                } else {
                    LOGGER.error(provider + " is Cloud not supported.");
                }

                if(StringUtils.equals(resultFile, Constants.RESULT_STATUS_SUCCESS)) {
                    if(!StringUtils.isBlank(idRsa) && !StringUtils.isBlank(host)) {
                        File uploadfile = new File( TerramanConstant.FILE_PATH(TerramanConstant.MOVE_DIR_CLUSTER(clusterId, processGb)) ); // 파일 객체 생성
                        resultCode = commandService.SSHFileUpload(TerramanConstant.MOVE_DIR_CLUSTER(clusterId, processGb), host, idRsa, uploadfile);
                    }
                    resultCode = commandService.execCommandOutput(TerramanConstant.INSTANCE_COPY_COMMAND(pod, clusterId), "", host, idRsa);
                    if(!StringUtils.equals(Constants.RESULT_STATUS_FAIL, resultCode)) {
                        resultCode = Constants.RESULT_STATUS_SUCCESS;
                        LOGGER.info("인스턴스 파일 복사가 완료되었습니다. " + resultCode);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return resultCode;
    }

    /**
     * terraform 파일 생성 및 작성 (String)
     *
     * @param fileModel
     * @param clusterId
     * @return String
     */
    private String tfCreateWithWriteAws(FileModel fileModel, String clusterId, String processGb) {
        String resultCode = Constants.RESULT_STATUS_SUCCESS;
        try {
            File file = new File(TerramanConstant.FILE_PATH(TerramanConstant.MOVE_DIR_CLUSTER(clusterId, processGb))); // File객체 생성
            if(!file.exists()){ // 파일이 존재하지 않으면
                file.createNewFile(); // 신규생성
            }

            // BufferedWriter 생성 및 쓰기설정(파일 덮어쓰기 - false)
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));

            // 파일 쓰기
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonString = gson.toJson(fileModel);
            LOGGER.info("provider - aws :: " + jsonString.toString());
            jsonString = jsonString.replaceAll(",", "");
            jsonString = jsonString.replaceAll("\"awsRegion\":", "region =");
            jsonString = jsonString.replaceAll("\"awsAccessKey\":", "access_key =");
            jsonString = jsonString.replaceAll("\"awsSecretKey\":", "secret_key =");

            writer.write("provider \"aws\" " + jsonString);

            // 버퍼 및 스트림 뒷정리
            writer.flush(); // 버퍼의 남은 데이터를 모두 쓰기
            writer.close(); // 스트림 종료
        }
        catch (IOException e) {
            resultCode = Constants.RESULT_STATUS_FAIL;
            LOGGER.error(e.getMessage());
        }
        return resultCode;
    }

    /**
     * terraform 파일 생성 및 작성 (String)
     *
     * @param fileModel
     * @param clusterId
     * @return String
     */
    private String tfCreateWithWriteOpenstack(FileModel fileModel, String clusterId, String processGb) {
        String resultCode = Constants.RESULT_STATUS_SUCCESS;
        try {
            File file = new File(TerramanConstant.FILE_PATH(TerramanConstant.MOVE_DIR_CLUSTER(clusterId, processGb))); // File객체 생성
            if(!file.exists()){ // 파일이 존재하지 않으면
                file.createNewFile(); // 신규생성
            }

            // BufferedWriter 생성 및 쓰기설정(파일 덮어쓰기 - false)
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));

            // 파일 쓰기
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonString = gson.toJson(fileModel);
            jsonString = jsonString.replaceAll(",", "");
            jsonString = jsonString.replaceAll("\"openstackTenantName\":", "tenant_name =");
            jsonString = jsonString.replaceAll("\"openstackPassword\":", "password =");
            jsonString = jsonString.replaceAll("\"openstackAuthUrl\":", "auth_url =");
            jsonString = jsonString.replaceAll("\"openstackUserName\":", "user_name =");
            jsonString = jsonString.replaceAll("\"openstackRegion\":", "region =");

            writer.write(TerramanConstant.PREFIX_PROVIDER_OPENSTACK + "\n\n" + "provider \"openstack\" " + jsonString);

            // 버퍼 및 스트림 뒷정리
            writer.flush(); // 버퍼의 남은 데이터를 모두 쓰기
            writer.close(); // 스트림 종료
        }
        catch (IOException e) {
            resultCode = Constants.RESULT_STATUS_FAIL;
            LOGGER.error(e.getMessage());
        }
        return resultCode;
    }

    /**
     * terraform 파일 생성 및 작성 (String)
     *
     * @param fileModel
     * @param clusterId
     * @return String
     */
    private String tfCreateWithWriteVSphere(FileModel fileModel, String clusterId, String processGb) {
        String resultCode = Constants.RESULT_STATUS_SUCCESS;
        try {
            File file = new File(TerramanConstant.FILE_PATH(TerramanConstant.MOVE_DIR_CLUSTER(clusterId, processGb))); // File객체 생성
            if(!file.exists()){ // 파일이 존재하지 않으면
                file.createNewFile(); // 신규생성
            }

            // BufferedWriter 생성 및 쓰기설정(파일 덮어쓰기 - false)
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));

            // 파일 쓰기
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonString = gson.toJson(fileModel);
            jsonString = jsonString.replaceAll(",", "");
            jsonString = jsonString.replaceAll("\"vSphereUser\":", "user =");
            jsonString = jsonString.replaceAll("\"vSpherePassword\":", "password =");
            jsonString = jsonString.replaceAll("\"vSphereServer\":", "vsphere_server =");

            writer.write("provider \"vsphere\" " + jsonString);

            // 버퍼 및 스트림 뒷정리
            writer.flush(); // 버퍼의 남은 데이터를 모두 쓰기
            writer.close(); // 스트림 종료
        }
        catch (IOException e) {
            resultCode = Constants.RESULT_STATUS_FAIL;
            LOGGER.error(e.getMessage());
        }
        return resultCode;
    }
}
