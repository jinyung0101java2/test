package org.paasta.container.terraman.api.common.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.paasta.container.terraman.api.common.constants.Constants;
import org.paasta.container.terraman.api.common.terramanproc.CommandProcess;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class CommandServiceTest {

    private static final String TEST_HOST = "1.1.1.1";
    private static final String TEST_ID_RSA = "id_rsa";
    private static final String TEST_DIR = "/";
    private static final String TEST_COMMAND = "pwd";
    private static final String TEST_LOCAL_DIR = "/";
    private static final String TEST_FILE_NAME = "testFileName";

    private File fileMock;

    @Mock
    private CommandProcess commandProcess;
    @Mock
    private CommandService commandServiceMock;

    @InjectMocks
    private CommandService commandService;

    @Before
    public void setUp() {
        fileMock = mock(File.class);
    }


    @Test
    public void sshFileUploadTest() {
        when(commandProcess.sshFileUpload(TEST_DIR, TEST_HOST, TEST_ID_RSA, fileMock)).thenReturn(Constants.RESULT_STATUS_SUCCESS);

        String result = commandService.sshFileUpload(TEST_DIR, TEST_HOST, TEST_ID_RSA, fileMock);

        assertEquals(Constants.RESULT_STATUS_FAIL, result);
    }

    @Test
    public void sshFileDownloadTest() {
        doNothing().when(commandProcess).sshFileDownload(TEST_DIR, TEST_LOCAL_DIR, TEST_FILE_NAME, TEST_HOST, TEST_ID_RSA);

        commandService.sshFileDownload(TEST_DIR, TEST_LOCAL_DIR, TEST_FILE_NAME, TEST_HOST, TEST_ID_RSA);
    }

    @Test
    public void getSSHResponseTest() {
        when(commandProcess.getSSHResponse(TEST_COMMAND, TEST_DIR, TEST_HOST, TEST_ID_RSA)).thenReturn(Constants.RESULT_STATUS_SUCCESS);

        String result = commandService.getSSHResponse(TEST_COMMAND, TEST_DIR, TEST_HOST, TEST_ID_RSA);

        assertEquals(Constants.RESULT_STATUS_FAIL, result);
    }

    @Test
    public void getResponseTest() {
        when(commandProcess.getResponse(TEST_COMMAND, TEST_DIR)).thenReturn(Constants.RESULT_STATUS_SUCCESS);

        String result = commandService.getResponse(TEST_COMMAND, TEST_DIR);

        assertEquals(Constants.RESULT_STATUS_FAIL, result);
    }

    @Test
    public void  execCommandOutputSShTest() {
        doReturn(Constants.RESULT_STATUS_SUCCESS).when(commandServiceMock).getSSHResponse(TEST_COMMAND, TEST_DIR, TEST_HOST, TEST_ID_RSA);
//        doReturn(Constants.RESULT_STATUS_SUCCESS).when(commandServiceMock).getResponse(TEST_COMMAND, TEST_DIR);

        String result = commandService.execCommandOutput(TEST_COMMAND, TEST_DIR, TEST_HOST, TEST_ID_RSA);

        assertEquals(Constants.RESULT_STATUS_FAIL, result);
    }

    @Test
    public void  execCommandOutputTest() {
//        doReturn(Constants.RESULT_STATUS_SUCCESS).when(commandServiceMock).getSSHResponse(TEST_COMMAND, TEST_DIR, TEST_HOST, TEST_ID_RSA);
        doReturn(Constants.RESULT_STATUS_SUCCESS).when(commandServiceMock).getResponse(TEST_COMMAND, TEST_DIR);

        String result = commandService.execCommandOutput(TEST_COMMAND, TEST_DIR, "", "");

        assertEquals(Constants.RESULT_STATUS_FAIL, result);
    }
}
