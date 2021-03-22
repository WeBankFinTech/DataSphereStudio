/*
 *
 *  * Copyright 2019 WeBank
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.orchestrator.db.dao;

import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorInfo;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorVersion;
import com.webank.wedatasphere.dss.orchestrator.common.entity.OrchestratorInfo;
import com.webank.wedatasphere.dss.orchestrator.common.entity.OrchestratorProdDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.*;

@Mapper
public interface OrchestratorMapper {

    void addOrchestrator(DSSOrchestratorInfo dssOrchestratorInfo);

    DSSOrchestratorInfo getOrchestrator(Long id);

    DSSOrchestratorInfo getOrchestratorByUUID(String uuid);

    void updateOrchestrator(DSSOrchestratorInfo dssOrchestratorInfo);

    void deleteOrchestrator(Long id);


    /**
     * @param dssOrchestratorVersion
     */

    void addOrchestratorVersion(DSSOrchestratorVersion dssOrchestratorVersion);

    DSSOrchestratorVersion getOrchestratorVersion(Long versionId);

    /**
     * 根据id查找最新的版本信息
     *
     * @param orchestratorId
     * @return
     */
    DSSOrchestratorVersion getLatestOrchestratorVersionById(Long orchestratorId);

    void updateOrchestratorVersion(DSSOrchestratorVersion dssOrchestratorVersion);

    void deleteOrchestratorVersion(Long versionId);

    /**
     * 根据id查找所有的版本信息
     *
     * @param orchestratorId
     * @return
     */
    List<DSSOrchestratorVersion> getVersionByOrchestratorId(Long orchestratorId);



    OrchestratorInfo getOrcInfoByAppId(@Param("appId")Long appId);

    List<OrchestratorProdDetail> getOrchestratorProdDetails(@Param("projectId") Long projectId);

    @Select("select max(id) from dss_orchestrator_version_info where `orchestrator_id` = #{orchestratorId}")
    Long findLatestOrcVersionId(@Param("orchestratorId") Long orchestratorId);

    List<DSSOrchestratorVersion> getOrchestratorVersions(@Param("projectId") Long projectId, @Param("orchestratorId") Long orchestratorId);

    @Select("select max(`version`) from dss_orchestrator_version_info where orchestrator_id = #{orchestratorId}")
    String getLatestVersion(@Param("orchestratorId") Long orchestratorId);

    @Select("select `id` from `dss_orchestrator_info` where `project_id` = #{projectId} and `is_published` = 1")
    List<Long> getAllOrcIdsByProjectId(@Param("projectId")Long projectId);

    @Select("select max(`app_id`) from `dss_orchestrator_version_info` where `orchestrator_id` = #{orchestratorId} and `version` = #{version}")
    Long getAppIdByVersion(@Param("orchestratorId") Long orchestratorId, @Param("version") String version);

    @Update("update `dss_orchestrator_info` set `is_published` =  1 where id = #{orchestratorId}")
    void setPublished(@Param("orchestratorId")Long orchestratorId);
}
