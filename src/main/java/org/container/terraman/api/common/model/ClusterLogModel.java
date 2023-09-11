package org.container.terraman.api.common.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;

@IdClass(ClusterLogEmbededModel.class)
@Entity
@Table(name = "cp_cluster_log")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ClusterLogModel {

//    @EmbeddedId
//    private ClusterLogEmbededModel logId;
    @Id
    @Column(name = "cluster_id")
    private String clusterId;
    @Id
    @Column(name = "process_no")
    private int processNo;

    @Column(name = "log_message")
    private String logMessage;

    @Column(name = "reg_timestamp")
    private String regTimestamp;
}
