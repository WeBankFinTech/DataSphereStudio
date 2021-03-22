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

import com.webank.wedatasphere.dss.framework.workspace.bean.vo.StaffInfoVO;
import com.webank.wedatasphere.dss.framework.workspace.dao.DSSWorkspaceUserMapper;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceUserService;
import com.webank.wedatasphere.linkis.common.conf.CommonVars;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * created by cooperyang on 2020/3/17
 * Description:
 */
@Service
public class DSSWorkspaceUserServiceImpl implements DSSWorkspaceUserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DSSWorkspaceUserServiceImpl.class);
    @Autowired
    private DSSWorkspaceUserMapper dssWorkspaceUserMapper;


    final CommonVars<String> staff = CommonVars.apply("wds.dss.workspace.staffs", "tom,alex,bob");


    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateWorkspaceUser(List<Integer> roles, int workspaceId, String userName, String creator) {
        dssWorkspaceUserMapper.removeAllRolesForUser(userName, workspaceId);
        roles.forEach(role ->{
            dssWorkspaceUserMapper.setUserRoleInWorkspace(workspaceId, role, userName, creator);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteWorkspaceUser(String userName, int workspaceId) {
        dssWorkspaceUserMapper.removeAllRolesForUser(userName, workspaceId);
        dssWorkspaceUserMapper.removeUserInWorkspace(userName, workspaceId);
    }

    @Override
    public List<StaffInfoVO> listAllDSSUsers() {

        String[] staffs = staff.getValue().split(",");
        List<StaffInfoVO> staffInfoVOs = new ArrayList<>();
        int count = 1;
        for (String s : staffs){
            StaffInfoVO staffInfoVO = new StaffInfoVO();
            staffInfoVO.setUsername(s);
            staffInfoVO.setDepartment("wds");
            staffInfoVO.setOffice("dss");
            staffInfoVO.setId(Integer.toString(count));
            count += 1;
            staffInfoVOs.add(staffInfoVO);
        }
        return staffInfoVOs;
    }



    @Override
    public List<String> getAllWorkspaceUsers(int workspaceId) {
        return dssWorkspaceUserMapper.getAllWorkspaceUsers(workspaceId);
    }

    @Override
    public    List<Integer>   getUserWorkspaceIds(String userName){
        List<Integer> tempWorkspaceIds = dssWorkspaceUserMapper.getWorkspaceIds(userName);
        return  tempWorkspaceIds;
    }
}
