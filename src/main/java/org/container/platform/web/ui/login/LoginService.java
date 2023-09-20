package org.container.platform.web.ui.login;

import org.container.platform.web.ui.common.Constants;
import org.container.platform.web.ui.login.model.AuthenticationResponse;
import org.container.platform.web.ui.login.model.UsersLoginMetaData;
import org.container.platform.web.ui.security.DashboardAuthenticationDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Login Service 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2021.03.16
 **/
@Service
public class LoginService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginService.class);


    /**
     * 현재 로그인된 Users Details MetaData 조회 (Get Login Meta-Information of currently logged in users)
     *
     * @return the UsersLoginMetaData
     */
    public UsersLoginMetaData getAuthenticationUserMetaData() {

        UsersLoginMetaData usersLoginMetaData = null;
        try {
            usersLoginMetaData = ((DashboardAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails()).getUsersLoginMetaData();

        } catch (NullPointerException e) {
            return null;
        }

        return usersLoginMetaData;
    }


    /**
     * Users Details MetaData 객체 생성(Create Users Login Meta-Information Object)
     *
     * @param authenticationResponse the AuthenticationResponse
     */
    public UsersLoginMetaData setAuthDetailsLoginMetaData(AuthenticationResponse authenticationResponse) {
        UsersLoginMetaData usersLoginMetaData = new UsersLoginMetaData();
        usersLoginMetaData.setAccessToken(authenticationResponse.getToken());
        usersLoginMetaData.setClusterId(authenticationResponse.getClusterId());
        usersLoginMetaData.setUserId(authenticationResponse.getUserId());
        usersLoginMetaData.setUserAuthid(authenticationResponse.getUserAuthId());
        usersLoginMetaData.setUserType(authenticationResponse.getUserType());
        usersLoginMetaData.setIsSuperAdmin(authenticationResponse.getIsSuperAdmin());
        usersLoginMetaData.setSelectedNamespace("");
        usersLoginMetaData.setUserMetaData("");
        usersLoginMetaData.setUserMetaDataList(null);
        usersLoginMetaData.setActive(Constants.CHECK_Y);
        return usersLoginMetaData;
    }


    /**
     * 현재 로그인된 Users Details MetaData 업데이트 (Update Login Meta-Information of currently logged in users)
     *
     * @return the UsersLoginMetaData
     */
    public UsersLoginMetaData updateAuthenticationUserMetaData(UsersLoginMetaData usersLoginMetaData) {

        UsersLoginMetaData updateUsersLoginMetaData = null;
        try {
            // DashboardAuthenticationDetails Update
            ((DashboardAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails()).setUsersLoginMetaData(usersLoginMetaData);
            // Get DashboardAuthenticationDetails
            updateUsersLoginMetaData = ((DashboardAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails()).getUsersLoginMetaData();

        } catch (Exception e) {
            return null;
        }

        return updateUsersLoginMetaData;
    }

}