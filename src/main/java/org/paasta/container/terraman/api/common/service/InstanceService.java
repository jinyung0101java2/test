package org.paasta.container.terraman.api.common.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.paasta.container.terraman.api.common.constants.TerramanConstant;
import org.paasta.container.terraman.api.common.model.InstanceModel;
import org.paasta.container.terraman.api.common.util.CommonFileUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.paasta.container.terraman.api.common.util.CommonUtils.hostName;

@Service
public class InstanceService {

    /**
     * Instance 정보 조회 (Get Instance Info)
     *
     * @return the InstanceModel
     */
    public InstanceModel getInstansceInfo() {
        InstanceModel resultModel = null;
        JsonObject jsonObject = readStateFile();

        if((!jsonObject.isJsonNull()) && jsonObject.size() > 0) {
            for(JsonElement obj : jsonObject.get("iaasAws").getAsJsonObject().getAsJsonArray("Resources")) {
                String rName = String.valueOf(obj.getAsJsonObject().get("Name"));
                for(JsonElement subObj : obj.getAsJsonObject().getAsJsonArray("Instances")) {
                    JsonElement attr = subObj.getAsJsonObject().get("Attributes");
                    String prvateIp = String.valueOf(attr.getAsJsonObject().get("PrivateIP"));
                    String publicIp = String.valueOf(attr.getAsJsonObject().get("PublicIp"));
                    String hostName = hostName(prvateIp);
                    resultModel = new InstanceModel(rName, hostName, prvateIp, publicIp);
                }
            }
        }

        return resultModel;
    }

    /**
     * Total Instance 정보 조회 (Get Total Instance Info)
     *
     * @return the List<InstanceModel>
     */
    public List<InstanceModel> getTotalInstance() {
        List<InstanceModel> modelList = new ArrayList<>();
        JsonObject jsonObject = readStateFile();

        if((!jsonObject.isJsonNull()) && jsonObject.size() > 0) {
            for (JsonElement obj : jsonObject.get("gcp").getAsJsonObject().getAsJsonArray("Resources")) {
                String rsourceName = String.valueOf(obj.getAsJsonObject().get("Name"));

                for (JsonElement subObj : obj.getAsJsonObject().getAsJsonArray("Instances")) {
                    JsonElement attr = subObj.getAsJsonObject().get("Attributes");
                    String instanceName = String.valueOf(attr.getAsJsonObject().get("Name"));

                    for (int i = 0; i < attr.getAsJsonObject().getAsJsonArray("NetworkInterface").size(); i++) {
                        String networkIp = String.valueOf(
                                attr.getAsJsonObject()
                                        .getAsJsonArray("NetworkInterface")
                                        .get(1)
                                        .getAsJsonObject()
                                        .get("NetworkIp")
                        );
                        int netSize = attr.getAsJsonObject()
                                .getAsJsonArray("NetworkInterface")
                                .get(1)
                                .getAsJsonObject()
                                .getAsJsonArray("AccessConfig")
                                .size();

                        for (int j = 0; j < netSize; j++) {
                            String natIp = String.valueOf(
                                    attr.getAsJsonObject()
                                            .getAsJsonArray("NetworkInterface")
                                            .get(1)
                                            .getAsJsonObject()
                                            .getAsJsonArray("AccessConfig")
                                            .get(j)
                                            .getAsJsonObject()
                                            .get("NatIp")
                            );
                            modelList.add(new InstanceModel(rsourceName, instanceName, networkIp, natIp));
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
    private JsonObject readStateFile() {
        CommonFileUtils instanceInfo = new CommonFileUtils();
        return instanceInfo.tfFileRead(TerramanConstant.TERRAFORM_STATE);
    }
}
