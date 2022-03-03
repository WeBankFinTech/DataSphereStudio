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
import com.webank.wedatasphere.dss.framework.common.exception.DSSFrameworkErrorException;
import com.webank.wedatasphere.dss.framework.project.contant.OrchestratorTypeEnum;
import com.webank.wedatasphere.dss.framework.project.contant.ProjectServerResponse;
import com.webank.wedatasphere.dss.framework.project.contant.ProjectUserPrivEnum;
import com.webank.wedatasphere.dss.framework.project.dao.DSSOrchestratorMapper;
import com.webank.wedatasphere.dss.framework.project.dao.DSSProjectMapper;
import com.webank.wedatasphere.dss.framework.project.entity.DSSOrchestrator;
import com.webank.wedatasphere.dss.framework.project.entity.DSSProjectDO;
import com.webank.wedatasphere.dss.framework.project.entity.DSSProjectUser;
import com.webank.wedatasphere.dss.framework.project.entity.request.OrchestratorCreateRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.OrchestratorDeleteRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.OrchestratorModifyRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.OrchestratorRequest;
import com.webank.wedatasphere.dss.framework.project.entity.vo.OrchestratorBaseInfo;
import com.webank.wedatasphere.dss.framework.project.exception.DSSProjectErrorException;
import com.webank.wedatasphere.dss.framework.project.service.DSSOrchestratorService;
import com.webank.wedatasphere.dss.framework.project.service.DSSProjectUserService;
import com.webank.wedatasphere.dss.framework.project.utils.ProjectStringUtils;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.RequestProjectImportOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.RequestProjectUpdateOrcVersion;
import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorCreateResponseRef;
import com.webank.wedatasphere.dss.sender.service.DSSSenderServiceFactory;
import org.apache.linkis.rpc.Sender;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class DSSOrchestratorServiceImpl extends ServiceImpl<DSSOrchestratorMapper, DSSOrchestrator> implements DSSOrchestratorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DSSOrchestratorServiceImpl.class);
    public static final String MODE_SPLIT = ",";
    @Autowired
    private DSSOrchestratorMapper orchestratorMapper;
    @Autowired
    private DSSProjectUserService projectUserService;
    @Autowired
    private DSSProjectMapper dssProjectMapper;

    private final Sender orcSender = DSSSenderServiceFactory.getOrCreateServiceInstance().getScheduleOrcSender();

    /**
     * 保存编排模式
     *
     * @param orchestratorCreateRequest
     * @param responseRef
     * @param username
     * @throws DSSFrameworkErrorException
     * @throws DSSProjectErrorException
     */
    @Override
    public void saveOrchestrator(OrchestratorCreateRequest orchestratorCreateRequest, OrchestratorCreateResponseRef responseRef, String username) throws DSSFrameworkErrorException, DSSProjectErrorException {
        //todo 使用工程的权限关系来限定 校验当前登录用户是否含有修改权限
        projectUserService.isEditProjectAuth(orchestratorCreateRequest.getProjectId(), username);
        DSSOrchestrator orchestrator = new DSSOrchestrator();
        orchestrator.setWorkspaceId(orchestratorCreateRequest.getWorkspaceId());
        orchestrator.setProjectId(orchestratorCreateRequest.getProjectId());
        //编排模式id（工作流,调用orchestrator服务返回的orchestratorId）
        orchestrator.setOrchestratorId(responseRef.getOrcId());
        //编排模式版本id（工作流,调用orchestrator服务返回的orchestratorVersionId）
        orchestrator.setOrchestratorVersionId(responseRef.getOrchestratorVersionId());
        //编排模式-名称
        orchestrator.setOrchestratorName(orchestratorCreateRequest.getOrchestratorName());
        //编排模式-类型
        orchestrator.setOrchestratorMode(orchestratorCreateRequest.getOrchestratorMode());
        //编排模式-方式
        orchestrator.setOrchestratorWay(ProjectStringUtils.getModeStr(orchestratorCreateRequest.getOrchestratorWays()));
        //编排模式-用途
        orchestrator.setUses(orchestratorCreateRequest.getUses());
        //编排模式-描述
        orchestrator.setDescription(orchestratorCreateRequest.getDescription());
        //编排模式-创建人
        orchestrator.setCreateUser(username);
        orchestrator.setCreateTime(new Date());
        orchestrator.setUpdateTime(new Date());
        this.save(orchestrator);
    }

    /**
     * 更新编排模式
     *
     * @param orchestratorModifRequest
     * @param username
     * @throws DSSFrameworkErrorException
     * @throws DSSProjectErrorException
     */
    @Override
    public void updateOrchestrator(OrchestratorModifyRequest orchestratorModifRequest, String username) throws DSSFrameworkErrorException, DSSProjectErrorException {
        //todo 使用工程的权限关系来限定 校验当前登录用户是否含有修改权限
        projectUserService.isEditProjectAuth(orchestratorModifRequest.getProjectId(), username);

        DSSOrchestrator orchestrator = new DSSOrchestrator();
        orchestrator.setId(orchestratorModifRequest.getId());
        //编排模式-名称
        orchestrator.setOrchestratorName(orchestratorModifRequest.getOrchestratorName());
        //编排模式
        orchestrator.setOrchestratorMode(orchestratorModifRequest.getOrchestratorMode());
        //编排模式-方式
        orchestrator.setOrchestratorWay(ProjectStringUtils.getModeStr(orchestratorModifRequest.getOrchestratorWays()));
        //编排模式-用途
        orchestrator.setUses(orchestratorModifRequest.getUses());
        //编排模式-描述
        orchestrator.setDescription(orchestratorModifRequest.getDescription());
        //编排模式-更新人
        orchestrator.setUpdateUser(username);
        //编排模式-更新时间
        orchestrator.setUpdateTime(new Date());
        this.updateById(orchestrator);
    }

    //新建前是否存在相同的编排名称
    @Override
    public void isExistSameNameBeforeCreate(Long workspaceId, Long projectId, String arrangeName) throws DSSFrameworkErrorException {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("workspace_id", workspaceId);
        queryWrapper.eq("project_id", projectId);
        queryWrapper.eq("orchestrator_name", arrangeName);
        List<DSSOrchestrator> list = this.list(queryWrapper);
        if (!CollectionUtils.isEmpty(list)) {
            DSSFrameworkErrorException.dealErrorException(60000, "编排名称已经存在");
        }
    }

    //是否存在相同的编排名称,如果不存在相同的编排名称則返回编排id
    @Override
    public Long isExistSameNameBeforeUpdate(OrchestratorModifyRequest orchestratorModifRequest) throws DSSFrameworkErrorException {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("workspace_id", orchestratorModifRequest.getWorkspaceId());
        queryWrapper.eq("project_id", orchestratorModifRequest.getProjectId());
        queryWrapper.eq("id", orchestratorModifRequest.getId());
        List<DSSOrchestrator> list = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            DSSFrameworkErrorException.dealErrorException(60000, "编排模式ID=" + orchestratorModifRequest.getId() + "不存在");
        }
        //是否存在相同的编排名称
        if (!orchestratorModifRequest.getOrchestratorName().equals(list.get(0).getOrchestratorName())) {
            isExistSameNameBeforeCreate(orchestratorModifRequest.getWorkspaceId(), orchestratorModifRequest.getProjectId(), orchestratorModifRequest.getOrchestratorName());
        }
        return list.get(0).getOrchestratorId();
    }


    /**
     * 查询编排模式
     *
     * @param orchestratorRequest
     * @param username
     * @return
     */
    @Override
    public List<OrchestratorBaseInfo> getListByPage(OrchestratorRequest orchestratorRequest, String username) {
      /*  QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("workspace_id", orchestratorRequest.getWorkspaceId());
        queryWrapper.eq("project_id", orchestratorRequest.getProjectId());
        if (StringUtils.isNotBlank(orchestratorRequest.getOrchestratorMode())) {
            queryWrapper.eq("orchestrator_mode", orchestratorRequest.getOrchestratorMode());
        }
        List<DSSOrchestrator> list = this.list(queryWrapper);
        List<OrchestratorBaseInfo> retList = new ArrayList<OrchestratorBaseInfo>(list.size());
        if (!CollectionUtils.isEmpty(list)) {
            //获取工程的权限等级
            List<DSSProjectUser> projectUserList = projectUserService.getEditProjectList(orchestratorRequest.getProjectId(), username);
            //获取最大权限

            Integer priv = CollectionUtils.isEmpty(projectUserList) ? null : projectUserList.stream().mapToInt(DSSProjectUser::getPriv).max().getAsInt();
            OrchestratorBaseInfo orchestratorBaseInfo = null;
            for (DSSOrchestrator orchestrator : list) {
                orchestratorBaseInfo = new OrchestratorBaseInfo();
                BeanUtils.copyProperties(orchestrator, orchestratorBaseInfo);
                orchestratorBaseInfo.setOrchestratorWays(ProjectStringUtils.convertList(orchestrator.getOrchestratorWay()));
                orchestratorBaseInfo.setPriv(priv);
                retList.add(orchestratorBaseInfo);
            }
        }
        return retList;*/
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("workspace_id", orchestratorRequest.getWorkspaceId());
        queryWrapper.eq("project_id", orchestratorRequest.getProjectId());
        if (orchestratorRequest.getId() != null) {
            queryWrapper.eq("orchestrator_id", orchestratorRequest.getId());
        }
        if (StringUtils.isNotBlank(orchestratorRequest.getOrchestratorMode())) {
            queryWrapper.eq("orchestrator_mode", orchestratorRequest.getOrchestratorMode());
        }
        List<DSSOrchestrator> list = this.list(queryWrapper);
        List<OrchestratorBaseInfo> retList = new ArrayList<OrchestratorBaseInfo>(list.size());
        if (!CollectionUtils.isEmpty(list)) {
            //获取工程的权限等级
            List<DSSProjectUser> projectUserList = projectUserService.getProjectUserPriv(orchestratorRequest.getProjectId(), username);
            List<Integer> editPriv = projectUserList.stream()
                    .map(DSSProjectUser::getPriv)
                    .filter(priv -> priv == ProjectUserPrivEnum.PRIV_EDIT.getRank())
                    .collect(Collectors.toList());
            List<Integer> releasePriv = projectUserList.stream()
                    .map(DSSProjectUser::getPriv)
                    .filter(priv -> priv == ProjectUserPrivEnum.PRIV_RELEASE.getRank())
                    .collect(Collectors.toList());

            OrchestratorBaseInfo orchestratorBaseInfo = null;
            for (DSSOrchestrator orchestrator : list) {
                orchestratorBaseInfo = new OrchestratorBaseInfo();
                BeanUtils.copyProperties(orchestrator, orchestratorBaseInfo);
                orchestratorBaseInfo.setOrchestratorWays(ProjectStringUtils.convertList(orchestrator.getOrchestratorWay()));
                orchestratorBaseInfo.setEditable(!editPriv.isEmpty() || !releasePriv.isEmpty());
                orchestratorBaseInfo.setReleasable(!releasePriv.isEmpty());
                retList.add(orchestratorBaseInfo);
            }
        }
        return retList;
    }

    /**
     * 删除编排模式
     *
     * @param orchestratorDeleteRequest
     * @param username
     * @return
     * @throws DSSProjectErrorException
     */
    @Override
    @SuppressWarnings("all")
    public boolean deleteOrchestrator(OrchestratorDeleteRequest orchestratorDeleteRequest, String username) throws DSSProjectErrorException {
        //todo 使用工程的权限关系来限定 校验当前登录用户是否含有修改权限
        projectUserService.isEditProjectAuth(orchestratorDeleteRequest.getProjectId(), username);

        UpdateWrapper updateWrapper = new UpdateWrapper<DSSOrchestrator>();
        updateWrapper.eq("workspace_id", orchestratorDeleteRequest.getWorkspaceId());
        updateWrapper.eq("project_id", orchestratorDeleteRequest.getProjectId());
        updateWrapper.eq("id", orchestratorDeleteRequest.getId());
        return this.remove(updateWrapper);
    }

    @Override
    public DSSOrchestrator getOrchestratorById(Long id) {
        return this.getById(id);
    }

    @Override
    public Long importOrchestrator(RequestProjectImportOrchestrator orchestratorInfo) throws Exception {
        DSSProjectDO projectDO = dssProjectMapper.selectById(orchestratorInfo.getProjectId());
        if (projectDO == null) {
            DSSFrameworkErrorException.dealErrorException(ProjectServerResponse.PROJECT_NOT_EXIST.getCode(),
                    ProjectServerResponse.PROJECT_NOT_EXIST.getMsg());
        }
        //校验参数
        String type = orchestratorInfo.getType();
        if (StringUtils.isEmpty(type)) {
            DSSFrameworkErrorException.dealErrorException(60000, "编排模式类型不能为不存在");
        }
        Long orcId = orchestratorInfo.getId();
        String orchestratorMode = OrchestratorTypeEnum.getKeyByOrcType(type);
        String orchestratorWay = orchestratorInfo.getSecondaryType();
        if (StringUtils.isNotBlank(orchestratorWay)) {
            orchestratorWay = orchestratorWay.replace("[", ",").replace("]", ",");
        }
        //查询是否存在相同的编排模式
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("orchestrator_id", orcId);
        queryWrapper.eq("project_id", projectDO.getId());
        List<DSSOrchestrator> list = this.list(queryWrapper);
        //如果存在即更新既可以
        if (!CollectionUtils.isEmpty(list)) {
            //导入更新
            return updateOrcByImport(list.get(0), orchestratorMode,orchestratorWay,orchestratorInfo);
        } else {
            //导入新增
            return insertOrcByImport(projectDO,orchestratorMode,orchestratorWay,orchestratorInfo);
        }
    }

    @Override
    public boolean updateProjectOrcVersionId(RequestProjectUpdateOrcVersion projectUpdateOrcVersion) throws Exception {
        UpdateWrapper updateWrapper = new UpdateWrapper<DSSOrchestrator>();
        updateWrapper.eq("project_id", projectUpdateOrcVersion.getProjectId());
        updateWrapper.eq("orchestrator_id", projectUpdateOrcVersion.getOrchestratorId());
        DSSOrchestrator orchestrator = new DSSOrchestrator();
        orchestrator.setOrchestratorVersionId(projectUpdateOrcVersion.getVersionId());
        return this.update(orchestrator,updateWrapper);
    }


    //导入更新
    public Long updateOrcByImport(DSSOrchestrator dbEntity, String orchestratorMode, String orchestratorWay, RequestProjectImportOrchestrator orchestratorInfo) {
        DSSOrchestrator updateEntity = new DSSOrchestrator();
        BeanUtils.copyProperties(dbEntity, updateEntity);
        //更新字段
        updateEntity.setOrchestratorName(orchestratorInfo.getName());
        updateEntity.setOrchestratorMode(orchestratorMode);
        updateEntity.setOrchestratorWay(orchestratorWay);
        updateEntity.setDescription(orchestratorInfo.getDesc());
        updateEntity.setUpdateUser(orchestratorInfo.getCreator());
        updateEntity.setUpdateTime(new Date());
        //数据库更新
        this.updateById(updateEntity);
        return 0L;
    }

    //导入新增
    public Long insertOrcByImport(DSSProjectDO projectDO, String orchestratorMode, String orchestratorWay, RequestProjectImportOrchestrator orchestratorInfo)
            throws DSSFrameworkErrorException {
        //新增，查看是否含有同名的编排模式
       /* QueryWrapper queryNameWrapper = new QueryWrapper();
        queryNameWrapper.eq("workspace_id", projectDO.getWorkspaceId());
        queryNameWrapper.eq("project_id", orchestratorInfo.getProjectId());
        queryNameWrapper.eq("orchestrator_name", orchestratorInfo.getName());
        List<DSSOrchestrator> sameNameList = this.list(queryNameWrapper);
        if (!CollectionUtils.isEmpty(sameNameList)) {
            DSSFrameworkErrorException.dealErrorException(60000, "编排名称已经存在");
        }*/
        DSSOrchestrator orchestrator = new DSSOrchestrator();
        orchestrator.setProjectId(projectDO.getId());
        orchestrator.setOrchestratorId(orchestratorInfo.getId());
        orchestrator.setOrchestratorVersionId(orchestratorInfo.getVersionId());
        orchestrator.setOrchestratorName(orchestratorInfo.getName());
        orchestrator.setOrchestratorMode(orchestratorMode);
        orchestrator.setOrchestratorWay(orchestratorWay);
        orchestrator.setDescription(orchestratorInfo.getDesc());
        orchestrator.setCreateUser(orchestratorInfo.getCreator());
        orchestrator.setCreateTime(new Date());
        orchestrator.setUpdateTime(new Date());
        orchestrator.setWorkspaceId(projectDO.getWorkspaceId());
        this.save(orchestrator);
        return orchestrator.getId();
    }
}
