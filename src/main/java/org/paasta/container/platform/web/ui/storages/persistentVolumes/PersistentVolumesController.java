package org.paasta.container.platform.web.ui.storages.persistentVolumes;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.paasta.container.platform.web.ui.common.ConstantsUrl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * PersistentVolumes Controller 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2021.05.06
 */
@Api(value = "PersistentVolumesController v1")
@PreAuthorize("@authSecurity.checkisGlobalAdmin()")
@Controller
public class PersistentVolumesController {

    private static final String BASE_URL = "persistentVolumes/";

    /**
     * PersistentVolumes 목록 페이지 이동(Go to the persistentVolumes list page)
     *
     * @return the view
     */
    @ApiOperation(value = "PersistentVolumes 목록 페이지 이동(Go to the persistentVolumes list page)", nickname = "getPersistentVolumesList")
    @GetMapping(value = ConstantsUrl.URI_CP_STORAGES_PERSISTENTVOLUMES )
    public String getPersistentVolumesList() {
        return BASE_URL + "persistentVolumes";
    }

    /**
     * PersistentVolumes 상세 페이지 이동(Go to the persistentVolumes details page)
     *
     * @return the view
     */
    @ApiOperation(value = "PersistentVolumes 상세 페이지 이동(Go to the persistentVolumes details page)", nickname = "getPersistentVolumesDetails")
    @GetMapping(value = ConstantsUrl.URI_CP_STORAGES_PERSISTENTVOLUMES + ConstantsUrl.URI_CP_DETAILS)
    public String getPersistentVolumesDetails() {
        return BASE_URL + "persistentVolumesDetail";
    }

}
