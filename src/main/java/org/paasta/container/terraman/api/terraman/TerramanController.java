package org.paasta.container.terraman.api.terraman;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.paasta.container.terraman.api.common.constants.TerramanConstant;
import org.paasta.container.terraman.api.common.model.InstanceModel;
import org.paasta.container.terraman.api.common.model.ResultStatusModel;
import org.paasta.container.terraman.api.common.util.CommonFileUtils;
import org.paasta.container.terraman.api.common.util.SSHUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.paasta.container.terraman.api.common.util.CommonUtils.hostName;

/**
 * Terraman Controller 클래스
 *
 * @author yjh
 * @version 1.0
 * @since 2022.07.11
 */
@Api(value = "TerramanController v1")
@RestController
@RequestMapping(value = "/terraman")
public class TerramanController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TerramanController.class);

    private final TerramanService terramanService;
    private final CommonFileUtils commonFileUtils;


    @Autowired
    public TerramanController(TerramanService terramanService, CommonFileUtils commonFileUtils) {
        this.terramanService = terramanService;
        this.commonFileUtils = commonFileUtils;
    }

    /**
     * Terraman 생성(Create Terraman)
     *
     * @param terramanRequest the terramanRequest
     * @return the resultStatus
     */
    @ApiOperation(value = "Terraman 생성(Create Terraman)", nickname = "initTerraman")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "terramanRequest", value = "Terraman 생성 정보", required = true, dataType = "TerramanRequest", paramType = "body")
    })
    @PostMapping
    public ResultStatusModel initTerraman(@RequestBody TerramanRequest terramanRequest) {
        return terramanService.createTerraman(terramanRequest);
    }

    /**
     * Terraman 삭제(Delete Terraman)
     *
     * @param terramanRequest the terramanRequest
     * @return the resultStatus
     */
    @ApiOperation(value = "Terraman 삭제(Delete Terraman)", nickname = "deleteTerraman")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "terramanRequest", value = "Terraman 삭제 정보", required = true, dataType = "TerramanRequest", paramType = "body")
    })
    @DeleteMapping(value = "/{clusterId:.+}")
    public ResultStatusModel deleteTerraman(
            @PathVariable String clusterId) {
        return terramanService.deleteTerraman(clusterId);
    }

    /**
     * test
     *
     */
    @ApiOperation(value = "test", nickname = "test")
    @PostMapping(value = "/test3")
    public void testTerraman3() {
        List<InstanceModel> modelList = new ArrayList<>();
        JsonObject jsonObject = commonFileUtils.tfFileRead("C:\\terraman\\terraform.tfstate");

        String rName = "", privateIp = "", publicIp = "", hostName = "", instanceId = "", compInstanceId = "";

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
                        }
                        modelList.add(new InstanceModel(rName, hostName, privateIp, publicIp));
                    }
                }
            }
        }
        System.out.println("resultMOdel :: " + modelList);
    }

    /**
     * test
     *
     */
    @ApiOperation(value = "test", nickname = "test")
    @PostMapping(value = "/test")
    public void testTerraman() {
        InstanceModel resultModel = null;
        JsonObject jsonObject = commonFileUtils.tfFileRead("C:\\terraman\\terraform.tfstate");

        String rName = "", privateIp = "", publicIp = "", hostName = "", instanceId = "", compInstanceId = "";

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
                            }
                        }
                    }
                }
            }
        }
        resultModel = new InstanceModel(rName, hostName, privateIp, publicIp);
        System.out.println("resultMOdel :: " + resultModel.toString());

//        if((!jsonObject.isJsonNull()) && jsonObject.size() > 0) {
//            for(JsonElement obj : jsonObject.getAsJsonObject().getAsJsonArray("resources")) {
//                //for(JsonElement obj : jsonObject.get("iaasAws").getAsJsonObject().getAsJsonArray("resources")) {
//                String rName = String.valueOf(obj.getAsJsonObject().get("name"));
//                for(JsonElement subObj : obj.getAsJsonObject().getAsJsonArray("instances")) {
//                    JsonElement attr = subObj.getAsJsonObject().get("attributes");
//                    String privateIp = String.valueOf(attr.getAsJsonObject().get("privateIP"));
//                    String publicIp = String.valueOf(attr.getAsJsonObject().get("publicIp"));
//                    String hostName = hostName(privateIp);
//                    resultModel = new InstanceModel(rName, hostName, privateIp, publicIp);
//                }
//            }
//        }

//        LOGGER.info(resultModel.toString());
    }

    /**
     * test
     *
     */
    @ApiOperation(value = "test", nickname = "test")
    @PostMapping(value = "/test2")
    public void testTerraman2() {
        SSHUtil sshUtil = new SSHUtil();
        String cResult = sshUtil.getSSHResponse("hostname", "203.255.255.118");
        LOGGER.info("test_cResult :: " + cResult);
    }

    /**
     * test
     *
     */
    @ApiOperation(value = "test", nickname = "test")
    @PostMapping(value = "/test4")
    public void testTerraman4() {
        List<InstanceModel> instanceList = new ArrayList<>();
        JsonObject jsonObject = commonFileUtils.tfFileRead("C:\\terraman\\terraform.tfstate");

        String rName = "", privateIp = "", publicIp = "", hostName = "", instanceId = "", compInstanceId = "";

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
                        }
                        instanceList.add(new InstanceModel(rName, hostName, privateIp, publicIp));
                    }
                }
            }
        }

        int workerCnt = instanceList.size()-1;
        int workerSeq = 1;
        StringBuffer sb = new StringBuffer();
        List<String> rst = new ArrayList<String>();
        sb.append(TerramanConstant.TERRAFORM_KUBESPRAY_COMMAND);
        for(InstanceModel obj : instanceList) {
            String line = "";
            if( obj.getResourceName().contains("master") ) {
                line = "export MASTER_NODE_HOSTNAME=" + obj.getInstanceName()
                        + "\\n"
                        + "export MASTER_NODE_PUBLIC_IP=" + obj.getPublicIp()
                        + "\\n"
                        + "export MASTER_NODE_PRIVATE_IP=" + obj.getPrivateIp();
            }
            sb.append(line);
        }

        sb.append("\\n\\n" + "export WORKER_NODE_CNT=" + workerCnt + "\\n");

        for(InstanceModel obj : instanceList) {
            String line = "";
            if( obj.getResourceName().contains("worker") ) {
                line = "\\n"
                        + "export WORKER" + workerSeq
                        + "_NODE_HOSTNAME=" + obj.getInstanceName()
                        + "\\n"
                        + "export WORKER" + workerSeq
                        + "_NODE_PRIVATE_IP=" + obj.getPrivateIp();
                workerSeq++;
            }
            sb.append(line);
        }

        LOGGER.info(sb.toString());
    }





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
