package org.container.terraman.api.common.model;

import lombok.Data;

@Data
public class FileModel {

    /*
    * OPENSTACK FOR TERRAFORM
    * */
    private String openstackTenantName;
    private String openstackPassword;
    private String openstackAuthUrl;
    private String openstackUserName;
    private String openstackRegion;

    /*
     * AWS FOR TERRAFORM
     * */
    private String awsAccessKey;
    private String awsSecretKey;
    private String awsRegion;

    /*
     * VSPHERE FOR TERRAFORM
     * */
    private String vSphereUser;
    private String vSpherePassword;
    private String vSphereServer;
}
