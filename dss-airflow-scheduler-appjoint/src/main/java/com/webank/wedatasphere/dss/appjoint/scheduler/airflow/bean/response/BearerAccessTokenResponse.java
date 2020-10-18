package com.webank.wedatasphere.dss.appjoint.scheduler.airflow.bean.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BearerAccessTokenResponse {

    @JsonProperty("access_token")
    private String accessToken;
}
