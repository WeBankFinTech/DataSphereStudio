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

import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectCreateRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectDeleteRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectModifyRequest;
import com.webank.wedatasphere.dss.framework.project.entity.vo.DSSProjectDetailVo;
import com.webank.wedatasphere.dss.framework.project.entity.vo.DSSProjectVo;
import com.webank.wedatasphere.dss.framework.project.entity.vo.ProcessNode;
import com.webank.wedatasphere.dss.framework.project.exception.DSSProjectErrorException;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;

import java.util.List;

/**
 * created by cooperyang on 2020/9/22
 * Description:
 */
public interface DSSFrameworkProjectService {

    DSSProjectDetailVo getProjectSettings(Long projectId);

    /**
     * 1. 首先要去所有满足工程结构的规范的去建工程，首先必须要满足建工程的appconn进行进工程
     * 2. 自己创建工程
     * 3. 如果第三方系统创建失败，最多重试一次
     * 4. 如果本身失败了，则进行回滚
     * @param projectCreateRequest
     * @return
     */
    DSSProjectVo createProject(ProjectCreateRequest projectCreateRequest, String username, Workspace workspace,boolean checkProjectName) throws Exception;

    void modifyProject(ProjectModifyRequest projectModifyRequest, String username) throws Exception;

    void deleteProject(String username, ProjectDeleteRequest projectDeleteRequest, Workspace workspace)
        throws Exception;
}
