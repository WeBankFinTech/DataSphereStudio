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

import com.webank.wedatasphere.dss.server.service.DWSFlowService;
import com.webank.wedatasphere.dss.server.service.DWSFlowTaxonomyService;
import com.webank.wedatasphere.dss.server.service.DWSProjectService;
import com.webank.wedatasphere.dss.server.dao.FlowMapper;
import com.webank.wedatasphere.dss.server.dao.FlowTaxonomyMapper;
import com.webank.wedatasphere.dss.server.entity.DWSFlowTaxonomy;
import com.webank.wedatasphere.dss.common.entity.flow.DWSFlow;
import com.webank.wedatasphere.dss.common.entity.flow.DWSFlowVersion;
import com.webank.wedatasphere.dss.common.entity.project.DWSProject;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.server.lock.Lock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class DWSFlowTaxonomyServiceImpl implements DWSFlowTaxonomyService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private FlowTaxonomyMapper flowTaxonomyMapper;

    @Autowired
    private FlowMapper flowMapper;

    @Autowired
    private DWSProjectService projectService;

    @Autowired
    private DWSFlowService flowService;

    @Override
    public DWSFlowTaxonomy getFlowTaxonomyByID(Long id) {
        return flowTaxonomyMapper.selectFlowTaxonomyByID(id);
    }

    private List<DWSFlow> listFlowByTaxonomyID(Long projectID, Long taxonomyID, Boolean isRootFlow){
        return flowMapper.listFlowByTaxonomyID(projectID,taxonomyID,isRootFlow);
    }

    // TODO: 2019/5/16 级联查询返回json序列化好像有问题，暂时只能循环查
    @Override
    public List<DWSFlowTaxonomy> listAllFlowTaxonomy(Long projectVersionID,Boolean isRootFlow) {
        DWSProject dwsProject = projectService.getProjectByProjectVersionID(projectVersionID);
        List<DWSFlowTaxonomy> dwsFlowTaxonomies = listFlowTaxonomyByProjectID(dwsProject.getId());
        for (DWSFlowTaxonomy dwsFlowTaxonomy : dwsFlowTaxonomies) {
            List<DWSFlow> dwsFlowList = listFlowByTaxonomyID(dwsProject.getId(),dwsFlowTaxonomy.getId(),isRootFlow);
            for (DWSFlow dwsFlow : dwsFlowList) {
                DWSFlowVersion version = flowService.getLatestVersionByFlowIDAndProjectVersionID(dwsFlow.getId(),projectVersionID);
                dwsFlow.setLatestVersion(version);
            }
            dwsFlowTaxonomy.setDwsFlowList(dwsFlowList.stream().filter(f ->f.getLatestVersion() !=null).collect(Collectors.toList()));
        }
        return dwsFlowTaxonomies;
    }

    private List<DWSFlowTaxonomy> listFlowTaxonomyByProjectID(Long projectID) {
        return flowTaxonomyMapper.listFlowTaxonomyByProjectID(projectID);
    }

    //有projectID应该可以同时过滤不同用户中的，我的工作流分类下的工作流
    @Override
    public List<DWSFlowTaxonomy> listFlowTaxonomy(Long projectVersionID,Long flowTaxonomyID, Boolean isRootFlow) {
        DWSFlowTaxonomy flowTaxonomy = getFlowTaxonomyByID(flowTaxonomyID);
        DWSProject dwsProject = projectService.getProjectByProjectVersionID(projectVersionID);
        List<DWSFlow> dwsFlowList = listFlowByTaxonomyID(dwsProject.getId(),flowTaxonomyID,isRootFlow);
        for (DWSFlow dwsFlow : dwsFlowList) {
            DWSFlowVersion version = flowService.getLatestVersionByFlowIDAndProjectVersionID(dwsFlow.getId(),projectVersionID);
            dwsFlow.setLatestVersion(version);
        }
        flowTaxonomy.setDwsFlowList(dwsFlowList.stream().filter(f ->f.getLatestVersion() !=null).collect(Collectors.toList()));
        return Arrays.asList(flowTaxonomy);
    }

    @Lock
    @Transactional(rollbackFor = DSSErrorException.class)
    @Override
    public void addFlowTaxonomy(DWSFlowTaxonomy dwsFlowTaxonomy,Long projectVersionID) throws DSSErrorException {
        try {
            flowTaxonomyMapper.insertFlowTaxonomy(dwsFlowTaxonomy);
        }catch (DuplicateKeyException e){
            logger.info(e.getMessage());
            throw new DSSErrorException(90005,"工作流名分类名不能重复");
        }
    }

    @Lock
    @Transactional(rollbackFor = DSSErrorException.class)
    @Override
    public void updateFlowTaxonomy(DWSFlowTaxonomy dwsFlowTaxonomy,Long projectVersionID) throws DSSErrorException {
        try {
            flowTaxonomyMapper.updateFlowTaxonomy(dwsFlowTaxonomy);
        }catch (DuplicateKeyException e){
            logger.info(e.getMessage());
            throw new DSSErrorException(90005,"工作流名分类名不能重复");
        }
    }

    @Override
    public boolean hasFlows(Long flowTaxonomyID) {
        Long count = flowTaxonomyMapper.hasFlows(flowTaxonomyID);
        return count != 0;
    }

    @Lock
    @Transactional(rollbackFor = DSSErrorException.class)
    @Override
    public void deleteFlowTaxonomy(Long flowTaxonomyID,Long projectVersionID) {
        flowTaxonomyMapper.deleteFlowTaxonomy(flowTaxonomyID);
    }
}
