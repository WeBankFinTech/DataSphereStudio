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

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.appconn.core.ext.OnlyStructureAppConn;
import com.webank.wedatasphere.dss.common.entity.BmlResource;
import com.webank.wedatasphere.dss.common.entity.project.DSSProject;
import com.webank.wedatasphere.dss.common.utils.*;
import com.webank.wedatasphere.dss.framework.project.conf.ProjectConf;
import com.webank.wedatasphere.dss.framework.project.contant.ProjectServerResponse;
import com.webank.wedatasphere.dss.framework.project.entity.DSSProjectDO;
import com.webank.wedatasphere.dss.framework.project.entity.DSSProjectUser;
import com.webank.wedatasphere.dss.framework.project.entity.request.ExportAllOrchestratorsReqest;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectCreateRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectModifyRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectTransferRequest;
import com.webank.wedatasphere.dss.framework.project.entity.vo.DSSProjectDetailVo;
import com.webank.wedatasphere.dss.framework.project.entity.vo.DSSProjectVo;
import com.webank.wedatasphere.dss.framework.project.exception.DSSProjectErrorException;
import com.webank.wedatasphere.dss.framework.project.service.DSSFrameworkProjectService;
import com.webank.wedatasphere.dss.framework.project.service.DSSProjectService;
import com.webank.wedatasphere.dss.framework.project.service.DSSProjectUserService;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.StaffInfoVO;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceUserService;
import com.webank.wedatasphere.dss.git.common.protocol.constant.GitConstant;
import com.webank.wedatasphere.dss.git.common.protocol.request.*;
import com.webank.wedatasphere.dss.git.common.protocol.response.*;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorInfo;
import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorRefConstant;
import com.webank.wedatasphere.dss.orchestrator.db.dao.OrchestratorMapper;
import com.webank.wedatasphere.dss.orchestrator.server.entity.request.OrchestratorRequest;
import com.webank.wedatasphere.dss.orchestrator.server.entity.vo.OrchestratorBaseInfo;
import com.webank.wedatasphere.dss.orchestrator.server.service.OrchestratorService;
import com.webank.wedatasphere.dss.sender.service.DSSSenderServiceFactory;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.app.structure.project.*;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.*;
import com.webank.wedatasphere.dss.standard.app.structure.utils.StructureOperationUtils;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.dss.framework.project.dao.DSSProjectMapper;
import com.webank.wedatasphere.dss.workflow.dao.LockMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.linkis.common.conf.CommonVars;
import org.apache.linkis.common.exception.WarnException;
import org.apache.linkis.protocol.util.ImmutablePair;
import org.apache.linkis.rpc.Sender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.webank.wedatasphere.dss.framework.project.utils.ProjectOperationUtils.tryProjectOperation;

public class DSSFrameworkProjectServiceImpl implements DSSFrameworkProjectService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DSSFrameworkProjectServiceImpl.class);
    public static final int MAX_PROJECT_NAME_SIZE = 64;
    public static final int MAX_PROJECT_DESC_SIZE = ProjectConf.MAX_DESC_LENGTH.getValue();
    private static final int MAX_PROJECT_BUSSINESS_SIZE = 200;
    @Autowired
    private DSSProjectService dssProjectService;
    @Autowired
    private DSSProjectUserService projectUserService;
    @Autowired
    private DSSProjectMapper projectMapper;
    @Autowired
    private DSSWorkspaceUserService dssWorkspaceUserService;
    @Autowired
    private OrchestratorService orchestratorService;
    @Autowired
    private LockMapper lockMapper;


    private static final boolean STRICT_PROJECT_CREATE_MODE = CommonVars.apply("wds.dss.project.strict.mode", false).getValue();

    @Override
    public DSSProjectDetailVo getProjectSettings(Long projectId) {
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DSSProjectVo createProject(ProjectCreateRequest projectCreateRequest, String username, Workspace workspace) throws Exception {
        //1.新建DSS工程,这样才能进行回滚,如果后面去DSS工程，可能会由于DSS工程建立失败了，但是仍然无法去回滚第三方系统的工程
        //2.开始创建appconn的相关的工程，如果失败了，抛异常，然后进行数据库进行回滚
        boolean isWorkspaceUser = projectUserService.isWorkspaceUser(projectCreateRequest.getWorkspaceId(), username);
        //非管理员
        if (!isWorkspaceUser) {
            DSSExceptionUtils.dealErrorException(ProjectServerResponse.PROJECT_USER_NOT_IN_WORKSPACE.getCode(), ProjectServerResponse.PROJECT_USER_NOT_IN_WORKSPACE.getMsg(), DSSProjectErrorException.class);
        }
        //增加名称长度限制
        if(projectCreateRequest.getName().length()> MAX_PROJECT_NAME_SIZE){
            DSSExceptionUtils.dealErrorException(60021,"project name is too long. the length must be less than " + MAX_PROJECT_NAME_SIZE, DSSProjectErrorException.class);
        } else if(projectCreateRequest.getDescription().length() > MAX_PROJECT_DESC_SIZE) {
            DSSExceptionUtils.dealErrorException(60021,"project description is too long. the length must be less than " + MAX_PROJECT_DESC_SIZE, DSSProjectErrorException.class);
        } else if(StringUtils.isNotEmpty(projectCreateRequest.getBusiness()) && projectCreateRequest.getBusiness().length() > MAX_PROJECT_BUSSINESS_SIZE){
            DSSExceptionUtils.dealErrorException(60021,"project bussiness is too long. the length must be less than " + MAX_PROJECT_BUSSINESS_SIZE, DSSProjectErrorException.class);
        }

        // 对于接入git的项目校验项目名称
        if(projectCreateRequest.getAssociateGit() != null && projectCreateRequest.getAssociateGit()) {
            // 校验gitUser gitToken合法性以及projectName是否重复
            checkGitName(projectCreateRequest.getName(), workspace, username, projectCreateRequest.getGitUser(), projectCreateRequest.getGitToken());
        }

        //3.保存dss_project
        DSSProjectDO project = dssProjectService.createProject(username, projectCreateRequest);
        // 同步项目信息至git
        if(projectCreateRequest.getAssociateGit() != null && projectCreateRequest.getAssociateGit()) {
            ExportAllOrchestratorsReqest exportAllOrchestratorsReqest = new ExportAllOrchestratorsReqest();
            exportAllOrchestratorsReqest.setProjectId(project.getId());
            exportAllOrchestratorsReqest.setComment("create Project:" + projectCreateRequest.getName());
            exportAllOrchestratorsReqest.setLabels(DSSCommonUtils.ENV_LABEL_VALUE_DEV);
            BmlResource bmlResource = dssProjectService.exportProject(exportAllOrchestratorsReqest, username, "", workspace);
            createGitProject(workspace.getWorkspaceId(), project.getName(), bmlResource, username, projectCreateRequest.getGitUser(), projectCreateRequest.getGitToken());
        }
        //4.保存dss_project_user 工程与用户关系
        projectUserService.saveProjectUser(project.getId(), username, projectCreateRequest, workspace);
        //5.保存dss工程与其他工程的对应关系,应该都是以id来作为标识
        Map<AppInstance, Long> projectMap = createAppConnProject(projectCreateRequest, workspace, username);
        if (projectMap.size() > 0) {
            dssProjectService.saveProjectRelation(project, projectMap);
        }
        DSSProjectVo dssProjectVo = new DSSProjectVo();
        dssProjectVo.setDescription(project.getDescription());
        dssProjectVo.setId(project.getId());
        dssProjectVo.setName(project.getName());
        return dssProjectVo;
    }

    private void createGitProject(Long workspaceId, String projectName, BmlResource bmlResource, String username, String gitUser, String gitToken) {
        Sender gitSender = DSSSenderServiceFactory.getOrCreateServiceInstance().getGitSender();
        GitCreateProjectRequest request1 = new GitCreateProjectRequest(workspaceId, projectName, bmlResource, username, gitUser, gitToken);
        LOGGER.info("-------=======================begin to create project: {}=======================-------", projectName);
        Object ask = gitSender.ask(request1);
        GitCreateProjectResponse responseWorkflowValidNode = RpcAskUtils.processAskException(ask, GitCreateProjectResponse.class, GitCreateProjectResponse.class);
        LOGGER.info("-------=======================End to create project: {}=======================-------: {}", projectName, responseWorkflowValidNode);
    }


    @Override
    public void checkProjectName(String name, Workspace workspace, String username) throws DSSProjectErrorException {
        //判断工程是否存在相同的名称
        DSSProjectDO dbProject = dssProjectService.getProjectByName(name);
        if (dbProject != null) {
            DSSExceptionUtils.dealErrorException(60022, String.format("project name %s has already been exists.", name), DSSProjectErrorException.class);
        }
        List<String> appConnNameList = new ArrayList<>(1);
        //判断已有组件是否已经存在相同的工程名称
        ProjectCreateRequest projectCreateRequest = new ProjectCreateRequest();
        projectCreateRequest.setName(name);
        try {
            isExistSameProjectName(projectCreateRequest, workspace, appConnNameList, username);
        } catch (WarnException e) {
            throw new DSSProjectErrorException(71000, "向第三方应用发起检查工程名是否重复失败. " + e.getDesc(), e);
        } catch (Exception e) {
            throw new DSSProjectErrorException(71000, "向第三方应用发起检查工程名是否重复失败. 原因：" + ExceptionUtils.getRootCauseMessage(e), e);
        }
        if (!appConnNameList.isEmpty()) {
            throw new DSSProjectErrorException(71000, String.join(", ", appConnNameList) + " 已存在相同项目名称，请重新命名!");
        }
    }

    @Override
    public void modifyProject(ProjectModifyRequest projectModifyRequest, DSSProjectDO dbProject, String username, Workspace workspace) throws Exception {
        Boolean associateGit = dbProject.getAssociateGit();
        // 校验参数正确性
        checkProjectRight(projectModifyRequest, dbProject, username, workspace);

        // 1.统一修改各个接入的第三方的系统的工程状态信息
        //调用第三方的工程修改接口
        dbProject.setUsername(username);
        dbProject.setApplicationArea(Integer.valueOf(projectModifyRequest.getApplicationArea()));
        dbProject.setDescription(projectModifyRequest.getDescription());
        dbProject.setBusiness(projectModifyRequest.getBusiness());
        dbProject.setProduct(projectModifyRequest.getProduct());
        modifyThirdProject(projectModifyRequest, dbProject, workspace, dbProject.getCreateBy());

        //2.修改dss_project_user 工程与用户关系
        projectUserService.modifyProjectUser(dbProject, projectModifyRequest, username, workspace);

        //3.修改dss_project DSS基本工程信息
        DSSProjectDO dssProjectDO = dssProjectService.modifyProject(username, projectModifyRequest);

        // 同步项目配置元数据到git
        try {
            syncGitProject(projectModifyRequest, dbProject, username, workspace);
        }catch (Exception e) {
            DSSProjectDO project = new DSSProjectDO();
            project.setAssociateGit(associateGit);
            UpdateWrapper<DSSProjectDO> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", projectModifyRequest.getId());
            updateWrapper.eq("workspace_id", projectModifyRequest.getWorkspaceId());
            projectMapper.update(project, updateWrapper);
            throw new DSSProjectErrorException(71000, "修改项目接入Git失败，原因为：" + e.getMessage());
        }
    }

    private void updateProject(Long workspaceId, String projectName,BmlResource bmlResource, String username, String gitUser, String gitToken) {
        Sender gitSender = DSSSenderServiceFactory.getOrCreateServiceInstance().getGitSender();
        Map<String, BmlResource> file = new HashMap<>();
        // 测试数据 key表示项目名、value为项目BmlResource资源
        file.put(".projectmeta", bmlResource);
        GitCommitRequest request1 = new GitCommitRequest(workspaceId, projectName, file, "update Project", username, gitUser, gitToken);
        LOGGER.info("-------=======================begin to update project: {}=======================-------", projectName);
        GitCommitResponse responseWorkflowValidNode = RpcAskUtils.processAskException(gitSender.ask(request1), GitCommitResponse.class, GitCommitRequest.class);
        LOGGER.info("-------=======================End to update project: {}=======================-------: {}", projectName, responseWorkflowValidNode);
    }

    /**
     * 统一修改各个接入的第三方的系统的工程状态信息，修改 dss_project 调用。
     */
    private void modifyThirdProject(ProjectModifyRequest projectModifyRequest,
                                    DSSProjectDO dbProject, Workspace workspace, String username){
        DSSProject dssProject = new DSSProject();
        BeanUtils.copyProperties(dbProject, dssProject);
        DSSProjectPrivilege privilege = DSSProjectPrivilege.newBuilder().setAccessUsers(projectModifyRequest.getAccessUsers())
                .setEditUsers(projectModifyRequest.getEditUsers())
                .setReleaseUsers(projectModifyRequest.getReleaseUsers()).build();
        List<DSSProjectUser> projectUsers = projectUserService.getProjectPriv(projectModifyRequest.getId());
        DSSProjectPrivilege addedPrivilege;
        DSSProjectPrivilege removedPrivilege;
        if(CollectionUtils.isEmpty(projectUsers)) {
            addedPrivilege = privilege;
            removedPrivilege = DSSProjectPrivilege.EMPTY;
        } else {
            BiFunction<ImmutablePair<Integer, Boolean>, List<String>, List<String>> getDifference = (privAndIsAdded, users) -> {
                List<String> privUsers = projectUsers.stream().filter(user -> user.getPriv().equals(privAndIsAdded.getKey())).map(DSSProjectUser::getUsername).collect(Collectors.toList());
                if(privAndIsAdded.getValue()) {
                    return (List<String>) CollectionUtils.subtract(users, privUsers);
                } else {
                    return (List<String>) CollectionUtils.subtract(privUsers, users);
                }
            };
            Function<Boolean, DSSProjectPrivilege> getPrivilege = isAdded -> DSSProjectPrivilege.newBuilder().setAccessUsers(getDifference.apply(new ImmutablePair<>(1, isAdded), projectModifyRequest.getAccessUsers()))
                    .setEditUsers(getDifference.apply(new ImmutablePair<>(2, isAdded), projectModifyRequest.getEditUsers()))
                    .setReleaseUsers(getDifference.apply(new ImmutablePair<>(3, isAdded), projectModifyRequest.getReleaseUsers())).build();
            addedPrivilege = getPrivilege.apply(true);
            removedPrivilege = getPrivilege.apply(false);
        }
        List<DSSProjectDataSource> dataSourceList = projectModifyRequest.getDataSourceList();
        Map<AppInstance, Long> appInstanceToRefProjectId = new HashMap<>(10);
        tryProjectOperation((appConn, appInstance) -> {
                Long refProjectId = dssProjectService.getAppConnProjectId(appInstance.getId(), dbProject.getId());
                if(refProjectId == null) {
                    LOGGER.warn("update project {} for third-party AppConn {} is ignored, appInstance is {}. Caused by: the refProjectId is null.",
                            projectModifyRequest.getName(), appConn.getAppDesc().getAppName(), appInstance.getBaseUrl());
                    return false;
                } else {
                    appInstanceToRefProjectId.put(appInstance, refProjectId);
                    return true;
                }
            }, workspace, projectService -> projectService.getProjectUpdateOperation(),
            dssProjectContentRequestRef -> dssProjectContentRequestRef.setDSSProject(dssProject).setDSSProjectPrivilege(privilege).setDSSProjectDataSources(dataSourceList).setUserName(username).setWorkspace(workspace),
            (appInstance, refProjectContentRequestRef) -> refProjectContentRequestRef.setRefProjectId(appInstanceToRefProjectId.get(appInstance)),
            (structureOperation, structureRequestRef) -> {
                ProjectUpdateRequestRef projectUpdateRequestRef = (ProjectUpdateRequestRef) structureRequestRef;
                projectUpdateRequestRef.setAddedDSSProjectPrivilege(addedPrivilege).setRemovedDSSProjectPrivilege(removedPrivilege);
                return ((ProjectUpdateOperation) structureOperation).updateProject(projectUpdateRequestRef);
            }, null, "update refProject " + projectModifyRequest.getName());
    }



    private void isExistSameProjectName(ProjectCreateRequest dssProjectCreateRequest,
                                        Workspace workspace,
                                        List<String> appConnNameList,
                                        String username) throws ExternalOperationFailedException {
        LOGGER.info("begin to check whether the project name {} is already exists in third-party AppConn...", dssProjectCreateRequest.getName());
        tryProjectOperation(null, workspace, ProjectService::getProjectSearchOperation,
                null,
                (appInstance, refProjectContentRequestRef) -> refProjectContentRequestRef.setProjectName(dssProjectCreateRequest.getName()).setUserName(username),
                (structureOperation, structureRequestRef) -> ((ProjectSearchOperation) structureOperation).searchProject((RefProjectContentRequestRef) structureRequestRef),
                (pair, responseRef) -> {
                    ProjectService projectService = ((OnlyStructureAppConn) pair.getLeft()).getOrCreateStructureStandard().getProjectService(pair.getRight());
                    if(responseRef.getRefProjectId() != null && responseRef.getRefProjectId() > 0 && projectService.isProjectNameUnique()) {
                        appConnNameList.add(pair.getLeft().getAppDesc().getAppName());
                    }
                }, "check project name " + dssProjectCreateRequest.getName() + " whether it is exists");
    }

    // 1.新建DSS工程,这样才能进行回滚,如果后面去DSS工程，可能会由于DSS工程建立失败了，但是仍然无法去回滚第三方系统的工程 新增dss_project调用
    private Map<AppInstance, Long> createAppConnProject(ProjectCreateRequest dssProjectCreateRequest, Workspace workspace, String username) {
        final Map<AppInstance, Long> projectMap = new HashMap<>(10);
        final Map<AppConn, List<AppInstance>> appConnListMap = new HashMap<>(10);
        DSSProject dssProject = new DSSProject();
        BeanUtils.copyProperties(dssProjectCreateRequest, dssProject);
        dssProject.setCreateBy(username);
        dssProject.setCreateTime(new Date());
        dssProject.setUsername(username);
        DSSProjectPrivilege privilege = DSSProjectPrivilege.newBuilder().setAccessUsers(dssProjectCreateRequest.getAccessUsers())
                .setEditUsers(dssProjectCreateRequest.getEditUsers())
                .setReleaseUsers(dssProjectCreateRequest.getReleaseUsers()).build();
        List<DSSProjectDataSource> dataSourceList = dssProjectCreateRequest.getDataSourceList();
        try {
            tryProjectOperation(null, workspace, ProjectService::getProjectCreationOperation,
                    dssProjectContentRequestRef -> dssProjectContentRequestRef.setDSSProject(dssProject)
                            .setDSSProjectPrivilege(privilege).setDSSProjectDataSources(dataSourceList).setUserName(username), null,
                    (structureOperation, structureRequestRef) -> ((ProjectCreationOperation) structureOperation).createProject((DSSProjectContentRequestRef) structureRequestRef),
                    (pair, projectResponseRef) -> {
                        projectMap.put(pair.right, projectResponseRef.getRefProjectId());
                        if (!appConnListMap.containsKey(pair.left)) {
                            appConnListMap.put(pair.left, new ArrayList<>());
                        }
                        appConnListMap.get(pair.left).add(pair.right);
                    }, "create refProject " + dssProjectCreateRequest.getName());
        } catch (RuntimeException e) {
            LOGGER.error("create AppConn project failed!", e);
            if(!STRICT_PROJECT_CREATE_MODE) {
                throw e;
            }
            LOGGER.warn("Strict project create mode is opened, now try to delete the projects {} created in external AppConns.", dssProjectCreateRequest.getName());
            // 如果创建失败并且是严格创建模式
            // 如果一个AppInstance实例是失败的，那么我们将所有已经建的工程给撤销掉
            try {
                appConnListMap.forEach((key, value) -> value.forEach(appInstance -> {
                    LOGGER.warn("{} try to delete the create-failed project {} with AppInstance {}!", key.getAppDesc().getAppName(), dssProjectCreateRequest.getName(), appInstance.getBaseUrl());
                    StructureOperationUtils.tryProjectOperation(() -> ((OnlyStructureAppConn) key).getOrCreateStructureStandard().getProjectService(appInstance),
                            ProjectService::getProjectDeletionOperation, null,
                            refProjectContentRequestRef -> refProjectContentRequestRef.setRefProjectId(projectMap.get(appInstance))
                                    .setProjectName(dssProjectCreateRequest.getName()).setWorkspace(workspace).setUserName(username),
                            (structureOperation, structureRequestRef) -> ((ProjectDeletionOperation) structureOperation).deleteProject((RefProjectContentRequestRef) structureRequestRef),
                            key.getAppDesc().getAppName() + " try to delete refProject " + dssProjectCreateRequest.getName());
                }));
            } catch (RuntimeException e1) {
                // 原则上，如果删除失败，只是会存在一个无用的工程，不会对用户的使用造成影响
                LOGGER.error("try to delete the create-failed project {} in AppConn failed!", dssProjectCreateRequest.getName(), e1);
                String errorMsg;
                if(e instanceof WarnException) {
                    errorMsg = String.format("%s. please notify the admin that try to rollback the created projects in external AppConn failed.", ((WarnException) e).getDesc());
                } else {
                    errorMsg = String.format("create project failed, Reason: %s. please notify the admin that try to rollback the created projects in external AppConn failed.", ExceptionUtils.getRootCauseMessage(e));
                }
                throw new ExternalOperationFailedException(50009, errorMsg, e);
            }
            throw e;
        }
        return projectMap;
    }

    private void checkGitName(String name, Workspace workspace, String username, String gitUser, String gitToken) throws DSSProjectErrorException {
        // 校验是否为虚拟用户，此处禁止使用DSS实名用户接入Git
        List<StaffInfoVO> all = dssWorkspaceUserService.listAllDSSUsers();
        Set<String> allDSSUsers = all.stream().map(StaffInfoVO::getUsername).collect(Collectors.toSet());
        if (allDSSUsers.contains(gitUser)) {
            throw new DSSProjectErrorException(71000, "Git读写账号用户名必须为虚拟用户，不能是DSS实名用户！");
        }
        // 校验Git名称
        Sender gitSender = DSSSenderServiceFactory.getOrCreateServiceInstance().getGitSender();
        GitCheckProjectRequest request1 = new GitCheckProjectRequest(workspace.getWorkspaceId(), name, username, gitUser, gitToken);
        LOGGER.info("-------=======================begin to check project: {}=======================-------", name);
        Object ask = gitSender.ask(request1);
        GitCheckProjectResponse responseWorkflowValidNode = RpcAskUtils.processAskException(ask, GitCheckProjectResponse.class, GitCheckProjectRequest.class);
        LOGGER.info("-------=======================End to check project: {}=======================-------: {}", name, responseWorkflowValidNode);
        if (responseWorkflowValidNode == null) {
            throw new DSSProjectErrorException(71000, "向Git发起检查工程名是否重复失败，请稍后重试 ");
        } else if (responseWorkflowValidNode.getRepeat()) {
            throw new DSSProjectErrorException(71000,  "git 已存在相同项目名称，请重新命名!");
        }
    }

    private void initGitProject(Long workspaceId, String projectName, String resource, String version, String username, String gitUser, String gitToken) {
        Sender gitSender = DSSSenderServiceFactory.getOrCreateServiceInstance().getGitSender();
        Map<String, BmlResource> file = new HashMap<>();
        // 测试数据 key表示项目名、value为项目BmlResource资源
        GitCreateProjectRequest request1 = new GitCreateProjectRequest(workspaceId, projectName, new BmlResource(resource, version), username, gitUser, gitToken);
        LOGGER.info("-------=======================begin to create project: {}=======================-------", projectName);
        Object ask = gitSender.ask(request1);
        GitCreateProjectResponse responseWorkflowValidNode = RpcAskUtils.processAskException(ask, GitCreateProjectResponse.class, GitCreateProjectRequest.class);
        LOGGER.info("-------=======================End to create project: {}=======================-------: {}", projectName, responseWorkflowValidNode);
    }

    private void checkAssociateGit(ProjectModifyRequest projectModifyRequest, DSSProjectDO dbProject, String username, Workspace workspace) throws DSSProjectErrorException {
        // 对于已经接入git的项目不允许进行取消
        if (dbProject.getAssociateGit()!=null && dbProject.getAssociateGit()
                && projectModifyRequest.getAssociateGit()!= null && !projectModifyRequest.getAssociateGit()) {
            throw new DSSProjectErrorException(71000, "项目接入git后无法取消");
        }
        // 对于首次接入git的项目需要校验项目名称
        if ((dbProject.getAssociateGit() == null || !dbProject.getAssociateGit()) && projectModifyRequest.getAssociateGit()!= null && projectModifyRequest.getAssociateGit()) {
            // 校验gitUser gitToken合法性以及projectName是否重复
            checkGitName(projectModifyRequest.getName(), workspace, username, projectModifyRequest.getGitUser(), projectModifyRequest.getGitToken());
        }
    }

    @Override
    public void modifyProjectMeta(ProjectModifyRequest projectModifyRequest, DSSProjectDO dbProject, String username, Workspace workspace) throws Exception {
        // 校验项目参数正确性
        checkProjectRight(projectModifyRequest, dbProject, username, workspace);

        // 1.统一修改各个接入的第三方的系统的工程状态信息
        //调用第三方的工程修改接口
        dbProject.setDescription(projectModifyRequest.getDescription());
        dbProject.setUsername(username);
        modifyThirdProject(projectModifyRequest, dbProject, workspace, dbProject.getCreateBy());

        //2.修改dss_project_user 工程与用户关系
        projectUserService.modifyProjectUser(dbProject, projectModifyRequest, username, workspace);

        //3.修改dss_project DSS基本工程信息
        dssProjectService.modifyProjectMeta(username, projectModifyRequest);

        // 同步项目配置元数据到git
        syncGitProject(projectModifyRequest, dbProject, username, workspace);

    }


    public void checkProjectRight(ProjectModifyRequest projectModifyRequest, DSSProjectDO dbProject, String username, Workspace workspace) throws Exception {

        //如果不是工程的创建人，则校验是否管理员或发布者权限
        if (!username.equalsIgnoreCase(dbProject.getCreateBy())) {
            //获取发布者权限用户
            List<String> projectUsers = projectUserService.getProjectPriv(projectModifyRequest.getId()).stream()
                    .filter(projectUser -> projectUser.getPriv() == 3).map(DSSProjectUser::getUsername).collect(Collectors.toList());
            boolean isAdmin = projectUserService.isAdminByUsername(projectModifyRequest.getWorkspaceId(), username);
            //非管理员非发布者权限
            if (!isAdmin && !projectUsers.contains(username)) {
                DSSExceptionUtils.dealErrorException(ProjectServerResponse.PROJECT_IS_NOT_ADMIN_OR_RELEASE.getCode(), ProjectServerResponse.PROJECT_IS_NOT_ADMIN_OR_RELEASE.getMsg(), DSSProjectErrorException.class);
            }
        }

        //不允许修改工程名称
        if (!dbProject.getName().equalsIgnoreCase(projectModifyRequest.getName())) {
            DSSExceptionUtils.dealErrorException(ProjectServerResponse.PROJECT_NOT_EDIT_NAME.getCode(), ProjectServerResponse.PROJECT_NOT_EDIT_NAME.getMsg(), DSSProjectErrorException.class);
        }

        // 校验是否接入git
        if (projectModifyRequest.getAssociateGit() != null) {
            checkAssociateGit(projectModifyRequest, dbProject, username, workspace);
        }

    }

    public void syncGitProject(ProjectModifyRequest projectModifyRequest, DSSProjectDO dbProject, String username, Workspace workspace) throws Exception{

        // 同步项目配置元数据到git
        if (projectModifyRequest.getAssociateGit() != null && projectModifyRequest.getAssociateGit()) {
            ExportAllOrchestratorsReqest exportAllOrchestratorsReqest = new ExportAllOrchestratorsReqest();
            exportAllOrchestratorsReqest.setProjectId(dbProject.getId());
            exportAllOrchestratorsReqest.setComment("modify Project");
            exportAllOrchestratorsReqest.setLabels(DSSCommonUtils.ENV_LABEL_VALUE_DEV);
            //如果不是工程的创建人，则校验是否管理员或发布者权限
            String uploadUserName = username;
            if (!username.equalsIgnoreCase(dbProject.getCreateBy())) {
                //获取发布者权限用户
                List<String> projectUsers = projectUserService.getProjectPriv(projectModifyRequest.getId()).stream()
                        .filter(projectUser -> projectUser.getPriv() == 3).map(DSSProjectUser::getUsername).collect(Collectors.toList());
                boolean isAdmin = projectUserService.isAdminByUsername(projectModifyRequest.getWorkspaceId(), username);
                // 管理员非发布者权限
                if (!projectUsers.contains(username) && isAdmin) {
                    uploadUserName = dbProject.getCreateBy();
                }
            }
            BmlResource bmlResource = dssProjectService.exportOnlyProjectMeta(exportAllOrchestratorsReqest, uploadUserName, "", workspace);
            if ((dbProject.getAssociateGit() == null || !dbProject.getAssociateGit()) && projectModifyRequest.getAssociateGit() != null && projectModifyRequest.getAssociateGit()) {
                createGitProject(workspace.getWorkspaceId(), dbProject.getName(), bmlResource, username, projectModifyRequest.getGitUser(), projectModifyRequest.getGitToken());
                OrchestratorRequest orchestratorRequest = new OrchestratorRequest(workspace.getWorkspaceId(), projectModifyRequest.getId());
                List<DSSOrchestratorInfo> orchestratorInfoByLabel = orchestratorService.getOrchestratorInfoByLabel(orchestratorRequest);
                if (CollectionUtils.isNotEmpty(orchestratorInfoByLabel)) {
                    List<Long> orchestratorIdList = orchestratorInfoByLabel.stream().map(DSSOrchestratorInfo::getId).collect(Collectors.toList());
                    lockMapper.batchUpdateOrchestratorStatus(orchestratorIdList, OrchestratorRefConstant.FLOW_STATUS_SAVE);
                }

            } else {
                updateProject(workspace.getWorkspaceId(), dbProject.getName(), bmlResource, username, projectModifyRequest.getGitUser(), projectModifyRequest.getGitToken());
            }
        }
    }

    @Override
    public void transferProject(ProjectTransferRequest projectTransferRequest, DSSProjectDO dbProject, String username, Workspace workspace) throws Exception {
        ProjectModifyRequest projectModifyRequest = new ProjectModifyRequest();

        BeanUtils.copyProperties(dbProject, projectModifyRequest);
        initProjectModifyRequest(projectModifyRequest,projectTransferRequest, username);

        dbProject.setCreateBy(projectTransferRequest.getTransferUserName());
        dbProject.setUsername(projectTransferRequest.getTransferUserName());
        // 1.统一修改各个接入的第三方的系统的工程状态信息
        //调用第三方的工程修改接口
        modifyThirdProject(projectModifyRequest, dbProject, workspace, username);

        //2.修改dss_project_user 工程与用户关系
        projectUserService.modifyProjectUser(dbProject, projectModifyRequest, username, workspace);

        //3.修改dss_project DSS基本工程信息
        updateProject4Transfer(dbProject, username);

        syncGitProject(projectModifyRequest, dbProject, username, workspace);
    }

    private void updateProject4Transfer(DSSProjectDO dbProject,String username){
        dbProject.setUpdateByStr(username);
        dbProject.setUpdateTime(new Date());
        UpdateWrapper<DSSProjectDO> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", dbProject.getId());
        updateWrapper.eq("workspace_id", dbProject.getWorkspaceId());
        projectMapper.update(dbProject, updateWrapper);
    }

    private void initProjectModifyRequest(ProjectModifyRequest projectModifyRequest,ProjectTransferRequest projectTransferRequest,String username){
        List<DSSProjectUser> projectPriv = projectUserService.getProjectPriv(projectModifyRequest.getId());
        List<String> releaseUsers = projectPriv.stream().filter(projectUser -> projectUser.getPriv() == 3).map(DSSProjectUser::getUsername).collect(Collectors.toList());
        if(!releaseUsers.contains(projectTransferRequest.getTransferUserName())){
            releaseUsers.add(projectTransferRequest.getTransferUserName());
        }
        if(releaseUsers.contains(username)){
            releaseUsers.remove(username);
        }
        projectModifyRequest.setReleaseUsers(releaseUsers);

        List<String> editUsers = projectPriv.stream().filter(projectUser -> projectUser.getPriv() == 2).map(DSSProjectUser::getUsername).collect(Collectors.toList());
        if(!editUsers.contains(projectTransferRequest.getTransferUserName())){
            editUsers.add(projectTransferRequest.getTransferUserName());
        }
        if(editUsers.contains(username)){
            editUsers.remove(username);
        }
        projectModifyRequest.setEditUsers(editUsers);
        List<String> accessUsers = projectPriv.stream().filter(projectUser -> projectUser.getPriv() == 1).map(DSSProjectUser::getUsername).collect(Collectors.toList());
        if(accessUsers.contains(projectTransferRequest.getTransferUserName())){
            accessUsers.remove(projectTransferRequest.getTransferUserName());
        }
        projectModifyRequest.setAccessUsers(accessUsers);
    }

}
