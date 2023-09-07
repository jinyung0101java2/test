package org.container.platform.web.ui.security;


import org.container.platform.web.ui.login.model.UsersLoginMetaData;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * Extension of {@link OAuth2AuthenticationDetails} providing extra details about the current
 * user and his grant to manage the current service instance.
 *
 * @author Sebastien Gerard
 */
@SuppressWarnings("serial")
public class DashboardAuthenticationDetails extends OAuth2AuthenticationDetails {

    private final String id;
    private final String userid;
    private final String userFullName;
    private final List<String> roles;

    private final RestTemplate restTemplate;
    private OAuth2AccessToken accessToken;

    private UsersLoginMetaData usersLoginMetaData;

    /**
     * Records the access token value and remote address and will also set the session Id if a session already exists
     * (it won't create one).
     * @param request that the authentication request was received from
     * @param id
     * @param userFullName the full user name (first name + last name)
     * @param restTemplate 사용자의 권한이 포함되어 있는 RestTemplate
     */
    public DashboardAuthenticationDetails(HttpServletRequest request, String id, String user_id, List<String> roles, String userFullName, RestTemplate restTemplate) {
        super(request);

        this.id = id;
        this.userid = user_id;
        this.roles =  roles;
        this.userFullName = userFullName;
        this.restTemplate = restTemplate;
    }



    public String getId() {
        return id;
    }

    public String getUserid() {
        return userid;
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public OAuth2AccessToken getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(OAuth2AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    public UsersLoginMetaData getUsersLoginMetaData() {
        return usersLoginMetaData;
    }

    public void setUsersLoginMetaData(UsersLoginMetaData usersLoginMetaData) {
        this.usersLoginMetaData = usersLoginMetaData;
    }

    public String getUserFullName() { return userFullName; }

    public List<String> getRoles() { return roles; }
}
