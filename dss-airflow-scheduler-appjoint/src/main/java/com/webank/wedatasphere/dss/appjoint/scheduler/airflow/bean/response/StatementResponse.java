package com.webank.wedatasphere.dss.appjoint.scheduler.airflow.bean.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class StatementResponse {
    private Integer id;
    private String state;
    private Output output;
    @Data
    static class Output {
        private String status;
        private List<String> traceback;
        private OutputData data;
    }
    @Data
    static class OutputData {
        @JsonProperty("text/plain")
        private String text;
    }
}
