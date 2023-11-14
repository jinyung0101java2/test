package org.container.terraman.api.common.constants;

import org.apache.commons.lang3.StringUtils;
import org.container.terraman.api.common.model.TerramanCommandModel;
import org.container.terraman.api.common.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TerramanConstant {

    private static final Logger LOGGER = LoggerFactory.getLogger(TerramanConstant.class);

    private TerramanConstant() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * etc linux 명령어
     * */
    public static final String DIRECTORY_COMMAND = "pwd";
    public static final String TERRAFORM_KUBESPRAY_COMMAND = "#!/bin/bash \\n\\n";
    public static final String DEFAULT_USER_NAME = "ubuntu";
    public static final String NCLOUD_USER_NAME = "ncloud";
    public static final String CUSTOM_USER_NAME = "1000";
    public static final String LINUX_BASH = "/bin/bash";
    public static final String LINUX_BASH_C = "-c";

    /**
     * ssh conn key
     * */
    public static final String MASTER_ID_RSA = "/home/" + CUSTOM_USER_NAME + "/.ssh/master-key";
    public static final String CLUSTER_PRIVATE_KEY(String clusterName) {
        return "/home/" + CUSTOM_USER_NAME + "/.ssh/" + clusterName + "-key";
    }
    /*********************************************************************************************************/

    /**
     * file name & directory
     * */
    public static final String TERRAFORM_STATE_FILE_NAME = "terraform.tfstate";
    public static final String TERRAFORM_STATE_FILE_PATH(String clusterPath) {
        return clusterPath + "/terraform.tfstate";
    }
    public static final String NCLOUD_PRI_FILE_PATH(String clusterPath, String clusterId) {
        return clusterPath + "/" + clusterId + "-key";
    }
    public static final String NCLOUD_PUB_FILE_PATH(String clusterPath, String clusterId) {
        return clusterPath + "/" + clusterId + "-key.pub";
    }

    public static final String NCLOUD_PUB_FILE_PATH(String clusterPath) {
        return clusterPath + "/authorized_keys";
    }

    public static final String FILE_PATH(String clusterPath) {
        return clusterPath + "/provider.tf";
    }

    public static final String NCLOUD_PRIVATE_KEY_FILE_PATH(String clusterPath, String clusterId) {
        return clusterPath + "/" + clusterId + "-key";
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
     * ncloud provider prefix 생성
     * */
    public static final String PREFIX_PROVIDER_NCLOUD = "terraform {\n" +
            "  required_providers {\n" +
            "    ncloud = {\n" +
            "      source  = \"NaverCloudPlatform/ncloud\"\n" +
            "    }\n" +
            "  }\n" +
            "  required_version = \">= 0.13\"\n" +
            "}";


    /**
     * kubespray cluster cp-cluster-terraman-vars.sh 변경 및 실행 명령어
     * */
    public static final String CLUSTER_KUBESPRAY_SH_FILE_COMMAND(String contents) {
        return "echo -e \"" + contents + "\" > /home/ubuntu/cp-deployment/standalone/cp-cluster-terraman-vars.sh";
    }
    public static final String KUBESPRAY_CHMOD_COMMAND = "chmod +x deploy-cp-cluster-terraman.sh";
    public static final String CLUSTER_KUBESPRAY_DEPLOY_COMMAND = "source deploy-cp-cluster-terraman.sh";

    /**
     * keys 권한 변경
     * */
    public static final String KEYS_CHANGE_MOD = "find /home/1000/.ssh/ -type f -exec chmod -v 600 {} \\;";

    /**
     * keys 권한 변경
     * */
    public static final String NCLOUD_PRIVATE_KEY_CHANGE_MOD(String clusterId){
        return "chmod 600 " + clusterId + "-key";
    }

    /**
     * keys 문자 치환
     * */
    public static final String NCLOUD_PRIVATE_KEY_SED_QUOTES_REPLACE(String clusterId) {
        return "sed -i 's/\"//g' /tmp/terraform/ncp-cluster/" + clusterId + "-key";
    }

    /**
     * keys 문자 줄바꿈
     * */
    public static final String NCLOUD_PRIVATE_KEY_SED_NEW_LINE(String clusterId){
        return "sed -i 's/,/\\n/g' /tmp/terraform/ncp-cluster/" + clusterId + "-key";
    }

    /**
     * change directory 명령어
     * */
    public static final String CREATE_DIR_CLUSTER(String clusterId) {
        return "mkdir -p -v tmp/terraform/"+clusterId;
    }
    public static final String CREATE_DIR_SSH_FILE = "mkdir -p -v .ssh/";
    public static final String CREATE_NCLOUD_PUBLIC_KEY(String clusterId) {
        return "ssh-keygen -f " + clusterId + "-key -y > authorized_keys";
    };
    public static final String COPY_NCLOUD_PUBLIC_KEY(String clusterId) {
        return "cp authorized_keys " + clusterId + "-key.pub";
    };

    public static final String CLUSTER_STATE_DIR(String clusterId) {
        return "tmp/terraform/" + clusterId;
    }

    public static final String MOVE_DIR_CLUSTER(String clusterId) {
        return CLUSTER_STATE_DIR(clusterId);
    }
    public static final String DELETE_DIR_CLUSTER = "/home/ubuntu/tmp/terraform";
    public static final String DELETE_CLUSTER(String clusterId) {
        return "rm -r "+clusterId;
    }
    public static final String MOVE_DIR_KUBESPRAY = "/home/ubuntu/cp-deployment/standalone/";

    /**
     * account token 생성 명령어
     * */
    public static final String SERVICE_ACCOUNT_CHECK1 = "kubectl get ns";
    public static final String SERVICE_ACCOUNT_CHECK2 = "sudo kubectl get ns";
    public static final String SERVICE_ACCOUNT_CREATE = "sudo kubectl create serviceaccount k8sadmin -n kube-system";
    public static final String SERVICE_ACCOUNT_BINDING = "sudo kubectl create clusterrolebinding k8sadmin --clusterrole=cluster-admin --serviceaccount=kube-system:k8sadmin";
    public static final String SERVICE_ACCOUNT_TOKEN = "sudo kubectl create token k8sadmin --duration=999999h -n kube-system";
//    public static final String SERVICE_ACCOUNT_SECRET_NAME = "sudo kubectl describe serviceaccount k8sadmin -n kube-system | grep 'Mountable secrets'";
//    public static final String SERVICE_ACCOUNT_TOKEN(String secrets) {
//        return "sudo kubectl describe secret " + secrets.substring(secrets.indexOf("k8sadmin")) + " -n kube-system | grep -E '^token' | cut -f2 -d':' | tr -d \" \"";
//    }

    /**
     * .tf 파일 복사 명령어
     * */
    public static  final String POD_NAME_COMMAND = "kubectl get pods -n cp-portal -l app=cp-portal-api --field-selector=status.phase=Running -o custom-columns=:metadata.name | grep 'cp-portal-api-deployment'";
    public static final String INSTANCE_COPY_COMMAND(String pod, String clusterId) {
        return "kubectl cp -n cp-portal " + pod + ":tmp/terraform/" + clusterId + " /home/ubuntu/tmp/terraform/"+clusterId;
    }

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
    public static final String TERRAFORM_CREATE_CLUSTER_PARAMETER_ERROR = "ERROR - Failed to create cluster. Please check the parameters.";
    public static final String TERRAFORM_CREATE_CLUSTER_DIRECTORY_ERROR = "ERROR - cluster directory create failed";
    public static final String TERRAFORM_NOT_EXISTS_INSTANCE_ERROR = "ERROR - Instance is not exists";
    public static final String TERRAFORM_NOT_EXISTS_PRIVATE_IP_ERROR = "ERROR - privateIp is not exists";
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
    public static final String TERRAFORM_SSH_CONNECTION_FAIL = "ERROR - SSH CONNECTION FAILED";
    public static final String TERRAFORM_SSH_CONNECTION_TIME_OUT = "ERROR - SSH CONNECTION TIME OUT";
    public static final String TERRAFORM_SSH_MASTER_CONNECTION_TIME_OUT = "ERROR - SSH MASTER CONNECTION TIME OUT";


    /**
     * cluster 생성 상태값
     * */
    public static final String CLUSTER_CREATE_STATUS = "C";
    public static final String CLUSTER_COMPLETE_STATUS = "A";
    public static final String CLUSTER_FAIL_STATUS = "D";

    /**
     * terraman InstanceService 상수
     */
    public static final String CONTAINER_MSG = "CONTAINER";
    public static final String RESOURCE_MSG = "resources";
    public static final String MASTER_MSG = "master";
    public static final String MASTER_MSG_UPPER = "MASTER";
    public static final String SERVER_MSG_UPPER = "SERVER";
    public static final String PUBLIC_IP_MSG_UPPER = "PUBLIC";
    public static final String NIC_MSG_UPPER = "NIC";
    public static final String INSTANCES_MSG = "instances";
    public static final String INSTANCE_MSG = "instance";
    public static final String SERVER_MSG = "server";
    public static final String LOGIN_KEY_MSG = "login_key";
    public static final String NIC_MSG = "nic";
    public static final String INTERFACE_MSG = "interface";
    public static final String ATTRIBUTE_MSG = "attributes";
    public static final String PRIVATE_IP_MSG = "private_ip";
    public static final String PRIVATE_MSG = "private";
    public static final String PRIVATE_MSG_UPPER = "PRIVATE";
    public static final String PUBLIC_IP_MSG = "public_ip";
    public static final String PUBLIC_MSG = "public";
    public static final String PUBLIC_MSG_UPPER = "PUBLIC";
    public static final String TYPE_MSG = "type";
    public static final String NAME_MSG = "name";
    public static final String AWS_INSTANCE_MSG = "aws_instance";
    public static final String NCLOUD_INSTANCE_MSG = "ncloud_instance";

    public static final String MODE_MSG = "mode";
    public static final String MANAGED_MSG = "managed";
    public static final String ID_MSG = "id";
    public static final String ISTANCE_NO_MSG = "instance_no";
    public static final String PRIVATE_KEY_MSG = "private_key";
    public static final String ACCESS_IP_V4_MSG = "access_ip_v4";

    public static final String FLOATINGIP_MSG= "floatingip";
    public static final String INSTANCE_ID_MSG= "instance_id";
    public static final String SERVER_INSTANCE_NO_MSG= "server_instance_no";
    public static final String FLOATING_IP_MSG= "floating_ip";
    public static final String NETWORK_INTERFACE_MSG= "network_interface";

    /**
     * terraman kuberspray vars
     */
    public static final String KUBERSPRAY_VARS_EXPORT_WORKER = "export WORKER";
    public static final String KUBERSPRAY_VARS_HOSTNAME = "_NODE_HOSTNAME=";
    public static final String KUBERSPRAY_VARS_PUBLIC_IP = "_NODE_PUBLIC_IP=";
    public static final String KUBERSPRAY_VARS_PRIVATE_IP = "_NODE_PRIVATE_IP=";
    public static final String KUBERSPRAY_VARS_PRIVATE_KEY = "export CLUSTER_PRIVATE_KEY=";

    public static final String COMMAND_SWITCH(TerramanCommandModel terramanCommandModel) {
        String switchStr = "";
        switch(terramanCommandModel.getCommand()) {
            case "1" : switchStr = CREATE_DIR_CLUSTER(terramanCommandModel.getClusterId()); break;
            case "2" : switchStr = DIRECTORY_COMMAND; break;
            case "3" : switchStr = POD_NAME_COMMAND; break;
            case "4" : switchStr = TERRAFORM_INIT_COMMAND; break;
            case "5" : switchStr = TERRAFORM_PLAN_COMMAND; break;
            case "6" : switchStr = TERRAFORM_APPLY_COMMAND; break;
            case "7" : switchStr = CLUSTER_KUBESPRAY_SH_FILE_COMMAND(terramanCommandModel.getContents()); break;
            case "8" : switchStr = KUBESPRAY_CHMOD_COMMAND; break;
            case "9" : switchStr = CLUSTER_KUBESPRAY_DEPLOY_COMMAND; break;
            case "10" : switchStr = SERVICE_ACCOUNT_CREATE; break;
            case "11" : switchStr = SERVICE_ACCOUNT_BINDING; break;
//            case "12" : switchStr = SERVICE_ACCOUNT_SECRET_NAME; break;
            case "13" : switchStr = SERVICE_ACCOUNT_TOKEN; break;
            case "14" : switchStr = INSTANCE_COPY_COMMAND(terramanCommandModel.getPod(), terramanCommandModel.getClusterId()); break;
            case "15" : switchStr = CREATE_DIR_CLUSTER(terramanCommandModel.getClusterId()); break;
            case "16" : switchStr = KEYS_CHANGE_MOD; break;
            case "17" : switchStr = SERVICE_ACCOUNT_CHECK1; break;
            case "18" : switchStr = SERVICE_ACCOUNT_CHECK2; break;
            case "19" : switchStr = CREATE_DIR_SSH_FILE; break;
            case "20" : switchStr = NCLOUD_PRIVATE_KEY_CHANGE_MOD(terramanCommandModel.getClusterId()); break;
            case "21" : switchStr = NCLOUD_PRIVATE_KEY_SED_QUOTES_REPLACE(terramanCommandModel.getClusterId()); break;
            case "22" : switchStr = NCLOUD_PRIVATE_KEY_SED_NEW_LINE(terramanCommandModel.getClusterId()); break;
            case "23" : switchStr = CREATE_NCLOUD_PUBLIC_KEY(terramanCommandModel.getClusterId()); break;
            case "24" : switchStr = COPY_NCLOUD_PUBLIC_KEY(terramanCommandModel.getClusterId()); break;
        }
        return switchStr;
    }
}

