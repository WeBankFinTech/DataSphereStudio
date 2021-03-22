
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

package com.webank.wedatasphere.dss.standard.app.development.execution.core;

import com.webank.wedatasphere.dss.standard.app.development.execution.ExecutionRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.execution.common.AsyncExecutionResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.execution.common.CompletedExecutionResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.execution.common.RefExecutionAction;
import com.webank.wedatasphere.dss.standard.app.development.execution.conf.RefExecutionConfiguration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * created by cooperyang on 2019/9/26
 * Description:
 */
public abstract class CallbackLongTermRefExecutionOperation extends LongTermRefExecutionOperation {

    protected Map<RefExecutionAction, AsyncExecutionResponseRef> asyncResponses = new ConcurrentHashMap<RefExecutionAction, AsyncExecutionResponseRef>();

    public String getCallbackURL(ExecutionRequestRefContext executionRequestRefContext){
        String urlSuffix = RefExecutionConfiguration.CALL_BACK_URL().getValue();
        return executionRequestRefContext.getGatewayUrl() + urlSuffix;
    }

    public abstract void acceptCallback(Map<String, Object> callbackMap);

    protected void markCompleted(RefExecutionAction action, CompletedExecutionResponseRef resultResponse) {
        AsyncExecutionResponseRef response = asyncResponses.get(action);
        asyncResponses.remove(action);
        response.setCompleted(resultResponse);
    }

    @Override
    protected AsyncExecutionResponseRef createAsyncResponseRef(ExecutionRequestRef requestRef, RefExecutionAction action) {
        AsyncExecutionResponseRef response = super.createAsyncResponseRef(requestRef, action);
        response.setAskStatePeriod(RefExecutionConfiguration.CALLBACK_REF_EXECUTION_REFRESH_INTERVAL().getValue().toLong());
        asyncResponses.put(action, response);
        return response;
    }
}
