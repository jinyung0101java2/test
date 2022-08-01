package org.paasta.container.terraman.api.common.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "cp_cluster_log")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ClusterLogModel {

    @EmbeddedId
    private ClusterLogEmbededModel logId;

    @Column(name = "log_message")
    private String logMessage;

    @Column(name = "reg_timestamp")
    private String regTimestamp;
}
