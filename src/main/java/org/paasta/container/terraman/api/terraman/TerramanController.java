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
     * @return the resultStatus
     */
    @ApiOperation(value = "Terraman 삭제(Delete Terraman)", nickname = "deleteTerraman")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "clusterId", value = "Terraman 삭제 정보", required = true, dataType = "string", paramType = "path", defaultValue = "test_cluster")
    })
    @DeleteMapping(value = "/{clusterId:.+}")
    public ResultStatusModel deleteTerraman(
            @PathVariable String clusterId) {
        return terramanService.deleteTerraman(clusterId);
    }
}
