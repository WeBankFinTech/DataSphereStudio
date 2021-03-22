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

import com.webank.wedatasphere.dss.framework.workspace.bean.DSSUser;

public interface DSSUserService {
    /**
     * 在用户及角色添加到工作空间 之前，
     * 判断是否存在 username是否存在dss_user
     * 不存在保存 ： 将username保存到dss_user、linkis_user两张表中
     * @param userName
     */
    public void saveWorkspaceUser(String userName);

    /**
     * 根据用户名获取userId
     * @param userName
     * @return
     */
    public Long getUserID(String userName);

    /**
     * 根据用户名获取dss_user的用户信息
     * @param userName
     * @return
     */
    public DSSUser getByUsername(String userName);

    public boolean isAdminUser(String username);
}
