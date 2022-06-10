package org.paasta.container.platform.web.admin.security;

import org.paasta.container.platform.web.admin.common.CommonUtils;
import org.paasta.container.platform.web.admin.common.Constants;
import org.paasta.container.platform.web.admin.common.PropertyService;
import org.paasta.container.platform.web.admin.common.model.ResultStatus;
import org.paasta.container.platform.web.admin.login.LoginService;
import org.paasta.container.platform.web.admin.login.ProviderService;
import org.paasta.container.platform.web.admin.login.model.AuthenticationResponse;
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


    @Value("${keycloak.oauth.client.superAdminRole}")
    private String superAdminRole;

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
        LOGGER.info(CommonUtils.loggerReplace("SESSION INFOMATION SETTING [" + name + "]" + " [" + userId + ","+userAuthId+"]"));
        LOGGER.info("###############################################################");

        Users users = new Users(userId, userAuthId, false);

        try {
            // SUPER-ADMIN 권한인 경우
            if(userRoles.contains(superAdminRole)){
                users.setIsSuperAdmin(true);
            }

            LOGGER.info("###############################################################");
            LOGGER.info(CommonUtils.loggerReplace("[CHECK REGISTRATION] USER  [" + userId + ", "+userAuthId+ ", isSuperAdmin: "+ users.getIsSuperAdmin()+ " ]"));
            LOGGER.info("###############################################################");

            // 사용자 계정 생성
            ResultStatus resultStatus =  providerService.registerUsers(users);

            if(resultStatus.getResultCode().equals(Constants.RESULT_STATUS_FAIL)) {
                if(!Constants.ALREADY_REGISTERED_MESSAGE.contains(resultStatus.getResultMessage())) {
                    throw new InternalAuthenticationServiceException(resultStatus.getResultMessage());
                }
            }

        }
        catch(Exception e){
            throw new InternalAuthenticationServiceException(e.getMessage());
        }


        // 3. CP-API 로그인 처리
        try {
            Users loginUser = new Users();
            loginUser.setUserId(userId);
            loginUser.setUserAuthId(userAuthId);

            AuthenticationResponse authenticationResponse = providerService.loginUsers(loginUser);

            if(authenticationResponse.getResultCode().equals(Constants.RESULT_STATUS_SUCCESS)){
                LOGGER.info("###############################################################");
                LOGGER.info("[LOGIN] CP API LOGIN SUCCESSFUL ");
                LOGGER.info("###############################################################");
                UsersLoginMetaData usersLoginMetaData = loginService.setAuthDetailsLoginMetaData(authenticationResponse);
                dashboardAuthenticationDetails.setUsersLoginMetaData(usersLoginMetaData);
            }
            else {
                //로그인 실패
                if(authenticationResponse.getResultMessage().equals(Constants.LOGIN_FAIL)) {
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