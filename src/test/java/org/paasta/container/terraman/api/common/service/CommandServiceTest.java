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
import java.util.ArrayList;
import java.util.List;

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


    private File uploadFile = mock(File.class);
    private FileInputStream fileInputStream = mock(FileInputStream.class);
    private Session session = null;
    private ChannelExec channelExec = null;
    private ChannelSftp channelSftp = null;
    private Channel channel = null;
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

        uploadFile = mock(File.class);
        fileInputStream = mock(FileInputStream.class);
        session = mock(Session.class);
        channelExec = mock(ChannelExec.class);
        channelSftp = mock(ChannelSftp.class);
        channel = mock(Channel.class);
        jsch = mock(JSch.class);
    }

    @Test
    public void  disConnectSSHTest() {
        doNothing().when(session).disconnect();
        doNothing().when(channelExec).disconnect();
        doNothing().when(channelSftp).quit();
        doNothing().when(channel).disconnect();

        commandService.disConnectSSH();
    }

    @Test
    public void  execCommandOutputTest() {
        doReturn(Constants.RESULT_STATUS_SUCCESS).when(commandServiceMock).getSSHResponse(TEST_COMMAND, TEST_DIR, TEST_HOST, TEST_ID_RSA);
        doReturn(Constants.RESULT_STATUS_SUCCESS).when(commandServiceMock).getResponse(TEST_COMMAND, TEST_DIR);

        String result = commandService.execCommandOutput(TEST_COMMAND, TEST_DIR, TEST_HOST, TEST_ID_RSA);

        assertThat(result).isNotNull();
        assertEquals(Constants.RESULT_STATUS_FAIL, result);
    }
}
