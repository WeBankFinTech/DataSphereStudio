package com.webank.wedatasphere.dss.appjoint.scheduler.airflow.bean.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FileTreeResponse {
    @Data
    @AllArgsConstructor
    public static class FileTreeItem {
        private String type;
        private String id;
        private String path;
    }
    List<FileTreeItem> fileTree;
}
