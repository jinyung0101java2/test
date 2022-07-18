package org.paasta.container.terraman.api.common.model;

import lombok.Data;

@Data
public class FileModel {

    /*
    * OPENSTACK FOR TERRAFORM
    * */
    private String tenant_name;
    private String password;
    private String auth_url;
    private String user_name;
    private String region;

    /*
     * AWS FOR TERRAFORM
     * */
    private String access_key;
    private String secret_key;
}
