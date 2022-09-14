package com.webank.wedatasphere.dss.framework.workspace.client.entity;



import java.io.Serializable;
import java.util.Date;
import java.util.List;


public class DSSWorkspaceUser implements Serializable {
    private int id;
    private String name;
    private List<Integer> roles;
    private String department;
    private String office;
    private String creator;
    private Date joinTime;

    public DSSWorkspaceUser() {
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getRoles() {
        return roles;
    }

    public void setRoles(List<Integer> roles) {
        this.roles = roles;
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

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(Date joinTime) {
        this.joinTime = joinTime;
    }
}
