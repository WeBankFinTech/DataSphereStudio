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

package com.webank.wedatasphere.dss.orchestrator.publish.impl;

import com.webank.wedatasphere.dss.common.entity.project.DSSProject;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.common.utils.RpcAskUtils;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.RequestConvertOrchestrations;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.ResponseOperateOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.core.plugin.AbstractDSSOrchestratorPlugin;
import com.webank.wedatasphere.dss.orchestrator.publish.ConversionDSSOrchestratorPlugin;
import com.webank.wedatasphere.dss.sender.service.DSSSenderServiceFactory;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import org.apache.linkis.rpc.Sender;

import java.util.List;
import java.util.Map;


public class ConversionDSSOrchestratorPluginImpl extends AbstractDSSOrchestratorPlugin implements ConversionDSSOrchestratorPlugin {

    @Override
    public ResponseOperateOrchestrator convert(String userName,
                                               DSSProject project,
                                               Workspace workspace,
                                               Map<Long, Long> orchestrationIdMap,
                                               List<DSSLabel> dssLabels,
                                               String approvalId) {
        //1、发布DSS编排，如DSS工作流
        RequestConvertOrchestrations requestConvertOrchestrator = new RequestConvertOrchestrations();
        requestConvertOrchestrator.setOrchestrationIdMap(orchestrationIdMap);
        requestConvertOrchestrator.setProject(project);
        requestConvertOrchestrator.setWorkspace(workspace);
        requestConvertOrchestrator.setApprovalId(approvalId);
        requestConvertOrchestrator.setDSSLabels(dssLabels);
        requestConvertOrchestrator.setUserName(userName);
        Sender sender = DSSSenderServiceFactory.getOrCreateServiceInstance().getWorkflowSender(dssLabels);
        return RpcAskUtils.processAskException(sender.ask(requestConvertOrchestrator), ResponseOperateOrchestrator.class, RequestConvertOrchestrations.class);
    }
}
