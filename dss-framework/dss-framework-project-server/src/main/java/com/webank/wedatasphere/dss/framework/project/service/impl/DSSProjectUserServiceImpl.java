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
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.framework.project.contant.ProjectServerResponse;
import com.webank.wedatasphere.dss.framework.project.contant.ProjectUserPrivEnum;
import com.webank.wedatasphere.dss.framework.project.dao.DSSProjectUserMapper;
import com.webank.wedatasphere.dss.framework.project.entity.DSSProject;
import com.webank.wedatasphere.dss.framework.project.entity.DSSProjectUser;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectCreateRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectModifyRequest;
import com.webank.wedatasphere.dss.framework.project.exception.DSSProjectErrorException;
import com.webank.wedatasphere.dss.framework.project.server.service.BMLService;
import com.webank.wedatasphere.dss.framework.project.service.DSSProjectUserService;
import com.webank.wedatasphere.dss.framework.project.utils.ProjectUserUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author v_wbzwchen
 * @since 2020-12-17
 */
@Component
public class DSSProjectUserServiceImpl implements DSSProjectUserService {

    @Autowired
    private DSSProjectUserMapper  projectUserMapper;
    @Autowired
    @Qualifier("projectServerBMLService")
    private BMLService bmlService;

    /**
     * 是否有修改工程权限
     * @param projectId
     * @param username
     * @return
     */
    @Override
    public boolean isEditProjectAuth(Long projectId, String username) throws DSSProjectErrorException {
        //校验当前登录用户是否含有修改权限
        QueryWrapper<DSSProjectUser> queryWrapper = new QueryWrapper<DSSProjectUser>();
        queryWrapper.eq("project_id",projectId);
        queryWrapper.eq("username",username);
        queryWrapper.ge("priv",ProjectUserPrivEnum.PRIV_EDIT.getRank());//编辑权限
        long count = projectUserMapper.selectCount(queryWrapper);
        if(count == 0){
            DSSExceptionUtils.dealErrorException(ProjectServerResponse.PROJECT_NOT_EDIT_AUTH.getCode(),ProjectServerResponse.PROJECT_NOT_EDIT_AUTH.getMsg(), DSSProjectErrorException.class);
        }
        return true;
    }
    /**
     * 根据用户名和工程id获取工程权限
     * @param projectId
     * @param username
     * @return
     */
    @Override
    public List<DSSProjectUser> getEditProjectList(Long projectId, String username){
        QueryWrapper<DSSProjectUser> queryWrapper = new QueryWrapper<DSSProjectUser>();
        queryWrapper.eq("project_id",projectId);
        queryWrapper.eq("username",username);
        return projectUserMapper.selectList(queryWrapper);
    }

    /**
     * 保存工程与用户关系
     * @param projectID
     * @param request
     */
    @Override
    public void saveProjectUser(Long projectID,String username, ProjectCreateRequest request){
        //將创建人默认为发布权限
        List<String> releaseUsers = request.getReleaseUsers();
        if(releaseUsers==null){
            if(!request.getEditUsers().contains(username)){
                request.getEditUsers().add(username);
            }
            releaseUsers = new ArrayList<>();
        }else{
            if(!releaseUsers.contains(username)){
                releaseUsers.add(username);
            }
        }
        //批量保存
        saveBatch(request.getWorkspaceId(),projectID,releaseUsers,request.getEditUsers(),request.getAccessUsers());

        //获取所有编辑权限的用户
        List<String> sumEditUsers = ProjectUserUtils.getEditUserList(releaseUsers, request.getEditUsers());
        //去bml建一个工程
        bmlService.createBmlProject(username, request.getName(), sumEditUsers, request.getAccessUsers());
    }

    /**
     * 修改工程与用户关系
     * @param dbProject
     * @param modifyRequest
     */
    @Override
    public void modifyProjectUser(DSSProject dbProject, ProjectModifyRequest modifyRequest) {
        projectUserMapper.deleteAllPriv(dbProject.getId());
        //將创建人默认为发布权限
        List<String> releaseUsers = modifyRequest.getReleaseUsers();
        String username = dbProject.getUsername();
        if(releaseUsers==null){
            if(!modifyRequest.getEditUsers().contains(username)){
                modifyRequest.getEditUsers().add(username);
            }
            releaseUsers = new ArrayList<>();
        }else{
            if(!releaseUsers.contains(username)){
                releaseUsers.add(username);
            }
        }
        //批量保存
        saveBatch(modifyRequest.getWorkspaceId(),dbProject.getId(),releaseUsers,modifyRequest.getEditUsers(),modifyRequest.getAccessUsers());

        //获取所有编辑权限的用户
        List<String> sumEditUsers = ProjectUserUtils.getEditUserList(releaseUsers, modifyRequest.getEditUsers());
        //更新底层的权限
        bmlService.updateProjectPriv(dbProject.getName(), username, sumEditUsers, modifyRequest.getAccessUsers());
    }

    @Override
    public List<DSSProjectUser> getListByParam(Long workspaceId, String username) {
        QueryWrapper<DSSProjectUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("workspace_id",workspaceId);
        queryWrapper.eq("username",username);
        queryWrapper.ge("priv",ProjectUserPrivEnum.PRIV_ACCESS.getRank());
        List<DSSProjectUser> dssProjectUserList = projectUserMapper.selectList(queryWrapper);
        return dssProjectUserList;
    }

    //批量保存
    public void saveBatch(Long workspaceId,Long projectID,List<String> releaseUsers,List<String> editUsers,List<String> accessUsers){
        List<String> realReleaseUsers = releaseUsers.stream().distinct().collect(Collectors.toList());
        List<String> realEditUsers = editUsers.stream().distinct().collect(Collectors.toList());
        List<String> realAccessUsers = accessUsers.stream().distinct().collect(Collectors.toList());
        List<DSSProjectUser> addList = new ArrayList<>();
        addList.addAll(ProjectUserUtils.createPUser(workspaceId,projectID,realReleaseUsers,ProjectUserPrivEnum.PRIV_RELEASE.getRank()));
        addList.addAll(ProjectUserUtils.createPUser(workspaceId,projectID,realEditUsers, ProjectUserPrivEnum.PRIV_EDIT.getRank()));
        addList.addAll(ProjectUserUtils.createPUser(workspaceId,projectID,realAccessUsers,ProjectUserPrivEnum.PRIV_ACCESS.getRank()));
        if(CollectionUtils.isEmpty(addList)){
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
}
