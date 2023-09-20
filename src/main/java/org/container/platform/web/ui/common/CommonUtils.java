package org.container.platform.web.ui.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StringUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Common Utils 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.25
 */
public class CommonUtils {

    /**
     * 요청 파라미터들의 빈값 또는 null 값 확인을 하나의 메소드로 처리할 수 있도록 생성한 메소드
     * 요청 파라미터 중 빈값 또는 null 값인 파라미터가 있는 경우, false 를 리턴
     *
     * @param params the string
     * @return the boolean
     */
    public static boolean paramCheck(String... params) {
        return Arrays.stream(params).allMatch(param -> null != param && !param.equals(""));
    }


    /**
     * 요청 파라미터들 중 빈 값 또는 null 인 파라미터를 추출
     *
     * @param obj the Object
     * @return the List<String>
     */
    public static List<String> stringNullCheck(Object obj) {
        List<String> checkParamList = new ArrayList<>();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> map = objectMapper.convertValue(obj, Map.class);

        for (String key : map.keySet()) {
            if (StringUtils.hasText(map.get(key))) {
                checkParamList.add(key);
            }
        }

        return checkParamList;
    }



    /**
     * 쌍따옴표 및 대괄호 제거
     *
     * @param str
     * @return String the replaced string
     */
    public static String stringReplace(String str) {
        String match = "\\\"";
        str = str.replaceAll(match, "").replaceAll("[\\[\\]]", "");
        return str;
    }


    /**
     * 리소스 리스트 조회 시 파라미터 쿼리 생성
     *
     * @param offset
     * @param limit
     * @param orderBy
     * @param order
     * @param searchName
     * @return the String
     */
    public static String makeResourceListParamQuery(int offset, int limit, String orderBy, String order, String searchName) {
        String param = "?offset=" + offset + "&limit=" + limit + "&orderBy=" + orderBy + "&order=" + order;
        if (searchName != null) {
            param += "&searchName=" + searchName;
        }
        return param;
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

}

