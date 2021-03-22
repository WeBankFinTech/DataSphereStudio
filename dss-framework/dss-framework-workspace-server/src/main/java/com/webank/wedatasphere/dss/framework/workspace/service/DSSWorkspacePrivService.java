/*
 *
 *  * Copyright 2019 WeBank
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.framework.workspace.service;

import org.apache.commons.math3.util.Pair;

import java.util.List;

/**
 * created by cooperyang on 2020/3/18
 * Description:
 */
public interface DSSWorkspacePrivService {
    void updateRoleMenuPriv(int workspaceId, int menuId, String updater, List<Pair<Integer, Boolean>> pairs);

    void updateRoleComponentPriv(int workspaceId, int menuId, String username, List<Pair<Integer, Boolean>> pairs);

    Integer getRoleId(int workspaceId, String key);
}
