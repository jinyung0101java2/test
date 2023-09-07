package org.container.platform.web.ui.services.ingresses;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.container.platform.web.ui.common.ConstantsUrl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Ingresses Controller 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2022.05.17
 */
@Api(value = "IngressesController v1")
@Controller
public class IngressesController {

    private static final String BASE_URL = "ingresses/";

    /**
     * Ingresses 목록 페이지 이동(Go to the ingresses list page)
     *
     * @return the view
     */
    @ApiOperation(value = "Ingresses 목록 페이지 이동(Go to the ingresses list page)", nickname = "getIngressesList")
    @GetMapping(value = ConstantsUrl.URI_CP_SERVICES_INGRESSES )
    public String getIngressesList(){
        return BASE_URL + "ingresses";}

    /**
     * Ingresses 생성 페이지 이동(Go to the ingresses create page)
     *
     * @return the view
     */
    @ApiOperation(value = "Ingresses 생성 페이지 이동(Go to the ingresses create page)", nickname = "createIngresses")
    @GetMapping(value = ConstantsUrl.URI_CP_SERVICES_INGRESSES + ConstantsUrl.URI_CP_CREATE)
    public String createIngresses() {
        return BASE_URL + "ingressesCreate";
    }


    /**
     * Ingresses 상세 페이지 이동(Go to the ingresses details page)
     *
     * @return the view
     */
    @ApiOperation(value = "Ingresses 상세 페이지 이동(Go to the ingresses details page)", nickname = "getIngressesDetails")
    @GetMapping(value = ConstantsUrl.URI_CP_SERVICES_INGRESSES + ConstantsUrl.URI_CP_DETAILS)
    public String getIngressesDetails(){return
            BASE_URL + "ingressesDetail";}

    /**
     * Ingresses 수정 페이지 이동(Go to the ingresses update page)
     *
     * @return the view
     */
    @ApiOperation(value = "Ingresses 수정 페이지 이동(Go to the ingresses update page)", nickname = "updateIngresses")
    @GetMapping(value = ConstantsUrl.URI_CP_SERVICES_INGRESSES + ConstantsUrl.URI_CP_UPDATE)
    public String updateIngresses() {
        return BASE_URL + "ingressesUpdate";
    }
}
