package com.webank.wedatasphere.dss.workflow.entity;

import java.util.Date;
import java.util.Map;

public class DataDevelopNodeInfo  extends NodeBaseInfo{

    // 是否引用参数模板
    private Boolean isRefTemplate;

    // 模板名称
    private String templateName;

    // 模板ID

    private String templateId;

    // 资源名称
    private String resource;

    // 是否复用引擎
    private Boolean reuseEngine;

    private String script;

    // spark.executor.memory
    private String  sparkExecutorMemory;

    // spark.driver.memory
    private String  sparkDriverMemory;

    // spark.conf
    private String  sparkConf;

    // spark.executor.core
    private String  sparkExecutorCore;

    // spark.executor.instances
    private String sparkExecutorInstances;


    private String executeCluster;

    public String getExecuteCluster() {
        return executeCluster;
    }

    public void setExecuteCluster(String executeCluster) {
        this.executeCluster = executeCluster;
    }
    public Boolean getRefTemplate() {
        return isRefTemplate;
    }

    public void setRefTemplate(Boolean refTemplate) {
        isRefTemplate = refTemplate;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public Boolean getReuseEngine() {
        return reuseEngine;
    }

    public void setReuseEngine(Boolean reuseEngine) {
        this.reuseEngine = reuseEngine;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }


    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }


    public String getSparkExecutorMemory() {
        return sparkExecutorMemory;
    }

    public void setSparkExecutorMemory(String sparkExecutorMemory) {
        this.sparkExecutorMemory = sparkExecutorMemory;
    }

    public String getSparkDriverMemory() {
        return sparkDriverMemory;
    }

    public void setSparkDriverMemory(String sparkDriverMemory) {
        this.sparkDriverMemory = sparkDriverMemory;
    }

    public String getSparkConf() {
        return sparkConf;
    }

    public void setSparkConf(String sparkConf) {
        this.sparkConf = sparkConf;
    }

    public String getSparkExecutorCore() {
        return sparkExecutorCore;
    }

    public void setSparkExecutorCore(String sparkExecutorCore) {
        this.sparkExecutorCore = sparkExecutorCore;
    }

    public String getSparkExecutorInstances() {
        return sparkExecutorInstances;
    }

    public void setSparkExecutorInstances(String sparkExecutorInstances) {
        this.sparkExecutorInstances = sparkExecutorInstances;
    }
}
