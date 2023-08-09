package com.webank.wedatasphere.dss.orchestrator.publish.entity;

import com.webank.wedatasphere.dss.common.entity.BmlResource;

/**
 * 工作流导出结果
 * Author: xlinliu
 * Date: 2022/8/22
 */
public class OrchestratorExportResult {
    /**
     * 导出工作流的bml文件
     */
    private BmlResource bmlResource;
    /**
     * 工作流的版本
     */
    private String orcVersionId;

    public OrchestratorExportResult() {
    }

    public OrchestratorExportResult(BmlResource bmlResource, String orcVersionId) {
        this.bmlResource = bmlResource;
        this.orcVersionId = orcVersionId;
    }

    public BmlResource getBmlResource() {
        return bmlResource;
    }

    public void setBmlResource(BmlResource bmlResource) {
        this.bmlResource = bmlResource;
    }

    public String getOrcVersionId() {
        return orcVersionId;
    }

    public void setOrcVersionId(String orcVersionId) {
        this.orcVersionId = orcVersionId;
    }
}
