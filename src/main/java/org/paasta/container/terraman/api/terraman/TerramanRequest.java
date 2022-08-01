package org.paasta.container.terraman.api.terraman;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;

@Data
public class TerramanRequest {
    private String clusterId;
    private String seq;
    private String provider;
}
