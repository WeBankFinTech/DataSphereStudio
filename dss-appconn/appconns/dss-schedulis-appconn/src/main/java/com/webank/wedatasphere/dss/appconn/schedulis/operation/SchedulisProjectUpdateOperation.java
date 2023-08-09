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
import com.webank.wedatasphere.dss.appconn.schedulis.utils.SchedulisHttpUtils;
import com.webank.wedatasphere.dss.standard.app.structure.AbstractStructureOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectUpdateOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.ProjectUpdateRequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.Map;

public class SchedulisProjectUpdateOperation
        extends AbstractStructureOperation<ProjectUpdateRequestRef.ProjectUpdateRequestRefImpl, ResponseRef>
        implements ProjectUpdateOperation<ProjectUpdateRequestRef.ProjectUpdateRequestRefImpl> {

    private String managerUrl;

    @Override
    public void init() {
        super.init();
        managerUrl = mergeBaseUrl("manager");
    }

    @Override
    public ResponseRef updateProject(ProjectUpdateRequestRef.ProjectUpdateRequestRefImpl projectRef) {
        if (CollectionUtils.isNotEmpty(projectRef.getDSSProjectPrivilege().getReleaseUsers())) {
            // 先校验运维用户是否存在于 Schedulis，如果不存在，则不能成功创建工程。
            projectRef.getDSSProjectPrivilege().getReleaseUsers().forEach(releaseUser -> {
                if (!AzkabanUserService.containsUser(releaseUser, getBaseUrl(), ssoRequestOperation, projectRef.getWorkspace())) {
                    throw new ExternalOperationFailedException(100323, "当前设置的发布用户: " + releaseUser + ", 在 Schedulis 系统中不存在，请在Schedulis中创建该用户！");
                }
            });
        }
        // 更新description
        syncProjectDesc(projectRef);
        // 往 Schedulis 增加新增的用户
        addProjectPrivilege(projectRef);
        // 往 Schedulis 删除已去掉的用户
        removeRelProjectPrivilege(projectRef);
        return ResponseRef.newInternalBuilder().success();
    }

    private void addProjectPrivilege(ProjectUpdateRequestRef.ProjectUpdateRequestRefImpl projectRef) {
        String projectName = projectRef.getProjectName();
        if (projectRef.getAddedDSSProjectPrivilege() == null ||
                CollectionUtils.isEmpty(projectRef.getAddedDSSProjectPrivilege().getReleaseUsers())) {
            return;
        }
        projectRef.getAddedDSSProjectPrivilege().getReleaseUsers().forEach(releaseUser -> {
            String userId = AzkabanUserService.getUserId(releaseUser, getBaseUrl(), ssoRequestOperation, projectRef.getWorkspace());
            Map<String, Object> params = new HashMap<>(8);
            params.put("project", projectName);
            params.put("userId", userId);
            params.put("ajax", "ajaxAddProjectUserPermission");
            params.put("permissions[admin]", "false");
            params.put("permissions[read]", "true");
            params.put("permissions[write]", "true");
            params.put("permissions[execute]", "true");
            params.put("permissions[schedule]", "true");
            String response = SchedulisHttpUtils.getHttpGetResult(managerUrl, params, ssoRequestOperation, projectRef.getWorkspace());
            logger.info("for project {} add a accessUser {} response is {}.",
                    projectName, releaseUser, response);
        });
    }

    private void removeRelProjectPrivilege(ProjectUpdateRequestRef.ProjectUpdateRequestRefImpl projectRef) {
        String projectName = projectRef.getProjectName();
        if (projectRef.getRemovedDSSProjectPrivilege() == null ||
                CollectionUtils.isEmpty(projectRef.getRemovedDSSProjectPrivilege().getReleaseUsers())) {
            return;
        }
        projectRef.getRemovedDSSProjectPrivilege().getReleaseUsers().forEach(accessUser -> {
            Map<String, Object> params = new HashMap<>(3);
            params.put("project", projectName);
            params.put("userId", accessUser);
            params.put("ajax", "ajaxRemoveProjectAdmin");
            String response = SchedulisHttpUtils.getHttpGetResult(managerUrl, params, ssoRequestOperation, projectRef.getWorkspace());
            logger.info("for project {} remove a accessUser {} response is {}.", projectName, accessUser, response);
        });
    }

    private void syncProjectDesc(ProjectUpdateRequestRef.ProjectUpdateRequestRefImpl projectRef) {
        String projectName = projectRef.getProjectName();
        Map<String, Object> params = new HashMap<>();
        params.put("project", projectName);
        params.put("ajax", "changeDescription");
        params.put("description", projectRef.getDSSProject().getDescription());
        String response = SchedulisHttpUtils.getHttpGetResult(managerUrl, params, ssoRequestOperation, projectRef.getWorkspace());
        logger.info("for project {} update description, response is {}.", projectName, response);
    }

    @Override
    protected String getAppConnName() {
        return SchedulisAppConn.SCHEDULIS_APPCONN_NAME;
    }
}
