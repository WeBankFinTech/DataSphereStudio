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

import com.webank.wedatasphere.dss.framework.workspace.bean.DSSMenu;
import com.webank.wedatasphere.dss.framework.workspace.dao.DSSWorkspaceMenuMapper;
import com.webank.wedatasphere.dss.framework.workspace.dao.DSSWorkspaceUserMapper;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * created by cooperyang on 2020/3/17
 * Description:
 */
@Service
public class DSSWorkspaceMenuServiceImpl implements DSSWorkspaceMenuService {


    @Autowired
    private DSSWorkspaceMenuMapper dssWorkspaceMenuMapper;

    @Autowired
    private DSSWorkspaceUserMapper dssWorkspaceUserMapper;

    @Override
    public List<DSSMenu> getRealComponents(List<DSSMenu> subMenus, String workspaceId, String username) {
        Set<DSSMenu> dssMenuSet = new HashSet<>(subMenus);
        subMenus.forEach(subMenu -> {
            if (subMenu.isComponent()){
                int applicationId = subMenu.getApplicationId();
                List<Integer> roles = dssWorkspaceUserMapper.getRoleInWorkspace(Integer.parseInt(workspaceId),username);
                int priv = 0;
                for (Integer role : roles) {
                    Integer rolePriv = dssWorkspaceMenuMapper.getOneCompoentRolePriv(Integer.parseInt(workspaceId), role, applicationId);
                    if(rolePriv != null){
                        priv += rolePriv;
                    }
                }
                if (priv == 0){
                    dssMenuSet.remove(subMenu);
                }
            }
        });
        return new ArrayList<>(dssMenuSet);
    }
}
