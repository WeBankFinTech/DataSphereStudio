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

package com.webank.wedatasphere.dss.framework.project.service.impl;

import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.framework.appconn.service.AppConnService;
import com.webank.wedatasphere.dss.framework.project.contant.ProjectServerResponse;
import com.webank.wedatasphere.dss.framework.project.entity.DSSProject;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectCreateRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectModifyRequest;
import com.webank.wedatasphere.dss.framework.project.entity.vo.*;
import com.webank.wedatasphere.dss.framework.project.exception.DSSProjectErrorException;
import com.webank.wedatasphere.dss.framework.project.exception.LambdaWarnException;
import com.webank.wedatasphere.dss.framework.project.service.DSSFrameworkProjectService;
import com.webank.wedatasphere.dss.framework.project.service.DSSProjectService;
import com.webank.wedatasphere.dss.framework.project.service.DSSProjectUserService;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.app.structure.StructureIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.structure.project.*;
import com.webank.wedatasphere.dss.standard.common.core.AppStandard;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.linkis.common.conf.CommonVars;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * created by cooperyang on 2020/9/27
 * Description:
 */
@Component
public class DSSFrameworkProjectServiceImpl implements DSSFrameworkProjectService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DSSFrameworkProjectServiceImpl.class);

    @Autowired
    private AppConnService appConnService;
    @Autowired
    private DSSProjectService projectService;
    @Autowired
    private DSSProjectUserService projectUserService;

    private List<ProcessNode> processNodes;

    public static final String MODE_SPLIT = ",";

    private static final boolean STRICT_PROJECT_CREATE_MODE = CommonVars.apply("wds.dss.project.strict.mode", false).getValue();

    @PostConstruct
    private void init() {
        //todo 为了联调 要干掉
        processNodes = Arrays.asList(new DevCenterProcessNode(), new ProdCenterProcessNode());
    }

    @Override
    public DSSProjectDetailVo getProjectSettings(Long projectId) {
        return null;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public DSSProjectVo createProject(ProjectCreateRequest projectCreateRequest, String username, Workspace workspace) throws DSSProjectErrorException {
        //1.新建DSS工程,这样才能进行回滚,如果后面去DSS工程，可能会由于DSS工程建立失败了，但是仍然无法去回滚第三方系统的工程
        //2.开始创建appconn的相关的工程，如果失败了，抛异常，然后进行数据库进行回滚

        //判断工程是否存在相同的名称
        DSSProject dbProject = projectService.getProjectByName(projectCreateRequest.getName());
        if (dbProject != null) {
            DSSExceptionUtils.dealErrorException(60022, String.format("project name already has the same name %s ", projectCreateRequest.getName()), DSSProjectErrorException.class);
        }
        //todo 创建appconn的相关的工程 还没有调试通过
        Map<AppInstance, Long> projectMap = createAppConnProject(projectCreateRequest, username, workspace);
        if (null == projectMap || projectMap.size() == 0) {
            LOGGER.error("projectMap is null, it means some appConns create project failed");
            throw new DSSProjectErrorException(71000, "projectMap is null, create project in appconn failed");
        }
        //3.保存dss_project
        DSSProject project = projectService.createProject(username, projectCreateRequest);
        //4.保存dss_project_user 工程与用户关系
        projectUserService.saveProjectUser(project.getId(), username, projectCreateRequest);
        //5.保存dss工程与其他工程的对应关系,应该都是以id来作为标识
        projectService.saveProjectRelation(project, projectMap);
        DSSProjectVo dssProjectVo = new DSSProjectVo();
        dssProjectVo.setDescription(project.getDescription());
        dssProjectVo.setId(project.getId());
        dssProjectVo.setName(project.getName());
        return dssProjectVo;
    }


    @Override
    public void modifyProject(ProjectModifyRequest projectModifyRequest, String username) throws DSSProjectErrorException {
        DSSProject dbProject = projectService.getProjectById(projectModifyRequest.getId());
        if (dbProject == null) {//工程不存在
            LOGGER.error("{} project id is null, can not modify", projectModifyRequest.getName());
            DSSExceptionUtils.dealErrorException(60021,
                    String.format("%s project id is null, can not modify", projectModifyRequest.getName()), DSSProjectErrorException.class);
        }
        //不允许修改工程名称
        if (!dbProject.getName().toUpperCase().equals(projectModifyRequest.getName().toUpperCase())) {
            DSSExceptionUtils.dealErrorException(ProjectServerResponse.PROJECT_NOT_EDIT_NAME.getCode(), ProjectServerResponse.PROJECT_NOT_EDIT_NAME.getMsg(), DSSProjectErrorException.class);
        }
        //1.统一修改各个接入的第三方的系统的工程状态信息
        // modifyThirdProject(projectModifyRequest, username);
        //2.修改dss_project DSS基本工程信息
        projectService.modifyProject(username, projectModifyRequest);
        try {
            //todo 3.修改dss_project_user 工程与用户关系 这一步还没有调试通过
            projectUserService.modifyProjectUser(dbProject, projectModifyRequest);
        } catch (Exception e) {
            LOGGER.error("modifyProjectUserError:", e);
        }
    }

    //统一修改各个接入的第三方的系统的工程状态信息   修改dss_project调用
    private void modifyThirdProject(ProjectModifyRequest projectModifyRequest, String username) {
        for (AppConn appConn : appConnService.listAppConns()) {
            ProjectRequestRef projectRequestRef = new ProjectRequestRefImpl();
            projectRequestRef.setDescription(projectModifyRequest.getDescription());
            projectRequestRef.setUpdateBy(username);
            projectRequestRef.setName(projectModifyRequest.getName());
            appConn.getAppStandards().stream().
                    filter(appStandard -> appStandard instanceof StructureIntegrationStandard).
                    forEach(appStandard -> {
                        ProjectService projectService = ((StructureIntegrationStandard) appStandard).getProjectService();
                        ProjectUpdateOperation operation = projectService.createProjectUpdateOperation();
                        try {
                            ProjectResponseRef responseRef = operation.updateProject(projectRequestRef);
                        } catch (ExternalOperationFailedException e) {
                            DSSExceptionUtils.dealWarnException(60015,
                                    String.format("failed to update project %s", projectModifyRequest.getName()), e, LambdaWarnException.class);
                        }
                    });
        }
    }

    //1.新建DSS工程,这样才能进行回滚,如果后面去DSS工程，可能会由于DSS工程建立失败了，但是仍然无法去回滚第三方系统的工程  新增dss_project调用
    private Map<AppInstance, Long> createAppConnProject(ProjectCreateRequest dssProjectCreateRequest, String username,
                                                        Workspace workspace) throws DSSProjectErrorException {
        Map<AppInstance, Long> projectMap = new HashMap<>(16);
        Map<AppConn, ProjectResponseRef> successAppConns = new HashMap<>(16);
        boolean createFailed = false;
        ProjectRequestRefImpl requestRef = new ProjectRequestRefImpl();
        requestRef.setName(dssProjectCreateRequest.getName());
        requestRef.setCreateBy(username);
        requestRef.setDescription(dssProjectCreateRequest.getDescription());
        requestRef.setWorkspaceName(dssProjectCreateRequest.getWorkspaceName());
        requestRef.setWorkspace(workspace);
        for (AppConn appConn : appConnService.listAppConns()) {
            for (AppStandard appStandard : appConn.getAppStandards()) {
                if (appStandard instanceof StructureIntegrationStandard) {
                    //如果该AppConn是有structureIntegrationStandard的话,那么所有的appinstance都要进行新建工程
                    for (AppInstance appInstance : appStandard.getAppDesc().getAppInstances()) {
                        ProjectService projectService = ((StructureIntegrationStandard) appStandard).getProjectService();
                        projectService.setAppInstance(appInstance);
                        ProjectCreationOperation projectCreationOperation = projectService.createProjectCreationOperation();
                        try {
                            LOGGER.info("Begin to create project {} in {}", dssProjectCreateRequest.getName(), appConn.getAppDesc().getAppName());
                            ProjectResponseRef responseRef = projectCreationOperation.createProject(requestRef);
                            LOGGER.info("End to create project {} in {}, response projectId is {} ",
                                    dssProjectCreateRequest.getName(), appConn.getAppDesc().getAppName(), responseRef.getProjectRefId());
                            successAppConns.put(appConn, responseRef);
                            projectMap.put(appInstance, responseRef.getProjectRefId());
                        } catch (final Exception e) {
                            LOGGER.error("Failed to create project {} in {}", dssProjectCreateRequest.getName(), appConn.getAppDesc().getAppInstances(), e);
                            createFailed = true;
                            //break;
                        }
                    }
                }
            }
//            if (createFailed) {
//                break;
//            }
        }
        //如果创建失败并且是严格创建模式
        if (createFailed && STRICT_PROJECT_CREATE_MODE) {
            //如果一个AppInstance实例是失败的，那么我们将所有已经建的工程给撤销掉
            for (AppConn appConn : successAppConns.keySet()) {
                appConn.getAppStandards().stream().filter(appStandard -> appStandard instanceof StructureIntegrationStandard).forEach(appStandard -> {
                    ProjectDeletionOperation operation =
                            ((StructureIntegrationStandard) appStandard).getProjectService().createProjectDeletionOperation();
                    try {
                        operation.deleteProject(requestRef);
                    } catch (ExternalOperationFailedException e) {
                        LOGGER.error("Failed to delete project {} in {}",
                                requestRef.getName(), appConn.getAppDesc().getAppName(), e);
                        //TODO 如果删除不了，可以先不管
                    }
                });
            }
            projectMap = null;
        }
        return projectMap;
    }


    @Override
    public List<ProcessNode> getProcessNodes(String username, Long workspaceId, Long projectId) {
        return this.processNodes;
    }
}
