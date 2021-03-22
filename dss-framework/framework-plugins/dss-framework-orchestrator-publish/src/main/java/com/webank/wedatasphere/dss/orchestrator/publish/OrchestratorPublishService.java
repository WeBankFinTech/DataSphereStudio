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

package com.webank.wedatasphere.dss.orchestrator.publish;


import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.entity.DSSLabel;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.common.entity.project.DSSProject;
import com.webank.wedatasphere.dss.standard.common.exception.NoSuchAppInstanceException;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author allenlliu
 * @date 2020/12/3 10:36
 */
public interface OrchestratorPublishService {

    /**
     * 导出Orchestrator基本信息和工作流基本信息
     */
   Map<String,Object> exportOrchestrator(String userName,
                                         String workspaceName,
                                         Long orchestratorId,
                                         Long orcVersionId,
                                         String projectName,
                                         List<DSSLabel> dssLabels,
                                         boolean addOrcVersion, Workspace workspace) throws Exception;


    /**
     * 导入Orchestrator
     * @param userName 用户名
     * @param workspaceName 工作空间名
     * @param projectName 工程名
     * @param projectId 工程id
     * @param resourceId bml resourceId
     * @param version bml version
     * @param dssLabels dss标签
     * @param workspace
     * @return
     * @throws DSSErrorException
     * @throws IOException
     * @throws ExternalOperationFailedException
     */
   Long importOrchestrator(String userName,
                           String workspaceName,
                           String projectName,
                           Long projectId,
                           String resourceId,
                           String version,
                           List<DSSLabel> dssLabels,
                           Workspace workspace) throws Exception;


    void publishOrchestrator(String userName,
                             DSSProject project,
                             Workspace workspace,
                             List<Long> orcIdList,
                             List<DSSLabel> dssLabels
                             ) throws DSSErrorException, NoSuchAppInstanceException, ExternalOperationFailedException;


}
