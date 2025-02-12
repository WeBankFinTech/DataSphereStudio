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

package com.webank.wedatasphere.dss.framework.project.service;

import java.util.List;

import com.webank.wedatasphere.dss.common.constant.project.ProjectUserPrivEnum;
import com.webank.wedatasphere.dss.framework.project.entity.DSSProjectDO;
import com.webank.wedatasphere.dss.framework.project.entity.DSSProjectUser;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectCreateRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectModifyRequest;
import com.webank.wedatasphere.dss.framework.project.exception.DSSProjectErrorException;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;

public interface DSSProjectUserService {

    /**
     * 是否有修改工程权限
     *
     * @param projectId
     *            the project id
     * @param username
     *            the username
     * @return boolean
     * @throws DSSProjectErrorException
     *             the dss project error exception
     */
    boolean isEditProjectAuth(Long projectId,String username) throws DSSProjectErrorException;

    /**
     * 保存工程与用户关系
     *
     * @param projectID
     *            the project id
     * @param username
     *            the username
     * @param dssProjectCreateRequest
     *            the dss project create request
     * @throws Exception
     *             the exception
     */
    void saveProjectUser(Long projectID, String username, ProjectCreateRequest dssProjectCreateRequest, Workspace workspace)throws Exception;

    /**
     * 修改工程与用户关系
     *
     * @param dbProject
     *            the db project
     * @param projectModifyRequest
     *            the project modify request
     * @throws Exception
     *             the exception
     */
    void modifyProjectUser(DSSProjectDO dbProject, ProjectModifyRequest projectModifyRequest);


    List<DSSProjectUser> getListByParam(Long workspaceId, String username);


    boolean isAdminByUsername(Long workspaceId,String username);

    List<DSSProjectUser> getProjectUserPriv(Long projectId, String username);

    List<DSSProjectUser> getProjectPriv(Long projectId);

    boolean isWorkspaceUser(Long workspaceId,String username);

    /**
     * 获取某个项目下指定权限的用户名合.
     *
     * @param projectId
     *            the project id
     * @param privEnum
     *            the priv enum
     * @return the list
     */
    public List<DSSProjectUser> listByPriv(Long projectId, ProjectUserPrivEnum privEnum);
}
