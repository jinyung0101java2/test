package org.paasta.container.platform.web.admin.managements.resourceQuotas;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.paasta.container.platform.web.admin.common.ConstantsUrl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * ResourceQuotas Controller 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2021.05.04
 **/
@Api(value = "ResourceQuotasController v1")
@Controller
public class ResourceQuotasController {

    private static final String BASE_URL = "resourceQuotas/";

    /**
     * ResourceQuotas 목록 페이지 이동(Go to the resourceQuotas list page)
     *
     * @return the view
     */
    @ApiOperation(value = "ResourceQuotas 목록 페이지 이동(Go to the resourceQuotas list page)", nickname = "getLimitResourceQuotasList")
    @GetMapping(value = ConstantsUrl.URI_CP_MANAGEMENTS_RESOURCEQUOTAS )
    public String getLimitResourceQuotasList() {
        return BASE_URL + "resourceQuotas";
    }


    /**
     * ResourceQuotas 상세 페이지 이동(Go to the resourceQuotas details page)
     *
     * @return the view
     */
    @ApiOperation(value = "ResourceQuotas 상세 페이지 이동(Go to the resourceQuotas details page)", nickname = "getResourceQuotasDetails")
    @GetMapping(value = ConstantsUrl.URI_CP_MANAGEMENTS_RESOURCEQUOTAS + ConstantsUrl.URI_CP_DETAILS)
    public String getResourceQuotasDetails() {
        return BASE_URL + "resourceQuotasDetail";
    }
}
