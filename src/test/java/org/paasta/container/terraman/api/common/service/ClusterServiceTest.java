package org.paasta.container.terraman.api.common.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.paasta.container.terraman.api.common.model.ClusterModel;
import org.paasta.container.terraman.api.common.repository.ClusterRepository;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class ClusterServiceTest {

    private static final String TEST_PROVIDER_TYPE = "testProviderType";
    private static final String TEST_CLUSTER_ID = "testClusterId";
    private static final String TEST_CLUSTER_TYPE = "testClusterType";
    private static final String TEST_DESCRIPTION = "testDescription";
    private static final String TEST_NAME = "testName";
    private static final String TEST_STATUS = "testStatus";

    private ClusterModel clusterModelMock;
    private ClusterModel clusterModelResultMock;
    private Optional<ClusterModel> optClusterModelMock;

    @Mock
    private ClusterRepository clusterRepository;

    @InjectMocks
    private ClusterService clusterService;

    @Before
    public void setUp(){
        clusterModelMock = new ClusterModel();
        clusterModelMock.setProviderType(TEST_PROVIDER_TYPE);
        clusterModelMock.setClusterId(TEST_CLUSTER_ID);
        clusterModelMock.setClusterType(TEST_CLUSTER_TYPE);
        clusterModelMock.setDescription(TEST_DESCRIPTION);
        clusterModelMock.setName(TEST_NAME);
        clusterModelMock.setStatus(TEST_STATUS);

        clusterModelResultMock = new ClusterModel();
        clusterModelResultMock.setClusterId(TEST_CLUSTER_ID);

        optClusterModelMock = Optional.of(clusterModelResultMock);
    }

    @Test
    public void updateClusterTest() {
        when(clusterRepository.findById(TEST_CLUSTER_ID)).thenReturn(optClusterModelMock);
        when(clusterRepository.save(clusterModelMock)).thenReturn(clusterModelResultMock);

        ClusterModel result = clusterService.updateCluster(TEST_CLUSTER_ID, TEST_STATUS);
        assertEquals(null, result);
    }

    @Test
    public void getClusterTest() {
        when(clusterRepository.findById(TEST_CLUSTER_ID)).thenReturn(optClusterModelMock);

        ClusterModel result = clusterService.getCluster(TEST_CLUSTER_ID);
        assertEquals(clusterModelResultMock, result);
    }
}
