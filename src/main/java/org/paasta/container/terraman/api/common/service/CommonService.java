package org.paasta.container.terraman.api.common.service;

import com.google.gson.Gson;
import org.paasta.container.terraman.api.common.constants.Constants;
import org.paasta.container.terraman.api.common.constants.CommonStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Common Service 클래스
 *
 * @author yjh
 * @version 1.0
 * @since 2022.07.11
 */
@Service
public class CommonService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonService.class);
    private static final String SET_RESULT_CODE = "setResultCode";
    private static final String SET_RESULT_MESSAGE = "setResultMessage";
    private static final String SET_HTTP_STATUS_CODE = "setHttpStatusCode";
    private static final String SET_DETAIL_MESSAGE = "setDetailMessage";
    private static final String NO_SUCH_METHOD_EXCEPTION_LOG = "NoSuchMethodException :: {}";
    private static final String ILLEGAL_ACCESS_EXCEPTION_LOG = "IllegalAccessException :: {}";
    private static final String INVOCATION_TARGET_EXCEPTION_LOG = "InvocationTargetException :: {}";
    private final Gson gson;


    /**
     * Instantiates a new Common service
     *
     * @param gson the gson
     */
    @Autowired
    public CommonService(Gson gson) {
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
            LOGGER.error(NO_SUCH_METHOD_EXCEPTION_LOG, e);
        } catch (IllegalAccessException e1) {
            LOGGER.error(ILLEGAL_ACCESS_EXCEPTION_LOG, e1);
        } catch (InvocationTargetException e2) {
            LOGGER.error(INVOCATION_TARGET_EXCEPTION_LOG, e2);
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
    private String toJson(Object requestObject) {
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
}
