package org.paasta.container.terraman.api.common.service;

import com.jcraft.jsch.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.paasta.container.terraman.api.common.constants.Constants;
import org.paasta.container.terraman.api.common.constants.TerramanConstant;
import org.paasta.container.terraman.api.common.util.CommonFileUtils;
import org.paasta.container.terraman.api.terraman.TerramanProcessService;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class CommandServiceTest {

    private static final String TEST_USER_NAME = "ubuntu";
    private static final String TEST_HOST = "1.1.1.1";
    private static final int TEST_PORT = 22;
    private static final String TEST_ID_RSA = "id_rsa";
    private static final String TEST_CONFIG_KEY = "StrictHostKeyChecking";
    private static final String TEST_CONFIG_VALUE = "no";
    private static final String TEST_OPEN_TYPE = "sftp";
    private static final String TEST_DIR = "/";
    private static final String TEST_FILE_PATH = "/file";
    private static final String TEST_UPLOAD_NAME = "upload";
    private static final String TEST_COMMAND = "pwd";
    private static final String TEST_STR = "test";


    Vector vectorMock;
    private File uploadFile = mock(File.class);
    private FileInputStream fileInputStream = mock(FileInputStream.class);
    private Session session = null;
    private ChannelExec channelExec = null;
    private ChannelSftp channelSftp = null;
    private ChannelSftp channelSftpMock = null;
    private Channel channel = null;
    private Channel channelMock = null;
    private JSch jsch = null;
    private List<String> list;


    @Mock
    private CommonService commonService;
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
    private CommandService commandServiceMock;
    @Mock
    private TerramanProcessService terramanProcessService;

    @InjectMocks
    private CommandService commandService;

    @Before
    public void setUp() throws Exception{

        list = new ArrayList<>();
        list.add("pwd");
        list.add("test");

        vectorMock = null;
        uploadFile = mock(File.class);
        fileInputStream = mock(FileInputStream.class);
        session = mock(Session.class);
        channelExec = mock(ChannelExec.class);
        channelMock = mock(Channel.class);
        channelSftpMock = mock(ChannelSftp.class);

        channelSftp = (ChannelSftp)channel;

        channel = null;
        jsch = mock(JSch.class);
    }

    @Test
    public void sshConnectTest() {
        try (MockedConstruction mocked = mockConstruction(File.class)) {
            File f = new File("test");
            doNothing().when(jsch).addIdentity(f.getName());
            when(jsch.getSession(TEST_USER_NAME, TEST_HOST, TEST_PORT)).thenReturn(session);
            doNothing().when(session).setConfig("StrictHostKeyChecking", "no");
            doNothing().when(session).connect();

            commandService.sshConnect(TEST_HOST, TEST_ID_RSA);
        } catch (Exception e) {}
    }

    @Test
    public void existsTest() {
        try {
            when(channelSftp.ls(TEST_FILE_PATH)).thenReturn(vectorMock);

            boolean result = commandService.exists(TEST_FILE_PATH);

            assertEquals(false, result);
        } catch (Exception e) {}
    }

    @Test
    public void existsTestFailed() {
        try {
            when(channelSftp.ls(TEST_FILE_PATH)).thenThrow(SftpException.class);

            boolean result = commandService.exists(TEST_FILE_PATH);

            assertEquals(false, result);
        } catch (Exception e) {}
    }

    @Test
    public void  disConnectSSHTest() {
        doNothing().when(session).disconnect();
        doNothing().when(channelExec).disconnect();
        doNothing().when(channelSftpMock).quit();
        doNothing().when(channelMock).disconnect();

        commandService.disConnectSSH();
    }

    @Test
    public void sshFileUploadTest() {
        try (MockedConstruction mocked = mockConstruction(File.class);
            MockedConstruction mocked2 = mockConstruction(FileInputStream.class);) {
            File f = new File("test");
            FileInputStream in = new FileInputStream(f);

            doNothing().when(commandServiceMock).sshConnect(TEST_HOST, TEST_ID_RSA);
            doReturn(channel).when(session).openChannel("sftp");
            doNothing().when(channel).connect();
            doNothing().when(channelSftp).cd(TEST_DIR);
            doNothing().when(channelSftp).put(in, f.getName());

            String result = commandService.sshFileUpload(TEST_DIR, TEST_HOST, TEST_ID_RSA, f);

            assertEquals("", result);
        } catch (Exception e) {}

    }

    @Test
    public void sshFileDownloadTest() {
        try (MockedConstruction mocked = mockConstruction(File.class);
             MockedConstruction mocked2 = mockConstruction(FileOutputStream.class);) {
            File f = new File("test");
            FileOutputStream out = new FileOutputStream(f);

            doNothing().when(commandServiceMock).sshConnect(TEST_HOST, TEST_ID_RSA);
            doReturn(channel).when(session).openChannel("sftp");
            doNothing().when(channel).connect();
            doNothing().when(channelSftp).cd(TEST_DIR);
            doNothing().when(channelSftp).get(TEST_UPLOAD_NAME);

            commandService.sshFileDownload(TEST_DIR, TEST_DIR, TEST_UPLOAD_NAME, TEST_HOST, TEST_ID_RSA);

        } catch (Exception e) {}

    }

    @Test
    public void  execCommandOutputTest() {
        doReturn(Constants.RESULT_STATUS_SUCCESS).when(commandServiceMock).getSSHResponse(TEST_COMMAND, TEST_DIR, TEST_HOST, TEST_ID_RSA);
        doReturn(Constants.RESULT_STATUS_SUCCESS).when(commandServiceMock).getResponse(TEST_COMMAND, TEST_DIR);

        String result = commandService.execCommandOutput(TEST_COMMAND, TEST_DIR, TEST_HOST, TEST_ID_RSA);

        assertEquals(Constants.RESULT_STATUS_FAIL, result);
    }
}
