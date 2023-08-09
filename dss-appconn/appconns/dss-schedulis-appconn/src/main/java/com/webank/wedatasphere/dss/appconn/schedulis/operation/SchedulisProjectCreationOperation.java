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

package com.webank.wedatasphere.dss.appconn.schedulis.operation;

import com.webank.wedatasphere.dss.appconn.schedulis.SchedulisAppConn;
import com.webank.wedatasphere.dss.appconn.schedulis.service.AzkabanUserService;
import com.webank.wedatasphere.dss.appconn.schedulis.service.SchedulisProjectService;
import com.webank.wedatasphere.dss.appconn.schedulis.utils.AzkabanUtils;
import com.webank.wedatasphere.dss.appconn.schedulis.utils.SchedulisHttpUtils;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.standard.app.structure.AbstractStructureOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectCreationOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.DSSProjectContentRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.ProjectUpdateRequestRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.Map;

public class SchedulisProjectCreationOperation
        extends AbstractStructureOperation<DSSProjectContentRequestRef.DSSProjectContentRequestRefImpl, ProjectResponseRef>
        implements ProjectCreationOperation<DSSProjectContentRequestRef.DSSProjectContentRequestRefImpl> {

    private String projectUrl;

    private String managerUrl;

    public static final int WTSS_MAX_PROJECT_NAME_SIZE = 64;

    @Override
    protected String getAppConnName() {
        return SchedulisAppConn.SCHEDULIS_APPCONN_NAME;
    }

    @Override
    public void init() {
        super.init();
        this.projectUrl = getBaseUrl().endsWith("/") ? getBaseUrl() + "manager" : getBaseUrl() + "/manager";
        managerUrl = getBaseUrl().endsWith("/") ? getBaseUrl() + "manager" : getBaseUrl() + "/manager";
    }

    @Override
    public ProjectResponseRef createProject(DSSProjectContentRequestRef.DSSProjectContentRequestRefImpl requestRef) throws ExternalOperationFailedException {
        logger.info("begin to create project in schedulis, project name is {}.", requestRef.getDSSProject().getName());
        if (requestRef.getDSSProject().getName().length() > WTSS_MAX_PROJECT_NAME_SIZE) {
            throw new ExternalOperationFailedException(60021, "project name is too long, it must be less then " + WTSS_MAX_PROJECT_NAME_SIZE + " in schedulis! ");
        }
        if (CollectionUtils.isNotEmpty(requestRef.getDSSProjectPrivilege().getReleaseUsers())) {
            // 先校验运维用户是否存在于 Schedulis，如果不存在，则不能成功创建工程。
            requestRef.getDSSProjectPrivilege().getReleaseUsers().forEach(releaseUser -> {
                if (!AzkabanUserService.containsUser(releaseUser, getBaseUrl(), ssoRequestOperation, requestRef.getWorkspace())) {
                    throw new ExternalOperationFailedException(100323, "当前设置的发布用户: " + releaseUser + ", 在 Schedulis 系统中不存在，请在Schedulis创建该用户！");
                }
            });
        }
        Map<String, String> params = new HashMap<>(3);
        params.put("action", "create");
        params.put("name", requestRef.getDSSProject().getName());
        params.put("description", requestRef.getDSSProject().getDescription());
        try {
            String entStr = SchedulisHttpUtils.getHttpPostResult(projectUrl, params, ssoRequestOperation, requestRef.getWorkspace());
            logger.info("新建工程 {}, Schedulis 返回的信息是 {}.", requestRef.getDSSProject().getName(), entStr);
            String message = AzkabanUtils.handleAzkabanEntity(entStr);
            if (!"success".equals(message)) {
                throw new ExternalOperationFailedException(90008, "Schedulis 新建工程失败, 原因: " + message);
            }
        } catch (final Exception t) {
            logger.error("Failed to create project!", t);
            return ProjectResponseRef.newExternalBuilder().error(t);
        }
        // 绑定权限
        if(CollectionUtils.isNotEmpty(requestRef.getDSSProjectPrivilege().getReleaseUsers())) {
            ProjectUpdateRequestRef.ProjectUpdateRequestRefImpl updateRequestRef = new ProjectUpdateRequestRef.ProjectUpdateRequestRefImpl()
                    .setWorkspace(requestRef.getWorkspace()).setUserName(requestRef.getUserName()).setDSSProject(requestRef.getDSSProject())
                    .setDSSProjectPrivilege(requestRef.getDSSProjectPrivilege()).setAddedDSSProjectPrivilege(requestRef.getDSSProjectPrivilege());
            ((SchedulisProjectService) service).getProjectUpdateOperation().updateProject(updateRequestRef);
        }
        Long projectId = null;
        try {
            projectId = getSchedulisProjectId(requestRef.getDSSProject().getName(), requestRef);
        } catch (Exception e) {
            DSSExceptionUtils.dealWarnException(60051, "failed to get project id.", e,
                    ExternalOperationFailedException.class);
        }

        return ProjectResponseRef.newExternalBuilder().setRefProjectId(projectId).success();
    }

    /**
     * Get project ID.
     */
    public Long getSchedulisProjectId(String projectName,
                                      DSSProjectContentRequestRef.DSSProjectContentRequestRefImpl requestRef) {

        Map<String, Object> params = new HashMap<>(2);
        params.put("ajax", "getProjectId");
        params.put("project", projectName);
        long projectId = 0L;
        try {
            String content = SchedulisHttpUtils.getHttpGetResult(this.managerUrl, params, ssoRequestOperation, requestRef.getWorkspace());
            logger.info("Get Schedulis project id return str is {}.", content);
            Map map = DSSCommonUtils.COMMON_GSON.fromJson(content, Map.class);
            projectId = DSSCommonUtils.parseToLong(map.get("projectId"));
        } catch (final Throwable t) {
            DSSExceptionUtils.dealWarnException(60051, "failed to fetch project id in schedulis.", t,
                    ExternalOperationFailedException.class);
        }
        return projectId;
    }


}
