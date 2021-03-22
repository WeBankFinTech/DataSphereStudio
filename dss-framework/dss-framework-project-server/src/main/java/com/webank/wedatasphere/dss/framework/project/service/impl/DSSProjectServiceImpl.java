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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.common.entity.DSSLabel;
import com.webank.wedatasphere.dss.framework.appconn.service.AppConnService;
import com.webank.wedatasphere.dss.framework.project.conf.ProjectConf;
import com.webank.wedatasphere.dss.framework.project.contant.ProjectUserPrivEnum;
import com.webank.wedatasphere.dss.framework.project.dao.DSSProjectMapper;
import com.webank.wedatasphere.dss.framework.project.entity.DSSProject;
import com.webank.wedatasphere.dss.framework.project.entity.po.ProjectRelationPo;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectCreateRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectDeleteRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectModifyRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectQueryRequest;
import com.webank.wedatasphere.dss.framework.project.entity.vo.ProjectInfoVo;
import com.webank.wedatasphere.dss.framework.project.entity.vo.QueryProjectVo;
import com.webank.wedatasphere.dss.framework.project.entity.response.ProjectResponse;
import com.webank.wedatasphere.dss.framework.project.exception.DSSProjectErrorException;
import com.webank.wedatasphere.dss.framework.project.service.DSSProjectService;
import com.webank.wedatasphere.dss.framework.project.service.DSSProjectUserService;
import com.webank.wedatasphere.dss.framework.project.utils.ProjectStringUtils;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author v_wbzwchen
 * @since 2020-12-17
 */
@Component
public class DSSProjectServiceImpl extends ServiceImpl<DSSProjectMapper, DSSProject> implements DSSProjectService {


    private static final Logger LOGGER = LoggerFactory.getLogger(DSSProjectServiceImpl.class);

    @Autowired
    private DSSProjectMapper projectMapper;
    @Autowired
    private DSSProjectUserService projectUserService;

    @Autowired
    private AppConnService appConnService;


    public static final String MODE_SPLIT = ",";
    public static final String KEY_SPLIT = "-";

    private final String SUPPORT_ABILITY = ProjectConf.SUPPORT_ABILITY.getValue();

    @Override
    public DSSProject createProject(String username, ProjectCreateRequest projectCreateRequest) {
        DSSProject project = new DSSProject();
        project.setName(projectCreateRequest.getName());
        project.setWorkspaceId(projectCreateRequest.getWorkspaceId());
        project.setCreateBy(username);
        project.setCreateTime(new Date());
        project.setBusiness(projectCreateRequest.getBusiness());
        project.setProduct(projectCreateRequest.getProduct());
        project.setUpdateTime(new Date());
        project.setDescription(projectCreateRequest.getDescription());
        project.setApplicationArea(projectCreateRequest.getApplicationArea());
        //开发流程，编排模式组拼接 前后进行英文逗号接口
        project.setDevProcess(ProjectStringUtils.getModeStr(projectCreateRequest.getDevProcessList()));
        project.setOrchestratorMode(ProjectStringUtils.getModeStr(projectCreateRequest.getOrchestratorModeList()));
        projectMapper.insert(project);
        return project;
    }

    //修改dss_project工程字段
    @Override
    public void modifyProject(String username, ProjectModifyRequest projectModifyRequest) throws DSSProjectErrorException {
        //校验当前登录用户是否含有修改权限
        projectUserService.isEditProjectAuth(projectModifyRequest.getId(), username);
        DSSProject project = new DSSProject();
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

        UpdateWrapper<DSSProject> updateWrapper = new UpdateWrapper<DSSProject>();
        updateWrapper.eq("id", projectModifyRequest.getId());
        updateWrapper.eq("workspace_id", projectModifyRequest.getWorkspaceId());
        projectMapper.update(project, updateWrapper);
    }

    @Override
    public DSSProject getProjectByName(String name) {
        QueryWrapper<DSSProject> projectQueryWrapper = new QueryWrapper<DSSProject>();
        projectQueryWrapper.eq("name", name);
        List<DSSProject> projectList = projectMapper.selectList(projectQueryWrapper);
        return CollectionUtils.isEmpty(projectList) ? null : projectList.get(0);
    }

    @Override
    public DSSProject getProjectById(Long id) {
        return projectMapper.selectById(id);
    }

    @Override
    public List<ProjectResponse> getListByParam(ProjectQueryRequest projectRequest) {
        //根据dss_project、dss_project_user查询出所在空间登录用户相关的工程
        List<QueryProjectVo> list = projectMapper.getListByParam(projectRequest);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }

        List<ProjectResponse> projectResponseList = new ArrayList<>();
        ProjectResponse projectResponse = null;
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
            projectResponse.setDevProcessList(ProjectStringUtils.convertList(projectVo.getDevProcess()));
            projectResponse.setOrchestratorModeList(ProjectStringUtils.convertList(projectVo.getOrchestratorMode()));
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
    public ProjectInfoVo getProjectInfoById(Long id) {
        return projectMapper.getProjectInfoById(id);
    }


    @Override
    public void saveProjectRelation(DSSProject project, Map<AppInstance, Long> projectMap) {
        List<ProjectRelationPo> relationPos = new ArrayList<>();
        projectMap.forEach((k, v) -> {
            relationPos.add(new ProjectRelationPo(project.getId(), k.getId(), v));
        });
        projectMapper.saveProjectRelation(relationPos);
    }


    @Override
    public Long getAppConnProjectId(Long dssProjectId, String appConnName, List<DSSLabel> dssLabels) throws Exception {
        AppConn appConn = appConnService.getAppConn(appConnName);
        List<AppInstance> appInstances = appConn.getAppDesc().getAppInstancesByLabels(dssLabels);
        if (appInstances.get(0) != null) {
            Long appInstanceId = appInstances.get(0).getId();
            return projectMapper.getAppConnProjectId(appInstanceId, dssProjectId);
        } else {
            LOGGER.error("appInstances is null {}", appInstances);
            return null;
        }
    }

    @Override
    public void deleteProject(String username, ProjectDeleteRequest projectDeleteRequest) {
        LOGGER.warn("user {} begins to delete project {}", username, projectDeleteRequest);
        //1.check权限
        //2.进行屏蔽
        //todo 进行真实的删除
        projectMapper.deleteProject(projectDeleteRequest.getId());
        LOGGER.warn("user {} deleted project {}", username, projectDeleteRequest);
    }

    @Override
    public List<String> getProjectAbilities(String username) {
        LOGGER.info("{} begins to get project ability", username);
        return Arrays.asList(SUPPORT_ABILITY.trim().split(","));
    }
}
