package org.container.terraman.api.common.model;

import lombok.Data;

@Data
public class NcloudPrivateKeyModel {
    private String publicIp;
    private String instanceNo;
    private String privateKey;
    private String encodeParameter;
    private String signature;
    private String rootPassword;

    public NcloudPrivateKeyModel(String instanceNo, String privateKey, String publicIp, String encodeParameter, String signature, String rootPassword) {
        this.instanceNo = instanceNo;
        this.privateKey = privateKey;
        this.publicIp = publicIp;
        this.encodeParameter = encodeParameter;
        this.signature = signature;
        this.rootPassword = rootPassword;
    }
}
