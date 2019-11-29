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


import com.webank.wedatasphere.dss.server.dao.ProjectTaxonomyMapper;
import com.webank.wedatasphere.dss.common.entity.project.DWSProject;
import com.webank.wedatasphere.dss.server.entity.DWSProjectTaxonomy;
import com.webank.wedatasphere.dss.server.service.DWSProjectService;
import com.webank.wedatasphere.dss.server.service.DWSProjectTaxonomyService;
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
public class DWSProjectTaxonomyServiceImpl implements DWSProjectTaxonomyService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ProjectTaxonomyMapper projectTaxonomyMapper;
    @Autowired
    private DWSProjectService projectService;

    @Override
    public DWSProjectTaxonomy getProjectTaxonomyByID(Long id) {
        return projectTaxonomyMapper.selectProjectTaxonomyByID(id);
    }

    @Override
    public List<DWSProjectTaxonomy> listProjectTaxonomyByUser(String userName) {
        return projectTaxonomyMapper.listProjectTaxonomyByUser(userName);
    }


    private List<Long> listProjectIDByTaxonomyID(Long taxonomyID, String userName) {
        return projectTaxonomyMapper.listProjectIDByTaxonomyID(taxonomyID, userName);
    }


    @Override
    public List<DWSProjectTaxonomy> listAllProjectTaxonomy(String userName) {
        List<DWSProjectTaxonomy> dwsProjectTaxonomies = listProjectTaxonomyByUser(userName);
        for (DWSProjectTaxonomy dwsProjectTaxonomy : dwsProjectTaxonomies) {
            List<Long> projectIDs = listProjectIDByTaxonomyID(dwsProjectTaxonomy.getId(), userName);
            ArrayList<DWSProject> dwsProjectList = new ArrayList<>();
            for (Long projectID : projectIDs) {
                DWSProject dwsProject = projectService.getLatestVersionProject(projectID);
                dwsProjectList.add(dwsProject);
            }
            dwsProjectTaxonomy.setDwsProjectList(dwsProjectList);
        }
        return dwsProjectTaxonomies;
    }

    @Override
    public List<DWSProjectTaxonomy> listProjectTaxonomy(Long taxonomyID, String userName) {
        DWSProjectTaxonomy dwsProjectTaxonomy = getProjectTaxonomyByID(taxonomyID);
        List<Long> projectIDs = listProjectIDByTaxonomyID(dwsProjectTaxonomy.getId(), userName);
        ArrayList<DWSProject> dwsProjectList = new ArrayList<>();
        for (Long projectID : projectIDs) {
            DWSProject dwsProject = projectService.getLatestVersionProject(projectID);
            dwsProjectList.add(dwsProject);
        }
        dwsProjectTaxonomy.setDwsProjectList(dwsProjectList);
        return Arrays.asList(dwsProjectTaxonomy);
    }

    @Override
    public void addProjectTaxonomy(DWSProjectTaxonomy dwsProjectTaxonomy) throws DSSErrorException {
        try {
            projectTaxonomyMapper.insertProjectTaxonomy(dwsProjectTaxonomy);
        } catch (DuplicateKeyException e) {
            logger.info(e.getMessage());
            throw new DSSErrorException(90004, "工程分类名不能重复");
        }
    }

    @Override
    public void updateProjectTaxonomy(DWSProjectTaxonomy dwsProjectTaxonomy) throws DSSErrorException {
        try {
            projectTaxonomyMapper.updateProjectTaxonomy(dwsProjectTaxonomy);
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
