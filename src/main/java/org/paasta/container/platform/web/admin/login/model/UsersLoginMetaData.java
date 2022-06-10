package org.paasta.container.platform.web.admin.login.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Users Login MetaData Model 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2021.03.15
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsersLoginMetaData implements Serializable {

    private String userId;
    private String accessToken;
    private String userMetaData;
    private String selectedNamespace;
    private String clusterId;
    private List<UsersLoginMetaDataItem> userMetaDataList;
    private String active;

}