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
import org.paasta.container.terraman.api.common.constants.Constants;
import org.paasta.container.terraman.api.common.model.ResultStatusModel;
import org.paasta.container.terraman.api.common.service.*;
import org.paasta.container.terraman.api.common.util.CommonFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.FileReader;
import java.io.Reader;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

import static org.paasta.container.terraman.api.common.util.CommonUtils.getSysTimestamp;
import static org.paasta.container.terraman.api.common.util.CommonUtils.procSetTimestamp;

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

    private final CommonService commonService;
    private final VaultTemplate vaultTemplate;
    private final TerramanService terramanService;
    private final CommonFileUtils commonFileUtils;
    private final ClusterService clusterService;
    private final ClusterLogService clusterLogService;
    private final VaultService vaultService;
    private final PropertyService propertyService;
    private final TfFileService tfFileService;
    private final CommandService commandService;

    @Value("${master.host}")
    private String MASTER_HOST;

    @Autowired
    public TerramanController(
            CommonService commonService
            , TerramanService terramanService
            , CommonFileUtils commonFileUtils
            , ClusterService clusterService
            , ClusterLogService clusterLogService
            , VaultService vaultService
            , PropertyService propertyService
            , VaultTemplate vaultTemplate
            , TfFileService tfFileService
            , CommandService commandService) {
        this.commonService = commonService;
        this.terramanService = terramanService;
        this.commonFileUtils = commonFileUtils;
        this.clusterService = clusterService;
        this.clusterLogService = clusterLogService;
        this.vaultService = vaultService;
        this.propertyService = propertyService;
        this.vaultTemplate = vaultTemplate;
        this.tfFileService = tfFileService;
        this.commandService = commandService;
    }

    /**
     * Terraman 생성(Create Terraman) - Container 실행
     *
     * @param terramanRequest the terramanRequest
     * @return the resultStatus
     */
    @ApiOperation(value = "Terraman 생성(Create Terraman) - Container 실행", nickname = "initTerraman")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "terramanRequest", value = "Terraman 생성 정보", required = true, dataType = "TerramanRequest", paramType = "body"),
            @ApiImplicitParam(name = "processGb", value = "Terraman 생성 구분", required = false, dataType = "string", paramType = "path")
    })
    @PostMapping(value = "/create/{processGb:.+}")
    public void initTerraman(@RequestBody TerramanRequest terramanRequest, @PathVariable String processGb) {
        terramanService.createTerraman(terramanRequest, processGb);
    }

    /**
     * Terraman 생성(Create Terraman) - Daemon 실행
     *
     * @param terramanRequest the terramanRequest
     * @return the resultStatus
     */
    @ApiOperation(value = "Terraman 생성(Create Terraman) - Daemon 실행", nickname = "initTerraman")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "terramanRequest", value = "Terraman 생성 정보", required = true, dataType = "TerramanRequest", paramType = "body")
    })
    @PostMapping(value = "/create")
    public void initTerraman(@RequestBody TerramanRequest terramanRequest) {
        terramanService.createTerraman(terramanRequest, "Daemon");
    }

    /**
     * Terraman 삭제(Delete Terraman) - Container 실행
     *
     * @return the resultStatus
     */
    @ApiOperation(value = "Terraman 삭제(Delete Terraman) - Container 실행", nickname = "deleteTerraman")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "clusterId", value = "Terraman 삭제 정보", required = true, dataType = "string", paramType = "path", defaultValue = "terraform-cluster"),
            @ApiImplicitParam(name = "processGb", value = "Terraman 삭제 구분", required = false, dataType = "string", paramType = "path")
    })
    @DeleteMapping(value = "/{clusterId:.+}/{processGb:.+}")
    public ResultStatusModel deleteTerraman(
            @PathVariable String clusterId
            , @PathVariable String processGb) {
//        return terramanService.deleteTerraman(clusterId, processGb);
        return (ResultStatusModel) commonService.setResultModel(new ResultStatusModel(), Constants.RESULT_STATUS_FAIL);
    }

    /**
     * Terraman 삭제(Delete Terraman) - Daemon 실행
     *
     * @return the resultStatus
     */
    @ApiOperation(value = "Terraman 삭제(Delete Terraman) - Daemon 실행", nickname = "deleteTerraman")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "clusterId", value = "Terraman 삭제 정보", required = true, dataType = "string", paramType = "path", defaultValue = "terraform-cluster")
    })
    @DeleteMapping(value = "/{clusterId:.+}")
    public ResultStatusModel deleteTerraman(@PathVariable String clusterId) {
//        return terramanService.deleteTerraman(clusterId, "Daemon");
        return (ResultStatusModel) commonService.setResultModel(new ResultStatusModel(), Constants.RESULT_STATUS_FAIL);
    }

    @GetMapping(value = "/test")
    public void test() {
        String result = "";
        result = commandService.getSSHResponse("sudo chmod 666 /etc/hosts", "", "15.164.195.107", "/home/ubuntu/.ssh/paasta-master-key");
        LOGGER.info("first :: {}", result);
        result = commandService.getSSHResponse("echo \"15.164.195.107 foo.bar.com.new\" >> /etc/hosts", "", "15.164.195.107", "/home/ubuntu/.ssh/paasta-master-key");
        LOGGER.info("second :: {}", result);
        result = commandService.getSSHResponse("sudo chmod 644 /etc/hosts", "", "15.164.195.107", "/home/ubuntu/.ssh/paasta-master-key");
        LOGGER.info("third :: {}", result);
    }

    @GetMapping(value = "/test2")
    public void test2() {
    }
}
