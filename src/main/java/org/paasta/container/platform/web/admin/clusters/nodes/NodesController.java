package org.paasta.container.platform.web.admin.clusters.nodes;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.paasta.container.platform.web.admin.common.Constants;
import org.paasta.container.platform.web.admin.common.ConstantsUrl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Nodes Controller 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2021.04.30
 */
@Api(value = "NodesController v1")
@Controller
public class NodesController {
    private static final String BASE_URL = "nodes/";

    /**
     * Nodes 목록 페이지 이동(Go to the nodes list page)
     *
     * @return the view
     */
    @ApiOperation(value = "Nodes 목록 페이지 이동(Go to the nodes list page)", nickname = "getNodesList")
    @GetMapping(value = ConstantsUrl.URI_CP_CLUSTERS_NODES)
    public String getNodesList() {
        return BASE_URL + "nodes";
    }

    /**
     * Nodes 상세 페이지 이동(Go to the nodes details page)
     *
     * @return the view
     */
    @ApiOperation(value = "Nodes 상세 페이지 이동(Go to the nodes details page)", nickname = "getNodesDetails")
    @GetMapping(value = ConstantsUrl.URI_CP_CLUSTERS_NODES + ConstantsUrl.URI_CP_DETAILS)
    public String getNodesDetails() {
        return BASE_URL + "nodesDetail";
    }

}