package org.paasta.container.terraman.api.common.service;

import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.paasta.container.terraman.api.common.constants.CommonStatusCode;
import org.paasta.container.terraman.api.common.constants.Constants;
import org.paasta.container.terraman.api.common.constants.TerramanConstant;
import org.paasta.container.terraman.api.common.model.ResultStatusModel;
import org.paasta.container.terraman.api.common.util.CommonFileUtils;
import org.paasta.container.terraman.api.terraman.TerramanProcessService;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Method;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class CommonServiceTest {

    private static final String TEST_RESULT_STR = "test";
    private static final Object TEST_OBJECT = new Object();
    private static final String TEST_RESULT_CODE = "200";

    private static final String SET_RESULT_CODE = "setResultCode";
    private static final String SET_RESULT_MESSAGE = "setResultMessage";
    private static final String SET_HTTP_STATUS_CODE = "setHttpStatusCode";
    private static final String SET_DETAIL_MESSAGE = "setDetailMessage";
    private static final String NO_SUCH_METHOD_EXCEPTION_LOG = "NoSuchMethodException :: {}";
    private static final String ILLEGAL_ACCESS_EXCEPTION_LOG = "IllegalAccessException :: {}";
    private static final String INVOCATION_TARGET_EXCEPTION_LOG = "InvocationTargetException :: {}";

    private ResultStatusModel testReulstModel;
    private Gson gson;
    private Object objectMock;
    private Object objectReturnMock;
    private Method methodMock;
    private Class<?> classMock;

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
        objectReturnMock = new Object();

        Object testObject = new Object();
        Class<?> testClass = testObject.getClass();
        try {
            methodMock = testClass.getMethod(SET_RESULT_CODE, String.class);
        } catch (Exception e) {}

    }

//    @Test
//    public void setResultModelTest() throws Exception{
//        classMock = objectMock.getClass();
//        Method methodSetResultCode = mock(Method.class);
//        Method methodSetResultMessage = mock(Method.class);
//        Method methodSetHttpStatusCode = mock(Method.class);
//        Method methodSetDetailMessage = mock(Method.class);
//
//        when(classMock.getMethod(SET_RESULT_CODE, String.class)).thenReturn(methodMock);
//        when(classMock.getMethod(SET_RESULT_MESSAGE, String.class)).thenReturn(methodMock);
//        when(classMock.getMethod(SET_HTTP_STATUS_CODE, String.class)).thenReturn(methodMock);
//        when(classMock.getMethod(SET_DETAIL_MESSAGE, String.class)).thenReturn(methodMock);
//
//        when(methodSetResultCode.invoke(objectMock, TEST_RESULT_CODE)).thenReturn(objectReturnMock);
//        when(methodSetResultMessage.invoke(objectMock, CommonStatusCode.INTERNAL_SERVER_ERROR.getMsg())).thenReturn(objectReturnMock);
//        when(methodSetHttpStatusCode.invoke(objectMock, CommonStatusCode.INTERNAL_SERVER_ERROR.getCode())).thenReturn(objectReturnMock);
//        when(methodSetDetailMessage.invoke(objectMock, CommonStatusCode.INTERNAL_SERVER_ERROR.getMsg())).thenReturn(objectReturnMock);
//    }

    @Test
    public void setResultObjectTest() {
        //commonService.setResultObject(objectMock, Object.class);
    }

}
