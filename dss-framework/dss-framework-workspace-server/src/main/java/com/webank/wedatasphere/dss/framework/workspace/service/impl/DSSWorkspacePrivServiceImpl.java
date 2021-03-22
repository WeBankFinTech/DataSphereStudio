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

package com.webank.wedatasphere.dss.framework.workspace.service.impl;

import com.webank.wedatasphere.dss.framework.workspace.dao.DSSWorkspacePrivMapper;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspacePrivService;
import org.apache.commons.math3.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * created by cooperyang on 2020/3/18
 * Description:
 */
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
            dssWorkspacePrivMapper.updateRoleMenuPriv(workspaceId, menuId, roleId, priv);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRoleComponentPriv(int workspaceId, int componentId, String username, List<Pair<Integer, Boolean>> pairs) {
        pairs.forEach(pair -> {
            int roleId = pair.getKey();
            int priv = pair.getValue() ? 1 : 0;
            int count = dssWorkspacePrivMapper.queryCntOfRCP(workspaceId, componentId, roleId);
            if (count >= 1) {
                dssWorkspacePrivMapper.updateRoleComponentPriv(workspaceId, componentId, roleId, priv);
            }else {
                dssWorkspacePrivMapper.insertRolComponentPriv(workspaceId, componentId, roleId, priv);
            }
        });
    }

    @Override
    public Integer getRoleId(int workspaceId, String key) {
        return dssWorkspacePrivMapper.getRoleId(workspaceId, key);
    }
}
