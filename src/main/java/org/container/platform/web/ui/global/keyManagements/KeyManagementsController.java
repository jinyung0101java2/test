package org.container.platform.web.ui.global.keyManagements;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.container.platform.web.ui.common.ConstantsUrl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * KeyManagements Controller 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2023.12.22 */

@Api(value = "KeyManagementsController v1")
@PreAuthorize("@authSecurity.checkIsGlobal()")
@Controller
public class KeyManagementsController {
    private static final String BASE_URL = "global/keyManagements/";

    /**
     * KeyManagements 목록 페이지 이동(Go to the key managements list page)
     *
     * @return the view
     */
    @ApiOperation(value = "KeyManagements 목록 페이지 이동(Go to the key managements list page)", nickname = "getKeyManagementsList")
    @GetMapping(value = ConstantsUrl.URI_CP_GLOBAL_KEY_MANAGEMENTS)
    public String getKeyManagementsList() {
        return BASE_URL + "keyManagements";
    }

    /**
     * KeyManagements 상세 페이지 이동(Go to the key managements details page)
     *
     * @return the view
     */
    @ApiOperation(value = "KeyManagements 상세 페이지 이동(Go to the key managements details page)", nickname = "getKeyManagementsDetails")
    @GetMapping(value = ConstantsUrl.URI_CP_GLOBAL_KEY_MANAGEMENTS + ConstantsUrl.URI_CP_DETAILS)
    public String getKeyManagementsDetail() {
        return BASE_URL + "keyManagementsDetail";
    }

    /**
     * KeyManagements 생성 페이지 이동(Go to the key managements creates page)
     *
     * @return the view
     */
    @PreAuthorize("@authSecurity.checkIsSuperAdmin()")
    @ApiOperation(value = "KeyManagements 생성 페이지 이동(Go to the key managements create page)", nickname = "getKeyManagementsCreate")
    @GetMapping(value = ConstantsUrl.URI_CP_GLOBAL_KEY_MANAGEMENTS + ConstantsUrl.URI_CP_CREATE)
    public String getInstanceCodeTemplatesCreate() {
        return BASE_URL + "keyManagementsCreate";
    }
}
