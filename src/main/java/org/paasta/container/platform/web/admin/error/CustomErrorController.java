package org.paasta.container.platform.web.admin.error;

import io.swagger.annotations.ApiOperation;
import org.paasta.container.platform.web.admin.common.ConstantsUrl;
import org.paasta.container.platform.web.admin.config.NoAuth;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

/**
 * Error Controller 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2021.06.15
 **/
@Controller
public class CustomErrorController implements ErrorController {

    private static final String BASE_URL = "errors/";

    @Override
    public String getErrorPath() {
        return "/error";
    }

    @NoAuth
    @GetMapping("/error")
    public ModelAndView handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String reqUrl = BASE_URL + status.toString();

        ModelAndView mv = new ModelAndView();
        mv.setViewName(reqUrl);

        return mv;
    }


    /**
     * Session Out 페이지 이동(Go to the session out page)
     *
     * @return the view
     */
    @ApiOperation(value = "Session Out 페이지 이동(Go to the session out page)", nickname = "getSessionOutView")
    @NoAuth
    @GetMapping(ConstantsUrl.URI_CP_SESSION_OUT)
    public String getSessionOutView() {
        return BASE_URL + "sessionout";
    }


    /**
     * HTTP Code 400 Error 페이지 이동(Go to the http code 400 error page)
     *
     * @return the view
     */
    @ApiOperation(value = "HTTP Code 400 Error 페이지 이동(Go to the http code 400 error page)", nickname = "handleError400")
    @NoAuth
    @GetMapping("/error/400")
    public String handleError400() {
        return BASE_URL + "400";
    }


    /**
     * HTTP Code 401 Error 페이지 이동(Go to the http code 401 error page)
     *
     * @return the view
     */
    @ApiOperation(value = "HTTP Code 401 Error 페이지 이동(Go to the http code 401 error page)", nickname = "handleError401")
    @NoAuth
    @GetMapping("/error/401")
    public String handleError401() {
        return BASE_URL + "401";
    }


    /**
     * HTTP Code 403 Error 페이지 이동(Go to the http code 403 error page)
     *
     * @return the view
     */
    @ApiOperation(value = "HTTP Code 403 Error 페이지 이동(Go to the http code 403 error page)", nickname = "handleError403")
    @NoAuth
    @GetMapping("/error/403")
    public String handleError403() {
        return BASE_URL + "403";
    }


    /**
     * HTTP Code 404 Error 페이지 이동(Go to the http code 404 error page)
     *
     * @return the view
     */
    @ApiOperation(value = "HTTP Code 404 Error 페이지 이동(Go to the http code 404 error page)", nickname = "handleError404")
    @NoAuth
    @GetMapping("/error/404")
    public String handleError404() {
        return BASE_URL + "404";
    }


    /**
     * HTTP Code 500 Error 페이지 이동(Go to the http code 500 error page)
     *
     * @return the view
     */
    @ApiOperation(value = "HTTP Code 500 Error 페이지 이동(Go to the http code 500 error page)", nickname = "handleError500")
    @NoAuth
    @GetMapping("/error/500")
    public String handleError500() {
        return BASE_URL + "500";
    }


    /**
     * inactive 페이지 이동(Go to the inactive page)
     *
     * @return the view
     */
    @ApiOperation(value = "inactive 페이지 이동(Go to the inactive page)", nickname = "handleInActive")
    @NoAuth
    @GetMapping("/error/inactive")
    public String handleInActive() {
        return BASE_URL + "inactive";
    }

}