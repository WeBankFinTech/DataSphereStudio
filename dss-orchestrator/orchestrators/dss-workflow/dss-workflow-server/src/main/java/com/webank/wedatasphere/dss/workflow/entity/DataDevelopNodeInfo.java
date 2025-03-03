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
}
