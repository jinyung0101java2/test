package org.paasta.container.terraman.api.common.model;

import lombok.Data;

@Data
public class ClusterInfo {
    private String clusterId;
    private String clusterApiUrl;
    private String clusterToken;
}
