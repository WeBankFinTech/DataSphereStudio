package com.webank.wedatasphere.dss.framework.workspace.bean.request;

import java.util.List;

/**
 * linkis引擎列表请求参数
 * Author: xlinliu
 * Date: 2022/11/22
 */
public class ECInstanceRequest {
    /**
     * 引擎状态
     */
    private List<String> status;
    /**
     * 引擎创建者
     */
    private List<String> createUser;
    /**
     * 引擎类型
     */
    private List<String> engineType;
    /**
     * 队列名
     */
    private String yarnQueue;

    public List<String> getStatus() {
        return status;
    }

    public void setStatus(List<String> status) {
        this.status = status;
    }

    public List<String> getCreateUser() {
        return createUser;
    }

    public void setCreateUser(List<String> createUser) {
        this.createUser = createUser;
    }

    public List<String> getEngineType() {
        return engineType;
    }

    public void setEngineType(List<String> engineType) {
        this.engineType = engineType;
    }

    public String getYarnQueue() {
        return yarnQueue;
    }

    public void setYarnQueue(String yarnQueue) {
        this.yarnQueue = yarnQueue;
    }
}
