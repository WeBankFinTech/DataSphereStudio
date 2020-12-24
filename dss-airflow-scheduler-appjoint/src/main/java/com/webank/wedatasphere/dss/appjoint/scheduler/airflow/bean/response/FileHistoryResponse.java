package com.webank.wedatasphere.dss.appjoint.scheduler.airflow.bean.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FileHistoryResponse {
    @Data
    @AllArgsConstructor
    public static class FileHistory {
        private String label;
        private Long createTime;
    }
    List<FileHistory> fileHistoryList;
}
