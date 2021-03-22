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

package com.webank.wedatasphere.dss.framework.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.webank.wedatasphere.dss.common.entity.DSSLabel;
import com.webank.wedatasphere.dss.framework.project.entity.DSSProject;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectCreateRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectDeleteRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectModifyRequest;
import com.webank.wedatasphere.dss.framework.project.entity.vo.ProjectInfoVo;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectQueryRequest;
import com.webank.wedatasphere.dss.framework.project.entity.response.ProjectResponse;
import com.webank.wedatasphere.dss.framework.project.exception.DSSProjectErrorException;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;

import java.util.List;
import java.util.Map;

public interface DSSProjectService  extends IService<DSSProject> {

    /**
     * 新增工程
     * @param username
     * @param projectCreateRequest
     */
    public DSSProject createProject(String username, ProjectCreateRequest projectCreateRequest);

    /**
     * 修改工程
     * @param username
     * @param modifyRequest
     */
    public void modifyProject(String username, ProjectModifyRequest modifyRequest) throws DSSProjectErrorException;

    /**
     * 根据工程名称获取工程详情
     * @param name
     * @return
     */
    public DSSProject getProjectByName(String name);

    /**
     * 根据id获取工程详情
     * @param id
     * @return
     */
    public DSSProject getProjectById(Long id);

    /**
     * 查询工程list
     * @param projectRequest
     * @return
     */
    public List<ProjectResponse> getListByParam(ProjectQueryRequest projectRequest);

    /**
     * 获取工程名称和空间名称
     * @param id
     * @return
     */
    public ProjectInfoVo getProjectInfoById(Long id);

    void saveProjectRelation(DSSProject project, Map<AppInstance, Long> projectMap);

    Long getAppConnProjectId(Long dssProjectId, String appConnName, List<DSSLabel> dssLabels) throws Exception;

    void deleteProject(String username, ProjectDeleteRequest projectDeleteRequest);

    List<String> getProjectAbilities(String username);
}
