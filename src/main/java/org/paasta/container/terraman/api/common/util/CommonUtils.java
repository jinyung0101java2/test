package org.paasta.container.terraman.api.common.util;

import org.paasta.container.terraman.api.common.constants.Constants;
import org.paasta.container.terraman.api.common.model.ResultStatusModel;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;

/**
 * Common Utils 클래스
 *
 * @author yjh
 * @version 1.0
 * @since 2022.07.11
 */

public class CommonUtils {
    /**
     * Timestamp Timezone 을 변경하여 재설정(reset timestamp)
     *
     * @param requestTimestamp the request timestamp
     * @return the string
     */
    public static String procSetTimestamp(String requestTimestamp) {
        String resultString = "";

        if (null == requestTimestamp || "".equals(requestTimestamp)) {
            return resultString;
        }

        SimpleDateFormat simpleDateFormatForOrigin = new SimpleDateFormat(Constants.STRING_ORIGINAL_DATE_TYPE);
        SimpleDateFormat simpleDateFormatForSet = new SimpleDateFormat(Constants.STRING_DATE_TYPE);

        try {
            Date parseDate = simpleDateFormatForOrigin.parse(requestTimestamp);
            long parseDateTime = parseDate.getTime();
            int offset = TimeZone.getTimeZone(Constants.STRING_TIME_ZONE_ID).getOffset(parseDateTime);

            resultString = simpleDateFormatForSet.format(parseDateTime + offset);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultString;
    }

    public static String getSysTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        long parseDateTime = timestamp.getTime();
        int offset = TimeZone.getTimeZone(Constants.STRING_TIME_ZONE_ID).getOffset(parseDateTime);
        return sdf.format(parseDateTime + offset);
    }

    public static String getSysString() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatedNow = now.format(formatter);
        return formatedNow;
    }

    /**
     * Yaml match map
     *
     * @param username  the username
     * @param namespace the namespace
     * @return the map
     */
    public static Map<String, Object> yamlMatch(String username, String namespace) {
        return new HashMap<String, Object>() {{
            put("userName", username);
            put("spaceName", namespace);
        }};
    }




    /**
     * Object 목록 값 수정(Object List value modify)
     *
     * @param obj    the obj
     * @param index  the index
     * @param newObj the newObj
     * @return the Object List
     */
    public static Object[] modifyValue(Object[] obj, int index, Object newObj) {
        ArrayList<Object> list = new ArrayList<>(Arrays.asList(obj));
        if (index < list.size()) {
            list.set(index, newObj);
        }

        return list.toArray();
    }

    /**
     * Resource name check string
     *
     * @param resourceName the resource name
     * @return the string
     */
    public static String resourceNameCheck(String resourceName) {
        return (resourceName == null) ? Constants.NO_NAME : resourceName;
    }

    /**
     * Is result status instance check boolean
     *
     * @param object the object
     * @return the boolean
     */
    public static boolean isResultStatusInstanceCheck(Object object) {
        return object instanceof ResultStatusModel;
    }

    /**
     * Proc replace null value object
     *
     * @param requestObject the request object
     * @return object
     */
    public static Object procReplaceNullValue(Object requestObject) {
        return (StringUtils.isEmpty(requestObject)) ? Constants.NULL_REPLACE_TEXT : requestObject;
    }

    /**
     * Proc replace null value string
     *
     * @param requestString the request string
     * @return the string
     */
    public static String procReplaceNullValue(String requestString) {
        return (StringUtils.isEmpty(requestString)) ? Constants.NULL_REPLACE_TEXT : requestString;
    }

    /**
     * Proc replace null value map
     *
     * @param requestMap the request map
     * @return the map
     */
    public static Map<String, Object> procReplaceNullValue(Map<String, Object> requestMap) {
        return (StringUtils.isEmpty(requestMap)) ? new HashMap<String, Object>() {{
            put(Constants.SUPPORTED_RESOURCE_STORAGE, Constants.NULL_REPLACE_TEXT);
        }} : requestMap;
    }

    /**
     * LOGGER 개행문자 제거 (Object)
     *
     * @param obj
     * @return String the replaced string
     */
    public static String loggerReplace(Object obj) {
        return obj.toString().replaceAll("[\r\n]","");
    }

    /**
     * LOGGER 개행문자 제거 (String)
     *
     * @param str
     * @return String the replaced string
     */
    public static String loggerReplace(String str) {
        return str.replaceAll("[\r\n]","");
    }

    /**
     * hostname 변환 (String)
     *
     * @param str
     * @return String the replaced string
     */
    public static String hostName(String str) {
        if( StringUtils.hasText(str) && (str != "null") ) {
            str = str.trim().replaceAll("[.]","-");
            str = "ip-" + str;
        }
        return str;
    }

}
