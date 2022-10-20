package org.paasta.container.platform.web.ui.config.security;

import org.paasta.container.platform.web.ui.common.*;
import org.paasta.container.platform.web.ui.login.LoginService;
import org.paasta.container.platform.web.ui.login.ProviderService;
import org.paasta.container.platform.web.ui.security.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.provider.token.*;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static java.util.Arrays.asList;


/**
 * {@link Configuration} related to the dashboard security.
 *
 * @author Sebastien Gerard
 */
@Configuration
public class DashboardSecurityConfiguration {


    private static final Logger logger = LoggerFactory.getLogger(DashboardSecurityConfiguration.class);
    /**
     * Returns the SPeL expression checking that the current user is authorized
     * to manage this service.
     */
    public static String isManagingApp() {
        return "(authentication.details != null) " +
                "and (authentication.details instanceof T(" + DashboardAuthenticationDetails.class.getName() + ")) ";

    }

    @Value("${keycloak.oauth.client.id}")
    private String clientId;

    @Value("${keycloak.oauth.client.secret}")
    private String clientSecret;

    @Value("${keycloak.oauth.info.uri}")
    private String oauthInfoUrl;

    @Value("${keycloak.oauth.token.check.uri}")
    private String checkTokenUri;

    @Value("${keycloak.oauth.authorization.uri}")
    private String authorizationUri;

    @Value("${keycloak.oauth.token.access.uri}")
    private String accessUri;

    @Value("${keycloak.oauth.logout.url}")
    private String logoutUrl;
    
    @Value("${keycloak.oauth.client.scope}")
    private String[] scopes;
    
    @Value("${keycloak.oauth.url.exception.queryparams:USER_NAME,USER_ID,ORG_CD,ORG_NAME,PROJECT_ID,PROJECT_NAME,USER_SE_CD}")
    private String[] exceptionQueryParams;

    @Autowired
    @Qualifier("authenticationManager")
    private AuthenticationManager authenticationManager;

    @Autowired
    private HttpServletRequest httpServletRequest;


    @Bean(name = "dashboardEntryPointMatcher")
    public RequestMatcher dashboardEntryPointMatcher() {
        return new AntPathRequestMatcher("/**");
    }

    @Bean(name = "dashboardClientContextFilter")
    public FilterWrapper dashboardClientContextFilter() {
        // If it was a Filter bean it would be automatically added out of the Spring security filter chain.
        return FilterWrapper.wrap(new OAuth2ClientContextFilter() {

            @Override
            protected String calculateCurrentUri(HttpServletRequest request) throws UnsupportedEncodingException {
                // TODO Auto-generated method stub


                ServletUriComponentsBuilder builder = ServletUriComponentsBuilder
                        .fromRequest(request);
                // Now work around SPR-10172...

                RequestWrapper requestWrapper = new RequestWrapper(request);
                String queryString = requestWrapper.getQueryString();


                List<String> QueryParams =  asList(exceptionQueryParams);

                if(QueryParams!=null) {

                    for(String queryParam: QueryParams) {

                        boolean bFlag = queryString != null && queryString.contains(queryParam);
                        if (bFlag) {
                            builder.replaceQueryParam(queryParam, "%20");
                        }
                    }
                }

                boolean legalSpaces = queryString != null && queryString.contains("+");
                if (legalSpaces) {
                    builder.replaceQuery(queryString.replace("+", "%20"));
                }
                UriComponents uri = null;
                try {
                    uri = builder.replaceQueryParam("code").build(true);
                } catch (IllegalArgumentException ex) {
                    // ignore failures to parse the url (including query string). does't
                    // make sense for redirection purposes anyway.
                    logger.error(CommonUtils.loggerReplace(ex.toString()));

                    return null;
                }
                String query = uri.getQuery();
                if (legalSpaces) {
                    query = query.replace("%20", "+");
                }


                logger.warn("After Url={}", CommonUtils.loggerReplace(ServletUriComponentsBuilder.fromUri(uri.toUri())
                        .replaceQuery(query).build().toString()));
                return ServletUriComponentsBuilder.fromUri(uri.toUri())
                        .replaceQuery(query).build().toString();
            }

        });
    }

    @Bean(name = "dashboardSocialClientFilter")
    @Autowired
    public FilterWrapper dashboardSocialClientFilter(AuthService authService) {
        // If it was a Filter bean it would be automatically added out of the Spring security filter chain.
        final DashboardAuthenticationProcessingFilter filter
              = new DashboardAuthenticationProcessingFilter();
        filter.setRestTemplate(dashboardRestOperations());
        filter.setTokenServices(dashboardResourceServerTokenServices());
        filter.setAuthenticationManager(authenticationManager);
        filter.setRequiresAuthenticationRequestMatcher(dashboardEntryPointMatcher());
        filter.setDetailsSource(dashboardAuthenticationDetailsSource());
        filter.setAuthenticationSuccessHandler(new DashboardAuthenticationSuccessHandler());
        filter.setCommonService(authService);

        return FilterWrapper.wrap(filter);
    }

    @Bean(name = "dashboardProtectedResourceDetails")
    @Scope(value = WebApplicationContext.SCOPE_SESSION)
    @Autowired
    public AuthorizationCodeResourceDetails dashboardProtectedResourceDetails() {
        final AuthorizationCodeResourceDetails resourceDetails = new AuthorizationCodeResourceDetails() {
            @Override
            public boolean isClientOnly() {
                return true;
            }
        };
        resourceDetails.setClientId(clientId);
        resourceDetails.setClientSecret(clientSecret);
        resourceDetails.setUserAuthorizationUri(authorizationUri);
        resourceDetails.setAccessTokenUri(accessUri);
        resourceDetails.setUseCurrentUri(true);
        resourceDetails.setScope(asList(scopes));

        return resourceDetails;
    }

    @Bean(name = "dashboardClientContext")
    @Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
    @Autowired
    public OAuth2ClientContext dashboardClientContext() {
        return new DefaultOAuth2ClientContext(dashboardAccessTokenRequest());
    }

    @Bean(name = "dashboardAccessTokenRequest")
    @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    @Autowired
    public AccessTokenRequest dashboardAccessTokenRequest() {

        RequestWrapper requestWrapper = new RequestWrapper(httpServletRequest);
        final DefaultAccessTokenRequest request = new DefaultAccessTokenRequest(requestWrapper.getParameterMap());

        final Object currentUri = httpServletRequest.getAttribute(OAuth2ClientContextFilter.CURRENT_URI);
        if (currentUri != null) {
            request.setCurrentUri(currentUri.toString());
        }

        return request;
    }

    @Bean(name = "dashboardRestOperations")
    @Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
    @Autowired
    public OAuth2RestTemplate dashboardRestOperations() {
        try {
            SSLUtils.turnOffSslChecking();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return new OAuth2RestTemplate(dashboardProtectedResourceDetails(), dashboardClientContext());
    }

    @Bean(name = "dashboardAccessTokenConverter")
    public AccessTokenConverter dashboardAccessTokenConverter() {
        final DefaultAccessTokenConverter defaultAccessTokenConverter = new DefaultAccessTokenConverter();
        final DefaultUserAuthenticationConverter userTokenConverter = new DefaultUserAuthenticationConverter();

        userTokenConverter.setDefaultAuthorities(new String[]{"ROLE_" + ApplicationWebSecurityConfigurerAdapter.ROLE_DASHBOARD});
        defaultAccessTokenConverter.setUserTokenConverter(userTokenConverter);

        return defaultAccessTokenConverter;
    }

    @Bean(name = "dashboardResourceServerTokenServices")
    @Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
    @Autowired
    public ResourceServerTokenServices dashboardResourceServerTokenServices() {
        final RemoteTokenServices remoteTokenServices = new RemoteTokenServices();

        remoteTokenServices.setClientId(clientId);
        remoteTokenServices.setClientSecret(clientSecret);
        remoteTokenServices.setCheckTokenEndpointUrl(checkTokenUri);
        remoteTokenServices.setAccessTokenConverter(dashboardAccessTokenConverter());
        return remoteTokenServices;
    }

    @Bean(name = "dashboardAuthenticationDetailsSource")
    @Autowired
    public org.springframework.security.authentication.AuthenticationDetailsSource dashboardAuthenticationDetailsSource() {
        return new DashboardAuthenticationDetailsSource(dashboardRestOperations(), oauthInfoUrl);
    }

    @Bean(name = "dashboardAuthenticationProvider")
    @Autowired
    public DashboardAuthenticationProvider dashboardAuthenticationProvider(ProviderService providerService, LoginService loginService, PropertyService propertyService) {
        return new DashboardAuthenticationProvider(providerService, loginService, propertyService);
    }

    @Bean(name = "dashboardLogoutSuccessHandler")
    public LogoutSuccessHandler dashboardLogoutSuccessHandler() {
        final SimpleUrlLogoutSuccessHandler logoutSuccessHandler = new SimpleUrlLogoutSuccessHandler();
        logoutSuccessHandler.setRedirectStrategy(new DashboardLogoutRedirectStrategy());

        return logoutSuccessHandler;
    }

    @Bean(name = "dashboardLogoutUrlMatcher")
    public RequestMatcher dashboardLogoutUrlMatcher() {
        return new AntPathRequestMatcher(ConstantsUrl.URI_CP_SESSION_OUT);
    }
}
