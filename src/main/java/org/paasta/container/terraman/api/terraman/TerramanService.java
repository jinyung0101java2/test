package org.paasta.container.terraman.api.terraman;

import org.apache.commons.lang3.StringUtils;
import org.paasta.container.terraman.api.common.constants.Constants;
import org.paasta.container.terraman.api.common.constants.TerramanConstant;
import org.paasta.container.terraman.api.common.model.*;
import org.paasta.container.terraman.api.common.service.*;
import org.paasta.container.terraman.api.common.util.CommonFileUtils;
import org.paasta.container.terraman.api.common.util.SSHUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
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
    private final CommonFileUtils fileUtil;
    private final ClusterService clusterService;

    @Autowired
    public TerramanService(
            VaultService vaultService
            , CommonService commonService
            , ClusterLogService clusterLogService
            , CommandService commandService
            , InstanceService instanceService
            , AccountService accountService
            , CommonFileUtils fileUtil
            , ClusterService clusterService
    ) {
        this.vaultService = vaultService;
        this.commonService = commonService;
        this.clusterLogService = clusterLogService;
        this.commandService = commandService;
        this.instanceService = instanceService;
        this.accountService = accountService;
        this.fileUtil = fileUtil;
        this.clusterService = clusterService;
    }

    /**
     * Terraman 생성(Create Terraman)
     *
     * @param terramanRequest the init terramanRequest
     * @return the resultStatus
     */
    @Async
    public ResultStatusModel createTerraman(TerramanRequest terramanRequest) {
        /**************************************************************************************************************************************
         * 변수 정의
         * ************************************************************************************************************************************/
        ResultStatusModel resultStatus = new ResultStatusModel();
        String clusterId = terramanRequest.getClusterId();

        int seq = Integer.parseInt(terramanRequest.getSeq());
        String provider = terramanRequest.getProvider();
        String cResult = "";
        String fResult = "";
        int mpSeq = 0;

        InstanceModel instanceInfo = null;

        // 해당 클러스터 디렉토리 생성
        cResult = commandService.execCommandOutput(TerramanConstant.CREATE_DIR_CLUSTER(clusterId), "");
        if(StringUtils.equals(Constants.RESULT_STATUS_FAIL, cResult)) {
            return (ResultStatusModel) commonService.setResultModel(resultStatus, cResult);
        }

        /**************************************************************************************************************************************
         *  1. 대상 Container 명 알아내기
         * */
        LOGGER.info("1. current directory :: " + commandService.execCommandOutput(TerramanConstant.DIRECTORY_COMMAND, TerramanConstant.MOVE_DIR_CLUSTER(clusterId)));
        /*************************************************************************************************************************************/

        /**************************************************************************************************************************************
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
         * ***********************************************************************************************************************************/
        LOGGER.info("execute terraform!!");
        LOGGER.info("2. current directory :: " + commandService.execCommandOutput(TerramanConstant.DIRECTORY_COMMAND, TerramanConstant.MOVE_DIR_CLUSTER(clusterId)));

        // log 저장
        clusterLogService.saveClusterLog(clusterId, mpSeq++, TerramanConstant.TERRAFORM_START_LOG(provider));
        /*
        * terraform 생성을 위한 각종 tf파일 가져오기
        * */

        /*
        * ************************************************************************************************************************************/

        // log 저장
        clusterLogService.saveClusterLog(clusterId, mpSeq++, TerramanConstant.TERRAFORM_IAC_LOG);
        /*************************************************************************************************************************************/

        /**************************************************************************************************************************************
         * 3-1. IaaS에 따라 provider.tf 파일 정의 (Vault, Database)
         *
         * - command
         * INSTANCE_COPY_COMMAND = "kubectl cp -n cp-portal cp-portal-api-deployment-6b94d6945d-jzvfh:tmp/test/ /home/ubuntu/tmp/instance.tf"
         *
         * - log
         * TERRAFORM_TF_ERROR_LOG = "Provider file creation error, cluster creation aborted. errCode ::";
         * TERRAFORM_TF_LOG = "Tf file for instance configuration is complete.";
         * ************************************************************************************************************************************/
        LOGGER.info("3. current directory :: " + commandService.execCommandOutput(TerramanConstant.DIRECTORY_COMMAND, TerramanConstant.MOVE_DIR_CLUSTER(clusterId)));

        cResult = commandService.execCommandOutput(TerramanConstant.POD_NAME_COMMAND, "");

        fResult = fileUtil.createProviderFile(clusterId, provider, seq, cResult);
        if(StringUtils.equals(fResult, Constants.RESULT_STATUS_FAIL)) {
            // log 저장
            clusterLogService.saveClusterLog(clusterId, mpSeq++, TerramanConstant.TERRAFORM_TF_ERROR_LOG + fResult);
            return (ResultStatusModel) commonService.setResultModel(resultStatus, fResult);
        }

        //commandService.execCommandOutput(TerramanConstant.NETWORK_COPY_COMMAND(cResult), TerramanConstant.MOVE_DIR_CLUSTER(clusterId));

        // log 저장
        clusterLogService.saveClusterLog(clusterId, mpSeq++, TerramanConstant.TERRAFORM_TF_LOG);
        /*************************************************************************************************************************************/

        /**************************************************************************************************************************************
         * 4. terraform init 실행
         *
         * - command
         * TERRAFORM_INIT_COMMAND = "terraform init";
         *
         * - log
         * TERRAFORM_INIT_LOG = "Terraform initialization is complete.";
         * ************************************************************************************************************************************/
        LOGGER.info("4. current directory :: " + commandService.execCommandOutput(TerramanConstant.DIRECTORY_COMMAND, TerramanConstant.MOVE_DIR_CLUSTER(clusterId)));

        // command line 실행
        cResult = commandService.execCommandOutput(TerramanConstant.TERRAFORM_INIT_COMMAND, TerramanConstant.MOVE_DIR_CLUSTER(clusterId));
        if(StringUtils.equals(cResult, Constants.RESULT_STATUS_FAIL)) {
            LOGGER.info("terraform init 확인하십시오. " + cResult);
            return (ResultStatusModel) commonService.setResultModel(resultStatus, cResult);
        }

        LOGGER.info("Processing terraform init. " + cResult);

        // log 저장
        clusterLogService.saveClusterLog(clusterId, mpSeq++, TerramanConstant.TERRAFORM_INIT_LOG);
        /*************************************************************************************************************************************/

        /**************************************************************************************************************************************
         * 5. terraform plan 실행
         *
         * - command
         * TERRAFORM_PLAN_COMMAND = "terraform plan -var vpc_name=paasta-cp-vpc -var route_table_name=paasta-cp-routing-public";
         *
         * - log
         * TERRAFORM_PLAN_LOG = "The system has confirmed that there are no problems with the terraform plan.";
         * ************************************************************************************************************************************/
        LOGGER.info("5. current directory :: " + commandService.execCommandOutput(TerramanConstant.DIRECTORY_COMMAND, TerramanConstant.MOVE_DIR_CLUSTER(clusterId)));

        // command line 실행
        cResult = commandService.execCommandOutput(TerramanConstant.TERRAFORM_PLAN_COMMAND, TerramanConstant.MOVE_DIR_CLUSTER(clusterId));
        if(StringUtils.equals(Constants.RESULT_STATUS_FAIL, cResult)) {
            LOGGER.info("terraform plan을 확인하십시오. " + cResult);
            return (ResultStatusModel) commonService.setResultModel(resultStatus, cResult);
        }

        LOGGER.info("Processing terraform plan " + cResult);
        // log 저장
        clusterLogService.saveClusterLog(clusterId, mpSeq++, TerramanConstant.TERRAFORM_PLAN_LOG);
        /*************************************************************************************************************************************/

        /**************************************************************************************************************************************
         * 6. terraform apply 실행
         *
         * - command
         * TERRAFORM_APPLY_COMMAND = "terraform apply -auto-approve -var vpc_name=paasta-cp-vpc -var route_table_name=paasta-cp-routing-public";
         *
         * - log
         * TERRAFORM_APPLY_LOG = "The system has finished configuring the instances for cluster creation.";
         * ************************************************************************************************************************************/
        LOGGER.info("6. current directory :: " + commandService.execCommandOutput(TerramanConstant.DIRECTORY_COMMAND, TerramanConstant.MOVE_DIR_CLUSTER(clusterId)));

        // command line 실행
        cResult = commandService.execCommandOutput(TerramanConstant.TERRAFORM_APPLY_COMMAND, TerramanConstant.MOVE_DIR_CLUSTER(clusterId));
        if(StringUtils.equals(Constants.RESULT_STATUS_FAIL, cResult)) {
            LOGGER.info(cResult);
            return (ResultStatusModel) commonService.setResultModel(resultStatus, cResult);
        }

        LOGGER.info("Instance 생성이 완료되었습니다. " + cResult);
        // log 저장
        clusterLogService.saveClusterLog(clusterId, mpSeq++, TerramanConstant.TERRAFORM_APPLY_LOG);
        /*************************************************************************************************************************************/

        /**************************************************************************************************************************************
         * 7. Infra 생성 후 생성된 Instance IP 알아오기
         *
         * - log
         * TERRAFORM_SUCCESS_LOG = "It succeeded in loading the configuration information of the newly created instance.";
         * ************************************************************************************************************************************/
        LOGGER.info("7. current directory :: " + commandService.execCommandOutput(TerramanConstant.DIRECTORY_COMMAND, TerramanConstant.MOVE_DIR_CLUSTER(clusterId)));

        instanceInfo = instanceService.getInstansce(clusterId, provider);
        LOGGER.info("instanceInfo :: " + instanceInfo.toString());
        if(instanceInfo == null) {
            LOGGER.info("Instance is not exists");
            return (ResultStatusModel) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_FAIL);
        }

        SSHUtil sshUtil = new SSHUtil();
        Loop : for(int i = 0; i<100; i++) {
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
            cResult = sshUtil.getSSHResponse(TerramanConstant.DIRECTORY_COMMAND, instanceInfo.getPrivateIp());
            if(StringUtils.isNotBlank(cResult) && !StringUtils.equals(cResult, Constants.RESULT_STATUS_FAIL)) {
                break Loop;
            }
        }
        LOGGER.info("ssh connection :: " + cResult);

        try {
            Thread.sleep(60000);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

        clusterLogService.saveClusterLog(clusterId, mpSeq++, TerramanConstant.TERRAFORM_SUCCESS_LOG);
        /*************************************************************************************************************************************/

        /**************************************************************************************************************************************
         * 8-1. Kubespray 다운로드 및 kubespray_var.sh 파일 작성하기
         *
         * - command
         * TERRAFORM_CHANGE_DIRECTORY_COMMAND = "cd /paas-ta-container-platform-deployment/standalone/aws";
         * TERRAFORM_KUBESPRAY_COMMAND = "#!/bin/bash \\n\\n";
         *
         * - log
         * KUBESPRAY_CONFIG_LOG = "Configuration information update for cluster configuration has been completed.";
         * ************************************************************************************************************************************/
        LOGGER.info("8. current directory :: " + commandService.execCommandOutput(TerramanConstant.DIRECTORY_COMMAND, TerramanConstant.MOVE_DIR_KUBESPRAY));
        List<InstanceModel> instanceList = instanceService.getInstances(clusterId, provider);

        int workerCnt = instanceList.size()-1;
        int workerSeq = 1;
        StringBuffer sb = new StringBuffer();
        sb.append(TerramanConstant.TERRAFORM_KUBESPRAY_COMMAND);
        for(InstanceModel obj : instanceList) {
            String line = "";
            if( obj.getResourceName().contains("master") ) {
                line = "export MASTER_NODE_HOSTNAME=" + obj.getInstanceName()
                        + "\\n"
                        + "export MASTER_NODE_PUBLIC_IP=" + obj.getPublicIp()
                        + "\\n"
                        + "export MASTER_NODE_PRIVATE_IP=" + obj.getPrivateIp();
            }
            sb.append(line);
        }

        sb.append("\\n\\n" + "export WORKER_NODE_CNT=" + workerCnt + "\\n");

        for(InstanceModel obj : instanceList) {
            String line = "";
            if( obj.getResourceName().contains("worker") ) {
                line = "\\n"
                        + "export WORKER" + workerSeq
                        + "_NODE_HOSTNAME=" + obj.getInstanceName()
                        + "\\n"
                        + "export WORKER" + workerSeq
                        + "_NODE_PRIVATE_IP=" + obj.getPrivateIp();
                workerSeq++;
            }
            sb.append(line);
        }

        cResult = commandService.execCommandOutput(TerramanConstant.CLUSTER_KUBESPRAY_SH_FILE_COMMAND(sb.toString()), "");
        if(StringUtils.equals(Constants.RESULT_STATUS_FAIL, cResult)) {
            LOGGER.info("Kubespray 파일 생성 중 오류가 발생하였습니다. " + cResult);
            return (ResultStatusModel) commonService.setResultModel(resultStatus, cResult);
        }

        // log 저장
        clusterLogService.saveClusterLog(clusterId, mpSeq++, TerramanConstant.KUBESPRAY_CONFIG_LOG);
        /*************************************************************************************************************************************/

        /**************************************************************************************************************************************
         * 9. source deploy_kubespray.sh 실행하기
         *
         * - command
         * KUBESPRAY_CHMOD_COMMAND = "chmod +x /home/ubuntu/paas-ta-container-platform-deployment/standalone/single_control_plane/deploy-cp-cluster.sh";
         * CLUSTER_KUBESPRAY_DEPLOY_COMMAND = "source /home/ubuntu/paas-ta-container-platform-deployment/standalone/single_control_plane/deploy-cp-cluster.sh";
         *
         * - log
         * KUBESPRAY_DEPLOY_LOG = "The provisioning of the cluster is complete.";
         * ************************************************************************************************************************************/
        LOGGER.info("9. current directory :: " + commandService.execCommandOutput(TerramanConstant.DIRECTORY_COMMAND, TerramanConstant.MOVE_DIR_KUBESPRAY));

        cResult = commandService.execCommandOutput(TerramanConstant.KUBESPRAY_CHMOD_COMMAND, TerramanConstant.MOVE_DIR_KUBESPRAY);
        if(StringUtils.equals(Constants.RESULT_STATUS_FAIL, cResult)) {
            LOGGER.info("Kubespray 모드 변경 중 오류가 발생하였습니다. " + cResult);
            return (ResultStatusModel) commonService.setResultModel(resultStatus, cResult);
        }

        cResult = commandService.execCommandOutput(TerramanConstant.CLUSTER_KUBESPRAY_DEPLOY_COMMAND, TerramanConstant.MOVE_DIR_KUBESPRAY);
        if(StringUtils.equals(Constants.RESULT_STATUS_FAIL, cResult)) {
            LOGGER.info("Kubespray 실행 중 오류가 발생하였습니다. " + cResult);
            return (ResultStatusModel) commonService.setResultModel(resultStatus, cResult);
        }

        LOGGER.info("클러스터 배포가 완료되었습니다. " + cResult);
        // log 저장
        clusterLogService.saveClusterLog(clusterId, mpSeq++, TerramanConstant.KUBESPRAY_DEPLOY_LOG);
        /*************************************************************************************************************************************/

        /**************************************************************************************************************************************
         * 10. 클러스터 생성 상태 전송 --> DB 업데이트
         * ************************************************************************************************************************************/
        ClusterModel updateResult = clusterService.updateCluster(clusterId, TerramanConstant.CLUSTER_COMPLETE_STATUS);
        if(updateResult == null) {
            LOGGER.info("cluster 생성 완료 업데이트 중 오류가 발생하였습니다.");
            return (ResultStatusModel) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_FAIL);
        }
        /*************************************************************************************************************************************/

        /**************************************************************************************************************************************
         * 11. 완료 후 프로세스 종료
         * ************************************************************************************************************************************/
        return (ResultStatusModel) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
        /*************************************************************************************************************************************/
    }

    /**
     * Terraman 삭제(Delete Terraman)
     *
     * @param clusterId
     * @return the resultStatus
     */
    public ResultStatusModel deleteTerraman(String clusterId) {
        ResultStatusModel resultStatus = new ResultStatusModel();
        String cResult = Constants.RESULT_STATUS_SUCCESS;
        cResult = commandService.execCommandOutput(TerramanConstant.TERRAFORM_DESTROY_COMMAND, TerramanConstant.MOVE_DIR_CLUSTER(clusterId));
        if(StringUtils.equals(Constants.RESULT_STATUS_FAIL, cResult)) {
            LOGGER.info("terraform 삭제 중 오류가 발생하였습니다." + cResult);
            return (ResultStatusModel) commonService.setResultModel(resultStatus, cResult);
        } else {
            cResult = commandService.execCommandOutput(TerramanConstant.DELETE_CLUSTER(clusterId), TerramanConstant.DELETE_DIR_CLUSTER);
            if(StringUtils.equals(Constants.RESULT_STATUS_FAIL, cResult)) {
                LOGGER.info("Cluster 삭제 중 오류가 발생하였습니다. " + cResult);
                return (ResultStatusModel) commonService.setResultModel(resultStatus, cResult);
            }
        }

        return (ResultStatusModel) commonService.setResultModel(new ResultStatusModel(), Constants.RESULT_STATUS_SUCCESS);

    }
}
