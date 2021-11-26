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

package com.webank.wedatasphere.dss.standard.app.structure.plugin.role.impl;

import com.webank.wedatasphere.dss.standard.app.structure.plugin.role.DSSUserRoles;

import java.util.List;


public class DSSUserRolesImpl implements DSSUserRoles {

    private List<String> roles;
    private String workspaceName;
    private String user;

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public List<String> getRoles() {
        return roles;
    }

    @Override
    public String getWorkspaceName() {
        return workspaceName;
    }

    @Override
    public String getUser() {
        return user;
    }
}
