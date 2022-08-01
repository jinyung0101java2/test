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
    private static final String DEFAULT_PATH = "secret";
    private static final String PROVIDER = "openstack";
    private static final String CLUSTER_ID = "test_cluster";
    private static final String SEQ = "13";
    private static final int INT_SEQ = 13;
    private static final String PATH = "secret/OPENSTACK/1";
    private static final String STR = "terraman";
    private static final String RESULT_STR = "SUCCESS";
    private static final String RESULT_CODE = "200";

    private static final String RESOURCE_NAME = "resourceName";
    private static final String INSTANCE_NAME = "instanceName";
    private static final String PRIVATE_IP = "privateIp";
    private static final String PUBLIC_IP = "publicIp";

    private static final String FILE_NAME = "kubespray_var.sh";
    private static final String FILE_DATA = "{test : test}";

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
        gParams.setProvider(PROVIDER);
        gParams.setClusterId(CLUSTER_ID);
        gParams.setSeq(SEQ);

        gInstanceModel = new InstanceModel(RESOURCE_NAME, INSTANCE_NAME, PRIVATE_IP, PUBLIC_IP);
        gInstanceList = new ArrayList<>();
        gInstanceList.add(gInstanceModel);
    }


    /**
     * Terraman 생성(Create Terraman) Test
     */
    @Test
    public void createTerramanTest() {
        // when
        when(commandService.execCommandOutput(TerramanConstant.DIRECTORY_COMMAND, "")).thenReturn(RESULT_CODE);
        doNothing().when(clusterLogService).saveClusterLog(CLUSTER_ID, INT_SEQ, TerramanConstant.TERRAFORM_START_LOG(PROVIDER));
        //when(terramanService.createProviderFile(PROVIDER, INT_SEQ)).thenReturn(RESULT_CODE);
        when(instanceService.getInstansce(CLUSTER_ID, PROVIDER)).thenReturn(gInstanceModel);
        when(commonFileUtils.tfFileDelete(FILE_NAME)).thenReturn(RESULT_CODE);
        when(instanceService.getInstances(CLUSTER_ID, PROVIDER)).thenReturn(gInstanceList);
        when(commonFileUtils.createWithWrite(FILE_NAME, FILE_DATA)).thenReturn(RESULT_CODE);
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
//        doReturn(gResultModel).when(vaultService).read(PATH, new TerramanResponse().getClass());
        // when(commonService.setResultModel(gResultModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn();

        ResultStatusModel result = terramanService.deleteTerraman(CLUSTER_ID);

        // then
        assertThat(result).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }


}
