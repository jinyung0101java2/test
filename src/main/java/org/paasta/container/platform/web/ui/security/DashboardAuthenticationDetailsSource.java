package org.paasta.container.platform.web.ui.security;

import org.paasta.container.platform.web.ui.common.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * {@link AuthenticationDetailsSource} providing extra details about the current
 * user and his grant to manage the current service instance.
 *
 * @author Sebastien Gerard
 */
public class DashboardAuthenticationDetailsSource
        implements AuthenticationDetailsSource<HttpServletRequest, OAuth2AuthenticationDetails> {

    /**
     * Token to use in {@link #getCheckUrl(String serviceInstanceId)} to specify the service instance id.
     */
    public static final String TOKEN_SUID = "[SUID]";

    /**
     * Key used in the JSON map returned by the call to {@link #getCheckUrl(String serviceInstanceId)} and associated
     * to the service instance id.
     */

    private static final Logger logger = LoggerFactory.getLogger(DashboardAuthenticationDetailsSource.class);

    private final RestTemplate restTemplate;
    private final String userInfoUrl;

    /**
     * Returns the full name (first + last name) contains in the specified map.
     */
    protected static String getUserFullName(Map<String, String> map) {
    	if (map.containsKey("preferred_username")) {
            return map.get("preferred_username");
        }
        if (map.containsKey("name")) {
            return map.get("name");
        }
        if (map.containsKey("formattedName")) {
            return map.get("formattedName");
        }
        if (map.containsKey("fullName")) {
            return map.get("fullName");
        }
        String firstName = null;
        if (map.containsKey("firstName")) {
            firstName = map.get("firstName");
        }
        if (map.containsKey("givenName")) {
            firstName = map.get("givenName");
        }
        String lastName = null;
        if (map.containsKey("lastName")) {
            lastName = map.get("lastName");
        }
        if (map.containsKey("familyName")) {
            lastName = map.get("familyName");
        }
        if (firstName != null) {
            if (lastName != null) {
                return firstName + " " + lastName;
            }
        }
        return null;
    }

    /**
     * @param restTemplate the template to use to contact Cloud components
     * @param userInfoUrl the URL used to get the current OAuth user details
     */
    public DashboardAuthenticationDetailsSource(RestTemplate restTemplate,
                                                String userInfoUrl) {
        this.restTemplate = restTemplate;
        this.userInfoUrl = userInfoUrl;
    }

    @Override
    public DashboardAuthenticationDetails buildDetails(HttpServletRequest request) {

//        String serviceInstanceId = "";


        Map<String, Object> uaaUserInfo = null;
        try {
            uaaUserInfo = restTemplate.getForObject(userInfoUrl, Map.class);
        } catch (RestClientException e) {
            logger.error("Error while user full name from [" + CommonUtils.loggerReplace(userInfoUrl) + "].", e);
            return null;
        }

        String id = (String) uaaUserInfo.get("sub");//ab130dde-d42e-43cc-a6d1-fd7153bfa3ce
        String userid = (String) uaaUserInfo.get("preferred_username"); //username
        List<String> roles = new ArrayList<>();

        if(uaaUserInfo.get("roles") != null) {
            roles = (List) uaaUserInfo.get("roles"); // ['offline_access','uma_authorization']
        }

        Map<String, String> userFullNameInfo = (Map) uaaUserInfo;
        DashboardAuthenticationDetails authenticationDetails = new DashboardAuthenticationDetails(request, id, userid, roles, getUserFullName(userFullNameInfo), this.restTemplate);
        authenticationDetails.setAccessToken(((OAuth2RestTemplate) restTemplate).getAccessToken());

        return authenticationDetails;
    }

    /**
     * Checks whether the user is allowed to manage the current service instance.
     */
    private boolean isManagingApp(String serviceInstanceId) {
        final String url = getCheckUrl(serviceInstanceId);
        return true;
    }


    /**
     * Returns the URL used to check whether the current user is allowed
     * to access the current service instance.
     */
    private String getCheckUrl(String serviceInstanceId) {
        return "";
    }

    private RestTemplate getRestTemplate() {
        return this.restTemplate;
    }
}
