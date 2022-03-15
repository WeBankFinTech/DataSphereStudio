
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

package com.webank.wedatasphere.dss.standard.app.development.listener.core;

import com.webank.wedatasphere.dss.standard.app.development.listener.common.RefExecutionAction;
import com.webank.wedatasphere.dss.standard.app.development.listener.conf.RefExecutionConfiguration;
import com.webank.wedatasphere.dss.standard.app.development.listener.ref.AsyncExecutionResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.listener.ref.ExecutionResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.listener.ref.RefExecutionRequestRef;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public abstract class CallbackLongTermRefExecutionOperation<K extends RefExecutionRequestRef<K>>
        extends LongTermRefExecutionOperation<K> {

    protected Map<RefExecutionAction, AsyncExecutionResponseRef> asyncResponses = new ConcurrentHashMap<RefExecutionAction, AsyncExecutionResponseRef>();

    public String getCallbackURL(ExecutionRequestRefContext executionRequestRefContext){
        String urlSuffix = RefExecutionConfiguration.CALL_BACK_URL().getValue();
        return executionRequestRefContext.getGatewayUrl() + urlSuffix;
    }

    public abstract void acceptCallback(Map<String, Object> callbackMap);

    protected void markCompleted(RefExecutionAction action, ExecutionResponseRef resultResponse) {
        AsyncExecutionResponseRef response = asyncResponses.get(action);
        asyncResponses.remove(action);
        response.setCompleted(resultResponse);
    }

    @Override
    protected AsyncExecutionResponseRef createAsyncResponseRef(K requestRef, RefExecutionAction action) {
        AsyncExecutionResponseRef oldResponse = super.createAsyncResponseRef(requestRef, action);
        AsyncExecutionResponseRef response = AsyncExecutionResponseRef.newBuilder().setAsyncExecutionResponseRef(oldResponse)
            .setAskStatePeriod(RefExecutionConfiguration.CALLBACK_REF_EXECUTION_REFRESH_INTERVAL().getValue().toLong())
                .build();
        asyncResponses.put(action, response);
        return response;
    }
}
