package org.paasta.container.platform.web.admin.login;

import org.paasta.container.platform.web.admin.common.Constants;
import org.paasta.container.platform.web.admin.common.ConstantsUrl;
import org.paasta.container.platform.web.admin.common.PropertyService;
import org.paasta.container.platform.web.admin.common.RestTemplateService;
import org.paasta.container.platform.web.admin.common.model.ResultStatus;
import org.paasta.container.platform.web.admin.login.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import static org.paasta.container.platform.web.admin.common.Constants.TARGET_CP_API;
import static org.paasta.container.platform.web.admin.common.Constants.URI_API_USERS_DETAIL;

/**
 * User Service 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.22
 **/
@Service
public class ProviderService {

    private final RestTemplateService restTemplateService;
    private final PropertyService propertyService;


    @Autowired
    public ProviderService(RestTemplateService restTemplateService, PropertyService propertyService) {
        this.restTemplateService = restTemplateService;
        this.propertyService = propertyService;
    }


    /**
     * 클러스터 관리자 로그인(Post cluster admin login)
     *
     * @param users the users
     * @return the resultStatus
     */
    public ResultStatus loginClusterAdmin(Users users) {
        return restTemplateService.send(TARGET_CP_API, ConstantsUrl.URL_API_LOGIN, HttpMethod.POST, users, ResultStatus.class);
    }


    /**
     * 클러스터 관리자 등록(Post cluster admin sign up)
     *
     * @param users the users
     * @return the resultStatus
     */
    public ResultStatus registerClusterAdmin(Users users) {
        return restTemplateService.send(TARGET_CP_API, ConstantsUrl.URL_API_SIGNUP + propertyService.getKeycloakClusterAdminRole(),
                HttpMethod.POST, users, ResultStatus.class);
    }



    /**
     * Namespace, User id를 통한 사용자 단건 조회(Get Users id namespaces detail)
     *
     * @param cluster   the cluster
     * @param namespace the namespace
     * @param userId    the userId
     * @return the user detail
     */
    public Users getUsers(String cluster, String namespace, String userId) {
         Users users = restTemplateService.send(TARGET_CP_API, URI_API_USERS_DETAIL
                .replace("{cluster:.+}", cluster)
                .replace("{namespace:.+}", namespace)
                .replace("{userId:.+}", userId), HttpMethod.GET, null, Users.class);

         users.setClusterToken(Constants.EMPTY_VALUE);
         users.setSaToken(Constants.EMPTY_VALUE);
         users.setPassword(Constants.EMPTY_VALUE);


        return users;
    }



    /**
     * 사용자 Refresh Token 조회 (Get Refresh Token)
     *
     * @return the user detail
     */
    public void getRefreshToken() {
        restTemplateService.refreshToken(Constants.TARGET_CP_API, HttpMethod.GET, null, ResultStatus.class);
    }


}
