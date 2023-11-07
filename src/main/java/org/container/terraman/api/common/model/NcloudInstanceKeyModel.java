package org.container.terraman.api.common.model;

import lombok.Data;

@Data
public class NcloudInstanceKeyModel {
    private String ServerInstanceNo;
    private String RootPassword;

    public NcloudInstanceKeyModel() {
    }

    public NcloudInstanceKeyModel(String serverInstanceNo, String rootPassword) {
        ServerInstanceNo = serverInstanceNo;
        RootPassword = rootPassword;
    }
}
