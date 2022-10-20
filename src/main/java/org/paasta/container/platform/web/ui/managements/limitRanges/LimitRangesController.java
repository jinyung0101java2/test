package org.paasta.container.platform.web.ui.managements.limitRanges;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.paasta.container.platform.web.ui.common.ConstantsUrl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


/**
 * LimitRanges Controller 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2021.05.04
 **/
@Api(value = "LimitRangesController v1")
@Controller
public class LimitRangesController {

    private static final String BASE_URL = "limitRanges/";

    /**
     * LimitRanges 목록 페이지 이동(Go to the limitRanges list page)
     *
     * @return the view
     */
    @ApiOperation(value = "LimitRanges 목록 페이지 이동(Go to the limitRanges list page)", nickname = "getLimitRangesList")
    @GetMapping(value = ConstantsUrl.URI_CP_MANAGEMENTS_LIMITRANGES )
    public String getLimitRangesList() {
        return BASE_URL + "limitRanges";
    }


    /**
     * LimitRanges 상세 페이지 이동(Go to the limitRanges details page)
     *
     * @return the view
     */
    @ApiOperation(value = "LimitRanges 상세 페이지 이동(Go to the limitRanges details page)", nickname = "getLimitRangesDetails")
    @GetMapping(value = ConstantsUrl.URI_CP_MANAGEMENTS_LIMITRANGES + ConstantsUrl.URI_CP_DETAILS)
    public String getLimitRangesDetails() {
        return BASE_URL + "limitRangesDetail";
    }

}
