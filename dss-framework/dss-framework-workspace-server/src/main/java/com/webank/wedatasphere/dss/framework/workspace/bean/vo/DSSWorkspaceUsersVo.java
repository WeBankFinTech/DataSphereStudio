package com.webank.wedatasphere.dss.framework.workspace.bean.vo;

import java.util.List;

public class DSSWorkspaceUsersVo {


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
