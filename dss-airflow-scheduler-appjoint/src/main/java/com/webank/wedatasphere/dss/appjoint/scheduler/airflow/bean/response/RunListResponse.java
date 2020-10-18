package com.webank.wedatasphere.dss.appjoint.scheduler.airflow.bean.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RunListResponse {
    private Long totalElements;
    private Integer totalPages;
    @Data
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class RunInfo {
        private Integer id;
        private String state;
        private String executionDate;
        private String stateDate;
    }
    List<RunInfo> runList;
}

