package org.paasta.container.platform.web.admin.intro;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.paasta.container.platform.web.admin.common.Constants;
import org.paasta.container.platform.web.admin.common.ConstantsUrl;
import org.paasta.container.platform.web.admin.config.NoAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;


/**
 * Intro Overview Controller 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2021.05.06
 */
@Api(value = "IntroOverviewController v1")
@Controller
public class IntroOverviewController {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntroOverviewController.class);

    /**
     * index 페이지 이동(Move Intro overview page)
     *
     * @return the view
     */
    @ApiOperation(value = "Intro overview 페이지 이동(Move Intro overview page)", nickname = "indexView")
    @GetMapping("/")
    @NoAuth
    public RedirectView baseView(@RequestParam(name = Constants.SERVICE_SESSION_REFRESH, required = false, defaultValue = "false") String sessionRefresh) {

        if(sessionRefresh.equalsIgnoreCase(Constants.CHECK_TRUE)){
            LOGGER.info("[FOR THE SERVICE TYPE] CONNECT VIA DASHBOARD URI BUTTON TO REFRESH SESSION...");
            SecurityContextHolder.clearContext();
            return new RedirectView("/");
        }

        return new RedirectView(ConstantsUrl.URI_CP_INDEX_URL);
    }

    /**
     * Index 페이지 이동(Move Intro overview page)
     *
     * @return the intro overview
     */
    @ApiOperation(value = "Intro overview 페이지 이동(Move Intro overview page)", nickname = "getIntroOverview")
    @GetMapping(value = ConstantsUrl.URI_CP_INDEX_URL)
    public String getIntroOverview() {
        return "index";
    }
}

