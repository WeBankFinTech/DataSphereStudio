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


import com.webank.wedatasphere.dss.common.entity.project.DSSProject;
import com.webank.wedatasphere.dss.common.entity.project.DSSProjectPublishHistory;
import com.webank.wedatasphere.dss.common.entity.project.DSSProjectVersion;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


public interface ProjectMapper {
    DSSProject selectProjectByID(Long id);

    DSSProjectVersion selectLatestVersionByProjectID(Long projectID);

    DSSProject selectProjectByVersionID(Long projectVersionID);

    void addProject(DSSProject dssProject);

    void addProjectVersion(DSSProjectVersion dssProjectVersion);

    void updateDescription(@Param("projectID") Long projectID, @Param("description") String description, @Param("product")String product ,@Param("applicationArea")Integer applicationArea ,@Param("business")String business);

    List<DSSProjectVersion> listProjectVersionsByProjectID(Long projectID);

    Boolean noPublished(Long projectID);

    void deleteProjectVersions(long projectID);

    void deleteProjectBaseInfo(long projectID);

    DSSProjectVersion selectProjectVersionByID(Long id);

    DSSProjectVersion selectProjectVersionByProjectIDAndVersionID(@Param("projectID") Long projectId, @Param("version") String version);

    Integer updateLock(@Param("lock") Integer lock, @Param("projectVersionID") Long projectVersionID);

    DSSProjectPublishHistory selectProjectPublishHistoryByProjectVersionID(Long projectVersionID);

    void insertPublishHistory(DSSProjectPublishHistory dssProjectPublishHistory);

    void updatePublishHistoryState(@Param("projectVersionID") Long projectVersionID, @Param("status") Integer status);

    void addAccessProjectRelation(@Param("appjointProjectIDAndAppID") Map<Long,Long> appjointProjectIDAndAppID, @Param("projectID") Long projectID);

    Long getAppjointProjectID(@Param("projectID") Long projectID, @Param("applicationID") Integer applicationID);
}
