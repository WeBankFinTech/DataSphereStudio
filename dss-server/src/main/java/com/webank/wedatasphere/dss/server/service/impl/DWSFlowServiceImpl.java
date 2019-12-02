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
import com.webank.wedatasphere.dss.common.entity.flow.DWSFlow;
import com.webank.wedatasphere.dss.common.entity.flow.DWSFlowVersion;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.server.dao.DWSUserMapper;
import com.webank.wedatasphere.dss.server.dao.FlowMapper;
import com.webank.wedatasphere.dss.server.dao.FlowTaxonomyMapper;
import com.webank.wedatasphere.dss.server.lock.Lock;
import com.webank.wedatasphere.dss.server.operate.Op;
import com.webank.wedatasphere.dss.server.operate.Operate;
import com.webank.wedatasphere.dss.server.service.BMLService;
import com.webank.wedatasphere.dss.server.service.DWSFlowService;
import com.webank.wedatasphere.dss.server.service.DWSProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class DWSFlowServiceImpl implements DWSFlowService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FlowMapper flowMapper;
    @Autowired
    private FlowTaxonomyMapper flowTaxonomyMapper;
    @Autowired
    private DWSUserMapper dwsUserMapper;

    @Autowired
    private DWSProjectService projectService;
    @Autowired
    private Operate[] operates;
    @Autowired
    private BMLService bmlService;

    @Override
    public DWSFlow getFlowByID(Long id) {
        return flowMapper.selectFlowByID(id);
    }

    @Override
    public List<DWSFlowVersion> listAllFlowVersions(Long flowID, Long projectVersionID) {
        List<DWSFlowVersion> versions = flowMapper.listFlowVersionsByFlowID(flowID, projectVersionID).stream().sorted((v1, v2) -> {
            return v1.compareTo(v2);
        }).collect(Collectors.toList());
        return versions;
    }

    @Lock
    @Transactional(rollbackFor = DSSErrorException.class)
    @Override
    public DWSFlow addRootFlow(DWSFlow dwsFlow, Long taxonomyID, Long projectVersionID) throws DSSErrorException {
        try {
            flowMapper.insertFlow(dwsFlow);
        } catch (DuplicateKeyException e) {
            logger.info(e.getMessage());
            throw new DSSErrorException(90003, "工作流名不能重复");
        }
        //通过resource上传空文件，获取resourceId(jsonPath)和version
        Map<String, Object> bmlReturnMap = bmlService.upload(dwsUserMapper.getuserName(dwsFlow.getCreatorID()), "", UUID.randomUUID().toString() + ".json");
        //数据库中插入版本信息
        DWSFlowVersion version = new DWSFlowVersion();
        version.setFlowID(dwsFlow.getId());
        version.setSource("create by user");
        version.setJsonPath(bmlReturnMap.get("resourceId").toString());
        version.setVersion(bmlReturnMap.get("version").toString());
        version.setUpdateTime(new Date());
        version.setUpdatorID(dwsFlow.getCreatorID());
        // TODO: 2019/6/12 这里应该由前台传入
        version.setProjectVersionID(projectVersionID);
        flowMapper.insertFlowVersion(version);
        //数据库中插入分类关联信息
        flowTaxonomyMapper.insertFlowTaxonomyRelation(taxonomyID, dwsFlow.getId());
        return dwsFlow;
    }

    @Lock
    @Transactional(rollbackFor = DSSErrorException.class)
    @Override
    public DWSFlow addSubFlow(DWSFlow dwsFlow, Long parentFlowID, Long projectVersionID) throws DSSErrorException {
        Long taxonomyID = flowTaxonomyMapper.selectTaxonomyIDByFlowID(parentFlowID);
        DWSFlow parentFlow = flowMapper.selectFlowByID(parentFlowID);
        dwsFlow.setProjectID(parentFlow.getProjectID());
        DWSFlow subFlow = addRootFlow(dwsFlow, taxonomyID, projectVersionID);
        //数据库中插入关联信息
        flowMapper.insertFlowRelation(subFlow.getId(), parentFlowID);
        return subFlow;
    }

    @Override
    public DWSFlow getLatestVersionFlow(Long flowID, Long projectVersionID) throws DSSErrorException {
        DWSFlow dwsFlow = getFlowByID(flowID);
        DWSFlowVersion dwsFlowVersion = getLatestVersionByFlowIDAndProjectVersionID(flowID, projectVersionID);
        if (dwsFlowVersion == null) throw new DSSErrorException(90011, "该工作流已经被删除，请重新创建");
        String userName = dwsUserMapper.getuserName(dwsFlowVersion.getUpdatorID());
        Map<String, Object> query = bmlService.query(userName, dwsFlowVersion.getJsonPath(), dwsFlowVersion.getVersion());
        dwsFlowVersion.setJson(query.get("string").toString());
        dwsFlow.setLatestVersion(dwsFlowVersion);
        return dwsFlow;
    }

    @Override
    public DWSFlow getOneVersionFlow(Long flowID, String version, Long projectVersionID) {
        DWSFlow dwsFlow = getFlowByID(flowID);
        DWSFlowVersion dwsFlowVersion = flowMapper.selectVersionByFlowID(flowID, version, projectVersionID);
        String userName = dwsUserMapper.getuserName(dwsFlowVersion.getUpdatorID());
        Map<String, Object> query = bmlService.query(userName, dwsFlowVersion.getJsonPath(), dwsFlowVersion.getVersion());
        dwsFlowVersion.setJson(query.get("string").toString());
        dwsFlow.setFlowVersions(Arrays.asList(dwsFlowVersion));
        dwsFlow.setLatestVersion(dwsFlowVersion);
        return dwsFlow;
    }

/*    @Override
    public String getLatestJsonByFlow(DWSFlow dwsFlow) {
        DWSFlow latestVersionFlow = getLatestVersionFlow(dwsFlow.getId());
        return latestVersionFlow.getLatestVersion().getJson();
    }

    @Override
    public DWSFlow getLatestVersionFlow(Long ProjectID, String flowName) {
        Long flowID = flowMapper.selectFlowIDByProjectIDAndFlowName(ProjectID, flowName);
        return getLatestVersionFlow(flowID);
    }*/

    @Lock
    @Transactional(rollbackFor = DSSErrorException.class)
    @Override
    public void updateFlowBaseInfo(DWSFlow dwsFlow, Long projectVersionID, Long taxonomyID) throws DSSErrorException {
        try {
            flowMapper.updateFlowBaseInfo(dwsFlow);
        } catch (DuplicateKeyException e) {
            logger.info(e.getMessage());
            throw new DSSErrorException(90003, "工作流名不能重复");
        }
        if (taxonomyID != null) updateFlowTaxonomyRelation(dwsFlow.getId(), taxonomyID);
    }

    @Override
    public void updateFlowTaxonomyRelation(Long flowID, Long taxonomyID) throws DSSErrorException {
        DWSFlow dwsFlow = getFlowByID(flowID);
        Long oldTaxonomyID = flowTaxonomyMapper.selectTaxonomyIDByFlowID(flowID);
        if (!dwsFlow.getRootFlow() && (!oldTaxonomyID.equals(taxonomyID))) throw new DSSErrorException(90010, "子工作流不允许更新分类id");
        if (!dwsFlow.getRootFlow() && (oldTaxonomyID.equals(taxonomyID))) return;
        //这里也要同时更新子工作流的分类id
        List<Long> subFlowIDList = flowMapper.selectSubFlowIDByParentFlowID(flowID);
        subFlowIDList.add(flowID);
        flowTaxonomyMapper.updateFlowTaxonomyRelation(subFlowIDList, taxonomyID);
    }

    @Lock
    @Transactional(rollbackFor = DSSErrorException.class)
    @Override
    public void batchDeleteFlow(List<Long> flowIDlist, Long projectVersionID) {
        flowIDlist.stream().forEach(f -> {
            deleteFlow(f, projectVersionID);
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
        DWSFlowVersion dwsFlowVersion = new DWSFlowVersion();
        dwsFlowVersion.setUpdatorID(dwsUserMapper.getUserID(userName));
        dwsFlowVersion.setUpdateTime(new Date());
        dwsFlowVersion.setVersion(bmlReturnMap.get("version").toString());
        dwsFlowVersion.setJsonPath(resourceId);
        dwsFlowVersion.setComment(comment);
        dwsFlowVersion.setFlowID(flowID);
        dwsFlowVersion.setSource("保存更新");
        dwsFlowVersion.setProjectVersionID(projectVersionID);
        //version表中插入数据
        flowMapper.insertFlowVersion(dwsFlowVersion);
        return bmlReturnMap.get("version").toString();
    }

    @Override
    public Integer getParentRank(Long parentFlowID) {
        return getFlowByID(parentFlowID).getRank();
    }

    @Override
    public DWSFlowVersion getLatestVersionByFlowIDAndProjectVersionID(Long flowID, Long projectVersionID) {
        List<DWSFlowVersion> versions = flowMapper.listVersionByFlowIDAndProjectVersionID(flowID, projectVersionID)
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

    public void deleteFlow(Long flowId, Long projectVersionID) {
        List<Long> subFlowIDs = flowMapper.selectSubFlowIDByParentFlowID(flowId);
        for (Long subFlowID : subFlowIDs) {
            deleteFlow(subFlowID, projectVersionID);
        }
        for (Long subFlowID : subFlowIDs) {
            deleteDWSDB(subFlowID, projectVersionID);
            // TODO: 2019/6/5 wtss发布过的工作流的删除？
            // TODO: 2019/6/5 json中资源的删除
            // TODO: 2019/6/5 事务的保证
        }
        deleteDWSDB(flowId, projectVersionID);
    }

    private void deleteDWSDB(Long flowID, Long projectVersionID) {
        flowMapper.deleteFlowVersions(flowID, projectVersionID);
        if (projectVersionID == null || (flowMapper.noVersions(flowID) != null && flowMapper.noVersions(flowID))) {
            flowMapper.deleteFlowBaseInfo(flowID);
            flowMapper.deleteFlowRelation(flowID);
            flowTaxonomyMapper.deleteFlowTaxonomyRelation(flowID);
        }
        //第一期没有工作流的发布，所以不需要删除dws工作流的发布表
    }
}
