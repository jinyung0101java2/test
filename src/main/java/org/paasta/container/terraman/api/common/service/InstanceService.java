package org.paasta.container.terraman.api.common.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.swagger.models.auth.In;
import org.apache.commons.lang3.StringUtils;
import org.paasta.container.terraman.api.common.constants.Constants;
import org.paasta.container.terraman.api.common.constants.TerramanConstant;
import org.paasta.container.terraman.api.common.model.InstanceModel;
import org.paasta.container.terraman.api.common.util.CommonFileUtils;
import org.paasta.container.terraman.api.terraman.TerramanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.paasta.container.terraman.api.common.util.CommonUtils.hostName;

@Service
public class InstanceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(InstanceService.class);

    private final CommonFileUtils commonFileUtils;

    @Autowired
    public InstanceService(CommonFileUtils commonFileUtils) {
        this.commonFileUtils = commonFileUtils;
    }
    /**
     * Instance 정보 조회 (Get Instance Info)
     *
     * @return the InstanceModel
     */
    public InstanceModel getInstansce(String clusterId, String provider) {
        InstanceModel resultModel = null;
        if(StringUtils.equals(Constants.UPPER_AWS, provider.toUpperCase())) {
            resultModel = new InstanceModel("", "", "", "");
        } else if(StringUtils.equals(Constants.UPPER_GCP, provider.toUpperCase())) {
            resultModel = new InstanceModel("", "", "", "");
        } else if(StringUtils.equals(Constants.UPPER_OPENSTACK, provider.toUpperCase())) {
            resultModel = getInstanceInfo(clusterId);
        } else {
            LOGGER.error(provider + " is Cloud not supported.");
            resultModel = new InstanceModel("", "", "", "");
        }
        return resultModel;
    }

    /**
     * Instances 정보 조회 (Get Instances Info)
     *
     * @return the InstanceModel
     */
    public List<InstanceModel> getInstances(String clusterId, String provider) {
        List<InstanceModel> resultList = new ArrayList<InstanceModel>();
        if(StringUtils.equals(Constants.UPPER_AWS, provider.toUpperCase())) {
        } else if(StringUtils.equals(Constants.UPPER_GCP, provider.toUpperCase())) {
        } else if(StringUtils.equals(Constants.UPPER_OPENSTACK, provider.toUpperCase())) {
            resultList = getInstancesInfo(clusterId);
        } else {
            LOGGER.error(provider + " is Cloud not supported.");
        }
        return resultList;
    }

    /**
     * get Instance info
     *
     * @return the InstanceModel
     */
    private InstanceModel getInstanceInfo(String clusterId) {
        InstanceModel resultModel = null;
        JsonObject jsonObject = readStateFile(clusterId);
        String rName = "", privateIp = "", publicIp = "", hostName = "", compInstanceId = "";

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
                                    publicIp = getPublicIp(compInstanceId, jsonObject);
                                    hostName = attributes.get("name").isJsonNull() ? "" : attributes.get("name").getAsString();
                                    //hostName = hostName(privateIp);
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
     * get Instances Info
     *
     * @return the List<InstanceModel>
     */
    private List<InstanceModel> getInstancesInfo(String clusterId) {
        List<InstanceModel> modelList = new ArrayList<>();
        JsonObject jsonObject = readStateFile(clusterId);
        String rName = "", privateIp = "", publicIp = "", hostName = "", compInstanceId = "";

        if((!jsonObject.isJsonNull()) && jsonObject.size() > 0) {
            JsonArray resources = (JsonArray) jsonObject.get("resources");
            for(JsonElement resource : resources) {
                if(StringUtils.equals(resource.getAsJsonObject().get("mode").getAsString(), "managed")) {
                    if(StringUtils.contains(resource.getAsJsonObject().get("type").getAsString(), "instance")) {
                        rName = resource.getAsJsonObject().get("name").getAsString();
                        JsonArray instances = resource.getAsJsonObject().get("instances").isJsonNull() ? null : resource.getAsJsonObject().get("instances").getAsJsonArray();
                        if(instances != null) {
                            for(JsonElement instance : instances) {
                                JsonObject attributes = (JsonObject) instance.getAsJsonObject().get("attributes");
                                compInstanceId = attributes.get("id").isJsonNull() ? "" : attributes.get("id").getAsString();
                                privateIp = attributes.get("access_ip_v4").isJsonNull() ? "" : attributes.get("access_ip_v4").getAsString();
                                publicIp = getPublicIp(compInstanceId, jsonObject);
                                hostName = attributes.get("name").isJsonNull() ? "" : attributes.get("name").getAsString();
                                //hostName = hostName(privateIp);
                            }
                            modelList.add(new InstanceModel(rName, hostName, privateIp, publicIp));
                        }
                    }
                }
            }
        }
        return modelList;
    }

    /**
     * File Read (File)
     *
     * @return the JsonObject
     */
    private JsonObject readStateFile(String clusterId) {
        return commonFileUtils.tfFileRead(TerramanConstant.TERRAFORM_STATE_FILE_PATH(TerramanConstant.MOVE_DIR_CLUSTER(clusterId)));
    }

    /**
     * get Public IP
     *
     * @return the String
     */
    private String getPublicIp(String compInstanceId, JsonObject jsonObject) {
        String publicIp = "";
        if((!jsonObject.isJsonNull()) && jsonObject.size() > 0) {
            JsonArray resources = (JsonArray) jsonObject.get("resources");
            for (JsonElement resource : resources) {
                if (StringUtils.equals(resource.getAsJsonObject().get("mode").getAsString(), "managed")) {
                    if (StringUtils.contains(resource.getAsJsonObject().get("type").getAsString(), "floatingip")) {
                        JsonArray instances = resource.getAsJsonObject().get("instances").isJsonNull() ? null : resource.getAsJsonObject().get("instances").getAsJsonArray();
                        if (instances != null) {
                            for (JsonElement instance : instances) {
                                JsonObject attributes = (JsonObject) instance.getAsJsonObject().get("attributes");
                                String instanceId = attributes.get("instance_id").isJsonNull() ? "" : attributes.get("instance_id").getAsString();
                                if (instanceId.equals(compInstanceId)) {
                                    publicIp = attributes.get("floating_ip").isJsonNull() ? "" : attributes.get("floating_ip").getAsString();
                                }
                            }
                        }
                    }
                }
            }
        }
        return publicIp;
    }
}
