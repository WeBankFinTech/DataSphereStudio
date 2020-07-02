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


import com.webank.wedatasphere.dss.common.entity.project.DSSProject;
import com.webank.wedatasphere.dss.server.dao.ProjectTaxonomyMapper;
import com.webank.wedatasphere.dss.server.entity.DSSProjectTaxonomy;
import com.webank.wedatasphere.dss.server.service.DSSProjectService;
import com.webank.wedatasphere.dss.server.service.DSSProjectTaxonomyService;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Service
public class DSSProjectTaxonomyServiceImpl implements DSSProjectTaxonomyService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ProjectTaxonomyMapper projectTaxonomyMapper;
    @Autowired
    private DSSProjectService projectService;

    @Override
    public DSSProjectTaxonomy getProjectTaxonomyByID(Long id) {
        return projectTaxonomyMapper.selectProjectTaxonomyByID(id);
    }

    @Override
    public List<DSSProjectTaxonomy> listProjectTaxonomyByUser(String userName) {
        return projectTaxonomyMapper.listProjectTaxonomyByUser(userName);
    }


    private List<Long> listProjectIDByTaxonomyID(Long taxonomyID, String userName) {
        return projectTaxonomyMapper.listProjectIDByTaxonomyID(taxonomyID, userName);
    }


    @Override
    public List<DSSProjectTaxonomy> listAllProjectTaxonomy(String userName) {
        List<DSSProjectTaxonomy> dwsProjectTaxonomies = listProjectTaxonomyByUser(userName);
        for (DSSProjectTaxonomy dssProjectTaxonomy : dwsProjectTaxonomies) {
            List<Long> projectIDs = listProjectIDByTaxonomyID(dssProjectTaxonomy.getId(), userName);
            ArrayList<DSSProject> dssProjectList = new ArrayList<>();
            for (Long projectID : projectIDs) {
                DSSProject dssProject = projectService.getLatestVersionProject(projectID);
                dssProjectList.add(dssProject);
            }
            dssProjectTaxonomy.setDssProjectList(dssProjectList);
        }
        return dwsProjectTaxonomies;
    }

    @Override
    public List<DSSProjectTaxonomy> listProjectTaxonomy(Long taxonomyID, String userName) {
        DSSProjectTaxonomy dssProjectTaxonomy = getProjectTaxonomyByID(taxonomyID);
        List<Long> projectIDs = listProjectIDByTaxonomyID(dssProjectTaxonomy.getId(), userName);
        ArrayList<DSSProject> dssProjectList = new ArrayList<>();
        for (Long projectID : projectIDs) {
            DSSProject dssProject = projectService.getLatestVersionProject(projectID);
            dssProjectList.add(dssProject);
        }
        dssProjectTaxonomy.setDssProjectList(dssProjectList);
        return Arrays.asList(dssProjectTaxonomy);
    }

    @Override
    public void addProjectTaxonomy(DSSProjectTaxonomy dssProjectTaxonomy) throws DSSErrorException {
        try {
            projectTaxonomyMapper.insertProjectTaxonomy(dssProjectTaxonomy);
        } catch (DuplicateKeyException e) {
            logger.info(e.getMessage());
            throw new DSSErrorException(90004, "工程分类名不能重复");
        }
    }

    @Override
    public void updateProjectTaxonomy(DSSProjectTaxonomy dssProjectTaxonomy) throws DSSErrorException {
        try {
            projectTaxonomyMapper.updateProjectTaxonomy(dssProjectTaxonomy);
        } catch (DuplicateKeyException e) {
            logger.info(e.getMessage());
            throw new DSSErrorException(90004, "工程分类名不能重复");
        }
    }

    @Override
    public boolean hasProjects(Long projectTaxonomyID) {
        Long count = projectTaxonomyMapper.hasProjects(projectTaxonomyID);
        return count != 0;
    }

    @Override
    public void deleteProjectTaxonomy(Long projectTaxonomyID) {
        projectTaxonomyMapper.deleteProjectTaxonomy(projectTaxonomyID);
    }

    @Override
    public void updateProjectTaxonomyRelation(Long projectID, Long taxonomyID) {
        if (taxonomyID != null) {
            projectTaxonomyMapper.updateProjectTaxonomyRelation(projectID, taxonomyID);
        }
    }
}
