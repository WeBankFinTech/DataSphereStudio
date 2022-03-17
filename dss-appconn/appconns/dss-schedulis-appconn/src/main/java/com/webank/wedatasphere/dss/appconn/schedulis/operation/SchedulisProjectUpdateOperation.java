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
import com.webank.wedatasphere.dss.standard.app.structure.AbstractStructureOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectUpdateOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.ProjectUpdateRequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.apache.commons.collections4.CollectionUtils;

public class SchedulisProjectUpdateOperation
        extends AbstractStructureOperation<ProjectUpdateRequestRef.ProjectUpdateRequestRefImpl, ResponseRef>
        implements ProjectUpdateOperation<ProjectUpdateRequestRef.ProjectUpdateRequestRefImpl> {

    @Override
    public ResponseRef updateProject(ProjectUpdateRequestRef.ProjectUpdateRequestRefImpl projectRef) {
        if(CollectionUtils.isNotEmpty(projectRef.getReleaseUsers())) {
            // 先校验运维用户是否存在于 Schedulis，如果不存在，则不能成功创建工程。
            projectRef.getReleaseUsers().forEach(releaseUser -> {
                if (!AzkabanUserService.containsReleaseUser(releaseUser, getBaseUrl(), ssoRequestOperation, projectRef.getWorkspace())) {
                    throw new ExternalOperationFailedException(100323, "当前设置的发布用户: " + releaseUser + ", 在 Schedulis 系统中不存在，请联系 Schedulis 管理员创建该用户！");
                }
            });
        }
        logger.info("ignore the update operation in SchedulisAppConn.");
        return ResponseRef.newInternalBuilder().success();
    }

    @Override
    protected String getAppConnName() {
        return SchedulisAppConn.SCHEDULIS_APPCONN_NAME;
    }
}
