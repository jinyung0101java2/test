package org.paasta.container.terraman.api.terraman;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.paasta.container.terraman.api.common.model.ResultStatusModel;
import org.paasta.container.terraman.api.common.service.ClusterLogService;
import org.paasta.container.terraman.api.common.service.ClusterService;
import org.paasta.container.terraman.api.common.util.CommonFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

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

    private final TerramanService terramanService;
    private final CommonFileUtils commonFileUtils;
    private final ClusterService clusterService;
    private final ClusterLogService clusterLogService;
    @Value("${master.host}")
    private String MASTER_HOST;

    @Autowired
    public TerramanController(TerramanService terramanService, CommonFileUtils commonFileUtils, ClusterService clusterService, ClusterLogService clusterLogService) {
        this.terramanService = terramanService;
        this.commonFileUtils = commonFileUtils;
        this.clusterService = clusterService;
        this.clusterLogService = clusterLogService;
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
    @PostMapping(value = "/create")
    public ResultStatusModel initTerraman(@RequestBody TerramanRequest terramanRequest) {
        return terramanService.createTerraman(terramanRequest);
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
    @DeleteMapping(value = "/remove/{clusterId:.+}")
    public ResultStatusModel deleteTerraman(
            @PathVariable String clusterId) {
        return terramanService.deleteTerraman(clusterId);
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
        LOGGER.info("system env :: " + System.getenv("MASTER_HOST"));
        LOGGER.info("test yml value :: " + MASTER_HOST);
    }
}
