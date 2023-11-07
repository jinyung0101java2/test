package org.container.terraman.api.common.model;

import lombok.Data;

@Data
public class NcloudInstanceKeyInfoModel {
    private String instance_no;
    private String site;
    private String region;
    private String private_key;
    private Object ncloud;

    public NcloudInstanceKeyInfoModel(String instance_no, String site, String region, String private_key, Object ncloud) {
        this.instance_no = instance_no;
        this.site = site;
        this.region = region;
        this.private_key = private_key;
        this.ncloud = ncloud;
    }
}
