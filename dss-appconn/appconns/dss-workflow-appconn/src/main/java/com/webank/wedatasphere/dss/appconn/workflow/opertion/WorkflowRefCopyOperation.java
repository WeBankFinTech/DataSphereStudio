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

import com.webank.wedatasphere.dss.common.exception.DSSRuntimeException;
import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorRefConstant;
import com.webank.wedatasphere.dss.standard.app.development.operation.AbstractDevelopmentOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefCopyOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.RefJobContentResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.ThirdlyRequestRef;
import com.webank.wedatasphere.dss.workflow.DefaultWorkFlowManager;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import org.apache.linkis.DataWorkCloudApplication;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class WorkflowRefCopyOperation
        extends AbstractDevelopmentOperation<ThirdlyRequestRef.CopyWitContextRequestRefImpl, RefJobContentResponseRef>
        implements RefCopyOperation<ThirdlyRequestRef.CopyWitContextRequestRefImpl> {

    private final DefaultWorkFlowManager defaultWorkFlowManager = DataWorkCloudApplication.getApplicationContext().getBean(DefaultWorkFlowManager.class);

    @Override
    public RefJobContentResponseRef copyRef(ThirdlyRequestRef.CopyWitContextRequestRefImpl workflowCopyRequestRef) {
        Long appId = (Long) workflowCopyRequestRef.getRefJobContent().get(OrchestratorRefConstant.ORCHESTRATION_ID_KEY);
        String userName = workflowCopyRequestRef.getUserName();
        String contextIdStr = workflowCopyRequestRef.getContextId();
        String projectName = workflowCopyRequestRef.getProjectName();
        //插入version
        String version = workflowCopyRequestRef.getNewVersion();
        String description = (String) workflowCopyRequestRef.getRefJobContent().get(OrchestratorRefConstant.ORCHESTRATION_DESCRIPTION);
        Long targetProjectId = workflowCopyRequestRef.getRefProjectId();
        Optional<Object> nodeSuffix = Optional.ofNullable(workflowCopyRequestRef.getRefJobContent().get(OrchestratorRefConstant.ORCHESTRATION_NODE_SUFFIX));
        Optional<Object> newFlowName = Optional.ofNullable(workflowCopyRequestRef.getRefJobContent().get(OrchestratorRefConstant.ORCHESTRATION_NAME));
        DSSFlow dssFlow;
        try {
            dssFlow = defaultWorkFlowManager.copyRootFlowWithSubFlows(userName, appId, workflowCopyRequestRef.getWorkspace(),
                    projectName, contextIdStr, version, description, workflowCopyRequestRef.getDSSLabels(),
                    (String) nodeSuffix.orElse(null), (String) newFlowName.orElse(null), targetProjectId);
        } catch (Exception e) {
            throw new DSSRuntimeException(16001, "调用workflowManager复制workflow出现异常！", e);
        }
        Map<String, Object> refJobContent = new HashMap<>(2);
        refJobContent.put(OrchestratorRefConstant.ORCHESTRATION_ID_KEY, dssFlow.getId());
        refJobContent.put(OrchestratorRefConstant.ORCHESTRATION_CONTENT_KEY, dssFlow.getFlowJson());
        return RefJobContentResponseRef.newBuilder().setRefJobContent(refJobContent).success();
    }
}
