package com.webank.wedatasphere.dss.framework.workspace.bean;

/**
 * 引擎资源实体类
 * Author: xlinliu
 * Date: 2022/11/23
 */
public class EngineResource {
    /**
     * 核数
     */
    private Integer cores;
    /**
     * 实例数
     */
    private Integer instance;
    /**
     * 内存数，单位 byte
     */
    private Long memory;

    public Integer getCores() {
        return cores;
    }

    public void setCores(Integer cores) {
        this.cores = cores;
    }

    public Integer getInstance() {
        return instance;
    }

    public void setInstance(Integer instance) {
        this.instance = instance;
    }

    public Long getMemory() {
        return memory;
    }

    public void setMemory(Long memory) {
        this.memory = memory;
    }
}
