package com.webank.wedatasphere.dss.framework.workspace.bean.vo;

import java.util.List;

public class DSSWorkspaceUsersVo {


    private List<String> editUsers;

    private List<String> accessUsers;

    private List<String> releaseUsers;

    private List<String> createUsers;

    public void setAccessUsers(List<String> accessUsers) {
        this.accessUsers = accessUsers;
    }

    public void setReleaseUsers(List<String> releaseUsers) {
        this.releaseUsers = releaseUsers;
    }

    public void setEditUsers(List<String> editUsers) {
        this.editUsers = editUsers;
    }

    public List<String> getEditUsers() {
        return editUsers;
    }

    public List<String> getAccessUsers() {
        return accessUsers;
    }

    public List<String> getReleaseUsers() {
        return releaseUsers;
    }


    public List<String> getCreateUsers() {
        return createUsers;
    }

    public void setCreateUsers(List<String> createUsers) {
        this.createUsers = createUsers;
    }
}
