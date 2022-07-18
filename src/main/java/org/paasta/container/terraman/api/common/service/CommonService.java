package org.paasta.container.terraman.api.common.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.paasta.container.terraman.api.common.constants.Constants;
import org.paasta.container.terraman.api.common.constants.CommonStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

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
    private final Gson gson;
    private final PropertyService propertyService;
    private final HttpServletRequest request;


    /**
     * Instantiates a new Common service
     *
     * @param gson the gson
     */
    @Autowired
    public CommonService(Gson gson, PropertyService propertyService, HttpServletRequest request) {
        this.gson = gson;
        this.propertyService = propertyService;
        this.request = request;
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
            ObjectMapper oMapper = new ObjectMapper();
            Map map = oMapper.convertValue(reqObject, Map.class);

            Method methodSetResultCode = aClass.getMethod("setResultCode", String.class);
            Method methodSetResultMessage = aClass.getMethod("setResultMessage", String.class);
            Method methodSetHttpStatusCode = aClass.getMethod("setHttpStatusCode", Integer.class);
            Method methodSetDetailMessage = aClass.getMethod("setDetailMessage", String.class);

            if (Constants.RESULT_STATUS_FAIL.equals(resultCode)) {
                methodSetResultCode.invoke(reqObject, resultCode);
                methodSetResultMessage.invoke(reqObject, CommonStatusCode.CONFLICT.getMsg());
                methodSetHttpStatusCode.invoke(reqObject, CommonStatusCode.CONFLICT.getCode());
                methodSetDetailMessage.invoke(reqObject, CommonStatusCode.CONFLICT.getMsg());
            } else {
                methodSetResultCode.invoke(reqObject, resultCode);
                methodSetResultMessage.invoke(reqObject, CommonStatusCode.OK.getMsg());
                methodSetHttpStatusCode.invoke(reqObject, CommonStatusCode.OK.getCode());
                methodSetDetailMessage.invoke(reqObject, CommonStatusCode.OK.getMsg());

            }

        } catch (NoSuchMethodException e) {
            LOGGER.error("NoSuchMethodException :: {}", e);
        } catch (IllegalAccessException e1) {
            LOGGER.error("IllegalAccessException :: {}", e1);
        } catch (InvocationTargetException e2) {
            LOGGER.error("InvocationTargetException :: {}", e2);
        }

        return reqObject;
    }

    /**
     * result model 설정(Sets result model)
     *
     * @param reqObject  the req object
     * @param resultCode the result code
     * @param resultData the resultData code
     * @return the result model
     */
    public Object setResultModel(Object reqObject, String resultCode, Object resultData) {
        try {
            Class<?> aClass = reqObject.getClass();
            ObjectMapper oMapper = new ObjectMapper();
            Map map = oMapper.convertValue(reqObject, Map.class);

            Method methodSetResultCode = aClass.getMethod("setResultCode", String.class);
            Method methodSetResultMessage = aClass.getMethod("setResultMessage", String.class);
            Method methodSetHttpStatusCode = aClass.getMethod("setHttpStatusCode", Integer.class);
            Method methodSetDetailMessage = aClass.getMethod("setDetailMessage", String.class);
            Method methodSetOutData = aClass.getMethod("setOut", Object.class);

            if (Constants.RESULT_STATUS_FAIL.equals(map.get("resultCode"))) {
                methodSetResultCode.invoke(reqObject, map.get("resultCode"));
                methodSetResultMessage.invoke(reqObject, map.get("resultMessage"));
            } else {
                methodSetResultCode.invoke(reqObject, resultCode);
                methodSetResultMessage.invoke(reqObject, CommonStatusCode.OK.getMsg());
                methodSetHttpStatusCode.invoke(reqObject, CommonStatusCode.OK.getCode());
                methodSetDetailMessage.invoke(reqObject, CommonStatusCode.OK.getMsg());
                methodSetOutData.invoke(reqObject, resultData);
            }

        } catch (NoSuchMethodException e) {
            LOGGER.error("NoSuchMethodException :: {}", e);
        } catch (IllegalAccessException e1) {
            LOGGER.error("IllegalAccessException :: {}", e1);
        } catch (InvocationTargetException e2) {
            LOGGER.error("InvocationTargetException :: {}", e2);
        }

        return reqObject;
    }

    /**
     * 생성/수정/삭제 후 페이지 이동을 위한 result model 설정(Set result model for moving the page after a create/update/delete)
     *
     * @param reqObject     the reqObject
     * @param resultCode    the resultCode
     * @param nextActionUrl the nextActionUrl
     * @return the object
     */
    public Object setResultModelWithNextUrl(Object reqObject, String resultCode, String nextActionUrl) {
        try {
            Class<?> aClass = reqObject.getClass();
            ObjectMapper oMapper = new ObjectMapper();
            Map map = oMapper.convertValue(reqObject, Map.class);

            Method methodSetResultCode = aClass.getMethod("setResultCode", String.class);
            Method methodSetNextActionUrl = aClass.getMethod("setNextActionUrl", String.class);

            if (Constants.RESULT_STATUS_FAIL.equals(map.get("resultCode"))) {
                methodSetResultCode.invoke(reqObject, map.get("resultCode"));
            } else {
                methodSetResultCode.invoke(reqObject, resultCode);
            }

            if (nextActionUrl != null) {
                methodSetNextActionUrl.invoke(reqObject, nextActionUrl);
            }

        } catch (NoSuchMethodException e) {
            LOGGER.error("NoSuchMethodException :: {}", e);
        } catch (IllegalAccessException e1) {
            LOGGER.error("IllegalAccessException :: {}", e1);
        } catch (InvocationTargetException e2) {
            LOGGER.error("InvocationTargetException :: {}", e2);
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

    /**
     * 서로 다른 객체를 매핑 (mapping each other objects)
     *
     * @param instance    the instance
     * @param targetClass the targetClass
     * @param <A>         the type parameter
     * @param <B>         the type parameter
     * @return the b
     * @throws Exception
     */
    public <A, B> B convert(A instance, Class<B> targetClass) throws Exception {
        B target = targetClass.newInstance();

        for (Field targetField : targetClass.getDeclaredFields()) {
            Field[] instanceFields = instance.getClass().getDeclaredFields();

            for (Field instanceField : instanceFields) {
                if (targetField.getName().equals(instanceField.getName())) {
                    targetField.set(target, instance.getClass().getDeclaredField(targetField.getName()).get(instance));
                }
            }
        }
        return target;
    }


    /**
     * 필드를 조회하고, 그 값을 반환 처리(check the field and return the result)
     *
     * @param fieldName the fieldName
     * @param obj       the obj
     * @return the t
     */
    @SneakyThrows
    public <T> T getField(String fieldName, Object obj) {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        Object result = field.get(obj);
        field.setAccessible(false);
        return (T) result;
    }

    /**
     * 필드를 조회하고, 그 값을 저장 처리(check the field and save the result)
     *
     * @param fieldName the fieldName
     * @param obj       the obj
     * @param value     the value
     * @return the object
     */
    @SneakyThrows
    public Object setField(String fieldName, Object obj, Object value) {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
        field.setAccessible(false);
        return obj;
    }
}
