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

import com.webank.wedatasphere.dss.server.service.DSSFlowService;
import com.webank.wedatasphere.dss.server.service.DSSFlowTaxonomyService;
import com.webank.wedatasphere.dss.server.service.DSSProjectService;
import com.webank.wedatasphere.dss.server.dao.FlowMapper;
import com.webank.wedatasphere.dss.server.dao.FlowTaxonomyMapper;
import com.webank.wedatasphere.dss.server.entity.DSSFlowTaxonomy;
import com.webank.wedatasphere.dss.common.entity.flow.DSSFlow;
import com.webank.wedatasphere.dss.common.entity.flow.DSSFlowVersion;
import com.webank.wedatasphere.dss.common.entity.project.DSSProject;
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
public class DSSFlowTaxonomyServiceImpl implements DSSFlowTaxonomyService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private FlowTaxonomyMapper flowTaxonomyMapper;

    @Autowired
    private FlowMapper flowMapper;

    @Autowired
    private DSSProjectService projectService;

    @Autowired
    private DSSFlowService flowService;

    @Override
    public DSSFlowTaxonomy getFlowTaxonomyByID(Long id) {
        return flowTaxonomyMapper.selectFlowTaxonomyByID(id);
    }

    private List<DSSFlow> listFlowByTaxonomyID(Long projectID, Long taxonomyID, Boolean isRootFlow){
        return flowMapper.listFlowByTaxonomyID(projectID,taxonomyID,isRootFlow);
    }

    // TODO: 2019/5/16 级联查询返回json序列化好像有问题，暂时只能循环查
    @Override
    public List<DSSFlowTaxonomy> listAllFlowTaxonomy(Long projectVersionID, Boolean isRootFlow) {
        DSSProject dssProject = projectService.getProjectByProjectVersionID(projectVersionID);
        List<DSSFlowTaxonomy> dwsFlowTaxonomies = listFlowTaxonomyByProjectID(dssProject.getId());
        for (DSSFlowTaxonomy dssFlowTaxonomy : dwsFlowTaxonomies) {
            List<DSSFlow> dssFlowList = listFlowByTaxonomyID(dssProject.getId(), dssFlowTaxonomy.getId(),isRootFlow);
            for (DSSFlow dssFlow : dssFlowList) {
                DSSFlowVersion version = flowService.getLatestVersionByFlowIDAndProjectVersionID(dssFlow.getId(),projectVersionID);
                dssFlow.setLatestVersion(version);
            }
            dssFlowTaxonomy.setDssFlowList(dssFlowList.stream().filter(f ->f.getLatestVersion() !=null).collect(Collectors.toList()));
        }
        return dwsFlowTaxonomies;
    }

    private List<DSSFlowTaxonomy> listFlowTaxonomyByProjectID(Long projectID) {
        return flowTaxonomyMapper.listFlowTaxonomyByProjectID(projectID);
    }

    //有projectID应该可以同时过滤不同用户中的，我的工作流分类下的工作流
    @Override
    public List<DSSFlowTaxonomy> listFlowTaxonomy(Long projectVersionID, Long flowTaxonomyID, Boolean isRootFlow) {
        DSSFlowTaxonomy flowTaxonomy = getFlowTaxonomyByID(flowTaxonomyID);
        DSSProject dssProject = projectService.getProjectByProjectVersionID(projectVersionID);
        List<DSSFlow> dssFlowList = listFlowByTaxonomyID(dssProject.getId(),flowTaxonomyID,isRootFlow);
        for (DSSFlow dssFlow : dssFlowList) {
            DSSFlowVersion version = flowService.getLatestVersionByFlowIDAndProjectVersionID(dssFlow.getId(),projectVersionID);
            dssFlow.setLatestVersion(version);
        }
        flowTaxonomy.setDssFlowList(dssFlowList.stream().filter(f ->f.getLatestVersion() !=null).collect(Collectors.toList()));
        return Arrays.asList(flowTaxonomy);
    }

    @Lock
    @Transactional(rollbackFor = DSSErrorException.class)
    @Override
    public void addFlowTaxonomy(DSSFlowTaxonomy dssFlowTaxonomy, Long projectVersionID) throws DSSErrorException {
        try {
            flowTaxonomyMapper.insertFlowTaxonomy(dssFlowTaxonomy);
        }catch (DuplicateKeyException e){
            logger.info(e.getMessage());
            throw new DSSErrorException(90005,"工作流名分类名不能重复");
        }
    }

    @Lock
    @Transactional(rollbackFor = DSSErrorException.class)
    @Override
    public void updateFlowTaxonomy(DSSFlowTaxonomy dssFlowTaxonomy, Long projectVersionID) throws DSSErrorException {
        try {
            flowTaxonomyMapper.updateFlowTaxonomy(dssFlowTaxonomy);
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
