package org.paasta.container.platform.web.admin.common;

import org.paasta.container.platform.web.admin.common.model.CommonStatusCode;
import org.paasta.container.platform.web.admin.common.model.ResultStatus;
import org.paasta.container.platform.web.admin.login.LoginService;
import org.paasta.container.platform.web.admin.login.model.UsersLoginMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

/**
 * Rest Template Service 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2020.08.25
 */
@Service
public class RestTemplateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestTemplateService.class);
    private static final String AUTHORIZATION_HEADER_KEY = "Authorization";
    private static final String CONTENT_TYPE = "Content-Type";
    private final String cpApiBase64Authorization;
    private final String commonApiBase64Authorization;
    private final RestTemplate restTemplate;
    private final PropertyService propertyService;
    private final LoginService loginService;

    private String base64Authorization;
    private String baseUrl;
    private HttpServletRequest request;


    /**
     * Instantiates a new Rest template service
     *
     * @param cpApiAuthorizationId         the container platform api authorization id
     * @param cpApiAuthorizationPassword   the container platform api authorization password
     * @param commonApiAuthorizationId       the common api authorization id
     * @param commonApiAuthorizationPassword the common api authorization password
     * @param restTemplate                   the rest template
     * @param propertyService                the property service
     * @param loginService                the login service
     * @param request                        the HttpServletRequest
     */
    @Autowired
    public RestTemplateService(@Value("${cpApi.authorization.id}") String cpApiAuthorizationId,
                               @Value("${cpApi.authorization.password}") String cpApiAuthorizationPassword,
                               @Value("${commonApi.authorization.id}") String commonApiAuthorizationId,
                               @Value("${commonApi.authorization.password}") String commonApiAuthorizationPassword,
                               RestTemplate restTemplate,
                               PropertyService propertyService,
                               LoginService loginService,
                               HttpServletRequest request) {
        this.restTemplate = restTemplate;
        this.propertyService = propertyService;
        this.loginService = loginService;
        this.request = request ;
        cpApiBase64Authorization = "Bearer ";
        commonApiBase64Authorization = "Basic "
                + Base64Utils.encodeToString(
                (commonApiAuthorizationId + ":" + commonApiAuthorizationPassword).getBytes(StandardCharsets.UTF_8));
    }


    /**
     * Send t
     *
     * @param <T>          the type parameter
     * @param reqApi       the req api
     * @param reqUrl       the req url
     * @param httpMethod   the http method
     * @param bodyObject   the body object
     * @param responseType the response type
     * @return the t
     */
    public <T> T send(String reqApi, String reqUrl, HttpMethod httpMethod, Object bodyObject, Class<T> responseType) {

        setApiUrlAuthorization(reqApi);

        HttpHeaders reqHeaders = new HttpHeaders();
        reqHeaders.add(AUTHORIZATION_HEADER_KEY, base64Authorization);
        reqHeaders.add(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<Object> reqEntity = new HttpEntity<>(bodyObject, reqHeaders);

        LOGGER.info("<T> T send :: Request : {} {} : {}, Content-Type: {}", CommonUtils.loggerReplace(httpMethod), CommonUtils.loggerReplace(baseUrl), CommonUtils.loggerReplace(reqUrl), CommonUtils.loggerReplace(reqHeaders.get(CONTENT_TYPE)));

        ResponseEntity<T> resEntity = null;

        try {
            resEntity = restTemplate.exchange(baseUrl + reqUrl, httpMethod, reqEntity, responseType);
            if (resEntity.getBody() != null) {

                if(resEntity.getBody().toString().contains(Constants.LOGIN_TOKEN_EXPIRED)) {
                    LOGGER.info("######## REST_TEMPLATE_SERVICE SEND::: API TOKEN EXPIRED...REFRESH TOKEN IS REQUIRED");
                    // refresh token 가져온 후 사용자 토큰 업데이트
                    refreshToken(Constants.TARGET_CP_API, HttpMethod.GET, null, ResultStatus.class);
                    // header 업데이트
                    HttpEntity<Object> updateReqEntity = updateRequestEntity(reqApi, bodyObject, MediaType.APPLICATION_JSON_VALUE);
                    // api 재요청
                    resEntity = restTemplate.exchange(baseUrl + reqUrl, httpMethod, updateReqEntity, responseType);
                }

                LOGGER.info("Response Type: {}", CommonUtils.loggerReplace(resEntity.getBody().getClass()));
                LOGGER.info(CommonUtils.loggerReplace(resEntity.getBody().toString()));
            } else {
                LOGGER.info("Response Type: {}", "response body is null");
            }

            return resEntity.getBody();
        } catch (HttpStatusCodeException exception) {
            LOGGER.info("HttpStatusCodeException API Call URL : {}, errorCode : {}, errorMessage : {}", CommonUtils.loggerReplace(reqUrl), CommonUtils.loggerReplace(exception.getRawStatusCode()), CommonUtils.loggerReplace(exception.getMessage()));

            for (CommonStatusCode code : CommonStatusCode.class.getEnumConstants()) {
                if (code.getCode() == exception.getRawStatusCode()) {
                    return (T) new ResultStatus(Constants.RESULT_STATUS_FAIL, exception.getStatusText(), code.getCode(), code.getMsg());
                }
            }

        } catch (Exception e) {
            return (T) new ResultStatus(Constants.RESULT_STATUS_FAIL, e.getMessage(), CommonStatusCode.INTERNAL_SERVER_ERROR.getCode(), CommonStatusCode.INTERNAL_SERVER_ERROR.getMsg());
        }

        return resEntity.getBody();

    }



    /**
     * Send t
     *
     * @param <T>          the type parameter
     * @param reqApi       the req api
     * @param reqUrl       the req url
     * @param httpMethod   the http method
     * @param bodyObject   the body object
     * @param responseType the response type
     * @param contentType the content type
     * @return the t
     */
    public <T> T sendYaml(String reqApi, String reqUrl, HttpMethod httpMethod, Object bodyObject, Class<T> responseType, String contentType) {

        setApiUrlAuthorization(reqApi);

        HttpHeaders reqHeaders = new HttpHeaders();
        reqHeaders.add(AUTHORIZATION_HEADER_KEY, base64Authorization);
        reqHeaders.add(CONTENT_TYPE, contentType);

        HttpEntity<Object> reqEntity = new HttpEntity<>(bodyObject, reqHeaders);

        LOGGER.info("<T> T send :: Request : {} {} : {}, Content-Type: {}", CommonUtils.loggerReplace(httpMethod), CommonUtils.loggerReplace(baseUrl), CommonUtils.loggerReplace(reqUrl), CommonUtils.loggerReplace(reqHeaders.get(CONTENT_TYPE)));

        ResponseEntity<T> resEntity = null;

        try {
            resEntity = restTemplate.exchange(baseUrl + reqUrl, httpMethod, reqEntity, responseType);
            if (resEntity.getBody() != null) {

                if(resEntity.getBody().toString().contains(Constants.LOGIN_TOKEN_EXPIRED)) {
                    LOGGER.info("######## REST_TEMPLATE_SERVICE SEND YAML::: API TOKEN EXPIRED...REFRESH TOKEN IS REQUIRED");
                    // refresh token 가져온 후 사용자 토큰 업데이트
                    refreshToken(Constants.TARGET_CP_API, HttpMethod.GET, null, ResultStatus.class);
                    // header 업데이트
                    HttpEntity<Object> updateReqEntity = updateRequestEntity(reqApi, bodyObject, contentType);
                    // api 재요청
                    resEntity = restTemplate.exchange(baseUrl + reqUrl, httpMethod, updateReqEntity, responseType);
                }

                LOGGER.info("Response Type: {}", CommonUtils.loggerReplace(resEntity.getBody().getClass()));
                LOGGER.info(CommonUtils.loggerReplace(resEntity.getBody().toString()));
            } else {
                LOGGER.info("Response Type: {}", "response body is null");
            }

            return resEntity.getBody();
        } catch (HttpStatusCodeException exception) {
            LOGGER.info("HttpStatusCodeException API Call URL : {}, errorCode : {}, errorMessage : {}", CommonUtils.loggerReplace(reqUrl), CommonUtils.loggerReplace(exception.getRawStatusCode()), CommonUtils.loggerReplace(exception.getMessage()));

            for (CommonStatusCode code : CommonStatusCode.class.getEnumConstants()) {
                if (code.getCode() == exception.getRawStatusCode()) {
                    return (T) new ResultStatus(Constants.RESULT_STATUS_FAIL, exception.getStatusText(), code.getCode(), code.getMsg());
                }
            }

        } catch (Exception e) {
            return (T) new ResultStatus(Constants.RESULT_STATUS_FAIL, e.getMessage(), CommonStatusCode.INTERNAL_SERVER_ERROR.getCode(), CommonStatusCode.INTERNAL_SERVER_ERROR.getMsg());
        }

        return resEntity.getBody();
    }


    /**
     * Set Authorization according to Target API
     *
     * @param reqApi the reqApi
     */
    private void setApiUrlAuthorization(String reqApi) {

        String apiUrl = "";
        String authorization = "";

        // CONTAINER PLATFORM API
        if (Constants.TARGET_CP_API.equals(reqApi)) {
            apiUrl = propertyService.getCpApiUrl();
            authorization = cpApiBase64Authorization + getAccessToken();
        }

        // COMMON API
        if (Constants.TARGET_COMMON_API.equals(reqApi)) {
            apiUrl = propertyService.getCommonApiUrl();
            authorization = commonApiBase64Authorization;
        }

        base64Authorization = authorization;
        baseUrl = apiUrl;
    }


    /**
     * 현재 사용자 Access token 조회(Get Current User Access Token)
     *
     * @return the string
     */
    public String getAccessToken() {

       UsersLoginMetaData usersLoginMetaData = loginService.getAuthenticationUserMetaData();
       String accessToken = null;

       try {
           accessToken = usersLoginMetaData.getAccessToken();
       }
       catch (NullPointerException e) {

           return null;
       }

      return accessToken;
    }

///// Refresh Token

    /**
     * sendRefreshToken t
     *
     * @param <T>          the type parameter
     * @param reqApi       the req api
     * @param httpMethod   the http method
     * @param bodyObject   the body object
     * @param responseType the response type
     * @return the t
     */
    public <T> T sendRefreshToken(String reqApi, HttpMethod httpMethod, Object bodyObject, Class<T> responseType) {

        setApiUrlAuthorization(reqApi);

        HttpHeaders reqHeaders = new HttpHeaders();
        reqHeaders.add(AUTHORIZATION_HEADER_KEY, base64Authorization);
        reqHeaders.add(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        reqHeaders.add("isRefreshToken", "true");

        HttpEntity<Object> reqEntity = new HttpEntity<>(bodyObject, reqHeaders);
        ResponseEntity<T> resEntity = null;

        try {
            resEntity = restTemplate.exchange(baseUrl + Constants.URI_API_REFRESH_TOKEN, httpMethod, reqEntity, responseType);
            if (resEntity.getBody() != null) {
                LOGGER.info("Response Type: {}", CommonUtils.loggerReplace(resEntity.getBody().getClass()));
                LOGGER.info(CommonUtils.loggerReplace(resEntity.getBody().toString()));
            } else {
                LOGGER.info("Response Type: {}", "response body is null");
            }

            return resEntity.getBody();
        } catch (HttpStatusCodeException exception) {
            LOGGER.info("HttpStatusCodeException API Call URL : {}, errorCode : {}, errorMessage : {}", CommonUtils.loggerReplace(Constants.URI_API_REFRESH_TOKEN), CommonUtils.loggerReplace(exception.getRawStatusCode()), CommonUtils.loggerReplace(exception.getMessage()));

            for (CommonStatusCode code : CommonStatusCode.class.getEnumConstants()) {
                if (code.getCode() == exception.getRawStatusCode()) {
                    return (T) new ResultStatus(Constants.RESULT_STATUS_FAIL, exception.getStatusText(), code.getCode(), code.getMsg());
                }
            }

        } catch (Exception e) {
            return (T) new ResultStatus(Constants.RESULT_STATUS_FAIL, e.getMessage(), CommonStatusCode.INTERNAL_SERVER_ERROR.getCode(), CommonStatusCode.INTERNAL_SERVER_ERROR.getMsg());
        }

        return resEntity.getBody();

    }


    /**
     * 현재 사용자 Token 업데이트 (Get Current User Refresh Token)
     *
     * @return the string
     */
    public <T> void refreshToken(String reqApi, HttpMethod httpMethod, Object bodyObject, Class<T> responseType) {
        LOGGER.info("###############################################################");
        LOGGER.info("GET REFRESH TOKEN");
        LOGGER.info("###############################################################");
        try {
            // refreshToken 받아오기
            ResultStatus resultStatus  =  (ResultStatus) sendRefreshToken(reqApi, httpMethod, bodyObject, responseType);
            // Token 업데이트
            UsersLoginMetaData usersLoginMetaData = loginService.getAuthenticationUserMetaData();
            usersLoginMetaData.setAccessToken(resultStatus.getToken());
            loginService.updateAuthenticationUserMetaData(usersLoginMetaData);
        }
        catch(Exception e) {
            LOGGER.info("##### EXCEPTION IN REFRESH TOKEN GET...");
        }

    }



    /**
     * HttpEntity 값 업데이트 (Update HttpEntity)
     *
     * @return the HttpEntity<Object>
     */
    public  HttpEntity<Object>  updateRequestEntity(String reqApi, Object bodyObject, String contentType) {
        setApiUrlAuthorization(reqApi);

        HttpHeaders reqHeaders = new HttpHeaders();
        reqHeaders.add(AUTHORIZATION_HEADER_KEY, base64Authorization);
        reqHeaders.add(CONTENT_TYPE, contentType);

        HttpEntity<Object> reqEntity = new HttpEntity<>(bodyObject, reqHeaders);
        return reqEntity;
    }




}
