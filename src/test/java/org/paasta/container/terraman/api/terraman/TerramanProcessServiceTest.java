package org.paasta.container.terraman.api.terraman;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.paasta.container.terraman.api.common.CommonService;
import org.paasta.container.terraman.api.common.PropertyService;
import org.paasta.container.terraman.api.common.VaultService;
import org.paasta.container.terraman.api.common.constants.Constants;
import org.paasta.container.terraman.api.common.constants.TerramanConstant;
import org.paasta.container.terraman.api.common.model.ClusterInfo;
import org.paasta.container.terraman.api.common.model.ClusterModel;
import org.paasta.container.terraman.api.common.model.InstanceModel;
import org.paasta.container.terraman.api.common.service.*;
import org.paasta.container.terraman.api.common.util.CommonFileUtils;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class TerramanProcessServiceTest {

    private static final String TEST_CLUSTER_ID = "test_cluster";
    private static final int TEST_INT_SEQ = 13;
    private static final String TEST_PROCESS_GB = "test";
    private static final String TEST_HOST = "1.1.1.1";
    private static final String TEST_IDRSA = "id_rsa";
    private static final String TEST_PROVIDER = "test";
    private static final String TEST_RESULT_CODE = "200";
    private static final String TEST_DIR = "/home";

    private static final int TEST_MP_SEQ = 1;

    private static final String TEST_STRING = "test";
    private static final String TEST_ID_RSA_PATH = "/root/.ssh/id_rsa";

    private static final String TEST_RESOURCE_NAME = "resourceName";
    private static final String TEST_INSTANCE_NAME = "instanceName";
    private static final String TEST_PRIVATE_IP = "privateIp";
    private static final String TEST_PUBLIC_IP = "publicIp";

    private static final String TEST_CLUSTER_API_URL = "apiUrl";
    private static final String TEST_CLUSTER_TOKEN = "token";

    private static InstanceModel gInstanceModel = null;
    private static List<InstanceModel> gInstanceList = null;
    private static ClusterModel clusterModelMock = null;
    private static Object objectMock = null;
    private static ClusterInfo clusterInfoMock = null;

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

    @InjectMocks
    private TerramanProcessService terramanProcessService;

    @Before
    public void setUp() {
        gInstanceModel = new InstanceModel(TEST_RESOURCE_NAME, TEST_INSTANCE_NAME, TEST_PRIVATE_IP, TEST_PUBLIC_IP);
        gInstanceList = new ArrayList<>();
        gInstanceList.add(gInstanceModel);

        clusterModelMock = new ClusterModel();
        clusterModelMock.setClusterId(TEST_CLUSTER_ID);
        clusterModelMock.setProviderType(TEST_PROVIDER);

        objectMock = new Object();

        clusterInfoMock = new ClusterInfo(TEST_CLUSTER_ID, TEST_CLUSTER_API_URL, TEST_CLUSTER_TOKEN);
    }

    @Test
    public void terramanProcessSetTest() {
        doNothing().when(clusterLogService).deleteClusterLogByClusterId(TEST_CLUSTER_ID);
        when(commandService.execCommandOutput(TerramanConstant.CREATE_DIR_CLUSTER(TEST_CLUSTER_ID), TEST_DIR, "", "")).thenReturn(Constants.RESULT_STATUS_SUCCESS);

        int result = terramanProcessService.terramanProcessSet(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_DIR);

        assertEquals(1, result);
    }

    @Test
    public void terramanProcessSetFailTest() {
        doNothing().when(clusterLogService).deleteClusterLogByClusterId(TEST_CLUSTER_ID);
        when(commandService.execCommandOutput(TerramanConstant.CREATE_DIR_CLUSTER(TEST_CLUSTER_ID), TEST_DIR, "", "")).thenReturn(Constants.RESULT_STATUS_FAIL);

        doReturn(clusterModelMock).when(clusterService).updateCluster(TEST_CLUSTER_ID, TerramanConstant.CLUSTER_FAIL_STATUS);
        doNothing().when(clusterLogService).saveClusterLog(TEST_CLUSTER_ID, TEST_INT_SEQ, TerramanConstant.TERRAFORM_CREATE_CLUSTER_DIRECTORY_ERROR);

        int result = terramanProcessService.terramanProcessSet(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_DIR);

        assertEquals(-1, result);
    }

    @Test
    public void terramanProcessStartTest() {
        when(commandService.execCommandOutput(TerramanConstant.DIRECTORY_COMMAND, TerramanConstant.MOVE_DIR_CLUSTER(TEST_CLUSTER_ID, TEST_PROCESS_GB), TEST_HOST, TEST_IDRSA)).thenReturn(TEST_RESULT_CODE);
        doNothing().when(clusterLogService).saveClusterLog(TEST_CLUSTER_ID, TEST_INT_SEQ, TerramanConstant.TERRAFORM_START_LOG(TEST_PROVIDER));
        doNothing().when(clusterLogService).saveClusterLog(TEST_CLUSTER_ID, TEST_INT_SEQ, TerramanConstant.TERRAFORM_IAC_LOG);

        int result = terramanProcessService.terramanProcessStart(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_PROVIDER, TEST_PROCESS_GB, TEST_HOST, TEST_IDRSA);

        assertEquals(3, result);
    }

    @Test
    public void terramanProcessSetTfFileTest() {
        TfFileService tfFileServiceMock = mock(TfFileService.class);
        when(commandService.execCommandOutput(TerramanConstant.TERRAFORM_DESTROY_COMMAND, TerramanConstant.MOVE_DIR_CLUSTER(TEST_CLUSTER_ID, TEST_PROCESS_GB), TEST_HOST, TEST_IDRSA)).thenReturn(TEST_RESULT_CODE);
        when(tfFileServiceMock.createProviderFile(TEST_CLUSTER_ID, TEST_PROVIDER, TEST_INT_SEQ, TEST_STRING, TEST_HOST, TEST_ID_RSA_PATH, TEST_PROCESS_GB)).thenReturn(TEST_RESULT_CODE);
        doNothing().when(clusterLogService).saveClusterLog(TEST_CLUSTER_ID, TEST_INT_SEQ, TerramanConstant.TERRAFORM_TF_ERROR_LOG);
        doReturn(clusterModelMock).when(clusterService).updateCluster(TEST_CLUSTER_ID, TerramanConstant.CLUSTER_FAIL_STATUS);
        doNothing().when(clusterLogService).saveClusterLog(TEST_CLUSTER_ID, TEST_INT_SEQ, TerramanConstant.TERRAFORM_TF_LOG);

        int result = terramanProcessService.terramanProcessSetTfFile(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_PROCESS_GB, TEST_HOST, TEST_IDRSA, TEST_PROVIDER, TEST_INT_SEQ);

        assertEquals(-1, result);
    }

    @Test
    public void terramanProcessSetTfFileFailTest() {
        when(commandService.execCommandOutput(TerramanConstant.TERRAFORM_DESTROY_COMMAND, TerramanConstant.MOVE_DIR_CLUSTER(TEST_CLUSTER_ID, TEST_PROCESS_GB), TEST_HOST, TEST_IDRSA)).thenReturn(TEST_RESULT_CODE);
        when(tfFileService.createProviderFile(TEST_CLUSTER_ID, TEST_PROVIDER, TEST_INT_SEQ, TEST_STRING, TEST_HOST, TEST_ID_RSA_PATH, TEST_PROCESS_GB)).thenReturn(Constants.RESULT_STATUS_FAIL);
        doNothing().when(clusterLogService).saveClusterLog(TEST_CLUSTER_ID, TEST_INT_SEQ, TerramanConstant.TERRAFORM_TF_ERROR_LOG);
        doReturn(clusterModelMock).when(clusterService).updateCluster(TEST_CLUSTER_ID, TerramanConstant.CLUSTER_FAIL_STATUS);

        int result = terramanProcessService.terramanProcessSetTfFile(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_PROCESS_GB, TEST_HOST, TEST_IDRSA, TEST_PROVIDER, TEST_INT_SEQ);

        assertEquals(-1, result);
    }

    @Test
    public void terramanProcessInitTest() {
        when(commandService.execCommandOutput(TerramanConstant.DIRECTORY_COMMAND, TerramanConstant.MOVE_DIR_CLUSTER(TEST_CLUSTER_ID, TEST_PROCESS_GB), TEST_HOST, TEST_IDRSA)).thenReturn(TEST_STRING);
        when(commandService.execCommandOutput(TerramanConstant.TERRAFORM_INIT_COMMAND, TerramanConstant.MOVE_DIR_CLUSTER(TEST_CLUSTER_ID, TEST_PROCESS_GB), TEST_HOST, TEST_IDRSA)).thenReturn(Constants.RESULT_STATUS_SUCCESS);

        int result = terramanProcessService.terramanProcessInit(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_PROCESS_GB, TEST_HOST, TEST_IDRSA);

        assertEquals(2, result);
    }

    @Test
    public void terramanProcessInitFailTest() {
        when(commandService.execCommandOutput(TerramanConstant.DIRECTORY_COMMAND, TerramanConstant.MOVE_DIR_CLUSTER(TEST_CLUSTER_ID, TEST_PROCESS_GB), TEST_HOST, TEST_IDRSA)).thenReturn(TEST_STRING);
        when(commandService.execCommandOutput(TerramanConstant.TERRAFORM_INIT_COMMAND, TerramanConstant.MOVE_DIR_CLUSTER(TEST_CLUSTER_ID, TEST_PROCESS_GB), TEST_HOST, TEST_IDRSA)).thenReturn(Constants.RESULT_STATUS_FAIL);
        doNothing().when(clusterLogService).saveClusterLog(TEST_CLUSTER_ID, TEST_INT_SEQ, TerramanConstant.TERRAFORM_INIT_FAIL_LOG);
        doReturn(clusterModelMock).when(clusterService).updateCluster(TEST_CLUSTER_ID, TerramanConstant.CLUSTER_FAIL_STATUS);

        int result = terramanProcessService.terramanProcessInit(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_PROCESS_GB, TEST_HOST, TEST_IDRSA);

        assertEquals(-1, result);
    }

    @Test
    public void terramanProcessPlanTest() {
        when(commandService.execCommandOutput(TerramanConstant.DIRECTORY_COMMAND, TerramanConstant.MOVE_DIR_CLUSTER(TEST_CLUSTER_ID, TEST_PROCESS_GB), TEST_HOST, TEST_IDRSA)).thenReturn(TEST_STRING);
        when(commandService.execCommandOutput(TerramanConstant.TERRAFORM_PLAN_COMMAND, TerramanConstant.MOVE_DIR_CLUSTER(TEST_CLUSTER_ID, TEST_PROCESS_GB), TEST_HOST, TEST_IDRSA)).thenReturn(Constants.RESULT_STATUS_SUCCESS);

        int result = terramanProcessService.terramanProcessPlan(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_PROCESS_GB, TEST_HOST, TEST_IDRSA);

        assertEquals(2, result);
    }

    @Test
    public void terramanProcessPlanFailTest() {
        when(commandService.execCommandOutput(TerramanConstant.DIRECTORY_COMMAND, TerramanConstant.MOVE_DIR_CLUSTER(TEST_CLUSTER_ID, TEST_PROCESS_GB), TEST_HOST, TEST_IDRSA)).thenReturn(TEST_STRING);
        when(commandService.execCommandOutput(TerramanConstant.TERRAFORM_PLAN_COMMAND, TerramanConstant.MOVE_DIR_CLUSTER(TEST_CLUSTER_ID, TEST_PROCESS_GB), TEST_HOST, TEST_IDRSA)).thenReturn(Constants.RESULT_STATUS_FAIL);
        doNothing().when(clusterLogService).saveClusterLog(TEST_CLUSTER_ID, TEST_INT_SEQ, TerramanConstant.TERRAFORM_PLAN_FAIL_LOG);
        doReturn(clusterModelMock).when(clusterService).updateCluster(TEST_CLUSTER_ID, TerramanConstant.CLUSTER_FAIL_STATUS);

        int result = terramanProcessService.terramanProcessPlan(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_PROCESS_GB, TEST_HOST, TEST_IDRSA);

        assertEquals(-1, result);
    }

    @Test
    public void terramanProcessApplyTest() {
        when(commandService.execCommandOutput(TerramanConstant.DIRECTORY_COMMAND, TerramanConstant.MOVE_DIR_CLUSTER(TEST_CLUSTER_ID, TEST_PROCESS_GB), TEST_HOST, TEST_IDRSA)).thenReturn(TEST_STRING);
        when(commandService.execCommandOutput(TerramanConstant.TERRAFORM_APPLY_COMMAND, TerramanConstant.MOVE_DIR_CLUSTER(TEST_CLUSTER_ID, TEST_PROCESS_GB), TEST_HOST, TEST_IDRSA)).thenReturn(Constants.RESULT_STATUS_SUCCESS);

        int result = terramanProcessService.terramanProcessApply(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_PROCESS_GB, TEST_HOST, TEST_IDRSA);

        assertEquals(2, result);
    }

    @Test
    public void terramanProcessApplyFailTest() {
        when(commandService.execCommandOutput(TerramanConstant.DIRECTORY_COMMAND, TerramanConstant.MOVE_DIR_CLUSTER(TEST_CLUSTER_ID, TEST_PROCESS_GB), TEST_HOST, TEST_IDRSA)).thenReturn(TEST_STRING);
        when(commandService.execCommandOutput(TerramanConstant.TERRAFORM_APPLY_COMMAND, TerramanConstant.MOVE_DIR_CLUSTER(TEST_CLUSTER_ID, TEST_PROCESS_GB), TEST_HOST, TEST_IDRSA)).thenReturn(Constants.RESULT_STATUS_FAIL);
        doNothing().when(clusterLogService).saveClusterLog(TEST_CLUSTER_ID, TEST_INT_SEQ, TerramanConstant.TERRAFORM_APPLY_FAIL_LOG);
        doReturn(clusterModelMock).when(clusterService).updateCluster(TEST_CLUSTER_ID, TerramanConstant.CLUSTER_FAIL_STATUS);

        int result = terramanProcessService.terramanProcessApply(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_PROCESS_GB, TEST_HOST, TEST_IDRSA);

        assertEquals(-1, result);
    }

    @Test
    public void terramanProcessGetInstanceIpTest() {
        when(commandService.execCommandOutput(TerramanConstant.DIRECTORY_COMMAND, "", gInstanceModel.getPublicIp(), TerramanConstant.CLUSTER_PRIVATE_KEY(TEST_CLUSTER_ID, TEST_PROCESS_GB))).thenReturn(Constants.RESULT_STATUS_TIME_OUT);
        when(instanceService.getInstance(TEST_CLUSTER_ID, TEST_PROVIDER, TEST_HOST, TEST_IDRSA, TEST_PROCESS_GB)).thenReturn(gInstanceModel);
        doNothing().when(clusterLogService).saveClusterLog(TEST_CLUSTER_ID, TEST_INT_SEQ, TerramanConstant.TERRAFORM_START_LOG(TEST_PROVIDER));
        doReturn(clusterModelMock).when(clusterService).updateCluster(TEST_CLUSTER_ID, TerramanConstant.CLUSTER_FAIL_STATUS);

        int result = terramanProcessService.terramanProcessGetInstanceIp(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_PROCESS_GB, TEST_HOST, TEST_IDRSA, TEST_PROVIDER);

        assertEquals(2, result);
    }

    @Test
    public void terramanProcessGetInstanceNullTest() {
        when(commandService.execCommandOutput(TerramanConstant.DIRECTORY_COMMAND, "", gInstanceModel.getPublicIp(), TerramanConstant.CLUSTER_PRIVATE_KEY(TEST_CLUSTER_ID, TEST_PROCESS_GB))).thenReturn(Constants.RESULT_STATUS_TIME_OUT);
        when(instanceService.getInstance(TEST_CLUSTER_ID, TEST_PROVIDER, TEST_HOST, TEST_IDRSA, TEST_PROCESS_GB)).thenReturn(null);

        int result = terramanProcessService.terramanProcessGetInstanceIp(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_PROCESS_GB, TEST_HOST, TEST_IDRSA, TEST_PROVIDER);

        assertEquals(-1, result);
    }

    @Test
    public void terramanProcessSetKubesprayTest() {
        when(commandService.execCommandOutput(TerramanConstant.DIRECTORY_COMMAND, "", gInstanceModel.getPublicIp(), TerramanConstant.CLUSTER_PRIVATE_KEY(TEST_CLUSTER_ID, TEST_PROCESS_GB))).thenReturn(Constants.RESULT_STATUS_TIME_OUT);
        when(instanceService.getInstances(TEST_CLUSTER_ID, TEST_PROVIDER, TEST_HOST, TEST_IDRSA, TEST_PROCESS_GB)).thenReturn(gInstanceList);
        when(commandService.execCommandOutput(TerramanConstant.CLUSTER_KUBESPRAY_SH_FILE_COMMAND("test"), "", TEST_HOST, TEST_IDRSA)).thenReturn(Constants.RESULT_STATUS_SUCCESS);
        doNothing().when(clusterLogService).saveClusterLog(TEST_CLUSTER_ID, TEST_INT_SEQ, TerramanConstant.TERRAFORM_START_LOG(TEST_PROVIDER));
        doReturn(clusterModelMock).when(clusterService).updateCluster(TEST_CLUSTER_ID, TerramanConstant.CLUSTER_FAIL_STATUS);

        int result = terramanProcessService.terramanProcessSetKubespray(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_PROCESS_GB, TEST_HOST, TEST_IDRSA, TEST_PROVIDER);

        assertEquals(2, result);
    }

    @Test
    public void terramanProcessExecKubesprayTest() {
        when(commandService.execCommandOutput(TerramanConstant.DIRECTORY_COMMAND, TerramanConstant.MOVE_DIR_KUBESPRAY, TEST_HOST, TEST_IDRSA)).thenReturn(TEST_STRING);
        when(commandService.execCommandOutput(TerramanConstant.KUBESPRAY_CHMOD_COMMAND, TerramanConstant.MOVE_DIR_KUBESPRAY, TEST_HOST, TEST_IDRSA)).thenReturn(Constants.RESULT_STATUS_SUCCESS);
        when(commandService.execCommandOutput(TerramanConstant.CLUSTER_KUBESPRAY_DEPLOY_COMMAND, TerramanConstant.MOVE_DIR_KUBESPRAY, TEST_HOST, TEST_IDRSA)).thenReturn(Constants.RESULT_STATUS_SUCCESS);

        int result = terramanProcessService.terramanProcessExecKubespray(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_HOST, TEST_IDRSA);

        assertEquals(1, result);
    }

    @Test
    public void terramanProcessExecKubesprayModErrorTest() {
        when(commandService.execCommandOutput(TerramanConstant.DIRECTORY_COMMAND, TerramanConstant.MOVE_DIR_KUBESPRAY, TEST_HOST, TEST_IDRSA)).thenReturn(TEST_STRING);
        when(commandService.execCommandOutput(TerramanConstant.KUBESPRAY_CHMOD_COMMAND, TerramanConstant.MOVE_DIR_KUBESPRAY, TEST_HOST, TEST_IDRSA)).thenReturn(Constants.RESULT_STATUS_FAIL);

        doNothing().when(clusterLogService).saveClusterLog(TEST_CLUSTER_ID, TEST_MP_SEQ, TerramanConstant.TERRAFORM_CHANGE_MODE_ERROR);
        doReturn(clusterModelMock).when(clusterService).updateCluster(TEST_CLUSTER_ID, TerramanConstant.CLUSTER_FAIL_STATUS);

        int result = terramanProcessService.terramanProcessExecKubespray(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_HOST, TEST_IDRSA);

        assertEquals(-1, result);
    }

    @Test
    public void terramanProcessExecKubesprayDeployErrorTest() {
        when(commandService.execCommandOutput(TerramanConstant.DIRECTORY_COMMAND, TerramanConstant.MOVE_DIR_KUBESPRAY, TEST_HOST, TEST_IDRSA)).thenReturn(TEST_STRING);
        when(commandService.execCommandOutput(TerramanConstant.KUBESPRAY_CHMOD_COMMAND, TerramanConstant.MOVE_DIR_KUBESPRAY, TEST_HOST, TEST_IDRSA)).thenReturn(Constants.RESULT_STATUS_SUCCESS);
        when(commandService.execCommandOutput(TerramanConstant.CLUSTER_KUBESPRAY_DEPLOY_COMMAND, TerramanConstant.MOVE_DIR_KUBESPRAY, TEST_HOST, TEST_IDRSA)).thenReturn(Constants.RESULT_STATUS_FAIL);

        doNothing().when(clusterLogService).saveClusterLog(TEST_CLUSTER_ID, TEST_INT_SEQ, TerramanConstant.TERRAFORM_DEPLOY_CLUSTER_ERROR);
        doReturn(clusterModelMock).when(clusterService).updateCluster(TEST_CLUSTER_ID, TerramanConstant.CLUSTER_FAIL_STATUS);

        int result = terramanProcessService.terramanProcessExecKubespray(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_HOST, TEST_IDRSA);

        assertEquals(-1, result);
    }

    @Test
    public void terramanProcessCreateVaultTest() {
        when(instanceService.getInstance(TEST_CLUSTER_ID, TEST_PROVIDER, TEST_HOST, TEST_IDRSA, TEST_PROCESS_GB)).thenReturn(gInstanceModel);
        when(commandService.execCommandOutput(TerramanConstant.SERVICE_ACCOUNT_CREATE, "", gInstanceModel.getPublicIp(), TerramanConstant.CLUSTER_PRIVATE_KEY(TEST_CLUSTER_ID, TEST_PROCESS_GB))).thenReturn(Constants.RESULT_STATUS_SUCCESS);
        when(commandService.execCommandOutput(TerramanConstant.SERVICE_ACCOUNT_BINDING, "", gInstanceModel.getPublicIp(), TerramanConstant.CLUSTER_PRIVATE_KEY(TEST_CLUSTER_ID, TEST_PROCESS_GB))).thenReturn(Constants.RESULT_STATUS_SUCCESS);
        when(commandService.execCommandOutput(TerramanConstant.SERVICE_ACCOUNT_SECRET_NAME, "", gInstanceModel.getPublicIp(), TerramanConstant.CLUSTER_PRIVATE_KEY(TEST_CLUSTER_ID, TEST_PROCESS_GB))).thenReturn(Constants.RESULT_STATUS_SUCCESS);
        when(commandService.execCommandOutput(TerramanConstant.SERVICE_ACCOUNT_TOKEN(TEST_STRING), "", gInstanceModel.getPublicIp(), TerramanConstant.CLUSTER_PRIVATE_KEY(TEST_CLUSTER_ID, TEST_PROCESS_GB))).thenReturn(Constants.RESULT_STATUS_SUCCESS);
        when(propertyService.getVaultClusterApiUrl()).thenReturn(TEST_CLUSTER_API_URL);
        when(propertyService.getVaultClusterTokenPath()).thenReturn(TEST_CLUSTER_TOKEN);
        when(vaultService.write(TEST_CLUSTER_API_URL, clusterInfoMock)).thenReturn(gInstanceModel);

        doNothing().when(clusterLogService).saveClusterLog(TEST_CLUSTER_ID, TEST_INT_SEQ, TerramanConstant.TERRAFORM_CREATE_TOKEN_ERROR);
        doReturn(clusterModelMock).when(clusterService).updateCluster(TEST_CLUSTER_ID, TerramanConstant.CLUSTER_FAIL_STATUS);

        int result = terramanProcessService.terramanProcessCreateVault(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_PROCESS_GB, TEST_HOST, TEST_IDRSA, TEST_PROVIDER);

        assertEquals(-1, result);
    }

    @Test
    public void terramanProcessCreateVaulCreateErrortTest() {
        when(instanceService.getInstance(TEST_CLUSTER_ID, TEST_PROVIDER, TEST_HOST, TEST_IDRSA, TEST_PROCESS_GB)).thenReturn(gInstanceModel);
        when(commandService.execCommandOutput(TerramanConstant.SERVICE_ACCOUNT_CREATE, "", gInstanceModel.getPublicIp(), TerramanConstant.CLUSTER_PRIVATE_KEY(TEST_CLUSTER_ID, TEST_PROCESS_GB))).thenReturn(Constants.RESULT_STATUS_FAIL);
        when(propertyService.getVaultClusterApiUrl()).thenReturn(TEST_CLUSTER_API_URL);
        when(propertyService.getVaultClusterTokenPath()).thenReturn(TEST_CLUSTER_TOKEN);
        when(vaultService.write(TEST_CLUSTER_API_URL, clusterInfoMock)).thenReturn(gInstanceModel);

        doNothing().when(clusterLogService).saveClusterLog(TEST_CLUSTER_ID, TEST_INT_SEQ, TerramanConstant.TERRAFORM_CREATE_SERVICE_ACCOUNT_ERROR);
        doReturn(clusterModelMock).when(clusterService).updateCluster(TEST_CLUSTER_ID, TerramanConstant.CLUSTER_FAIL_STATUS);

        int result = terramanProcessService.terramanProcessCreateVault(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_PROCESS_GB, TEST_HOST, TEST_IDRSA, TEST_PROVIDER);

        assertEquals(-1, result);
    }

    @Test
    public void terramanProcessCreateVaultBindingErrorTest() {
        when(instanceService.getInstance(TEST_CLUSTER_ID, TEST_PROVIDER, TEST_HOST, TEST_IDRSA, TEST_PROCESS_GB)).thenReturn(gInstanceModel);
        when(commandService.execCommandOutput(TerramanConstant.SERVICE_ACCOUNT_CREATE, "", gInstanceModel.getPublicIp(), TerramanConstant.CLUSTER_PRIVATE_KEY(TEST_CLUSTER_ID, TEST_PROCESS_GB))).thenReturn(Constants.RESULT_STATUS_SUCCESS);
        when(commandService.execCommandOutput(TerramanConstant.SERVICE_ACCOUNT_BINDING, "", gInstanceModel.getPublicIp(), TerramanConstant.CLUSTER_PRIVATE_KEY(TEST_CLUSTER_ID, TEST_PROCESS_GB))).thenReturn(Constants.RESULT_STATUS_FAIL);
        when(propertyService.getVaultClusterApiUrl()).thenReturn(TEST_CLUSTER_API_URL);
        when(propertyService.getVaultClusterTokenPath()).thenReturn(TEST_CLUSTER_TOKEN);
        when(vaultService.write(TEST_CLUSTER_API_URL, clusterInfoMock)).thenReturn(gInstanceModel);

        doNothing().when(clusterLogService).saveClusterLog(TEST_CLUSTER_ID, TEST_INT_SEQ, TerramanConstant.TERRAFORM_BIND_ROLE_ERROR);
        doReturn(clusterModelMock).when(clusterService).updateCluster(TEST_CLUSTER_ID, TerramanConstant.CLUSTER_FAIL_STATUS);

        int result = terramanProcessService.terramanProcessCreateVault(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_PROCESS_GB, TEST_HOST, TEST_IDRSA, TEST_PROVIDER);

        assertEquals(-1, result);
    }

    @Test
    public void terramanProcessCreateVaultSecretNameErrorTest() {
        when(instanceService.getInstance(TEST_CLUSTER_ID, TEST_PROVIDER, TEST_HOST, TEST_IDRSA, TEST_PROCESS_GB)).thenReturn(gInstanceModel);
        when(commandService.execCommandOutput(TerramanConstant.SERVICE_ACCOUNT_CREATE, "", gInstanceModel.getPublicIp(), TerramanConstant.CLUSTER_PRIVATE_KEY(TEST_CLUSTER_ID, TEST_PROCESS_GB))).thenReturn(Constants.RESULT_STATUS_SUCCESS);
        when(commandService.execCommandOutput(TerramanConstant.SERVICE_ACCOUNT_BINDING, "", gInstanceModel.getPublicIp(), TerramanConstant.CLUSTER_PRIVATE_KEY(TEST_CLUSTER_ID, TEST_PROCESS_GB))).thenReturn(Constants.RESULT_STATUS_SUCCESS);
        when(commandService.execCommandOutput(TerramanConstant.SERVICE_ACCOUNT_SECRET_NAME, "", gInstanceModel.getPublicIp(), TerramanConstant.CLUSTER_PRIVATE_KEY(TEST_CLUSTER_ID, TEST_PROCESS_GB))).thenReturn(Constants.RESULT_STATUS_FAIL);
        when(propertyService.getVaultClusterApiUrl()).thenReturn(TEST_CLUSTER_API_URL);
        when(propertyService.getVaultClusterTokenPath()).thenReturn(TEST_CLUSTER_TOKEN);
        when(vaultService.write(TEST_CLUSTER_API_URL, clusterInfoMock)).thenReturn(gInstanceModel);

        doNothing().when(clusterLogService).saveClusterLog(TEST_CLUSTER_ID, TEST_INT_SEQ, TerramanConstant.TERRAFORM_GET_SECRET_NAME_ERROR);
        doReturn(clusterModelMock).when(clusterService).updateCluster(TEST_CLUSTER_ID, TerramanConstant.CLUSTER_FAIL_STATUS);

        int result = terramanProcessService.terramanProcessCreateVault(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_PROCESS_GB, TEST_HOST, TEST_IDRSA, TEST_PROVIDER);

        assertEquals(-1, result);
    }

    @Test
    public void terramanProcessCreateVaultTokenErrorTest() {
        when(instanceService.getInstance(TEST_CLUSTER_ID, TEST_PROVIDER, TEST_HOST, TEST_IDRSA, TEST_PROCESS_GB)).thenReturn(gInstanceModel);
        when(commandService.execCommandOutput(TerramanConstant.SERVICE_ACCOUNT_CREATE, "", gInstanceModel.getPublicIp(), TerramanConstant.CLUSTER_PRIVATE_KEY(TEST_CLUSTER_ID, TEST_PROCESS_GB))).thenReturn(Constants.RESULT_STATUS_SUCCESS);
        when(commandService.execCommandOutput(TerramanConstant.SERVICE_ACCOUNT_BINDING, "", gInstanceModel.getPublicIp(), TerramanConstant.CLUSTER_PRIVATE_KEY(TEST_CLUSTER_ID, TEST_PROCESS_GB))).thenReturn(Constants.RESULT_STATUS_SUCCESS);
        when(commandService.execCommandOutput(TerramanConstant.SERVICE_ACCOUNT_SECRET_NAME, "", gInstanceModel.getPublicIp(), TerramanConstant.CLUSTER_PRIVATE_KEY(TEST_CLUSTER_ID, TEST_PROCESS_GB))).thenReturn(Constants.RESULT_STATUS_SUCCESS);
        when(commandService.execCommandOutput(TerramanConstant.SERVICE_ACCOUNT_TOKEN(TEST_STRING), "", gInstanceModel.getPublicIp(), TerramanConstant.CLUSTER_PRIVATE_KEY(TEST_CLUSTER_ID, TEST_PROCESS_GB))).thenReturn(Constants.RESULT_STATUS_FAIL);
        when(propertyService.getVaultClusterApiUrl()).thenReturn(TEST_CLUSTER_API_URL);
        when(propertyService.getVaultClusterTokenPath()).thenReturn(TEST_CLUSTER_TOKEN);
        when(vaultService.write(TEST_CLUSTER_API_URL, clusterInfoMock)).thenReturn(gInstanceModel);

        doNothing().when(clusterLogService).saveClusterLog(TEST_CLUSTER_ID, TEST_INT_SEQ, TerramanConstant.TERRAFORM_GET_CLUSTER_TOKEN_ERROR);
        doReturn(clusterModelMock).when(clusterService).updateCluster(TEST_CLUSTER_ID, TerramanConstant.CLUSTER_FAIL_STATUS);

        int result = terramanProcessService.terramanProcessCreateVault(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_PROCESS_GB, TEST_HOST, TEST_IDRSA, TEST_PROVIDER);

        assertEquals(-1, result);
    }

    @Test
    public void terramanProcessClusterStatusUpdateTest() {
        doReturn(clusterModelMock).when(clusterService).updateCluster(TEST_CLUSTER_ID, TerramanConstant.CLUSTER_COMPLETE_STATUS);

        int result = terramanProcessService.terramanProcessClusterStatusUpdate(TEST_MP_SEQ, TEST_CLUSTER_ID);

        assertEquals(1, result);
    }

    @Test
    public void terramanProcessClusterStatusUpdateFailTest() {
        doReturn(null).when(clusterService).updateCluster(TEST_CLUSTER_ID, TerramanConstant.CLUSTER_FAIL_STATUS);
        doNothing().when(clusterLogService).saveClusterLog(TEST_CLUSTER_ID, TEST_MP_SEQ, TerramanConstant.TERRAFORM_COMPLETE_CLUSTER_ERROR);
        doReturn(clusterModelMock).when(clusterService).updateCluster(TEST_CLUSTER_ID, TerramanConstant.CLUSTER_FAIL_STATUS);

        int result = terramanProcessService.terramanProcessClusterStatusUpdate(TEST_MP_SEQ, TEST_CLUSTER_ID);

        assertEquals(-1, result);
    }
}
