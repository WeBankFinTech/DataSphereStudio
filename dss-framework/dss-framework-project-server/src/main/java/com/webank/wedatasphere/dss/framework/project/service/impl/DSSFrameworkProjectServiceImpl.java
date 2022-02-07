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

package com.webank.wedatasphere.dss.framework.project.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.webank.wedatasphere.dss.standard.app.structure.project.*;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.appconn.core.ext.OnlyStructureAppConn;
import com.webank.wedatasphere.dss.appconn.manager.AppConnManager;
import com.webank.wedatasphere.dss.common.entity.project.DSSProject;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.framework.project.contant.ProjectServerResponse;
import com.webank.wedatasphere.dss.framework.project.contant.ProjectUserPrivEnum;
import com.webank.wedatasphere.dss.framework.project.dao.DSSProjectMapper;
import com.webank.wedatasphere.dss.framework.project.entity.DSSProjectDO;
import com.webank.wedatasphere.dss.framework.project.entity.DSSProjectUser;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectCreateRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectDeleteRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectModifyRequest;
import com.webank.wedatasphere.dss.framework.project.entity.vo.DSSProjectDetailVo;
import com.webank.wedatasphere.dss.framework.project.entity.vo.DSSProjectVo;
import com.webank.wedatasphere.dss.framework.project.entity.vo.ProjectInfoVo;
import com.webank.wedatasphere.dss.framework.project.exception.DSSProjectErrorException;
import com.webank.wedatasphere.dss.framework.project.exception.LambdaWarnException;
import com.webank.wedatasphere.dss.framework.project.service.DSSFrameworkProjectService;
import com.webank.wedatasphere.dss.framework.project.service.DSSProjectNoCreateSwitchService;
import com.webank.wedatasphere.dss.framework.project.service.DSSProjectService;
import com.webank.wedatasphere.dss.framework.project.service.DSSProjectUserService;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.app.structure.StructureIntegrationStandard;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.entity.ref.RefFactory;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.apache.linkis.common.conf.CommonVars;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DSSFrameworkProjectServiceImpl implements DSSFrameworkProjectService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DSSFrameworkProjectServiceImpl.class);
    public static final int MaxProjectNameSize = 64;
    public static final int MaxPrjectDescSize = 2048;
    @Autowired
    private DSSProjectService dssProjectService;
    @Autowired
    private DSSProjectUserService projectUserService;
    @Autowired
    private DSSProjectNoCreateSwitchService projectNameCheckSwitchService;


    public static final String MODE_SPLIT = ",";

    private static final boolean STRICT_PROJECT_CREATE_MODE = CommonVars.apply("wds.dss.project.strict.mode", false).getValue();

    @Override
    public DSSProjectDetailVo getProjectSettings(Long projectId) {
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DSSProjectVo createProject(ProjectCreateRequest projectCreateRequest, String username, Workspace workspace,boolean checkProjectName) throws Exception {
        //1.新建DSS工程,这样才能进行回滚,如果后面去DSS工程，可能会由于DSS工程建立失败了，但是仍然无法去回滚第三方系统的工程
        //2.开始创建appconn的相关的工程，如果失败了，抛异常，然后进行数据库进行回滚
        boolean isWorkspaceUser = projectUserService.isWorkspaceUser(projectCreateRequest.getWorkspaceId(), username);
        //非管理员
        if (!isWorkspaceUser) {
            DSSExceptionUtils.dealErrorException(ProjectServerResponse.PROJECT_USER_NOT_IN_WORKSPACE.getCode(), ProjectServerResponse.PROJECT_USER_NOT_IN_WORKSPACE.getMsg(), DSSProjectErrorException.class);
        }
        //增加名称长度限制
        if(projectCreateRequest.getName().length()> MaxProjectNameSize ||projectCreateRequest.getDescription().length()> MaxPrjectDescSize){
            DSSExceptionUtils.dealErrorException(60021,"project name or desc is too long for size is "+projectCreateRequest.getName().length()+",desc:"+projectCreateRequest.getDescription().length(),DSSProjectErrorException.class);
        }

        //判断工程是否存在相同的名称
        DSSProjectDO dbProject = dssProjectService.getProjectByName(projectCreateRequest.getName());
        if (dbProject != null) {
            DSSExceptionUtils.dealErrorException(60022, String.format("project name already has the same name %s ", projectCreateRequest.getName()), DSSProjectErrorException.class);
        }


        List<String> appConnNameList = new ArrayList<>(1);
        //判断已有组件是否已经存在相同的工程名称
        if(checkProjectName) {
            if (isExistSameProjectName(projectCreateRequest, username, workspace, appConnNameList)) {
                throw new DSSProjectErrorException(71000, "" + appConnNameList.get(0) + "已存在相同项目名称，请重新命名");
            }
        }else {
            //针对迁移的操作，wtss已经存在该同名工程，如果强行检查会导致迁移报错。
            LOGGER.info("不检查工程名称是否在第三方节点已经存在");
        }

        //todo 创建appconn的相关的工程 还没有调试通过
        Map<AppInstance, Long> projectMap = createAppConnProject(projectCreateRequest, username, workspace);
        if (null == projectMap) {
            LOGGER.error("projectMap is null, it means some appConns create project failed");
            throw new DSSProjectErrorException(71000, "projectMap is null, create project in appconn failed");
        }
        //3.保存dss_project
        DSSProjectDO project = dssProjectService.createProject(username, projectCreateRequest);
        //4.保存dss_project_user 工程与用户关系
        projectUserService.saveProjectUser(project.getId(), username, projectCreateRequest, workspace);
        //5.保存dss工程与其他工程的对应关系,应该都是以id来作为标识
        if (null != projectMap && projectMap.size() > 0) {
            dssProjectService.saveProjectRelation(project, projectMap);
        }
        DSSProjectVo dssProjectVo = new DSSProjectVo();
        dssProjectVo.setDescription(project.getDescription());
        dssProjectVo.setId(project.getId());
        dssProjectVo.setName(project.getName());
        return dssProjectVo;
    }


    @Override
    public void modifyProject(ProjectModifyRequest projectModifyRequest, String username, Workspace workspace) throws Exception {
        DSSProjectDO dbProject = dssProjectService.getProjectById(projectModifyRequest.getId());
        //如果不是工程的创建人，则校验是否管理员
        if (!username.equalsIgnoreCase(dbProject.getCreateBy())) {
            boolean isAdmin = projectUserService.isAdminByUsername(projectModifyRequest.getWorkspaceId(), username);
            //非管理员
            if (!isAdmin) {
                DSSExceptionUtils.dealErrorException(ProjectServerResponse.PROJECT_IS_NOT_ADMIN.getCode(), ProjectServerResponse.PROJECT_IS_NOT_ADMIN.getMsg(), DSSProjectErrorException.class);
            }
        }
        //工程不存在
        if (dbProject == null) {
            LOGGER.error("{} project id is null, can not modify", projectModifyRequest.getName());
            DSSExceptionUtils.dealErrorException(60021,
                    String.format("%s project id is null, can not modify", projectModifyRequest.getName()), DSSProjectErrorException.class);
        }
        //不允许修改工程名称
        if (!dbProject.getName().equalsIgnoreCase(projectModifyRequest.getName())) {
            DSSExceptionUtils.dealErrorException(ProjectServerResponse.PROJECT_NOT_EDIT_NAME.getCode(), ProjectServerResponse.PROJECT_NOT_EDIT_NAME.getMsg(), DSSProjectErrorException.class);
        }

        //1.统一修改各个接入的第三方的系统的工程状态信息
        //2.修改dss_project_user 工程与用户关系
        projectUserService.modifyProjectUser(dbProject, projectModifyRequest, username, workspace);
        //调用第三方的工程修改接口
        modifyThirdProject(projectModifyRequest, username,dbProject);
        //3.修改dss_project DSS基本工程信息
        dssProjectService.modifyProject(username, projectModifyRequest);
    }

    //统一修改各个接入的第三方的系统的工程状态信息   修改dss_project调用
    private void modifyThirdProject(ProjectModifyRequest projectModifyRequest, String username,DSSProjectDO dbProject){
        for (AppConn appConn : AppConnManager.getAppConnManager().listAppConns()) {
            ProjectRequestRef projectRequestRef = new ProjectRequestRefImpl();
            projectRequestRef.setDescription(projectModifyRequest.getDescription());
            projectRequestRef.setUpdateBy(username);
            projectRequestRef.setCreateBy(dbProject.getCreateBy());
            projectRequestRef.setName(projectModifyRequest.getName());
            projectRequestRef.setAccessUsers(projectModifyRequest.getAccessUsers());
            projectRequestRef.setEditUsers(projectModifyRequest.getEditUsers());
            projectRequestRef.setReleaseUsers(projectModifyRequest.getReleaseUsers());
            if (appConn instanceof OnlyStructureAppConn) {
                StructureIntegrationStandard appStandard = ((OnlyStructureAppConn) appConn).getOrCreateStructureStandard();
                for (AppInstance appInstance : appConn.getAppDesc().getAppInstances()) {
                    try {
                        ProjectService projectService = appStandard.getProjectService(appInstance);
                        if (projectService == null) {
                            continue;
                        }
                        ProjectUpdateOperation operation = projectService.getProjectUpdateOperation();
                        if (operation == null) {
                            continue;
                        }
                        if (!isNeadToCreateProject(appInstance.getId())) {
                            continue;
                        }
                        Long thirdProjectId = dssProjectService.getAppConnProjectId(appInstance.getId(), dbProject.getId());
                        if(thirdProjectId == null){
                            LOGGER.error("modifyThirdProject, appName: {}, projectId is null ",appConn.getAppDesc().getAppName());
                        }
                        projectRequestRef.setId(thirdProjectId);
                        ProjectResponseRef responseRef = operation.updateProject(projectRequestRef);
                        if(responseRef != null){
                            LOGGER.info("appName:{},modifyThirdProject responseBody is ",responseRef.getResponseBody());
                        }
                    } catch (Exception e) {
                        LOGGER.error("modify Third Project error ",e);
/*                        DSSExceptionUtils.dealWarnException(60015,
                                String.format("failed to update project %s", projectModifyRequest.getName()), e, LambdaWarnException.class);*/
                    }
                }
            }
        }
    }

    private boolean isExistSameProjectName(ProjectCreateRequest dssProjectCreateRequest,
                                           String username,
                                           Workspace workspace,
                                           List<String> appConnNameList) throws ExternalOperationFailedException {
        LOGGER.info("begin to check project name...");
        ProjectRequestRefImpl requestRef;
        try {
            requestRef = RefFactory.INSTANCE.newRef(ProjectRequestRefImpl.class);
        } catch (DSSErrorException e) {
            LOGGER.error("get ProjectRequestRefImpl failed when check project name", e);
            throw new ExternalOperationFailedException(71000, "check project name error", e);
        }
        requestRef.setName(dssProjectCreateRequest.getName());
        requestRef.setCreateBy(username);
        requestRef.setDescription(dssProjectCreateRequest.getDescription());
        requestRef.setWorkspaceName(dssProjectCreateRequest.getWorkspaceName());
        requestRef.setWorkspace(workspace);

        for (AppConn appConn : AppConnManager.getAppConnManager().listAppConns()) {
            if (appConn instanceof OnlyStructureAppConn) {
                StructureIntegrationStandard appStandard = ((OnlyStructureAppConn) appConn).getOrCreateStructureStandard();
                for (AppInstance appInstance : appConn.getAppDesc().getAppInstances()) {
                    ProjectService projectService = appStandard.getProjectService(appInstance);
                    if (projectService == null) {
                        continue;
                    }
                    ProjectGetOperation projectGetOperation = projectService.getProjectGetOperation();
                    if (projectGetOperation == null) {
                        continue;
                    }
                    if (!isNeadToCreateProject(appInstance.getId())) {
                        continue;
                    }
                    try {
                        LOGGER.info("begin to check project name in {}...", appConn.getAppDesc().getAppName());
                        //获取各组件工程信息
                        DSSProject project = projectGetOperation.getProject(requestRef);
                        if (project != null && project.getId() > 0) {
                            appConnNameList.add(appConn.getAppDesc().getAppName());
                            return true;
                        }
                    } catch (ExternalOperationFailedException e) {
                        LOGGER.error("Failed to check project {} name in {}", dssProjectCreateRequest.getName(), appConn.getAppDesc().getAppName(), e);
                        throw new ExternalOperationFailedException(71000, "Failed to check project: " + dssProjectCreateRequest.getName() + " in " + appConn.getAppDesc().getAppName() + ", Instance id:"+appInstance.getId(), e);
                    }
                }
            }
        }
        return false;
    }

    // 1.新建DSS工程,这样才能进行回滚,如果后面去DSS工程，可能会由于DSS工程建立失败了，但是仍然无法去回滚第三方系统的工程 新增dss_project调用
    private Map<AppInstance, Long> createAppConnProject(ProjectCreateRequest dssProjectCreateRequest, String username,
        Workspace workspace) {
        Map<AppInstance, Long> projectMap = new HashMap<>(16);
        Map<AppConn, ProjectResponseRef> successAppConns = new HashMap<>(16);
        boolean createFailed = false;

        ProjectRequestRefImpl requestRef = new ProjectRequestRefImpl();
        requestRef.setName(dssProjectCreateRequest.getName());
        requestRef.setCreateBy(username);
        requestRef.setDescription(dssProjectCreateRequest.getDescription());
        requestRef.setWorkspaceName(dssProjectCreateRequest.getWorkspaceName());
        requestRef.setWorkspace(workspace);
        requestRef.setAccessUsers(dssProjectCreateRequest.getAccessUsers());
        requestRef.setEditUsers(dssProjectCreateRequest.getEditUsers());
        requestRef.setReleaseUsers(dssProjectCreateRequest.getReleaseUsers());
        for (AppConn appConn : AppConnManager.getAppConnManager().listAppConns()) {
            if (appConn instanceof OnlyStructureAppConn) {
                OnlyStructureAppConn onlyStructureAppConn = (OnlyStructureAppConn) appConn;
                StructureIntegrationStandard appStandard = onlyStructureAppConn.getOrCreateStructureStandard();
                //如果该AppConn是有structureIntegrationStandard的话,那么所有的appinstance都要进行新建工程
                for (AppInstance appInstance : appConn.getAppDesc().getAppInstances()) {
                    ProjectService projectService = appStandard.getProjectService(appInstance);
                    if (projectService == null) {
                        continue;
                    }
                    if (!isNeadToCreateProject(appInstance.getId())) {
                        continue;
                    }
                    ProjectCreationOperation projectCreationOperation = projectService.getProjectCreationOperation();
                    try {
                        LOGGER.info("Begin to create project {} in {}", dssProjectCreateRequest.getName(), appConn.getAppDesc().getAppName());
                        ProjectResponseRef responseRef = projectCreationOperation.createProject(requestRef);
                        LOGGER.info("End to create project {} in {}, response projectId is {} ",
                                dssProjectCreateRequest.getName(), appConn.getAppDesc().getAppName(), responseRef.getProjectRefId());
                        successAppConns.put(appConn, responseRef);
                        Long projectRefId = responseRef.getProjectRefId();
                        projectMap.put(appInstance, projectRefId);
                    } catch (final Exception e) {
                        LOGGER.error("Failed to create project {} in {}", dssProjectCreateRequest.getName(), appConn.getAppDesc().getAppInstances(), e);
                        createFailed = true;
                    }
                }
            }
        }
        // 如果创建失败并且是严格创建模式
        if (createFailed && STRICT_PROJECT_CREATE_MODE) {
            // 如果一个AppInstance实例是失败的，那么我们将所有已经建的工程给撤销掉
            for (AppConn appConn : successAppConns.keySet()) {
                StructureIntegrationStandard appStandard = null;
                if (appConn instanceof OnlyStructureAppConn) {
                    OnlyStructureAppConn onlyStructureAppConn = (OnlyStructureAppConn)appConn;
                    appStandard = onlyStructureAppConn.getOrCreateStructureStandard();
                    for (AppInstance appInstance : appConn.getAppDesc().getAppInstances()) {
                        ProjectService appStandardProjectService = appStandard.getProjectService(appInstance);
                        ProjectDeletionOperation operation = appStandardProjectService.getProjectDeletionOperation();
                        try {
                            operation.deleteProject(requestRef);
                        } catch (ExternalOperationFailedException e) {
                            LOGGER.error("Failed to delete project {} in {}", requestRef.getName(),
                                appConn.getAppDesc().getAppName(), e);
                            // TODO 如果删除不了，可以先不管
                        }
                    }
                }
            }
            projectMap = null;
        }
        return projectMap;
    }

    /**
     * 是否需要创建工程
     * appconn instance 是否配置在dss_project_no_create_switch这个表里面
     * 如果配置上，则不需要检查工程名称或创建工程
     */
    private boolean isNeadToCreateProject(Long appconnInstanceId){
        Long nocheckAppconnCount = projectNameCheckSwitchService.getCountByAppconnInstanceId(appconnInstanceId);
        if (nocheckAppconnCount.longValue() != 0) {
            return false;
        }
        return true;
    }
}
