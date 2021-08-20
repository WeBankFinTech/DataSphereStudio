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

import com.baomidou.mybatisplus.extension.service.IService;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.framework.project.entity.DSSProjectDO;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectCreateRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectDeleteRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectModifyRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectQueryRequest;
import com.webank.wedatasphere.dss.framework.project.entity.response.ProjectResponse;
import com.webank.wedatasphere.dss.framework.project.entity.vo.ProjectInfoVo;
import com.webank.wedatasphere.dss.framework.project.exception.DSSProjectErrorException;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorInfo;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.RequestProjectImportOrchestrator;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;

import java.util.List;
import java.util.Map;

public interface DSSProjectService  extends IService<DSSProjectDO> {


    DSSProjectDO createProject(String username, ProjectCreateRequest projectCreateRequest);


    void modifyProject(String username, ProjectModifyRequest modifyRequest) throws DSSProjectErrorException;


    DSSProjectDO getProjectByName(String name);


    DSSProjectDO getProjectById(Long id);


    List<ProjectResponse> getListByParam(ProjectQueryRequest projectRequest);


    ProjectInfoVo getProjectInfoById(Long id);

    void saveProjectRelation(DSSProjectDO project, Map<AppInstance, Long> projectMap);

    Long getAppConnProjectId(Long dssProjectId, String appConnName, List<DSSLabel> dssLabels) throws Exception;

    void deleteProject(String username, ProjectDeleteRequest projectDeleteRequest, Workspace workspace)  throws Exception;

    List<String> getProjectAbilities(String username);


    Long importOrchestrator(RequestProjectImportOrchestrator orchestratorInfo) throws Exception;
}
