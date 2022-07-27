package org.paasta.container.platform.web.admin.privateRepositoryInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.paasta.container.platform.web.admin.common.ConstantsUrl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Private Registry Controller 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2022.07.13
 */

@Api(value = "PrivateRepositoryInfoController v1")
@Controller
public class PrivateRepositoryInfoController {
    private static final String BASE_URL = "privateRepositoryInfo/";

    /**
     * Private Repository Info 페이지 이동(Go to the Private Repository Info page)
     *
     * @return the view
     */
    @ApiOperation(value = "Private Repository Info 페이지 이동(Go to the Private Repository Info page)", nickname = "getPrivateRepositoryInfo")
    @GetMapping(value = ConstantsUrl.URI_CP_INFO_PRIVATE_REPOSITORY )
    public String getPrivateRepositoryInfo() {
        return BASE_URL + "privateRepositoryInfo";
    }
}
