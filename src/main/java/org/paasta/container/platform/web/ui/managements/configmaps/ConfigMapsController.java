package org.paasta.container.platform.web.ui.managements.configmaps;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.paasta.container.platform.web.ui.common.ConstantsUrl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


/**
 * Configmaps Controller 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.05.10
 **/
@Api(value = "ConfigMapsController v1")
@Controller
public class ConfigMapsController {

    private static final String BASE_URL = "configMaps/";

    /**
     * Configmaps 목록 페이지 이동(Go to the configMaps list page)
     *
     * @return the view
     */
    @ApiOperation(value = "ConfigMaps 목록 페이지 이동(Go to the configmaps list page)", nickname = "getConfigMapsList")
    @GetMapping(value = ConstantsUrl.URI_CP_MANAGEMENTS_CONFIGMAPS )
    public String getConfigMapsList() {
        return BASE_URL + "configMaps";
    }


    /**
     * Configmaps 상세 페이지 이동(Go to the configmaps details page)
     *
     * @return the view
     */
    @ApiOperation(value = "ConfigMaps 상세 페이지 이동(Go to the configmaps details page)", nickname = "getConfigMapsDetails")
    @GetMapping(value = ConstantsUrl.URI_CP_MANAGEMENTS_CONFIGMAPS + ConstantsUrl.URI_CP_DETAILS)
    public String getConfigMapsDetails() {
        return BASE_URL + "configMapsDetail";
    }

}
