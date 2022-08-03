package org.paasta.container.terraman.api.common.service;

import org.paasta.container.terraman.api.common.model.ClusterLogEmbededModel;
import org.paasta.container.terraman.api.common.model.ClusterLogModel;
import org.paasta.container.terraman.api.common.model.ClusterModel;
import org.paasta.container.terraman.api.common.repository.ClusterLogRepository;
import org.paasta.container.terraman.api.common.repository.ClusterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.paasta.container.terraman.api.common.util.CommonUtils.getSysTimestamp;

@Service
public class ClusterService {

    private final ClusterRepository clusterRepository;

    @Autowired
    public ClusterService(ClusterRepository clusterRepository) {
        this.clusterRepository = clusterRepository;
    }

    public void updateCluster(String clusterId, String status) {
        ClusterModel clusterModel = clusterRepository.getOne(clusterId);
        clusterModel.setStatus(status);
        clusterRepository.save(clusterModel);
    }
}
