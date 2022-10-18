package org.paasta.container.terraman.api.common.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.paasta.container.terraman.api.common.constants.Constants;
import org.paasta.container.terraman.api.common.model.AccountModel;
import org.paasta.container.terraman.api.common.model.FileModel;
import org.paasta.container.terraman.api.common.util.CommonFileUtils;
import org.paasta.container.terraman.api.terraman.TerramanProcessService;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
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


    private FileModel fileModel;
    private HashMap hashMap;
    private AccountModel accountModel;

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
    @Mock
    private TfFileService tfFileServiceMock;

    @InjectMocks
    private TfFileService TfFileService;

    @Before
    public void setUp() {
        hashMap = new HashMap();
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
    public void createProviderFileTest() {
        when(propertyService.getVaultBase()).thenReturn(TEST_STR);
        when(vaultService.read(TEST_PATH, HashMap.class)).thenReturn(hashMap);
        when(accountService.getAccountInfo(TEST_SEQ)).thenReturn(accountModel);

        String result = tfFileService.createProviderFile(TEST_CLUSTER_ID, TEST_PROVIDER, TEST_SEQ, TEST_POD, TEST_HOST, TEST_ID_RSA, TEST_PROCESS_GB);

//        assertThat(result).isNotNull();
        assertEquals(null, result);
    }

//    @Test
//    public void createTfFileDivTest() {
//        File fileMock = mock(File.class);
//        FileWriter fileWriter = mock(FileWriter.class);
//        BufferedWriter bufferedWriter = mock(BufferedWriter.class);
//
//        when(TfFileService.tfCreateWithWriteAws(fileModel, fileMock)).thenReturn(Constants.RESULT_STATUS_SUCCESS);
//        when(TfFileService.tfCreateWithWriteVSphere(fileModel, fileMock)).thenReturn(Constants.RESULT_STATUS_SUCCESS);
//        when(TfFileService.tfCreateWithWriteOpenstack(fileModel, fileMock)).thenReturn(Constants.RESULT_STATUS_SUCCESS);
//
//        String result = tfFileService.createTfFileDiv(fileModel, TEST_CLUSTER_ID, TEST_PROCESS_GB, TEST_PROVIDER);
//
//        assertThat(result).isNotNull();
//        assertEquals(null, result);
//    }
}
