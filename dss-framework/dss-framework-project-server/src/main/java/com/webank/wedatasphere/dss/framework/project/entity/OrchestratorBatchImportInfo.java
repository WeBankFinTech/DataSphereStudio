package com.webank.wedatasphere.dss.framework.project.entity;

import com.webank.wedatasphere.dss.orchestrator.server.entity.vo.OrchestratorBaseInfo;

import java.util.List;

/**
 * 批量导入编排的信息记录
 * Author: xlinliu
 * Date: 2022/9/14
 */
public class OrchestratorBatchImportInfo {
    /**
     * 来源编排信息
     */
    List<OrchestratorBaseInfo> from;
    /**
     * 导入后的编排信息
     */
    List<OrchestratorBaseInfo> to;

    public OrchestratorBatchImportInfo() {
    }

    public OrchestratorBatchImportInfo(List<OrchestratorBaseInfo> from, List<OrchestratorBaseInfo> to) {
        this.from = from;
        this.to = to;
    }

    public List<OrchestratorBaseInfo> getFrom() {
        return from;
    }

    public void setFrom(List<OrchestratorBaseInfo> from) {
        this.from = from;
    }

    public List<OrchestratorBaseInfo> getTo() {
        return to;
    }

    public void setTo(List<OrchestratorBaseInfo> to) {
        this.to = to;
    }
}
