package org.paasta.container.terraman.api.terraman;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.paasta.container.terraman.api.common.model.ResultStatusModel;
import org.paasta.container.terraman.api.common.service.ClusterLogService;
import org.paasta.container.terraman.api.common.service.ClusterService;
import org.paasta.container.terraman.api.common.service.PropertyService;
import org.paasta.container.terraman.api.common.service.VaultService;
import org.paasta.container.terraman.api.common.util.CommonFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.FileReader;
import java.io.Reader;

/**
 * Terraman Controller 클래스
 *
 * @author yjh
 * @version 1.0
 * @since 2022.07.11
 */
@Api(value = "TerramanController v1")
@RestController
@RequestMapping(value = "/clusters")
public class TerramanController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TerramanController.class);

    private final VaultTemplate vaultTemplate;
    private final TerramanService terramanService;
    private final CommonFileUtils commonFileUtils;
    private final ClusterService clusterService;
    private final ClusterLogService clusterLogService;
    private final VaultService vaultService;
    private final PropertyService propertyService;
    @Value("${master.host}")
    private String MASTER_HOST;

    @Autowired
    public TerramanController(TerramanService terramanService
            , CommonFileUtils commonFileUtils
            , ClusterService clusterService
            , ClusterLogService clusterLogService
            , VaultService vaultService
            , PropertyService propertyService
            , VaultTemplate vaultTemplate) {
        this.terramanService = terramanService;
        this.commonFileUtils = commonFileUtils;
        this.clusterService = clusterService;
        this.clusterLogService = clusterLogService;
        this.vaultService = vaultService;
        this.propertyService = propertyService;
        this.vaultTemplate = vaultTemplate;
    }

    /**
     * Terraman 생성(Create Terraman)
     *
     * @param terramanRequest the terramanRequest
     * @return the resultStatus
     */
    @ApiOperation(value = "Terraman 생성(Create Terraman)", nickname = "initTerraman")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "terramanRequest", value = "Terraman 생성 정보", required = true, dataType = "TerramanRequest", paramType = "body"),
            @ApiImplicitParam(name = "processGb", value = "Terraman 생성 구분", required = false, dataType = "string", paramType = "path")
    })
    @PostMapping(value = "/create/{processGb:.+}")
    public ResultStatusModel initTerraman(@RequestBody TerramanRequest terramanRequest, @PathVariable String processGb) {
        LOGGER.info("terraman parameter :: " + terramanRequest.toString());
        return terramanService.createTerraman(terramanRequest, processGb);
    }

    /**
     * Terraman 삭제(Delete Terraman)
     *
     * @return the resultStatus
     */
    @ApiOperation(value = "Terraman 삭제(Delete Terraman)", nickname = "deleteTerraman")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "clusterId", value = "Terraman 삭제 정보", required = true, dataType = "string", paramType = "path", defaultValue = "terraform-cluster"),
            @ApiImplicitParam(name = "processGb", value = "Terraman 삭제 구분", required = false, dataType = "string", paramType = "path")
    })
    @DeleteMapping(value = "/{clusterId:.+}/{processGb:.+}")
    public ResultStatusModel deleteTerraman(
            @PathVariable String clusterId
            , @PathVariable String processGb) {
        return terramanService.deleteTerraman(clusterId, processGb);
    }

    /**
     * Terraman 삭제(Delete Terraman)
     *
     * @return the resultStatus
     */
    @ApiOperation(value = "Terraman 삭제(Delete Terraman)", nickname = "deleteTerraman")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "clusterId", value = "Terraman 삭제 정보", required = true, dataType = "string", paramType = "path", defaultValue = "test_cluster")
    })
    @PostMapping(value = "/test/{clusterId:.+}")
    public void test(@PathVariable String clusterId) {
        //clusterLogService.saveClusterLog("test", 0, "test");
//        ClusterModel aa = clusterService.updateCluster(clusterId, TerramanConstant.CLUSTER_COMPLETE_STATUS);
//        System.out.println(aa);
//        String sysProp = System.getProperty("CP_PORTAL_DB_SCHEMA");
//        LOGGER.info("system env :: " + System.getenv("MASTER_HOST"));
//        LOGGER.info("test yml value :: " + MASTER_HOST);

//        Object ret;
//        String path = propertyService.getCpVaultPathProviderCredential()
//                .replace("{iaas}", params.getProviderType().name()).replace("{id}", params.getResourceUid());
//        try {
//            ret = vaultService.read(path, ((Map)(getProviderInfoList(params))).get(params.getProviderType().name()).getClass());
//        } catch (Exception e) {
//            LOGGER.info("Error from getProviderInfoFromVault!");
//        }

//        String path = "secret/AWS/10";
//        HashMap res = vaultService.read(path, new HashMap().getClass());
//        LOGGER.info("valut key :: " + res.toString());
//        String path2 = "secret/OPENSTACK/13";
//        HashMap res2 = vaultService.read(path2, new HashMap().getClass());
//        LOGGER.info("valut key2 :: " + res2.toString());
//        String cluster = "terraform-aws";
//        String clusterApiUrl = "";
//        String clusterToken = "";
//
//        ClusterInfo clusterInfo = new ClusterInfo();
//        clusterInfo.setClusterId(cluster);
//        clusterInfo.setClusterApiUrl(clusterApiUrl);
//        clusterInfo.setClusterToken(clusterToken);
//
//        try {
//            vaultService.write(propertyService.getVaultClusterTokenPath().replace("{id}", cluster), clusterInfo);
//        } catch (Exception e) {
//            LOGGER.info("Vault Write failed in createClusterInfoToVault");
//        }
//        String rst = "ip-";
//        String ipString = "172.10.0.133".replaceAll("[.]","-");
//        rst += ipString;
//        LOGGER.info(rst);
//        ClusterInfo clusterInfo = new ClusterInfo();
//        clusterInfo.setClusterId("test1");
//        clusterInfo.setClusterToken("test2");
//        clusterInfo.setClusterApiUrl("test3");
//        vaultService.write("secret/cluster/terraform-test", clusterInfo);
//        String aa = "Mountable secrets:   k8sadmin-token-gtpwb";
//        int index = aa.indexOf("k8sadmin");
//        LOGGER.info("11 :: " + index);
//        aa = aa.substring(index);
//        LOGGER.info(aa);
        JsonObject vaultResponse = vaultService.read("secret/cluster/terraform-aws", JsonObject.class);
        LOGGER.info(vaultResponse.toString());
    }

    /**
     * Terraman 삭제(Delete Terraman)
     *
     * @return the resultStatus
     */
    @ApiOperation(value = "Terraman 삭제(Delete Terraman)", nickname = "deleteTerraman")
    @PostMapping(value = "/test2")
    public void test2() {
        try{
            Reader reader = new FileReader("C:\\terraman_dev\\terraform_test.tfstate");

            // Json 파일 읽어서, Lecture 객체로 변환
            Gson gson = new Gson();
            JsonObject obj = gson.fromJson(reader, JsonObject.class);
            JsonArray modules = obj.getAsJsonArray("modules");
            for(JsonElement module : modules) {
                JsonObject resources = (JsonObject) module.getAsJsonObject().get("resources");
                resources.keySet().forEach(key -> {
                    JsonObject resource = (JsonObject) resources.get(key);
                    if(!StringUtils.equals("data.vsphere_virtual_machine.template", key)) {
                        if(StringUtils.equals(resource.get("type").getAsString(), "vsphere_virtual_machine") ) {
                            JsonObject primary = (JsonObject) resource.get("primary");
                            JsonObject attributes = (JsonObject) primary.get("attributes");
                            String ip = attributes.get("default_ip_address").getAsString();
                            LOGGER.info("{ " + key + " } ip :: " + ip);
                        }
                    }
                });
            }
        } catch (Exception e) {

        }
    }
}
