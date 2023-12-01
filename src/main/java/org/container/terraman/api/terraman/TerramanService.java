package org.container.terraman.api.terraman;

import org.apache.commons.lang3.StringUtils;
import org.container.terraman.api.common.PropertyService;
import org.container.terraman.api.common.constants.Constants;
import org.container.terraman.api.common.constants.TerramanConstant;
import org.container.terraman.api.common.model.ClusterModel;
import org.container.terraman.api.common.model.TerramanCommandModel;
import org.container.terraman.api.common.service.*;
import org.container.terraman.api.common.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class TerramanService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TerramanService.class);

    private final ClusterLogService clusterLogService;
    private final CommandService commandService;
    private final ClusterService clusterService;
    private final PropertyService propertyService;
    private final TerramanProcessService terramanProcessService;

    @Autowired
    public TerramanService(
            ClusterLogService clusterLogService
            , CommandService commandService
            , ClusterService clusterService
            , PropertyService propertyService
            , TerramanProcessService terramanProcessService
    ) {
        this.clusterLogService = clusterLogService;
        this.commandService = commandService;
        this.clusterService = clusterService;
        this.propertyService = propertyService;
        this.terramanProcessService = terramanProcessService;
    }

    /**
     * Terraman 생성(Create Terraman)
     *
     * @param terramanRequest the init terramanRequest
     * @param processGb the processGb
     * @return the resultStatus
     */
    @Async
    public void createTerraman(TerramanRequest terramanRequest, String processGb) throws UnsupportedEncodingException, InterruptedException {
        /**************************************************************************************************************************************
         * 변수 정의
         * ************************************************************************************************************************************/
        String clusterId = terramanRequest.getClusterId();
        int seq = StringUtils.isBlank(String.valueOf(terramanRequest.getSeq())) ? 0 : Integer.parseInt(terramanRequest.getSeq());
        String provider = terramanRequest.getProvider();

        if (provider.equalsIgnoreCase(Constants.UPPER_NAVER)) {
            provider = TerramanConstant.NCLOUD_USER_NAME;
        }

        String clusterName = "";

        String cResult = "";
        int mpSeq = 0;

        String host = "";
        String idRsa = "";
        String hostDir = "/home/ubuntu";

        ClusterModel clusterModel = clusterService.getCluster(clusterId);

        // keys change mod
        TerramanCommandModel terramanCommandModelForKey = new TerramanCommandModel();
        terramanCommandModelForKey.setCommand("16");
        commandService.execCommandOutput(terramanCommandModelForKey);


        if(StringUtils.isBlank(clusterId) || StringUtils.isBlank(provider)) {
            clusterService.updateCluster(clusterId, TerramanConstant.CLUSTER_FAIL_STATUS);
            mpSeq += 1;
            clusterLogService.saveClusterLog(clusterId, mpSeq, TerramanConstant.TERRAFORM_CREATE_CLUSTER_PARAMETER_ERROR);
            mpSeq = -1;
        }

        if(clusterModel != null && StringUtils.isNotBlank(clusterModel.getName())) {
            clusterName = clusterModel.getName();
        }

        // 생성중 status 변경
        clusterService.updateCluster(clusterId, TerramanConstant.CLUSTER_CREATE_STATUS);
        if(StringUtils.isNotBlank(processGb) && StringUtils.equals(processGb.toUpperCase(), "CONTAINER")) {
            TerramanCommandModel terramanCommandModel = new TerramanCommandModel();
            host = propertyService.getMasterHost();
            idRsa = TerramanConstant.MASTER_ID_RSA;
            LOGGER.info("host :: {}", CommonUtils.loggerReplace(host));
            LOGGER.info("idRsa :: {}", CommonUtils.loggerReplace(idRsa));
            terramanCommandModel.setCommand("15");
            terramanCommandModel.setHost(host);
            terramanCommandModel.setIdRsa(idRsa);
            terramanCommandModel.setUserName(TerramanConstant.DEFAULT_USER_NAME);
            terramanCommandModel.setClusterId(clusterId);

            cResult = commandService.execCommandOutput(terramanCommandModel);
            if(StringUtils.equals(Constants.RESULT_STATUS_FAIL, cResult)) {
                clusterService.updateCluster(clusterId, TerramanConstant.CLUSTER_FAIL_STATUS);
                mpSeq = -1;
            }
            hostDir = "";
        }

        /**************************************************************************************************************************************
         * 0. terraman process setting
         * ***********************************************************************************************************************************/
        if(mpSeq > -1) {
            mpSeq = terramanProcessService.terramanProcessSet(mpSeq, clusterId, hostDir);
        }

        /**************************************************************************************************************************************
         * 1. terraman process start
         * ***********************************************************************************************************************************/
        if(mpSeq > -1) {
            mpSeq = terramanProcessService.terramanProcessStart(mpSeq, clusterId, provider, processGb, host, idRsa);
        }

        /**************************************************************************************************************************************
         * 2. IaaS에 따라 provider.tf 파일 정의 (Vault, Database)
         * ************************************************************************************************************************************/
        if(mpSeq > -1) {
            mpSeq = terramanProcessService.terramanProcessSetTfFile(mpSeq, clusterId, processGb, host, idRsa, provider, seq);
        }

        /**************************************************************************************************************************************
         * 3. tofu init 실행
         * ************************************************************************************************************************************/
        if(mpSeq > -1) {
            mpSeq = terramanProcessService.terramanProcessInit(mpSeq, clusterId, processGb, host, idRsa);
        }

        /**************************************************************************************************************************************
         * 4. tofu plan 실행
         * ************************************************************************************************************************************/
        if(mpSeq > -1) {
            mpSeq = terramanProcessService.terramanProcessPlan(mpSeq, clusterId, processGb, host, idRsa);
        }

        /**************************************************************************************************************************************
         * 5. tofu apply 실행
         * ************************************************************************************************************************************/
        if(mpSeq > -1) {
            mpSeq = terramanProcessService.terramanProcessApply(mpSeq, clusterId, processGb, host, idRsa);
        }

        /**************************************************************************************************************************************
         * 6. Infra 생성 후 생성된 Instance IP 알아오기
         * ************************************************************************************************************************************/
        if(mpSeq > -1) {
            mpSeq = terramanProcessService.terramanProcessGetInstanceIp(mpSeq, clusterId, processGb, host, idRsa, provider, clusterName, seq);
        }

        /**************************************************************************************************************************************
         * 7. Kubespray 다운로드 및 kubespray_var.sh 파일 작성하기
         * ************************************************************************************************************************************/
        if(mpSeq > -1) {
            mpSeq = terramanProcessService.terramanProcessSetKubespray(mpSeq, clusterId, processGb, host, idRsa, provider, clusterName);
        }

        /**************************************************************************************************************************************
         * 8. source deploy_kubespray.sh 실행하기
         * ************************************************************************************************************************************/
        if(mpSeq > -1) {
            mpSeq = terramanProcessService.terramanProcessExecKubespray(mpSeq, clusterId, host, idRsa);
        }

        /**************************************************************************************************************************************
         * 9. 클러스터 정보 vault 생성
         * ************************************************************************************************************************************/
        if(mpSeq > -1) {
            mpSeq = terramanProcessService.terramanProcessCreateVault(mpSeq, clusterId, processGb, host, idRsa, provider, clusterName);
        }

        /**************************************************************************************************************************************
         * 10. 클러스터 생성 상태 전송 --> DB 업데이트
         * ************************************************************************************************************************************/
        if(mpSeq > -1) {
            mpSeq = terramanProcessService.terramanProcessClusterStatusUpdate(mpSeq, clusterId);
        }
        /*************************************************************************************************************************************/

        /**************************************************************************************************************************************
         * 11. 완료 후 프로세스 종료
         * ************************************************************************************************************************************/
        if(mpSeq > -1) {
            LOGGER.info("클러스터 배포가 완료되었습니다.");
            // log 저장
            clusterLogService.saveClusterLog(clusterId, mpSeq, TerramanConstant.KUBESPRAY_DEPLOY_LOG);
        }

        /*************************************************************************************************************************************/
    }
}
