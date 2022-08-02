package org.paasta.container.terraman.api.common.constants;

public class TerramanConstant {

    /**
     * file name & directory
     * */
    public static final String TERRAFORM_STATE_FILE_PATH(String clusterPath) {
        return clusterPath + "//terraform.tfstate";
    }
    public static final String FILE_PATH(String clusterPath) {
        return clusterPath + "/provider.tf";
    }
    public static final String KEYPAIR_TF_FILE_PATH(String clusterPath) {
        return clusterPath + "/keypair.tf";
    }

    /**
     * openstack provider prefix 생성
     * */
    public static final String PREFIX_PROVIDER_OPENSTACK = "terraform {\n" +
            "  required_providers {\n" +
            "    openstack = {\n" +
            "      source  = \"terraform-provider-openstack/openstack\"\n" +
            "      version = \"1.47.0\"\n" +
            "    }\n" +
            "  }\n" +
            "}";


    /**
     * vm create key-pair ssh key 생성
     * */
    public static final String SSH_KEY_COMMAND(String clusterId) {
        return "ssh-keygen -f /home/ubuntu/.ssh/" + clusterId + "_id_rsa -t rsa -N ''";
    }
    public static final String SSH_KEY_RSA_COMMAND(String clusterId) {
        return "cat /home/ubuntu/.ssh/" + clusterId + "_id_rsa.pub";
    }

    public static final String SSH_KEY_COMMAND = "ssh-keygen -f /home/ubuntu/.ssh/id_rsa -t rsa -N '' -m PEM";
    public static final String SSH_KEY_RSA_COMMAND = "cat /home/ubuntu/.ssh/id_rsa.pub";


    /**
     * terraman keyPair 생성
     * */

    public static final String DELETE_KEYPAIR_ALL = "/home/ubuntu/.ssh";
    public static final String KEYPAIR_TF_FILE(String sshKey) {
        return "resource \"openstack_compute_keypair_v2\" \"paasta-cp-keypair\" {\n" +
                "  name       = \"paasta-cp-keypair\"\n" +
                "  public_key = \"" + sshKey.trim() + "\"\n" +
                "}";
    }


    /**
     * kubespray cluster cp-cluster-vars.sh 변경 및 실행 명령어
     * */
    public static final String CLUSTER_KUBESPRAY_SH_FILE_COMMAND(String contents) {
        return "echo -e \"" + contents + "\" > /home/ubuntu/paas-ta-container-platform-deployment/standalone/single_control_plane/cp-cluster-vars.sh";
    }
    public static final String KUBESPRAY_CHMOD_COMMAND = "chmod +x deploy-cp-cluster.sh";
    public static final String CLUSTER_KUBESPRAY_DEPLOY_COMMAND = "source deploy-cp-cluster.sh";

    /**
     * TERRAFORM COMMAND 명령어
     * */

    /**
     * change directory 명령어
     * */
    public static final String CREATE_DIR_CLUSTER(String clusterId) {
        return "mkdir -p -v $HOME/terraman/clusters/cluster_"+clusterId;
    }

    public static final String MOVE_DIR_CLUSTER(String clusterId) {
        return "/home/ubuntu/terraman/clusters/cluster_"+clusterId;
    }
    public static final String DELETE_DIR_CLUSTER = "/home/ubuntu/terraman/clusters";
    public static final String DELETE_CLUSTER(String clusterId) {
        return "rm -r cluster_"+clusterId;
    }
    public static final String MOVE_DIR_KUBESPRAY = "/home/ubuntu/paas-ta-container-platform-deployment/standalone/single_control_plane";

    /**
     * etc linux 명령어
     * */
    public static final String DIRECTORY_COMMAND = "pwd";
    public static final String TERRAFORM_KUBESPRAY_COMMAND = "#!/bin/bash \\n\\n";

    /**
     * .tf 파일 복사 명령어
     * */
//    public static final String INSTANCE_COPY_COMMAND =  "kubectl cp -n cp-portal cp-portal-api-deployment-6b94d6945d-jzvfh:tmp/test/ /home/ubuntu/tmp/instance.tf"
//    public static final String NETWORK_COPY_COMMAND =  "kubectl cp -n cp-portal cp-portal-api-deployment-6b94d6945d-jzvfh:tmp/test/ /home/ubuntu/tmp/network.tf"
    public static final String INSTANCE_COPY_COMMAND = "cp ~/tf-source/openstack/tf-instance/instance-resource.tf ./instance.tf";
    public static final String NETWORK_COPY_COMMAND = "cp ~/tf-source/openstack/tf-network/network-resource.tf ./network.tf";

    /**
     * terraform 실행 명령어
     * */
    public static final String TERRAFORM_INIT_COMMAND = "terraform init";
    public static final String TERRAFORM_PLAN_COMMAND = "terraform plan";
    public static final String TERRAFORM_APPLY_COMMAND = "terraform apply -auto-approve";
    public static final String TERRAFORM_DESTROY_COMMAND = "terraform destroy -auto-approve";




    /**
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
}
