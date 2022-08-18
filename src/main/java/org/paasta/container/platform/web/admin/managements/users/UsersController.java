package org.paasta.container.platform.web.admin.managements.users;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.paasta.container.platform.web.admin.common.Constants;
import org.paasta.container.platform.web.admin.common.ConstantsUrl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Users Controller 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2021.05.06
 **/
@Api(value = "UsersController v1")
@Controller
public class UsersController {


    private static final String BASE_URL = "users/";


    /**
     * Admin 목록 페이지 이동(Go to the admin list page)
     *
     * @return the view
     */
    @ApiOperation(value = "Admin 목록 페이지 이동(Go to the admin list page)", nickname = "getAdminList")
    @GetMapping(value = ConstantsUrl.URI_CP_MANAGEMENTS_USERS_ADMIN )
    public String getAdminList() {
        return BASE_URL + "admin";
    }

/*

    */
/**
     * Admin 상세 페이지 이동(Go to the admin details page)
     *
     * @return the view
     *//*

    @ApiOperation(value = "Admin 상세 페이지 이동(Go to the admin details page)", nickname = "getAdminDetails")
    @GetMapping(value = ConstantsUrl.URI_CP_MANAGEMENTS_USERS_ADMIN + ConstantsUrl.URI_CP_DETAILS)
    public String getAdminDetails() {
        return BASE_URL + "adminDetail";
    }
*/



    /**
     * Users 목록 페이지 이동(Go to the users list page)
     *
     * @return the view
     */
    @ApiOperation(value = "Users 목록 페이지 이동(Go to the users list page)", nickname = "getUsersList")
    @GetMapping(value = ConstantsUrl.URI_CP_MANAGEMENTS_USERS )
    public String getUsersList() {
        return BASE_URL + "users";
    }



    /**
     * 비활성화 Users 목록 페이지 이동(Go to the inactive users list page)
     *
     * @return the view
     */
    @ApiOperation(value = "비활성화 Users 목록 페이지 이동(Go to the inactive users list page)", nickname = "getInactiveUsersList")
    @GetMapping(value = ConstantsUrl.URI_CP_MANAGEMENTS_INACTIVE_USERS )
    public String getInactiveUsersList() {
        return BASE_URL + "inactiveUsers";
    }


    /**
     * Users 상세 페이지 이동(Go to the users details page)
     *
     * @return the view
     */
    @ApiOperation(value = "Users 상세 페이지 이동(Go to the users details page)", nickname = "getUsersDetails")
    @GetMapping(value = ConstantsUrl.URI_CP_MANAGEMENTS_USERS + ConstantsUrl.URI_CP_DETAILS)
    public String getUsersDetails() {
        return BASE_URL + "usersDetail";
    }


    /**
     * Users 수정 페이지 이동(Go to the users update page)
     *
     * @return the view
     */
    @ApiOperation(value = "Users 수정 페이지 이동(Go to the users update page)", nickname = "updateUsers")
    @GetMapping(value = ConstantsUrl.URI_CP_MANAGEMENTS_USERS + ConstantsUrl.URI_CP_UPDATE)
    public String updateUsers() { return BASE_URL + "usersUpdate"; }


}
