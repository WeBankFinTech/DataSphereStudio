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

import com.webank.wedatasphere.dss.framework.release.entity.project.ProjectInfo;
import com.webank.wedatasphere.dss.framework.release.entity.resource.BmlResource;

import java.util.List;

/**
 * created by cooperyang on 2020/12/9
 * Description: ProjectService在发布框架中是为了能够与Project框架进行交互，然后获取到工程的相关信息
 * 但是现在一般是放在同一个JVM下的话，可以直接通过数据库进行读取
 */
public interface ProjectService {

    ProjectInfo getProjectInfoByOrcVersionId(Long orchestratorVersionId);

    String encapsulate(Long projectId, String releaseUser, List<BmlResource> resourceList, Long orchestratorId, Long orchestratorVersionId);

    ProjectInfo getProjectInfoById(Long projectId);

    ProjectInfo getProjectInfoByOrchestratorId(Long orchestratorId);

    String getOrchestratorName(Long orchestratorId, Long orchestratorVersionId);

    String getWorkspaceName(Long projectId);

    void updateProjectOrcInfo(Long projectId, Long orchestratorId, Long orchestratorVersionId);

    Long getAppIdByOrchestratorVersionId(Long orchestratorVersionId);
}
