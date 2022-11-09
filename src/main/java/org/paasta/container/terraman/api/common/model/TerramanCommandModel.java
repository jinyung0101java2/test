package org.paasta.container.terraman.api.common.model;

import lombok.Data;
import org.paasta.container.terraman.api.common.constants.Constants;
import org.paasta.container.terraman.api.common.constants.TerramanConstant;

@Data
public class TerramanCommandModel {
    private String clusterId = Constants.EMPTY_STRING;
    private String command = Constants.EMPTY_STRING;
    private String dir = Constants.EMPTY_STRING;
    private String host = Constants.EMPTY_STRING;
    private String idRsa = Constants.EMPTY_STRING;
    private String userName = Constants.EMPTY_STRING;
    private String pod = Constants.EMPTY_STRING;
    private String secrets = Constants.EMPTY_STRING;
    private String contents = Constants.EMPTY_STRING;
}