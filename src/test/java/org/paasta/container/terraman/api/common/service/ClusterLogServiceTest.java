package org.paasta.container.terraman.api.common.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.paasta.container.terraman.api.common.model.AccountModel;
import org.paasta.container.terraman.api.common.model.ClusterLogModel;
import org.paasta.container.terraman.api.common.repository.ClusterLogRepository;
import org.paasta.container.terraman.api.common.util.CommonFileUtils;
import org.paasta.container.terraman.api.terraman.TerramanProcessService;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.paasta.container.terraman.api.common.util.CommonUtils.getSysTimestamp;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class ClusterLogServiceTest {

    private static final String TEST_CLUSTER_ID = "testClusterId";
    private static final int TEST_PROCESS_NO = 1;
    private static final String TEST_LOG_MESSAGE = "testLogMessage";

    private ClusterLogModel clusterLogModelMock;
    private ClusterLogModel clusterLogModelResultMock;

    @Mock
    private ClusterLogRepository clusterLogRepository;

    @InjectMocks
    private ClusterLogService clusterLogService;

    @Before
    public void setUp(){
        clusterLogModelMock = new ClusterLogModel();
        clusterLogModelMock.setClusterId(TEST_CLUSTER_ID);
        clusterLogModelMock.setProcessNo(TEST_PROCESS_NO);
        clusterLogModelMock.setLogMessage(TEST_LOG_MESSAGE);
        clusterLogModelMock.setRegTimestamp(getSysTimestamp());

        clusterLogModelResultMock = new ClusterLogModel();
        clusterLogModelResultMock.setClusterId(TEST_CLUSTER_ID);
    }

    @Test
    public void saveClusterLogTest() {
        when(clusterLogRepository.save(clusterLogModelMock)).thenReturn(clusterLogModelResultMock);

        clusterLogService.saveClusterLog(TEST_CLUSTER_ID, TEST_PROCESS_NO, TEST_LOG_MESSAGE);
    }

    @Test
    public void deleteClusterLogByClusterIdTest() {
        doNothing().when(clusterLogRepository).deleteClusterLog(TEST_CLUSTER_ID);

        clusterLogService.deleteClusterLogByClusterId(TEST_CLUSTER_ID);
    }
}
