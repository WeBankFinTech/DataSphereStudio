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
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectModifyRequest;
import com.webank.wedatasphere.dss.framework.project.entity.vo.DSSProjectDetailVo;
import com.webank.wedatasphere.dss.framework.project.entity.vo.DSSProjectVo;
import com.webank.wedatasphere.dss.framework.project.entity.vo.ProcessNode;
import com.webank.wedatasphere.dss.framework.project.exception.DSSProjectErrorException;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;

import java.util.List;


public interface DSSFrameworkProjectService {

    DSSProjectDetailVo getProjectSettings(Long projectId);


    DSSProjectVo createProject(ProjectCreateRequest projectCreateRequest, String username, Workspace workspace) throws Exception;

    void modifyProject(ProjectModifyRequest projectModifyRequest, String username) throws Exception;

}
