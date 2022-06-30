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
import java.util.List;


public class DSSWorkspacePrivVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private int workspaceId;
    private List<DSSWorkspaceMenuPrivVO> menuPrivVOS;
    private List<DSSWorkspaceComponentPrivVO> componentPrivVOS;
    private List<DSSWorkspaceRoleVO> roleVOS;

    public List<DSSWorkspaceRoleVO> getRoleVOS() {
        return roleVOS;
    }

    public void setRoleVOS(List<DSSWorkspaceRoleVO> roleVOS) {
        this.roleVOS = roleVOS;
    }


    public DSSWorkspacePrivVO() {
    }

    public DSSWorkspacePrivVO(int workspaceId, List<DSSWorkspaceMenuPrivVO> menuPrivVOS, List<DSSWorkspaceComponentPrivVO> componentPrivVOS, List<DSSWorkspaceRoleVO> roleVOS) {
        this.workspaceId = workspaceId;
        this.menuPrivVOS = menuPrivVOS;
        this.componentPrivVOS = componentPrivVOS;
        this.roleVOS = roleVOS;
    }

    public int getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(int workspaceId) {
        this.workspaceId = workspaceId;
    }

    public List<DSSWorkspaceMenuPrivVO> getMenuPrivVOS() {
        return menuPrivVOS;
    }

    public void setMenuPrivVOS(List<DSSWorkspaceMenuPrivVO> menuPrivVOS) {
        this.menuPrivVOS = menuPrivVOS;
    }

    public List<DSSWorkspaceComponentPrivVO> getComponentPrivVOS() {
        return componentPrivVOS;
    }

    public void setComponentPrivVOS(List<DSSWorkspaceComponentPrivVO> componentPrivVOS) {
        this.componentPrivVOS = componentPrivVOS;
    }

    @Override
    public String toString() {
        return "DSSWorkspacePrivVO{" +
                "workspaceId=" + workspaceId +
                ", menuPrivVOS=" + menuPrivVOS +
                ", componentPrivVOS=" + componentPrivVOS +
                ", roleVOS=" + roleVOS +
                '}';
    }


}
