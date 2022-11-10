package org.paasta.container.terraman.api.common.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.paasta.container.terraman.api.common.constants.Constants;
import org.paasta.container.terraman.api.common.constants.TerramanConstant;
import org.paasta.container.terraman.api.common.model.TerramanCommandModel;
import org.paasta.container.terraman.api.common.terramanproc.CommandProcess;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class CommandServiceTest {

    private static final String TEST_CLUSTER_ID = "testClusterId";
    private static final String TEST_HOST = "1.1.1.1";
    private static final String TEST_ID_RSA = "id_rsa";
    private static final String TEST_DIR = "/";
    private static final String TEST_COMMAND = "pwd";
    private static final String TEST_LOCAL_DIR = "/";
    private static final String TEST_FILE_NAME = "testFileName";

    private File fileMock;

    private static TerramanCommandModel terramanCommandModel;
    private static TerramanCommandModel terramanCommandModel2;

    @Mock
    private CommandProcess commandProcess;
    @Mock
    private CommandService commandServiceMock;

    @InjectMocks
    private CommandService commandService;

    @Before
    public void setUp() {
        fileMock = mock(File.class);
        terramanCommandModel = new TerramanCommandModel();
        terramanCommandModel.setCommand("1");
        terramanCommandModel.setDir(TEST_DIR);
        terramanCommandModel.setHost(TEST_HOST);
        terramanCommandModel.setIdRsa(TEST_ID_RSA);
        terramanCommandModel.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel.setClusterId(TEST_CLUSTER_ID);

        terramanCommandModel2 = new TerramanCommandModel();
        terramanCommandModel2.setCommand("1");
        terramanCommandModel2.setDir(TEST_DIR);
        terramanCommandModel2.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel2.setClusterId(TEST_CLUSTER_ID);
    }


    @Test
    public void sshFileUploadTest() {
        when(commandProcess.sshFileUpload(TEST_DIR, TEST_HOST, TEST_ID_RSA, fileMock, TerramanConstant.DEFAULT_USER_NAME)).thenReturn(Constants.RESULT_STATUS_SUCCESS);

        String result = commandService.sshFileUpload(TEST_DIR, TEST_HOST, TEST_ID_RSA, fileMock, TerramanConstant.DEFAULT_USER_NAME);

        assertEquals(Constants.RESULT_STATUS_FAIL, result);
    }

    @Test
    public void sshFileDownloadTest() {
        doNothing().when(commandProcess).sshFileDownload(TEST_DIR, TEST_LOCAL_DIR, TEST_FILE_NAME, TEST_HOST, TEST_ID_RSA, TerramanConstant.DEFAULT_USER_NAME);

        commandService.sshFileDownload(TEST_DIR, TEST_LOCAL_DIR, TEST_FILE_NAME, TEST_HOST, TEST_ID_RSA, TerramanConstant.DEFAULT_USER_NAME);
    }

    @Test
    public void getSSHResponseTest() {

        when(commandProcess.getSSHResponse(terramanCommandModel)).thenReturn(Constants.RESULT_STATUS_SUCCESS);

        String result = commandService.getSSHResponse(terramanCommandModel);

        assertEquals(Constants.RESULT_STATUS_FAIL, result);
    }

    @Test
    public void getResponseTest() {
        when(commandProcess.getResponse(terramanCommandModel)).thenReturn(Constants.RESULT_STATUS_SUCCESS);

        String result = commandService.getResponse(terramanCommandModel);

        assertEquals(Constants.RESULT_STATUS_FAIL, result);
    }

    @Test
    public void  execCommandOutputSShTest() {
        doReturn(Constants.RESULT_STATUS_SUCCESS).when(commandServiceMock).getSSHResponse(terramanCommandModel);

        String result = commandService.execCommandOutput(terramanCommandModel);

        assertEquals(Constants.RESULT_STATUS_FAIL, result);
    }

    @Test
    public void  execCommandOutputTest() {
        doReturn(Constants.RESULT_STATUS_SUCCESS).when(commandServiceMock).getResponse(terramanCommandModel);

        String result = commandService.execCommandOutput(terramanCommandModel2);

        assertEquals(Constants.RESULT_STATUS_FAIL, result);
    }
}
