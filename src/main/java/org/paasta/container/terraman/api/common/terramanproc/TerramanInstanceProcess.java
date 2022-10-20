package org.paasta.container.terraman.api.common.terramanproc;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.paasta.container.terraman.api.common.constants.TerramanConstant;
import org.paasta.container.terraman.api.common.model.InstanceModel;

import java.util.ArrayList;
import java.util.List;

public class TerramanInstanceProcess {

    /**
     * Select Instance Info AWS
     *
     * @param jsonObject the jsonObject
     * @return the InstanceModel
     */
    public InstanceModel getInstanceInfoAws(JsonObject jsonObject) {
        InstanceModel resultModel = null;

        String rName = "";
        String privateIp = "";
        String publicIp = "";
        String hostName = "";
        if((jsonObject != null) &&(!jsonObject.isJsonNull()) && jsonObject.size() > 0) {
            JsonArray resources = (JsonArray) jsonObject.get(TerramanConstant.RESOURCE_MSG);
            for(JsonElement resource : resources) {
                if(StringUtils.equals(resource.getAsJsonObject().get(TerramanConstant.TYPE_MSG).getAsString(), TerramanConstant.AWS_INSTANCE_MSG)) {
                    rName = resource.getAsJsonObject().get(TerramanConstant.NAME_MSG).getAsString();
                    if(StringUtils.equals(rName, TerramanConstant.MASTER_MSG)) {
                        JsonArray instances = resource.getAsJsonObject().get(TerramanConstant.INSTANCES_MSG).isJsonNull() ? null : resource.getAsJsonObject().get(TerramanConstant.INSTANCES_MSG).getAsJsonArray();
                        if(instances != null) {
                            for(JsonElement instance : instances) {
                                JsonObject attributes = (JsonObject) instance.getAsJsonObject().get(TerramanConstant.ATTRIBUTE_MSG);
                                privateIp = attributes.get(TerramanConstant.PRIVATE_IP_MSG).isJsonNull() ? "" : attributes.get(TerramanConstant.PRIVATE_IP_MSG).getAsString();
                                publicIp = attributes.get(TerramanConstant.PUBLIC_IP_MSG).isJsonNull() ? "" : attributes.get(TerramanConstant.PUBLIC_IP_MSG).getAsString();
                                hostName = getAWSHostName(privateIp);
                            }
                        }
                    }
                }
            }

        }
        resultModel = new InstanceModel(rName, hostName, privateIp, publicIp);

        return resultModel;
    }

    /**
     * Select Instance Info OpenStack
     *
     * @param jsonObject the jsonObject
     * @return the InstanceModel
     */
    public InstanceModel getInstanceInfoOpenstack(JsonObject jsonObject) {
        InstanceModel resultModel = null;

        String rName = "";
        String privateIp = "";
        String publicIp = "";
        String hostName = "";
        String compInstanceId = "";
        if((jsonObject != null) && (!jsonObject.isJsonNull()) && jsonObject.size() > 0) {
            JsonArray resources = (JsonArray) jsonObject.get(TerramanConstant.RESOURCE_MSG);
            for(JsonElement resource : resources) {
                if(StringUtils.equals(resource.getAsJsonObject().get(TerramanConstant.MODE_MSG).getAsString(), TerramanConstant.MANAGED_MSG)
                        && StringUtils.contains(resource.getAsJsonObject().get(TerramanConstant.TYPE_MSG).getAsString(), TerramanConstant.INSTANCE_MSG)
                        && StringUtils.contains(resource.getAsJsonObject().get(TerramanConstant.NAME_MSG).getAsString(), TerramanConstant.MASTER_MSG)) {
                    rName = resource.getAsJsonObject().get(TerramanConstant.NAME_MSG).getAsString();
                    JsonArray instances = resource.getAsJsonObject().get(TerramanConstant.INSTANCES_MSG).isJsonNull() ? null : resource.getAsJsonObject().get(TerramanConstant.INSTANCES_MSG).getAsJsonArray();
                    if(instances != null) {
                        for(JsonElement instance : instances) {
                            JsonObject attributes = (JsonObject) instance.getAsJsonObject().get(TerramanConstant.ATTRIBUTE_MSG);
                            compInstanceId = attributes.get(TerramanConstant.ID_MSG).isJsonNull() ? "" : attributes.get(TerramanConstant.ID_MSG).getAsString();
                            privateIp = attributes.get(TerramanConstant.ACCESS_IP_V4_MSG).isJsonNull() ? "" : attributes.get(TerramanConstant.ACCESS_IP_V4_MSG).getAsString();
                            publicIp = getPublicIp(compInstanceId, jsonObject, privateIp);
                            hostName = attributes.get(TerramanConstant.NAME_MSG).isJsonNull() ? "" : attributes.get(TerramanConstant.NAME_MSG).getAsString();
                        }
                    }
                }
            }
        }
        resultModel = new InstanceModel(rName, hostName, privateIp, publicIp);

        return resultModel;
    }

    /**
     * Select Instances Info AWS
     *
     * @param jsonObject the jsonObject
     * @return the InstanceModel
     */
    public List<InstanceModel> getInstancesInfoAws(JsonObject jsonObject) {
        List<InstanceModel> modelList = new ArrayList<>();

        String rName = "";
        String privateIp = "";
        String publicIp = "";
        String hostName = "";
        if((jsonObject != null) &&(!jsonObject.isJsonNull()) && jsonObject.size() > 0) {
            JsonArray resources = (JsonArray) jsonObject.get(TerramanConstant.RESOURCE_MSG);
            for(JsonElement resource : resources) {
                if(StringUtils.equals(resource.getAsJsonObject().get(TerramanConstant.TYPE_MSG).getAsString(), TerramanConstant.AWS_INSTANCE_MSG)) {
                    rName = resource.getAsJsonObject().get(TerramanConstant.NAME_MSG).getAsString();
                    JsonArray instances = resource.getAsJsonObject().get(TerramanConstant.INSTANCES_MSG).isJsonNull() ? null : resource.getAsJsonObject().get(TerramanConstant.INSTANCES_MSG).getAsJsonArray();
                    if(instances != null) {
                        for(JsonElement instance : instances) {
                            JsonObject attributes = (JsonObject) instance.getAsJsonObject().get(TerramanConstant.ATTRIBUTE_MSG);
                            privateIp = attributes.get(TerramanConstant.PRIVATE_IP_MSG).isJsonNull() ? "" : attributes.get(TerramanConstant.PRIVATE_IP_MSG).getAsString();
                            publicIp = attributes.get(TerramanConstant.PUBLIC_IP_MSG).isJsonNull() ? "" : attributes.get(TerramanConstant.PUBLIC_IP_MSG).getAsString();
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
     * Select Instances Info OpenStack
     *
     * @param jsonObject the jsonObject
     * @return the InstanceModel
     */
    public List<InstanceModel> getInstancesInfoOpenstack(JsonObject jsonObject) {
        List<InstanceModel> modelList = new ArrayList<>();

        String rName = "";
        String privateIp = "";
        String publicIp = "";
        String hostName = "";
        String compInstanceId = "";

        if((jsonObject != null) &&(!jsonObject.isJsonNull()) && jsonObject.size() > 0) {
            JsonArray resources = (JsonArray) jsonObject.get(TerramanConstant.RESOURCE_MSG);
            for(JsonElement resource : resources) {
                if(StringUtils.equals(resource.getAsJsonObject().get(TerramanConstant.MODE_MSG).getAsString(), TerramanConstant.MANAGED_MSG)
                        && StringUtils.contains(resource.getAsJsonObject().get(TerramanConstant.TYPE_MSG).getAsString(), TerramanConstant.INSTANCE_MSG)) {
                    rName = resource.getAsJsonObject().get(TerramanConstant.NAME_MSG).getAsString();
                    JsonArray instances = resource.getAsJsonObject().get(TerramanConstant.INSTANCES_MSG).isJsonNull() ? null : resource.getAsJsonObject().get(TerramanConstant.INSTANCES_MSG).getAsJsonArray();
                    if(instances != null) {
                        for(JsonElement instance : instances) {
                            JsonObject attributes = (JsonObject) instance.getAsJsonObject().get(TerramanConstant.ATTRIBUTE_MSG);
                            compInstanceId = attributes.get(TerramanConstant.ID_MSG).isJsonNull() ? "" : attributes.get(TerramanConstant.ID_MSG).getAsString();
                            privateIp = attributes.get(TerramanConstant.ACCESS_IP_V4_MSG).isJsonNull() ? "" : attributes.get(TerramanConstant.ACCESS_IP_V4_MSG).getAsString();
                            publicIp = getPublicIp(compInstanceId, jsonObject, privateIp);
                            hostName = attributes.get(TerramanConstant.NAME_MSG).isJsonNull() ? "" : attributes.get(TerramanConstant.NAME_MSG).getAsString();
                        }
                        modelList.add(new InstanceModel(rName, hostName, privateIp, publicIp));
                    }
                }
            }
        }
        return modelList;
    }

    /**
     * Get Public Ip
     *
     * @param compInstanceId the compInstanceId
     * @param jsonObject the jsonObject
     * @param privateIp the privateIp
     * @return the String
     */
    public String getPublicIp(String compInstanceId, JsonObject jsonObject, String privateIp) {
        String publicIp = privateIp;
        if((!jsonObject.isJsonNull()) && jsonObject.size() > 0) {
            JsonArray resources = (JsonArray) jsonObject.get(TerramanConstant.RESOURCE_MSG);
            for (JsonElement resource : resources) {
                if (StringUtils.equals(resource.getAsJsonObject().get(TerramanConstant.MODE_MSG).getAsString(), TerramanConstant.MANAGED_MSG)
                        && StringUtils.contains(resource.getAsJsonObject().get(TerramanConstant.TYPE_MSG).getAsString(), TerramanConstant.FLOATINGIP_MSG)) {
                    JsonArray instances = resource.getAsJsonObject().get(TerramanConstant.INSTANCES_MSG).isJsonNull() ? null : resource.getAsJsonObject().get(TerramanConstant.INSTANCES_MSG).getAsJsonArray();
                    if (instances != null) {
                        for (JsonElement instance : instances) {
                            JsonObject attributes = (JsonObject) instance.getAsJsonObject().get(TerramanConstant.ATTRIBUTE_MSG);
                            String instanceId = attributes.get(TerramanConstant.INSTANCE_ID_MSG).isJsonNull() ? "" : attributes.get(TerramanConstant.INSTANCE_ID_MSG).getAsString();
                            if (instanceId.equals(compInstanceId)) {
                                publicIp = attributes.get(TerramanConstant.FLOATING_IP_MSG).isJsonNull() ? privateIp : attributes.get(TerramanConstant.FLOATING_IP_MSG).getAsString();
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
    public String getAWSHostName(String ipAddr) {
        String rst = "ip-";
        String ipString = ipAddr.replaceAll("[.]","-");
        rst += ipString;
        return rst;
    }
}
