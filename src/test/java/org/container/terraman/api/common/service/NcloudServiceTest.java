package org.container.terraman.api.common.service;

import com.google.gson.JsonObject;
import org.container.terraman.api.common.CommonService;
import org.container.terraman.api.common.PropertyService;
import org.container.terraman.api.common.VaultService;
import org.container.terraman.api.common.constants.TerramanConstant;
import org.container.terraman.api.common.model.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class NcloudServiceTest {
    private static final String TEST_CLUSTER_ID = "testClusterId";
    private static final String TEST_STATUS = "testStatus";
    private static final String TEST_POD = "testPod";
    private static final String TEST_HOST = "testHost";
    private static final String TEST_ID_RSA = "testIdRsa";
    private static final String TEST_PROVIDER = "testProvider";
    private static final String TEST_PROCESS_GB = "testProcessGb";
    private static final String TEST_PRIVATE_KEY = "testPrivateKey";
    private static final String TEST_INSTANCE_NO = "1978956";
    private static final String TEST_SITE = "TestSite";
    private static final String TEST_REGION = "testRegion";
    private static final Object TEST_OBJECT = "testObject";
    private static final String TEST_SERVER_INSTANCE_NO = "1978956";
    private static final String TEST_RESOURCE_NAME = "testResourceName";
    private static final String TEST_INSTANCE_NAME = "testInstanceName";
    private static final String TEST_PRIVATE_IP = "testPrivateIp";
    private static final String TEST_PUBLIC_IP = "testPublicIp";
    private static final String TEST_ROOT_PASSWORD = "u8kfor!LDD&3od";
    private static final int TEST_SEQ = 13;
    private static final String TEST_STR = "test";
    private static final String TEST_PATH = "path";
    private static final String TEST_API_URL = "testApiUrl";
    private static final String TEST_HTTP_METHOD = "testHttpMethod";
    private static final String TEST_ACCESS_KEY = "testAccessKey";
    private static final String TEST_SECRET_KEY = "testSeccessKey";
    private NcloudPrivateKeyModel ncloudPrivateKeyModel;
    private NcloudPrivateKeyModel ncloudPrivateKeyResultModel;
    private List<NcloudPrivateKeyModel> ncloudPrivateKeysModel;
    private List<NcloudPrivateKeyModel> ncloudPrivateKeysResultModel;
    private NcloudInstanceKeyModel ncloudInstanceKeyModel;
    private NcloudInstanceKeyModel ncloudInstanceKeyResultModel;
    private List<NcloudInstanceKeyModel> ncloudInstanceKeysModel;
    private List<NcloudInstanceKeyModel> ncloudInstanceKeysResultModel;
    private NcloudInstanceKeyInfoModel ncloudInstanceKeyInfoModel;
    private NcloudInstanceKeyInfoModel ncloudInstanceKeyInfoResultModel;
    private List<NcloudInstanceKeyInfoModel> ncloudInstanceKeysInfoModel;
    private List<NcloudInstanceKeyInfoModel> ncloudInstanceKeysInfoResultModel;

    private static File fileMock;
    private static FileModel fileModel = null;
    private static AccountModel accountModel = null;
    private static HashMap<String, Object> hashMap = null;
    private JsonObject readStateFile;
    private JsonObject jsonObject;
    private static TerramanCommandModel terramanCommandModel = null;

    @Mock
    private PropertyService propertyService;
    @Mock
    private VaultService vaultService;
    @Mock
    private AccountService accountService;
    @Mock
    private InstanceService instanceService;
    @Mock
    private CommonService commonService;
    @Mock
    private CommandService commandService;
    @Mock
    private NcloudService ncloudService;
    @Mock
    private ClusterService clusterService;

    @InjectMocks
    private InstanceService instanceServiceMock;

    @Before
    public void setUp() {
        fileMock = mock(File.class);
        fileModel = new FileModel();

        hashMap = new HashMap();
        hashMap.put("test","test");

        ncloudPrivateKeyResultModel = new NcloudPrivateKeyModel("","","");
        ncloudPrivateKeysResultModel = new ArrayList<>();

        ncloudPrivateKeyModel = new NcloudPrivateKeyModel(TEST_INSTANCE_NO, TEST_PRIVATE_KEY, TEST_PUBLIC_IP);
        ncloudPrivateKeysModel = new ArrayList<>();
        ncloudPrivateKeysModel.add(ncloudPrivateKeyModel);

        ncloudInstanceKeyResultModel = new NcloudInstanceKeyModel("","");
        ncloudInstanceKeysResultModel = new ArrayList<>();

        ncloudInstanceKeyModel = new NcloudInstanceKeyModel(TEST_SERVER_INSTANCE_NO, TEST_ROOT_PASSWORD);
        ncloudInstanceKeysModel = new ArrayList<>();
        ncloudInstanceKeysModel.add(ncloudInstanceKeyModel);

        ncloudInstanceKeyInfoResultModel = new NcloudInstanceKeyInfoModel("","","","","");
        ncloudInstanceKeysInfoResultModel = new ArrayList<>();

        ncloudInstanceKeyInfoModel = new NcloudInstanceKeyInfoModel(TEST_INSTANCE_NO, TEST_SITE, TEST_REGION, TEST_PRIVATE_KEY, TEST_OBJECT);
        ncloudInstanceKeysInfoModel = new ArrayList<>();
        ncloudInstanceKeysInfoModel.add(ncloudInstanceKeyInfoModel);


        readStateFile = new JsonObject();
        jsonObject = new JsonObject();

        accountModel = new AccountModel();
        accountModel.setId(1);
        accountModel.setName("testAccount");
        accountModel.setProject("testProject");
        accountModel.setProvider("testProvider");
        accountModel.setRegion("testRegion");
        accountModel.setSite("testSite");

        // Ncloud
        fileModel.setNcloudAccessKey("testAccessKey");
        fileModel.setNcloudSecretKey("testSecretKey");
        fileModel.setNcloudRegion("testRegion");
        fileModel.setNcloudSite("testSite");

        terramanCommandModel = new TerramanCommandModel();
        terramanCommandModel.setCommand("14");
        terramanCommandModel.setHost(TEST_HOST);
        terramanCommandModel.setIdRsa(TEST_ID_RSA);
        terramanCommandModel.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel.setClusterId(TEST_CLUSTER_ID);
        terramanCommandModel.setPod(TEST_POD);

    }


    @Test
    public void getNcloudSSHKey() {
        HashMap response = new HashMap();
        response.put("access_key", "ncloudAccessKey");
        response.put("secret_key", "ncloudSecretKey");

        when(propertyService.getVaultBase()).thenReturn(TEST_STR);
        doReturn(response).when(vaultService).read(TEST_PATH, HashMap.class);
        when(accountService.getAccountInfo(TEST_SEQ)).thenReturn(accountModel);
        when(propertyService.getNcloudInstancePasswordApiUrl()).thenReturn(TEST_API_URL);
        when(instanceService.getNcloudPrivateKeysInfo(TEST_CLUSTER_ID, TEST_HOST, TEST_ID_RSA, TEST_PROCESS_GB)).thenReturn(ncloudPrivateKeysModel);
        when(commonService.toJson(ncloudInstanceKeyInfoModel)).thenReturn(String.valueOf(ncloudInstanceKeyInfoResultModel));

    }

    @Test
    public void createNcloudPublicKey() {
        when(ncloudService.getNcloudSSHKey(TEST_CLUSTER_ID, TEST_PROVIDER, TEST_HOST, TEST_ID_RSA, TEST_PROCESS_GB, TEST_SEQ)).thenReturn(ncloudInstanceKeysModel);
        when(instanceService.getNcloudPrivateKey(TEST_CLUSTER_ID, TEST_PROVIDER, TEST_HOST, TEST_ID_RSA, TEST_PROCESS_GB, TEST_PRIVATE_KEY)).thenReturn(ncloudPrivateKeyModel);
        when(commandService.execCommandOutput(terramanCommandModel)).thenReturn(TEST_STR);

        ClusterModel result = clusterService.updateCluster(TEST_CLUSTER_ID, TEST_STATUS);
        assertEquals(null, result);
    }
}