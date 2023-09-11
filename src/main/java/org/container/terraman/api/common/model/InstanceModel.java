package org.container.terraman.api.common.model;

import lombok.Data;

@Data
public class InstanceModel {
    private String resourceName;
    private String instanceName;
    private String privateIp;
    private String publicIp;

    public InstanceModel(String resourceName, String instanceName, String privateIp, String publicIp) {
        this.resourceName = resourceName;
        this.instanceName = instanceName;
        this.privateIp = privateIp;
        this.publicIp = publicIp;
    }
}
