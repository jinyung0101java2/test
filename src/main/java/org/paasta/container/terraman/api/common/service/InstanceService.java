package org.paasta.container.terraman.api.common.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.paasta.container.terraman.api.common.constants.Constants;
import org.paasta.container.terraman.api.common.constants.TerramanConstant;
import org.paasta.container.terraman.api.common.model.InstanceModel;
import org.paasta.container.terraman.api.common.util.CommonFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;


@Service
public class InstanceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(InstanceService.class);
    private static final String containerMsg = "CONTAINER";

    private final CommonFileUtils commonFileUtils;
    private final CommandService commandService;

    @Autowired
    public InstanceService(CommonFileUtils commonFileUtils, CommandService commandService) {
        this.commonFileUtils = commonFileUtils;
        this.commandService = commandService;
    }

    /**
     * Select Instance Info
     *
     * @param clusterId the clusterId
     * @param provider the provider
     * @param host the host
     * @param idRsa the idRsa
     * @param processGb the processGb
     * @return the InstanceModel
     */
    public InstanceModel getInstance(String clusterId, String provider, String host, String idRsa, String processGb) {
        InstanceModel resultModel = null;

        switch(provider.toUpperCase()) {
            case Constants.UPPER_AWS : resultModel = getInstanceInfoAws(clusterId, host, idRsa, processGb); break;
            case Constants.UPPER_GCP : resultModel = getInstanceInfoGcp(); break;
            case Constants.UPPER_VSPHERE : resultModel = getInstanceInfoVSphere(); break;
            case Constants.UPPER_OPENSTACK : resultModel = getInstanceInfoOpenstack(clusterId, host, idRsa, processGb); break;
            default : LOGGER.error("%s is Cloud not supported.", provider);
                resultModel = new InstanceModel("", "", "", "");
                break;
        }

        return resultModel;
    }

    /**
     * Select Instances Info
     *
     * @param clusterId the clusterId
     * @param provider the provider
     * @param host the host
     * @param idRsa the idRsa
     * @param processGb the processGb
     * @return the List<InstanceModel>
     */
    public List<InstanceModel> getInstances(String clusterId, String provider, String host, String idRsa, String processGb) {
        List<InstanceModel> resultList = new ArrayList<>();

        switch (provider.toUpperCase()) {
            case Constants.UPPER_AWS : resultList = getInstancesInfoAws(clusterId, host, idRsa, processGb); break;
            case Constants.UPPER_GCP : resultList = getInstancesInfoGcp(); break;
            case Constants.UPPER_VSPHERE : resultList = getInstancesInfoVSphere(); break;
            case Constants.UPPER_OPENSTACK : resultList = getInstancesInfoOpenstack(clusterId, host, idRsa, processGb); break;
            default : LOGGER.error("%s is Cloud not supported.", provider); break;
        }

        return resultList;
    }

    /**
     * Select Instance Info AWS
     *
     * @param clusterId the cluster
     * @param host the host
     * @param idRsa the idRsa
     * @param processGb the processGb
     * @return the InstanceModel
     */
    private InstanceModel getInstanceInfoAws(String clusterId, String host, String idRsa, String processGb) {
        InstanceModel resultModel = null;
        if(!StringUtils.isBlank(processGb) && StringUtils.equals(processGb.toUpperCase(), containerMsg)) {
            commandService.sshFileDownload(TerramanConstant.CLUSTER_STATE_DIR(clusterId)
                    , TerramanConstant.TERRAFORM_STATE_FILE_PATH(TerramanConstant.CLUSTER_STATE_DIR(clusterId))
                    , TerramanConstant.TERRAFORM_STATE_FILE_NAME
                    , host
                    , idRsa);
        }
        JsonObject jsonObject = readStateFile(clusterId, processGb);
        String rName = "";
        String privateIp = "";
        String publicIp = "";
        String hostName = "";
        if((!jsonObject.isJsonNull()) && jsonObject.size() > 0) {
            JsonArray resources = (JsonArray) jsonObject.get("resources");
            for(JsonElement resource : resources) {
                if(StringUtils.equals(resource.getAsJsonObject().get("type").getAsString(), "aws_instance")) {
                    rName = resource.getAsJsonObject().get("name").getAsString();
                    if(StringUtils.equals(rName, "master")) {
                        JsonArray instances = resource.getAsJsonObject().get("instances").isJsonNull() ? null : resource.getAsJsonObject().get("instances").getAsJsonArray();
                        if(instances != null) {
                            for(JsonElement instance : instances) {
                                JsonObject attributes = (JsonObject) instance.getAsJsonObject().get("attributes");
                                privateIp = attributes.get("private_ip").isJsonNull() ? "" : attributes.get("private_ip").getAsString();
                                publicIp = attributes.get("public_ip").isJsonNull() ? "" : attributes.get("public_ip").getAsString();
                                hostName = getAWSHostName(privateIp);
                            }
                        }
                    }
                }
            }
            resultModel = new InstanceModel(rName, hostName, privateIp, publicIp);
        }
        return resultModel;
    }

    /**
     * Select Instance Info GCP
     *
     * @return the InstanceModel
     */
    private InstanceModel getInstanceInfoGcp() {
        return new InstanceModel("", "", "", "");
    }

    /**
     * Select Instance Info VSphere
     *
     * @return the InstanceModel
     */
    private InstanceModel getInstanceInfoVSphere() {
        return new InstanceModel("", "", "", "");
    }

    /**
     * Select Instance Info OpenStack
     *
     * @param clusterId the clusterId
     * @param host the host
     * @param idRsa the idRsa
     * @param processGb the processGb
     * @return the InstanceModel
     */
    private InstanceModel getInstanceInfoOpenstack(String clusterId, String host, String idRsa, String processGb) {
        InstanceModel resultModel = null;
        if(!StringUtils.isBlank(processGb) && StringUtils.equals(processGb.toUpperCase(), containerMsg)) {
            commandService.sshFileDownload(TerramanConstant.CLUSTER_STATE_DIR(clusterId)
                    , TerramanConstant.TERRAFORM_STATE_FILE_PATH(TerramanConstant.CLUSTER_STATE_DIR(clusterId))
                    , TerramanConstant.TERRAFORM_STATE_FILE_NAME
                    , host
                    , idRsa);
        }

        JsonObject jsonObject = readStateFile(clusterId, processGb);
        String rName = "";
        String privateIp = "";
        String publicIp = "";
        String hostName = "";
        String compInstanceId = "";

        if((!jsonObject.isJsonNull()) && jsonObject.size() > 0) {
            JsonArray resources = (JsonArray) jsonObject.get("resources");
            for(JsonElement resource : resources) {
                if(StringUtils.equals(resource.getAsJsonObject().get("mode").getAsString(), "managed")) {
                    if(StringUtils.contains(resource.getAsJsonObject().get("type").getAsString(), "instance")) {
                        if(StringUtils.contains(resource.getAsJsonObject().get("name").getAsString(), "master")) {
                            rName = resource.getAsJsonObject().get("name").getAsString();
                            JsonArray instances = resource.getAsJsonObject().get("instances").isJsonNull() ? null : resource.getAsJsonObject().get("instances").getAsJsonArray();
                            if(instances != null) {
                                for(JsonElement instance : instances) {
                                    JsonObject attributes = (JsonObject) instance.getAsJsonObject().get("attributes");
                                    compInstanceId = attributes.get("id").isJsonNull() ? "" : attributes.get("id").getAsString();
                                    privateIp = attributes.get("access_ip_v4").isJsonNull() ? "" : attributes.get("access_ip_v4").getAsString();
                                    publicIp = getPublicIp(compInstanceId, jsonObject, privateIp);
                                    hostName = attributes.get("name").isJsonNull() ? "" : attributes.get("name").getAsString();
                                }
                            }
                        }
                    }
                }
            }
            resultModel = new InstanceModel(rName, hostName, privateIp, publicIp);
        }

        return resultModel;
    }

    /**
     * Select Instances Info AWS
     *
     * @param clusterId the clusterId
     * @param host the host
     * @param idRsa the idRsa
     * @param processGb the processGb
     * @return the InstanceModel
     */
    private List<InstanceModel> getInstancesInfoAws(String clusterId, String host, String idRsa, String processGb) {
        List<InstanceModel> modelList = new ArrayList<>();
        if(!StringUtils.isBlank(processGb) && StringUtils.equals(processGb.toUpperCase(), containerMsg)) {
            commandService.sshFileDownload(TerramanConstant.CLUSTER_STATE_DIR(clusterId)
                    , TerramanConstant.TERRAFORM_STATE_FILE_PATH(TerramanConstant.CLUSTER_STATE_DIR(clusterId))
                    , TerramanConstant.TERRAFORM_STATE_FILE_NAME
                    , host
                    , idRsa);
        }

        JsonObject jsonObject = readStateFile(clusterId, processGb);
        String rName = "";
        String privateIp = "";
        String publicIp = "";
        String hostName = "";
        if((!jsonObject.isJsonNull()) && jsonObject.size() > 0) {
            JsonArray resources = (JsonArray) jsonObject.get("resources");
            for(JsonElement resource : resources) {
                if(StringUtils.equals(resource.getAsJsonObject().get("type").getAsString(), "aws_instance")) {
                    rName = resource.getAsJsonObject().get("name").getAsString();
                    JsonArray instances = resource.getAsJsonObject().get("instances").isJsonNull() ? null : resource.getAsJsonObject().get("instances").getAsJsonArray();
                    if(instances != null) {
                        for(JsonElement instance : instances) {
                            JsonObject attributes = (JsonObject) instance.getAsJsonObject().get("attributes");
                            privateIp = attributes.get("private_ip").isJsonNull() ? "" : attributes.get("private_ip").getAsString();
                            publicIp = attributes.get("public_ip").isJsonNull() ? "" : attributes.get("public_ip").getAsString();
                            hostName = getAWSHostName(privateIp);
                        }
                        modelList.add(new InstanceModel(rName, hostName, privateIp, publicIp));
                    }
                }
            }
        }
        return modelList;
    }

    /**
     * Select Instances Info GCP
     *
     * @return the List<InstanceModel>
     */
    private List<InstanceModel> getInstancesInfoGcp() {
        return new ArrayList<>();
    }

    /**
     * Select Instances Info VSphere
     *
     * @return the List<InstanceModel>
     */
    private List<InstanceModel> getInstancesInfoVSphere() {
        return new ArrayList<>();
    }

    /**
     * Select Instances Info OpenStack
     *
     * @param clusterId the clusterId
     * @param host the host
     * @param idRsa the idRsa
     * @param processGb the processGb
     * @return the InstanceModel
     */
    private List<InstanceModel> getInstancesInfoOpenstack(String clusterId, String host, String idRsa, String processGb) {
        List<InstanceModel> modelList = new ArrayList<>();
        if(!StringUtils.isBlank(processGb) && StringUtils.equals(processGb.toUpperCase(), containerMsg)) {
            commandService.sshFileDownload(TerramanConstant.CLUSTER_STATE_DIR(clusterId)
                    , TerramanConstant.TERRAFORM_STATE_FILE_PATH(TerramanConstant.CLUSTER_STATE_DIR(clusterId))
                    , TerramanConstant.TERRAFORM_STATE_FILE_NAME
                    , host
                    , idRsa);
        }
        JsonObject jsonObject = readStateFile(clusterId, processGb);
        String rName = "";
        String privateIp = "";
        String publicIp = "";
        String hostName = "";
        String compInstanceId = "";

        if((!jsonObject.isJsonNull()) && jsonObject.size() > 0) {
            JsonArray resources = (JsonArray) jsonObject.get("resources");
            for(JsonElement resource : resources) {
                if(StringUtils.equals(resource.getAsJsonObject().get("mode").getAsString(), "managed")
                && StringUtils.contains(resource.getAsJsonObject().get("type").getAsString(), "instance")) {
                    rName = resource.getAsJsonObject().get("name").getAsString();
                    JsonArray instances = resource.getAsJsonObject().get("instances").isJsonNull() ? null : resource.getAsJsonObject().get("instances").getAsJsonArray();
                    if(instances != null) {
                        for(JsonElement instance : instances) {
                            JsonObject attributes = (JsonObject) instance.getAsJsonObject().get("attributes");
                            compInstanceId = attributes.get("id").isJsonNull() ? "" : attributes.get("id").getAsString();
                            privateIp = attributes.get("access_ip_v4").isJsonNull() ? "" : attributes.get("access_ip_v4").getAsString();
                            publicIp = getPublicIp(compInstanceId, jsonObject, privateIp);
                            hostName = attributes.get("name").isJsonNull() ? "" : attributes.get("name").getAsString();
                        }
                        modelList.add(new InstanceModel(rName, hostName, privateIp, publicIp));
                    }
                }
            }
        }
        return modelList;
    }

    /**
     * Read State File
     *
     * @param clusterId the clusterId
     * @param processGb the processGb
     * @return the JsonObject
     */
    private JsonObject readStateFile(String clusterId, String processGb) {
        return commonFileUtils.fileRead(TerramanConstant.TERRAFORM_STATE_FILE_PATH(TerramanConstant.MOVE_DIR_CLUSTER(clusterId, processGb)));
    }

    /**
     * Get Public Ip
     *
     * @param compInstanceId the compInstanceId
     * @param jsonObject the jsonObject
     * @param privateIp the privateIp
     * @return the String
     */
    private String getPublicIp(String compInstanceId, JsonObject jsonObject, String privateIp) {
        String publicIp = privateIp;
        if((!jsonObject.isJsonNull()) && jsonObject.size() > 0) {
            JsonArray resources = (JsonArray) jsonObject.get("resources");
            for (JsonElement resource : resources) {
                if (StringUtils.equals(resource.getAsJsonObject().get("mode").getAsString(), "managed")
                && StringUtils.contains(resource.getAsJsonObject().get("type").getAsString(), "floatingip")) {
                    JsonArray instances = resource.getAsJsonObject().get("instances").isJsonNull() ? null : resource.getAsJsonObject().get("instances").getAsJsonArray();
                    if (instances != null) {
                        for (JsonElement instance : instances) {
                            JsonObject attributes = (JsonObject) instance.getAsJsonObject().get("attributes");
                            String instanceId = attributes.get("instance_id").isJsonNull() ? "" : attributes.get("instance_id").getAsString();
                            if (instanceId.equals(compInstanceId)) {
                                publicIp = attributes.get("floating_ip").isJsonNull() ? privateIp : attributes.get("floating_ip").getAsString();
                            }
                        }
                    }
                }
            }
        }
        return publicIp;
    }

    /**
     * Get AWS HostName
     *
     * @param ipAddr the ip address
     * @return the String
     */
    private String getAWSHostName(String ipAddr) {
        String rst = "ip-";
        String ipString = ipAddr.replaceAll("[.]","-");
        rst += ipString;
        return rst;
    }
}
