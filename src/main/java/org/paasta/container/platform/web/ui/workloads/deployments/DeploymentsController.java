package org.paasta.container.platform.web.ui.workloads.deployments;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.paasta.container.platform.web.ui.common.ConstantsUrl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Deployments Controller 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2021.05.06
 */
@Api(value = "DeploymentsController v1")
@Controller
public class DeploymentsController {

    private static final String BASE_URL = "deployments/";

    /**
     * Deployments 목록 페이지 이동(Go to the deployments list page)
     *
     * @return the view
     */
    @ApiOperation(value = "Deployments 목록 페이지 이동(Go to the deployments list page)", nickname = "getDeploymentsList")
    @GetMapping(value = ConstantsUrl.URI_CP_WORKLOADS_DEPLOYMENTS )
    public String getDeploymentsList() {
        return BASE_URL + "deployments";
    }

    /**
     * Deployments 상세 페이지 이동(Go to the deployments details page)
     *
     * @return the view
     */
    @ApiOperation(value = "Deployments 상세 페이지 이동(Go to the deployments details page)", nickname = "getDeploymentsDetails")
    @GetMapping(value = ConstantsUrl.URI_CP_WORKLOADS_DEPLOYMENTS + ConstantsUrl.URI_CP_DETAILS)
    public String getDeploymentsDetails() {
        return BASE_URL + "deploymentsDetail";
    }
}
