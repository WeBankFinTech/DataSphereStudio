package com.webank.wedatasphere.dss.appjoint.scheduler.airflow.bean.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class JobStateResponse {
    @Data
    @AllArgsConstructor
    public static class JobState {
        private Integer id;
        private String state;
        private String executionDate;
        private String stateDate;
    }
    List<JobState> stateList;
}
