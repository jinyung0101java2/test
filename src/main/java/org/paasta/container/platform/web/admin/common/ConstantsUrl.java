package org.paasta.container.platform.web.admin.common;

/**
 * Constants 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2021.06.14
 */
public class ConstantsUrl {
    public static final String URI_CP_BASE_URL = "/container-platform";

    public static final String URI_CP_INDEX_URL = "/container-platform";
    public static final String URI_CP_CLUSTERS_NAMESPACES = "/container-platform/namespaces";
    public static final String URI_CP_CLUSTERS_NODES = "/container-platform/nodes";

    public static final String URI_CP_WORKLOADS_DEPLOYMENTS = "/container-platform/deployments";
    public static final String URI_CP_WORKLOADS_PODS = "/container-platform/pods";
    public static final String URI_CP_WORKLOADS_REPLICASETS = "/container-platform/replicaSets";

    public static final String URI_CP_SERVICES_SERVICES = "/container-platform/services";
    public static final String URI_CP_SERVICES_INGRESSES = "/container-platform/ingresses";

    public static final String URI_CP_STORAGES_STORAGECLASSES = "/container-platform/storageClasses";
    public static final String URI_CP_STORAGES_PERSISTENTVOLUMES = "/container-platform/persistentVolumes";
    public static final String URI_CP_STORAGES_PERSISTENTVOLUMECLAIMS = "/container-platform/persistentVolumeClaims";

    public static final String URI_CP_MANAGEMENTS_LIMITRANGES = "/container-platform/limitRanges";
    public static final String URI_CP_MANAGEMENTS_RESOURCEQUOTAS = "/container-platform/resourceQuotas";
    public static final String URI_CP_MANAGEMENTS_ROLES = "/container-platform/roles";
    public static final String URI_CP_MANAGEMENTS_USERS_ADMIN = "/container-platform/admin";
    public static final String URI_CP_MANAGEMENTS_USERS = "/container-platform/users";
    public static final String URI_CP_MANAGEMENTS_INACTIVE_USERS = "/container-platform/inactiveUsers";
    public static final String URI_CP_MANAGEMENTS_CONFIGMAPS = "/container-platform/configMaps";

    public static final String URI_CP_LIST = "/list";
    public static final String URI_CP_DETAILS = "/details";
    public static final String URI_CP_CREATE = "/create";
    public static final String URI_CP_UPDATE = "/update";
    public static final String URI_CP_POPUP = "/popup";

    public static final String URI_CP_GET_USER_LOGIN_DATA = "/container-platform/userLoginData";
    public static final String URI_CP_REFRESH_TOKEN = "/container-platform/refreshToken";

    public static final String URI_CP_SESSION_OUT = "/sessionout";
    public static final String URI_AUTHENTICATION_FAILED = "/error/authenticationFailed";
    public static final String URI_CP_LOGOUT ="/logout";


    //CP-API REQUEST URI
    public static final String URL_API_LOGIN = "/login";
    public static final String URL_API_SIGNUP = "/signUp";

    //LOCALE LANGUAGE
    public static final String URL_API_LOCALE_LANGUAGE = "/localeLanguage";
    public static final String URL_API_CHANGE_LOCALE_PARAM = "language";
    public static final String LANG_KO = "ko";
    public static final String LANG_KO_START_WITH = "ko_";
    public static final String LANG_EN = "en";
}