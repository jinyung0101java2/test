package org.paasta.container.platform.web.ui.login;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.paasta.container.platform.web.ui.common.ConstantsUrl;
import org.paasta.container.platform.web.ui.common.CustomIntercepterService;
import org.paasta.container.platform.web.ui.login.model.UsersLoginMetaData;
import org.paasta.container.platform.web.ui.security.DashboardAuthenticationDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Login Controller 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2021.06.15
 **/
@Api(value = "LoginController v1")
@RestController
public class LoginController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);


    @Autowired
    LocaleResolver localeResolver;

    private final LoginService loginService;
    private final ProviderService providerService;
    private final CustomIntercepterService customIntercepterService;

    @Autowired
    public LoginController(LoginService loginService, ProviderService providerService, CustomIntercepterService customIntercepterService) {
        this.loginService = loginService;
        this.providerService = providerService;
        this.customIntercepterService = customIntercepterService;
    }


    /**
     * User Session 데이터 조회
     *
     * @return the resultStatus
     */
    @ApiOperation(value = "User Session 데이터 조회 ", nickname = "UpdateSelectedNamespace")
    @GetMapping(value = ConstantsUrl.URI_CP_GET_USER_LOGIN_DATA)
    @ResponseBody
    public UsersLoginMetaData getAdminLoginData() {
        UsersLoginMetaData usersLoginMetaData = loginService.getAuthenticationUserMetaData();
        return usersLoginMetaData;
    }


    /**
     * User Refresh Token 조회
     *
     * @return the usersLoginMetaData
     */
    @ApiOperation(value = " User Refresh Token 조회", nickname = "getRefreshToken")
    @GetMapping(value = ConstantsUrl.URI_CP_REFRESH_TOKEN)
    @ResponseBody
    public UsersLoginMetaData getReFreshToken() {
        providerService.getRefreshToken();
        UsersLoginMetaData usersLoginMetaData = loginService.getAuthenticationUserMetaData();
        return usersLoginMetaData;
    }

    /**
     * User 로그아웃 (User Logout)
     */
    @ApiOperation(value = "User 로그아웃 (User Logout)", nickname = "logoutUsers")
    @GetMapping(value = ConstantsUrl.URI_CP_LOGOUT)
    public void logoutUsers(HttpServletRequest request, HttpServletResponse response) {

        try {
            customIntercepterService.logout();
            request.getSession().invalidate();

            response.sendRedirect(ConstantsUrl.URI_CP_SESSION_OUT);
            return;
        } catch (Exception e) {
        }
    }


    /**
     * Locale 언어 변경 (Change Locale Language)
     */
    @ApiOperation(value = "Locale 언어 변경 (Change Locale Language)", nickname = "changeLocaleLang")
    @PutMapping(value = ConstantsUrl.URL_API_LOCALE_LANGUAGE)
    public void changeLocaleLang(@RequestParam(required = false, name = ConstantsUrl.URL_API_CHANGE_LOCALE_PARAM, defaultValue = ConstantsUrl.LANG_EN) String language,
                                 HttpServletRequest request, HttpServletResponse response) {
        try {
            Locale locale = new Locale(language);
            localeResolver.setLocale(request, response, locale);
        } catch (Exception e) {
            LOGGER.info("EXCEPTION OCCURRED IN LOCALE LANGUAGE CHANGE..");
        }
    }


    /**
     * Locale 언어 조회 (Get Locale Language)
     */
    @ApiOperation(value = "Locale 언어 조회 (Get Locale Language)", nickname = "getLocaleLang")
    @GetMapping(value = ConstantsUrl.URL_API_LOCALE_LANGUAGE)
    public String getLocaleLang() {
        try {
            Locale locale = LocaleContextHolder.getLocale();

            if (locale.toString().equalsIgnoreCase(ConstantsUrl.LANG_KO)) {
                return ConstantsUrl.LANG_KO;
            }

            if (locale.toString().toLowerCase().startsWith(ConstantsUrl.LANG_KO_START_WITH)) {
                return ConstantsUrl.LANG_KO;
            }

        } catch (Exception e) {
            return ConstantsUrl.LANG_EN;
        }

        return ConstantsUrl.LANG_EN;
    }



    /**
     * User 클러스터 권한 설정 (Setting User Cluster Authority)
     */
    @ApiOperation(value = "User 클러스터 권한 설정 (Setting User Cluster Authority)", nickname = "setUserClusterAuthority")
    @PutMapping(value = ConstantsUrl.URI_API_SET_CLUSTER_AUTHORITY)
    @ResponseBody
    public void setUserClusterAuthority(@RequestBody String userType) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UsersLoginMetaData metaData = ((DashboardAuthenticationDetails) auth.getDetails()).getUsersLoginMetaData();


        List<GrantedAuthority> updatedAuthorities = new ArrayList<>();
        updatedAuthorities.add(new SimpleGrantedAuthority(metaData.getUserType()));
        updatedAuthorities.add(new SimpleGrantedAuthority(userType));

        Authentication newAuth = new OAuth2Authentication(((OAuth2Authentication) auth).getOAuth2Request(), new UsernamePasswordAuthenticationToken(auth.getPrincipal(), auth.getPrincipal(), updatedAuthorities));
        ((OAuth2Authentication) newAuth).setDetails(auth.getDetails());

        SecurityContextHolder.getContext().setAuthentication(newAuth);

        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();

        LOGGER.info("old: auth.getAuthorities():" + auth.getAuthorities());
        LOGGER.info("new: auth.getAuthorities():" + currentAuth.getAuthorities());
    }

}
