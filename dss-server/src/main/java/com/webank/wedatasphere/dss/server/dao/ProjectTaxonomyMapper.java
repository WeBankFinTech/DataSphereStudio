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

package com.webank.wedatasphere.dss.server.dao;

import com.webank.wedatasphere.dss.server.entity.DWSProjectTaxonomy;
import com.webank.wedatasphere.dss.server.entity.DWSProjectTaxonomyRelation;
import org.apache.ibatis.annotations.Param;
import org.springframework.dao.DuplicateKeyException;

import java.util.List;


public interface ProjectTaxonomyMapper {
    DWSProjectTaxonomy selectProjectTaxonomyByID(Long id);
    DWSProjectTaxonomyRelation selectProjectTaxonomyRelationByTaxonomyIdOrProjectId(Long taxonomyIdOrProjectId);
    List<DWSProjectTaxonomy> listProjectTaxonomyByUser(String userName);

    //--------------------
    List<Long> listProjectIDByTaxonomyID(@Param("taxonomyID") Long taxonomyID, @Param("userName") String userName);

    void insertProjectTaxonomy(DWSProjectTaxonomy dwsProjectTaxonomy) throws DuplicateKeyException;

    void updateProjectTaxonomy(DWSProjectTaxonomy dwsProjectTaxonomy) throws DuplicateKeyException;

    Long hasProjects(Long projectTaxonomyID);

    void deleteProjectTaxonomy(Long projectTaxonomyID);

    void addProjectTaxonomyRelation(@Param("projectID") Long id, @Param("taxonomyID") Long taxonomyID, @Param("creatorID") Long creatorID);

    void updateProjectTaxonomyRelation(@Param("projectID") Long projectID, @Param("taxonomyID") Long taxonomyID);

    void deleteProjectTaxonomyRelationByProjectID(Long projectID);
}
