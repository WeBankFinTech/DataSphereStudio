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

package com.webank.wedatasphere.dss.framework.release.service.impl;

import com.webank.wedatasphere.dss.framework.release.dao.ProjectMapper;
import com.webank.wedatasphere.dss.framework.release.entity.project.ProjectInfo;
import com.webank.wedatasphere.dss.framework.release.entity.project.ProjectOrcEntity;
import com.webank.wedatasphere.dss.framework.release.entity.resource.BmlResource;
import com.webank.wedatasphere.dss.framework.release.service.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * created by cooperyang on 2020/12/11
 * Description:
 */
@Service
public class ProjectServiceImpl implements ProjectService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectServiceImpl.class);

    @Autowired
    private ProjectMapper projectMapper;


    @Override
    public ProjectInfo getProjectInfoByOrcVersionId(Long orchestratorVersionId) {
        return null;
    }

    /**
     * 封装工程信息,最后以json的形式进行返回
     * @return 封装之后的json
     */
    @Override
    public String encapsulate(Long projectId, String releaseUser, List<BmlResource> resourceList, Long orchestratorId, Long orchestratorVersionId) {
        ProjectInfo projectInfo = this.getProjectInfoById(projectId);
        ProjectOrcEntity projectOrcEntity = new ProjectOrcEntity();
        ProjectOrcEntity.InnerProject innerProject = ProjectOrcEntity.InnerProject.newInstance(projectId,
                projectInfo.getProjectName(),
                projectInfo.getCreateBy(),
                releaseUser,
                projectInfo.getDescription(),
                projectInfo.getCreateTime());
        projectOrcEntity.setProject(innerProject);
        List<ProjectOrcEntity.InnerOrchestrator> orchestratorList = resourceList.stream().
                 map(resource -> ProjectOrcEntity.InnerOrchestrator.newInstance(resource.getResourceId(),resource.getVersion())).
                 collect(Collectors.toList());
        projectOrcEntity.setOrchestrators(orchestratorList);
        return projectOrcEntity.toString();
    }


    @Override
    public ProjectInfo getProjectInfoByOrchestratorId(Long orchestratorId) {
        Long projectId = projectMapper.getProjectIdByOrcId(orchestratorId);
        return this.getProjectInfoById(projectId);
    }

    @Override
    public ProjectInfo getProjectInfoById(Long projectId) {
        return projectMapper.getProjectInfoById(projectId);
    }


    @Override
    public String getOrchestratorName(Long orchestratorId, Long orchestratorVersionId) {
        return projectMapper.getOrchestratorName(orchestratorId, orchestratorVersionId);
    }

    @Override
    public String getWorkspaceName(Long projectId) {
        return projectMapper.getWorkspaceName(projectId);
    }

    @Override
    public void updateProjectOrcInfo(Long projectId, Long orchestratorId, Long orchestratorVersionId) {
        projectMapper.updateProjectOrcInfo(projectId, orchestratorId, orchestratorVersionId);
    }

    @Override
    public Long getAppIdByOrchestratorVersionId(Long orchestratorVersionId) {
        return projectMapper.getAppIdByOrchestratorVersionId(orchestratorVersionId);
    }
}

