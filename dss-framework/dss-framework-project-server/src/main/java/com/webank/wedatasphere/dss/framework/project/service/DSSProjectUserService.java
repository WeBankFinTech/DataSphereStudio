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

package com.webank.wedatasphere.dss.framework.project.service;

import com.webank.wedatasphere.dss.framework.project.entity.DSSProject;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectCreateRequest;
import com.webank.wedatasphere.dss.framework.project.entity.DSSProjectUser;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectModifyRequest;
import com.webank.wedatasphere.dss.framework.project.exception.DSSProjectErrorException;

import java.util.List;

public interface DSSProjectUserService {

    /**
     * 是否有修改工程权限
     * @param projectId
     * @param username
     * @return
     */
    public boolean isEditProjectAuth(Long projectId,String username) throws DSSProjectErrorException;

    /**
     * 根据用户名和工程id获取工程权限
     * @param projectId
     * @param username
     * @return
     */
    public List<DSSProjectUser> getEditProjectList(Long projectId,String username);

    /**
     * 保存工程与用户关系
     * @param projectID
     * @param dssProjectCreateRequest
     */
    public void saveProjectUser(Long projectID, String username, ProjectCreateRequest dssProjectCreateRequest);

    /**
     * 修改工程与用户关系
     * @param dbProject
     * @param projectModifyRequest
     */
    public void modifyProjectUser(DSSProject dbProject, ProjectModifyRequest projectModifyRequest);

    //根据空间id和用户名获取工程与用户关系
    public List<DSSProjectUser> getListByParam(Long workspaceId, String username);
}
