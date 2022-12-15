package org.paasta.container.platform.web.ui.global.clusters;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.paasta.container.platform.web.ui.common.ConstantsUrl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Clusters Controller 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.07.01
 */

@Api(value = "ClustersController v1")
@PreAuthorize("@authSecurity.checkIsGlobal()")
@Controller
public class ClustersController {
    private static final String BASE_URL = "global/clusters/";

    /**
     * Clusters 목록 페이지 이동(Go to the clusters list page)
     *
     * @return the view
     */
    @ApiOperation(value = "Clusters 목록 페이지 이동(Go to the clusters list page)", nickname = "getClustersList")
    @GetMapping(value = ConstantsUrl.URI_CP_GLOBAL_CLUSTERS)
    public String getClustersList() {
        return BASE_URL + "clusters";
    }

    /**
     * Clusters 상세 페이지 이동(Go to the clusters details page)
     *
     * @return the view
     */
    @ApiOperation(value = "Clusters 상세 페이지 이동(Go to the clusters details page)", nickname = "getClustersDetails")
    @GetMapping(value = ConstantsUrl.URI_CP_GLOBAL_CLUSTERS + ConstantsUrl.URI_CP_DETAILS)
    public String getClustersDetail() {
        return BASE_URL + "clustersDetail";
    }

    /**
     * Clusters 생성 페이지 이동(Go to the clusters creates page)
     *
     * @return the view
     */
    @PreAuthorize("@authSecurity.checkIsSuperAdmin()")
    @ApiOperation(value = "Clusters 생성 페이지 이동(Go to the clusters create page)", nickname = "getClustersCreate")
    @GetMapping(value = ConstantsUrl.URI_CP_GLOBAL_CLUSTERS + ConstantsUrl.URI_CP_CREATE)
    public String getClustersCreate() {
        return BASE_URL + "clustersCreate";
    }

    /**
     * Clusters 로그 페이지 이동(Go to the clusters logs page)
     *
     * @return the view
     */
    @ApiOperation(value = "Clusters 로그 페이지 이동(Go to the clusters logs page)", nickname = "getClustersLogs")
    @GetMapping(value = ConstantsUrl.URI_CP_GLOBAL_CLUSTERS + ConstantsUrl.URI_CP_LOGS)
    public String getClustersLog() {
        return BASE_URL + "clustersLogs";
    }
}
