package org.paasta.container.terraman.api.common.constants;

public class TerramanConstant {

    public static final String TERRAFORM_STATE = "terraform.tfstate";

    /*
    * TERRAFORM LOG MESSAGE
    * */
    public static final String TERRAFORM_START_LOG(String provider) {
        return "Start creating cluster(Provider : "+provider+")";
    }
    public static final String TERRAFORM_IAC_LOG = "Upload of requested IaC information is complete.";
    public static final String TERRAFORM_TF_ERROR_LOG = "Provider file creation error, cluster creation aborted. errCode ::";
    public static final String TERRAFORM_TF_LOG = "Tf file for instance configuration is complete.";
    public static final String TERRAFORM_INIT_LOG = "Terraform initialization is complete.";
    public static final String TERRAFORM_PLAN_LOG = "The system has confirmed that there are no problems with the terraform plan.";
    public static final String TERRAFORM_APPLY_LOG = "The system has finished configuring the instances for cluster creation.";
    public static final String TERRAFORM_SUCCESS_LOG = "It succeeded in loading the configuration information of the newly created instance.";
    public static final String KUBESPRAY_CONFIG_LOG = "Configuration information update for cluster configuration has been completed.";
    public static final String KUBESPRAY_DEPLOY_LOG = "The provisioning of the cluster is complete.";

    /*
    * TERRAFORM COMMAND 명령어
    * */
    public static final String DIRECTORY_COMMAND = "pwd";
    public static final String MOD_CHG = "chmod +x";
    public static final String KUBECTL = "kubectl";
    public static final String BASE_DIR = "/tmp/terraform/";


    public static final String FILE_COPY_COMMAND = KUBECTL + " cp -n paas-ta-container-platform-mariadb-0:bitnami" + BASE_DIR;
    public static final String INSTANCE_COPY_COMMAND = "cp tf-source/aws/terraman-opt02/opt02-resource.tf ./instance.tf";
    public static final String TERRAFORM_INIT_COMMAND = "terraform init";
    public static final String TERRAFORM_PLAN_COMMAND = "terraform plan -var vpc_name=paasta-cp-vpc -var route_table_name=paasta-cp-routing-public";
    public static final String TERRAFORM_APPLY_COMMAND = "terraform apply -auto-approve -var vpc_name=paasta-cp-vpc -var route_table_name=paasta-cp-routing-public";
    public static final String TERRAFORM_CHANGE_DIRECTORY_COMMAND = "cd /paas-ta-container-platform-deployment/standalone/aws";
    public static final String TERRAFORM_KUBESPRAY_COMMAND = "#!/bin/bash \\n\\n";
//    public static final String KUBESPRAY_CHMOD_COMMAND = "chmod -R 755 deploy_kubespray.sh";
    public static final String KUBESPRAY_CHMOD_COMMAND = MOD_CHG + " deploy_kubespray.sh";
    public static final String KUBESPRAY_DEPLOY_COMMAND = "./deploy_kubespray.sh";
}
