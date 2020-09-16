/*
 * Copyright (C) 2006-2016 AiJia All rights reserved
 * Author: chongchuanbing
 * Date: 2020/7/14
 * Description:ApiVersionVo.java
 */
package com.webank.wedatasphere.linkis.oneservice.core.vo;

/**
 * @author chongchuanbing
 */
public class ApiVersionVo {
    
    private Long id;
    
    private String version;
    
    private Integer status;
    
    private String statusStr;
    
    private String scriptPath;
    
    private String path;
    
    private String creator;
    
    private String publishDateStr;
    
    private String updateDateStr;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusStr() {
        return statusStr;
    }

    public void setStatusStr(String statusStr) {
        this.statusStr = statusStr;
    }

    public String getScriptPath() {
        return scriptPath;
    }

    public void setScriptPath(String scriptPath) {
        this.scriptPath = scriptPath;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getPublishDateStr() {
        return publishDateStr;
    }

    public void setPublishDateStr(String publishDateStr) {
        this.publishDateStr = publishDateStr;
    }

    public String getUpdateDateStr() {
        return updateDateStr;
    }

    public void setUpdateDateStr(String updateDateStr) {
        this.updateDateStr = updateDateStr;
    }
}
