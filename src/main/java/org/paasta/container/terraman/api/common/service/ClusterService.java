package org.paasta.container.terraman.api.common.service;

import org.paasta.container.terraman.api.common.model.ClusterLogEmbededModel;
import org.paasta.container.terraman.api.common.model.ClusterLogModel;
import org.paasta.container.terraman.api.common.model.ClusterModel;
import org.paasta.container.terraman.api.common.repository.ClusterLogRepository;
import org.paasta.container.terraman.api.common.repository.ClusterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.paasta.container.terraman.api.common.util.CommonUtils.getSysString;
import static org.paasta.container.terraman.api.common.util.CommonUtils.getSysTimestamp;

@Service
public class ClusterService {

    private final ClusterRepository clusterRepository;

    @Autowired
    public ClusterService(ClusterRepository clusterRepository) {
        this.clusterRepository = clusterRepository;
    }

    @Transactional
    public ClusterModel updateCluster(String clusterId, String status) {
        ClusterModel clusterModel = clusterRepository.findById(clusterId).get();
        clusterModel.setStatus(status);
        clusterModel.setLastModified(getSysString());
        return clusterRepository.save(clusterModel);
    }
}
