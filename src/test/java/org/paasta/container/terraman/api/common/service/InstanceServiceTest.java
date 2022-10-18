package org.paasta.container.terraman.api.common.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.paasta.container.terraman.api.common.constants.TerramanConstant;
import org.paasta.container.terraman.api.common.model.InstanceModel;
import org.paasta.container.terraman.api.common.util.CommonFileUtils;
import org.paasta.container.terraman.api.terraman.TerramanProcessService;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;


@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class InstanceServiceTest {

    private static final String TEST_CLUSTER_ID = "testClusterId";
    private static final String TEST_HOST = "testHost";
    private static final String TEST_ID_RSA = "testIdRsa";
    private static final String TEST_PROVIDER = "testProvider";
    private static final String TEST_PROCESS_GB = "testProcessGb";

    private static final String TEST_AWS = "AWS";
    private static final String TEST_OPENSTCK = "OPENSTACK";
    private static final String TEST_GCP = "GCP";
    private static final String TEST_VSPHERE = "VSPHERE";

    private static final String TEST_RESOURCE_NAME = "testResourceName";
    private static final String TEST_INSTANCE_NAME = "testInstanceName";
    private static final String TEST_PRIVATE_IP = "testPrivateIp";
    private static final String TEST_PUBLIC_IP = "testPublicIp";

    private static final String TEST_IP_ADDR = "1.1.1.1";
    private static final String TEST_INSTANCE_ID = "12561528-a68c-435f-ab8e-deffb350e238";

    private static final String TEST_FLOATING_IP = "203.255.255.115";
    private static final String TEST_CH_PRIVATE_IP = "ip-1-1-1-1";

    private static final String TEST_FILE_NAME = "fileName";
    private static final String TEST_CONTAINER = "CONTAINER";

    private InstanceModel instanceModel;
    private List<InstanceModel> instancesModel;
    private JsonObject readStateFile;
    private static JsonObject jsonObject;

    @Mock
    private CommonService commonService;
    @Mock
    private CommandService commandService;
    @Mock
    private ClusterLogService clusterLogService;
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
    @Mock
    private InstanceService instanceService;

    @InjectMocks
    private InstanceService instanceServiceMock;

    @Before
    public void setUp() {
        instanceModel = new InstanceModel(TEST_RESOURCE_NAME, TEST_INSTANCE_NAME, TEST_PRIVATE_IP, TEST_PUBLIC_IP);
        instancesModel = new ArrayList<>();
        instancesModel.add(instanceModel);

        readStateFile = new JsonObject();
        jsonObject = new JsonObject();
    }

    @Test
    public void getInstanceTestOfDefault() {
        InstanceModel result = instanceServiceMock.getInstance(TEST_CLUSTER_ID, TEST_PROVIDER, TEST_HOST, TEST_ID_RSA, TEST_CONTAINER);
    }

    @Test
    public void getInstanceTestOfAws() {
        when(instanceService.getInstanceInfoAws(TEST_CLUSTER_ID, TEST_HOST, TEST_ID_RSA, TEST_PROCESS_GB)).thenReturn(instanceModel);

        InstanceModel result = instanceServiceMock.getInstance(TEST_CLUSTER_ID, TEST_AWS, TEST_HOST, TEST_ID_RSA, TEST_CONTAINER);

//        assertEquals(null, result);
    }

    @Test
    public void getInstanceTestOfOpenstack() {
        when(instanceService.getInstanceInfoOpenstack(TEST_CLUSTER_ID, TEST_HOST, TEST_ID_RSA, TEST_PROCESS_GB)).thenReturn(instanceModel);

        InstanceModel result = instanceServiceMock.getInstance(TEST_CLUSTER_ID, TEST_OPENSTCK, TEST_HOST, TEST_ID_RSA, TEST_CONTAINER);
//        assertEquals(null, result);
    }

    @Test
    public void getInstanceTestOfGcp() {
        when(instanceService.getInstanceInfoGcp()).thenReturn(instanceModel);

        InstanceModel result = instanceServiceMock.getInstance(TEST_CLUSTER_ID, TEST_GCP, TEST_HOST, TEST_ID_RSA, TEST_CONTAINER);
//        assertEquals(null, result);
    }

    @Test
    public void getInstanceTestOfVsphere() {
        when(instanceService.getInstanceInfoVSphere()).thenReturn(instanceModel);

        InstanceModel result = instanceServiceMock.getInstance(TEST_CLUSTER_ID, TEST_VSPHERE, TEST_HOST, TEST_ID_RSA, TEST_CONTAINER);
//        assertEquals(null, result);
    }

    @Test
    public void getInstancesTestOfDefault() {
        List<InstanceModel> result = instanceServiceMock.getInstances(TEST_CLUSTER_ID, TEST_PROVIDER, TEST_HOST, TEST_ID_RSA, TEST_PROCESS_GB);
    }

    @Test
    public void getInstancesTestOfAws() {
        when(instanceService.getInstancesInfoAws(TEST_CLUSTER_ID, TEST_HOST, TEST_ID_RSA, TEST_PROCESS_GB)).thenReturn(instancesModel);

        List<InstanceModel> result = instanceServiceMock.getInstances(TEST_CLUSTER_ID, TEST_AWS, TEST_HOST, TEST_ID_RSA, TEST_PROCESS_GB);
    }

    @Test
    public void getInstancesTestOfOpenstack() {
        when(instanceService.getInstancesInfoGcp()).thenReturn(instancesModel);

        List<InstanceModel> result = instanceServiceMock.getInstances(TEST_CLUSTER_ID, TEST_OPENSTCK, TEST_HOST, TEST_ID_RSA, TEST_PROCESS_GB);
    }

    @Test
    public void getInstancesTestOfGcp() {
        when(instanceService.getInstancesInfoAws(TEST_CLUSTER_ID, TEST_HOST, TEST_ID_RSA, TEST_PROCESS_GB)).thenReturn(instancesModel);
        when(instanceService.getInstancesInfoGcp()).thenReturn(instancesModel);
        when(instanceService.getInstancesInfoVSphere()).thenReturn(instancesModel);
        when(instanceService.getInstancesInfoOpenstack(TEST_CLUSTER_ID, TEST_HOST, TEST_ID_RSA, TEST_PROCESS_GB)).thenReturn(instancesModel);

        List<InstanceModel> result = instanceServiceMock.getInstances(TEST_CLUSTER_ID, TEST_GCP, TEST_HOST, TEST_ID_RSA, TEST_PROCESS_GB);
    }

    @Test
    public void getInstancesTestOfVsphere() {
        when(instanceService.getInstancesInfoAws(TEST_CLUSTER_ID, TEST_HOST, TEST_ID_RSA, TEST_PROCESS_GB)).thenReturn(instancesModel);
        when(instanceService.getInstancesInfoGcp()).thenReturn(instancesModel);
        when(instanceService.getInstancesInfoVSphere()).thenReturn(instancesModel);
        when(instanceService.getInstancesInfoOpenstack(TEST_CLUSTER_ID, TEST_HOST, TEST_ID_RSA, TEST_PROCESS_GB)).thenReturn(instancesModel);

        List<InstanceModel> result = instanceServiceMock.getInstances(TEST_CLUSTER_ID, TEST_VSPHERE, TEST_HOST, TEST_ID_RSA, TEST_PROCESS_GB);
    }

    @Test
    public void getInstanceInfoAwsTest() {
        doNothing().when(commandService).sshFileDownload(TerramanConstant.CLUSTER_STATE_DIR(TEST_CLUSTER_ID), TerramanConstant.TERRAFORM_STATE_FILE_PATH(TerramanConstant.CLUSTER_STATE_DIR(TEST_CLUSTER_ID)), TerramanConstant.TERRAFORM_STATE_FILE_NAME, TEST_HOST, TEST_ID_RSA);
        when(instanceService.readStateFile(TEST_CLUSTER_ID, TEST_PROCESS_GB)).thenReturn(jsonObject);

        InstanceModel result = instanceServiceMock.getInstanceInfoAws(TEST_CLUSTER_ID, TEST_HOST, TEST_ID_RSA, TEST_CONTAINER);
    }

    @Test
    public void getInstanceInfoOpenstackTest() {
        doNothing().when(commandService).sshFileDownload(TerramanConstant.CLUSTER_STATE_DIR(TEST_CLUSTER_ID), TerramanConstant.TERRAFORM_STATE_FILE_PATH(TerramanConstant.CLUSTER_STATE_DIR(TEST_CLUSTER_ID)), TerramanConstant.TERRAFORM_STATE_FILE_NAME, TEST_HOST, TEST_ID_RSA);
        when(instanceService.readStateFile(TEST_CLUSTER_ID, TEST_PROCESS_GB)).thenReturn(jsonObject);

        InstanceModel result = instanceServiceMock.getInstanceInfoOpenstack(TEST_CLUSTER_ID, TEST_HOST, TEST_ID_RSA, TEST_CONTAINER);
    }

    @Test
    public void getInstanceInfoGcpTest() {
        InstanceModel result = instanceServiceMock.getInstanceInfoGcp();
    }

    @Test
    public void getInstanceInfoVsphereTest() {
        InstanceModel result = instanceServiceMock.getInstanceInfoVSphere();
    }

    @Test
    public void getInstancesInfoAwsTest() {
        doNothing().when(commandService).sshFileDownload(TerramanConstant.CLUSTER_STATE_DIR(TEST_CLUSTER_ID), TerramanConstant.TERRAFORM_STATE_FILE_PATH(TerramanConstant.CLUSTER_STATE_DIR(TEST_CLUSTER_ID)), TerramanConstant.TERRAFORM_STATE_FILE_NAME, TEST_HOST, TEST_ID_RSA);
        when(instanceService.readStateFile(TEST_CLUSTER_ID, TEST_PROCESS_GB)).thenReturn(jsonObject);

        List<InstanceModel> result = instanceServiceMock.getInstancesInfoAws(TEST_CLUSTER_ID, TEST_HOST, TEST_ID_RSA, TEST_CONTAINER);
    }

    @Test
    public void getInstancesInfoOpenstackTest() {
        doNothing().when(commandService).sshFileDownload(TerramanConstant.CLUSTER_STATE_DIR(TEST_CLUSTER_ID), TerramanConstant.TERRAFORM_STATE_FILE_PATH(TerramanConstant.CLUSTER_STATE_DIR(TEST_CLUSTER_ID)), TerramanConstant.TERRAFORM_STATE_FILE_NAME, TEST_HOST, TEST_ID_RSA);
        when(instanceService.readStateFile(TEST_CLUSTER_ID, TEST_PROCESS_GB)).thenReturn(jsonObject);

        List<InstanceModel> result = instanceServiceMock.getInstancesInfoOpenstack(TEST_CLUSTER_ID, TEST_HOST, TEST_ID_RSA, TEST_CONTAINER);
    }

    @Test
    public void getInstancesInfoGcpTest() {
        List<InstanceModel> result = instanceServiceMock.getInstancesInfoGcp();
    }

    @Test
    public void getInstancesInfoVsphereTest() {
        List<InstanceModel> result = instanceServiceMock.getInstancesInfoVSphere();
    }

    @Test
    public void readStateFileTest() {
        when(commonFileUtils.fileRead(TEST_FILE_NAME)).thenReturn(readStateFile);

        JsonObject result = instanceServiceMock.readStateFile(TEST_CLUSTER_ID, TEST_PROCESS_GB);
    }

    @Test
    public void getPublicIpTest() {
        String result = instanceServiceMock.getPublicIp(TEST_INSTANCE_ID, jsonObject, TEST_PRIVATE_IP);

        assertEquals(TEST_PRIVATE_IP, result);
    }

    @Test
    public void getAWSHostNameTest() {
        String result = instanceServiceMock.getAWSHostName(TEST_IP_ADDR);

        assertEquals(TEST_CH_PRIVATE_IP, result);
    }
}
