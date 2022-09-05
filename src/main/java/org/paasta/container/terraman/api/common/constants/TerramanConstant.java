package org.paasta.container.terraman.api.common.constants;

import org.apache.commons.lang3.StringUtils;

public class TerramanConstant {

    /**
     * ssh conn key
     * */
    public static final String MASTER_ID_RSA = "/root/.ssh/id_rsa";

    /**
     * file name & directory
     * */
    public static final String TERRAFORM_STATE_FILE_NAME = "terraform.tfstate";
    public static final String TERRAFORM_STATE_FILE_PATH(String clusterPath) {
        return clusterPath + "/terraform.tfstate";
    }
    public static final String FILE_PATH(String clusterPath) {
        return clusterPath + "/provider.tf";
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
        return "mkdir -p -v tmp/terraform/"+clusterId;
    }

    public static final String CLUSTER_STATE_DIR(String clusterId) {
        return "tmp/terraform/" + clusterId;
    }

    public static final String MOVE_DIR_CLUSTER(String clusterId, String processGb) {
        String dir = "tmp/terraform/"+clusterId;
        if(StringUtils.isBlank(processGb) || !StringUtils.equals(processGb.toUpperCase(), "CONTAINER")) {
            dir = "/home/ubuntu/" + dir;
        }
        return dir;
    }
    public static final String DELETE_DIR_CLUSTER = "/home/ubuntu/tmp/terraform";
    public static final String DELETE_CLUSTER(String clusterId) {
        return "rm -r "+clusterId;
    }
    public static final String MOVE_DIR_KUBESPRAY = "/home/ubuntu/paas-ta-container-platform-deployment/standalone/single_control_plane";

    /**
     * get cluster info
     * */
    public static final String CLUSTER_PRIVATE_KEY(String clusterId, String processGb) {
        String path = ".ssh/cluster-key";
        if(!StringUtils.isBlank(processGb) && StringUtils.equals(processGb.toUpperCase(), "CONTAINER")) {
            path = "/root/" + path;
        } else {
            path = "/home/ubuntu/" + path;
        }
        return path;
    }
    public static final String SERVICE_ACCOUNT_CREATE = "kubectl create serviceaccount k8sadmin -n kube-system";
    public static final String SERVICE_ACCOUNT_BINDING = "kubectl create clusterrolebinding k8sadmin --clusterrole=cluster-admin --serviceaccount=kube-system:k8sadmin";
    public static final String SERVICE_ACCOUNT_SECRET_NAME = "kubectl describe serviceaccount k8sadmin -n kube-system | grep 'Mountable secrets'";
    public static final String SERVICE_ACCOUNT_TOKEN(String secrets) {
        return "kubectl describe secret " + secrets.substring(secrets.indexOf("k8sadmin")) + " -n kube-system | grep -E '^token' | cut -f2 -d':' | tr -d \" \"";
    }


    /**
     * etc linux 명령어
     * */
    public static final String DIRECTORY_COMMAND = "pwd";
    public static final String TERRAFORM_KUBESPRAY_COMMAND = "#!/bin/bash \\n\\n";

    /**
     * .tf 파일 복사 명령어
     * */
    public static  final String POD_NAME_COMMAND = "kubectl get pods -n cp-portal -l app=cp-portal-api -o custom-columns=:metadata.name | grep 'cp-portal-api-deployment'";
    public static final String INSTANCE_COPY_COMMAND(String pod, String clusterId) {
        return "kubectl cp -n cp-portal " + pod + ":tmp/terraform/" + clusterId + " /home/ubuntu/tmp/terraform/"+clusterId;
    }
//    public static final String INSTANCE_COPY_COMMAND = "sudo docker cp a44ddef5e883:openstack-resource.tf instance.tf";
//    public static final String NETWORK_COPY_COMMAND(String pod) {
//        return "kubectl cp -n cp-portal " + pod + ":tmp/test/ ./network.tf";
//    }

    /********************************************************************************************************************************
     * local
     * ******************************************************************************************************************************/
    public static final String INSTANCE_COPY_COMMAND = "cp ~/tf-source/openstack/tf-resource/openstack-resource.tf ./instance.tf";
    public static final String NETWORK_COPY_COMMAND = "cp ~/tf-source/openstack/tf-network/network-resource.tf ./network.tf";

//    public static final String CREATE_DIR_CLUSTER(String clusterId) {
//        return "mkdir -p -v /home/ubuntu/tmp/terraform/cluster_"+clusterId;
//    }
//
//    public static final String MOVE_DIR_CLUSTER(String clusterId) {
//        return "/home/ubuntu/tmp/terraform/cluster_"+clusterId;
//    }
//    public static final String DELETE_DIR_CLUSTER = "/home/ubuntu/tmp/terraform";
    /********************************************************************************************************************************/

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

    /**
     * TERRAFORM LOG MESSAGE - FAIL
     * */
    public static final String TERRAFORM_INIT_FAIL_LOG = "ERROR - Terraform init is failed.";
    public static final String TERRAFORM_PLAN_FAIL_LOG = "ERROR - Terraform plan is failed.";
    public static final String TERRAFORM_APPLY_FAIL_LOG = "ERROR - Terraform apply is failed.";

    /**
     * TERRAFORM LOG MESSAGE - ERROR
     * */

    public static final String TERRAFORM_CREATE_CLUSTER_DIRECTORY = "ERROR - cluster directory create failed";
    public static final String TERRAFORM_NOT_EXISTS_INSTANCE_ERROR = "ERROR - Instance is not exists";
    public static final String TERRAFORM_CREATE_CLUSTER_FILE_ERROR = "ERROR - cluster file create failed";
    public static final String TERRAFORM_NOT_EXISTS_INSTANCES_ERROR = "ERROR - Instances are not exists";
    public static final String TERRAFORM_CHANGE_MODE_ERROR = "ERROR - mode change failed";
    public static final String TERRAFORM_DEPLOY_CLUSTER_ERROR = "ERROR - cluster deploy failed";
    public static final String TERRAFORM_CREATE_SERVICE_ACCOUNT_ERROR = "ERROR - service account create failed";
    public static final String TERRAFORM_BIND_ROLE_ERROR = "ERROR - role binding create failed";
    public static final String TERRAFORM_GET_SECRET_NAME_ERROR = "ERROR - get secret name failed";
    public static final String TERRAFORM_GET_CLUSTER_TOKEN_ERROR = "ERROR - get cluster token failed";
    public static final String TERRAFORM_CREATE_TOKEN_ERROR = "ERROR - token create failed";
    public static final String TERRAFORM_COMPLETE_CLUSTER_ERROR = "ERROR - complete cluster update failed";


    /**
     * cluster 생성 상태값
     * */
    public static final String CLUSTER_CREATE_STATUS = "C";
    public static final String CLUSTER_COMPLETE_STATUS = "A";
    public static final String CLUSTER_FAIL_STATUS = "D";
}
