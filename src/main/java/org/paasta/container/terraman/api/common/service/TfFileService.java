package org.paasta.container.terraman.api.common.service;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.paasta.container.terraman.api.common.PropertyService;
import org.paasta.container.terraman.api.common.VaultService;
import org.paasta.container.terraman.api.common.constants.Constants;
import org.paasta.container.terraman.api.common.constants.TerramanConstant;
import org.paasta.container.terraman.api.common.model.AccountModel;
import org.paasta.container.terraman.api.common.model.FileModel;
import org.paasta.container.terraman.api.common.terramanproc.TerramanFileProcess;
import org.paasta.container.terraman.api.common.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
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

        String path = propertyService.getVaultBase();
        path = path + provider.toUpperCase() + Constants.DIV + seq;
        HashMap<String, Object> res = vaultService.read(path, HashMap.class);
        AccountModel account = accountService.getAccountInfo(seq);
        FileModel fileModel = new FileModel();
        String resultFile = "";

        String awsAccessKey = "";
        String awsSecretKey = "";

        String vsphereUser = "";
        String vspherePassword = "";
        String vsphereVsphereServer = "";

        String openstackPassword = "";
        String openstackAuthUrl = "";
        String openstackUserName = "";

        switch(provider.toUpperCase()) {
            case Constants.UPPER_AWS :
                awsAccessKey = res != null ? String.valueOf(res.get("access_key")) : "";
                awsSecretKey = res != null ? String.valueOf(res.get("secret_key")) : "";
                fileModel.setAwsAccessKey(awsAccessKey);
                fileModel.setAwsSecretKey(awsSecretKey);
                fileModel.setAwsRegion(account.getRegion());
                break;
            case Constants.UPPER_GCP :
                LOGGER.error("{} is Cloud not supported.", CommonUtils.loggerReplace(provider));
                break;
            case Constants.UPPER_VSPHERE :
                vsphereUser = res != null ? String.valueOf(res.get("uesr")) : "";
                vspherePassword = res != null ? String.valueOf(res.get("password")) : "";
                vsphereVsphereServer = res != null ? String.valueOf(res.get("vsphere_server")) : "";
                fileModel.setVSphereUser(vsphereUser);
                fileModel.setVSpherePassword(vspherePassword);
                fileModel.setVSphereServer(vsphereVsphereServer);
                break;
            case Constants.UPPER_OPENSTACK :
                openstackPassword = res != null ? String.valueOf(res.get("password")) : "";
                openstackAuthUrl = res != null ? String.valueOf(res.get("auth_url")) : "";
                openstackUserName = res != null ? String.valueOf(res.get("user_name")) : "";
                fileModel.setOpenstackTenantName(account.getProject());
                fileModel.setOpenstackPassword(openstackPassword);
                fileModel.setOpenstackAuthUrl(openstackAuthUrl);
                fileModel.setOpenstackUserName(openstackUserName);
                fileModel.setOpenstackRegion(account.getRegion());
                break;
            default :
                resultCode = Constants.RESULT_STATUS_FAIL;
                LOGGER.error("{} is Cloud not supported.", CommonUtils.loggerReplace(provider));
                break;
        }

        resultFile = new TerramanFileProcess().createTfFileDiv(fileModel, clusterId, processGb, provider.toUpperCase());

        if(StringUtils.equals(resultFile, Constants.RESULT_STATUS_SUCCESS)) {
            if(!StringUtils.isBlank(idRsa) && !StringUtils.isBlank(host)) {
                if(StringUtils.isNotBlank(clusterId)) {
                    File uploadfile = new File( TerramanConstant.FILE_PATH(TerramanConstant.MOVE_DIR_CLUSTER(FilenameUtils.getName(clusterId))) ); // 파일 객체 생성
                    commandService.sshFileUpload(TerramanConstant.MOVE_DIR_CLUSTER(clusterId), host, idRsa, uploadfile, TerramanConstant.DEFAULT_USER_NAME);
                }
            }
//            resultCode = commandService.execCommandOutput(TerramanConstant.INSTANCE_COPY_COMMAND(pod, clusterId), "", host, idRsa, TerramanConstant.DEFAULT_USER_NAME);
            resultCode = commandService.execCommandOutput("14", "", host, idRsa, TerramanConstant.DEFAULT_USER_NAME);
            if(!StringUtils.equals(Constants.RESULT_STATUS_FAIL, resultCode)) {
                resultCode = Constants.RESULT_STATUS_SUCCESS;
                LOGGER.info("인스턴스 파일 복사가 완료되었습니다. : {}", resultCode);
            }
        }



        return resultCode;
    }




}
