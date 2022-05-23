package org.paasta.container.platform.web.admin.services.services;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.paasta.container.platform.web.admin.common.ConstantsUrl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Services Controller 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2021.05.06
 */
@Api(value = "ServicesController v1")
@Controller
public class ServicesController {

    private static final String BASE_URL = "services/";

    /**
     * Services 목록 페이지 이동(Go to the services list page)
     *
     * @return the view
     */
    @ApiOperation(value = "Services 목록 페이지 이동(Go to the services list page)", nickname = "getServicesList")
    @GetMapping(value = ConstantsUrl.URI_CP_SERVICES_SERVICES )
    public String getServicesList() {
        return BASE_URL + "services";
    }

    /**
     * Services 상세 페이지 이동(Go to the services details page)
     *
     * @return the view
     */
    @ApiOperation(value = "Services 상세 페이지 이동(Go to the services details page)", nickname = "getServicesDetails")
    @GetMapping(value = ConstantsUrl.URI_CP_SERVICES_SERVICES + ConstantsUrl.URI_CP_DETAILS)
    public String getServicesDetails() {
        return BASE_URL + "servicesDetail";
    }

}