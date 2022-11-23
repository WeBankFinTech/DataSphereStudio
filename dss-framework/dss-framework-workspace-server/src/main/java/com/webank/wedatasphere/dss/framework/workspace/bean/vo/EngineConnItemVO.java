package com.webank.wedatasphere.dss.framework.workspace.bean.vo;

import com.webank.wedatasphere.dss.framework.workspace.bean.EngineResource;

/**
 * 工作空间引擎实体VO
 * Author: xlinliu
 * Date: 2022/11/22
 */
public class EngineConnItemVO {
    /**
     * 引擎实例名
     */
    private String instanceName;
    /**
     * 引擎类型
     */
    private String engineType;
    /**
     * 引擎状态
     */
    private String status;
    /**
     * yarn队列名
     */
    private String yarnQueue;
    /**
     * 已使用的本地资源
     */
    private EngineResource usedResource;
    /**
     * yarn队列内存资源，单位Byte
     */
    private Long yarnMemoryResource;
    /**
     * yarn队列cpu资源，单位核
     */
    private Integer yarnCoreResource;
    /**
     * 创建者
     */
    private String createUser;
    /**
     * 启动时间
     */
    private String createTime;
    /**
     * 日志目录
     */
    private String logDirSuffix;

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getEngineType() {
        return engineType;
    }

    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getYarnQueue() {
        return yarnQueue;
    }

    public void setYarnQueue(String yarnQueue) {
        this.yarnQueue = yarnQueue;
    }

    public EngineResource getUsedResource() {
        return usedResource;
    }

    public void setUsedResource(EngineResource usedResource) {
        this.usedResource = usedResource;
    }

    public Long getYarnMemoryResource() {
        return yarnMemoryResource;
    }

    public void setYarnMemoryResource(Long yarnMemoryResource) {
        this.yarnMemoryResource = yarnMemoryResource;
    }

    public Integer getYarnCoreResource() {
        return yarnCoreResource;
    }

    public void setYarnCoreResource(Integer yarnCoreResource) {
        this.yarnCoreResource = yarnCoreResource;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLogDirSuffix() {
        return logDirSuffix;
    }

    public void setLogDirSuffix(String logDirSuffix) {
        this.logDirSuffix = logDirSuffix;
    }
}
