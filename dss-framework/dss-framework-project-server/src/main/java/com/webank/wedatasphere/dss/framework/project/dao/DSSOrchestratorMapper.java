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

package com.webank.wedatasphere.dss.framework.project.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.wedatasphere.dss.framework.project.entity.DSSOrchestrator;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSReleasedFlowVO;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

/**
 * created by v_wbzwchen  on 2020/12/16
 * Description:
 */
@Mapper
public interface DSSOrchestratorMapper extends BaseMapper<DSSOrchestrator> {


    @Select("select `orchestrator_name` from dss_project_orchestrator where `orchestrator_id` = #{orchestratorId}")
    String getOrchestratorNameById(@Param("orchestratorId") int orchestratorId);

    @Delete("delete from `dss_orchestrator_schedule_info` where `orchestrator_id` = #{orchestratorId}")
    void deleteScheduleInfo(@Param("orchestratorId") Integer orchestratorId);

    @Insert("insert into `dss_orchestrator_schedule_info`" +
            "(`orchestrator_id`, `project_name`, `schedule_user`, `schedule_time`, `alarm_user_emails`, `alarm_level`, `last_update_time`) " +
            "values(#{orchestratorId}, #{projectName}, #{scheduleUser}, #{scheduleTime}, #{alarmEmails}, #{alarmLevel}, now())")
    void setScheduleInfo(@Param("projectName")String projectName, @Param("scheduleUser")String scheduleUser,
                         @Param("scheduleTime")String scheduleTime, @Param("alarmEmails")String alarmEmails,
                         @Param("alarmLevel")String alarmLevel, @Param("orchestratorId")Integer orchestratorId);


    DSSReleasedFlowVO.ScheduleInfo getScheduleInfo(@Param("orchestratorId") Long orchestratorId);

    @Insert({
            "<script>",
            "insert into `dss_orchestrator_user`",
            "(`workspace_id`, `project_id`, `orchestrator_id`, `username`, `priv`, `last_update_time`)",
            "values",
            "<foreach collection='accessUsers' item='accessUser' open='(' separator='),(' close=')'>",
            " #{workspaceId}, #{projectId}, #{orchestratorId}, #{accessUser}, #{priv}, #{updateTime}",
            "</foreach>",
            "</script>"
    })
    void setOrchestratorPriv(@Param("workspaceId")int workspaceId,
                             @Param("projectId")Long projectId, @Param("orchestratorId")int orchestratorId,
                             @Param("accessUsers")List<String> accessUsers, @Param("priv")int priv, @Param("updateTime") Date date);

    @Delete("delete from `dss_orchestrator_user` " +
            "where `workspace_id` = #{workspaceId} " +
            "and `project_id` = #{projectId} " +
            "and `orchestrator_id` = #{orchestratorId}")
    void deleteAllOrchestratorPriv(@Param("workspaceId")int workspaceId, @Param("projectId")Long projectId, @Param("orchestratorId")int orchestratorId);


    @Select("Select `username` from `dss_orchestrator_user` where `orchestrator_id` = #{orchestratorId} ")
    List<String> getPrivUsers(@Param("orchestratorId") Long orchestratorId);
}
