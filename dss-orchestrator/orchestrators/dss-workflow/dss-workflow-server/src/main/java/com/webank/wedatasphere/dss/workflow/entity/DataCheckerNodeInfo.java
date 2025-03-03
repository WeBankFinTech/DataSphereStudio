package com.webank.wedatasphere.dss.workflow.entity;

import java.util.Date;
import java.util.Map;

public class DataCheckerNodeInfo extends  NodeBaseInfo {



    // 节点描述
    private String nodeDesc;

    // source.type
    private String sourceType;

    // check.object
    private String checkObject;

    // max.check.hours
    private String maxCheckHours;

    //job.desc
    private String jobDesc;

    // qualities 校验
    private Boolean qualitisCheck;

    public String getNodeDesc() {
        return nodeDesc;
    }

    public void setNodeDesc(String nodeDesc) {
        this.nodeDesc = nodeDesc;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getCheckObject() {
        return checkObject;
    }

    public void setCheckObject(String checkObject) {
        this.checkObject = checkObject;
    }

    public String getMaxCheckHours() {
        return maxCheckHours;
    }

    public void setMaxCheckHours(String maxCheckHours) {
        this.maxCheckHours = maxCheckHours;
    }

    public String getJobDesc() {
        return jobDesc;
    }

    public void setJobDesc(String jobDesc) {
        this.jobDesc = jobDesc;
    }

    public Boolean getQualitisCheck() {
        return qualitisCheck;
    }

    public void setQualitisCheck(Boolean qualitisCheck) {
        this.qualitisCheck = qualitisCheck;
    }
}
