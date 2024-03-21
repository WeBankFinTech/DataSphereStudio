/*
 * Copyright 2019 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.framework.workspace.bean.vo;

import java.io.Serializable;
import java.util.StringJoiner;


public class DSSWorkspaceRoleVO implements Serializable {
    private static final long serialVersionUID=1L;

    private int workspaceId;
    private String workspaceName;
    private int roleId;
    private String roleName;
    private String roleFrontName;

    public int getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(int workspaceId) {
        this.workspaceId = workspaceId;
    }

    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleFrontName() {
        return roleFrontName;
    }

    public void setRoleFrontName(String roleFrontName) {
        this.roleFrontName = roleFrontName;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", DSSWorkspaceRoleVO.class.getSimpleName() + "[", "]")
                .add("workspaceId=" + workspaceId)
                .add("workspaceName='" + workspaceName + "'")
                .add("roleId=" + roleId)
                .add("roleName='" + roleName + "'")
                .add("roleFrontName='" + roleFrontName + "'")
                .toString();
    }
}
