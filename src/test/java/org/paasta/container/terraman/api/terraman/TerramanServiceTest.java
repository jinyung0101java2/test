package org.paasta.container.terraman.api.terraman;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.paasta.container.terraman.api.common.CommonService;
import org.paasta.container.terraman.api.common.PropertyService;
import org.paasta.container.terraman.api.common.VaultService;
import org.paasta.container.terraman.api.common.constants.CommonStatusCode;
import org.paasta.container.terraman.api.common.constants.Constants;
import org.paasta.container.terraman.api.common.constants.TerramanConstant;
import org.paasta.container.terraman.api.common.model.AccountModel;
import org.paasta.container.terraman.api.common.model.ClusterModel;
import org.paasta.container.terraman.api.common.model.InstanceModel;
import org.paasta.container.terraman.api.common.model.ResultStatusModel;
import org.paasta.container.terraman.api.common.service.*;
import org.paasta.container.terraman.api.common.util.CommonFileUtils;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class TerramanServiceTest {
    private static final String TEST_DEFAULT_PATH = "secret";
    private static final String TEST_PROVIDER = "openstack";
    private static final String TEST_CLUSTER_ID = "test_cluster";
    private static final String TEST_CLUSTER_NAME = "test_cluster_name";
    private static final String TEST_SEQ = "13";
    private static final int TEST_INT_SEQ = 13;

    private static final String TEST_DIR = "/home";
    private static final String TEST_FILE_PATH = "/file";

    private static final String TEST_PATH = "secret/OPENSTACK/1";
    private static final String TEST_STR = "terraman";
    private static final String TEST_RESULT_STR = "SUCCESS";
    private static final String TEST_RESULT_CODE = "200";

    private static final String TEST_RESOURCE_NAME = "resourceName";
    private static final String TEST_INSTANCE_NAME = "instanceName";
    private static final String TEST_PRIVATE_IP = "privateIp";
    private static final String TEST_PUBLIC_IP = "publicIp";

    private static final String TEST_FILE_NAME = "kubespray_var.sh";
    private static final String TEST_FILE_DATA = "{test : test}";

    private static final String TEST_HOST = "1.1.1.1";
    private static final String TEST_IDRSA = "id_rsa";

    private static final String TEST_CLUSTER_TOKEN_PATH = "123123";
    private static final String TEST_CLUSTER_API_URL = "https://1.1.1.1:6443";
    private static final String TEST_VAULT_BASE = "/";

    private static final String TEST_STRING = "test";
    private static final String TEST_ID_RSA_PATH = "/root/.ssh/id_rsa";

    private static final String TEST_PROCESS_GB = "CONTAINER";
    private static final String TEST_PROCESS_GB_CONTAINER = "Container";

    private static final int TEST_MP_SEQ = 1;

    private static TerramanRequest gParams = null;
    private static TerramanRequest gParams2 = null;
    private static ResultStatusModel gResultModel = null;
    private static ResultStatusModel gResultStatusModelModel = null;
    private static InstanceModel gInstanceModel = null;
    private static List<InstanceModel> gInstanceList = null;
    private static ClusterModel clusterModel = null;
    private static ClusterModel clusterModelMock = null;
    private static HashMap hashMap = null;
    private static AccountModel accountModel = null;
    private File uploadFile = null;



    @Mock
    private CommonService commonService;
    @Mock
    private CommandService commandService;
    @Mock
    private ClusterLogService clusterLogService;
    @Mock
    private InstanceService instanceService;
    @Mock
    private CommonFileUtils commonFileUtils;
    @Mock
    private ClusterService clusterService;
    @Mock
    private PropertyService propertyService;
    @Mock
    private VaultService vaultService;
    @Mock
    private AccountService accountService;
    @Mock
    private TfFileService tfFileService;
    @Mock
    private TerramanProcessService terramanProcessService;

    @InjectMocks
    private TerramanService terramanService;

    @Before
    public void setUp() {
        gResultStatusModelModel = new ResultStatusModel();
        gResultStatusModelModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gResultStatusModelModel.setResultMessage(Constants.RESULT_STATUS_SUCCESS);
        gResultStatusModelModel.setHttpStatusCode(CommonStatusCode.OK.getCode());
        gResultStatusModelModel.setDetailMessage(CommonStatusCode.OK.getMsg());

        gResultModel = new ResultStatusModel();

        gParams = new TerramanRequest();
        gParams.setProvider(TEST_PROVIDER);
        gParams.setClusterId(TEST_CLUSTER_ID);
        gParams.setSeq(TEST_SEQ);

        gParams2 = new TerramanRequest();
        gParams2.setSeq(TEST_SEQ);

        gInstanceModel = new InstanceModel(TEST_RESOURCE_NAME, TEST_INSTANCE_NAME, TEST_PRIVATE_IP, TEST_PUBLIC_IP);
        gInstanceList = new ArrayList<>();
        gInstanceList.add(gInstanceModel);

        clusterModel = new ClusterModel();
        accountModel = new AccountModel(1,"2","3","4","5","6","7");
        hashMap = new HashMap();
        hashMap.put("test", "test");
        uploadFile = new File(TEST_FILE_PATH);

        clusterModelMock = new ClusterModel();
        clusterModelMock.setClusterId(TEST_CLUSTER_ID);
        clusterModelMock.setName(TEST_CLUSTER_NAME);
    }


    /**
     * Terraman 생성(Create Terraman) Test
     */
    @Test
    public void createTerramanTest() throws Exception {
        // when
        when(clusterService.getCluster(TEST_CLUSTER_ID)).thenReturn(clusterModelMock);
        doNothing().when(clusterLogService).saveClusterLog(TEST_CLUSTER_ID, TEST_INT_SEQ, TerramanConstant.TERRAFORM_START_LOG(TEST_PROVIDER));
        doReturn(clusterModel).when(clusterService).updateCluster(TEST_CLUSTER_ID, TerramanConstant.CLUSTER_FAIL_STATUS);
        when(propertyService.getMasterHost()).thenReturn(TEST_HOST);
        when(commandService.execCommandOutput(TerramanConstant.TERRAFORM_DESTROY_COMMAND, TerramanConstant.MOVE_DIR_CLUSTER(TEST_CLUSTER_ID, TEST_PROCESS_GB), TEST_HOST, TEST_IDRSA)).thenReturn(TEST_RESULT_CODE);
        when(terramanProcessService.terramanProcessSet(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_DIR)).thenReturn(TEST_MP_SEQ);
        when(terramanProcessService.terramanProcessStart(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_PROVIDER, TEST_PROCESS_GB, TEST_HOST, TEST_IDRSA)).thenReturn(TEST_MP_SEQ);
        when(terramanProcessService.terramanProcessSetTfFile(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_PROCESS_GB, TEST_HOST, TEST_IDRSA, TEST_PROVIDER, TEST_INT_SEQ)).thenReturn(TEST_MP_SEQ);
        when(terramanProcessService.terramanProcessInit(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_PROCESS_GB, TEST_HOST, TEST_IDRSA)).thenReturn(TEST_MP_SEQ);
        when(terramanProcessService.terramanProcessPlan(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_PROCESS_GB, TEST_HOST, TEST_IDRSA)).thenReturn(TEST_MP_SEQ);
        when(terramanProcessService.terramanProcessApply(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_PROCESS_GB, TEST_HOST, TEST_IDRSA)).thenReturn(TEST_MP_SEQ);
        when(terramanProcessService.terramanProcessGetInstanceIp(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_PROCESS_GB, TEST_HOST, TEST_IDRSA, TEST_PROVIDER, TEST_CLUSTER_NAME)).thenReturn(TEST_MP_SEQ);
        when(terramanProcessService.terramanProcessSetKubespray(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_PROCESS_GB, TEST_HOST, TEST_IDRSA, TEST_PROVIDER, TEST_CLUSTER_NAME)).thenReturn(TEST_MP_SEQ);
        when(terramanProcessService.terramanProcessExecKubespray(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_HOST, TEST_IDRSA)).thenReturn(TEST_MP_SEQ);
        when(terramanProcessService.terramanProcessCreateVault(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_PROCESS_GB, TEST_HOST, TEST_IDRSA, TEST_PROVIDER, TEST_CLUSTER_NAME)).thenReturn(TEST_MP_SEQ);
        when(terramanProcessService.terramanProcessClusterStatusUpdate(TEST_MP_SEQ, TEST_CLUSTER_ID)).thenReturn(TEST_MP_SEQ);

        terramanService.createTerraman(gParams, TEST_PROCESS_GB);

        // then
        //assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

    /**
     * Terraman 생성(Create Terraman) Test
     */
    @Test
    public void createTerramanTestFailed() throws Exception {
        // when
        doReturn(clusterModel).when(clusterService).updateCluster(TEST_CLUSTER_ID, TerramanConstant.CLUSTER_CREATE_STATUS);
        when(propertyService.getMasterHost()).thenReturn(TEST_HOST);
        when(commandService.execCommandOutput(TerramanConstant.CREATE_DIR_CLUSTER(TEST_CLUSTER_ID), "", TEST_HOST, TEST_IDRSA)).thenReturn(Constants.RESULT_STATUS_FAIL);
        doReturn(clusterModel).when(clusterService).updateCluster(TEST_CLUSTER_ID, TerramanConstant.CLUSTER_FAIL_STATUS);

        terramanService.createTerraman(gParams, TEST_PROCESS_GB);

        // then
        //assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

    /**
     * Terraman 생성(Create Terraman) Test
     */
    @Test
    public void createTerramanTestClusterIdIsNull() throws Exception {
        // when
        doNothing().when(clusterLogService).saveClusterLog(null, TEST_INT_SEQ, TerramanConstant.TERRAFORM_START_LOG(TEST_PROVIDER));
        doReturn(clusterModel).when(clusterService).updateCluster(null, TerramanConstant.CLUSTER_FAIL_STATUS);
        when(propertyService.getMasterHost()).thenReturn(TEST_HOST);
        when(commandService.execCommandOutput(TerramanConstant.TERRAFORM_DESTROY_COMMAND, TerramanConstant.MOVE_DIR_CLUSTER(null, TEST_PROCESS_GB), TEST_HOST, TEST_IDRSA)).thenReturn(TEST_RESULT_CODE);

        terramanService.createTerraman(gParams2, TEST_PROCESS_GB);

        // then
        //assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

    /**
     * Terraman 생성(Create Terraman) Test
     */
    @Test
    public void createTerramanTestProviderIsNull() throws Exception {
        // when
        doNothing().when(clusterLogService).saveClusterLog(TEST_CLUSTER_ID, TEST_INT_SEQ, TerramanConstant.TERRAFORM_START_LOG(null));
        doReturn(clusterModel).when(clusterService).updateCluster(TEST_CLUSTER_ID, TerramanConstant.CLUSTER_FAIL_STATUS);
        when(propertyService.getMasterHost()).thenReturn(TEST_HOST);
        when(commandService.execCommandOutput(TerramanConstant.TERRAFORM_DESTROY_COMMAND, TerramanConstant.MOVE_DIR_CLUSTER(TEST_CLUSTER_ID, TEST_PROCESS_GB), TEST_HOST, TEST_IDRSA)).thenReturn(TEST_RESULT_CODE);

        terramanService.createTerraman(gParams2, TEST_PROCESS_GB);

        // then
        //assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

    /**
     * Terraman 생성(Create Terraman) Test
     */
    @Test
    public void createTerramanTestClusterIdIsNullProviderIsNull() throws Exception {
        // when
        doNothing().when(clusterLogService).saveClusterLog(null, TEST_INT_SEQ, TerramanConstant.TERRAFORM_START_LOG(null));
        doReturn(clusterModel).when(clusterService).updateCluster(null, TerramanConstant.CLUSTER_FAIL_STATUS);
        when(propertyService.getMasterHost()).thenReturn(TEST_HOST);
        when(commandService.execCommandOutput(TerramanConstant.TERRAFORM_DESTROY_COMMAND, TerramanConstant.MOVE_DIR_CLUSTER(null, TEST_PROCESS_GB), TEST_HOST, TEST_IDRSA)).thenReturn(TEST_RESULT_CODE);

        terramanService.createTerraman(gParams2, TEST_PROCESS_GB);

        // then
        //assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

    /**
     * Terraman 생성(Create Terraman) Test
     */
    @Test
    public void createTerramanTestIsContainer() throws Exception {
        // when
        doNothing().when(clusterLogService).saveClusterLog(TEST_CLUSTER_ID, TEST_INT_SEQ, TerramanConstant.TERRAFORM_START_LOG(TEST_PROVIDER));
        doReturn(clusterModel).when(clusterService).updateCluster(TEST_CLUSTER_ID, TerramanConstant.CLUSTER_FAIL_STATUS);
        when(propertyService.getMasterHost()).thenReturn(TEST_HOST);
        when(commandService.execCommandOutput(TerramanConstant.CREATE_DIR_CLUSTER(TEST_CLUSTER_ID), "", TEST_HOST, TEST_IDRSA)).thenReturn(Constants.RESULT_STATUS_FAIL);
        doReturn(clusterModel).when(clusterService).updateCluster(TEST_CLUSTER_ID, TerramanConstant.CLUSTER_FAIL_STATUS);

        terramanService.createTerraman(gParams, TEST_PROCESS_GB_CONTAINER);

        // then
        //assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

    /**
     * Terraman 생성(Create Terraman) Test
     */
    @Test
    public void createTerramanTestIsContainerDir() throws Exception {
        // when
        doNothing().when(clusterLogService).saveClusterLog(TEST_CLUSTER_ID, TEST_INT_SEQ, TerramanConstant.TERRAFORM_START_LOG(TEST_PROVIDER));
        doReturn(clusterModel).when(clusterService).updateCluster(TEST_CLUSTER_ID, TerramanConstant.CLUSTER_FAIL_STATUS);
        when(propertyService.getMasterHost()).thenReturn(TEST_HOST);
        when(commandService.execCommandOutput(TerramanConstant.CREATE_DIR_CLUSTER(TEST_CLUSTER_ID), "", TEST_HOST, TEST_IDRSA)).thenReturn(Constants.RESULT_STATUS_SUCCESS);


        terramanService.createTerraman(gParams, TEST_PROCESS_GB_CONTAINER);

        // then
        //assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

    /**
     * Terraman 삭제(Create Terraman) Test
     */
//    @Test
//    public void deleteTerramanTest() throws Exception {
//        // when
//        when(commandService.execCommandOutput(TerramanConstant.TERRAFORM_DESTROY_COMMAND, TerramanConstant.MOVE_DIR_CLUSTER(TEST_CLUSTER_ID, TEST_PROCESS_GB), TEST_HOST, TEST_IDRSA)).thenReturn(TEST_RESULT_CODE);
//        when(commandService.execCommandOutput(TerramanConstant.DELETE_CLUSTER(TEST_CLUSTER_ID), TerramanConstant.DELETE_DIR_CLUSTER, TEST_HOST, TEST_IDRSA)).thenReturn(TEST_RESULT_CODE);
//        when(commonService.setResultModel(gResultModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gResultStatusModelModel);
//        when(propertyService.getVaultClusterTokenPath()).thenReturn(TEST_CLUSTER_TOKEN_PATH);
//        doNothing().when(vaultService).delete(TEST_PATH);
//        doNothing().when(clusterLogService).deleteClusterLogByClusterId(TEST_CLUSTER_ID);
//
//        ResultStatusModel result = terramanService.deleteTerraman(TEST_CLUSTER_ID, TEST_PROCESS_GB);
//
//        // then
//        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
//    }


}
