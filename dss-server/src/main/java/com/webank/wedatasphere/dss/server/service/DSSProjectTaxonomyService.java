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

package com.webank.wedatasphere.dss.server.service;

import com.webank.wedatasphere.dss.server.entity.DSSProjectTaxonomy;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;

import java.util.List;


public interface DSSProjectTaxonomyService {

    DSSProjectTaxonomy getProjectTaxonomyByID(Long id);

    List<DSSProjectTaxonomy> listProjectTaxonomyByUser(String userName);

    //----------------------
    List<DSSProjectTaxonomy> listAllProjectTaxonomy(String userName, Long workspaceId);

    List<DSSProjectTaxonomy> listProjectTaxonomy(Long taxonomyID, String userName);

    void addProjectTaxonomy(DSSProjectTaxonomy dssProjectTaxonomy) throws DSSErrorException;

    void updateProjectTaxonomy(DSSProjectTaxonomy dssProjectTaxonomy) throws DSSErrorException;

    boolean hasProjects(Long projectTaxonomyID);

    void deleteProjectTaxonomy(Long projectTaxonomyID);

    void updateProjectTaxonomyRelation(Long projectID, Long taxonomyID);
}
