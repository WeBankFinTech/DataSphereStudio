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

package com.webank.wedatasphere.dss.standard.app.structure.plugin.role;

import com.webank.wedatasphere.dss.standard.app.structure.plugin.role.impl.OriginRolePlugin;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

public interface RolePlugin {

    DSSUserRoles getRoles(HttpServletRequest request);

    List<String> getUsers(HttpServletRequest request, String role);

    static RolePlugin getRolePlugin() {
        return OriginRolePlugin.getRolePlugin();
    }
}
