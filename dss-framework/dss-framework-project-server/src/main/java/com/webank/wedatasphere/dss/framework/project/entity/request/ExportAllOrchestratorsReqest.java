package com.webank.wedatasphere.dss.framework.project.entity.request;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Author: xlinliu
 * Date: 2024/4/15
 */
public class ExportAllOrchestratorsReqest {

    /**
     * 项目id
     */
    @NotNull
    private Long projectId;

    /**
     * 环境标签
     */
    @NotNull
    private String labels;


    /**
     * 导出备注
     */
    private String comment;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getLabels() {
        return labels;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
