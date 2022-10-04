package org.paasta.container.terraman.api.common.service;

import org.paasta.container.terraman.api.common.model.ClusterLogEmbededModel;
import org.paasta.container.terraman.api.common.model.ClusterLogModel;
import org.paasta.container.terraman.api.common.repository.ClusterLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

import static org.paasta.container.terraman.api.common.util.CommonUtils.getSysString;
import static org.paasta.container.terraman.api.common.util.CommonUtils.getSysTimestamp;

@Service
public class ClusterLogService {

    private final ClusterLogRepository clusterLogRepository;

    @Autowired
    public ClusterLogService(ClusterLogRepository clusterLogRepository) {
        this.clusterLogRepository = clusterLogRepository;
    }

    /**
     * Account 정보 조회
     *
     * @param clusterId the clusterId
     * @param processNo the processNo
     * @param logMessage the logMessage
     * @return the void
     */
    public void saveClusterLog(String clusterId, int processNo, String logMessage){
        ClusterLogModel logModel = new ClusterLogModel();
        logModel.setClusterId(clusterId);
        logModel.setProcessNo(processNo);
        logModel.setLogMessage(logMessage);
        logModel.setRegTimestamp(getSysTimestamp());
        clusterLogRepository.save(logModel);
    }

    /**
     * Account 정보 조회
     *
     * @param clusterId the clusterId
     * @return the AccountModel
     */
    public void deleteClusterLogByClusterId(String clusterId) throws Exception {
        clusterLogRepository.deleteClusterLog(clusterId);
    }
}
