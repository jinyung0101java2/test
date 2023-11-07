package org.container.terraman.api.common.model;

import lombok.Data;

@Data
public class NcloudPrivateKeyModel {
    private String publicIp;
    private String instanceNo;
    private String privateKey;

    public NcloudPrivateKeyModel(String instanceNo, String privateKey, String publicIp) {
        this.instanceNo = instanceNo;
        this.privateKey = privateKey;
        this.publicIp = publicIp;
    }
}
