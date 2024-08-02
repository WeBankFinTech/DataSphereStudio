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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.appconn.manager.AppConnManager;
import com.webank.wedatasphere.dss.common.entity.BmlResource;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.exception.DSSRuntimeException;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.common.label.EnvDSSLabel;
import com.webank.wedatasphere.dss.common.label.LabelRouteVO;
import com.webank.wedatasphere.dss.common.service.BMLService;
import com.webank.wedatasphere.dss.common.utils.*;
import com.webank.wedatasphere.dss.framework.project.conf.ProjectConf;
import com.webank.wedatasphere.dss.framework.project.contant.ProjectServerResponse;
import com.webank.wedatasphere.dss.common.constant.project.ProjectUserPrivEnum;
import com.webank.wedatasphere.dss.framework.project.dao.DSSProjectMapper;
import com.webank.wedatasphere.dss.framework.project.dao.DSSProjectUserMapper;
import com.webank.wedatasphere.dss.framework.project.entity.DSSProjectDO;
import com.webank.wedatasphere.dss.framework.project.entity.po.ProjectRelationPo;
import com.webank.wedatasphere.dss.framework.project.entity.request.*;
import com.webank.wedatasphere.dss.framework.project.entity.response.ProjectResponse;
import com.webank.wedatasphere.dss.framework.project.entity.vo.ProjectInfoVo;
import com.webank.wedatasphere.dss.framework.project.entity.vo.QueryProjectVo;
import com.webank.wedatasphere.dss.framework.project.exception.DSSProjectErrorException;
import com.webank.wedatasphere.dss.framework.project.service.DSSProjectService;
import com.webank.wedatasphere.dss.framework.project.service.ExportService;
import com.webank.wedatasphere.dss.framework.project.service.ImportService;
import com.webank.wedatasphere.dss.framework.project.utils.ProjectStringUtils;
import com.webank.wedatasphere.dss.framework.workspace.bean.DSSWorkspaceUser;
import com.webank.wedatasphere.dss.git.common.protocol.GitUserEntity;
import com.webank.wedatasphere.dss.git.common.protocol.request.GitArchiveProjectRequest;
import com.webank.wedatasphere.dss.git.common.protocol.request.GitUserByWorkspaceIdRequest;
import com.webank.wedatasphere.dss.git.common.protocol.response.GitArchivePorjectResponse;
import com.webank.wedatasphere.dss.git.common.protocol.response.GitUserByWorkspaceResponse;
import com.webank.wedatasphere.dss.orchestrator.server.entity.request.OrchestratorRequest;
import com.webank.wedatasphere.dss.orchestrator.server.entity.vo.OrchestratorBaseInfo;
import com.webank.wedatasphere.dss.orchestrator.server.service.OrchestratorService;
import com.webank.wedatasphere.dss.sender.service.DSSSenderServiceFactory;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectDeletionOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectService;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.DSSProjectDataSource;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.RefProjectContentRequestRef;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.rpc.Sender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static com.webank.wedatasphere.dss.framework.project.utils.ProjectOperationUtils.tryProjectOperation;

public class DSSProjectServiceImpl extends ServiceImpl<DSSProjectMapper, DSSProjectDO> implements DSSProjectService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DSSProjectServiceImpl.class);
    @Autowired
    private DSSProjectMapper projectMapper;
    @Autowired
    private ExportService exportService;
    @Autowired
    private DSSProjectUserMapper projectUserMapper;
    @Autowired
    private OrchestratorService orchestratorService;
    @Autowired
    private DSSProjectService projectService;
    @Autowired
    private ImportService importService;
    @Autowired
    @Qualifier("projectBmlService")
    private BMLService bmlService;


    public static final String MODE_SPLIT = ",";
    public static final String KEY_SPLIT = "-";
    public static final String PROJECT_META_FILE_NAME = ".projectmeta";
    private final String SUPPORT_ABILITY = ProjectConf.SUPPORT_ABILITY.getValue();
    private final ThreadFactory projectOperateThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("dss-project-operate-thread-%d")
            .setDaemon(false)
            .build();
    private final ExecutorService projectOperateThreadPool = new ThreadPoolExecutor(5, 200, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(1024), projectOperateThreadFactory, new ThreadPoolExecutor.AbortPolicy());

    @Override
    public DSSProjectDO createProject(String username, ProjectCreateRequest projectCreateRequest) {
        DSSProjectDO project = new DSSProjectDO();
        project.setName(projectCreateRequest.getName());
        project.setWorkspaceId(projectCreateRequest.getWorkspaceId());
        project.setCreateBy(username);
        project.setUsername(username);
        project.setCreateTime(new Date());
        project.setBusiness(projectCreateRequest.getBusiness());
        project.setProduct(projectCreateRequest.getProduct());
        project.setUpdateTime(new Date());
        project.setDescription(projectCreateRequest.getDescription());
        project.setApplicationArea(projectCreateRequest.getApplicationArea());
        project.setAssociateGit(projectCreateRequest.getAssociateGit());
        //开发流程，编排模式组拼接 前后进行英文逗号接口
        project.setDevProcess(ProjectStringUtils.getModeStr(projectCreateRequest.getDevProcessList()));
        project.setOrchestratorMode(ProjectStringUtils.getModeStr(projectCreateRequest.getOrchestratorModeList()));
        List<DSSProjectDataSource> dataSourceList = projectCreateRequest.getDataSourceList();
        if (dataSourceList != null && !dataSourceList.isEmpty()) {
            project.setDataSourceListJson(new Gson().toJson(dataSourceList));
        }
        if (projectCreateRequest.getCreatorLabel() != null) {
            project.setLabel(projectCreateRequest.getCreatorLabel());
        }
        projectMapper.insert(project);
        return project;
    }

    //修改dss_project工程字段
    @Override
    public DSSProjectDO modifyProject(String username, ProjectModifyRequest projectModifyRequest) throws DSSProjectErrorException {
        //校验当前登录用户是否含有修改权限
//        projectUserService.isEditProjectAuth(projectModifyRequest.getId(), username);
        DSSProjectDO project = new DSSProjectDO();
        //修改的字段
        project.setDescription(projectModifyRequest.getDescription());
        project.setUpdateTime(new Date());
        project.setUpdateByStr(username);
        project.setDevProcess(ProjectStringUtils.getModeStr(projectModifyRequest.getDevProcessList()));
        if (StringUtils.isNotBlank(projectModifyRequest.getApplicationArea())) {
            project.setApplicationArea(Integer.valueOf(projectModifyRequest.getApplicationArea()));
        }
        project.setOrchestratorMode(ProjectStringUtils.getModeStr(projectModifyRequest.getOrchestratorModeList()));
        project.setBusiness(projectModifyRequest.getBusiness());
        project.setProduct(projectModifyRequest.getProduct());
        project.setAssociateGit(projectModifyRequest.getAssociateGit());
        List<DSSProjectDataSource> dataSourceList = projectModifyRequest.getDataSourceList();
        if (dataSourceList != null && !dataSourceList.isEmpty()) {
            project.setDataSourceListJson(new Gson().toJson(dataSourceList));
        }
        UpdateWrapper<DSSProjectDO> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", projectModifyRequest.getId());
        updateWrapper.eq("workspace_id", projectModifyRequest.getWorkspaceId());
        projectMapper.update(project, updateWrapper);

        return project;
    }

    /**
     * 修改旧dss_project工程字段
     */
    @Override
    public void modifyOldProject(DSSProjectDO updateProject, DSSProjectDO dbProject) {
        DSSProjectDO project = new DSSProjectDO();
        //修改的字段
        project.setUpdateTime(new Date());
        project.setUpdateByStr(updateProject.getUpdateByStr());
        project.setDescription(updateProject.getDescription());
        project.setBusiness(updateProject.getBusiness());
        project.setApplicationArea(updateProject.getApplicationArea());

        UpdateWrapper<DSSProjectDO> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", dbProject.getId());
        updateWrapper.eq("workspace_id", dbProject.getWorkspaceId());
        projectMapper.update(project, updateWrapper);
    }

    @Override
    public DSSProjectDO getProjectByName(String name) {
        QueryWrapper<DSSProjectDO> projectQueryWrapper = new QueryWrapper<>();
        projectQueryWrapper.eq("name", name);
        List<DSSProjectDO> projectList = projectMapper.selectList(projectQueryWrapper);
        return CollectionUtils.isEmpty(projectList) ? null : projectList.get(0);
    }

    @Override
    public DSSProjectDO getProjectById(Long id) {
        return projectMapper.selectById(id);
    }

    @Override
    public List<ProjectResponse> getListByParam(ProjectQueryRequest projectRequest) {
        //根据dss_project、dss_project_user查询出所在空间登录用户相关的工程
        List<QueryProjectVo> list;
        //判断工作空间是否设置了管理员能否查看该工作空间下所有项目的权限
        Integer workspaceAdminPermission = projectUserMapper.getWorkspaceAdminPermission(projectRequest.getWorkspaceId());
        if (isWorkspaceAdmin(projectRequest.getWorkspaceId(), projectRequest.getUsername()) && workspaceAdminPermission == 1) {
            list = projectMapper.getListForAdmin(projectRequest);
        } else {
            list = projectMapper.getListByParam(projectRequest);
        }
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }

        Map<String, GitUserEntity> projectGitUserInfo = getProjectGitUserInfo(projectRequest.getUsername(), projectRequest.getWorkspaceId());
        List<ProjectResponse> projectResponseList = new ArrayList<>();
        ProjectResponse projectResponse;
        for (QueryProjectVo projectVo : list) {
            if (projectVo.getVisible() == 0) {
                continue;
            }
            projectResponse = new ProjectResponse();
            projectResponse.setApplicationArea(projectVo.getApplicationArea());
            projectResponse.setId(projectVo.getId());
            projectResponse.setBusiness(projectVo.getBusiness());
            projectResponse.setCreateBy(projectVo.getCreateBy());
            projectResponse.setDescription(projectVo.getDescription());
            projectResponse.setName(projectVo.getName());
            projectResponse.setProduct(projectVo.getProduct());
            projectResponse.setSource(projectVo.getSource());
            projectResponse.setArchive(projectVo.getArchive());
            projectResponse.setCreateTime(projectVo.getCreateTime());
            projectResponse.setUpdateTime(projectVo.getUpdateTime());
            projectResponse.setAssociateGit(projectVo.getAssociateGit());
            if (projectVo.getAssociateGit()) {
                GitUserEntity gitUserEntity = projectGitUserInfo.get(projectVo.getName());
                if (gitUserEntity != null) {
                    projectResponse.setGitUser(gitUserEntity.getGitUser());
                    projectResponse.setGitToken(gitUserEntity.getGitToken());
                }
            }
            projectResponse.setDevProcessList(ProjectStringUtils.convertList(projectVo.getDevProcess()));
            projectResponse.setOrchestratorModeList(ProjectStringUtils.convertList(projectVo.getOrchestratorMode()));
            if (projectVo.getDataSourceListJson() != null) {
                List<DSSProjectDataSource> dataSourceList = new Gson().fromJson(projectVo.getDataSourceListJson(),
                        new TypeToken<List<DSSProjectDataSource>>() {
                        }.getType());
                projectResponse.setDataSourceList(dataSourceList);
            }

            String pusername = projectVo.getPusername();
            String editPriv = projectVo.getId() + KEY_SPLIT + ProjectUserPrivEnum.PRIV_EDIT.getRank()
                    + KEY_SPLIT + projectRequest.getUsername();

            LOGGER.info("user:{} get project privilege info ,workspaceId:{}, projectId:{}, projectName:{}, pusername:{}, editPriv:{}",
                    projectRequest.getUsername(), projectRequest.getWorkspaceId(), projectVo.getId(), projectVo.getName(), pusername, editPriv);

            Map<String, List<String>> userPricMap = new HashMap<>();
            String[] tempstrArr = pusername.split(MODE_SPLIT);

            // 拆分有projectId +"-" + priv + "-" + username的拼接而成的字段，
            // 从而得到：查看权限用户、编辑权限用户、发布权限用a
            for (String s : tempstrArr) {
                String[] strArr = s.split(KEY_SPLIT);
                if (strArr.length >= 3) {
                    String key = strArr[0] + KEY_SPLIT + strArr[1];
                    userPricMap.computeIfAbsent(key, k -> new ArrayList<>());
                    userPricMap.get(key).add(strArr[2]);
                }
            }
            List<String> accessUsers = userPricMap.get(projectVo.getId() + KEY_SPLIT + ProjectUserPrivEnum.PRIV_ACCESS.getRank());
            List<String> editUsers = userPricMap.get(projectVo.getId() + KEY_SPLIT + ProjectUserPrivEnum.PRIV_EDIT.getRank());
            List<String> releaseUsers = userPricMap.get(projectVo.getId() + KEY_SPLIT + ProjectUserPrivEnum.PRIV_RELEASE.getRank());
            projectResponse.setAccessUsers(CollectionUtils.isEmpty(accessUsers) ? new ArrayList<>() : accessUsers.stream().distinct().collect(Collectors.toList()));
            projectResponse.setEditUsers(CollectionUtils.isEmpty(editUsers) ? new ArrayList<>() : editUsers.stream().distinct().collect(Collectors.toList()));
            projectResponse.setReleaseUsers(CollectionUtils.isEmpty(releaseUsers) ? new ArrayList<>() : releaseUsers.stream().distinct().collect(Collectors.toList()));

            LOGGER.info("user:{} get project access users info, workspaceId:{}, projectId:{}, projectName:{}, accessUsers:{}, editUsers:{}, releaseUsers:{}",
                    projectRequest.getUsername(), projectRequest.getWorkspaceId(), projectVo.getId(), projectVo.getName(), accessUsers, editUsers, releaseUsers);

            // 用户是否具有编辑权限  编辑权限和创建者都有
            if (!StringUtils.isEmpty(pusername) &&
                    (pusername.contains(editPriv) ||
                            projectVo.getCreateBy().equals(projectRequest.getUsername()) ||
                            isWorkspaceAdmin(projectRequest.getWorkspaceId(), projectRequest.getUsername())) || projectResponse.getReleaseUsers().contains(projectRequest.getUsername())) {
                projectResponse.setEditable(true);
            } else if (isWorkspaceAdmin(projectRequest.getWorkspaceId(), projectRequest.getUsername()) ||
                    projectVo.getCreateBy().equals(projectRequest.getUsername())) {
                projectResponse.setEditable(true);
            } else {
                projectResponse.setEditable(false);
            }

            projectResponseList.add(projectResponse);
        }
        return projectResponseList;
    }

    @Override
    public ProjectInfoVo getProjectInfoById(Long id) {
        return projectMapper.getProjectInfoById(id);
    }


    @Override
    public void saveProjectRelation(DSSProjectDO project, Map<AppInstance, Long> projectMap) {
        List<ProjectRelationPo> relationPos = new ArrayList<>();
        projectMap.forEach((k, v) -> relationPos.add(new ProjectRelationPo(project.getId(), k.getId(), v)));
        projectMapper.saveProjectRelation(relationPos);
    }


    @Override
    public Long getAppConnProjectId(Long dssProjectId, String appConnName, List<DSSLabel> dssLabels) throws Exception {
        AppConn appConn = AppConnManager.getAppConnManager().getAppConn(appConnName);
        List<AppInstance> appInstances = appConn.getAppDesc().getAppInstancesByLabels(dssLabels);
        if (appInstances.get(0) != null) {
            Long appInstanceId = appInstances.get(0).getId();
            return getAppConnProjectId(appInstanceId, dssProjectId);
        } else {
            LOGGER.error("the appInstances of AppConn {} is null.", appConnName);
            return null;
        }
    }

    @Override
    public Long getAppConnProjectId(Long appInstanceId, Long dssProjectId) {
        return projectMapper.getAppConnProjectId(appInstanceId, dssProjectId);
    }

    @Override
    public void deleteProject(String username, ProjectDeleteRequest projectDeleteRequest, Workspace workspace, DSSProjectDO dssProjectDO) throws Exception {
        if (dssProjectDO == null) {
            throw new DSSErrorException(600001, "工程不存在!");
        }
        LOGGER.warn("user {} begins to delete project {} in workspace {}.", username, dssProjectDO.getName(), workspace.getWorkspaceName());
        if (!dssProjectDO.getUsername().equalsIgnoreCase(username)) {
            throw new DSSErrorException(600002, "刪除工程失敗，沒有删除权限!");
        }
        if (projectDeleteRequest.isIfDelOtherSys()) {
            LOGGER.warn("User {} requires to delete all projects with name {} in third-party AppConns.", username, dssProjectDO.getName());
            Map<AppInstance, Long> appInstanceToRefProjectId = new HashMap<>(10);
            tryProjectOperation((appConn, appInstance) -> {
                        Long refProjectId = getAppConnProjectId(appInstance.getId(), projectDeleteRequest.getId());
                        if (refProjectId == null) {
                            LOGGER.warn("delete project {} for third-party AppConn {} is ignored, appInstance is {}. Caused by: the refProjectId is null.",
                                    dssProjectDO.getName(), appConn.getAppDesc().getAppName(), appInstance.getBaseUrl());
                            return false;
                        } else {
                            appInstanceToRefProjectId.put(appInstance, refProjectId);
                            return true;
                        }
                    }, workspace, ProjectService::getProjectDeletionOperation,
                    null,
                    (appInstance, refProjectContentRequestRef) -> refProjectContentRequestRef.setProjectName(dssProjectDO.getName())
                            .setRefProjectId(appInstanceToRefProjectId.get(appInstance)).setUserName(username),
                    (structureOperation, structureRequestRef) -> ((ProjectDeletionOperation) structureOperation).deleteProject((RefProjectContentRequestRef) structureRequestRef),
                    null, "delete refProject " + dssProjectDO.getName());
        }
        projectMapper.deleteProject(projectDeleteRequest.getId());
        // 对于DSS项目进行归档
        archiveGitProject(username, projectDeleteRequest, workspace, dssProjectDO);
        LOGGER.warn("User {} deleted project {}.", username, dssProjectDO.getName());
    }

    private void archiveGitProject(String username, ProjectDeleteRequest projectDeleteRequest, Workspace workspace, DSSProjectDO dssProjectDO) {
        if (dssProjectDO.getAssociateGit() != null && dssProjectDO.getAssociateGit()) {
            Sender gitSender = DSSSenderServiceFactory.getOrCreateServiceInstance().getGitSender();
            Map<String, BmlResource> file = new HashMap<>();
            // 测试数据 key表示项目名、value为项目BmlResource资源
            GitArchiveProjectRequest request1 = new GitArchiveProjectRequest(workspace.getWorkspaceId(), dssProjectDO.getName(), username);
            LOGGER.info("-------=======================begin to archive project: {}=======================-------", dssProjectDO.getName());
            Object ask = gitSender.ask(request1);
            GitArchivePorjectResponse responseWorkflowValidNode = RpcAskUtils.processAskException(ask, GitArchivePorjectResponse.class, GitArchiveProjectRequest.class);
            LOGGER.info("-------=======================End to archive project: {}=======================-------: {}", dssProjectDO.getName(), responseWorkflowValidNode);
        }
    }

    private Map<String, GitUserEntity> getProjectGitUserInfo(String username, Long workspaceId) {
        Sender gitSender = DSSSenderServiceFactory.getOrCreateServiceInstance().getGitSender();
        Map<String, BmlResource> file = new HashMap<>();
        // 测试数据 key表示项目名、value为项目BmlResource资源
        GitUserByWorkspaceIdRequest request1 = new GitUserByWorkspaceIdRequest();
        request1.setWorkspaceId(workspaceId);
        request1.setUsername(username);
        Object ask = gitSender.ask(request1);
        GitUserByWorkspaceResponse workspaceResponse = RpcAskUtils.processAskException(ask, GitUserByWorkspaceResponse.class, GitUserByWorkspaceIdRequest.class);

        return workspaceResponse.getMap();
    }

    @Override
    public List<String> getProjectAbilities(String username) {
        LOGGER.info("{} begins to get project ability", username);
        return Arrays.asList(SUPPORT_ABILITY.trim().split(","));
    }

    @Override
    public boolean isDeleteProjectAuth(Long projectId, String username) throws DSSProjectErrorException {
        //校验当前登录用户是否含有删除权限，默认创建用户可以删除工程
        QueryWrapper<DSSProjectDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", projectId);
        queryWrapper.eq("create_by", username);
        long count = projectMapper.selectCount(queryWrapper);
        if (count == 0) {
            DSSExceptionUtils.dealErrorException(ProjectServerResponse.PROJECT_NOT_EDIT_AUTH.getCode(), ProjectServerResponse.PROJECT_NOT_EDIT_AUTH.getMsg(), DSSProjectErrorException.class);
        }
        return true;
    }

    private boolean isWorkspaceAdmin(Long workspaceId, String username) {
        return !projectUserMapper.getUserWorkspaceAdminRole(workspaceId, username).isEmpty();
    }

    @Override
    public List<ProjectResponse> getDeletedProjects(ProjectQueryRequest projectRequest) {
        //根据dss_project、dss_project_user查询出所在空间登录用户相关的工程,再删选出其中是已删除的项目
        List<QueryProjectVo> list = projectMapper.getDeletedProjects(projectRequest);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        List<ProjectResponse> projectResponseList = new ArrayList<>();
        for (QueryProjectVo projectVo : list) {
            ProjectResponse projectResponse = new ProjectResponse();
            projectResponse.setApplicationArea(projectVo.getApplicationArea());
            projectResponse.setId(projectVo.getId());
            projectResponse.setBusiness(projectVo.getBusiness());
            projectResponse.setCreateBy(projectVo.getCreateBy());
            projectResponse.setDescription(projectVo.getDescription());
            projectResponse.setName(projectVo.getName());
            projectResponse.setProduct(projectVo.getProduct());
            projectResponse.setSource(projectVo.getSource());
            projectResponse.setArchive(projectVo.getArchive());
            projectResponse.setCreateTime(projectVo.getCreateTime());
            projectResponse.setUpdateTime(projectVo.getUpdateTime());
            projectResponse.setDevProcessList(ProjectStringUtils.convertList(projectVo.getDevProcess()));
            projectResponse.setOrchestratorModeList(ProjectStringUtils.convertList(projectVo.getOrchestratorMode()));
            if (projectVo.getDataSourceListJson() != null) {
                List<DSSProjectDataSource> dataSourceList = new Gson().fromJson(projectVo.getDataSourceListJson(),
                        new TypeToken<List<DSSProjectDataSource>>() {
                        }.getType());
                projectResponse.setDataSourceList(dataSourceList);
            }
            projectResponseList.add(projectResponse);
            /**
             * 拆分有projectId +"-" + priv + "-" + username的拼接而成的字段，
             * 从而得到：查看权限用户、编辑权限用户、发布权限用户
             */
            String pusername = projectVo.getPusername();
            if (StringUtils.isEmpty(pusername)) {
                continue;
            }
            Map<String, List<String>> userPricMap = new HashMap<>();
            String[] tempstrArr = pusername.split(MODE_SPLIT);

            for (String s : tempstrArr) {
                String[] strArr = s.split(KEY_SPLIT);
                String key = strArr[0] + KEY_SPLIT + strArr[1];
                userPricMap.computeIfAbsent(key, k -> new ArrayList<>());
                userPricMap.get(key).add(strArr[2]);
            }
            List<String> accessUsers = userPricMap.get(projectVo.getId() + KEY_SPLIT + ProjectUserPrivEnum.PRIV_ACCESS.getRank());
            List<String> editUsers = userPricMap.get(projectVo.getId() + KEY_SPLIT + ProjectUserPrivEnum.PRIV_EDIT.getRank());
            List<String> releaseUsers = userPricMap.get(projectVo.getId() + KEY_SPLIT + ProjectUserPrivEnum.PRIV_RELEASE.getRank());
            projectResponse.setAccessUsers(CollectionUtils.isEmpty(accessUsers) ? new ArrayList<>() : accessUsers.stream().distinct().collect(Collectors.toList()));
            projectResponse.setEditUsers(CollectionUtils.isEmpty(editUsers) ? new ArrayList<>() : editUsers.stream().distinct().collect(Collectors.toList()));
            projectResponse.setReleaseUsers(CollectionUtils.isEmpty(releaseUsers) ? new ArrayList<>() : releaseUsers.stream().distinct().collect(Collectors.toList()));
        }
        return projectResponseList;
    }

    @Override
    public BmlResource exportProject(ExportAllOrchestratorsReqest exportAllOrchestratorsReqest,
                                     String username, String proxyUser, Workspace workspace) throws Exception {
        Long projectId = exportAllOrchestratorsReqest.getProjectId();
        EnvDSSLabel envLabel = new EnvDSSLabel(exportAllOrchestratorsReqest.getLabels());
        OrchestratorRequest orchestratorRequest = new OrchestratorRequest(workspace.getWorkspaceId(), exportAllOrchestratorsReqest.getProjectId());
        LabelRouteVO labelRouteVO = new LabelRouteVO();
        labelRouteVO.setRoute(exportAllOrchestratorsReqest.getLabels());
        orchestratorRequest.setLabels(labelRouteVO);
        List<OrchestratorBaseInfo> orchestrators = orchestratorService.getOrchestratorInfos(orchestratorRequest, username);
        DSSProjectDO projectDO = projectService.getProjectById(projectId);
        String projectName = projectDO.getName();
        String projectPath = exportService.batchExport(username, projectId, orchestrators, projectName, envLabel, workspace);
        exportProjectMeta(projectDO, projectPath);
        String zipFile = ZipHelper.zip(projectPath, true);
        LOGGER.info("export project file locate at {}", zipFile);
        //先上传
        InputStream inputStream = bmlService.readLocalResourceFile(username, zipFile);
        BmlResource bmlResource = bmlService.upload(username, inputStream, projectName + ".OrcsExport", projectName);

        LOGGER.info("export zip file upload to bmlResourceId:{} bmlResourceVersion:{}",
                bmlResource.getResourceId(), bmlResource.getVersion());
        FileUtils.deleteQuietly(new File(zipFile));
        return bmlResource;

    }

    @Override
    public void importProject(ProjectInfoVo projectInfo, BmlResource importResource, String username,
                              String checkCode, String packageInfo, EnvDSSLabel envLabel, Workspace workspace) throws Exception {
        String projectName = projectInfo.getProjectName();
        //下载到本地处理
        String importSaveBasePath = IoUtils.generateTempIOPath(username);
        String importFile = importSaveBasePath + File.separator + projectName + ".zip";
        LOGGER.info("import zip file locate at {}", importFile);
        //下载压缩包
        bmlService.downloadToLocalPath(username, importResource.getResourceId(), importResource.getVersion(), importFile);
        try {
            String originProjectName = ZipHelper.readImportZipProjectName(importFile);
            if (!projectName.equals(originProjectName)) {
                String msg = String.format("target project name must be same with origin project name.origin project name:%s,target project name:%s(导入的目标工程名必须与导出时源工程名保持一致。源工程名：%s，目标工程名：%s)"
                        , originProjectName, projectName, originProjectName, projectName);
                throw new DSSRuntimeException(msg);
            }
        } catch (IOException e) {
            throw new DSSRuntimeException("upload file format error(导入包格式错误)");
        }
        //解压
        ZipHelper.unzipFile(importFile, importSaveBasePath, true);
        String projectPath = IoUtils.addFileSeparator(importSaveBasePath, projectName);
        importService.batchImportOrc(username, projectInfo.getId(),
                projectInfo.getProjectName(), projectPath, checkCode, envLabel, workspace);


    }

    /**
     * 导出项目元数据
     *
     * @param projectDO   项目元数据
     * @param projectPath 项目目录
     * @throws IOException
     */
    private void exportProjectMeta(DSSProjectDO projectDO, String projectPath) throws Exception {
        String path = projectPath + File.separator + PROJECT_META_FILE_NAME;
        File projectMetaFile = new File(path);
        if (!projectMetaFile.exists()) {
            try {
                Path filePath = Paths.get(path);
                // 确保父级路径存在
                Files.createDirectories(filePath.getParent());
                // 创建文件
                Files.createFile(filePath);
                LOGGER.info("create success");
            } catch (Exception e) {
                LOGGER.error("create File {} failed, the reason is : {}", path, e);
                throw new Exception("create File failed");
            }
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        // 文件不存在，直接创建并写入orchestratorInfo信息
        try (FileWriter writer = new FileWriter(projectMetaFile)) {
            gson.toJson(projectDO, writer);
        }

    }

    @Override
    public BmlResource exportOnlyProjectMeta(ExportAllOrchestratorsReqest exportAllOrchestratorsReqest,
                                             String username, String proxyUser, Workspace workspace) throws Exception {
        Long projectId = exportAllOrchestratorsReqest.getProjectId();
        EnvDSSLabel envLabel = new EnvDSSLabel(exportAllOrchestratorsReqest.getLabels());
        OrchestratorRequest orchestratorRequest = new OrchestratorRequest(workspace.getWorkspaceId(), exportAllOrchestratorsReqest.getProjectId());
        LabelRouteVO labelRouteVO = new LabelRouteVO();
        labelRouteVO.setRoute(exportAllOrchestratorsReqest.getLabels());
        orchestratorRequest.setLabels(labelRouteVO);
        List<OrchestratorBaseInfo> orchestrators = orchestratorService.getOrchestratorInfos(orchestratorRequest, username);
        DSSProjectDO projectDO = projectService.getProjectById(projectId);
        String projectName = projectDO.getName();
        String exportSaveBasePath = IoUtils.generateTempIOPath(username);
        String projectPath = IoUtils.addFileSeparator(exportSaveBasePath, projectName);
        exportProjectMeta(projectDO, projectPath);
        String zipFile = ZipHelper.zip(projectPath, true);
        LOGGER.info("export project file locate at {}", zipFile);
        //先上传
        InputStream inputStream = bmlService.readLocalResourceFile(username, zipFile);
        BmlResource bmlResource = bmlService.upload(username, inputStream, projectName + ".OrcsExport", projectName);

        LOGGER.info("export zip file upload to bmlResourceId:{} bmlResourceVersion:{}",
                bmlResource.getResourceId(), bmlResource.getVersion());
        FileUtils.deleteQuietly(new File(zipFile));
        return bmlResource;
    }


    @Override
    public DSSProjectDO modifyProjectMeta(String username, ProjectModifyRequest projectModifyRequest) throws DSSProjectErrorException {

        DSSProjectDO project = new DSSProjectDO();
        //修改的字段
        project.setDescription(projectModifyRequest.getDescription());
        project.setUpdateTime(new Date());
        project.setUpdateByStr(username);

        UpdateWrapper<DSSProjectDO> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", projectModifyRequest.getId());
        updateWrapper.eq("workspace_id", projectModifyRequest.getWorkspaceId());
        projectMapper.update(project, updateWrapper);

        return project;
    }

    @Override
    public List<ProjectResponse> queryListByParam(ProjectQueryRequest projectRequest, List<Long> totals) {

        //判断工作空间是否设置了管理员能否查看该工作空间下所有项目的权限
        Integer workspaceAdminPermission = projectUserMapper.getWorkspaceAdminPermission(projectRequest.getWorkspaceId());

        if (!isWorkspaceAdmin(projectRequest.getWorkspaceId(), projectRequest.getUsername()) || workspaceAdminPermission != 1) {
            projectRequest.setQueryUser(projectRequest.getUsername());
        }

        List<Integer> projectIdList = new ArrayList<>();
        // 根据传入的查看用户获取 项目ID 取交集
        if(!CollectionUtils.isEmpty(projectRequest.getAccessUsers())){
            intersectionProjectId(projectIdList,projectRequest.getAccessUsers(),ProjectUserPrivEnum.PRIV_ACCESS.getRank());
        }
        // 根据传入的编辑用户获取 项目ID 取交集
        if (!CollectionUtils.isEmpty(projectRequest.getEditUsers())) {
            intersectionProjectId(projectIdList,projectRequest.getEditUsers(),ProjectUserPrivEnum.PRIV_EDIT.getRank());
        }
        // 根据传入的发布用户获取项目ID 取交集
        if (!CollectionUtils.isEmpty(projectRequest.getReleaseUsers())) {
            intersectionProjectId(projectIdList,projectRequest.getReleaseUsers(),ProjectUserPrivEnum.PRIV_RELEASE.getRank());
        }
        // 去重
        projectIdList = projectIdList.stream().distinct().collect(Collectors.toList());

        //根据dss_project、dss_project_user查询出所在空间登录用户相关的工程
        if (StringUtils.isEmpty(projectRequest.getOrderBySql())) {
            projectRequest.setOrderBySql("updateTime desc");
        }
        LOGGER.info("queryListByParam order by sql is {} ", projectRequest.getOrderBySql());
        projectRequest.setProjectIdList(projectIdList);
        PageHelper.startPage(projectRequest.getPageNow(), projectRequest.getPageSize(), projectRequest.getOrderBySql());
        List<QueryProjectVo> list = projectMapper.queryListByParam(projectRequest);
        if (CollectionUtils.isEmpty(list)) {
            totals.add(0L);
            return new ArrayList<>();
        }

        PageInfo<QueryProjectVo> pageInfo = new PageInfo<QueryProjectVo>(list);
        totals.add(pageInfo.getTotal());
        List<ProjectResponse> projectResponseList = new ArrayList<>();

        for (QueryProjectVo projectVo : list) {

            ProjectResponse projectResponse = new ProjectResponse();
            projectResponse.setApplicationArea(projectVo.getApplicationArea());
            projectResponse.setId(projectVo.getId());
            projectResponse.setBusiness(projectVo.getBusiness());
            projectResponse.setCreateBy(projectVo.getCreateBy());
            projectResponse.setDescription(projectVo.getDescription());
            projectResponse.setName(projectVo.getName());
            projectResponse.setProduct(projectVo.getProduct());
            projectResponse.setSource(projectVo.getSource());
            projectResponse.setArchive(projectVo.getArchive());
            projectResponse.setCreateTime(projectVo.getCreateTime());
            projectResponse.setUpdateTime(projectVo.getUpdateTime());
            projectResponse.setDevProcessList(ProjectStringUtils.convertList(projectVo.getDevProcess()));
            projectResponse.setOrchestratorModeList(ProjectStringUtils.convertList(projectVo.getOrchestratorMode()));
            projectResponse.setWorkspaceName(projectVo.getWorkspaceName());

            if (projectVo.getDataSourceListJson() != null) {
                List<DSSProjectDataSource> dataSourceList = new Gson().fromJson(projectVo.getDataSourceListJson(),
                        new TypeToken<List<DSSProjectDataSource>>() {
                        }.getType());
                projectResponse.setDataSourceList(dataSourceList);
            }

            List<String> accessUsers = StringUtils.isBlank(projectVo.getAccessUsers()) ? new ArrayList<>()
                    : Arrays.asList(projectVo.getAccessUsers().split(MODE_SPLIT));
            List<String> editUsers = StringUtils.isBlank(projectVo.getEditUsers()) ? new ArrayList<>()
                    : Arrays.asList(projectVo.getEditUsers().split(MODE_SPLIT));
            List<String> releaseUsers = StringUtils.isBlank(projectVo.getReleaseUsers()) ? new ArrayList<>()
                    : Arrays.asList(projectVo.getReleaseUsers().split(MODE_SPLIT));

            projectResponse.setAccessUsers(accessUsers.stream().distinct().collect(Collectors.toList()));
            projectResponse.setEditUsers(editUsers.stream().distinct().collect(Collectors.toList()));
            projectResponse.setReleaseUsers(releaseUsers.stream().distinct().collect(Collectors.toList()));

            // 用户是否具有编辑权限  编辑权限和创建者都有
            if (editUsers.contains(projectRequest.getUsername())
                    || projectVo.getCreateBy().equals(projectRequest.getUsername())
                    || isWorkspaceAdmin(projectRequest.getWorkspaceId(), projectRequest.getUsername())
                    || projectResponse.getReleaseUsers().contains(projectRequest.getUsername())
            ) {
                projectResponse.setEditable(true);
            } else {
                projectResponse.setEditable(false);
            }

            projectResponseList.add(projectResponse);
        }

        return projectResponseList;

    }


    public void intersectionProjectId(List<Integer> projectIdList,List<String> users,Integer rank){

        List<Integer> projectIds = projectMapper.getProjectIdByUser(rank, users);

        if(CollectionUtils.isEmpty(projectIds)){
            return;
        }

        if(CollectionUtils.isEmpty(projectIdList)){
            projectIdList.addAll(projectIds);
        }else{
            projectIdList.retainAll(projectIds);
        }

    }


}
