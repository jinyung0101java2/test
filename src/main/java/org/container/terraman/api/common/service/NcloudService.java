package org.container.terraman.api.common.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.StringUtils;
import org.container.terraman.api.common.CommonService;
import org.container.terraman.api.common.PropertyService;
import org.container.terraman.api.common.VaultService;
import org.container.terraman.api.common.constants.Constants;
import org.container.terraman.api.common.constants.TerramanConstant;
import org.container.terraman.api.common.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class NcloudService {


    private static final Logger LOGGER = LoggerFactory.getLogger(TfFileService.class);
    private final VaultService vaultService;
    private final AccountService accountService;
    private final PropertyService propertyService;
    private final InstanceService instanceService;
    private final CommonService commonService;
    private final CommandService commandService;
    private final ClusterService clusterService;
    private final ClusterLogService clusterLogService;


    public NcloudService(VaultService vaultService,
                         AccountService accountService,
                         PropertyService propertyService,
                         InstanceService instanceService,
                         CommonService commonService,
                         CommandService commandService,
                         ClusterService clusterService,
                         ClusterLogService clusterLogService) {
        this.vaultService = vaultService;
        this.accountService = accountService;
        this.propertyService = propertyService;
        this.instanceService = instanceService;
        this.commonService = commonService;
        this.commandService = commandService;
        this.clusterService = clusterService;
        this.clusterLogService = clusterLogService;
    }

    /**
     * Select Ncloud SSH Key
     *
     * @param clusterId the clusterId
     * @param provider  the provider
     * @param host      the host
     * @param idRsa     the idRsa
     * @param processGb the processGb
     * @param seq       the seq
     * @return the NcloudInstanceKeyModel
     */
    public List<NcloudInstanceKeyModel> getNcloudSSHKey(String clusterId, String provider, String host, String idRsa, String processGb, int seq) {
        String path = propertyService.getVaultBase();
        path = path + provider.toUpperCase() + Constants.DIV + seq;
        HashMap<String, Object> res = vaultService.read(path, HashMap.class);
        AccountModel account = accountService.getAccountInfo(seq);
        String reqUrl = propertyService.getNcloudInstancePasswordApiUrl() + Constants.NCLOUD_INSTANCE_PASSWORD_API_PATH;
        List<NcloudPrivateKeyModel> ncloudPrivateKeysModel = instanceService.getNcloudPrivateKeys(clusterId, provider, host, idRsa, processGb);
        NcloudAuthKeyModel ncloudAuthKeyModel = new NcloudAuthKeyModel("", "");
        NcloudInstanceKeyInfoModel ncloudInstanceKeyInfoModel = new NcloudInstanceKeyInfoModel("", "", "", "", ncloudAuthKeyModel);

        String ncloudAccessKey = "";
        String ncloudSecretKey = "";
        ncloudAccessKey = res != null ? String.valueOf(res.get("access_key")) : "";
        ncloudSecretKey = res != null ? String.valueOf(res.get("secret_key")) : "";
        ncloudAuthKeyModel.setAccess_key(ncloudAccessKey);
        ncloudAuthKeyModel.setSecret_key(ncloudSecretKey);
        ncloudInstanceKeyInfoModel.setSite(account.getSite());
        ncloudInstanceKeyInfoModel.setRegion(account.getRegion());

        List<NcloudInstanceKeyModel> resultList = new ArrayList<>();

        // Ncloud Instance RootPassword 조회
        for (int i = 0; i < ncloudPrivateKeysModel.size(); i++) {
            String instanceNo = ncloudPrivateKeysModel.get(i).getInstanceNo();
            String instanceprivateKey = ncloudPrivateKeysModel.get(i).getPrivateKey();
            ncloudInstanceKeyInfoModel.setInstance_no(instanceNo);
            ncloudInstanceKeyInfoModel.setPrivate_key(instanceprivateKey);
            Object InstanceKey = commonService.sendNcloudJson(reqUrl, HttpMethod.PATCH, commonService.toJson(ncloudInstanceKeyInfoModel), Object.class);
            NcloudInstanceKeyModel ncloudInstanceKeyModel = commonService.setResultObject(InstanceKey, NcloudInstanceKeyModel.class);

            resultList.add(new NcloudInstanceKeyModel(ncloudInstanceKeyModel.getServerInstanceNo(), ncloudInstanceKeyModel.getRootPassword()));
            LOGGER.info("PublicIp ::: " + ncloudPrivateKeysModel.get(i).getPublicIp() + ", RootPassword ::: " + resultList.get(i).getRootPassword());
        }

        return resultList;
    };

    /**
     * Create Ncloud Public Key File
     *
     * @param clusterId  the clusterId
     * @param provider   the provider
     * @param host       the host
     * @param idRsa      the idRsa
     * @param processGb  the processGb
     * @param seq        the seq
     * @param privateKey the privateKey
     * @param mpSeq      the mpSeq
     * @return the String
     */
    public String createNcloudPublicKey(String clusterId, String provider, String host, String idRsa, String processGb, int seq, String privateKey, int mpSeq) {
        String masterHost = host;
        String cResult = "";
        String resultCode = Constants.RESULT_STATUS_SUCCESS;
        TerramanCommandModel terramanCommandModel = new TerramanCommandModel();
        File file = new File(TerramanConstant.NCLOUD_PRIVATE_KEY_FILE_PATH(TerramanConstant.MOVE_DIR_CLUSTER(clusterId), clusterId));

        // Ncloud 루트 패스워드 조회
        List<NcloudInstanceKeyModel> ncloudInstanceKeyModel = getNcloudSSHKey(clusterId, provider, host, idRsa, processGb, seq);

        // Ncloud 개인키 조회
        NcloudPrivateKeyModel ncloudPrivateKeyModel = instanceService.getNcloudPrivateKey(clusterId, provider, host, idRsa, processGb, privateKey);

        // Ncloud 개인키 파일 생성
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));) {
            Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
            String jsonString = gson.toJson(ncloudPrivateKeyModel.getPrivateKey());
            writer.write(jsonString);
            writer.flush();
            resultCode = Constants.RESULT_STATUS_SUCCESS;
        } catch (IOException e1) {
            resultCode = Constants.RESULT_STATUS_FAIL;
        }

        // 개인키 권한 변경 600
        terramanCommandModel.setCommand("20");
        terramanCommandModel.setDir(TerramanConstant.CLUSTER_STATE_DIR(clusterId));
        terramanCommandModel.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel.setClusterId(clusterId);
        cResult = commandService.execCommandOutput(terramanCommandModel);
        if (StringUtils.equals(Constants.RESULT_STATUS_FAIL, cResult)) {
            clusterService.updateCluster(clusterId, TerramanConstant.CLUSTER_FAIL_STATUS);
            clusterLogService.saveClusterLog(clusterId, mpSeq, TerramanConstant.TERRAFORM_CREATE_CLUSTER_DIRECTORY_ERROR);
        }

        // 개인키 따옴표 제거
        terramanCommandModel.setCommand("21");
        terramanCommandModel.setDir(TerramanConstant.CLUSTER_STATE_DIR(clusterId));
        terramanCommandModel.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel.setClusterId(clusterId);
        cResult = commandService.execCommandOutput(terramanCommandModel);
        if (StringUtils.equals(Constants.RESULT_STATUS_FAIL, cResult)) {
            clusterService.updateCluster(clusterId, TerramanConstant.CLUSTER_FAIL_STATUS);
            clusterLogService.saveClusterLog(clusterId, mpSeq, TerramanConstant.TERRAFORM_CREATE_CLUSTER_DIRECTORY_ERROR);
        }

        // 개인키 줄바꿈
        terramanCommandModel.setCommand("22");
        terramanCommandModel.setDir(TerramanConstant.CLUSTER_STATE_DIR(clusterId));
        terramanCommandModel.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel.setClusterId(clusterId);
        cResult = commandService.execCommandOutput(terramanCommandModel);
        if (StringUtils.equals(Constants.RESULT_STATUS_FAIL, cResult)) {
            clusterService.updateCluster(clusterId, TerramanConstant.CLUSTER_FAIL_STATUS);
            clusterLogService.saveClusterLog(clusterId, mpSeq, TerramanConstant.TERRAFORM_CREATE_CLUSTER_DIRECTORY_ERROR);
        }

        //개인키 Master로 업로드
        File uploadKeyFile = new File(TerramanConstant.NCLOUD_PRI_FILE_PATH(TerramanConstant.MOVE_DIR_CLUSTER(clusterId), clusterId));
        commandService.sshFileUpload(Constants.MASTER_SSH_DIR, host, idRsa, uploadKeyFile, TerramanConstant.DEFAULT_USER_NAME);

        // 공개키(authorized_keys) 생성
        terramanCommandModel.setCommand("23");
        terramanCommandModel.setDir(TerramanConstant.CLUSTER_STATE_DIR(clusterId));
        terramanCommandModel.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel.setClusterId(clusterId);
        commandService.execCommandOutput(terramanCommandModel);
        cResult = commandService.execCommandOutput(terramanCommandModel);
        if (StringUtils.equals(Constants.RESULT_STATUS_FAIL, cResult)) {
            clusterService.updateCluster(clusterId, TerramanConstant.CLUSTER_FAIL_STATUS);
            clusterLogService.saveClusterLog(clusterId, mpSeq, TerramanConstant.TERRAFORM_CREATE_CLUSTER_DIRECTORY_ERROR);
        }

        // Ncloud 접속 및 .ssh 파일 생성
        List<NcloudPrivateKeyModel> ncloudPrivateKeysModel = instanceService.getNcloudPrivateKeys(clusterId, provider, host, idRsa, processGb);
        for (int i = 0; i < ncloudInstanceKeyModel.size(); i++) {
            terramanCommandModel.setCommand("19");
            terramanCommandModel.setDir(Constants.NCLOUD_HOST_DIR);
            if (ncloudPrivateKeysModel.get(i).getInstanceNo().equals(ncloudInstanceKeyModel.get(i).getServerInstanceNo())) {
                terramanCommandModel.setHost(ncloudPrivateKeysModel.get(i).getPublicIp());
            }
            terramanCommandModel.setUserName(TerramanConstant.NCLOUD_USER_NAME);
            terramanCommandModel.setInstanceKey(ncloudInstanceKeyModel.get(i).getRootPassword());
            terramanCommandModel.setIdRsa(ncloudInstanceKeyModel.get(i).getRootPassword());
            cResult = commandService.execPwdCommandOutput(terramanCommandModel);
            if (StringUtils.equals(Constants.RESULT_STATUS_FAIL, cResult)) {
                clusterService.updateCluster(clusterId, TerramanConstant.CLUSTER_FAIL_STATUS);
                clusterLogService.saveClusterLog(clusterId, mpSeq, TerramanConstant.TERRAFORM_CREATE_CLUSTER_DIRECTORY_ERROR);
            }
        }

        // Ncloud 접속 및 .ssh/authorized_keys 파일(공개키) 업로드
        File uploadFile = new File(TerramanConstant.NCLOUD_PUB_FILE_PATH(TerramanConstant.MOVE_DIR_CLUSTER(clusterId)));
        for (int i = 0; i < ncloudInstanceKeyModel.size(); i++) {

            if (ncloudPrivateKeysModel.get(i).getInstanceNo().equals(ncloudInstanceKeyModel.get(i).getServerInstanceNo())) {
                host = ncloudPrivateKeysModel.get(i).getPublicIp();
            }
            commandService.sshPwdFileUpload(Constants.NCLOUD_SSH_DIR, host, ncloudInstanceKeyModel.get(i).getRootPassword(), uploadFile, TerramanConstant.NCLOUD_USER_NAME);
        }

        // Master Ncloud 개인키 권한 변경 600
        terramanCommandModel.setCommand("20");
        terramanCommandModel.setDir(Constants.MASTER_SSH_DIR);
        terramanCommandModel.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel.setClusterId(clusterId);
        terramanCommandModel.setHost(masterHost);
        terramanCommandModel.setIdRsa(idRsa);
        cResult = commandService.execCommandOutput(terramanCommandModel);
        if (StringUtils.equals(Constants.RESULT_STATUS_FAIL, cResult)) {
            clusterService.updateCluster(clusterId, TerramanConstant.CLUSTER_FAIL_STATUS);
            clusterLogService.saveClusterLog(clusterId, mpSeq, TerramanConstant.TERRAFORM_CREATE_CLUSTER_DIRECTORY_ERROR);
        }
        return resultCode;
    };
}

