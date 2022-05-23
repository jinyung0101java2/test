package org.paasta.container.platform.web.admin.config;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Dashboard Controller 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2020.08.24
 */
@Api(value = "DashboardController v1")
@Controller
public class DashboardController {


    /**
     * 권한없음 페이지로 이동(Move to an unauthorized page)
     *
     * @return the view
     */
    @ApiOperation(value = "권한없음 페이지로 이동(Move to an unauthorized page)", nickname = "pageError401")
    @NoAuth
    @GetMapping(value = "/common/error/unauthorized")
    public ModelAndView pageError401() {
        ModelAndView model = new ModelAndView();

        model.setViewName("/common/unauthorized");
        return model;
    }


}
