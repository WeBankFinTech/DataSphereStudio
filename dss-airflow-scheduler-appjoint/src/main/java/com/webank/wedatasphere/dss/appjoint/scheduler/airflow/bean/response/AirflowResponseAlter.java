package com.webank.wedatasphere.dss.appjoint.scheduler.airflow.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class AirflowResponseAlter {
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Output {
        private String stderr;
        private String stdin;
        private String stdout;
    }
    private Integer http_response_code;
    private Output output;
    private String status;
    private String call_time;
    private String response_time;
}
