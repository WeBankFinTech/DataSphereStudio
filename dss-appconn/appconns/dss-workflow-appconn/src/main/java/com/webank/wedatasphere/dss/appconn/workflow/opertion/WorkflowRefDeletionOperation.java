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

package com.webank.wedatasphere.dss.appconn.workflow.opertion;

import com.webank.wedatasphere.dss.appconn.workflow.utils.Utils;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.exception.DSSRuntimeException;
import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorRefConstant;
import com.webank.wedatasphere.dss.standard.app.development.operation.AbstractDevelopmentOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefDeletionOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.OnlyDevelopmentRequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;


public class WorkflowRefDeletionOperation
        extends AbstractDevelopmentOperation<OnlyDevelopmentRequestRef.RefJobContentRequestRefImpl, ResponseRef>
        implements RefDeletionOperation<OnlyDevelopmentRequestRef.RefJobContentRequestRefImpl> {

    @Override
    public ResponseRef deleteRef(OnlyDevelopmentRequestRef.RefJobContentRequestRefImpl requestRef) throws ExternalOperationFailedException {
        String userName = requestRef.getUserName();
        Long flowId = (Long) requestRef.getRefJobContent().get(OrchestratorRefConstant.ORCHESTRATION_ID_KEY);
        try {
            Utils.getDefaultWorkflowManager().deleteWorkflow(userName, flowId);
        } catch (DSSErrorException e) {
            throw new DSSRuntimeException(16003, "调用workflowManager删除workflow出现异常！", e);
        }
        return ResponseRef.newInternalBuilder().success();
    }

}
