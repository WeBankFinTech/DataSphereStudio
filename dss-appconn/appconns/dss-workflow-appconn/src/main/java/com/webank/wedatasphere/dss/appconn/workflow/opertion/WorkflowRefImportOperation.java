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
import com.webank.wedatasphere.dss.common.exception.DSSRuntimeException;
import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorRefConstant;
import com.webank.wedatasphere.dss.standard.app.development.operation.AbstractDevelopmentOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefImportOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.ImportRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.RefJobContentResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.ThirdlyRequestRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.dss.workflow.entity.DSSFlowImportParam;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkflowRefImportOperation
        extends AbstractDevelopmentOperation<ThirdlyRequestRef.ImportWitContextRequestRefImpl, RefJobContentResponseRef>
        implements RefImportOperation<ThirdlyRequestRef.ImportWitContextRequestRefImpl> {

    @Override
    public RefJobContentResponseRef importRef(ThirdlyRequestRef.ImportWitContextRequestRefImpl requestRef) throws ExternalOperationFailedException {
        DSSFlowImportParam dssFlowImportParam = new DSSFlowImportParam();
        dssFlowImportParam.setProjectID(requestRef.getRefProjectId());
        dssFlowImportParam.setProjectName(requestRef.getProjectName());
        dssFlowImportParam.setUserName(requestRef.getUserName());
        dssFlowImportParam.setOrcVersion(requestRef.getNewVersion());
        dssFlowImportParam.setWorkspace(requestRef.getWorkspace());
        dssFlowImportParam.setContextId(requestRef.getContextId());

        List<DSSFlow> responseFlowInfos;
        try {
            responseFlowInfos = Utils.getDefaultWorkflowManager().importWorkflow(requestRef.getUserName(),
                    (String) requestRef.getResourceMap().get(ImportRequestRef.RESOURCE_ID_KEY),
                    (String) requestRef.getResourceMap().get(ImportRequestRef.RESOURCE_VERSION_KEY),
                    dssFlowImportParam, requestRef.getDSSLabels());
        } catch (Exception e) {
            throw new DSSRuntimeException(16005, "调用workflowManager导入workflow出现异常！", e);
        }

        if (CollectionUtils.isEmpty(responseFlowInfos)) {
            return RefJobContentResponseRef.newBuilder()
                    .error("Empty workflow returned from workflow server, please ask admin for help!");
        }
        Map<String, Object> refMap = new HashMap<>(2);
        responseFlowInfos.forEach(flow -> {
            refMap.put(OrchestratorRefConstant.ORCHESTRATION_ID_KEY, flow.getId());
            refMap.put(OrchestratorRefConstant.ORCHESTRATION_CONTENT_KEY, flow.getFlowJson());
        });
        return RefJobContentResponseRef.newBuilder().setRefJobContent(refMap).success();
    }
}
