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


import com.webank.wedatasphere.dss.common.entity.flow.DSSFlow;
import com.webank.wedatasphere.dss.common.entity.flow.DSSFlowVersion;
import org.apache.ibatis.annotations.Param;
import org.springframework.dao.DuplicateKeyException;

import java.util.List;


public interface FlowMapper {
    DSSFlow selectFlowByID(Long id);

    List<DSSFlow> listFlowByTaxonomyID(@Param("projectID") Long projectID, @Param("taxonomyID") Long taxonomyID, @Param("isRootFlow") Boolean isRootFlow);

    List<DSSFlowVersion> listFlowVersionsByFlowID(@Param("flowID") Long flowID, @Param("projectVersionID") Long projectVersionID);

    void insertFlow(DSSFlow dssFlow) throws DuplicateKeyException;

    void insertFlowVersion(DSSFlowVersion version);

    void batchInsertFlowVersion(@Param("flowVersions") List<DSSFlowVersion> flowVersions);

    void insertFlowRelation(@Param("flowID") Long flowID, @Param("parentFlowID") Long parentFlowID);

    DSSFlowVersion selectVersionByFlowID(@Param("flowID") Long flowID, @Param("version") String version, @Param("projectVersionID") Long projectVersionID);

    void updateFlowBaseInfo(DSSFlow dssFlow) throws DuplicateKeyException;

    List<Long> selectSubFlowIDByParentFlowID(Long parentFlowID);

    void deleteFlowVersions(@Param("flowID") Long flowID, @Param("projectVersionID") Long projectVersionID);

    void deleteFlowBaseInfo(Long flowID);

    void deleteFlowRelation(Long flowID);

    Long selectParentFlowIDByFlowID(Long flowID);

    List<DSSFlow> listFlowByProjectID(Long projectID);

    List<DSSFlowVersion> listVersionByFlowIDAndProjectVersionID(@Param("flowID") Long flowID, @Param("projectVersionID") Long projectVersionID);

    Boolean noVersions(Long flowID);

    List<DSSFlowVersion> listLastFlowVersionsByProjectVersionID(@Param("projectVersionID") Long projectVersionId);

    List<DSSFlowVersion> listLatestRootFlowVersionByProjectVersionID(Long projectVersionID);

    void batchUpdateFlowVersion(List<DSSFlowVersion> flowVersions);

    Long getParentFlowID(Long flowID);
}
