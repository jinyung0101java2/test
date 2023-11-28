package org.container.terraman.api.common.terramanproc;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.container.terraman.api.common.constants.Constants;
import org.container.terraman.api.common.constants.TerramanConstant;
import org.container.terraman.api.common.model.InstanceModel;
import org.container.terraman.api.common.model.NcloudPrivateKeyModel;

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
//                    if(StringUtils.equals(rName, TerramanConstant.MASTER_MSG)) {
                    if(StringUtils.contains(rName, TerramanConstant.MASTER_MSG) || StringUtils.contains(rName, TerramanConstant.MASTER_MSG_UPPER)) {
                        JsonArray instances = resource.getAsJsonObject().get(TerramanConstant.INSTANCES_MSG).isJsonNull() ? null : resource.getAsJsonObject().get(TerramanConstant.INSTANCES_MSG).getAsJsonArray();
                        if(instances != null) {
                            for(JsonElement instance : instances) {
                                JsonObject attributes = (JsonObject) instance.getAsJsonObject().get(TerramanConstant.ATTRIBUTE_MSG);
                                privateIp = attributes.get(TerramanConstant.PRIVATE_IP_MSG).isJsonNull() ? "" : attributes.get(TerramanConstant.PRIVATE_IP_MSG).getAsString();
                                publicIp = attributes.get(TerramanConstant.PUBLIC_IP_MSG).isJsonNull() ? privateIp : attributes.get(TerramanConstant.PUBLIC_IP_MSG).getAsString();
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
                        && (StringUtils.contains(resource.getAsJsonObject().get(TerramanConstant.NAME_MSG).getAsString(), TerramanConstant.MASTER_MSG)
                        || StringUtils.contains(resource.getAsJsonObject().get(TerramanConstant.NAME_MSG).getAsString(), TerramanConstant.MASTER_MSG_UPPER)) ) {
                    rName = resource.getAsJsonObject().get(TerramanConstant.NAME_MSG).getAsString();
                    JsonArray instances = resource.getAsJsonObject().get(TerramanConstant.INSTANCES_MSG).isJsonNull() ? null : resource.getAsJsonObject().get(TerramanConstant.INSTANCES_MSG).getAsJsonArray();
                    if(instances != null) {
                        for(JsonElement instance : instances) {
                            JsonObject attributes = (JsonObject) instance.getAsJsonObject().get(TerramanConstant.ATTRIBUTE_MSG);
                            compInstanceId = attributes.get(TerramanConstant.ID_MSG).isJsonNull() ? "" : attributes.get(TerramanConstant.ID_MSG).getAsString(); //0f20b6ec-0d1e-4c93-9abf-b2391d29d9b3
                            privateIp = attributes.get(TerramanConstant.ACCESS_IP_V4_MSG).isJsonNull() ? "" : attributes.get(TerramanConstant.ACCESS_IP_V4_MSG).getAsString(); //192.168.0.19
                            publicIp = getOpenstackPublicIp(compInstanceId, jsonObject, privateIp);
                            hostName = attributes.get(TerramanConstant.NAME_MSG).isJsonNull() ? "" : attributes.get(TerramanConstant.NAME_MSG).getAsString(); //cp-cluster-master
                        }
                    }
                }
            }
        }
        resultModel = new InstanceModel(rName, hostName, privateIp, publicIp);

        return resultModel;
    }

    /**
     * Select Instance Info Nhn
     *
     * @param jsonObject the jsonObject
     * @return the InstanceModel
     */
    public InstanceModel getInstanceInfoNhn(JsonObject jsonObject) {
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
                        && (StringUtils.contains(resource.getAsJsonObject().get(TerramanConstant.NAME_MSG).getAsString(), TerramanConstant.MASTER_MSG)
                        || StringUtils.contains(resource.getAsJsonObject().get(TerramanConstant.NAME_MSG).getAsString(), TerramanConstant.MASTER_MSG_UPPER)) ) {
                    rName = resource.getAsJsonObject().get(TerramanConstant.NAME_MSG).getAsString();
                    JsonArray instances = resource.getAsJsonObject().get(TerramanConstant.INSTANCES_MSG).isJsonNull() ? null : resource.getAsJsonObject().get(TerramanConstant.INSTANCES_MSG).getAsJsonArray();
                    if(instances != null) {
                        for(JsonElement instance : instances) {
                            JsonObject attributes = (JsonObject) instance.getAsJsonObject().get(TerramanConstant.ATTRIBUTE_MSG);
                            compInstanceId = attributes.get(TerramanConstant.ID_MSG).isJsonNull() ? "" : attributes.get(TerramanConstant.ID_MSG).getAsString(); //0f20b6ec-0d1e-4c93-9abf-b2391d29d9b3
                            privateIp = attributes.get(TerramanConstant.ACCESS_IP_V4_MSG).isJsonNull() ? "" : attributes.get(TerramanConstant.ACCESS_IP_V4_MSG).getAsString(); //192.168.0.19
                            publicIp = getNhnPublicIp(compInstanceId, jsonObject, privateIp);
                            hostName = attributes.get(TerramanConstant.NAME_MSG).isJsonNull() ? "" : attributes.get(TerramanConstant.NAME_MSG).getAsString(); //cp-cluster-master
                        }
                    }
                }
            }
        }
        resultModel = new InstanceModel(rName, hostName, privateIp, publicIp);

        return resultModel;
    }

    /**
     * Select Instance Info Ncloud
     *
     * @param jsonObject the jsonObject
     * @return the InstanceModel
     */

    public InstanceModel getInstanceInfoNcloud(JsonObject jsonObject) {
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
                        && StringUtils.contains(resource.getAsJsonObject().get(TerramanConstant.TYPE_MSG).getAsString(), TerramanConstant.SERVER_MSG)
                        && (StringUtils.contains(resource.getAsJsonObject().get(TerramanConstant.NAME_MSG).getAsString(), TerramanConstant.MASTER_MSG)
                        || StringUtils.contains(resource.getAsJsonObject().get(TerramanConstant.NAME_MSG).getAsString(), TerramanConstant.MASTER_MSG_UPPER)) ) {
                    JsonArray instances = resource.getAsJsonObject().get(TerramanConstant.INSTANCES_MSG).isJsonNull() ? null : resource.getAsJsonObject().get(TerramanConstant.INSTANCES_MSG).getAsJsonArray();
                    if (instances != null) {
                        for (JsonElement instance : instances) {
                            JsonObject attributes = (JsonObject) instance.getAsJsonObject().get(TerramanConstant.ATTRIBUTE_MSG);
                            compInstanceId = attributes.get(TerramanConstant.ID_MSG).isJsonNull() ? "" : attributes.get(TerramanConstant.ID_MSG).getAsString();
                            rName = attributes.get(TerramanConstant.NAME_MSG).isJsonNull() ? "" : attributes.get(TerramanConstant.NAME_MSG).getAsString();
                            hostName = attributes.get(TerramanConstant.NAME_MSG).isJsonNull() ? "" : attributes.get(TerramanConstant.NAME_MSG).getAsString();
                            JsonArray network_interface = (JsonArray) attributes.getAsJsonObject().get(TerramanConstant.NETWORK_INTERFACE_MSG);
                            if (network_interface != null) {
                                JsonObject members = (JsonObject) network_interface.get(0);
                                privateIp = String.valueOf(members.get(TerramanConstant.PRIVATE_IP_MSG)).replaceAll("\"","");
                            }
                            publicIp = getNcloudPublicIp(compInstanceId, jsonObject);
                        }
                    }
                }
            }
        }
        resultModel = new InstanceModel(rName, hostName, privateIp, publicIp);

        return resultModel;
    }


    /**
     * Select Ncloud Private Key Info
     *
     * @param jsonObject the jsonObject
     * @return the NcloudPrivateKeyModel
     */

    public NcloudPrivateKeyModel getNcloudPrivateKeyInfo(JsonObject jsonObject) {
        String resultCode = Constants.RESULT_STATUS_FAIL;
        NcloudPrivateKeyModel resultModel = null;

        String instanceNo = "";
        String privateKey = "";
        String publicIp = "";
        String encodeParameter = "";
        String signature = "";
        String rootPassword = "";

        if((jsonObject != null) && (!jsonObject.isJsonNull()) && jsonObject.size() > 0) {
            JsonArray resources = (JsonArray) jsonObject.get(TerramanConstant.RESOURCE_MSG);
            for(JsonElement resource : resources) {
                if(StringUtils.equals(resource.getAsJsonObject().get(TerramanConstant.MODE_MSG).getAsString(), TerramanConstant.MANAGED_MSG)
                        && StringUtils.contains(resource.getAsJsonObject().get(TerramanConstant.TYPE_MSG).getAsString(), TerramanConstant.LOGIN_KEY_MSG)) {
                    JsonArray instances = resource.getAsJsonObject().get(TerramanConstant.INSTANCES_MSG).isJsonNull() ? null : resource.getAsJsonObject().get(TerramanConstant.INSTANCES_MSG).getAsJsonArray();
                    if (instances != null) {
                        for (JsonElement instance : instances) {
                            JsonObject attributes = (JsonObject) instance.getAsJsonObject().get(TerramanConstant.ATTRIBUTE_MSG);
                            privateKey = attributes.get(TerramanConstant.PRIVATE_KEY_MSG).isJsonNull() ? "" : attributes.get(TerramanConstant.PRIVATE_KEY_MSG).getAsString();
                            privateKey = privateKey.replaceAll("(\r\n|\r|\n|\n\r)", "");
                        }
                    }
                }
                if(StringUtils.equals(resource.getAsJsonObject().get(TerramanConstant.MODE_MSG).getAsString(), TerramanConstant.MANAGED_MSG)
                        && StringUtils.contains(resource.getAsJsonObject().get(TerramanConstant.TYPE_MSG).getAsString(), TerramanConstant.SERVER_MSG)
                        && (StringUtils.contains(resource.getAsJsonObject().get(TerramanConstant.NAME_MSG).getAsString(), TerramanConstant.MASTER_MSG)
                        || StringUtils.contains(resource.getAsJsonObject().get(TerramanConstant.NAME_MSG).getAsString(), TerramanConstant.MASTER_MSG_UPPER)) ) {
                    JsonArray instances = resource.getAsJsonObject().get(TerramanConstant.INSTANCES_MSG).isJsonNull() ? null : resource.getAsJsonObject().get(TerramanConstant.INSTANCES_MSG).getAsJsonArray();
                    if (instances != null) {
                        for (JsonElement instance : instances) {
                            JsonObject attributes = (JsonObject) instance.getAsJsonObject().get(TerramanConstant.ATTRIBUTE_MSG);
                            instanceNo = attributes.get(TerramanConstant.ISTANCE_NO_MSG).isJsonNull() ? "" : attributes.get(TerramanConstant.ISTANCE_NO_MSG).getAsString();
                        }
                    }
                }
            }
        }
        resultModel = new NcloudPrivateKeyModel(instanceNo, privateKey, publicIp, encodeParameter, signature, rootPassword);

        return resultModel;
    }

    /**
     * Select Ncloud RSA Private Key Info
     *
     * @param jsonObject the jsonObject
     * @return the NcloudPrivateKeyModel
     */

    public NcloudPrivateKeyModel getNcloudRsaPrivateKeyInfo(JsonObject jsonObject) {
        String resultCode = Constants.RESULT_STATUS_FAIL;
        NcloudPrivateKeyModel resultModel = null;

        String instanceNo = "";
        String privateKey = "";
        String compInstanceId = "";
        String publicIp = "";
        String encodeParameter = "";
        String signature = "";
        String rootPassword = "";

        if((jsonObject != null) && (!jsonObject.isJsonNull()) && jsonObject.size() > 0) {
            JsonArray resources = (JsonArray) jsonObject.get(TerramanConstant.RESOURCE_MSG);
            for(JsonElement resource : resources) {
                if(StringUtils.equals(resource.getAsJsonObject().get(TerramanConstant.MODE_MSG).getAsString(), TerramanConstant.MANAGED_MSG)
                        && StringUtils.contains(resource.getAsJsonObject().get(TerramanConstant.TYPE_MSG).getAsString(), TerramanConstant.LOGIN_KEY_MSG)) {
                    JsonArray instances = resource.getAsJsonObject().get(TerramanConstant.INSTANCES_MSG).isJsonNull() ? null : resource.getAsJsonObject().get(TerramanConstant.INSTANCES_MSG).getAsJsonArray();
                    if (instances != null) {
                        for (JsonElement instance : instances) {
                            JsonObject attributes = (JsonObject) instance.getAsJsonObject().get(TerramanConstant.ATTRIBUTE_MSG);
                            compInstanceId = attributes.get(TerramanConstant.ID_MSG).isJsonNull() ? "" : attributes.get(TerramanConstant.ID_MSG).getAsString();
                            publicIp = getNcloudPublicIp(compInstanceId, jsonObject);
                            privateKey = attributes.get(TerramanConstant.PRIVATE_KEY_MSG).isJsonNull() ? "" : attributes.get(TerramanConstant.PRIVATE_KEY_MSG).getAsString();
                            privateKey = privateKey.replaceAll("\n", ",");
                        }
                    }
                }
            }
        }
        resultModel = new NcloudPrivateKeyModel(instanceNo, privateKey, publicIp, encodeParameter, signature, rootPassword);

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
                            publicIp = attributes.get(TerramanConstant.PUBLIC_IP_MSG).isJsonNull() ? privateIp : attributes.get(TerramanConstant.PUBLIC_IP_MSG).getAsString();
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
                            publicIp = getOpenstackPublicIp(compInstanceId, jsonObject, privateIp);
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
     * Select Instances Info Nhn
     *
     * @param jsonObject the jsonObject
     * @return the InstanceModel
     */
    public List<InstanceModel> getInstancesInfoNhn(JsonObject jsonObject) {
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
                            publicIp = getNhnPublicIp(compInstanceId, jsonObject, privateIp);
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
     * Select Instances Info Ncloud
     *
     * @param jsonObject the jsonObject
     * @return the List<InstanceModel>
     */
    public List<InstanceModel> getInstancesInfoNcloud(JsonObject jsonObject) {
        List<InstanceModel> modelList = new ArrayList<>();

        String rName = "";
        String privateIp = "";
        String publicIp = "";
        String hostName = "";
        String compInstanceId = "";
        if((jsonObject != null) && (!jsonObject.isJsonNull()) && jsonObject.size() > 0) {
            JsonArray resources = (JsonArray) jsonObject.get(TerramanConstant.RESOURCE_MSG);
            for(JsonElement resource : resources) {
                if(StringUtils.equals(resource.getAsJsonObject().get(TerramanConstant.MODE_MSG).getAsString(), TerramanConstant.MANAGED_MSG)
                        && StringUtils.contains(resource.getAsJsonObject().get(TerramanConstant.TYPE_MSG).getAsString(), TerramanConstant.SERVER_MSG)                         ) {
                    JsonArray instances = resource.getAsJsonObject().get(TerramanConstant.INSTANCES_MSG).isJsonNull() ? null : resource.getAsJsonObject().get(TerramanConstant.INSTANCES_MSG).getAsJsonArray();
                    if (instances != null) {
                        for (JsonElement instance : instances) {
                            JsonObject attributes = (JsonObject) instance.getAsJsonObject().get(TerramanConstant.ATTRIBUTE_MSG);
                            compInstanceId = attributes.get(TerramanConstant.ID_MSG).isJsonNull() ? "" : attributes.get(TerramanConstant.ID_MSG).getAsString();
                            rName = attributes.get(TerramanConstant.NAME_MSG).isJsonNull() ? "" : attributes.get(TerramanConstant.NAME_MSG).getAsString();
                            hostName = attributes.get(TerramanConstant.NAME_MSG).isJsonNull() ? "" : attributes.get(TerramanConstant.NAME_MSG).getAsString();
                            JsonArray network_interface = (JsonArray) attributes.getAsJsonObject().get(TerramanConstant.NETWORK_INTERFACE_MSG);
                            if (network_interface != null) {
                                JsonObject members = (JsonObject) network_interface.get(0);
                                privateIp = String.valueOf(members.get(TerramanConstant.PRIVATE_IP_MSG)).replaceAll("\"","");
                            }
                            publicIp = getNcloudPublicIp(compInstanceId, jsonObject);
                        }
                        modelList.add(new InstanceModel(rName, hostName, privateIp, publicIp));
                    }
                }

            }

        }
        return modelList;
    }

    /**
     * Select Ncloud Private Keys Info
     *
     * @param jsonObject the jsonObject
     * @return the List<NcloudPrivateKeyModel>
     */

    public List<NcloudPrivateKeyModel> getNcloudPrivateKeysInfo(JsonObject jsonObject) {
        String resultCode = Constants.RESULT_STATUS_FAIL;
        List<NcloudPrivateKeyModel> modelList = new ArrayList<>();

        String instanceNo = "";
        String privateKey = "";
        String compInstanceId = "";
        String publicIp = "";
        String encodeParameter = "";
        String signature = "";
        String rootPassword = "";

        if((jsonObject != null) && (!jsonObject.isJsonNull()) && jsonObject.size() > 0) {
            JsonArray resources = (JsonArray) jsonObject.get(TerramanConstant.RESOURCE_MSG);
            for(JsonElement resource : resources) {
                if(StringUtils.equals(resource.getAsJsonObject().get(TerramanConstant.MODE_MSG).getAsString(), TerramanConstant.MANAGED_MSG)
                        && StringUtils.contains(resource.getAsJsonObject().get(TerramanConstant.TYPE_MSG).getAsString(), TerramanConstant.LOGIN_KEY_MSG)) {
                    JsonArray instances = resource.getAsJsonObject().get(TerramanConstant.INSTANCES_MSG).isJsonNull() ? null : resource.getAsJsonObject().get(TerramanConstant.INSTANCES_MSG).getAsJsonArray();
                    if (instances != null) {
                        for (JsonElement instance : instances) {
                            JsonObject attributes = (JsonObject) instance.getAsJsonObject().get(TerramanConstant.ATTRIBUTE_MSG);
                            privateKey = attributes.get(TerramanConstant.PRIVATE_KEY_MSG).isJsonNull() ? "" : attributes.get(TerramanConstant.PRIVATE_KEY_MSG).getAsString();
                            privateKey = privateKey.replaceAll("(\r\n|\r|\n|\n\r)", "");
                        }
                    }
                }

                if(StringUtils.equals(resource.getAsJsonObject().get(TerramanConstant.MODE_MSG).getAsString(), TerramanConstant.MANAGED_MSG)
                        && StringUtils.contains(resource.getAsJsonObject().get(TerramanConstant.TYPE_MSG).getAsString(), TerramanConstant.SERVER_MSG)) {
                    JsonArray instances = resource.getAsJsonObject().get(TerramanConstant.INSTANCES_MSG).isJsonNull() ? null : resource.getAsJsonObject().get(TerramanConstant.INSTANCES_MSG).getAsJsonArray();
                    if (instances != null) {
                        for (JsonElement instance : instances) {
                            JsonObject attributes = (JsonObject) instance.getAsJsonObject().get(TerramanConstant.ATTRIBUTE_MSG);
                            compInstanceId = attributes.get(TerramanConstant.ID_MSG).isJsonNull() ? "" : attributes.get(TerramanConstant.ID_MSG).getAsString();
                            publicIp = getNcloudPublicIp(compInstanceId, jsonObject);
                            instanceNo = attributes.get(TerramanConstant.ISTANCE_NO_MSG).isJsonNull() ? "" : attributes.get(TerramanConstant.ISTANCE_NO_MSG).getAsString();
                        }
                        modelList.add(new NcloudPrivateKeyModel(instanceNo, privateKey, publicIp, encodeParameter, signature, privateKey));
                    }
                }
            }
        }

        return modelList;
    }

    /**
     * Get Openstack Public Ip
     *
     * @param compInstanceId the compInstanceId
     * @param jsonObject the jsonObject
     * @param privateIp the privateIp
     * @return the String
     */
    public String getOpenstackPublicIp(String compInstanceId, JsonObject jsonObject, String privateIp) {
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
     * Get Nhn Public Ip
     *
     * @param compInstanceId the compInstanceId
     * @param jsonObject the jsonObject
     * @param privateIp the privateIp
     * @return the String
     */
    public String getNhnPublicIp(String compInstanceId, JsonObject jsonObject, String privateIp) {
        String publicIp = privateIp;
        if((!jsonObject.isJsonNull()) && jsonObject.size() > 0) {
            JsonArray resources = (JsonArray) jsonObject.get(TerramanConstant.RESOURCE_MSG);
            for (JsonElement resource : resources) {
                if (StringUtils.equals(resource.getAsJsonObject().get(TerramanConstant.MODE_MSG).getAsString(), TerramanConstant.MANAGED_MSG)
                        && StringUtils.contains(resource.getAsJsonObject().get(TerramanConstant.TYPE_MSG).getAsString(), TerramanConstant.FLOATINGIP_ASSOCIATE_MSG)) {
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
     * Get Ncloud Public Ip
     *
     * @param compInstanceId the compInstanceId
     * @param jsonObject the jsonObject
     * @return the String
     */
    public String getNcloudPublicIp(String compInstanceId, JsonObject jsonObject) {
        String publicIp = "";
        if((!jsonObject.isJsonNull()) && jsonObject.size() > 0) {
            JsonArray resources = (JsonArray) jsonObject.get(TerramanConstant.RESOURCE_MSG);
            for (JsonElement resource : resources) {
                if (StringUtils.equals(resource.getAsJsonObject().get(TerramanConstant.MODE_MSG).getAsString(), TerramanConstant.MANAGED_MSG)
                        && StringUtils.contains(resource.getAsJsonObject().get(TerramanConstant.TYPE_MSG).getAsString(), TerramanConstant.PUBLIC_MSG)) {
                    JsonArray instances = resource.getAsJsonObject().get(TerramanConstant.INSTANCES_MSG).isJsonNull() ? null : resource.getAsJsonObject().get(TerramanConstant.INSTANCES_MSG).getAsJsonArray();
                    if (instances != null) {
                        for (JsonElement instance : instances) {
                            JsonObject attributes = (JsonObject) instance.getAsJsonObject().get(TerramanConstant.ATTRIBUTE_MSG);
                            String serverInstanceNo = attributes.get(TerramanConstant.SERVER_INSTANCE_NO_MSG).isJsonNull() ? "" : attributes.get(TerramanConstant.SERVER_INSTANCE_NO_MSG).getAsString();
                            if (serverInstanceNo.equals(compInstanceId)){
                                publicIp = attributes.get(TerramanConstant.PUBLIC_IP_MSG).isJsonNull() ? "" : attributes.get(TerramanConstant.PUBLIC_IP_MSG).getAsString();
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
