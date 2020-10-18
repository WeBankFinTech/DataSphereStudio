package com.webank.wedatasphere.dss.appjoint.scheduler.airflow.bean.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DagInfoResponse {
    @Data
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class DagInfo {
        private String name;
        private Boolean isActive;
    }
    List<DagInfo> dagList;
}

