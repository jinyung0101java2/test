package org.paasta.container.platform.web.ui.managements.roles;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.paasta.container.platform.web.ui.common.ConstantsUrl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Roles Controller 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2021.05.06
 */
@Api(value = "RolesController v1")
@Controller
public class RolesController {

    private static final String BASE_URL = "roles/";

    /**
     * Roles 목록 페이지 이동(Go to the roles list page)
     *
     * @return the view
     */
    @ApiOperation(value = "Roles 목록 페이지 이동(Go to the roles list page)", nickname = "getRolesList")
    @GetMapping(value = ConstantsUrl.URI_CP_MANAGEMENTS_ROLES)
    public String getRolesList() {
        return BASE_URL + "roles";
    }

    /**
     * Roles 상세 페이지 이동(Go to the roles details page)
     *
     * @return the view
     */
    @ApiOperation(value = "Roles 상세 페이지 이동(Go to the roles details page)", nickname = "getRolesDetails")
    @GetMapping(value = ConstantsUrl.URI_CP_MANAGEMENTS_ROLES + ConstantsUrl.URI_CP_DETAILS)
    public String getRolesDetails() {
        return BASE_URL + "rolesDetail";
    }

}
