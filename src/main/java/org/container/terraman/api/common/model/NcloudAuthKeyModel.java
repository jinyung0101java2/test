package org.container.terraman.api.common.model;

import lombok.Data;

@Data
public class NcloudAuthKeyModel {
    private String access_key;
    private String secret_key;

    public NcloudAuthKeyModel(String access_key, String secret_key) {
        this.access_key = access_key;
        this.secret_key = secret_key;
    }
}

