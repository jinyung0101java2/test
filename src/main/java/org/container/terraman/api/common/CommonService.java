package org.container.terraman.api.common;

import com.google.gson.Gson;
import org.apache.commons.codec.binary.Base64;
import org.container.terraman.api.common.constants.Constants;
import org.container.terraman.api.common.constants.CommonStatusCode;
import org.container.terraman.api.common.model.NcloudInstanceKeyModel;
import org.container.terraman.api.common.util.CommonUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

/**
 * Common Service 클래스
 *
 * @author yjh
 * @version 1.0
 * @since 2022.07.11
 */
@Service
public class CommonService {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonService.class);
    private static final String SET_RESULT_CODE = "setResultCode";
    private static final String SET_RESULT_MESSAGE = "setResultMessage";
    private static final String SET_HTTP_STATUS_CODE = "setHttpStatusCode";
    private static final String SET_DETAIL_MESSAGE = "setDetailMessage";
    private static final String NO_SUCH_METHOD_EXCEPTION_LOG = "NoSuchMethodException :: {}";
    private static final String ILLEGAL_ACCESS_EXCEPTION_LOG = "IllegalAccessException :: {}";
    private static final String INVOCATION_TARGET_EXCEPTION_LOG = "InvocationTargetException :: {}";
    private final RestTemplate restTemplate;
    private final Gson gson;


    /**
     * Instantiates a new Common service
     *
     * @param restTemplate
     * @param gson         the gson
     */
    @Autowired
    public CommonService(RestTemplate restTemplate, Gson gson) {
        this.restTemplate = restTemplate;
        this.gson = gson;
    }


    /**
     * result model 설정(Sets result model)
     *
     * @param reqObject  the req object
     * @param resultCode the result code
     * @return the result model
     */
    public Object setResultModel(Object reqObject, String resultCode) {
        try {
            Class<?> aClass = reqObject.getClass();

            Method methodSetResultCode = aClass.getMethod(SET_RESULT_CODE, String.class);
            Method methodSetResultMessage = aClass.getMethod(SET_RESULT_MESSAGE, String.class);
            Method methodSetHttpStatusCode = aClass.getMethod(SET_HTTP_STATUS_CODE, Integer.class);
            Method methodSetDetailMessage = aClass.getMethod(SET_DETAIL_MESSAGE, String.class);

            if (Constants.RESULT_STATUS_FAIL.equals(resultCode)) {
                methodSetResultCode.invoke(reqObject, resultCode);
                methodSetResultMessage.invoke(reqObject, CommonStatusCode.INTERNAL_SERVER_ERROR.getMsg());
                methodSetHttpStatusCode.invoke(reqObject, CommonStatusCode.INTERNAL_SERVER_ERROR.getCode());
                methodSetDetailMessage.invoke(reqObject, CommonStatusCode.INTERNAL_SERVER_ERROR.getMsg());
            } else {
                methodSetResultCode.invoke(reqObject, resultCode);
                methodSetResultMessage.invoke(reqObject, CommonStatusCode.OK.getMsg());
                methodSetHttpStatusCode.invoke(reqObject, CommonStatusCode.OK.getCode());
                methodSetDetailMessage.invoke(reqObject, CommonStatusCode.OK.getMsg());

            }

        } catch (NoSuchMethodException e) {
            LOGGER.error(NO_SUCH_METHOD_EXCEPTION_LOG, CommonUtils.loggerReplace(e));
        } catch (IllegalAccessException e1) {
            LOGGER.error(ILLEGAL_ACCESS_EXCEPTION_LOG, CommonUtils.loggerReplace(e1));
        } catch (InvocationTargetException e2) {
            LOGGER.error(INVOCATION_TARGET_EXCEPTION_LOG, CommonUtils.loggerReplace(e2));
        }

        return reqObject;
    }

    /**
     * result object 설정(Set result object)
     *
     * @param <T>           the type parameter
     * @param requestObject the request object
     * @param requestClass  the request class
     * @return the result object
     */
    public <T> T setResultObject(Object requestObject, Class<T> requestClass) {
        return this.fromJson(this.toJson(requestObject), requestClass);
    }

    /**
     * json string 으로 변환(To json string)
     *
     * @param requestObject the request object
     * @return the string
     */
    public String toJson(Object requestObject) {
        return gson.toJson(requestObject);
    }


    /**
     * json 에서 t로 변환(From json t)
     *
     * @param <T>           the type parameter
     * @param requestString the request string
     * @param requestClass  the request class
     * @return the t
     */
    private <T> T fromJson(String requestString, Class<T> requestClass) {
        return gson.fromJson(requestString, requestClass);
    }

    /**
     * t 전송(sendNcloudJson t)
     *
     * @param <T>          the type parameter
     * @param reqUrl       the req url
     * @param httpMethod   the http method
     * @param bodyObject the body object
     * @param responseType the response type
     * @return the t
     */
    public <T> T sendNcloudJson(String reqUrl, HttpMethod httpMethod, Object bodyObject, Class<T> responseType){
        HttpHeaders reqHeaders = new HttpHeaders();
        reqHeaders.add(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        reqHeaders.add("ACCEPT", MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<Object> reqEntity;
        reqEntity = new HttpEntity<>(bodyObject, reqHeaders);

        ClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);

        ResponseEntity<T> resEntity = null;

        resEntity = restTemplate.exchange(reqUrl, httpMethod, reqEntity, responseType);

        return resEntity.getBody();
    };


    /**
     * Make Signature 설정
     *
     * @param timestamp the timestamp
     * @param accessKey the access key
     * @param secretKey the secret key
     * @param targetURL the target URL
     * @param param the param
     * @return null
     */
    public static String makeSignature(long timestamp, String accessKey, String secretKey, String targetURL, String param) {

        URI uri = URI.create(targetURL);
        StringBuilder message = new StringBuilder()
                .append("POST")
                .append(" ").append(uri).append("?").append(param)
                .append("\n")
                .append(timestamp)
                .append("\n")
                .append(accessKey);
        try {
            SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);

            byte[] rawHmac = mac.doFinal(message.toString().getBytes("UTF-8"));
            String signature = Base64.encodeBase64String(rawHmac);
            return signature;

        } catch (Exception e) {
        }
        return null;
    }

    /**
     * Data 설정
     *
     * @param timestamp the timestamp
     * @param accessKey the access key
     * @param Url the Url
     * @param signature the signature
     * @return String
     */
    public String getData(long timestamp, String accessKey, String Url, String signature) {
        String ret = "";

        try {

            URL url = new URL(Url);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("x-ncp-apigw-timestamp", String.valueOf(timestamp));
            conn.setRequestProperty("x-ncp-iam-access-key", accessKey);
            conn.setRequestProperty("x-ncp-apigw-signature-v2", signature);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;

            while((line = br.readLine()) != null) {
                sb.append(line);
            }

            JSONObject obj = new JSONObject(sb.toString());
            JSONObject subObj = obj.getJSONObject("getRootPasswordResponse");
            ret = subObj.getString("rootPassword");

        } catch (Exception e) {
        }
        return ret;
    }

}
