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

package com.webank.wedatasphere.dss.framework.workspace.service.impl;

import com.webank.wedatasphere.dss.framework.workspace.dao.DSSWorkspacePrivMapper;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspacePrivService;
import org.apache.commons.math3.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class DSSWorkspacePrivServiceImpl implements DSSWorkspacePrivService {


    @Autowired
    private DSSWorkspacePrivMapper dssWorkspacePrivMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRoleMenuPriv(int workspaceId, int menuId, String updater, List<Pair<Integer, Boolean>> pairs) {
        pairs.forEach(pair -> {
            int roleId = pair.getKey();
            int priv = pair.getValue() ? 1 : 0;
            int count = dssWorkspacePrivMapper.queryCntOfMenuRolePriv(workspaceId, menuId, roleId);
            if (count >= 1) {
                dssWorkspacePrivMapper.updateRoleMenuPriv(workspaceId, menuId, roleId, priv);
            } else {
                dssWorkspacePrivMapper.insertMenuRolePriv(workspaceId, menuId, roleId, priv, updater);
            }
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRoleComponentPriv(int workspaceId, int appconnId, String username, List<Pair<Integer, Boolean>> pairs) {
        pairs.forEach(pair -> {
            int roleId = pair.getKey();
            int priv = pair.getValue() ? 1 : 0;
            int count = dssWorkspacePrivMapper.queryCntOfRCP(workspaceId, appconnId, roleId);
            if (count >= 1) {
                dssWorkspacePrivMapper.updateRoleComponentPriv(workspaceId, appconnId, roleId, priv);
            } else {
                dssWorkspacePrivMapper.insertRolComponentPriv(workspaceId, appconnId, roleId, priv, username);
            }
        });
    }

    @Override
    public Integer getRoleId(int workspaceId, String key) {
        return dssWorkspacePrivMapper.getRoleId(workspaceId, key);
    }
}
