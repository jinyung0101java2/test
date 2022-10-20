package org.paasta.container.platform.web.ui.security;

import org.paasta.container.platform.web.ui.common.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Extension of {@link OAuth2ClientAuthenticationProcessingFilter} that uses the
 * {@link org.springframework.security.authentication.AuthenticationManager}.
 * This implementation also starts authentication if there is no authentication and
 * if the current request requires authentication.
 *
 * @author Sebastien Gerard
 */
public class DashboardAuthenticationProcessingFilter extends OAuth2ClientAuthenticationProcessingFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardAuthenticationProcessingFilter.class);

    private AuthenticationDetailsSource<HttpServletRequest, ?> detailsSource;

    private AuthService authService;

    public DashboardAuthenticationProcessingFilter() {
        super("/");
    }

    @Override
    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        LOGGER.info("######### requiresAuthentication");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication == null && super.requiresAuthentication(request, response);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        final Authentication authentication = super.attemptAuthentication(request, response);
        if (detailsSource != null) {
            request.getSession().invalidate();
            ((OAuth2Authentication) authentication).setDetails(detailsSource.buildDetails(request));

        }

        Authentication resultAuth = null;

        try {
            resultAuth =  getAuthenticationManager().authenticate(authentication);
        }
        catch (Exception e){
            LOGGER.info("######### AUTHENTICATION EXCEPTION MESSAGE : {}", CommonUtils.loggerReplace(e.getMessage()));
            String redirect_url = "/error/500";

            if(e.getMessage().equals(Constants.LOGIN_INACTIVE_USER_MESSAGE)) {
                redirect_url = "/error/inactive";
            }
            if(Constants.LOGIN_UNAUTHORIZED_MESSAGE.contains(e.getMessage())) {
                redirect_url = "/error/401";
            }
            response.sendRedirect(redirect_url);
        }
        return resultAuth;
    }

    /**
     * Sets the optional source providing {@link Authentication#getDetails() authentication details}.
     */
    public void setDetailsSource(AuthenticationDetailsSource<HttpServletRequest, ?> detailsSource) {
        this.detailsSource = detailsSource;
    }

    public void setCommonService(AuthService authService) {
        this.authService = authService;
    }



}
