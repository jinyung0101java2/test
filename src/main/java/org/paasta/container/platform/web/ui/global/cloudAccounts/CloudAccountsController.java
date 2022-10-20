package org.paasta.container.platform.web.ui.global.cloudAccounts;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.paasta.container.platform.web.ui.common.ConstantsUrl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * CloudAccounts Controller 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.07.01
 */

@Api(value = "CloudAccountsController v1")
@PreAuthorize("@authSecurity.checkisGlobalAdmin()")
@Controller
public class CloudAccountsController {
    private static final String BASE_URL = "global/cloudAccounts/";

    /**
     * CloudAccounts 목록 페이지 이동(Go to the cloudAccounts list page)
     *
     * @return the view
     */
    @ApiOperation(value = "CloudAccounts 목록 페이지 이동(Go to the cloudAccounts list page)", nickname = "getCloudAccountsList")
    @GetMapping(value = ConstantsUrl.URI_CP_GLOBAL_CLOUD_ACCOUNTS)
    public String getCloudAccountsList() {
        return BASE_URL + "cloudAccounts";
    }

    /**
     * CloudAccounts 상세 페이지 이동(Go to the cloudAccounts details page)
     *
     * @return the view
     */
    @ApiOperation(value = "CloudAccounts 상세 페이지 이동(Go to the cloudAccounts details page)", nickname = "getCloudAccountsDetails")
    @GetMapping(value = ConstantsUrl.URI_CP_GLOBAL_CLOUD_ACCOUNTS + ConstantsUrl.URI_CP_DETAILS)
    public String getCloudAccountsDetail() {
        return BASE_URL + "cloudAccountsDetail";
    }

    /**
     * CloudAccounts 생성 페이지 이동(Go to the cloudAccounts create page)
     *
     * @return the view
     */
    @ApiOperation(value = "CloudAccounts 생성 페이지 이동(Go to the cloudAccounts create page)", nickname = "getCloudAccountsCreate")
    @GetMapping(value = ConstantsUrl.URI_CP_GLOBAL_CLOUD_ACCOUNTS + ConstantsUrl.URI_CP_CREATE)
    public String getCloudAccountsCreate() {
        return BASE_URL + "cloudAccountsCreate";
    }
}

