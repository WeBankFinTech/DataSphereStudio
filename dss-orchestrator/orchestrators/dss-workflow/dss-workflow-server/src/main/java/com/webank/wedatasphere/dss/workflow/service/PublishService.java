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

package com.webank.wedatasphere.dss.workflow.service;

import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.ResponseConvertOrchestrator;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.dss.workflow.entity.request.BatchPublishWorkflowRequest;

import java.util.Map;

public interface PublishService {


    String submitPublish(String publishUser, Long workFlowId,
        Map<String, Object> dssLabel, Workspace workspace, String comment) throws Exception;

    String batchSubmit(BatchPublishWorkflowRequest publishWorkflowRequest, Workspace workspace, String publishUser)throws DSSErrorException;

    ResponseConvertOrchestrator getStatus(String username, String taskId) throws Exception;

}
