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
import com.webank.wedatasphere.dss.standard.app.development.operation.RefUpdateOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.ThirdlyRequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;


public class WorkflowRefUpdateOperation
        extends AbstractDevelopmentOperation<ThirdlyRequestRef.UpdateRequestRefImpl, ResponseRef>
        implements RefUpdateOperation<ThirdlyRequestRef.UpdateRequestRefImpl> {

    @Override
    public ResponseRef updateRef(ThirdlyRequestRef.UpdateRequestRefImpl requestRef) throws ExternalOperationFailedException {
        String userName = requestRef.getUserName();
        long flowId = (long) requestRef.getRefJobContent().get(OrchestratorRefConstant.ORCHESTRATION_ID_KEY);
        String flowName = (String) requestRef.getDSSJobContent().get(OrchestratorRefConstant.ORCHESTRATION_NAME);
        String description = (String) requestRef.getDSSJobContent().get(OrchestratorRefConstant.ORCHESTRATION_DESCRIPTION);
        String uses = (String) requestRef.getDSSJobContent().get(OrchestratorRefConstant.ORCHESTRATION_USES);
        try {
            Utils.getDefaultWorkflowManager().updateWorkflow(userName, flowId, flowName, description, uses);
        } catch (DSSErrorException e) {
            throw new DSSRuntimeException(16006, "调用workflowManager更新workflow出现异常！", e);
        }
        return ResponseRef.newInternalBuilder().success();
    }

}
