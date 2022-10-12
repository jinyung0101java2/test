package org.paasta.container.terraman.api.common.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.StringUtils;
import org.paasta.container.terraman.api.common.constants.Constants;
import org.paasta.container.terraman.api.common.constants.TerramanConstant;
import org.paasta.container.terraman.api.common.model.AccountModel;
import org.paasta.container.terraman.api.common.model.FileModel;
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
     * @param clusterId the clusterId
     * @param provider the provider
     * @param seq the seq
     * @param pod the pod
     * @param host the host
     * @param idRsa the idRsa
     * @param processGb the processGb
     * @return the String
     */
    public String createProviderFile(String clusterId, String provider, int seq, String pod, String host, String idRsa, String processGb) {
        String resultCode = Constants.RESULT_STATUS_FAIL;
        try {
            String path = propertyService.getVaultBase() + provider.toUpperCase() + "/" + seq;
            HashMap<String, Object> res = vaultService.read(path, new HashMap<String, Object>().getClass());
            AccountModel account = accountService.getAccountInfo(seq);
            FileModel fileModel = new FileModel();
            String resultFile = "";
            if(res != null) {
                switch(provider.toUpperCase()) {
                    case Constants.UPPER_AWS : fileModel.setAwsAccessKey(String.valueOf(res.get("access_key")));
                        fileModel.setAwsSecretKey(String.valueOf(res.get("secret_key")));
                        fileModel.setAwsRegion(account.getRegion());
                        resultFile = this.tfCreateWithWriteAws(fileModel, clusterId, processGb);
                        break;
                    case Constants.UPPER_GCP : LOGGER.error("%s is Cloud not supported.", provider);
                        break;
                    case Constants.UPPER_VSPHERE : fileModel.setVSphereUser(String.valueOf(res.get("uesr")));
                        fileModel.setVSpherePassword(String.valueOf(res.get("password")));
                        fileModel.setVSphereServer(String.valueOf(res.get("vsphere_server")));
                        resultFile = this.tfCreateWithWriteVSphere(fileModel, clusterId, processGb);
                        break;
                    case Constants.UPPER_OPENSTACK : fileModel.setOpenstackTenantName(account.getProject());
                        fileModel.setOpenstackPassword(String.valueOf(res.get("password")));
                        fileModel.setOpenstackAuthUrl(String.valueOf(res.get("auth_url")));
                        fileModel.setOpenstackUserName(String.valueOf(res.get("user_name")));
                        fileModel.setOpenstackRegion(account.getRegion());
                        resultFile = this.tfCreateWithWriteOpenstack(fileModel, clusterId, processGb);
                        break;
                    default : LOGGER.error("%s is Cloud not supported.", provider);
                }

                if(StringUtils.equals(resultFile, Constants.RESULT_STATUS_SUCCESS)) {
                    if(!StringUtils.isBlank(idRsa) && !StringUtils.isBlank(host)) {
                        File uploadfile = new File( TerramanConstant.FILE_PATH(TerramanConstant.MOVE_DIR_CLUSTER(clusterId, processGb)) ); // 파일 객체 생성
                        resultCode = commandService.sshFileUpload(TerramanConstant.MOVE_DIR_CLUSTER(clusterId, processGb), host, idRsa, uploadfile);
                    }
                    resultCode = commandService.execCommandOutput(TerramanConstant.INSTANCE_COPY_COMMAND(pod, clusterId), "", host, idRsa);
                    if(!StringUtils.equals(Constants.RESULT_STATUS_FAIL, resultCode)) {
                        resultCode = Constants.RESULT_STATUS_SUCCESS;
                        LOGGER.info("인스턴스 파일 복사가 완료되었습니다. : %s", resultCode);
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
     * @param fileModel the fileModel
     * @param clusterId the clusterId
     * @param processGb the processGb
     * @return the String
     */
    private String tfCreateWithWriteAws(FileModel fileModel, String clusterId, String processGb) {
        String resultCode = Constants.RESULT_STATUS_SUCCESS;
        boolean fileFlag = true;
        try {
            File file = new File(TerramanConstant.FILE_PATH(TerramanConstant.MOVE_DIR_CLUSTER(clusterId, processGb))); // File객체 생성
            if(!file.exists()){ // 파일이 존재하지 않으면
                fileFlag = file.createNewFile(); // 신규생성
            }

            if(fileFlag) {
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
                } catch (IOException e1) {
                    resultCode = Constants.RESULT_STATUS_FAIL;
                }
            }
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
     * @param fileModel the fileModel
     * @param clusterId the clusterId
     * @param processGb the processGb
     * @return the String
     */
    private String tfCreateWithWriteOpenstack(FileModel fileModel, String clusterId, String processGb) {
        String resultCode = Constants.RESULT_STATUS_SUCCESS;
        boolean fileFlag = true;
        try {
            File file = new File(TerramanConstant.FILE_PATH(TerramanConstant.MOVE_DIR_CLUSTER(clusterId, processGb))); // File객체 생성
            if(!file.exists()){ // 파일이 존재하지 않으면
                fileFlag = file.createNewFile(); // 신규생성
            }

            if(fileFlag) {
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
                } catch (IOException e1) {
                    resultCode = Constants.RESULT_STATUS_FAIL;
                }
            }
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
     * @param fileModel the fileModel
     * @param clusterId the clusterId
     * @param processGb the processGb
     * @return the String
     */
    private String tfCreateWithWriteVSphere(FileModel fileModel, String clusterId, String processGb) {
        String resultCode = Constants.RESULT_STATUS_SUCCESS;
        boolean fileFlag = true;
        try {
            File file = new File(TerramanConstant.FILE_PATH(TerramanConstant.MOVE_DIR_CLUSTER(clusterId, processGb))); // File객체 생성
            if(!file.exists()){ // 파일이 존재하지 않으면
                fileFlag = file.createNewFile(); // 신규생성
            }
            if(fileFlag) {
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
                } catch (IOException e1) {
                    resultCode = Constants.RESULT_STATUS_FAIL;
                }
            }
        }
        catch (IOException e) {
            resultCode = Constants.RESULT_STATUS_FAIL;
            LOGGER.error(e.getMessage());
        }
        return resultCode;
    }
}
