package org.paasta.container.terraman.api.terraman;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class TerramanServiceTest {
    private static final String TEST_DEFAULT_PATH = "secret";
    private static final String TEST_PROVIDER = "openstack";
    private static final String TEST_CLUSTER_ID = "test_cluster";
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

    private static final String TEST_PROCESS_GB = "";

    private static TerramanRequest gParams = null;
    private static ResultStatusModel gResultModel = null;
    private static ResultStatusModel gResultStatusModelModel = null;
    private static InstanceModel gInstanceModel = null;
    private static List<InstanceModel> gInstanceList = null;
    private static ClusterModel clusterModel = null;
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

        gInstanceModel = new InstanceModel(TEST_RESOURCE_NAME, TEST_INSTANCE_NAME, TEST_PRIVATE_IP, TEST_PUBLIC_IP);
        gInstanceList = new ArrayList<>();
        gInstanceList.add(gInstanceModel);

        clusterModel = new ClusterModel();
        accountModel = new AccountModel(1,"2","3","4","5","6","7");
        hashMap = new HashMap();
        hashMap.put("test", "test");
        uploadFile = new File(TEST_FILE_PATH);
    }


    /**
     * Terraman 생성(Create Terraman) Test
     */
    @Test
    public void createTerramanTest() {
        // when
        when(propertyService.getMASTER_HOST()).thenReturn(TEST_HOST);
        when(propertyService.getVaultClusterTokenPath()).thenReturn(TEST_CLUSTER_TOKEN_PATH);
        when(propertyService.getVaultClusterApiUrl()).thenReturn(TEST_CLUSTER_API_URL);
        when(propertyService.getVaultBase()).thenReturn(TEST_VAULT_BASE);

//        doReturn(TEST_RESULT_CODE).when(commonFileUtils).createProviderFile(TEST_CLUSTER_ID, TEST_PROVIDER, TEST_INT_SEQ, TEST_STRING, TEST_HOST, TEST_ID_RSA_PATH);
        //doReturn(TEST_RESULT_CODE).when(commonFileUtils).createProviderFile(TEST_CLUSTER_ID, TEST_PROVIDER, TEST_INT_SEQ, TEST_STRING, TEST_HOST, TEST_ID_RSA_PATH);
        when(tfFileService.createProviderFile(TEST_CLUSTER_ID, TEST_PROVIDER, TEST_INT_SEQ, TEST_STRING, TEST_HOST, TEST_ID_RSA_PATH, TEST_PROCESS_GB)).thenReturn(TEST_RESULT_CODE);
//        doReturn(hashMap).when(vaultService).read(TEST_PATH, hashMap.getClass());
//        when(accountService.getAccountInfo(TEST_INT_SEQ)).thenReturn(accountModel);
//        when(commandService.SSHFileUpload(TEST_DIR, TEST_HOST, TEST_IDRSA, uploadFile)).thenReturn(TEST_RESULT_CODE);
//        when(commandService.execCommandOutput(TerramanConstant.DIRECTORY_COMMAND, "", TEST_HOST, TEST_IDRSA)).thenReturn(TEST_RESULT_CODE);


        doNothing().when(clusterLogService).saveClusterLog(TEST_CLUSTER_ID, TEST_INT_SEQ, TerramanConstant.TERRAFORM_START_LOG(TEST_PROVIDER));
        doReturn(clusterModel).when(clusterService).updateCluster(TEST_CLUSTER_ID, TerramanConstant.CLUSTER_FAIL_STATUS);
        when(instanceService.getInstansce(TEST_CLUSTER_ID, TEST_PROVIDER, TEST_HOST, TEST_IDRSA, TEST_PROCESS_GB)).thenReturn(gInstanceModel);
        when(commonFileUtils.fileDelete(TEST_FILE_NAME)).thenReturn(TEST_RESULT_CODE);
        when(instanceService.getInstances(TEST_CLUSTER_ID, TEST_PROVIDER, TEST_HOST, TEST_IDRSA, TEST_PROCESS_GB)).thenReturn(gInstanceList);
        when(commonFileUtils.createWithWrite(TEST_FILE_NAME, TEST_FILE_DATA)).thenReturn(TEST_RESULT_CODE);
        //doReturn(gFinalResultModel).when(vaultService).read(PATH, new TerramanResponse().getClass());
        when(commonService.setResultModel(gResultModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gResultStatusModelModel);
        when(commonService.setResultModel(gResultModel, Constants.RESULT_STATUS_FAIL)).thenReturn(gResultStatusModelModel);

        ResultStatusModel result = terramanService.createTerraman(gParams, TEST_PROCESS_GB);

        // then
        assertThat(result).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

    /**
     * Terraman 생성(Create Terraman) Test
     */
    @Test
    public void deleteTerramanTest() {
        // when
        when(commandService.execCommandOutput(TerramanConstant.TERRAFORM_DESTROY_COMMAND, TerramanConstant.MOVE_DIR_CLUSTER(TEST_CLUSTER_ID, TEST_PROCESS_GB), TEST_HOST, TEST_IDRSA)).thenReturn(TEST_RESULT_CODE);
        when(commandService.execCommandOutput(TerramanConstant.DELETE_CLUSTER(TEST_CLUSTER_ID), TerramanConstant.DELETE_DIR_CLUSTER, TEST_HOST, TEST_IDRSA)).thenReturn(TEST_RESULT_CODE);
        when(commonService.setResultModel(gResultModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gResultStatusModelModel);
        when(propertyService.getVaultClusterTokenPath()).thenReturn(TEST_CLUSTER_TOKEN_PATH);
        doNothing().when(vaultService).delete(TEST_PATH);

        ResultStatusModel result = terramanService.deleteTerraman(TEST_CLUSTER_ID, TEST_PROCESS_GB);

        // then
        assertThat(result).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }


}
