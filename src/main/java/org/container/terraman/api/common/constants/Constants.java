package org.container.terraman.api.common.constants;

public class Constants {
    public static final String RESULT_STATUS_SUCCESS = "SUCCESS";
    public static final String RESULT_STATUS_FAIL = "FAIL";
    public static final String RESULT_STATUS_TIME_OUT = "timeOut";
    public static final String RESULT_STATUS_TIME_OUT2 = "timed out";
    public static final String RESULT_STATUS_FILE_NOT_FOUND = "No such file or directory";
    public static final String RESULT_STATUS_AUTH_FAIL = "Auth fail";
    public static final String RESULT_STATUS_MASTER_FAIL = "The kube-apiserver is not running!! Check The remote master's status!!";
    public static final String RESULT_STATUS_MASTER_SUCCESS = "The kube-apiserver is running!!";

    public static final String SUPPORTED_RESOURCE_STORAGE = "storage";

    public static final String STRING_DATE_TYPE = "yyyy-MM-dd HH:mm:ss";
    public static final String STRING_ORIGINAL_DATE_TYPE = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String STRING_TIME_ZONE_ID = "Asia/Seoul";

    public static final String NO_NAME = "[-]";
    public static final String NULL_REPLACE_TEXT = "-";
    public static final String EMPTY_STRING ="";
    public static final int EMPTY_INT =0;
    public static final String EMPTY_DIR ="/home/ubuntu";
    public static final String MASTER_SSH_DIR ="/home/ubuntu/.ssh";
    public static final String NCLOUD_HOST_DIR ="/home1/ncloud/";
    public static final String NCLOUD_SSH_DIR ="/home1/ncloud/.ssh";

    public static final String U_LANG_KO = "ko";

    public static final String UPPER_AWS = "AWS";
    public static final String UPPER_GCP = "GCP";
    public static final String UPPER_OPENSTACK = "OPENSTACK";
    public static final String UPPER_NHN = "NHN";
    public static final String UPPER_VSPHERE = "VSPHERE";
    public static final String UPPER_NCLOUD = "NCLOUD";
    public static final String UPPER_NAVER = "NAVER";
    public static final String  RSA_PRIVATE_KEY= "RPK";

    public static final String DIV = "/";

    public Constants() {
        throw new IllegalStateException();
    }


}
