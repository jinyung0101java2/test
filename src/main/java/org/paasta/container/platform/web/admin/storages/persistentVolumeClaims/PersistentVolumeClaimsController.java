package org.paasta.container.platform.web.admin.storages.persistentVolumeClaims;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.paasta.container.platform.web.admin.common.ConstantsUrl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * PersistentVolumeClaims Controller 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2021.05.06
 */
@Api(value = "PersistentVolumeClaimsController v1")
@Controller
public class PersistentVolumeClaimsController {

    private static final String BASE_URL = "persistentVolumeClaims/";

    /**
     * PersistentVolumeClaims 목록 페이지 이동(Go to the persistentVolumeClaims list page)
     *
     * @return the view
     */
    @ApiOperation(value = "PersistentVolumeClaims 목록 페이지 이동(Go to the persistentVolumeClaims list page)", nickname = "getPersistentVolumeClaimsList")
    @GetMapping(value = ConstantsUrl.URI_CP_STORAGES_PERSISTENTVOLUMECLAIMS )
    public String getPersistentVolumeClaimsList() {
        return BASE_URL + "persistentVolumeClaims";
    }

    /**
     * PersistentVolumeClaims 상세 페이지 이동(Go to the persistentVolumeClaims details page)
     *
     * @return the view
     */
    @ApiOperation(value = "PersistentVolumeClaims 상세 페이지 이동(Go to the persistentVolumeClaims details page)", nickname = "getPersistentVolumeClaimsDetails")
    @GetMapping(value = ConstantsUrl.URI_CP_STORAGES_PERSISTENTVOLUMECLAIMS + ConstantsUrl.URI_CP_DETAILS)
    public String getPersistentVolumeClaimsDetails() {
        return BASE_URL + "persistentVolumeClaimsDetail";
    }

}