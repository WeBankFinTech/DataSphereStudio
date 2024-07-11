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

package com.webank.wedatasphere.dss.framework.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.webank.wedatasphere.dss.common.entity.BmlResource;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.common.label.EnvDSSLabel;
import com.webank.wedatasphere.dss.framework.project.entity.DSSProjectDO;
import com.webank.wedatasphere.dss.framework.project.entity.request.*;
import com.webank.wedatasphere.dss.framework.project.entity.response.ProjectResponse;
import com.webank.wedatasphere.dss.framework.project.entity.vo.ProjectInfoVo;
import com.webank.wedatasphere.dss.framework.project.exception.DSSProjectErrorException;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;

import java.util.List;
import java.util.Map;

public interface DSSProjectService  extends IService<DSSProjectDO> {


    DSSProjectDO createProject(String username, ProjectCreateRequest projectCreateRequest);


    DSSProjectDO modifyProject(String username, ProjectModifyRequest modifyRequest) throws DSSProjectErrorException;

    /**
     * 旧工程导入到新环境的，修改新环境工程相关字段
     * @param updateProject  旧工程（91）
     * @param dbProject      数据库工程（246）
     * @throws Exception
     */
    void modifyOldProject(DSSProjectDO updateProject, DSSProjectDO dbProject);

    DSSProjectDO getProjectByName(String name);


    DSSProjectDO getProjectById(Long id);


    List<ProjectResponse> getListByParam(ProjectQueryRequest projectRequest);


    ProjectInfoVo getProjectInfoById(Long id);

    void saveProjectRelation(DSSProjectDO project, Map<AppInstance, Long> projectMap);

    Long getAppConnProjectId(Long dssProjectId, String appConnName, List<DSSLabel> dssLabels) throws Exception;

    Long getAppConnProjectId(Long appInstanceId, Long dssProjectId);

    void deleteProject(String username, ProjectDeleteRequest projectDeleteRequest, Workspace workspace, DSSProjectDO dssProjectDO)  throws Exception;

    List<String> getProjectAbilities(String username);


    boolean isDeleteProjectAuth(Long projectId, String username) throws DSSProjectErrorException ;

    /**
     * 查询已删除工程list
     * @param projectRequest
     * @return
     */
    List<ProjectResponse> getDeletedProjects(ProjectQueryRequest projectRequest);

    /**
     * 批量导出工程内的所有编排
     */
    BmlResource exportProject(ExportAllOrchestratorsReqest exportAllOrchestratorsReqest, String username, String proxyUser, Workspace workspace) throws Exception;

    /**
     * 批量导入编排到指定工程
     * @param  projectInfo 要导入的目标工程
     * @param importResource 导入的编排资源
     * @param username 导入人
     * @param checkCode  校验码
     * @param packageInfo 导入包路径或者文件名
     * @param envLabel 环境标签
     * @param workspace 导入工作空间

     */
    void importProject(ProjectInfoVo projectInfo, BmlResource importResource, String username,
                       String checkCode, String packageInfo, EnvDSSLabel envLabel, Workspace workspace) throws Exception;

    BmlResource exportOnlyProjectMeta(ExportAllOrchestratorsReqest exportAllOrchestratorsReqest,
                                  String username, String proxyUser, Workspace workspace) throws Exception;

    DSSProjectDO modifyProjectMeta(String username, ProjectModifyRequest projectModifyRequest) throws DSSProjectErrorException;

}
