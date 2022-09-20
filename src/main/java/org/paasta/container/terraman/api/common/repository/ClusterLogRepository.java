package org.paasta.container.terraman.api.common.repository;

import org.paasta.container.terraman.api.common.model.ClusterLogModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface ClusterLogRepository extends JpaRepository<ClusterLogModel, Long> {

    @Query(value = "delete from cp_cluster_log where cluster_id = :clusterId", nativeQuery = true)
    void deleteClusterLog(@Param(value = "clusterId") String clusterId);
}
