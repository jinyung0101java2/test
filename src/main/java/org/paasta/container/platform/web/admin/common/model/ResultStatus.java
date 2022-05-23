package org.paasta.container.platform.web.admin.common.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Result Status Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.08.28
 **/
@Data
@Builder
public class ResultStatus {
    private String resultCode;
    private String resultMessage;
    private int httpStatusCode;
    private String detailMessage;
    private String nextActionUrl;
    private String userId;
    private String token;
    private List loginMetaData;
    private String clusterName;

    public ResultStatus() {
    }

    public ResultStatus(String resultCode, String resultMessage, int httpStatusCode, String detailMessage) {
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
        this.httpStatusCode = httpStatusCode;
        this.detailMessage = detailMessage;
    }

    public ResultStatus(String resultCode, String resultMessage, int httpStatusCode, String detailMessage, String nextActionUrl) {
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
        this.httpStatusCode = httpStatusCode;
        this.detailMessage = detailMessage;
        this.nextActionUrl = nextActionUrl;
    }
    public ResultStatus(String resultCode, String resultMessage, int httpStatusCode, String detailMessage, String nextActionUrl, String userId, String token, List loginMetaData, String clusterName) {
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
        this.httpStatusCode = httpStatusCode;
        this.detailMessage = detailMessage;
        this.nextActionUrl = nextActionUrl;
        this.userId = userId;
        this.token = token;
        this.loginMetaData = loginMetaData;
        this.clusterName = clusterName;
    }


}
