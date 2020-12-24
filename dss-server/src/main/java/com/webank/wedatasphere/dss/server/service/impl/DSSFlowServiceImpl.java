/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package com.webank.wedatasphere.dss.server.service.impl;


import com.webank.wedatasphere.dss.appjoint.exception.AppJointErrorException;
import com.webank.wedatasphere.dss.appjoint.scheduler.SchedulerAppJoint;
import com.webank.wedatasphere.dss.appjoint.service.ProjectService;
import com.webank.wedatasphere.dss.application.service.ApplicationService;
import com.webank.wedatasphere.dss.common.entity.flow.DSSFlow;
import com.webank.wedatasphere.dss.common.entity.flow.DSSFlowVersion;
import com.webank.wedatasphere.dss.common.entity.project.DSSProject;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.server.dao.DSSUserMapper;
import com.webank.wedatasphere.dss.server.dao.FlowMapper;
import com.webank.wedatasphere.dss.server.dao.FlowTaxonomyMapper;
import com.webank.wedatasphere.dss.server.function.FunctionInvoker;
import com.webank.wedatasphere.dss.server.lock.Lock;
import com.webank.wedatasphere.dss.server.operate.Op;
import com.webank.wedatasphere.dss.server.operate.Operate;
import com.webank.wedatasphere.dss.server.service.BMLService;
import com.webank.wedatasphere.dss.server.service.DSSFlowService;
import com.webank.wedatasphere.dss.server.service.DSSProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class DSSFlowServiceImpl implements DSSFlowService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FlowMapper flowMapper;
    @Autowired
    private FlowTaxonomyMapper flowTaxonomyMapper;
    @Autowired
    private DSSUserMapper dssUserMapper;

    @Autowired
    private DSSProjectService projectService;
    @Autowired
    private Operate[] operates;
    @Autowired
    private BMLService bmlService;
    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private FunctionInvoker functionInvoker;

    private SchedulerAppJoint schedulerAppJoint=null;

    @Override
    public DSSFlow getFlowByID(Long id) {
        return flowMapper.selectFlowByID(id);
    }

    @Override
    public List<DSSFlowVersion> listAllFlowVersions(Long flowID, Long projectVersionID) {
        List<DSSFlowVersion> versions = flowMapper.listFlowVersionsByFlowID(flowID, projectVersionID).stream().sorted((v1, v2) -> {
            return v1.compareTo(v2);
        }).collect(Collectors.toList());
        return versions;
    }

    @Lock
    @Transactional(rollbackFor = DSSErrorException.class)
    @Override
    public DSSFlow addRootFlow(DSSFlow dssFlow, Long taxonomyID, Long projectVersionID) throws DSSErrorException {
        try {
            flowMapper.insertFlow(dssFlow);
        } catch (DuplicateKeyException e) {
            logger.info(e.getMessage());
            throw new DSSErrorException(90003, "工作流名不能重复");
        }
        //通过resource上传空文件，获取resourceId(jsonPath)和version
        Map<String, Object> bmlReturnMap = bmlService.upload(dssUserMapper.getuserName(dssFlow.getCreatorID()), "", UUID.randomUUID().toString() + ".json");
        //数据库中插入版本信息
        DSSFlowVersion version = new DSSFlowVersion();
        version.setFlowID(dssFlow.getId());
        version.setSource("create by user");
        version.setJsonPath(bmlReturnMap.get("resourceId").toString());
        version.setVersion(bmlReturnMap.get("version").toString());
        version.setUpdateTime(new Date());
        version.setUpdatorID(dssFlow.getCreatorID());
        // TODO: 2019/6/12 这里应该由前台传入
        version.setProjectVersionID(projectVersionID);
        flowMapper.insertFlowVersion(version);
        //数据库中插入分类关联信息
        flowTaxonomyMapper.insertFlowTaxonomyRelation(taxonomyID, dssFlow.getId());
        return dssFlow;
    }

    @Lock
    @Transactional(rollbackFor = DSSErrorException.class)
    @Override
    public DSSFlow addSubFlow(DSSFlow dssFlow, Long parentFlowID, Long projectVersionID) throws DSSErrorException {
        Long taxonomyID = flowTaxonomyMapper.selectTaxonomyIDByFlowID(parentFlowID);
        DSSFlow parentFlow = flowMapper.selectFlowByID(parentFlowID);
        dssFlow.setProjectID(parentFlow.getProjectID());
        DSSFlow subFlow = addRootFlow(dssFlow, taxonomyID, projectVersionID);
        //数据库中插入关联信息
        flowMapper.insertFlowRelation(subFlow.getId(), parentFlowID);
        return subFlow;
    }

    @Override
    public DSSFlow getLatestVersionFlow(Long flowID, Long projectVersionID) throws DSSErrorException {
        DSSFlow dssFlow = getFlowByID(flowID);
        DSSFlowVersion dssFlowVersion = getLatestVersionByFlowIDAndProjectVersionID(flowID, projectVersionID);
        if (dssFlowVersion == null) throw new DSSErrorException(90011, "该工作流已经被删除，请重新创建");
        String userName = dssUserMapper.getuserName(dssFlowVersion.getUpdatorID());
        Map<String, Object> query = bmlService.query(userName, dssFlowVersion.getJsonPath(), dssFlowVersion.getVersion());
        dssFlowVersion.setJson(query.get("string").toString());
        dssFlow.setLatestVersion(dssFlowVersion);
        return dssFlow;
    }

    @Override
    public DSSFlow getOneVersionFlow(Long flowID, String version, Long projectVersionID) {
        DSSFlow dssFlow = getFlowByID(flowID);
        DSSFlowVersion dssFlowVersion = flowMapper.selectVersionByFlowID(flowID, version, projectVersionID);
        String userName = dssUserMapper.getuserName(dssFlowVersion.getUpdatorID());
        Map<String, Object> query = bmlService.query(userName, dssFlowVersion.getJsonPath(), dssFlowVersion.getVersion());
        dssFlowVersion.setJson(query.get("string").toString());
        dssFlow.setFlowVersions(Arrays.asList(dssFlowVersion));
        dssFlow.setLatestVersion(dssFlowVersion);
        return dssFlow;
    }

/*    @Override
    public String getLatestJsonByFlow(DSSFlow DSSFlow) {
        DSSFlow latestVersionFlow = getLatestVersionFlow(DSSFlow.getId());
        return latestVersionFlow.getLatestVersion().getJson();
    }

    @Override
    public DSSFlow getLatestVersionFlow(Long ProjectID, String flowName) {
        Long flowID = flowMapper.selectFlowIDByProjectIDAndFlowName(ProjectID, flowName);
        return getLatestVersionFlow(flowID);
    }*/

    @Lock
    @Transactional(rollbackFor = DSSErrorException.class)
    @Override
    public void updateFlowBaseInfo(DSSFlow dssFlow, Long projectVersionID, Long taxonomyID) throws DSSErrorException {
        try {
            flowMapper.updateFlowBaseInfo(dssFlow);
        } catch (DuplicateKeyException e) {
            logger.info(e.getMessage());
            throw new DSSErrorException(90003, "工作流名不能重复");
        }
        if (taxonomyID != null) updateFlowTaxonomyRelation(dssFlow.getId(), taxonomyID);
    }

    @Override
    public void updateFlowTaxonomyRelation(Long flowID, Long taxonomyID) throws DSSErrorException {
        DSSFlow dssFlow = getFlowByID(flowID);
        Long oldTaxonomyID = flowTaxonomyMapper.selectTaxonomyIDByFlowID(flowID);
        if (!dssFlow.getRootFlow() && (!oldTaxonomyID.equals(taxonomyID))) throw new DSSErrorException(90010, "子工作流不允许更新分类id");
        if (!dssFlow.getRootFlow() && (oldTaxonomyID.equals(taxonomyID))) return;
        //这里也要同时更新子工作流的分类id
        List<Long> subFlowIDList = flowMapper.selectSubFlowIDByParentFlowID(flowID);
        subFlowIDList.add(flowID);
        flowTaxonomyMapper.updateFlowTaxonomyRelation(subFlowIDList, taxonomyID);
    }

    @Lock
    @Transactional(rollbackFor = DSSErrorException.class)
    @Override
    public void batchDeleteFlow(List<Long> flowIDlist, Long projectVersionID, Boolean ifDelScheduler, String userName) {
        flowIDlist.stream().forEach(f -> {
            deleteFlow(f, projectVersionID, ifDelScheduler, userName);
        });
    }

    @Lock
    @Transactional(rollbackFor = {DSSErrorException.class,AppJointErrorException.class})
    @Override
    public String saveFlow(Long flowID, String jsonFlow, String comment, String userName, Long projectVersionID, List<Op> ops) throws DSSErrorException, AppJointErrorException {
        for (Op op : ops) {
            op.getParams().put("projectVersionID",projectVersionID);
            op.getParams().put("userName",userName);
            logger.info("{}保存工作流，操作:{}id:{}nodetype{},projectVersionID:{}",userName,op.getOp(),op.getId(),op.getNodeType(),projectVersionID);
            Optional<Operate> operate = Arrays.stream(operates).filter(f -> f.canInvokeOperate(op)).findFirst();
            // TODO: 2019/10/29 optinal 判断 and throws exception
            switch (op.getOp()) {
                case "add":
                    operate.get().add(this,op);
                    break;
                case "update":
                    operate.get().update(this,op);
                    break;
                case "delete":
                    operate.get().delete(this,op);
                    break;
                default:
                    logger.error("other operation:unable to occur");
            }
        }
        //获取rsourceId，就是jsonPaht
        String resourceId = getLatestVersionByFlowIDAndProjectVersionID(flowID, projectVersionID).getJsonPath();
        //上传文件获取resourceId和version save应该是已经有
        Map<String, Object> bmlReturnMap = bmlService.update(userName, resourceId, jsonFlow);
        DSSFlowVersion dssFlowVersion = new DSSFlowVersion();
        dssFlowVersion.setUpdatorID(dssUserMapper.getUserID(userName));
        dssFlowVersion.setUpdateTime(new Date());
        dssFlowVersion.setVersion(bmlReturnMap.get("version").toString());
        dssFlowVersion.setJsonPath(resourceId);
        dssFlowVersion.setComment(comment);
        dssFlowVersion.setFlowID(flowID);
        dssFlowVersion.setSource("保存更新");
        dssFlowVersion.setProjectVersionID(projectVersionID);
        //version表中插入数据
        flowMapper.insertFlowVersion(dssFlowVersion);
        return bmlReturnMap.get("version").toString();
    }

    @Override
    public Integer getParentRank(Long parentFlowID) {
        return getFlowByID(parentFlowID).getRank();
    }

    @Override
    public DSSFlowVersion getLatestVersionByFlowIDAndProjectVersionID(Long flowID, Long projectVersionID) {
        List<DSSFlowVersion> versions = flowMapper.listVersionByFlowIDAndProjectVersionID(flowID, projectVersionID)
                .stream().sorted((v1, v2) -> {
                    return v1.compareTo(v2);
                }).collect(Collectors.toList());
        return versions.isEmpty() ? null : versions.get(0);
    }

    @Override
    public Long getParentFlowID(Long flowID) {
        return flowMapper.getParentFlowID(flowID);
    }

    @Deprecated
    private Integer getParentFlowIDByFlowID(Long flowID, Integer rank) {
        Long parentFlowID = flowMapper.selectParentFlowIDByFlowID(flowID);
        if (parentFlowID != null) {
            rank++;
            getParentFlowIDByFlowID(parentFlowID, rank);
        }
        return rank;
    }

    public void deleteFlow(Long flowId, Long projectVersionID, Boolean ifDelScheduler, String userName) {
        logger.info(String.format("Begin to delete flow id %d with project version id %d", flowId, projectVersionID));
        List<Long> subFlowIDs = flowMapper.selectSubFlowIDByParentFlowID(flowId);
        for (Long subFlowID : subFlowIDs) {
            deleteFlow(subFlowID, projectVersionID, false, userName); // 目前subflow不支持删除scheduler
        }
        for (Long subFlowID : subFlowIDs) {
            deleteDSSDB(subFlowID, projectVersionID);
            // TODO: 2019/6/5 wtss发布过的工作流的删除？
            // TODO: 2019/6/5 json中资源的删除
            // TODO: 2019/6/5 事务的保证
        }
<<<<<<< HEAD:dss-server/src/main/java/com/webank/wedatasphere/dss/server/service/impl/DSSFlowServiceImpl.java
        try {
            DSSProject dssProject = projectService.getProjectByProjectVersionID(projectVersionID);
            dssProject.setUserName(userName);
            DSSFlow dssFlow = getFlowByID(flowId);
            if (ifDelScheduler) {
                if (applicationService.getApplication("schedulis") != null) {
                    if (getSchedulerAppJoint() != null) {
                        functionInvoker.projectServiceFlowFunction(dssProject, dssFlow, ProjectService::deleteFlowExtra, Arrays.asList(getSchedulerAppJoint()));
                    } else {
                        logger.error("Delete scheduler project failed for scheduler appjoint is null");
                    }
                }
            }
            functionInvoker.projectServiceFlowFunction(dssProject, dssFlow, ProjectService::deleteFlowExtra, applicationService.listAppjoint());
        } catch (AppJointErrorException e) {
            logger.error("Delete flow from scheduler failed!", e);
        }
=======
>>>>>>> 8cc4d5bf688f7e8045fa4eecdcfcc1746cc0a44a:dss-server/src/main/java/com/webank/wedatasphere/dss/server/service/impl/DSSFlowServiceImpl.java
        deleteDSSDB(flowId, projectVersionID);
    }

    private void deleteDSSDB(Long flowID, Long projectVersionID) {
        flowMapper.deleteFlowVersions(flowID, projectVersionID);
        Boolean flowNoVersions = flowMapper.noVersions(flowID);
        if (projectVersionID == null || (flowNoVersions != null && flowNoVersions)) {
            flowMapper.deleteFlowBaseInfo(flowID);
            flowMapper.deleteFlowRelation(flowID);
            flowTaxonomyMapper.deleteFlowTaxonomyRelation(flowID);
        } else {
            logger.warn(String.format("Flow id %d still has version and cannot not delete this flow completely, please check db integrity", flowID));
        }
        //第一期没有工作流的发布，所以不需要删除DSS工作流的发布表
    }

    private SchedulerAppJoint getSchedulerAppJoint(){
        if(schedulerAppJoint == null){
            try {
                schedulerAppJoint = (SchedulerAppJoint)applicationService.getAppjoint("schedulis");
            } catch (AppJointErrorException e) {
                logger.error("Schedule system init failed!", e);
            }
        }
<<<<<<< HEAD:dss-server/src/main/java/com/webank/wedatasphere/dss/server/service/impl/DSSFlowServiceImpl.java
        return schedulerAppJoint;
=======
        //第一期没有工作流的发布，所以不需要删除DSS工作流的发布表
>>>>>>> 8cc4d5bf688f7e8045fa4eecdcfcc1746cc0a44a:dss-server/src/main/java/com/webank/wedatasphere/dss/server/service/impl/DSSFlowServiceImpl.java
    }
}
