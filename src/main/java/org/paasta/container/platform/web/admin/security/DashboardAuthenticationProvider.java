package org.paasta.container.platform.web.admin.security;

import org.paasta.container.platform.web.admin.common.CommonUtils;
import org.paasta.container.platform.web.admin.common.Constants;
import org.paasta.container.platform.web.admin.common.PropertyService;
import org.paasta.container.platform.web.admin.common.model.ResultStatus;
import org.paasta.container.platform.web.admin.login.LoginService;
import org.paasta.container.platform.web.admin.login.ProviderService;
import org.paasta.container.platform.web.admin.login.model.Users;
import org.paasta.container.platform.web.admin.login.model.UsersLoginMetaData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.List;


/**
 * {@link AuthenticationProvider} used to make the link between an OAuth user
 * and an internal User.
 *
 * @author Sebastien Gerard
 */
public class DashboardAuthenticationProvider implements AuthenticationProvider {


    @Value("${keycloak.oauth.client.clusterAdminRole}")
    private String clusterAdminRole;

    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardAuthenticationProvider.class);
    private final ProviderService providerService;
    private final LoginService loginService;
    private final PropertyService propertyService;



    public DashboardAuthenticationProvider(ProviderService providerService,LoginService loginService, PropertyService propertyService)
    {
        this.providerService = providerService;
        this.loginService = loginService;
        this.propertyService = propertyService;
    }



    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final String name = authentication.getName();
        final Object details = authentication.getDetails();


        if (!(details instanceof DashboardAuthenticationDetails)) {
            throw new InternalAuthenticationServiceException("The authentication details [" + details
                    + "] are not an instance of " + DashboardAuthenticationDetails.class.getSimpleName());
        }

        DashboardAuthenticationDetails dashboardAuthenticationDetails = (DashboardAuthenticationDetails) details;
        final String userId=dashboardAuthenticationDetails.getUserid();
        final String userAuthId =dashboardAuthenticationDetails.getId();
        final List<String> userRoles = dashboardAuthenticationDetails.getRoles();

        LOGGER.info("###############################################################");
        LOGGER.info(CommonUtils.loggerReplace("SESSION INFOMATION SETTING [" + name + "]" + " [" + userId + ","+userAuthId+"]" + " [" + authentication.getPrincipal() + "] "));
        LOGGER.info("###############################################################");


        // 1. CLUSTER ADMIN ROLE 에 맵핑된 사용자인지 확인
       try {
            if( !userRoles.contains(clusterAdminRole)){
                LOGGER.info(CommonUtils.loggerReplace("***** [UNAUTHORIZED] THE USER ["+ userId +"] DOES NOT HAVE A CLUSTER ADMIN ROLE...NEED TO ADD CP CLUSTER ADMIN ROLE MAPPING IN KEYCLOAK."));
                throw new InternalAuthenticationServiceException(Constants.NOT_CLUSTER_ADMIN_ROLE); }
       }
       catch (Exception e) {
           throw new InternalAuthenticationServiceException(e.getMessage());
       }


        // 2. 클러스터 관리자 계정 생성 (등록된 관리자 계정이 없다면 생성 진행)
        try {
                LOGGER.info("###############################################################");
                LOGGER.info(CommonUtils.loggerReplace("[REGISTRATION] CREATE CLUSTER ADMIN  [" + userId + ","+userAuthId+"]"));
                LOGGER.info("###############################################################");

                // 클러스터 관리자 계정 생성
                Users newClusterAdmin = new Users();
                newClusterAdmin.setUserId(userId);
                newClusterAdmin.setUserAuthId(userAuthId);

                ResultStatus resultStatus =  providerService.registerClusterAdmin(newClusterAdmin);

                if(resultStatus.getResultCode().equals(Constants.RESULT_STATUS_FAIL)) {

                    if(resultStatus.getResultMessage().equals(Constants.USER_NOT_REGISTERED_IN_KEYCLOAK)) {
                        LOGGER.info("***** THIS ACCOUNT IS NOT REGISTERED WITH KEYCLOAK.");
                        throw new InternalAuthenticationServiceException(Constants.USER_NOT_REGISTERED_IN_KEYCLOAK);
                    }
                    else if(resultStatus.getResultMessage().equals(Constants.CLUSTER_ADMIN_ALREADY_REGISTERED)) {
                        LOGGER.info(CommonUtils.loggerReplace("***** [DENY REGISTRATION] CLUSTER ADMIN ACCOUNT ALREADY EXISTS, THE USER ["+ userId +"] REGISTRATION IS DENIED."));
                    }
                    else {
                        LOGGER.info("EXCEPTION OCCURRED DURING CLUSTER ADMIN CREATED...LOOK AT THE LOGS ON THE CP API, COMMON API.");
                        throw new InternalAuthenticationServiceException("EXCEPTION OCCURRED DURING CLUSTER ADMIN CREATED.");
                    }
                }

        }
        catch(Exception e){
            throw new InternalAuthenticationServiceException(e.getMessage());
        }


        // 3. CP-API 로그인 처리
        try {
            Users loginClusterAdmin = new Users();
            loginClusterAdmin.setUserId(userId);
            loginClusterAdmin.setUserAuthId(userAuthId);

            ResultStatus resultStatus = providerService.loginClusterAdmin(loginClusterAdmin);

            if(resultStatus.getResultCode().equals(Constants.RESULT_STATUS_SUCCESS)){
                LOGGER.info("###############################################################");
                LOGGER.info("[LOGIN] CP API LOGIN SUCCESSFUL ");
                LOGGER.info("###############################################################");
                UsersLoginMetaData usersLoginMetaData = loginService.setAuthDetailsLoginMetaData(resultStatus);
                dashboardAuthenticationDetails.setUsersLoginMetaData(usersLoginMetaData);
            }
            else {
                //로그인 실패
                if(resultStatus.getResultMessage().equals(Constants.LOGIN_FAIL)) {
                    LOGGER.info(CommonUtils.loggerReplace("***** [UNAUTHORIZED] THE USER ["+ userId +"] NO PERMISSIONS DURING THE AUTHENTICATION CHECK OF ID AND AUTH ID(KEYCLOAK ID)"));
                    throw new InternalAuthenticationServiceException(Constants.LOGIN_FAIL); }
                else {
                    LOGGER.info("EXCEPTION OCCURRED DURING CP API LOGIN...LOOK AT THE LOGS ON THE CP API, COMMON API.");
                    throw new InternalAuthenticationServiceException("EXCEPTION OCCURRED DURING CP API LOGIN");
                }
            }

        }
        catch (Exception e) {
            throw new InternalAuthenticationServiceException(e.getMessage());
        }

        try {

            List authorities = new ArrayList();

            //이상없으면 세션
            authentication = new OAuth2Authentication(((OAuth2Authentication) authentication).getOAuth2Request(), new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), "N/A", authorities));
            ((OAuth2Authentication) authentication).setDetails(dashboardAuthenticationDetails);

        } catch (Exception e) {
            e.printStackTrace();
            // 세션 초기화
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            throw new InternalAuthenticationServiceException("Permission Error on [" + name + "]", e);
        }


        return authentication;
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return OAuth2Authentication.class.isAssignableFrom(authentication);
    }


}

