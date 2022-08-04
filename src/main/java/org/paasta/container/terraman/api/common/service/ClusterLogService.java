package org.paasta.container.terraman.api.common.service;

import org.paasta.container.terraman.api.common.model.ClusterLogEmbededModel;
import org.paasta.container.terraman.api.common.model.ClusterLogModel;
import org.paasta.container.terraman.api.common.repository.ClusterLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.paasta.container.terraman.api.common.util.CommonUtils.getSysString;
import static org.paasta.container.terraman.api.common.util.CommonUtils.getSysTimestamp;

@Service
public class ClusterLogService {

    private final ClusterLogRepository clusterLogRepository;

    @Autowired
    public ClusterLogService(ClusterLogRepository clusterLogRepository) {
        this.clusterLogRepository = clusterLogRepository;
    }

    public void saveClusterLog(String clusterId, int processNo, String logMessage) {
        ClusterLogModel logModel = new ClusterLogModel();
        ClusterLogEmbededModel logEmbededModel= new ClusterLogEmbededModel();
        logEmbededModel.setClusterId(clusterId);
        logEmbededModel.setProcessNo(processNo);
        logModel.setLogId(logEmbededModel);
        logModel.setLogMessage(logMessage);
        logModel.setRegTimestamp(getSysString());
        clusterLogRepository.save(logModel);
    }
}
