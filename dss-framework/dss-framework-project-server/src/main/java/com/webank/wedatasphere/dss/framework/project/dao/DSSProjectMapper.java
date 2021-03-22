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
import com.webank.wedatasphere.dss.framework.project.entity.DSSProject;
import com.webank.wedatasphere.dss.framework.project.entity.po.DSSProjectPo;
import com.webank.wedatasphere.dss.framework.project.entity.po.ProjectRelationPo;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectQueryRequest;
import com.webank.wedatasphere.dss.framework.project.entity.vo.ProjectInfoVo;
import com.webank.wedatasphere.dss.framework.project.entity.vo.QueryProjectVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * created by cooperyang on 2020/9/30
 * Description:
 */
@Mapper
public interface DSSProjectMapper extends BaseMapper<DSSProject> {


    void addProject(DSSProjectPo dssProjectPo);


    @Select("select id from dss_project where `name` = #{projectName}")
    Long getProjectIdByName(@Param("projectName") String projectName);

    @Select("select `name` from dss_project where `id` = #{projectId}")
    String getProjectNameById(@Param("projectId") Long projectId);


    List<QueryProjectVo> getListByParam(ProjectQueryRequest projectRequest);

    /**
     * 获取工程详情：工程名称、空间名称
     */
    ProjectInfoVo getProjectInfoById(@Param("id") Long id);

    void saveProjectRelation(List<ProjectRelationPo> projectRelationPoList);

    @Select("select `appconn_instance_project_id` from dss_appconn_project_relation " +
            "where `project_id` = #{dssProjectId} and `appconn_instance_id` = #{appInstanceId}")
    Long getAppConnProjectId(@Param("appInstanceId")Long appInstanceId, @Param("dssProjectId")Long dssProjectId);


    @Update("update dss_project set `visible` = 0 where `id` = #{projectId}")
    void deleteProject(@Param("projectId")Long projectId);
}
