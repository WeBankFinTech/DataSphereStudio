package com.webank.wedatasphere.dss.framework.workspace.bean.request;

/**
 * kill引擎请求
 * Author: xlinliu
 * Date: 2022/11/23
 */
public class KillECInstanceRequest {
    /**
     * 引擎名
     */
    private String engineInstance;

    public String getEngineInstance() {
        return engineInstance;
    }

    public void setEngineInstance(String engineInstance) {
        this.engineInstance = engineInstance;
    }
}
