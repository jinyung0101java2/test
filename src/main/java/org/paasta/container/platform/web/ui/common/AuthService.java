package org.paasta.container.platform.web.ui.common;

import org.paasta.container.platform.web.ui.security.DashboardAuthenticationDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Calendar;
import java.util.Date;

/**
 * paastaDeliveryPipelineApi
 * paasta.delivery.pipeline.ui.common
 *
 * @author REX
 * @version 1.0
 * @since 6 /30/2017
 */
@Service
public class AuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);
    private final RestTemplateService restTemplateService;


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


    /**
     * Instantiates a new Common service.
     */
    @Autowired
    public AuthService(RestTemplateService restTemplateService) {
        this.restTemplateService= restTemplateService;
    }


    /**
     * Diff day int.
     *
     * @param d          the d
     * @param accessDate the access date
     * @return the int
     */
    public static int diffDay(Date d, Date accessDate) {
        /**
         * 날짜 계산
         */
        Calendar curC = Calendar.getInstance();
        Calendar accessC = Calendar.getInstance();
        curC.setTime(d);
        accessC.setTime(accessDate);
        accessC.compareTo(curC);
        int diffCnt = 0;
        while (!accessC.after(curC)) {
            diffCnt++;
            accessC.add(Calendar.DATE, 1); // 다음날로 바뀜
        }
//        System.out.println("기준일로부터 " + diffCnt + "일이 지났습니다.");
//        System.out.println(accessC.compareTo(curC));
        return diffCnt;
    }




    /**
     * Gets user info.
     *
     * @return the user info
     */
    public DashboardAuthenticationDetails getUserInfo() {

        return (DashboardAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();
    }


    /**
     * Gets user id.
     *
     * @return the user id
     */
    public String getUserId() {
        //return SecurityContextHolder.getContext().getAuthentication().getName();
    	return ((DashboardAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails()).getUserid();
    }



    /**
     * Sets details.
     *
     * @param dashboardAuthenticationDetails the dashboard authentication details
     * @param roleString                     the role string
     * @param roleId                         the role id
     * @param roleDisplayName                the role display name
     * @return the details
     */
    public DashboardAuthenticationDetails setDetails(DashboardAuthenticationDetails dashboardAuthenticationDetails, String roleString, String roleId, String roleDisplayName) {
        return dashboardAuthenticationDetails;
    }


    
    public String getKeyCloakToken() {
        DashboardAuthenticationDetails user = ((DashboardAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails());
        LOGGER.debug("############################# Token Expired : " + (user.getAccessToken().getExpiration().getTime() - System.currentTimeMillis()) / 1000 + " sec");
        // Token 만료 시간 비교
        if (user.getAccessToken().getExpiration().getTime() <= System.currentTimeMillis()) {
            //Rest 생성
            RestTemplate rest = new RestTemplate();
            //Token 재요청을 위한 데이터 설정
            OAuth2ProtectedResourceDetails resource = getResourceDetails(user.getUserid(), "N/A", clientId, clientSecret, accessUri);
            AccessTokenRequest accessTokenRequest = new DefaultAccessTokenRequest();
            ResourceOwnerPasswordAccessTokenProvider provider = new ResourceOwnerPasswordAccessTokenProvider();
            provider.setRequestFactory(rest.getRequestFactory());
            //Token 재요청
            OAuth2AccessToken refreshToken = provider.refreshAccessToken(resource, user.getAccessToken().getRefreshToken(), accessTokenRequest);


            //재요청으로 받은 Token 재설정
            user.setAccessToken(refreshToken);
            // session에 적용
            Authentication authentication = new UsernamePasswordAuthenticationToken(SecurityContextHolder.getContext().getAuthentication(), "N/A", SecurityContextHolder.getContext().getAuthentication().getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        String token = user.getTokenValue();

        return token;
    }


    private OAuth2ProtectedResourceDetails getResourceDetails(String username, String password, String clientId, String clientSecret, String url) {
        ResourceOwnerPasswordResourceDetails resource = new ResourceOwnerPasswordResourceDetails();
        resource.setUsername(username);
        resource.setPassword(password);
        resource.setClientId(clientId);
        resource.setClientSecret(clientSecret);
        resource.setId(clientId);
        resource.setClientAuthenticationScheme(AuthenticationScheme.header);
        resource.setAccessTokenUri(url);

        return resource;
    }

    public static final String MANAGED_KEY = "manage";

    /**
     * Checks whether the user is allowed to manage the current service instance.
     */
    private boolean isManagingApp(String serviceInstanceId) {
        final String url = getCheckUrl(serviceInstanceId);

        return true;
    }


    /**
     * Checks whether the user is allowed to manage the current service instance.
     * isManagingApp()  => isManagingAppForCF() rename
     */
    private boolean isManagingAppForCF(String serviceInstanceId) {
        return true;
    }



    private String getCheckUrl(String serviceInstanceId) {
        return "";
    }



    /**
     * The enum Path variables list.
     */
    enum PathVariablesList {
        /**
         * Path variables su id path variables list.
         */
        PATH_VARIABLES_SU_ID("suid"),
        /**
         * Path variables service instance id path variables list.
         */
        PATH_VARIABLES_SERVICE_INSTANCE_ID("serviceInstancesId"),
        /**
         * Path variables pipeline id path variables list.
         */
        PATH_VARIABLES_PIPELINE_ID("pipelineId"),
        /**
         * Path variables job type path variables list.
         */
        PATH_VARIABLES_JOB_TYPE("jobType"),
        /**
         * Path variables job history id path variables list.
         */
        PATH_VARIABLES_JOB_HISTORY_ID("jobHistoryId"),
        /**
         * Path variables id path variables list.
         */
        PATH_VARIABLES_ID("id");

        private String actualValue;

        PathVariablesList(String actualValue) {
            this.actualValue = actualValue;
        }
    }


    /**
     * The enum Parameters list.
     */
    enum ParametersList {
        /**
         * Parameters id parameters list.
         */
        PARAMETERS_ID("id"),
        /**
         * Parameters name parameters list.
         */
        PARAMETERS_NAME("name"),
        /**
         * Parameters page parameters list.
         */
        PARAMETERS_PAGE("page"),
        /**
         * Parameters size parameters list.
         */
        PARAMETERS_SIZE("size"),
        /**
         * Parameters sort parameters list.
         */
        PARAMETERS_SORT("sort"),
        /**
         * Parameters job type parameters list.
         */
        PARAMETERS_JOB_TYPE("jobType"),
        /**
         * Parameters group order parameters list.
         */
        PARAMETERS_GROUP_ORDER("groupOrder"),
        /**
         * Parameters job order parameters list.
         */
        PARAMETERS_JOB_ORDER("jobOrder"),
        /**
         * Parameters auth type parameters list.
         */
        PARAMETERS_AUTH_TYPE("authName"),
        /**
         * Parameters cf name parameters list.
         */
        PARAMETERS_CF_NAME("cfName"),
        /**
         * Parameters kube name parameters list.
         */
        PARAMETERS_CONFIG_NAME("configName"),
        /**
         * Parameters vm name parameters list.
         */
        PARAMETERS_VM_NAME("vmName");

        private String actualValue;

        ParametersList(String actualValue) {
            this.actualValue = actualValue;
        }
    }
    


}
