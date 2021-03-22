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

package com.webank.wedatasphere.dss.workflow.appconn.stage;

import com.webank.wedatasphere.dss.common.entity.DSSLabel;
import com.webank.wedatasphere.dss.common.protocol.RequestQueryWorkFlow;
import com.webank.wedatasphere.dss.standard.app.development.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.stage.GetRefStage;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.dss.workflow.common.protocol.ResponseQueryWorkflow;
import com.webank.wedatasphere.linkis.rpc.Sender;

import java.util.List;

/**
 * @author allenlliu
 * @date 2020/11/30 15:23
 */
public class DefaultWorkflowGetRefStage implements GetRefStage {

    private DevelopmentService developmentService;


    @Override
    public DSSFlow getDssFlowById(String userName, Long rootFlowId, List<DSSLabel> dssLabels) throws ExternalOperationFailedException {
        RequestQueryWorkFlow requestQueryWorkflow = new RequestQueryWorkFlow(userName, rootFlowId);
        //todo 先写死吧，后续要通过label来进行获取最精确的实例
        Sender sender = getAccurateSender(dssLabels);
        ResponseQueryWorkflow responseQueryWorkflow = (ResponseQueryWorkflow) sender.ask(requestQueryWorkflow);
        if (null != responseQueryWorkflow) {
            return responseQueryWorkflow.getDssFlow();
        } else {
            throw new ExternalOperationFailedException(100085, "Error ask workflow to query!", null);
        }
    }

    private Sender getAccurateSender(List<DSSLabel> dssLabels){
        for (DSSLabel dssLabel : dssLabels){
            if ("DEV".equalsIgnoreCase(dssLabel.getLabel())){
                return Sender.getSender("DSS-WORKFLOW-SERVER-DEV");
            }
        }
        return Sender.getSender("DSS-WORKFLOW-SERVER-PROD");
    }

    @Override
    public void setDevelopmentService(DevelopmentService service) {
        this.developmentService = service;
    }
}
