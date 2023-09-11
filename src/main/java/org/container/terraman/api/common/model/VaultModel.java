package org.container.terraman.api.common.model;

import lombok.Data;

@Data
public class VaultModel {
    /*
     * OPENSTACK response data
     * */
    private String user_name;
    private String password;
    private String auth_url;

    /*
     * AWS response data
     * */
    private String access_Key;
    private String secret_Key;

    /*
     * GCP response data
     * */
    private String auth_provider_x509_cert_url;
    private String auth_uri;
    private String client_email;
    private String client_id;
    private String client_x509_cert_url;
    private String private_key;
    private String private_key_id;
    private String project_id;
    private String project_name;
    private String token_uri;
    private String type;

    /*
     * VSPHERE response data
     * */
    private String user;
    private String vsphere_server;

    /*
     * cluster response data
     * */
    private String clusterApiUrl;
    private String clusterId;
    private String clusterToken;
}
