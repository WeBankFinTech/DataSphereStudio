package com.webank.wedatasphere.dss.appconn.dolphinscheduler.ref;

import com.webank.wedatasphere.dss.appconn.schedulis.conf.SchedulisConf;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.entity.ref.AbstractResponseRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class DolphinSchedulerProjectResponseRef extends AbstractResponseRef implements ProjectResponseRef {

    private static final Logger logger = LoggerFactory.getLogger(DolphinSchedulerProjectResponseRef.class);

    private String errorMsg;

    private Long projectRefId;

    public DolphinSchedulerProjectResponseRef() {
        super("", 0);
    }

    public DolphinSchedulerProjectResponseRef(String responseBody) {
        super(responseBody, 0);
    }

    public DolphinSchedulerProjectResponseRef(String responseBody, int status, String errorMsg) {
        super(responseBody, status);
        this.errorMsg = errorMsg;
    }

    @Override
    public Map<String, Object> toMap() {
        try {
            return SchedulisConf.gson().fromJson(this.getResponseBody(), Map.class);
        } catch (Exception e) {
            logger.error("failed to covert {} to a map", this.getResponseBody(), e);
            return new HashMap<String, Object>();
        }
    }

    @Override
    public String getErrorMsg() {
        return this.errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    @Override
    public Long getProjectRefId() {
        return this.projectRefId;
    }

    @Override
    public Map<AppInstance, Long> getProjectRefIds() {
        return null;
    }

    public void setProjectRefId(Long projectRefId) {
        this.projectRefId = projectRefId;
    }

}
