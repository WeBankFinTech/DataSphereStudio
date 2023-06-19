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
import com.webank.wedatasphere.dss.common.constant.project.ProjectUserPrivEnum;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.framework.project.contant.ProjectServerResponse;
import com.webank.wedatasphere.dss.framework.project.dao.DSSProjectMapper;
import com.webank.wedatasphere.dss.framework.project.dao.DSSProjectUserMapper;
import com.webank.wedatasphere.dss.framework.project.entity.DSSProjectDO;
import com.webank.wedatasphere.dss.framework.project.entity.DSSProjectUser;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectCreateRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectModifyRequest;
import com.webank.wedatasphere.dss.framework.project.exception.DSSProjectErrorException;
import com.webank.wedatasphere.dss.common.service.BMLService;
import com.webank.wedatasphere.dss.framework.project.service.DSSProjectUserService;
import com.webank.wedatasphere.dss.framework.project.utils.ProjectUserUtils;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DSSProjectUserServiceImpl implements DSSProjectUserService {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private DSSProjectUserMapper projectUserMapper;
    @Autowired
    @Qualifier("projectBmlService")
    private BMLService bmlService;
    @Autowired
    private DSSProjectMapper dssProjectMapper;

    /**
     * 是否有修改工程权限
     *
     * @param projectId
     * @param username
     * @return
     */
    @Override
    public boolean isEditProjectAuth(Long projectId, String username) throws DSSProjectErrorException {
        //校验当前登录用户是否含有修改权限
        QueryWrapper<DSSProjectUser> queryWrapper = new QueryWrapper<DSSProjectUser>();
        queryWrapper.eq("project_id", projectId);
        queryWrapper.eq("username", username);
        queryWrapper.ge("priv", ProjectUserPrivEnum.PRIV_EDIT.getRank());//编辑权限
        long count = projectUserMapper.selectCount(queryWrapper);
        if (count == 0 && !isProjectOwner(projectId, username)) {
            DSSExceptionUtils.dealErrorException(ProjectServerResponse.PROJECT_NOT_EDIT_AUTH.getCode(), ProjectServerResponse.PROJECT_NOT_EDIT_AUTH.getMsg(), DSSProjectErrorException.class);
        }
        return true;
    }

    /**
     * 保存工程与用户关系
     *
     * @param projectID
     * @param request
     */
    @Override
    public void saveProjectUser(Long projectID, String username, ProjectCreateRequest request, Workspace workspace) throws Exception {
        //將创建人默认为发布权限
        List<String> releaseUsers = request.getReleaseUsers();
        //批量保存
        saveBatch(request.getWorkspaceId(), projectID, releaseUsers, request.getEditUsers(), request.getAccessUsers());
        //获取所有编辑权限的用户
        List<String> sumEditUsers = ProjectUserUtils.getEditUserList(releaseUsers, request.getEditUsers());
        //去bml建一个工程
        bmlService.createBmlProject(username, request.getName(), sumEditUsers, request.getAccessUsers());
    }

    /**
     * 修改工程与用户关系
     *
     * @param dbProject
     * @param modifyRequest
     */
    @Override
    public void modifyProjectUser(DSSProjectDO dbProject, ProjectModifyRequest modifyRequest, String loginuser, Workspace workspace) throws Exception {
        projectUserMapper.deleteAllPriv(dbProject.getId());
        //將创建人默认为发布权限
        List<String> releaseUsers = modifyRequest.getReleaseUsers();
        String username = dbProject.getUsername();
        //批量保存
        saveBatch(modifyRequest.getWorkspaceId(), dbProject.getId(), releaseUsers, modifyRequest.getEditUsers(), modifyRequest.getAccessUsers());

        //获取所有编辑权限的用户
        List<String> sumEditUsers = ProjectUserUtils.getEditUserList(releaseUsers, modifyRequest.getEditUsers());
        //更新底层的权限
        bmlService.updateProjectPriv(dbProject.getName(), username, sumEditUsers, modifyRequest.getAccessUsers());
    }


    @Override
    public List<DSSProjectUser> getListByParam(Long workspaceId, String username) {
        QueryWrapper<DSSProjectUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("workspace_id", workspaceId);
        queryWrapper.eq("username", username);
        queryWrapper.ge("priv", ProjectUserPrivEnum.PRIV_ACCESS.getRank());
        List<DSSProjectUser> DSSProjectUserList = projectUserMapper.selectList(queryWrapper);
        return DSSProjectUserList;
    }

    //批量保存
    public void saveBatch(Long workspaceId, Long projectID, List<String> releaseUsers, List<String> editUsers, List<String> accessUsers) {
        List<String> realReleaseUsers = releaseUsers.stream().distinct().collect(Collectors.toList());
        List<String> realEditUsers = editUsers.stream().distinct().collect(Collectors.toList());
        List<String> realAccessUsers = accessUsers.stream().distinct().collect(Collectors.toList());
        List<DSSProjectUser> addList = new ArrayList<>();
        addList.addAll(ProjectUserUtils.createPUser(workspaceId, projectID, realReleaseUsers, ProjectUserPrivEnum.PRIV_RELEASE.getRank()));
        addList.addAll(ProjectUserUtils.createPUser(workspaceId, projectID, realEditUsers, ProjectUserPrivEnum.PRIV_EDIT.getRank()));
        addList.addAll(ProjectUserUtils.createPUser(workspaceId, projectID, realAccessUsers, ProjectUserPrivEnum.PRIV_ACCESS.getRank()));
        if (CollectionUtils.isEmpty(addList)) {
            return;
        }
        //分批插入
        List<DSSProjectUser> tempAddList = new ArrayList<>();
        for (int i = 0; i < addList.size(); i++) {
            tempAddList.add(addList.get(i));
            if (i > 0 && i % 200 == 0) {
                projectUserMapper.insertBatchProjectUser(tempAddList);
                tempAddList.clear();
            }
        }
        if (tempAddList.size() > 0) {
            projectUserMapper.insertBatchProjectUser(tempAddList);
        }
    }

    @Override
    public boolean isAdminByUsername(Long workspaceId, String username) {
        //管理员ID，这里写死
        int roleId = 1;
        return projectUserMapper.isAdminByUsername(workspaceId, username, roleId).longValue() > 0;
    }

    /**
     * 根据用户名和工程id获取工程权限
     *
     * @param projectId
     * @param username
     * @return
     */
    @Override
    public List<DSSProjectUser> getProjectUserPriv(Long projectId, String username) {
        QueryWrapper<DSSProjectUser> queryWrapper = new QueryWrapper<DSSProjectUser>();
        queryWrapper.eq("project_id", projectId);
        queryWrapper.eq("username", username);
        return projectUserMapper.selectList(queryWrapper);
    }

    @Override
    public List<DSSProjectUser> getProjectPriv(Long projectId) {
        return projectUserMapper.getPrivsByProjectId(projectId);
    }

    private boolean isProjectOwner(Long projectId, String username) {
        QueryWrapper<DSSProjectDO> queryWrapper = new QueryWrapper<DSSProjectDO>();
        queryWrapper.eq("id", projectId);
        queryWrapper.eq("create_by", username);
        return dssProjectMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public List<DSSProjectUser> listByPriv(Long projectId, ProjectUserPrivEnum privEnum) {
        QueryWrapper<DSSProjectUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("username").eq("project_id", projectId).ge("priv", privEnum.getRank());
        return projectUserMapper.selectList(queryWrapper);
    }


    @Override
    public boolean isWorkspaceUser(Long workspaceId, String username) {
        return projectUserMapper.isWorkspaceUser(workspaceId, username).longValue() > 0;
    }

}
