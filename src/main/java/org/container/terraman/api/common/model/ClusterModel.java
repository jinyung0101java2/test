package org.container.terraman.api.common.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(name = "cp_clusters")
@Data
@DynamicUpdate
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ClusterModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cluster_id")
    private String clusterId;

    @Column(name = "name")
    private String name;

    @Column(name = "cluster_type")
    private String clusterType;

    @Column(name = "created")
    private String created;

    @Column(name = "last_modified")
    private String lastModified;

    @Column(name = "provider_type")
    private String providerType;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    private String status;
}
