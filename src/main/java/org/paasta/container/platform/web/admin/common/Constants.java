package org.paasta.container.platform.web.admin.common;

import java.util.Arrays;
import java.util.List;

/**
 * Constants 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2020.08.25
 */
public class Constants {

    // COMMON
    public static final String RESULT_STATUS_SUCCESS = "SUCCESS";
    public static final String RESULT_STATUS_FAIL = "FAIL";

    public static final String TARGET_CP_API = "cpApi";
    public static final String TARGET_COMMON_API = "commonApi";

    public static final String URI_API_USERS_DETAIL = "/clusters/{cluster:.+}/namespaces/{namespace:.+}/users/{userId:.+}";

    public static final String EMPTY_VALUE ="-";


    public static final String LOGIN_TOKEN_EXPIRED = "TOKEN_EXPIRED";
    public static final String CLUSTER_ADMIN_ALREADY_REGISTERED = "CLUSTER_ADMIN_ALREADY_REGISTERED";
    public static final String NOT_CLUSTER_ADMIN_ROLE = "NOT_CLUSTER_ADMIN_ROLE";
    public static final String LOGIN_FAIL = "LOGIN_FAILED";
    public static final String USER_NOT_REGISTERED_IN_KEYCLOAK = "USER_NOT_REGISTERED_IN_KEYCLOAK";

    public static final List<String> LOGIN_UNAUTHORIZED_MESSAGE =   Arrays.asList(new String[]{LOGIN_FAIL, NOT_CLUSTER_ADMIN_ROLE, USER_NOT_REGISTERED_IN_KEYCLOAK});

    public static final String CHECK_Y = "Y";
    public static final String CHECK_N = "N";

    public static final String CHECK_TRUE = "true";
    public static final String CHECK_FALSE = "false";

    public static final String SELECTED_ADMINISTRATOR = "administrator";
    public static final String SELECTED_USER = "user";


    public static final String URI_API_REFRESH_TOKEN = "/refreshtoken";
    public static final String ALL_VAL = "ALL";

    public static final String SERVICE_SESSION_REFRESH = "sessionRefresh";

    private Constants() {
        throw new IllegalStateException();
    }

}
