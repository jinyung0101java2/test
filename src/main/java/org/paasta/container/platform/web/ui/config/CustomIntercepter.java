package org.paasta.container.platform.web.ui.config;


import org.paasta.container.platform.web.ui.common.ConstantsUrl;
import org.paasta.container.platform.web.ui.common.CustomIntercepterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class CustomIntercepter extends HandlerInterceptorAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomIntercepter.class);


    @Autowired
    CustomIntercepterService customIntercepterService;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {


        String url = request.getRequestURI();


        if (!(  url.indexOf("/css") >= 0 ||
                url.indexOf("/dist") >= 0 ||
                url.indexOf("/plugins") >= 0 ||
                url.indexOf("/font") >= 0 ||
                url.indexOf("/img") >= 0 ||
                url.indexOf("/js") >= 0 ||
                url.indexOf("/webjars") >= 0 ||
                url.indexOf(ConstantsUrl.URI_CP_SESSION_OUT) >= 0 ||
                url.indexOf("/error") >= 0
        )) {

            boolean isActive = customIntercepterService.isActive();

 	        if(!isActive) {
	       	 request.getSession().invalidate();
                LOGGER.info("#### PREHANDLE :: USER IS INACTIVE");
	       		 response.sendRedirect(ConstantsUrl.URI_CP_SESSION_OUT);
	       		 return false;
	        }
      }


        return super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {

        super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response, Object handler, Exception ex)
            throws Exception {

        super.afterCompletion(request, response, handler, ex);
    }

}
