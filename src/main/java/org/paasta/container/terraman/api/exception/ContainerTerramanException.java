package org.paasta.container.terraman.api.exception;

public class ContainerTerramanException extends BaseBizException {
    private static final long serialVersionUID = -1288712633779609678L;

    public ContainerTerramanException(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    public ContainerTerramanException(String errorCode, String errorMessage, int statusCode, String detailMessage) {
        super(errorCode, errorMessage, statusCode, detailMessage);
    }

    public ContainerTerramanException(String errorMessage) {
        super(errorMessage);
    }

}
