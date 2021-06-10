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

package com.webank.wedatasphere.dss.framework.release.service;

import com.webank.wedatasphere.dss.common.entity.DSSLabel;
import com.webank.wedatasphere.dss.framework.release.entity.orchestrator.OrchestratorReleaseInfo;
import com.webank.wedatasphere.dss.framework.release.entity.project.ProjectInfo;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;

import java.util.List;
import java.util.Map;

/**
 * created by cooperyang on 2020/12/10
 * Description: publish是用来进行同步到调度系统
 */
public interface PublishService {

    void publish(String releaseUser, ProjectInfo projectInfo, List<OrchestratorReleaseInfo> orchestratorReleaseInfos,
                 DSSLabel dssLabel, Workspace workspace, boolean supportMultiEnv) throws Exception;


    void publish(Long projectId, Map<Long, Long> orchestratorInfoMap, DSSLabel dssLabel) throws Exception;

    void publish(String releaseUser, ProjectInfo projectInfo, Long orchestratorId, DSSLabel dssLabel, Workspace workspace) throws Exception;

    String getSchedulerWorkflowStatus(String workspaceName, String projectName, Long workflowId, String username)
        throws Exception;
}
