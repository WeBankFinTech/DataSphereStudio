package com.webank.wedatasphere.dss.common.auditlog;
/**
 * 审计日志操作目标类型枚举
 * Author: xlinliu
 * Date: 2022/8/10
 */
public enum TargetTypeEnum {
    /**
     * 项目
     */
    PROJECT("project"),
    /**
     * 工作空间
     */
    WORKSPACE("workspace"),
    /**
     * API服务
     */
    APISERVICE("apiservice"),
    /**
     * 工作流
     */
    WORKFLOW("workflow"),
    /**
     * 编排
     */
    ORCHESTRATOR("orchestrator"),
    /**
     * context id
     */
    CONTEXTID("contextid"),
    /**
     * 工作空间角色
     */
    WORKSPACE_ROLE("workspace_role"),
    ;
    private String name;

    TargetTypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
