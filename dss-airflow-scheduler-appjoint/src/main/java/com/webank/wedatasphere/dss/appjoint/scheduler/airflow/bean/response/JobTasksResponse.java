package com.webank.wedatasphere.dss.appjoint.scheduler.airflow.bean.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class JobTasksResponse {
    @Data
    @AllArgsConstructor
    public static class Task {
        private String id;
        private String name;
    }
    List<Task> taskList;
}
