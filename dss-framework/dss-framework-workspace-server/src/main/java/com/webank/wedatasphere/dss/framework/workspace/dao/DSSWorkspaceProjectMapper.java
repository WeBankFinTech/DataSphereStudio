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

package com.webank.wedatasphere.dss.framework.workspace.dao;

import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

/**
 * created by cooperyang on 2020/3/18
 * Description:
 */
@Mapper
public interface DSSWorkspaceProjectMapper {





    /*@Select("select * from dss_project where workspace_id = #{workspaceId} and name = #{projectName}")
    @Results(id ="dss_project_map", value = {
            @Result(property = "workspaceId", column = "workspace_id"),
            @Result(property = "isPersonal", column = "is_personal"),
            @Result(property = "id", column = "id"),
            @Result(property = "updateTime",column = "update_time")
    })
    DSSPersonalProject searchPersonalProjectInWorkspace(@Param("workspaceId") int workspaceId,
                                                        @Param("username") String username, @Param("projectName") String projectName);*/


    @Insert({
            "<script>",
            "insert into dss_project_user",
            "(project_id, username, workspace_id, priv, last_update_time)",
            "values",
            "<foreach collection='usernames' item='username' open='(' separator='),(' close=')'>",
            " #{projectId}, #{username}, #{workspaceId}, #{priv}, #{lastUpdateTime}",
            "</foreach>",
            "</script>"
    })
    void setProjectPriv(@Param("workspaceId") int workspaceId, @Param("projectId") Long projectId,
                        @Param("usernames") List<String> usernames, @Param("priv") int priv, @Param("lastUpdateTime") Date date);



    /*@Select("select * from dss_project where id in (" +
            "select project_id from dss_project_user where " +
            "workspace_id = #{workspaceId} and username = #{username} and priv >= 1" +
            ") and is_personal = 0 or username = #{username} and workspace_id = #{workspaceId} and is_personal = 0")
    @ResultMap(value = "dss_project_map")
    List<DWSProject> listCooperateProjects(@Param("workspaceId") int workspaceId, @Param("username") String username);*/

   @Select("select priv from dss_project_user where workspace_id = #{workspaceId} " +
           "and username = #{username} " +
           "and project_id = #{projectId}")
    int selectPriv(@Param("projectId") Long projectId, @Param("workspaceId") int workspaceId, @Param("username") String username);


    /*@Select("select * from dss_flow where project_id in " +
            "(select distinct project_id from dss_project_user " +
            "where workspace_id = #{workspaceId} and priv >=1 and username = #{username}) " +
            "or id in " +
            "(select flow_id from dss_flow_user where workspace_id = #{workspaceId} and priv >= 1 " +
            "and username = #{username})")
    @Results({
            @Result(property = "creatorID", column = "creator_id"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "projectID", column = "project_id"),
            @Result(property = "isRootFlow", column = "is_root_flow")
    })
    List<DSSFlow> getReleasedWorkflows(@Param("workspaceId") int workspaceId,
                                       @Param("username") String username);
*/
    @Select("select name from dss_project where id = #{projectId}")
    String getProjectNameById(@Param("projectId") Long projectId);

    @Select("select max(version) from dss_flow_version where flow_id = #{flowId}")
    String getFlowLatestVersion(@Param("flowId") Long flowId);

    @Select("select username from dss_flow_user where flow_id = #{flowId} and project_id = #{projectId} and workspace_id = #{workspaceId}")
    List<String> getAccessUsersByFlowId(@Param("flowId") Long flowId, @Param("projectId") Long projectId,
                                        @Param("workspaceId") int workspaceId);

    @Delete("delete from dss_flow_user where workspace_id = #{workspaceId} and project_id = #{projectId} and flow_id = #{flowId}")
    void deleteAllWorkflowPriv(@Param("workspaceId") int workspaceId,
                               @Param("projectId") int projectId,
                               @Param("flowId") int flowId);

    @Delete("delete from dss_project_user where workspace_id = #{workspaceId} and project_id = #{projectId}")
    void deleteAllProjectPriv(@Param("workspaceId") int workspaceId, @Param("projectId") int projectId);

    @Insert({
            "<script>",
            "insert into dss_flow_user",
            "(workspace_id, project_id, flow_id, username, priv, last_update_time)",
            "values",
            "<foreach collection='accessUsers' item='accessUser' open='(' separator='),(' close=')'>",
            " #{workspaceId}, #{projectId}, #{flowId}, #{accessUser}, #{priv}, #{updateTime}",
            "</foreach>",
            "</script>"
    })
    void setWorkflowPriv(@Param("workspaceId") int workspaceId,
                         @Param("projectId") int projectId,
                         @Param("flowId") int flowId,
                         @Param("accessUsers") List<String> accessUsers,
                         @Param("priv") int priv,
                         @Param("updateTime") Date updateTime);

    @Select("select name from dss_flow where id = #{flowId}")
    String getFlowNameById(@Param("flowId") int flowId);

    @Select("select id from dss_project where name = #{projectName}")
    int getProjectIdByName(@Param("projectName") String projectName);

  /*  @Select("select * from dss_flow_schedule_info where flow_id = #{flowId}")
    @Results({
            @Result(property = "scheduleTime", column = "schedule_time"),
            @Result(property = "alarmLevel", column = "alarm_level"),
            @Result(property = "alarmUserEmails", column = "alarm_user_emails")
    })
    DSSReleasedFlowVO.ScheduleInfo getScheduleInfo(@Param("flowId") Long flowId);
*/


    @Insert("insert into dss_flow_schedule_info(flow_id, schedule_time, alarm_user_emails, alarm_level) " +
            "values(#{flowId},#{scheduleTime}, #{alarmEmails}, #{alarmLevel})")
    void setScheduleInfo(@Param("scheduleTime") String scheduleTime, @Param("alarmEmails") String alarmEmails,
                         @Param("alarmLevel") String alarmLevel, @Param("flowId") int flowId);

    @Delete("delete from dss_flow_schedule_info where flow_id = #{flowId}")
    void deleteScheduleInfo(@Param("flowId") int flowId);

    @Select("select username from dss_project_user  WHERE project_id = #{projectId} and priv = 1")
    List<String> getAccessUsersByProjectId(@Param("projectId") Long projectId);


    @Select("select username from dss_project_user  WHERE project_id = #{projectId} and priv = 2")
    List<String> getEditUsersByProjectId(Long id);

    @Select("select project_id from dss_project_version where id = #{projectVersionId}")
    Long getProjectIdByVersionId(@Param("projectVersionId") Long projectVersionId);

    @Update("update dss_project set workspace_id = #{workspaceId} where id = #{projectId}")
    void setWorkspaceIdForProject(@Param("workspaceId") Integer workspaceId, @Param("projectId") Long projectId);
}
