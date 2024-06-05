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
    /**
     * 数据库操作
     */
    DATAPIPE("datapipe"),
    /**
     * ec 引擎实例
     */
    EC_INSTANCE("ec_instance"),

    /**
     * ec 自动释放规则
     */
    EC_KILL_STRATEGY("ec_kill_strategy"),
    /**
     * 参数模板
     */
    EC_CONF_TEMPLATE("ec_conf_template"),
    /**
     * 参数模板应用规则
     */
    EC_CONF_TEMPLATE_APPLY_RULE("ec_conf_template_apply_rule"),

    /**
     * 用户部门
     */
    USER_DEPT("user_dept"),

    /**
     * 工作流默认模板
     */
    DSS_WORKFLOW_DEFAULT_TEMPLATE("dss_workflow_default_template"),

    /**
     * 工作流引用默认模板
     */
     DSS_EC_CONFIG_TEMPLATE_WORKFLOW("dss_ec_config_template_workflow"),
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
