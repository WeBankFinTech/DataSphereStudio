/*
 * Copyright 2019 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

package com.webank.wedatasphere.dss.framework.project.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.wedatasphere.dss.framework.project.entity.DSSOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.common.entity.OrchestratorReleaseInfo;


@Mapper
public interface DSSOrchestratorMapper extends BaseMapper<DSSOrchestrator> {

    @Select("Select `uuid` from `dss_orchestrator_info` where `id` = #{orchestratorId} ")
    String getUUID(@Param("orchestratorId") Long orchestratorId);

    @Select("SELECT * FROM dss_orchestrator_release_info WHERE orchestrator_id = #{orchestratorId}")
    OrchestratorReleaseInfo getByOrchestratorId(Long orchestratorId);

    @Delete("DELETE FROM dss_orchestrator_release_info WHERE id = #{id}")
    int removeOrchestratorReleaseInfoById(Long id);
}


