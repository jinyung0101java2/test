package org.paasta.container.platform.web.admin.login.model;

import lombok.Data;

/**
 * Users Login MetaData Item Model 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2021.03.15
 **/
@Data
public class UsersLoginMetaDataItem {
    private String namespace;
    private String userType;
}
