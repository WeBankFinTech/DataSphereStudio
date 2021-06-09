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

package com.webank.wedatasphere.dss.framework.release.dao;

import com.webank.wedatasphere.dss.framework.release.entity.project.ProjectInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * created by cooperyang on 2020/12/11
 * Description:用于查询工程和的orchestrator的基本内容信息
 */
@Mapper
public interface ProjectMapper {

    ProjectInfo getProjectInfoById(@Param("projectId") Long projectId);

    @Select("Select project_id from dss_project_orchestrator where orchestrator_id = #{orchestratorId}")
    Long getProjectIdByOrcId(@Param("orchestratorId") Long orchestratorId);

    @Select("select orchestrator_name from dss_project_orchestrator where orchestrator_id = #{orchestratorId}")
    String getOrchestratorName(@Param("orchestratorId") Long orchestratorId,
        @Param("orchestratorVersionId") Long orchestratorVersionId);

    @Select(
        "select w.`name` from dss_workspace w join dss_project p on w.`id` = p.`workspace_id` and p.id = #{projectId}")
    String getWorkspaceName(@Param("projectId") Long projectId);

    void updateProjectOrcInfo(@Param("projectId") Long projectId, @Param("orchestratorId") Long orchestratorId,
        @Param("orchestratorVersionId") Long orchestratorVersionId);

    @Select("SELECT app_id FROM dss_orchestrator_version_info WHERE id = #{orchestratorVersionId}")
    Long getAppIdByOrchestratorVersionId(@Param("orchestratorVersionId") Long orchestratorVersionId);

    @Select("SELECT version FROM dss_orchestrator_version_info WHERE id = #{orchestratorVersionId}")
    String getVersionByOrchestratorVersionId(@Param("orchestratorVersionId") Long orchestratorVersionId);

    @Update("UPDATE dss_orchestrator_info SET comment = #{comment} WHERE id =  #{orchestratorId}")
    Boolean updateCommentInOrchestratorInfo(@Param("comment") String comment,
        @Param("orchestratorId") Long orchestratorId);
}
