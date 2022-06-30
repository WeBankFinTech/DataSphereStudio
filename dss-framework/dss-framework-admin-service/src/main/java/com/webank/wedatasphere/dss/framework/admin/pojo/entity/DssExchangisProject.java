package com.webank.wedatasphere.dss.framework.admin.pojo.entity;

import java.util.List;

/**
 * @Auther: Han Tang
 * @Date: 2022/1/18-01-18-14:05
 */
public class DssExchangisProject {
    private int id;
    private String projectName;
    private int parentId;
    private String createUser;
    private String createTime;
    private String modifyUser;
    private String modifyTime;
    private int level;
    private List<DssExchangisProject> children;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
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

    public String getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public List<DssExchangisProject> getChildren() {
        return children;
    }

    public void setChildren(List<DssExchangisProject> children) {
        this.children = children;
    }


}
