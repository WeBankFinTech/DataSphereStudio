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
import com.webank.wedatasphere.dss.framework.appconn.service.AppConnService;
import com.webank.wedatasphere.dss.framework.common.exception.DSSFrameworkErrorException;
import com.webank.wedatasphere.dss.framework.project.dao.DSSOrchestratorMapper;
import com.webank.wedatasphere.dss.framework.project.dao.DSSProjectMapper;
import com.webank.wedatasphere.dss.framework.project.entity.*;
import com.webank.wedatasphere.dss.framework.project.entity.request.OrchestratorCreateRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.OrchestratorDeleteRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.OrchestratorModifyRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.OrchestratorRequest;
import com.webank.wedatasphere.dss.framework.project.entity.vo.OrchestratorBaseInfo;
import com.webank.wedatasphere.dss.framework.project.exception.DSSProjectErrorException;
import com.webank.wedatasphere.dss.framework.project.service.DSSOrchestratorService;
import com.webank.wedatasphere.dss.framework.project.service.DSSProjectUserService;
import com.webank.wedatasphere.dss.framework.project.utils.ProjectStringUtils;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorVersion;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.RequestOrchestratorVersion;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.ResponseOrchetratorVersion;
import com.webank.wedatasphere.dss.orchestrator.core.ref.OrchestratorCreateResponseRef;
import com.webank.wedatasphere.linkis.common.conf.CommonVars;
import com.webank.wedatasphere.linkis.rpc.Sender;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * DSS编排模式信息表 服务实现类
 *
 * @author v_wbzwchen
 * @since 2020-12-21
 */
@Service
public class DSSOrchestratorServiceImpl extends ServiceImpl<DSSOrchestratorMapper, DSSOrchestrator> implements DSSOrchestratorService {


    private static final Logger LOGGER = LoggerFactory.getLogger(DSSOrchestratorServiceImpl.class);

    public static final String MODE_SPLIT = ",";
    @Autowired
    private DSSOrchestratorMapper orchestratorMapper;
    @Autowired
    private DSSProjectUserService projectUserService;

    @Autowired
    private AppConnService appConnService;

    @Autowired
    private DSSProjectMapper dssProjectMapper;


    private static final String PROD_ORC_NAME =
            CommonVars.apply("wds.dss.orchestrator.prod.name", "dss-framework-orchestrator-server-prod").getValue();


    private final Sender prodOrcSender = Sender.getSender(PROD_ORC_NAME);

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
        //是否存在相同的编排名称
        isExistSameName(orchestratorCreateRequest.getWorkspaceId(), orchestratorCreateRequest.getProjectId(), orchestratorCreateRequest.getOrchestratorName());
        DSSOrchestrator orchestrator = new DSSOrchestrator();
        orchestrator.setWorkspaceId(orchestratorCreateRequest.getWorkspaceId());
        orchestrator.setProjectId(orchestratorCreateRequest.getProjectId());
        //编排模式id（工作流,调用orchestrator服务返回的orchestratorId）
        orchestrator.setOrchestratorId(responseRef.getOrchestratorId());
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
            isExistSameName(orchestratorModifRequest.getWorkspaceId(), orchestratorModifRequest.getProjectId(), orchestratorModifRequest.getOrchestratorName());
        }
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

    //是否存在相同的编排名称
    public void isExistSameName(Long workspaceId, Long projectId, String arrangeName) throws DSSFrameworkErrorException {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("workspace_id", workspaceId);
        queryWrapper.eq("project_id", projectId);
        queryWrapper.eq("orchestrator_name", arrangeName);
        List<DSSOrchestrator> list = this.list(queryWrapper);
        if (!CollectionUtils.isEmpty(list)) {
            DSSFrameworkErrorException.dealErrorException(60000, "编排名称已经存在");
        }
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
        QueryWrapper queryWrapper = new QueryWrapper();
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
            Integer priv = CollectionUtils.isEmpty(projectUserList) ? null : projectUserList.stream().mapToInt(DSSProjectUser::getPriv).max().getAsInt(); //获取最大权限
            OrchestratorBaseInfo orchestratorBaseInfo = null;
            for (DSSOrchestrator orchestrator : list) {
                orchestratorBaseInfo = new OrchestratorBaseInfo();
                BeanUtils.copyProperties(orchestrator, orchestratorBaseInfo);
                orchestratorBaseInfo.setOrchestratorWays(ProjectStringUtils.convertList(orchestrator.getOrchestratorWay()));
                orchestratorBaseInfo.setPriv(priv);
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
    public List<DSSOrchestratorVersion> getOrchestratorVersions(String username, Long projectId, Long orchestratorId, String dssLabel) {
        //todo 按照dsslabel进行选择 默认搞一下生产的
        LOGGER.info("user {} wants to get versions in  projectId {}, orchestratorId {}, dssLabel {}", username,
                projectId, orchestratorId, dssLabel);
        //通过rpc的方式去获取的版本信息
        RequestOrchestratorVersion requestOrchestratorVersion = RequestOrchestratorVersion.newInstance(username, projectId, orchestratorId, dssLabel);
        ResponseOrchetratorVersion responseOrchetratorVersion = (ResponseOrchetratorVersion) prodOrcSender.ask(requestOrchestratorVersion);
        LOGGER.info("user {} ends to getVersions : {} ", username, responseOrchetratorVersion.getOrchestratorVersions());
        return responseOrchetratorVersion.getOrchestratorVersions();
    }



}
