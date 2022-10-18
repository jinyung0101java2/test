package org.paasta.container.terraman.api.common.service;

import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.paasta.container.terraman.api.common.constants.Constants;
import org.paasta.container.terraman.api.common.constants.TerramanConstant;
import org.paasta.container.terraman.api.common.model.ResultStatusModel;
import org.paasta.container.terraman.api.common.util.CommonFileUtils;
import org.paasta.container.terraman.api.terraman.TerramanProcessService;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class CommonServiceTest {

    private static final String TEST_RESULT_STR = "test";
    private static final Object TEST_OBJECT = new Object();

    private Class<?> classMock;
    private ResultStatusModel testReulstModel;
    private Gson gson;
    private Object objectMock;

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
    private CommonService commonService;

    @Before
    public void setUp() {
        testReulstModel = new ResultStatusModel();
        testReulstModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);

        objectMock = new Object();
    }

    @Test
    public void setResultModelTest() {

    }

    @Test
    public void setResultObjectTest() {
        //commonService.setResultObject(objectMock, Object.class);
    }

}
