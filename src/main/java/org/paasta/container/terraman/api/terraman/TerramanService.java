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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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
    private final PropertyService propertyService;
    private final TfFileService tfFileService;

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
            , PropertyService propertyService
            , TfFileService tfFileService
    ) {
        this.vaultService = vaultService;
        this.commonService = commonService;
        this.clusterLogService = clusterLogService;
        this.commandService = commandService;
        this.instanceService = instanceService;
        this.accountService = accountService;
        this.fileUtil = fileUtil;
        this.clusterService = clusterService;
        this.propertyService = propertyService;
        this.tfFileService = tfFileService;
    }

    /**
     * Terraman 생성(Create Terraman)
     *
     * @param terramanRequest the init terramanRequest
     * @return the resultStatus
     */
    @Async
    public ResultStatusModel createTerraman(TerramanRequest terramanRequest, String processGb) {
        /**************************************************************************************************************************************
         * 변수 정의
         * ************************************************************************************************************************************/
        ResultStatusModel resultStatus = new ResultStatusModel();
        String clusterId = terramanRequest.getClusterId();

        int seq = Integer.parseInt(terramanRequest.getSeq());
        String provider = terramanRequest.getProvider();
        //String processGb = terramanRequest.getProcessGb();
        String cResult = "";
        String fResult = "";
        int mpSeq = 0;
        int sshChk = 0;

        InstanceModel instanceInfo = null;

        String host = "";
        String idRsa = "";
        String hostDir = "/home/ubuntu";

        //clusterService.updateCluster(clusterId, TerramanConstant.CLUSTER_CREATE_STATUS);

        if(!StringUtils.isBlank(processGb) && StringUtils.equals(processGb.toUpperCase(), "CONTAINER")) {
            LOGGER.info("container conn");
            host = propertyService.getMASTER_HOST();
            idRsa = TerramanConstant.MASTER_ID_RSA;
            cResult = commandService.execCommandOutput(TerramanConstant.CREATE_DIR_CLUSTER(clusterId), "", host, idRsa);
            if(StringUtils.equals(Constants.RESULT_STATUS_FAIL, cResult)) {
                clusterService.updateCluster(clusterId, TerramanConstant.CLUSTER_FAIL_STATUS);
                return (ResultStatusModel) commonService.setResultModel(resultStatus, cResult);
            }
            hostDir = "";
        }

        // cluster log 삭제
        try {
            clusterLogService.deleteClusterLogByClusterId(clusterId);
        } catch (Exception e) {
            LOGGER.error("cluster log 삭제에 실패하였습니다.");
            clusterService.updateCluster(clusterId, TerramanConstant.CLUSTER_FAIL_STATUS);
            return (ResultStatusModel) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_FAIL);
        }

        // 해당 클러스터 디렉토리 생성
        cResult = commandService.execCommandOutput(TerramanConstant.CREATE_DIR_CLUSTER(clusterId), hostDir, "", "");
        if(StringUtils.equals(Constants.RESULT_STATUS_FAIL, cResult)) {
            clusterService.updateCluster(clusterId, TerramanConstant.CLUSTER_FAIL_STATUS);
            clusterLogService.saveClusterLog(clusterId, mpSeq++, TerramanConstant.TERRAFORM_CREATE_CLUSTER_DIRECTORY);
            return (ResultStatusModel) commonService.setResultModel(resultStatus, cResult);
        }


        /**************************************************************************************************************************************
         *  1. 대상 Container 명 알아내기
         * */
        LOGGER.info("1. current directory :: " + commandService.execCommandOutput(TerramanConstant.DIRECTORY_COMMAND, TerramanConstant.MOVE_DIR_CLUSTER(clusterId, processGb), host, idRsa));
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
        LOGGER.info("2. current directory :: " + commandService.execCommandOutput(TerramanConstant.DIRECTORY_COMMAND, TerramanConstant.MOVE_DIR_CLUSTER(clusterId, processGb), host, idRsa));

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
        LOGGER.info("3. current directory :: " + commandService.execCommandOutput(TerramanConstant.DIRECTORY_COMMAND, TerramanConstant.MOVE_DIR_CLUSTER(clusterId, processGb), host, idRsa));

        cResult = commandService.execCommandOutput(TerramanConstant.POD_NAME_COMMAND, "", host, idRsa);
        try {
            fResult = tfFileService.createProviderFile(clusterId, provider, seq, cResult.trim(), host, idRsa, processGb);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            clusterLogService.saveClusterLog(clusterId, mpSeq++, TerramanConstant.TERRAFORM_TF_ERROR_LOG + fResult);
            clusterService.updateCluster(clusterId, TerramanConstant.CLUSTER_FAIL_STATUS);
        }
        if(StringUtils.equals(fResult, Constants.RESULT_STATUS_FAIL)) {
            // log 저장
            clusterLogService.saveClusterLog(clusterId, mpSeq++, TerramanConstant.TERRAFORM_TF_ERROR_LOG + fResult);
            clusterService.updateCluster(clusterId, TerramanConstant.CLUSTER_FAIL_STATUS);
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
        LOGGER.info("4. current directory :: " + commandService.execCommandOutput(TerramanConstant.DIRECTORY_COMMAND, TerramanConstant.MOVE_DIR_CLUSTER(clusterId, processGb), host, idRsa));

        // command line 실행
        cResult = commandService.execCommandOutput(TerramanConstant.TERRAFORM_INIT_COMMAND, TerramanConstant.MOVE_DIR_CLUSTER(clusterId, processGb), host, idRsa);
        if(StringUtils.equals(cResult, Constants.RESULT_STATUS_FAIL)) {
            LOGGER.error("terraform init 확인하십시오. " + cResult);
            clusterLogService.saveClusterLog(clusterId, mpSeq++, TerramanConstant.TERRAFORM_INIT_FAIL_LOG);
            clusterService.updateCluster(clusterId, TerramanConstant.CLUSTER_FAIL_STATUS);
            return (ResultStatusModel) commonService.setResultModel(resultStatus, cResult);
        }

        LOGGER.info("Processing terraform init.");
        //LOGGER.info("Processing terraform init. " + cResult);

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
        LOGGER.info("5. current directory :: " + commandService.execCommandOutput(TerramanConstant.DIRECTORY_COMMAND, TerramanConstant.MOVE_DIR_CLUSTER(clusterId, processGb), host, idRsa));

        // command line 실행
        cResult = commandService.execCommandOutput(TerramanConstant.TERRAFORM_PLAN_COMMAND, TerramanConstant.MOVE_DIR_CLUSTER(clusterId, processGb), host, idRsa);
        if(StringUtils.equals(Constants.RESULT_STATUS_FAIL, cResult)) {
            LOGGER.error("terraform plan을 확인하십시오. " + cResult);
            clusterLogService.saveClusterLog(clusterId, mpSeq++, TerramanConstant.TERRAFORM_PLAN_FAIL_LOG);
            clusterService.updateCluster(clusterId, TerramanConstant.CLUSTER_FAIL_STATUS);
            return (ResultStatusModel) commonService.setResultModel(resultStatus, cResult);
        }
        LOGGER.info("Processing terraform plan.");
//        LOGGER.info("Processing terraform plan " + cResult);
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
        LOGGER.info("6. current directory :: " + commandService.execCommandOutput(TerramanConstant.DIRECTORY_COMMAND, TerramanConstant.MOVE_DIR_CLUSTER(clusterId, processGb), host, idRsa));

        // command line 실행
        cResult = commandService.execCommandOutput(TerramanConstant.TERRAFORM_APPLY_COMMAND, TerramanConstant.MOVE_DIR_CLUSTER(clusterId, processGb), host, idRsa);
        if(StringUtils.equals(Constants.RESULT_STATUS_FAIL, cResult)) {
            clusterLogService.saveClusterLog(clusterId, mpSeq++, TerramanConstant.TERRAFORM_APPLY_FAIL_LOG);
            clusterService.updateCluster(clusterId, TerramanConstant.CLUSTER_FAIL_STATUS);
            return (ResultStatusModel) commonService.setResultModel(resultStatus, cResult);
        }
        LOGGER.info("Instance 생성이 완료되었습니다.");
        
        // log 저장
        clusterLogService.saveClusterLog(clusterId, mpSeq++, TerramanConstant.TERRAFORM_APPLY_LOG);
        /*************************************************************************************************************************************/

        /**************************************************************************************************************************************
         * 7. Infra 생성 후 생성된 Instance IP 알아오기
         *
         * - log
         * TERRAFORM_SUCCESS_LOG = "It succeeded in loading the configuration information of the newly created instance.";
         * ************************************************************************************************************************************/
        LOGGER.info("7. current directory :: " + commandService.execCommandOutput(TerramanConstant.DIRECTORY_COMMAND, TerramanConstant.MOVE_DIR_CLUSTER(clusterId, processGb), host, idRsa));

        instanceInfo = instanceService.getInstansce(clusterId, provider, host, idRsa, processGb);

        if(instanceInfo == null) {
            LOGGER.error("Instance is not exists");
            clusterLogService.saveClusterLog(clusterId, mpSeq++, TerramanConstant.TERRAFORM_NOT_EXISTS_INSTANCE_ERROR);
            clusterService.updateCluster(clusterId, TerramanConstant.CLUSTER_FAIL_STATUS);
            return (ResultStatusModel) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_FAIL);
        }

        Loop : for(int i = 0; i<100; i++) {
            try {
                Thread.sleep(10000);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
            LOGGER.info("ssh connection checked");
            cResult = commandService.execCommandOutput(TerramanConstant.DIRECTORY_COMMAND, "", instanceInfo.getPublicIp(), TerramanConstant.CLUSTER_PRIVATE_KEY(clusterId, processGb));
            LOGGER.info("ssh connection result :: " + cResult);
            if(StringUtils.isNotBlank(cResult) && !StringUtils.equals(cResult, Constants.RESULT_STATUS_FAIL)) {
                break Loop;
            } else if (StringUtils.isNotBlank(cResult) && StringUtils.contains(cResult, Constants.RESULT_STATUS_TIME_OUT)) {
                break Loop;
            }
        }

        if (StringUtils.isNotBlank(cResult) && StringUtils.contains(cResult, Constants.RESULT_STATUS_TIME_OUT)) {
            LOGGER.error("ERROR - SSH CONNECTION TIME OUT");
            clusterLogService.saveClusterLog(clusterId, mpSeq++, TerramanConstant.TERRAFORM_SSH_CONNECTION_TIME_OUT);
            clusterService.updateCluster(clusterId, TerramanConstant.CLUSTER_FAIL_STATUS);
            return (ResultStatusModel) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_FAIL);
        }
        LOGGER.info("ssh connection :: " + cResult);

        try {
            Thread.sleep(10000);
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
        LOGGER.info("8. current directory :: " + commandService.execCommandOutput(TerramanConstant.DIRECTORY_COMMAND, TerramanConstant.MOVE_DIR_KUBESPRAY, host, idRsa));
        List<InstanceModel> instanceList = instanceService.getInstances(clusterId, provider, host, idRsa, processGb);
        if(instanceList.size() > 0) {
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
                            + "export MASTER_NODE_PRIVATE_IP=" + obj.getPrivateIp(); // kubespray를 배포하기 위해서 publicIp로 대체
//                            + "export MASTER_NODE_PRIVATE_IP=" + (StringUtils.equals(provider.toUpperCase(), Constants.UPPER_OPENSTACK) ? obj.getPublicIp() : obj.getPrivateIp()); // kubespray를 배포하기 위해서 publicIp로 대체

                }
                sb.append(line);
            }

            sb.append("\\n\\n" + "export WORKER_NODE_CNT=" + workerCnt + "\\n");

            for(InstanceModel obj : instanceList) {
                String line = "";
                if( !obj.getResourceName().contains("master") ) {
                    line = "\\n"
                            + "export WORKER" + workerSeq
                            + "_NODE_HOSTNAME=" + obj.getInstanceName()
                            + "\\n"
                            + "export WORKER" + workerSeq
                            + "_NODE_PUBLIC_IP=" + obj.getPublicIp() // kubespray를 배포하기 위해서 publicIp로 대체
                            + "\\n"
                            + "export WORKER" + workerSeq
                            + "_NODE_PRIVATE_IP=" + obj.getPrivateIp(); // kubespray를 배포하기 위해서 publicIp로 대체
//                            + "_NODE_PRIVATE_IP=" + (StringUtils.equals(provider.toUpperCase(), Constants.UPPER_OPENSTACK) ? obj.getPublicIp() : obj.getPrivateIp()); // kubespray를 배포하기 위해서 publicIp로 대체
                    workerSeq++;
                }
                sb.append(line);
            }

            cResult = commandService.execCommandOutput(TerramanConstant.CLUSTER_KUBESPRAY_SH_FILE_COMMAND(sb.toString()), "", host, idRsa);
            if(StringUtils.equals(Constants.RESULT_STATUS_FAIL, cResult)) {
                LOGGER.error("Kubespray 파일 생성 중 오류가 발생하였습니다. " + cResult);
                clusterLogService.saveClusterLog(clusterId, mpSeq++, TerramanConstant.TERRAFORM_CREATE_CLUSTER_FILE_ERROR);
                clusterService.updateCluster(clusterId, TerramanConstant.CLUSTER_FAIL_STATUS);
                return (ResultStatusModel) commonService.setResultModel(resultStatus, cResult);
            }
        } else {
            LOGGER.error("Instances are not exists");
            clusterLogService.saveClusterLog(clusterId, mpSeq++, TerramanConstant.TERRAFORM_NOT_EXISTS_INSTANCES_ERROR);
            clusterService.updateCluster(clusterId, TerramanConstant.CLUSTER_FAIL_STATUS);
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
        LOGGER.info("9. current directory :: " + commandService.execCommandOutput(TerramanConstant.DIRECTORY_COMMAND, TerramanConstant.MOVE_DIR_KUBESPRAY, host, idRsa));

        cResult = commandService.execCommandOutput(TerramanConstant.KUBESPRAY_CHMOD_COMMAND, TerramanConstant.MOVE_DIR_KUBESPRAY, host, idRsa);
        if(StringUtils.equals(Constants.RESULT_STATUS_FAIL, cResult)) {
            LOGGER.error("Kubespray 모드 변경 중 오류가 발생하였습니다. " + cResult);
            clusterLogService.saveClusterLog(clusterId, mpSeq++, TerramanConstant.TERRAFORM_CHANGE_MODE_ERROR);
            clusterService.updateCluster(clusterId, TerramanConstant.CLUSTER_FAIL_STATUS);
            return (ResultStatusModel) commonService.setResultModel(resultStatus, cResult);
        }

        cResult = commandService.execCommandOutput(TerramanConstant.CLUSTER_KUBESPRAY_DEPLOY_COMMAND, TerramanConstant.MOVE_DIR_KUBESPRAY, host, idRsa);
        if(StringUtils.equals(Constants.RESULT_STATUS_FAIL, cResult)) {
            LOGGER.error("Kubespray 실행 중 오류가 발생하였습니다. " + cResult);
            clusterLogService.saveClusterLog(clusterId, mpSeq++, TerramanConstant.TERRAFORM_DEPLOY_CLUSTER_ERROR);
            clusterService.updateCluster(clusterId, TerramanConstant.CLUSTER_FAIL_STATUS);
            return (ResultStatusModel) commonService.setResultModel(resultStatus, cResult);
        }

        LOGGER.info("클러스터 배포가 완료되었습니다.");
//        LOGGER.info("클러스터 배포가 완료되었습니다. " + cResult);
        // log 저장
        clusterLogService.saveClusterLog(clusterId, mpSeq++, TerramanConstant.KUBESPRAY_DEPLOY_LOG);
        /*************************************************************************************************************************************/

        /**************************************************************************************************************************************
         * 10. 클러스터 정보 vault 생성
         * clusterId = clusterId
         * clusterApiUrl = https://{ publicIp }:6443
         * clusterToken =
         *  - kubectl create serviceaccount k8sadmin -n kube-system
         *  - kubectl create clusterrolebinding k8sadmin --clusterrole=cluster-admin --serviceaccount=kube-system:k8sadmin
         *  - kubectl describe serviceaccount k8sadmin -n kube-system | grep 'Mountable secrets'      -->     SECRET_NAME 값 추출
         *  - kubectl describe secret {SECRET_NAME} -n kube-system | grep -E '^token' | cut -f2 -d':' | tr -d " "
         * ************************************************************************************************************************************/
        commandService.execCommandOutput(TerramanConstant.SERVICE_ACCOUNT_CREATE
                , ""
                , instanceInfo.getPublicIp()
                , TerramanConstant.CLUSTER_PRIVATE_KEY(clusterId, processGb));
        if(StringUtils.equals(Constants.RESULT_STATUS_FAIL, cResult)) {
            LOGGER.error("Token 생성 중 오류가 발생하였습니다. - serviceAccount 생성 오류" + cResult);
            clusterLogService.saveClusterLog(clusterId, mpSeq++, TerramanConstant.TERRAFORM_CREATE_SERVICE_ACCOUNT_ERROR);
            clusterService.updateCluster(clusterId, TerramanConstant.CLUSTER_FAIL_STATUS);
            return (ResultStatusModel) commonService.setResultModel(resultStatus, cResult);
        }

        commandService.execCommandOutput(TerramanConstant.SERVICE_ACCOUNT_BINDING
                , ""
                , instanceInfo.getPublicIp()
                , TerramanConstant.CLUSTER_PRIVATE_KEY(clusterId, processGb));
        if(StringUtils.equals(Constants.RESULT_STATUS_FAIL, cResult)) {
            LOGGER.error("Token 생성 중 오류가 발생하였습니다. - roleBinding 오류" + cResult);
            clusterLogService.saveClusterLog(clusterId, mpSeq++, TerramanConstant.TERRAFORM_BIND_ROLE_ERROR);
            clusterService.updateCluster(clusterId, TerramanConstant.CLUSTER_FAIL_STATUS);
            return (ResultStatusModel) commonService.setResultModel(resultStatus, cResult);
        }

        cResult = commandService.execCommandOutput(TerramanConstant.SERVICE_ACCOUNT_SECRET_NAME
                , ""
                , instanceInfo.getPublicIp()
                , TerramanConstant.CLUSTER_PRIVATE_KEY(clusterId, processGb));
        if(StringUtils.equals(Constants.RESULT_STATUS_FAIL, cResult) || StringUtils.isBlank(cResult)) {
            LOGGER.error("Token 생성 중 오류가 발생하였습니다. - secretName 값 추출 오류" + cResult);
            clusterLogService.saveClusterLog(clusterId, mpSeq++, TerramanConstant.TERRAFORM_GET_SECRET_NAME_ERROR);
            clusterService.updateCluster(clusterId, TerramanConstant.CLUSTER_FAIL_STATUS);
            return (ResultStatusModel) commonService.setResultModel(resultStatus, cResult);
        }

        cResult = commandService.execCommandOutput(TerramanConstant.SERVICE_ACCOUNT_TOKEN(cResult)
                , ""
                , instanceInfo.getPublicIp()
                , TerramanConstant.CLUSTER_PRIVATE_KEY(clusterId, processGb));
        if(StringUtils.equals(Constants.RESULT_STATUS_FAIL, cResult)) {
            LOGGER.error("Token 생성 중 오류가 발생하였습니다. - Token값 추출 오류" + cResult);
            clusterLogService.saveClusterLog(clusterId, mpSeq++, TerramanConstant.TERRAFORM_GET_CLUSTER_TOKEN_ERROR);
            clusterService.updateCluster(clusterId, TerramanConstant.CLUSTER_FAIL_STATUS);
            return (ResultStatusModel) commonService.setResultModel(resultStatus, cResult);
        }

        Object resultClusterInfo = vaultService.write(
                propertyService.getVaultClusterTokenPath().replace("{id}", clusterId)
                , new ClusterInfo(
                        clusterId
                        , propertyService.getVaultClusterApiUrl().replace("{ip}", instanceInfo.getPublicIp())
                        , ( StringUtils.isNotBlank(cResult) ? cResult.trim() : cResult )
                )
        );

        if(resultClusterInfo == null) {
            LOGGER.error("cluster token 생성 중 오류가 발생하였습니다.");
            clusterLogService.saveClusterLog(clusterId, mpSeq++, TerramanConstant.TERRAFORM_CREATE_TOKEN_ERROR);
            clusterService.updateCluster(clusterId, TerramanConstant.CLUSTER_FAIL_STATUS);
            return (ResultStatusModel) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_FAIL);
        }
        LOGGER.info("cluster token 생성 완료하였습니다.");
        /*************************************************************************************************************************************/

        /**************************************************************************************************************************************
         * 11. 클러스터 생성 상태 전송 --> DB 업데이트
         * ************************************************************************************************************************************/
        ClusterModel updateResult = clusterService.updateCluster(clusterId, TerramanConstant.CLUSTER_COMPLETE_STATUS);
        if(updateResult == null) {
            LOGGER.error("cluster 생성 완료 업데이트 중 오류가 발생하였습니다.");
            clusterLogService.saveClusterLog(clusterId, mpSeq++, TerramanConstant.TERRAFORM_COMPLETE_CLUSTER_ERROR);
            clusterService.updateCluster(clusterId, TerramanConstant.CLUSTER_FAIL_STATUS);
            return (ResultStatusModel) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_FAIL);
        }
        /*************************************************************************************************************************************/

        /**************************************************************************************************************************************
         * 12. 완료 후 프로세스 종료
         * ************************************************************************************************************************************/
        LOGGER.info("cluster 생성이 완료되었습니다.");
        return (ResultStatusModel) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
        /*************************************************************************************************************************************/
    }

    /**
     * Terraman 삭제(Delete Terraman)
     *
     * @param clusterId
     * @return the resultStatus
     */
    public ResultStatusModel deleteTerraman(String clusterId, String processGb) {
        String host = "";
        String idRsa = "";
        ResultStatusModel resultStatus = new ResultStatusModel();

        if(!StringUtils.isBlank(processGb) && StringUtils.equals(processGb.toUpperCase(), "CONTAINER")) {
            host = propertyService.getMASTER_HOST();
            idRsa = TerramanConstant.MASTER_ID_RSA;
        }

        String cResult = Constants.RESULT_STATUS_SUCCESS;
        cResult = commandService.execCommandOutput(TerramanConstant.TERRAFORM_DESTROY_COMMAND, TerramanConstant.MOVE_DIR_CLUSTER(clusterId, processGb), host, idRsa);
        if(StringUtils.equals(Constants.RESULT_STATUS_FAIL, cResult)) {
            LOGGER.error("terraform 삭제 중 오류가 발생하였습니다. " + cResult);
            return (ResultStatusModel) commonService.setResultModel(resultStatus, cResult);
        } else {
            cResult = commandService.execCommandOutput(TerramanConstant.DELETE_CLUSTER(clusterId), TerramanConstant.DELETE_DIR_CLUSTER, host, idRsa);
            if(StringUtils.equals(Constants.RESULT_STATUS_FAIL, cResult)) {
                LOGGER.error("Cluster 삭제 중 오류가 발생하였습니다. " + cResult);
                return (ResultStatusModel) commonService.setResultModel(resultStatus, cResult);
            }
        }

        vaultService.delete(propertyService.getVaultClusterTokenPath().replace("{id}", clusterId));
        // cluster log 삭제
        try {
            clusterLogService.deleteClusterLogByClusterId(clusterId);
        } catch (Exception e) {
            LOGGER.error("cluster log 삭제에 실패하였습니다.");
            clusterService.updateCluster(clusterId, TerramanConstant.CLUSTER_FAIL_STATUS);
            return (ResultStatusModel) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_FAIL);
        }

        return (ResultStatusModel) commonService.setResultModel(new ResultStatusModel(), Constants.RESULT_STATUS_SUCCESS);

    }

}
