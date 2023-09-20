package org.container.platform.web.ui.login.model;

import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * User Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.22
 **/
@Data
@NoArgsConstructor
public class Users {
    public String resultCode;
    public String resultMessage;
    public Integer httpStatusCode;
    public String detailMessage;

    public long id;
    public String clusterId;
    public String userId;
    public String userAuthId;
    public String password;
    public String passwordConfirm;
    public String email;
    public String clusterName;
    public String clusterProviderType;
    public String clusterApiUrl;
    public String clusterToken;
    public String cpNamespace;
    public String cpAccountTokenName;
    public String serviceAccountName;
    public String saSecret;
    public String saToken;
    public String isActive;
    public String roleSetCode;
    public String description;
    public String userType;
    public String created;
    public String lastModified;
    private String browser;
    private String clientIp;
    private Boolean isSuperAdmin;


    private String cpProviderType;
    private String serviceInstanceId;


    ///secret info
    private String secretName;
    private String secretUid;
    private String secretCreationTimestamp;


    private String oldUserType;

    public Users(String userId, String userAuthId, Boolean isSuperAdmin){
        this.userId = userId;
        this.userAuthId = userAuthId;
        this.isSuperAdmin = isSuperAdmin;
    }

}