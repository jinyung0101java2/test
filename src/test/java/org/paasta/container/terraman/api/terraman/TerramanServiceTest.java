package org.paasta.container.terraman.api.terraman;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.paasta.container.terraman.api.common.constants.CommonStatusCode;
import org.paasta.container.terraman.api.common.constants.Constants;
import org.paasta.container.terraman.api.common.constants.TerramanConstant;
import org.paasta.container.terraman.api.common.model.InstanceModel;
import org.paasta.container.terraman.api.common.model.ResultStatusModel;
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
@TestPropertySource("classpath:bootstrap.yml")
public class TerramanServiceTest {
    private static final String TEST_DEFAULT_PATH = "secret";
    private static final String TEST_PROVIDER = "openstack";
    private static final String TEST_CLUSTER_ID = "test_cluster";
    private static final String TEST_SEQ = "13";
    private static final int TEST_INT_SEQ = 13;
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

    private static TerramanRequest gParams = null;
    private static ResultStatusModel gResultModel = null;
    private static ResultStatusModel gResultStatusModelModel = null;
    private static InstanceModel gInstanceModel = null;
    private static List<InstanceModel> gInstanceList = null;



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
    private VaultService vaultService;

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
    }


    /**
     * Terraman 생성(Create Terraman) Test
     */
    @Test
    public void createTerramanTest() {
        // when
        when(commandService.execCommandOutput(TerramanConstant.DIRECTORY_COMMAND, "", TEST_HOST, TEST_IDRSA)).thenReturn(TEST_RESULT_CODE);
        doNothing().when(clusterLogService).saveClusterLog(TEST_CLUSTER_ID, TEST_INT_SEQ, TerramanConstant.TERRAFORM_START_LOG(TEST_PROVIDER));
        //when(terramanService.createProviderFile(PROVIDER, INT_SEQ)).thenReturn(RESULT_CODE);
        when(instanceService.getInstansce(TEST_CLUSTER_ID, TEST_PROVIDER, TEST_HOST, TEST_IDRSA)).thenReturn(gInstanceModel);
        when(commonFileUtils.tfFileDelete(TEST_FILE_NAME)).thenReturn(TEST_RESULT_CODE);
        when(instanceService.getInstances(TEST_CLUSTER_ID, TEST_PROVIDER, TEST_HOST, TEST_IDRSA)).thenReturn(gInstanceList);
        when(commonFileUtils.createWithWrite(TEST_FILE_NAME, TEST_FILE_DATA)).thenReturn(TEST_RESULT_CODE);
        //doReturn(gFinalResultModel).when(vaultService).read(PATH, new TerramanResponse().getClass());
        when(commonService.setResultModel(gResultModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gResultStatusModelModel);
        when(commonService.setResultModel(gResultModel, Constants.RESULT_STATUS_FAIL)).thenReturn(gResultStatusModelModel);

        ResultStatusModel result = terramanService.createTerraman(gParams);

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
        when(commandService.execCommandOutput(TerramanConstant.TERRAFORM_DESTROY_COMMAND, TerramanConstant.MOVE_DIR_CLUSTER(TEST_CLUSTER_ID), TEST_HOST, TEST_IDRSA)).thenReturn(TEST_RESULT_CODE);
        when(commandService.execCommandOutput(TerramanConstant.DELETE_CLUSTER(TEST_CLUSTER_ID), TerramanConstant.DELETE_DIR_CLUSTER, TEST_HOST, TEST_IDRSA)).thenReturn(TEST_RESULT_CODE);
        when(commonService.setResultModel(gResultModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gResultStatusModelModel);

        ResultStatusModel result = terramanService.deleteTerraman(TEST_CLUSTER_ID);

        // then
        assertThat(result).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }


}
