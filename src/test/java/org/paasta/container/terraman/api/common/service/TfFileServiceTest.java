package org.paasta.container.terraman.api.common.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.paasta.container.terraman.api.common.CommonService;
import org.paasta.container.terraman.api.common.PropertyService;
import org.paasta.container.terraman.api.common.VaultService;
import org.paasta.container.terraman.api.common.constants.Constants;
import org.paasta.container.terraman.api.common.model.AccountModel;
import org.paasta.container.terraman.api.common.model.FileModel;
import org.paasta.container.terraman.api.common.util.CommonFileUtils;
import org.paasta.container.terraman.api.common.util.TerramanFileUtils;
import org.paasta.container.terraman.api.terraman.TerramanProcessService;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class TfFileServiceTest {

    private static final String TEST_CLUSTER_ID = "testClusterId";
    private static final String TEST_PROVIDER = "testProvider";
    private static final int TEST_SEQ = 13;
    private static final String TEST_POD = "testPod";
    private static final String TEST_HOST = "testHost";
    private static final String TEST_ID_RSA = "testIdRsa";
    private static final String TEST_PROCESS_GB = "testProcessGb";

    private static final String TEST_STR = "test";
    private static final String TEST_PATH = "path";

    private static final String TEST_AWS = "AWS";
    private static final String TEST_OPENSTACK = "OPENSTACK";
    private static final String TEST_GCP = "GCP";
    private static final String TEST_VSPHERE = "VSPHERE";

    private static File fileMock;
    private static FileModel fileModel = null;
    private static HashMap<String, Object> hashMap = null;
    private static AccountModel accountModel = null;

    @Mock
    private TerramanFileUtils terramanFileUtils;
    @Mock
    private PropertyService propertyService;
    @Mock
    private VaultService vaultService;
    @Mock
    private AccountService accountService;
    @Mock
    private TfFileService tfFileServiceMock;

    @InjectMocks
    private TfFileService tfFileService;

    @Before
    public void setUp() {

        fileMock = mock(File.class);

        hashMap = new HashMap();
        hashMap.put("test","test");
        accountModel = new AccountModel();
        accountModel.setId(1);
        accountModel.setName("testAccount");
        accountModel.setProject("testProject");
        accountModel.setProvider("testProvider");
        accountModel.setRegion("testRegion");

        fileModel = new FileModel();
        // AWS
        fileModel.setAwsAccessKey("testAccessKey");
        fileModel.setAwsRegion("testRegion");
        fileModel.setAwsSecretKey("testSecretKey");

        // OPENSTACK
        fileModel.setOpenstackAuthUrl("testUrl");
        fileModel.setOpenstackPassword("password");
        fileModel.setOpenstackRegion("testRegion");
        fileModel.setOpenstackTenantName("testTenantName");
        fileModel.setOpenstackUserName("testUserName");

        // VSphere
        fileModel.setVSpherePassword("testPassword");
        fileModel.setVSphereServer("testServer");
        fileModel.setVSphereUser("testUser");



    }

    @Test
    public void createProviderFileDefaultTest() {
        HashMap response = new HashMap();
        response.put("default", "testDefault");

        when(propertyService.getVaultBase()).thenReturn(TEST_STR);
        doReturn(response).when(vaultService).read(TEST_PATH, HashMap.class);
        when(accountService.getAccountInfo(TEST_SEQ)).thenReturn(accountModel);
        when(terramanFileUtils.createTfFileDiv(fileModel, TEST_CLUSTER_ID, TEST_PROCESS_GB, TEST_PROVIDER)).thenReturn(Constants.RESULT_STATUS_SUCCESS);

        String result = tfFileService.createProviderFile(TEST_CLUSTER_ID, TEST_PROVIDER, TEST_SEQ, TEST_POD, TEST_HOST, TEST_ID_RSA, TEST_PROCESS_GB);

        assertEquals(Constants.RESULT_STATUS_FAIL, result);
    }

    @Test
    public void createProviderAwsFileTest() {
        HashMap response = new HashMap();
        response.put("access_key", "awsAccessKey");
        response.put("secret_key", "awsSecretKey");

        when(propertyService.getVaultBase()).thenReturn(TEST_STR);
        doReturn(response).when(vaultService).read(TEST_PATH, HashMap.class);
        when(accountService.getAccountInfo(TEST_SEQ)).thenReturn(accountModel);
        when(terramanFileUtils.createTfFileDiv(fileModel, TEST_CLUSTER_ID, TEST_PROCESS_GB, TEST_PROVIDER)).thenReturn(TEST_STR);

        String result = tfFileService.createProviderFile(TEST_CLUSTER_ID, TEST_AWS, TEST_SEQ, TEST_POD, TEST_HOST, TEST_ID_RSA, TEST_PROCESS_GB);

        assertEquals(Constants.RESULT_STATUS_FAIL, result);
    }

    @Test
    public void createProviderFileOpenstackTest() {
        HashMap response = new HashMap();
        response.put("password", "openstackPassword");
        response.put("auth_url", "openstackAuthUrl");
        response.put("user_name", "openstackUserName");

        when(propertyService.getVaultBase()).thenReturn(TEST_STR);
        doReturn(response).when(vaultService).read(TEST_PATH, HashMap.class);
        when(accountService.getAccountInfo(TEST_SEQ)).thenReturn(accountModel);
        when(terramanFileUtils.createTfFileDiv(fileModel, TEST_CLUSTER_ID, TEST_PROCESS_GB, TEST_PROVIDER)).thenReturn(TEST_STR);

        String result = tfFileService.createProviderFile(TEST_CLUSTER_ID, TEST_OPENSTACK, TEST_SEQ, TEST_POD, TEST_HOST, TEST_ID_RSA, TEST_PROCESS_GB);

        assertEquals(Constants.RESULT_STATUS_FAIL, result);
    }

    @Test
    public void createProviderFileVsphereTest() {
        HashMap response = new HashMap();
        response.put("uesr", "vsphereUser");
        response.put("password", "vspherePassword");
        response.put("vsphere_server", "vsphereVsphereServer");

        when(propertyService.getVaultBase()).thenReturn(TEST_STR);
        doReturn(response).when(vaultService).read(TEST_PATH, HashMap.class);
        when(accountService.getAccountInfo(TEST_SEQ)).thenReturn(accountModel);
        when(terramanFileUtils.createTfFileDiv(fileModel, TEST_CLUSTER_ID, TEST_PROCESS_GB, TEST_PROVIDER)).thenReturn(TEST_STR);

        String result = tfFileService.createProviderFile(TEST_CLUSTER_ID, TEST_VSPHERE, TEST_SEQ, TEST_POD, TEST_HOST, TEST_ID_RSA, TEST_PROCESS_GB);

        assertEquals(Constants.RESULT_STATUS_FAIL, result);
    }

    @Test
    public void createProviderFileGcpTest() {
        HashMap response = new HashMap();
        response.put("gcp", "gcpTest");

        when(propertyService.getVaultBase()).thenReturn(TEST_STR);
        doReturn(response).when(vaultService).read(TEST_PATH, HashMap.class);
        when(accountService.getAccountInfo(TEST_SEQ)).thenReturn(accountModel);
        when(terramanFileUtils.createTfFileDiv(fileModel, TEST_CLUSTER_ID, TEST_PROCESS_GB, TEST_PROVIDER)).thenReturn(TEST_STR);

        String result = tfFileService.createProviderFile(TEST_CLUSTER_ID, TEST_GCP, TEST_SEQ, TEST_POD, TEST_HOST, TEST_ID_RSA, TEST_PROCESS_GB);

        assertEquals(Constants.RESULT_STATUS_FAIL, result);
    }
}
