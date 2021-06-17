package com.webank.wedatasphere.dss.framework.workspace.bean;

import java.util.ArrayList;
import java.util.List;

public class DSSWorkspaceUser01 {
    private String id;
    private String name ;
    private String joinTime;
    private String creator;
    private String department;
    private String office;
    private List<Integer> roles;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(String joinTime) {
        this.joinTime = joinTime;
    }

    public String getCreatedBy() {
        return creator;
    }

    public void setCreatedBy(String creator) {
        this.creator = creator;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public List<Integer> getRoles() {
        return roles;
    }

    public void setRoles(List<Integer> roles) {
        this.roles = roles;
    }


}
