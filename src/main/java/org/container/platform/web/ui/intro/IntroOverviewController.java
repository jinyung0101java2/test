package org.container.platform.web.ui.intro;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.container.platform.web.ui.common.Constants;
import org.container.platform.web.ui.common.ConstantsUrl;
import org.container.platform.web.ui.config.NoAuth;
import org.container.platform.web.ui.login.LoginService;
import org.container.platform.web.ui.login.model.UsersLoginMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


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

    @Autowired
    private LoginService loginService;

    /**
     * index 페이지 이동(Move Intro overview page)
     *
     * @return the view
     */
    @ApiOperation(value = "Intro overview 페이지 이동(Move Intro overview page)", nickname = "indexView")
    @GetMapping(value = {"/", ConstantsUrl.URI_CP_GLOBAL_URL})
    @NoAuth
    public Object baseView() {
        UsersLoginMetaData usersLoginMetaData = loginService.getAuthenticationUserMetaData();
        if (Constants.AUTH_ADMIN_LIST.contains(usersLoginMetaData.getUserType())) {
            return "global/overview";
        }

        return "index";
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

