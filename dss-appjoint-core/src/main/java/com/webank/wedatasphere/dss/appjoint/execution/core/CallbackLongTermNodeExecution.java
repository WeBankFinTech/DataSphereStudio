
/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package com.webank.wedatasphere.dss.appjoint.execution.core;


import com.webank.wedatasphere.dss.appjoint.execution.common.AsyncNodeExecutionResponse;
import com.webank.wedatasphere.dss.appjoint.execution.common.CompletedNodeExecutionResponse;
import com.webank.wedatasphere.dss.appjoint.execution.common.NodeExecutionAction;
import com.webank.wedatasphere.dss.appjoint.execution.conf.NodeExecutionConfiguration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * created by enjoyyin on 2019/9/26
 * Description:
 */
public abstract class CallbackLongTermNodeExecution extends LongTermNodeExecution {

    protected Map<NodeExecutionAction, AsyncNodeExecutionResponse> asyncResponses = new ConcurrentHashMap<NodeExecutionAction, AsyncNodeExecutionResponse>();

    public String getCallbackURL(NodeContext nodeContext){
        String urlSuffix = NodeExecutionConfiguration.CALL_BACK_URL().getValue();
        return nodeContext.getGatewayUrl() + urlSuffix;
    }

    public abstract void acceptCallback(Map<String, Object> callbackMap);

    protected void markCompleted(NodeExecutionAction action, CompletedNodeExecutionResponse resultResponse) {
        AsyncNodeExecutionResponse response = asyncResponses.get(action);
        asyncResponses.remove(action);
        response.getListeners().forEach(l -> l.onNodeExecutionCompleted(resultResponse));
        response.setCompleted(true);
    }

    @Override
    protected AsyncNodeExecutionResponse createAsyncNodeExecutionResponse(AppJointNode appJointNode, NodeContext context, NodeExecutionAction action) {
        AsyncNodeExecutionResponse response = super.createAsyncNodeExecutionResponse(appJointNode, context, action);
        response.setAskStatePeriod(NodeExecutionConfiguration.CALLBACK_NODE_EXECUTION_REFRESH_INTERVAL().getValue().toLong());
        asyncResponses.put(action, response);
        return response;
    }
}
