package com.webank.wedatasphere.dss.appconn.dolphinscheduler.entity;

import java.util.List;

public class DolphinSchedulerTask {

    private String type;

    private String id;

    private String name;

    private DolphinSchedulerTaskParam params;

    private String description;

    private List<String> preTasks;

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setParams(DolphinSchedulerTaskParam params) {
        this.params = params;
    }

    public DolphinSchedulerTaskParam getParams() {
        return this.params;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public void setPreTasks(List<String> preTasks) {
        this.preTasks = preTasks;
    }

    public List<String> getPreTasks() {
        return this.preTasks;
    }

}
