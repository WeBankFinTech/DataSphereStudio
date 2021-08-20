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

import com.webank.wedatasphere.dss.framework.project.entity.DSSProjectDO;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectCreateRequest;
import com.webank.wedatasphere.dss.framework.project.entity.DSSProjectUser;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectModifyRequest;
import com.webank.wedatasphere.dss.framework.project.exception.DSSProjectErrorException;

import java.util.List;

public interface DSSProjectUserService {


    boolean isEditProjectAuth(Long projectId,String username) throws DSSProjectErrorException;


    List<DSSProjectUser> getEditProjectList(Long projectId, String username);


    void saveProjectUser(Long projectID, String username, ProjectCreateRequest dssProjectCreateRequest)throws Exception;


    void modifyProjectUser(DSSProjectDO dbProject, ProjectModifyRequest projectModifyRequest, String loginuser)throws Exception;


    List<DSSProjectUser> getListByParam(Long workspaceId, String username);


    boolean isAdminByUsername(Long workspaceId,String username);
}
