package com.webank.wedatasphere.dss.framework.workspace.bean.vo;

import java.io.Serializable;
import java.util.List;

public class DepartmentUserVo implements Serializable {
    private static final long serialVersionUID=1L;
    private String name;
    private String department;
    private String office;

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
