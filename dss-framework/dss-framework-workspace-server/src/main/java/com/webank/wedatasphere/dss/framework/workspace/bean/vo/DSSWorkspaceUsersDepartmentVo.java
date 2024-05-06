package com.webank.wedatasphere.dss.framework.workspace.bean.vo;

import java.io.Serializable;
import java.util.List;

public class DSSWorkspaceUsersDepartmentVo implements Serializable {

    private static final long serialVersionUID=1L;
    private List<DepartmentUserVo> editUsers;

    private List<DepartmentUserVo> accessUsers;

    private List<DepartmentUserVo> releaseUsers;

    public void setAccessUsers(List<DepartmentUserVo> accessUsers) {
        this.accessUsers = accessUsers;
    }

    public void setReleaseUsers(List<DepartmentUserVo> releaseUsers) {
        this.releaseUsers = releaseUsers;
    }

    public void setEditUsers(List<DepartmentUserVo> editUsers) {
        this.editUsers = editUsers;
    }
}
