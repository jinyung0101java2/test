package org.paasta.container.terraman.api.common.service;

import org.paasta.container.terraman.api.common.model.ClusterModel;
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

    /**
     * Update Cluster Info
     *
     * @param clusterId the clusterId
     * @param status the status
     * @return the ClusterModel
     */
    public ClusterModel updateCluster(String clusterId, String status) {
        ClusterModel clusterModel = clusterRepository.findById(clusterId).orElse(new ClusterModel());
        clusterModel.setStatus(status);
        clusterModel.setLastModified(getSysTimestamp());
        return clusterRepository.save(clusterModel);
    }

    /**
     * Update Cluster Info
     *
     * @param clusterId the clusterId
     * @return the ClusterModel
     */
    public ClusterModel getCluster(String clusterId) {
        return clusterRepository.findById(clusterId).orElse(new ClusterModel());
    }

}
