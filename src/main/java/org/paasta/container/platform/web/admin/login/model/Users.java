package org.paasta.container.platform.web.admin.login.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * User Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.22
 **/
@Data
public class Users {
    private String resultCode;
    private String resultMessage;

    private long id;

    @NotBlank(message = "ID 는 필수 입력 값입니다.")
    @Pattern(regexp="(^[a-z0-9]([-a-z0-9]*[a-z0-9])?(\\.[a-z0-9]([-a-z0-9]*[a-z0-9])?)*)",
            message = "아이디는 영문 소문자 또는 숫자로 시작하는 4~12자 이내의 영문 소문자, 특수문자 '-', 숫자만 혼합하여 사용 가능합니다.")
    private String userId;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Pattern(regexp="(^[a-zA-Z]+(?=.*\\d)(?=.*[-$@$!%*#?&])[a-zA-Z\\d-$@$!%*#?&]{3,39}$)",
            message = "비밀번호는 영문으로 시작하고, 최소 하나 이상의 숫자와 특수 문자를 혼합하여 4~40자 이내로 사용 가능합니다.")
    private String password;

    private String passwordConfirm;

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "이메일 형식에 맞지 않습니다.")
    private String email;

    private String userAuthId;
    private String clusterName;
    private String clusterApiUrl;
    private String clusterToken;
    private String cpNamespace;
    private String cpAccountTokenName;
    private String serviceAccountName;
    private String saSecret;
    private String saToken;
    private String isActive;
    private String roleSetCode;
    private String description;
    private String userType;
    private String created;
    private String lastModified;
    private String browser;
    private String clientIp;


}
