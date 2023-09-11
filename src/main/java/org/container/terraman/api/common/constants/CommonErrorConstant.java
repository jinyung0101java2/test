package org.container.terraman.api.common.constants;

public enum CommonErrorConstant {

    FILE_COPY_ERROR("1", "파일 복사가 실패하였습니다."),
    FILE_CREATE_ERROR("2", "파일 생성이 실패하였습니다."),
    TERRAFORM_INIT_ERROR("3", "terraform init이 실패하였습니다."),
    TERRAFORM_PLAN_ERROR("4", "terraform plan이 실패하였습니다."),
    TERRAFORM_APPLY_ERROR("5", "terraform apply가 실패하였습니다."),
    INSTANCE_INFO_ERROR("6", "instance 정보를 가져오는데 실패하였습니다."),
    FILE_WRITE_ERROR("7", "파일 작성이 실패하였습니다."),
    FILE_DELETE_ERROR("8", "파일 삭제가 실패하였습니다."),
    COMMAND_MOD_CHANGE_ERROR("9", "커맨드 모드변경이 실패하였습니다."),
    COMMAND_DEPLOY_ERROR("10", "커맨드 deploy가 실패하였습니다.");

    private String errorCode;
    private String errorMessage;

    CommonErrorConstant(String errorCode, String errorMessage) {
        this. errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

}
