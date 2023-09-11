package org.container.terraman.api.common.model;

import lombok.Data;

/**
 * Result Status model 클래스
 *
 * @author yjh
 * @version 1.0
 * @since 2022.07.11
 **/
@Data
public class ResultStatusModel {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private Object out;
}
