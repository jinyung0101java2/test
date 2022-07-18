package org.paasta.container.terraman.api.terraman;

import org.apache.commons.lang3.StringUtils;
import org.paasta.container.terraman.api.common.constants.Constants;
import org.paasta.container.terraman.api.common.constants.TerramanConstant;
import org.paasta.container.terraman.api.common.model.*;
import org.paasta.container.terraman.api.common.service.*;
import org.paasta.container.terraman.api.common.util.CommonFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TerramanService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TerramanService.class);

    private final VaultService vaultService;
    private final CommonService commonService;
    private final ClusterLogService clusterLogService;
    private final CommandService commandService;
    private final InstanceService instanceService;
    private final AccountService accountService;

    @Autowired
    public TerramanService(
            VaultService vaultService
            , CommonService commonService
            , ClusterLogService clusterLogService
            , CommandService commandService
            , InstanceService instanceService
            , AccountService accountService
    ) {
        this.vaultService = vaultService;
        this.commonService = commonService;
        this.clusterLogService = clusterLogService;
        this.commandService = commandService;
        this.instanceService = instanceService;
        this.accountService = accountService;
    }

    /**
     * Terraman 생성(Create Terraman)
     *
     * @param terramanRequest the init terramanRequest
     * @return the resultStatus
     */
    public ResultStatusModel createTerraman(TerramanRequest terramanRequest) {
        /**
         * 변수 정의
         */
        ResultStatusModel resultStatus = new ResultStatusModel();
        String clusterId = terramanRequest.getClusterId();
        int seq = Integer.parseInt(terramanRequest.getSeq());
        String provider = terramanRequest.getProvider();
        String cResult = "";
        String fResult = "";
        int mpSeq = 0;

        InstanceModel instanceInfo = null;

        /**
         *  1. 대상 Container 명 알아내기
         * */
        LOGGER.info("1. current directory :: " + commandService.execCommandOutput(TerramanConstant.DIRECTORY_COMMAND));
        /*************************************************************************************************************************************/

        /**
         * 2. 대상 Container 에서 kubectl cp 로 파일 복사해오기
         * kubectl cp -n mariadb paas-ta-container-platform-mariadb-0:var/lib/apt/extended_status /home/ubuntu/aa  --> 파일복사
         * kubectl cp -n mariadb paas-ta-container-platform-mariadb-0:bitnami/ /home/ubuntu/bitnami --> 폴더복사*
         *
         * - command
         * FILE_COPY_COMMAND = KUBECTL + " cp -n paas-ta-container-platform-mariadb-0:bitnami" + BASE_DIR;
         *
         * - log
         * TERRAFORM_IAC_LOG = "Upload of requested IaC information is complete.";
         * TERRAFORM_START_LOG = "Start creating cluster(Provider : "+provider+")";
         * */
        LOGGER.info("execute terraform!!");
        LOGGER.info("2. current directory :: " + commandService.execCommandOutput(TerramanConstant.DIRECTORY_COMMAND));

        // log 저장
        clusterLogService.saveClusterLog(clusterId, mpSeq++, TerramanConstant.TERRAFORM_START_LOG(provider));

        // command line 실행
        cResult = commandService.execCommandOutput(TerramanConstant.FILE_COPY_COMMAND);
        if(StringUtils.equals(Constants.RESULT_STATUS_FAIL, cResult)) {
            LOGGER.info(cResult);
            return (ResultStatusModel) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_FAIL);
        }

        LOGGER.info("file copy complete!!");


        // log 저장
        clusterLogService.saveClusterLog(clusterId, mpSeq++, TerramanConstant.TERRAFORM_IAC_LOG);
        /*************************************************************************************************************************************/

        /**
         * 3. IaaS에 따라 provider.tf 파일 정의 (Vault, Database)
         *
         * - command
         * INSTANCE_COPY_COMMAND = "cp tf-source/aws/terraman-opt02/opt02-resource.tf ./instance.tf";
         *
         * - log
         * TERRAFORM_TF_ERROR_LOG = "Provider file creation error, cluster creation aborted. errCode ::";
         * TERRAFORM_TF_LOG = "Tf file for instance configuration is complete.";
         * */
        LOGGER.info("3. current directory :: " + commandService.execCommandOutput(TerramanConstant.DIRECTORY_COMMAND));

        fResult = createProviderFile(provider, seq);
        if(!StringUtils.equals(fResult, "200")) {

            // log 저장
            clusterLogService.saveClusterLog(clusterId, mpSeq++, TerramanConstant.TERRAFORM_TF_ERROR_LOG + fResult);
            return (ResultStatusModel) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_FAIL);
        }


        // log 저장
        clusterLogService.saveClusterLog(clusterId, mpSeq++, TerramanConstant.TERRAFORM_TF_LOG);
        /*************************************************************************************************************************************/

        /**
         * 4. terraform init 실행
         *
         * - command
         * TERRAFORM_INIT_COMMAND = "terraform init";
         *
         * - log
         * TERRAFORM_INIT_LOG = "Terraform initialization is complete.";
         * */
        LOGGER.info("4. current directory :: " + commandService.execCommandOutput(TerramanConstant.DIRECTORY_COMMAND));

        // command line 실행
        cResult = commandService.execCommandOutput(TerramanConstant.TERRAFORM_INIT_COMMAND);
        if(StringUtils.equals(Constants.RESULT_STATUS_FAIL, cResult)) {
            LOGGER.info("terraform init 확인하십시오. " + cResult);
            return (ResultStatusModel) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_FAIL);
        }

        LOGGER.info("Processing terraform init. " + cResult);

        // log 저장
        clusterLogService.saveClusterLog(clusterId, mpSeq++, TerramanConstant.TERRAFORM_INIT_LOG);
        /*************************************************************************************************************************************/

        /**
         * 5. terraform plan 실행
         *
         * - command
         * TERRAFORM_PLAN_COMMAND = "terraform plan -var vpc_name=paasta-cp-vpc -var route_table_name=paasta-cp-routing-public";
         *
         * - log
         * TERRAFORM_PLAN_LOG = "The system has confirmed that there are no problems with the terraform plan.";
         */
        LOGGER.info("5. current directory :: " + commandService.execCommandOutput(TerramanConstant.DIRECTORY_COMMAND));

        // command line 실행
        cResult = commandService.execCommandOutput(TerramanConstant.TERRAFORM_PLAN_COMMAND);
        if(StringUtils.equals(Constants.RESULT_STATUS_FAIL, cResult)) {
            LOGGER.info("terraform plan을 확인하십시오. " + cResult);
            return (ResultStatusModel) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_FAIL);
        }

        LOGGER.info("Processing terraform plan " + cResult);
        // log 저장
        clusterLogService.saveClusterLog(clusterId, mpSeq++, TerramanConstant.TERRAFORM_PLAN_LOG);
        /*************************************************************************************************************************************/

        /**
         * 6. terraform apply 실행
         *
         * - command
         * TERRAFORM_APPLY_COMMAND = "terraform apply -auto-approve -var vpc_name=paasta-cp-vpc -var route_table_name=paasta-cp-routing-public";
         *
         * - log
         * TERRAFORM_APPLY_LOG = "The system has finished configuring the instances for cluster creation.";
         */
        LOGGER.info("6. current directory :: " + commandService.execCommandOutput(TerramanConstant.DIRECTORY_COMMAND));

        // command line 실행
        cResult = commandService.execCommandOutput(TerramanConstant.TERRAFORM_APPLY_COMMAND);
        if(StringUtils.equals(Constants.RESULT_STATUS_FAIL, cResult)) {
            LOGGER.info(cResult);
            return (ResultStatusModel) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_FAIL);
        }

        LOGGER.info("Instance 생성이 완료되었습니다. " + cResult);
        // log 저장
        clusterLogService.saveClusterLog(clusterId, mpSeq++, TerramanConstant.TERRAFORM_APPLY_LOG);
        /*************************************************************************************************************************************/

        /**
         * 7. Infra 생성 후 생성된 Instance IP 알아오기
         *
         * - log
         * TERRAFORM_SUCCESS_LOG = "It succeeded in loading the configuration information of the newly created instance.";
         */
        LOGGER.info("7. current directory :: " + commandService.execCommandOutput(TerramanConstant.DIRECTORY_COMMAND));

        instanceInfo = instanceService.getInstansceInfo();
        if(instanceInfo == null) {
            LOGGER.info("Instance is not exists");
            return (ResultStatusModel) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_FAIL);
        }

        clusterLogService.saveClusterLog(clusterId, mpSeq++, TerramanConstant.TERRAFORM_SUCCESS_LOG);
        /*************************************************************************************************************************************/

        /**
         * 8. kubespray_var.sh 파일 작성하기
         *
         * - command
         * TERRAFORM_CHANGE_DIRECTORY_COMMAND = "cd /paas-ta-container-platform-deployment/standalone/aws";
         * TERRAFORM_KUBESPRAY_COMMAND = "#!/bin/bash \\n\\n";
         *
         * - log
         * KUBESPRAY_CONFIG_LOG = "Configuration information update for cluster configuration has been completed.";
         */
        LOGGER.info("8. current directory :: " + commandService.execCommandOutput(TerramanConstant.DIRECTORY_COMMAND));
        commandService.execCommandOutput(TerramanConstant.TERRAFORM_CHANGE_DIRECTORY_COMMAND);
        LOGGER.info("8-1. current directory :: " + commandService.execCommandOutput(TerramanConstant.DIRECTORY_COMMAND));

        CommonFileUtils fileUtil = new CommonFileUtils();

        String delResult = fileUtil.tfFileDelete("kubespray_var.sh");
        if(!StringUtils.equals(delResult, "200")) {
            return (ResultStatusModel) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_FAIL);
        }

        int workerCnt = 1;
        List<String> rst = new ArrayList<String>();
        List<InstanceModel> instanceList = instanceService.getTotalInstance();
        for(InstanceModel obj : instanceList) {
            String line = "";
            if( StringUtils.equals("master",obj.getResourceName()) ) {
                line = "export MASTER_NODE_HOSTNAME=" + obj.getInstanceName()
                        + System.lineSeparator()
                        + "export MASTER_NODE_PUBLIC_IP=" + obj.getPublicIp()
                        + System.lineSeparator()
                        + "export MASTER_NODE_PRIVATE_IP=" + obj.getPrivateIp();

            } else {
                line = System.lineSeparator()
                        + System.lineSeparator()
                        + "export WORKER" + workerCnt
                        + "_NODE_HOSTNAME=" + obj.getInstanceName()
                        + System.lineSeparator()
                        + "export WORKER" + workerCnt
                        + "_NODE_PRIVATE_IP=" + obj.getPrivateIp();
            }
            workerCnt++;
            rst.add(line);
        }

        String fileData = TerramanConstant.TERRAFORM_KUBESPRAY_COMMAND;
        for(String line : rst) {
            fileData += line;
        }

        String fileResult = fileUtil.createWithWrite("kubespray_var.sh", fileData);

        if(!StringUtils.equals(fileResult, "200")) {
            return (ResultStatusModel) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_FAIL);
        }
        // log 저장
        clusterLogService.saveClusterLog(clusterId, mpSeq++, TerramanConstant.KUBESPRAY_CONFIG_LOG);
        /*************************************************************************************************************************************/

        /**
         * 9. source deploy_kubespray.sh 실행하기
         *
         * - command
         * KUBESPRAY_CHMOD_COMMAND = MOD_CHG + " deploy_kubespray.sh";
         * KUBESPRAY_DEPLOY_COMMAND = "./deploy_kubespray.sh";
         *
         * - log
         * KUBESPRAY_DEPLOY_LOG = "The provisioning of the cluster is complete.";
         */
        LOGGER.info("9. current directory :: " + commandService.execCommandOutput(TerramanConstant.DIRECTORY_COMMAND));

        // command line 실행
        cResult = commandService.execCommandOutput(TerramanConstant.KUBESPRAY_CHMOD_COMMAND);
        if(StringUtils.equals(Constants.RESULT_STATUS_FAIL, cResult)) {
            return (ResultStatusModel) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_FAIL);
        }

        // command line 실행
        cResult = commandService.execCommandOutput(TerramanConstant.KUBESPRAY_DEPLOY_COMMAND);
        if(StringUtils.equals(Constants.RESULT_STATUS_FAIL, cResult)) {
            return (ResultStatusModel) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_FAIL);
        }

        LOGGER.info("클러스터 배포가 완료되었습니다. " + cResult);
        // log 저장
        clusterLogService.saveClusterLog(clusterId, mpSeq++, TerramanConstant.KUBESPRAY_DEPLOY_LOG);
        /*************************************************************************************************************************************/

        /**
         * 10. 클러스터 생성 상태 전송 --> API
         *
         * - command
         *
         *
         * - log
         */

        /*************************************************************************************************************************************/

        /**
         * 11. 완료 후 프로세스 종료
         */

        /*************************************************************************************************************************************/
//        String path = "secret/" + String.valueOf(terramanRequest.getProvider()).toUpperCase() + "/" + terramanRequest.getSeq();
//        vaultService.read(path, new TerramanResponse().getClass());
        return (ResultStatusModel) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_FAIL);
    }

    /**
     * Terraman 삭제(Delete Terraman)
     *
     * @param terramanRequest the terramanRequest
     * @return the resultStatus
     */
    public ResultStatusModel deleteTerraman(TerramanRequest terramanRequest) {
        return (ResultStatusModel) commonService.setResultModel(deleteProcess(terramanRequest), Constants.RESULT_STATUS_SUCCESS);

    }

    /**
     * Terraman 생성 process (Create Terraman Process)
     *
     * @param terramanRequest the terramanRequest
     * @return the TerramanResponse
     */
//    private ResultStatusModel createProcess(TerramanRequest terramanRequest) {
//
//    }

    /**
     * Terraman 삭제 process (Delete Terraman Process)
     *
     * @param terramanRequest the terramanRequest
     * @return the TerramanResponse
     */
    public ResultStatusModel deleteProcess(TerramanRequest terramanRequest) {
//        String path = "secret/" + String.valueOf(terramanRequest.getProvider()).toUpperCase() + "/" + terramanRequest.getSeq();
//        vaultService.read(path, new TerramanResponse().getClass());
        return (ResultStatusModel) commonService.setResultModel(new ResultStatusModel(), Constants.RESULT_STATUS_FAIL);
    }


    private String createProviderFile(String provider, int seq) {
        String resultCode = "500";
        try {
            if(StringUtils.equals(Constants.UPPER_AWS, provider.toUpperCase())) {

            } else if(StringUtils.equals(Constants.UPPER_GCP, provider.toUpperCase())) {

            } else if(StringUtils.equals(Constants.UPPER_OPENSTACK, provider.toUpperCase())) {
                String path = "secret/" + provider.toUpperCase() + "/" + seq;
                VaultModel res = vaultService.read(path, new VaultModel().getClass());

                if(res != null) {
                    AccountModel account = accountService.getAccountInfo(seq);
                    // 파일 생성 및 쓰기
                    CommonFileUtils fileUtil = new CommonFileUtils();
                    FileModel fileModel = new FileModel();
                    fileModel.setTenant_name(account.getProject());
                    fileModel.setPassword(res.getPassword());
                    fileModel.setAuth_url(res.getAuth_url());
                    fileModel.setUser_name(res.getUser_name());
                    fileModel.setRegion(account.getRegion());
                    String resultFile = fileUtil.tfCreateWithWrite(fileModel);

                    if(StringUtils.equals(resultFile, "200")) {
                        String cResult = commandService.execCommandOutput(TerramanConstant.INSTANCE_COPY_COMMAND);
                        if(!StringUtils.equals(Constants.RESULT_STATUS_FAIL, cResult)) {
                            resultCode = "200";
                            LOGGER.info("인스턴스 파일 복사가 완료되었습니다. " + cResult);
                        }
                    }
                }
            } else {
                LOGGER.error(provider + " is Cloud not supported.");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return resultCode;
    }




}
