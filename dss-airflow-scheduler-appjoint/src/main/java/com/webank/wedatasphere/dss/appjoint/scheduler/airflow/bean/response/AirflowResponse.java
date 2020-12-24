package com.webank.wedatasphere.dss.appjoint.scheduler.airflow.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class AirflowResponse {
    private Integer http_response_code;
    private String output;
    private String status;
    private String call_time;
    private String response_time;
}
