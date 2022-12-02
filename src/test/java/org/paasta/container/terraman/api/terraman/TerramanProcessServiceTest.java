package org.paasta.container.terraman.api.terraman;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.paasta.container.terraman.api.common.PropertyService;
import org.paasta.container.terraman.api.common.VaultService;
import org.paasta.container.terraman.api.common.constants.Constants;
import org.paasta.container.terraman.api.common.constants.TerramanConstant;
import org.paasta.container.terraman.api.common.model.ClusterInfo;
import org.paasta.container.terraman.api.common.model.ClusterModel;
import org.paasta.container.terraman.api.common.model.InstanceModel;
import org.paasta.container.terraman.api.common.model.TerramanCommandModel;
import org.paasta.container.terraman.api.common.service.*;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class TerramanProcessServiceTest {

    private static final String TEST_CLUSTER_ID = "test_cluster";
    private static final String TEST_CLUSTER_NAME = "test_cluster_name";
    private static final int TEST_INT_SEQ = 13;
    private static final String TEST_PROCESS_GB = "test";
    private static final String TEST_HOST = "1.1.1.1";
    private static final String TEST_IDRSA = "id_rsa";
    private static final String TEST_PROVIDER = "test";
    private static final String TEST_RESULT_CODE = "200";
    private static final String TEST_DIR = "/home";
    private static final String[] TEST_COMMAND_NUMBER = {"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15"};

    private static final int TEST_MP_SEQ = 1;

    private static final String TEST_STRING = "test";
    private static final String TEST_ID_RSA_PATH = "/root/.ssh/id_rsa";

    private static final String TEST_RESOURCE_NAME = "master";
    private static final String TEST_INSTANCE_NAME = "instanceName";
    private static final String TEST_PRIVATE_IP = "privateIp";
    private static final String TEST_PUBLIC_IP = "publicIp";

    private static final String TEST_CLUSTER_API_URL = "apiUrl";
    private static final String TEST_CLUSTER_TOKEN = "token";

    private static InstanceModel gInstanceModel = null;
    private static InstanceModel gInstanceModel2 = null;
    private static List<InstanceModel> gInstanceList = null;
    private static ClusterModel clusterModelMock = null;
    private static Object objectMock = null;
    private static ClusterInfo clusterInfoMock = null;


    @Mock
    private CommandService commandService;
    @Mock
    private ClusterLogService clusterLogService;
    @Mock
    private InstanceService instanceService;
    @Mock
    private ClusterService clusterService;
    @Mock
    private PropertyService propertyService;
    @Mock
    private VaultService vaultService;
    @Mock
    private TfFileService tfFileService;

    @InjectMocks
    private TerramanProcessService terramanProcessService;

    @Before
    public void setUp() {
        gInstanceModel = new InstanceModel(TEST_RESOURCE_NAME, TEST_INSTANCE_NAME, TEST_PRIVATE_IP, TEST_PUBLIC_IP);
        gInstanceModel2 = new InstanceModel(TEST_RESOURCE_NAME, TEST_INSTANCE_NAME, "", TEST_PUBLIC_IP);
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
        TerramanCommandModel terramanCommandModel = new TerramanCommandModel();
        terramanCommandModel.setCommand(TEST_COMMAND_NUMBER[14]);
        terramanCommandModel.setDir(TEST_DIR);
        terramanCommandModel.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel.setClusterId(TEST_CLUSTER_ID);
        doNothing().when(clusterLogService).deleteClusterLogByClusterId(TEST_CLUSTER_ID);
        when(commandService.execCommandOutput(terramanCommandModel)).thenReturn(Constants.RESULT_STATUS_SUCCESS);

        int result = terramanProcessService.terramanProcessSet(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_DIR);

        assertEquals(1, result);
    }

    @Test
    public void terramanProcessSetFailTest() {
        TerramanCommandModel terramanCommandModel = new TerramanCommandModel();
        terramanCommandModel.setCommand(TEST_COMMAND_NUMBER[14]);
        terramanCommandModel.setDir(TEST_DIR);
        terramanCommandModel.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel.setClusterId(TEST_CLUSTER_ID);
        doNothing().when(clusterLogService).deleteClusterLogByClusterId(TEST_CLUSTER_ID);
        when(commandService.execCommandOutput(terramanCommandModel)).thenReturn(Constants.RESULT_STATUS_FAIL);

        doReturn(clusterModelMock).when(clusterService).updateCluster(TEST_CLUSTER_ID, TerramanConstant.CLUSTER_FAIL_STATUS);
        doNothing().when(clusterLogService).saveClusterLog(TEST_CLUSTER_ID, TEST_INT_SEQ, TerramanConstant.TERRAFORM_CREATE_CLUSTER_DIRECTORY_ERROR);

        int result = terramanProcessService.terramanProcessSet(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_DIR);

        assertEquals(1, result);
    }

    @Test
    public void terramanProcessStartTest() {
        TerramanCommandModel terramanCommandModel = new TerramanCommandModel();
        terramanCommandModel.setCommand(TEST_COMMAND_NUMBER[1]);
        terramanCommandModel.setDir(TerramanConstant.MOVE_DIR_CLUSTER(TEST_CLUSTER_ID));
        terramanCommandModel.setHost(TEST_HOST);
        terramanCommandModel.setIdRsa(TEST_IDRSA);
        terramanCommandModel.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel.setClusterId(TEST_CLUSTER_ID);
        when(commandService.execCommandOutput(terramanCommandModel)).thenReturn(TEST_RESULT_CODE);
        doNothing().when(clusterLogService).saveClusterLog(TEST_CLUSTER_ID, TEST_INT_SEQ, TerramanConstant.TERRAFORM_START_LOG(TEST_PROVIDER));
        doNothing().when(clusterLogService).saveClusterLog(TEST_CLUSTER_ID, TEST_INT_SEQ, TerramanConstant.TERRAFORM_IAC_LOG);

        int result = terramanProcessService.terramanProcessStart(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_PROVIDER, TEST_PROCESS_GB, TEST_HOST, TEST_IDRSA);

        assertEquals(3, result);
    }

    @Test
    public void terramanProcessSetTfFileTest() {
        TfFileService tfFileServiceMock = mock(TfFileService.class);
        TerramanCommandModel terramanCommandModel = new TerramanCommandModel();
        terramanCommandModel.setCommand(TEST_COMMAND_NUMBER[2]);
        terramanCommandModel.setDir(TerramanConstant.MOVE_DIR_CLUSTER(TEST_CLUSTER_ID));
        terramanCommandModel.setHost(TEST_HOST);
        terramanCommandModel.setIdRsa(TEST_IDRSA);
        terramanCommandModel.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel.setClusterId(TEST_CLUSTER_ID);
        when(commandService.execCommandOutput(terramanCommandModel)).thenReturn(TEST_RESULT_CODE);
        when(tfFileServiceMock.createProviderFile(TEST_CLUSTER_ID, TEST_PROVIDER, TEST_INT_SEQ, TEST_STRING, TEST_HOST, TEST_ID_RSA_PATH, TEST_PROCESS_GB)).thenReturn(TEST_RESULT_CODE);
        doNothing().when(clusterLogService).saveClusterLog(TEST_CLUSTER_ID, TEST_INT_SEQ, TerramanConstant.TERRAFORM_TF_ERROR_LOG);
        doReturn(clusterModelMock).when(clusterService).updateCluster(TEST_CLUSTER_ID, TerramanConstant.CLUSTER_FAIL_STATUS);
        doNothing().when(clusterLogService).saveClusterLog(TEST_CLUSTER_ID, TEST_INT_SEQ, TerramanConstant.TERRAFORM_TF_LOG);

        int result = terramanProcessService.terramanProcessSetTfFile(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_PROCESS_GB, TEST_HOST, TEST_IDRSA, TEST_PROVIDER, TEST_INT_SEQ);

        assertEquals(-1, result);
    }

    @Test
    public void terramanProcessSetTfFileFailTest() {
        TerramanCommandModel terramanCommandModel = new TerramanCommandModel();
        terramanCommandModel.setCommand(TEST_COMMAND_NUMBER[2]);
        terramanCommandModel.setDir(TerramanConstant.MOVE_DIR_CLUSTER(TEST_CLUSTER_ID));
        terramanCommandModel.setHost(TEST_HOST);
        terramanCommandModel.setIdRsa(TEST_IDRSA);
        terramanCommandModel.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel.setClusterId(TEST_CLUSTER_ID);
        when(commandService.execCommandOutput(terramanCommandModel)).thenReturn(TEST_RESULT_CODE);
        when(tfFileService.createProviderFile(TEST_CLUSTER_ID, TEST_PROVIDER, TEST_INT_SEQ, TEST_STRING, TEST_HOST, TEST_ID_RSA_PATH, TEST_PROCESS_GB)).thenReturn(Constants.RESULT_STATUS_FAIL);
        doNothing().when(clusterLogService).saveClusterLog(TEST_CLUSTER_ID, TEST_INT_SEQ, TerramanConstant.TERRAFORM_TF_ERROR_LOG);
        doReturn(clusterModelMock).when(clusterService).updateCluster(TEST_CLUSTER_ID, TerramanConstant.CLUSTER_FAIL_STATUS);

        int result = terramanProcessService.terramanProcessSetTfFile(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_PROCESS_GB, TEST_HOST, TEST_IDRSA, TEST_PROVIDER, TEST_INT_SEQ);

        assertEquals(-1, result);
    }

    @Test
    public void terramanProcessInitTest() {
        TerramanCommandModel terramanCommandModel = new TerramanCommandModel();
        TerramanCommandModel terramanCommandModel2 = new TerramanCommandModel();

        terramanCommandModel.setCommand(TEST_COMMAND_NUMBER[1]);
        terramanCommandModel.setDir(TerramanConstant.MOVE_DIR_CLUSTER(TEST_CLUSTER_ID));
        terramanCommandModel.setHost(TEST_HOST);
        terramanCommandModel.setIdRsa(TEST_IDRSA);
        terramanCommandModel.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel.setClusterId(TEST_CLUSTER_ID);

        terramanCommandModel2.setCommand(TEST_COMMAND_NUMBER[3]);
        terramanCommandModel2.setDir(TerramanConstant.MOVE_DIR_CLUSTER(TEST_CLUSTER_ID));
        terramanCommandModel2.setHost(TEST_HOST);
        terramanCommandModel2.setIdRsa(TEST_IDRSA);
        terramanCommandModel2.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel2.setClusterId(TEST_CLUSTER_ID);

        when(commandService.execCommandOutput(terramanCommandModel)).thenReturn(TEST_STRING);
        when(commandService.execCommandOutput(terramanCommandModel2)).thenReturn(Constants.RESULT_STATUS_SUCCESS);

        int result = terramanProcessService.terramanProcessInit(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_PROCESS_GB, TEST_HOST, TEST_IDRSA);

        assertEquals(2, result);
    }

    @Test
    public void terramanProcessInitFailTest() {
        TerramanCommandModel terramanCommandModel = new TerramanCommandModel();
        TerramanCommandModel terramanCommandModel2 = new TerramanCommandModel();

        terramanCommandModel.setCommand(TEST_COMMAND_NUMBER[1]);
        terramanCommandModel.setDir(TerramanConstant.MOVE_DIR_CLUSTER(TEST_CLUSTER_ID));
        terramanCommandModel.setHost(TEST_HOST);
        terramanCommandModel.setIdRsa(TEST_IDRSA);
        terramanCommandModel.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel.setClusterId(TEST_CLUSTER_ID);

        terramanCommandModel2.setCommand(TEST_COMMAND_NUMBER[3]);
        terramanCommandModel2.setDir(TerramanConstant.MOVE_DIR_CLUSTER(TEST_CLUSTER_ID));
        terramanCommandModel2.setHost(TEST_HOST);
        terramanCommandModel2.setIdRsa(TEST_IDRSA);
        terramanCommandModel2.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel2.setClusterId(TEST_CLUSTER_ID);

        when(commandService.execCommandOutput(terramanCommandModel)).thenReturn(TEST_STRING);
        when(commandService.execCommandOutput(terramanCommandModel2)).thenReturn(Constants.RESULT_STATUS_FAIL);
        doNothing().when(clusterLogService).saveClusterLog(TEST_CLUSTER_ID, TEST_INT_SEQ, TerramanConstant.TERRAFORM_INIT_FAIL_LOG);
        doReturn(clusterModelMock).when(clusterService).updateCluster(TEST_CLUSTER_ID, TerramanConstant.CLUSTER_FAIL_STATUS);

        int result = terramanProcessService.terramanProcessInit(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_PROCESS_GB, TEST_HOST, TEST_IDRSA);

        assertEquals(-1, result);
    }

    @Test
    public void terramanProcessPlanTest() {
        TerramanCommandModel terramanCommandModel = new TerramanCommandModel();
        TerramanCommandModel terramanCommandModel2 = new TerramanCommandModel();

        terramanCommandModel.setCommand(TEST_COMMAND_NUMBER[1]);
        terramanCommandModel.setDir(TerramanConstant.MOVE_DIR_CLUSTER(TEST_CLUSTER_ID));
        terramanCommandModel.setHost(TEST_HOST);
        terramanCommandModel.setIdRsa(TEST_IDRSA);
        terramanCommandModel.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel.setClusterId(TEST_CLUSTER_ID);

        terramanCommandModel2.setCommand(TEST_COMMAND_NUMBER[4]);
        terramanCommandModel2.setDir(TerramanConstant.MOVE_DIR_CLUSTER(TEST_CLUSTER_ID));
        terramanCommandModel2.setHost(TEST_HOST);
        terramanCommandModel2.setIdRsa(TEST_IDRSA);
        terramanCommandModel2.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel2.setClusterId(TEST_CLUSTER_ID);

        when(commandService.execCommandOutput(terramanCommandModel)).thenReturn(TEST_STRING);
        when(commandService.execCommandOutput(terramanCommandModel2)).thenReturn(Constants.RESULT_STATUS_SUCCESS);

        int result = terramanProcessService.terramanProcessPlan(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_PROCESS_GB, TEST_HOST, TEST_IDRSA);

        assertEquals(2, result);
    }

    @Test
    public void terramanProcessPlanFailTest() {
        TerramanCommandModel terramanCommandModel = new TerramanCommandModel();
        TerramanCommandModel terramanCommandModel2 = new TerramanCommandModel();

        terramanCommandModel.setCommand(TEST_COMMAND_NUMBER[1]);
        terramanCommandModel.setDir(TerramanConstant.MOVE_DIR_CLUSTER(TEST_CLUSTER_ID));
        terramanCommandModel.setHost(TEST_HOST);
        terramanCommandModel.setIdRsa(TEST_IDRSA);
        terramanCommandModel.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel.setClusterId(TEST_CLUSTER_ID);

        terramanCommandModel2.setCommand(TEST_COMMAND_NUMBER[4]);
        terramanCommandModel2.setDir(TerramanConstant.MOVE_DIR_CLUSTER(TEST_CLUSTER_ID));
        terramanCommandModel2.setHost(TEST_HOST);
        terramanCommandModel2.setIdRsa(TEST_IDRSA);
        terramanCommandModel2.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel2.setClusterId(TEST_CLUSTER_ID);

        when(commandService.execCommandOutput(terramanCommandModel)).thenReturn(TEST_STRING);
        when(commandService.execCommandOutput(terramanCommandModel2)).thenReturn(Constants.RESULT_STATUS_SUCCESS);
        doNothing().when(clusterLogService).saveClusterLog(TEST_CLUSTER_ID, TEST_INT_SEQ, TerramanConstant.TERRAFORM_PLAN_FAIL_LOG);
        doReturn(clusterModelMock).when(clusterService).updateCluster(TEST_CLUSTER_ID, TerramanConstant.CLUSTER_FAIL_STATUS);

        int result = terramanProcessService.terramanProcessPlan(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_PROCESS_GB, TEST_HOST, TEST_IDRSA);

        assertEquals(2, result);
    }

    @Test
    public void terramanProcessApplyTest() {
        TerramanCommandModel terramanCommandModel = new TerramanCommandModel();
        TerramanCommandModel terramanCommandModel2 = new TerramanCommandModel();

        terramanCommandModel.setCommand(TEST_COMMAND_NUMBER[1]);
        terramanCommandModel.setDir(TerramanConstant.MOVE_DIR_CLUSTER(TEST_CLUSTER_ID));
        terramanCommandModel.setHost(TEST_HOST);
        terramanCommandModel.setIdRsa(TEST_IDRSA);
        terramanCommandModel.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel.setClusterId(TEST_CLUSTER_ID);

        terramanCommandModel2.setCommand(TEST_COMMAND_NUMBER[5]);
        terramanCommandModel2.setDir(TerramanConstant.MOVE_DIR_CLUSTER(TEST_CLUSTER_ID));
        terramanCommandModel2.setHost(TEST_HOST);
        terramanCommandModel2.setIdRsa(TEST_IDRSA);
        terramanCommandModel2.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel2.setClusterId(TEST_CLUSTER_ID);

        when(commandService.execCommandOutput(terramanCommandModel)).thenReturn(TEST_STRING);
        when(commandService.execCommandOutput(terramanCommandModel2)).thenReturn(Constants.RESULT_STATUS_SUCCESS);

        int result = terramanProcessService.terramanProcessApply(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_PROCESS_GB, TEST_HOST, TEST_IDRSA);

        assertEquals(2, result);
    }

    @Test
    public void terramanProcessApplyFailTest() {
        TerramanCommandModel terramanCommandModel = new TerramanCommandModel();
        TerramanCommandModel terramanCommandModel2 = new TerramanCommandModel();

        terramanCommandModel.setCommand(TEST_COMMAND_NUMBER[1]);
        terramanCommandModel.setDir(TerramanConstant.MOVE_DIR_CLUSTER(TEST_CLUSTER_ID));
        terramanCommandModel.setHost(TEST_HOST);
        terramanCommandModel.setIdRsa(TEST_IDRSA);
        terramanCommandModel.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel.setClusterId(TEST_CLUSTER_ID);

        terramanCommandModel2.setCommand(TEST_COMMAND_NUMBER[5]);
        terramanCommandModel2.setDir(TerramanConstant.MOVE_DIR_CLUSTER(TEST_CLUSTER_ID));
        terramanCommandModel2.setHost(TEST_HOST);
        terramanCommandModel2.setIdRsa(TEST_IDRSA);
        terramanCommandModel2.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel2.setClusterId(TEST_CLUSTER_ID);

        when(commandService.execCommandOutput(terramanCommandModel)).thenReturn(TEST_STRING);
        when(commandService.execCommandOutput(terramanCommandModel2)).thenReturn(Constants.RESULT_STATUS_SUCCESS);
        doNothing().when(clusterLogService).saveClusterLog(TEST_CLUSTER_ID, TEST_INT_SEQ, TerramanConstant.TERRAFORM_APPLY_FAIL_LOG);
        doReturn(clusterModelMock).when(clusterService).updateCluster(TEST_CLUSTER_ID, TerramanConstant.CLUSTER_FAIL_STATUS);

        int result = terramanProcessService.terramanProcessApply(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_PROCESS_GB, TEST_HOST, TEST_IDRSA);

        assertEquals(2, result);
    }

    @Test
    public void terramanProcessGetInstanceIpTest() {
        TerramanCommandModel terramanCommandModel = new TerramanCommandModel();
        terramanCommandModel.setCommand(TEST_COMMAND_NUMBER[1]);
        terramanCommandModel.setHost(gInstanceModel.getPublicIp());
        terramanCommandModel.setIdRsa(TerramanConstant.CLUSTER_PRIVATE_KEY(TEST_CLUSTER_NAME));
        terramanCommandModel.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel.setClusterId(TEST_CLUSTER_ID);
        when(commandService.execCommandOutput(terramanCommandModel)).thenReturn(Constants.RESULT_STATUS_TIME_OUT);
        when(instanceService.getInstance(TEST_CLUSTER_ID, TEST_PROVIDER, TEST_HOST, TEST_IDRSA, TEST_PROCESS_GB)).thenReturn(gInstanceModel);
        doNothing().when(clusterLogService).saveClusterLog(TEST_CLUSTER_ID, TEST_INT_SEQ, TerramanConstant.TERRAFORM_START_LOG(TEST_PROVIDER));
        doReturn(clusterModelMock).when(clusterService).updateCluster(TEST_CLUSTER_ID, TerramanConstant.CLUSTER_FAIL_STATUS);

        int result = terramanProcessService.terramanProcessGetInstanceIp(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_PROCESS_GB, TEST_HOST, TEST_IDRSA, TEST_PROVIDER, TEST_CLUSTER_NAME);

        assertEquals(2, result);
    }

    @Test
    public void terramanProcessGetInstanceNullTest() {
        TerramanCommandModel terramanCommandModel = new TerramanCommandModel();
        terramanCommandModel.setCommand(TEST_COMMAND_NUMBER[1]);
        terramanCommandModel.setHost(gInstanceModel.getPublicIp());
        terramanCommandModel.setIdRsa(TerramanConstant.CLUSTER_PRIVATE_KEY(TEST_CLUSTER_NAME));
        terramanCommandModel.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel.setClusterId(TEST_CLUSTER_ID);
        when(commandService.execCommandOutput(terramanCommandModel)).thenReturn(Constants.RESULT_STATUS_TIME_OUT);
        when(instanceService.getInstance(TEST_CLUSTER_ID, TEST_PROVIDER, TEST_HOST, TEST_IDRSA, TEST_PROCESS_GB)).thenReturn(null);

        int result = terramanProcessService.terramanProcessGetInstanceIp(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_PROCESS_GB, TEST_HOST, TEST_IDRSA, TEST_PROVIDER, TEST_CLUSTER_NAME);

        assertEquals(-1, result);
    }

    @Test
    public void terramanProcessSetKubesprayTest() {
        TerramanCommandModel terramanCommandModel = new TerramanCommandModel();
        TerramanCommandModel terramanCommandModel2 = new TerramanCommandModel();

        terramanCommandModel.setCommand(TEST_COMMAND_NUMBER[1]);
        terramanCommandModel.setHost(gInstanceModel.getPublicIp());
        terramanCommandModel.setIdRsa(TerramanConstant.CLUSTER_PRIVATE_KEY(TEST_CLUSTER_NAME));
        terramanCommandModel.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel.setClusterId(TEST_CLUSTER_ID);

        terramanCommandModel2.setCommand(TEST_COMMAND_NUMBER[7]);
        terramanCommandModel2.setHost(TEST_HOST);
        terramanCommandModel2.setIdRsa(TEST_IDRSA);
        terramanCommandModel2.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel2.setClusterId(TEST_CLUSTER_ID);

        when(commandService.execCommandOutput(terramanCommandModel)).thenReturn(Constants.RESULT_STATUS_TIME_OUT);
        when(instanceService.getInstances(TEST_CLUSTER_ID, TEST_PROVIDER, TEST_HOST, TEST_IDRSA, TEST_PROCESS_GB)).thenReturn(gInstanceList);
        when(commandService.execCommandOutput(terramanCommandModel2)).thenReturn(Constants.RESULT_STATUS_SUCCESS);
        doNothing().when(clusterLogService).saveClusterLog(TEST_CLUSTER_ID, TEST_INT_SEQ, TerramanConstant.TERRAFORM_START_LOG(TEST_PROVIDER));
        doReturn(clusterModelMock).when(clusterService).updateCluster(TEST_CLUSTER_ID, TerramanConstant.CLUSTER_FAIL_STATUS);

        int result = terramanProcessService.terramanProcessSetKubespray(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_PROCESS_GB, TEST_HOST, TEST_IDRSA, TEST_PROVIDER, TEST_CLUSTER_NAME);

        assertEquals(2, result);
    }

    @Test
    public void terramanProcessExecKubesprayTest() {
        TerramanCommandModel terramanCommandModel = new TerramanCommandModel();
        TerramanCommandModel terramanCommandModel2 = new TerramanCommandModel();
        TerramanCommandModel terramanCommandModel3 = new TerramanCommandModel();

        terramanCommandModel.setCommand(TEST_COMMAND_NUMBER[1]);
        terramanCommandModel.setDir(TerramanConstant.MOVE_DIR_KUBESPRAY);
        terramanCommandModel.setHost(TEST_HOST);
        terramanCommandModel.setIdRsa(TEST_IDRSA);
        terramanCommandModel.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel.setClusterId(TEST_CLUSTER_ID);

        terramanCommandModel2.setCommand(TEST_COMMAND_NUMBER[7]);
        terramanCommandModel2.setDir(TerramanConstant.MOVE_DIR_KUBESPRAY);
        terramanCommandModel2.setHost(TEST_HOST);
        terramanCommandModel2.setIdRsa(TEST_IDRSA);
        terramanCommandModel2.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel2.setClusterId(TEST_CLUSTER_ID);

        terramanCommandModel3.setCommand(TEST_COMMAND_NUMBER[8]);
        terramanCommandModel3.setDir(TerramanConstant.MOVE_DIR_KUBESPRAY);
        terramanCommandModel3.setHost(TEST_HOST);
        terramanCommandModel3.setIdRsa(TEST_IDRSA);
        terramanCommandModel3.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel3.setClusterId(TEST_CLUSTER_ID);

        when(commandService.execCommandOutput(terramanCommandModel)).thenReturn(TEST_STRING);
        when(commandService.execCommandOutput(terramanCommandModel2)).thenReturn(Constants.RESULT_STATUS_SUCCESS);
        when(commandService.execCommandOutput(terramanCommandModel3)).thenReturn(Constants.RESULT_STATUS_SUCCESS);

        int result = terramanProcessService.terramanProcessExecKubespray(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_HOST, TEST_IDRSA);

        assertEquals(1, result);
    }

    @Test
    public void terramanProcessExecKubesprayModErrorTest() {
        TerramanCommandModel terramanCommandModel = new TerramanCommandModel();
        TerramanCommandModel terramanCommandModel2 = new TerramanCommandModel();

        terramanCommandModel.setCommand(TEST_COMMAND_NUMBER[1]);
        terramanCommandModel.setDir(TerramanConstant.MOVE_DIR_KUBESPRAY);
        terramanCommandModel.setHost(TEST_HOST);
        terramanCommandModel.setIdRsa(TEST_IDRSA);
        terramanCommandModel.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel.setClusterId(TEST_CLUSTER_ID);

        terramanCommandModel2.setCommand(TEST_COMMAND_NUMBER[7]);
        terramanCommandModel2.setDir(TerramanConstant.MOVE_DIR_KUBESPRAY);
        terramanCommandModel2.setHost(TEST_HOST);
        terramanCommandModel2.setIdRsa(TEST_IDRSA);
        terramanCommandModel2.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel2.setClusterId(TEST_CLUSTER_ID);

        when(commandService.execCommandOutput(terramanCommandModel)).thenReturn(TEST_STRING);
        when(commandService.execCommandOutput(terramanCommandModel2)).thenReturn(Constants.RESULT_STATUS_FAIL);

        doNothing().when(clusterLogService).saveClusterLog(TEST_CLUSTER_ID, TEST_MP_SEQ, TerramanConstant.TERRAFORM_CHANGE_MODE_ERROR);
        doReturn(clusterModelMock).when(clusterService).updateCluster(TEST_CLUSTER_ID, TerramanConstant.CLUSTER_FAIL_STATUS);

        int result = terramanProcessService.terramanProcessExecKubespray(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_HOST, TEST_IDRSA);

        assertEquals(-1, result);
    }

    @Test
    public void terramanProcessExecKubesprayDeployErrorTest() {
        TerramanCommandModel terramanCommandModel = new TerramanCommandModel();
        TerramanCommandModel terramanCommandModel2 = new TerramanCommandModel();
        TerramanCommandModel terramanCommandModel3 = new TerramanCommandModel();

        terramanCommandModel.setCommand(TEST_COMMAND_NUMBER[1]);
        terramanCommandModel.setDir(TerramanConstant.MOVE_DIR_KUBESPRAY);
        terramanCommandModel.setHost(TEST_HOST);
        terramanCommandModel.setIdRsa(TEST_IDRSA);
        terramanCommandModel.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel.setClusterId(TEST_CLUSTER_ID);

        terramanCommandModel2.setCommand(TEST_COMMAND_NUMBER[7]);
        terramanCommandModel2.setDir(TerramanConstant.MOVE_DIR_KUBESPRAY);
        terramanCommandModel2.setHost(TEST_HOST);
        terramanCommandModel2.setIdRsa(TEST_IDRSA);
        terramanCommandModel2.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel2.setClusterId(TEST_CLUSTER_ID);

        terramanCommandModel3.setCommand(TEST_COMMAND_NUMBER[8]);
        terramanCommandModel3.setDir(TerramanConstant.MOVE_DIR_KUBESPRAY);
        terramanCommandModel3.setHost(TEST_HOST);
        terramanCommandModel3.setIdRsa(TEST_IDRSA);
        terramanCommandModel3.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel3.setClusterId(TEST_CLUSTER_ID);

        when(commandService.execCommandOutput(terramanCommandModel)).thenReturn(TEST_STRING);
        when(commandService.execCommandOutput(terramanCommandModel2)).thenReturn(Constants.RESULT_STATUS_SUCCESS);
        when(commandService.execCommandOutput(terramanCommandModel3)).thenReturn(Constants.RESULT_STATUS_FAIL);

        doNothing().when(clusterLogService).saveClusterLog(TEST_CLUSTER_ID, TEST_INT_SEQ, TerramanConstant.TERRAFORM_DEPLOY_CLUSTER_ERROR);
        doReturn(clusterModelMock).when(clusterService).updateCluster(TEST_CLUSTER_ID, TerramanConstant.CLUSTER_FAIL_STATUS);

        int result = terramanProcessService.terramanProcessExecKubespray(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_HOST, TEST_IDRSA);

        assertEquals(-1, result);
    }

    @Test
    public void terramanProcessCreateVaultTest() {
        TerramanCommandModel terramanCommandModel = new TerramanCommandModel();
        TerramanCommandModel terramanCommandModel2 = new TerramanCommandModel();
        TerramanCommandModel terramanCommandModel3 = new TerramanCommandModel();
        TerramanCommandModel terramanCommandModel4 = new TerramanCommandModel();

        terramanCommandModel.setCommand(TEST_COMMAND_NUMBER[9]);
        terramanCommandModel.setHost(gInstanceModel.getPublicIp());
        terramanCommandModel.setIdRsa(TerramanConstant.CLUSTER_PRIVATE_KEY(TEST_CLUSTER_NAME));
        terramanCommandModel.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel.setClusterId(TEST_CLUSTER_ID);

        terramanCommandModel2.setCommand(TEST_COMMAND_NUMBER[10]);
        terramanCommandModel2.setHost(gInstanceModel.getPublicIp());
        terramanCommandModel2.setIdRsa(TerramanConstant.CLUSTER_PRIVATE_KEY(TEST_CLUSTER_NAME));
        terramanCommandModel2.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel2.setClusterId(TEST_CLUSTER_ID);

//        terramanCommandModel3.setCommand(TEST_COMMAND_NUMBER[11]);
//        terramanCommandModel3.setHost(gInstanceModel.getPublicIp());
//        terramanCommandModel3.setIdRsa(TerramanConstant.CLUSTER_PRIVATE_KEY(TEST_CLUSTER_NAME));
//        terramanCommandModel3.setUserName(TerramanConstant.DEFAULT_USER_NAME);
//        terramanCommandModel3.setClusterId(TEST_CLUSTER_ID);

        terramanCommandModel4.setCommand(TEST_COMMAND_NUMBER[12]);
        terramanCommandModel4.setHost(gInstanceModel.getPublicIp());
        terramanCommandModel4.setIdRsa(TerramanConstant.CLUSTER_PRIVATE_KEY(TEST_CLUSTER_NAME));
        terramanCommandModel4.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel4.setClusterId(TEST_CLUSTER_ID);

        when(instanceService.getInstance(TEST_CLUSTER_ID, TEST_PROVIDER, TEST_HOST, TEST_IDRSA, TEST_PROCESS_GB)).thenReturn(gInstanceModel);
        when(commandService.execCommandOutput(terramanCommandModel)).thenReturn(Constants.RESULT_STATUS_SUCCESS);
        when(commandService.execCommandOutput(terramanCommandModel2)).thenReturn(Constants.RESULT_STATUS_SUCCESS);
//        when(commandService.execCommandOutput(terramanCommandModel3)).thenReturn(Constants.RESULT_STATUS_SUCCESS);
        when(commandService.execCommandOutput(terramanCommandModel4)).thenReturn(Constants.RESULT_STATUS_SUCCESS);
        when(propertyService.getVaultClusterApiUrl()).thenReturn(TEST_CLUSTER_API_URL);
        when(propertyService.getVaultClusterTokenPath()).thenReturn(TEST_CLUSTER_TOKEN);
        when(vaultService.write(TEST_CLUSTER_API_URL, clusterInfoMock)).thenReturn(gInstanceModel);

        doNothing().when(clusterLogService).saveClusterLog(TEST_CLUSTER_ID, TEST_INT_SEQ, TerramanConstant.TERRAFORM_CREATE_TOKEN_ERROR);
        doReturn(clusterModelMock).when(clusterService).updateCluster(TEST_CLUSTER_ID, TerramanConstant.CLUSTER_FAIL_STATUS);

        int result = terramanProcessService.terramanProcessCreateVault(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_PROCESS_GB, TEST_HOST, TEST_IDRSA, TEST_PROVIDER, TEST_CLUSTER_NAME);

        assertEquals(-1, result);
    }

    @Test
    public void terramanProcessCreateVaulCreateErrortTest() {
        TerramanCommandModel terramanCommandModel = new TerramanCommandModel();

        terramanCommandModel.setCommand(TEST_COMMAND_NUMBER[9]);
        terramanCommandModel.setHost(gInstanceModel.getPublicIp());
        terramanCommandModel.setIdRsa(TerramanConstant.CLUSTER_PRIVATE_KEY(TEST_CLUSTER_NAME));
        terramanCommandModel.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel.setClusterId(TEST_CLUSTER_ID);

        when(instanceService.getInstance(TEST_CLUSTER_ID, TEST_PROVIDER, TEST_HOST, TEST_IDRSA, TEST_PROCESS_GB)).thenReturn(gInstanceModel);
        when(commandService.execCommandOutput(terramanCommandModel)).thenReturn(Constants.RESULT_STATUS_FAIL);

        doNothing().when(clusterLogService).saveClusterLog(TEST_CLUSTER_ID, TEST_INT_SEQ, TerramanConstant.TERRAFORM_CREATE_SERVICE_ACCOUNT_ERROR);
        doReturn(clusterModelMock).when(clusterService).updateCluster(TEST_CLUSTER_ID, TerramanConstant.CLUSTER_FAIL_STATUS);

        int result = terramanProcessService.terramanProcessCreateVault(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_PROCESS_GB, TEST_HOST, TEST_IDRSA, TEST_PROVIDER, TEST_CLUSTER_NAME);

        assertEquals(-1, result);
    }

    @Test
    public void terramanProcessCreateVaultBindingErrorTest() {
        TerramanCommandModel terramanCommandModel = new TerramanCommandModel();
        TerramanCommandModel terramanCommandModel2 = new TerramanCommandModel();

        when(instanceService.getInstance(TEST_CLUSTER_ID, TEST_PROVIDER, TEST_HOST, TEST_IDRSA, TEST_PROCESS_GB)).thenReturn(gInstanceModel);

        terramanCommandModel.setCommand(TEST_COMMAND_NUMBER[9]);
        terramanCommandModel.setHost(gInstanceModel.getPublicIp());
        terramanCommandModel.setIdRsa(TerramanConstant.CLUSTER_PRIVATE_KEY(TEST_CLUSTER_NAME));
        terramanCommandModel.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel.setClusterId(TEST_CLUSTER_ID);
        when(commandService.execCommandOutput(terramanCommandModel)).thenReturn(Constants.RESULT_STATUS_SUCCESS);

        terramanCommandModel2.setCommand(TEST_COMMAND_NUMBER[10]);
        terramanCommandModel2.setHost(gInstanceModel.getPublicIp());
        terramanCommandModel2.setIdRsa(TerramanConstant.CLUSTER_PRIVATE_KEY(TEST_CLUSTER_NAME));
        terramanCommandModel2.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel2.setClusterId(TEST_CLUSTER_ID);
        when(commandService.execCommandOutput(terramanCommandModel2)).thenReturn(Constants.RESULT_STATUS_FAIL);

        doNothing().when(clusterLogService).saveClusterLog(TEST_CLUSTER_ID, TEST_INT_SEQ, TerramanConstant.TERRAFORM_BIND_ROLE_ERROR);
        doReturn(clusterModelMock).when(clusterService).updateCluster(TEST_CLUSTER_ID, TerramanConstant.CLUSTER_FAIL_STATUS);

        int result = terramanProcessService.terramanProcessCreateVault(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_PROCESS_GB, TEST_HOST, TEST_IDRSA, TEST_PROVIDER, TEST_CLUSTER_NAME);

        assertEquals(-1, result);
    }

    @Test
    public void terramanProcessCreateVaultSecretNameErrorTest() {
        TerramanCommandModel terramanCommandModel = new TerramanCommandModel();
        TerramanCommandModel terramanCommandModel2 = new TerramanCommandModel();

        when(instanceService.getInstance(TEST_CLUSTER_ID, TEST_PROVIDER, TEST_HOST, TEST_IDRSA, TEST_PROCESS_GB)).thenReturn(gInstanceModel);

        terramanCommandModel.setCommand(TEST_COMMAND_NUMBER[9]);
        terramanCommandModel.setHost(gInstanceModel.getPublicIp());
        terramanCommandModel.setIdRsa(TerramanConstant.CLUSTER_PRIVATE_KEY(TEST_CLUSTER_NAME));
        terramanCommandModel.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel.setClusterId(TEST_CLUSTER_ID);
        when(commandService.execCommandOutput(terramanCommandModel)).thenReturn(Constants.RESULT_STATUS_SUCCESS);

        terramanCommandModel2.setCommand(TEST_COMMAND_NUMBER[10]);
        terramanCommandModel2.setHost(gInstanceModel.getPublicIp());
        terramanCommandModel2.setIdRsa(TerramanConstant.CLUSTER_PRIVATE_KEY(TEST_CLUSTER_NAME));
        terramanCommandModel2.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel2.setClusterId(TEST_CLUSTER_ID);
        when(commandService.execCommandOutput(terramanCommandModel2)).thenReturn(Constants.RESULT_STATUS_FAIL);

//        terramanCommandModel.setCommand(TEST_COMMAND_NUMBER[11]);
//        when(commandService.execCommandOutput(terramanCommandModel)).thenReturn(Constants.RESULT_STATUS_FAIL);

        doNothing().when(clusterLogService).saveClusterLog(TEST_CLUSTER_ID, TEST_INT_SEQ, TerramanConstant.TERRAFORM_GET_SECRET_NAME_ERROR);
        doReturn(clusterModelMock).when(clusterService).updateCluster(TEST_CLUSTER_ID, TerramanConstant.CLUSTER_FAIL_STATUS);

        int result = terramanProcessService.terramanProcessCreateVault(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_PROCESS_GB, TEST_HOST, TEST_IDRSA, TEST_PROVIDER, TEST_CLUSTER_NAME);

        assertEquals(-1, result);
    }

    @Test
    public void terramanProcessCreateVaultTokenErrorTest() {
        TerramanCommandModel terramanCommandModel = new TerramanCommandModel();
        TerramanCommandModel terramanCommandModel2 = new TerramanCommandModel();
        TerramanCommandModel terramanCommandModel3 = new TerramanCommandModel();

        when(instanceService.getInstance(TEST_CLUSTER_ID, TEST_PROVIDER, TEST_HOST, TEST_IDRSA, TEST_PROCESS_GB)).thenReturn(gInstanceModel);

        terramanCommandModel.setCommand(TEST_COMMAND_NUMBER[9]);
        terramanCommandModel.setHost(gInstanceModel.getPublicIp());
        terramanCommandModel.setIdRsa(TerramanConstant.CLUSTER_PRIVATE_KEY(TEST_CLUSTER_NAME));
        terramanCommandModel.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel.setClusterId(TEST_CLUSTER_ID);
        when(commandService.execCommandOutput(terramanCommandModel)).thenReturn(Constants.RESULT_STATUS_SUCCESS);

        terramanCommandModel2.setCommand(TEST_COMMAND_NUMBER[10]);
        terramanCommandModel2.setHost(gInstanceModel.getPublicIp());
        terramanCommandModel2.setIdRsa(TerramanConstant.CLUSTER_PRIVATE_KEY(TEST_CLUSTER_NAME));
        terramanCommandModel2.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel2.setClusterId(TEST_CLUSTER_ID);
        when(commandService.execCommandOutput(terramanCommandModel2)).thenReturn(Constants.RESULT_STATUS_SUCCESS);

//        terramanCommandModel.setCommand(TEST_COMMAND_NUMBER[11]);
//        when(commandService.execCommandOutput(terramanCommandModel)).thenReturn(Constants.RESULT_STATUS_SUCCESS);

        terramanCommandModel3.setCommand(TEST_COMMAND_NUMBER[12]);
        terramanCommandModel3.setHost(gInstanceModel.getPublicIp());
        terramanCommandModel3.setIdRsa(TerramanConstant.CLUSTER_PRIVATE_KEY(TEST_CLUSTER_NAME));
        terramanCommandModel3.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel3.setClusterId(TEST_CLUSTER_ID);
        when(commandService.execCommandOutput(terramanCommandModel3)).thenReturn(Constants.RESULT_STATUS_FAIL);

        doNothing().when(clusterLogService).saveClusterLog(TEST_CLUSTER_ID, TEST_INT_SEQ, TerramanConstant.TERRAFORM_GET_CLUSTER_TOKEN_ERROR);
        doReturn(clusterModelMock).when(clusterService).updateCluster(TEST_CLUSTER_ID, TerramanConstant.CLUSTER_FAIL_STATUS);

        int result = terramanProcessService.terramanProcessCreateVault(TEST_MP_SEQ, TEST_CLUSTER_ID, TEST_PROCESS_GB, TEST_HOST, TEST_IDRSA, TEST_PROVIDER, TEST_CLUSTER_NAME);

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
