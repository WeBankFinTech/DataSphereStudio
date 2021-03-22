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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.webank.wedatasphere.dss.framework.workspace.bean.DSSUser;
import com.webank.wedatasphere.dss.framework.workspace.bean.LinkisUser;
import com.webank.wedatasphere.dss.framework.workspace.dao.DSSUserMapper;
import com.webank.wedatasphere.dss.framework.workspace.dao.LinkisUserMapper;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSUserService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service
public class DSSUserServiceImpl implements DSSUserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DSSUserServiceImpl.class);
    @Autowired
    private DSSUserMapper dssUserMapper;
    @Autowired
    private LinkisUserMapper linkisUserMapper;

    /**
     * 在用户及角色添加到工作空间 之前，
     * 判断是否存在 username是否存在dss_user
     * 不存在保存 ： 将username以及默认信息分别保存到dss_user、linkis_user两张表中
     * @param userName
     */
    @Override
    public void saveWorkspaceUser(String userName) {
        if (getUserID(userName)==null) {
            LOGGER.info("User first enter dss, insert table dss_user");
            DSSUser dssUser = new DSSUser();
            dssUser.setUsername(userName);
            dssUser.setName(userName);
            dssUser.setFirstLogin(true);
            //INSERT INTO dss_user(<include refid = "dss_user" />)
            //        VALUES (#{id},#{username},#{name},#{isFirstLogin})
            dssUserMapper.insert(dssUser);

            /*  INSERT INTO linkis_user(`username`,`name`,`create_time`,`update_time`,`is_first_login`)
            VALUES (#{username},#{name},now(),now(),1)*/
            LinkisUser linkisUser = new LinkisUser();
            linkisUser.setUsername(userName);
            linkisUser.setName(userName);
            linkisUser.setCreateTime(new Date());
            linkisUser.setUpdateTime(new Date());
            linkisUser.setFirstLogin(true);
            linkisUserMapper.insert(linkisUser);
        }
    }

    //根据用户名获取userId
    @Override
    public Long getUserID(String userName) {
        DSSUser user = getByUsername(userName);
        return user!=null?user.getId():null;
    }

    //根据用户名获取userId
    @Override
    public DSSUser getByUsername(String userName) {
        QueryWrapper<DSSUser> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", userName);
        List<DSSUser> dssUserList = dssUserMapper.selectList(userQueryWrapper);
        if (!CollectionUtils.isEmpty(dssUserList)) {
            return dssUserList.get(0);
        }
        return null;
    }

    @Override
    public boolean isAdminUser(String username) {
        DSSUser user = getByUsername(username);
        return user!=null?user.getAdmin().booleanValue():false;
    }
}
