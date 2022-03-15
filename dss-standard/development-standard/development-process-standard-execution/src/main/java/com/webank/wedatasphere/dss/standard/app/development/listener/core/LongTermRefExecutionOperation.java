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

import com.webank.wedatasphere.dss.standard.app.development.listener.async.RefExecutionStatusListener;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.AbstractRefExecutionAction;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.RefExecutionAction;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.RefExecutionState;
import com.webank.wedatasphere.dss.standard.app.development.listener.ref.AsyncExecutionResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.listener.ref.ExecutionResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.listener.ref.RefExecutionRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.listener.scheduler.LongTermRefExecutionScheduler;
import com.webank.wedatasphere.dss.standard.app.development.operation.AbstractDevelopmentOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefExecutionOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: LongTermRefExecutionOperation is used to execute long-term tasks in external application systems.
 * Long-term task usually supports to execute asyncly and can fetch status and logs when it is submitted.
 *
 */
public abstract class LongTermRefExecutionOperation<K extends RefExecutionRequestRef<K>>
    extends AbstractDevelopmentOperation<K, ResponseRef> implements RefExecutionOperation<K> {

    private List<RefExecutionStatusListener<K>> refExecutionListener = new ArrayList<>();
    private LongTermRefExecutionScheduler scheduler = SchedulerManager.getScheduler();

    public void addRefExecutionStatusListener(RefExecutionStatusListener<K> refExecutionListener) {
        for(RefExecutionStatusListener<K> listener : this.refExecutionListener){
            if (listener.getClass().equals(refExecutionListener.getClass())){
                return;
            }
        }
        this.refExecutionListener.add(refExecutionListener);
    }

    protected abstract RefExecutionAction submit(K requestRef) throws ExternalOperationFailedException;

    public abstract RefExecutionState state(RefExecutionAction action) throws ExternalOperationFailedException;

    public abstract ExecutionResponseRef result(RefExecutionAction action) throws ExternalOperationFailedException;

    protected ExecutionRequestRefContext createExecutionRequestRefContext(K requestRef) {
        return requestRef.getExecutionRequestRefContext();
    }

    @Override
    public final ResponseRef execute(K requestRef) throws ExternalOperationFailedException {
        refExecutionListener.forEach(l -> l.beforeSubmit(requestRef));
        RefExecutionAction action = submit(requestRef);
        if(action instanceof AbstractRefExecutionAction) {
            ((AbstractRefExecutionAction) action).setExecutionRequestRefContext(createExecutionRequestRefContext(requestRef));
        }
        refExecutionListener.forEach(l -> l.afterSubmit(requestRef, action));
        RefExecutionState state = state(action);
        if(state != null && state.isCompleted()) {
            ExecutionResponseRef response = result(action);
            refExecutionListener.forEach(l -> l.afterCompletedExecutionResponseRef(requestRef, action, response));
            return response;
        } else {
            AsyncExecutionResponseRef oldResponse = createAsyncResponseRef(requestRef, action);
            AsyncExecutionResponseRef response = AsyncExecutionResponseRef.newBuilder().setAsyncExecutionResponseRef(oldResponse)
                    .setRefExecutionOperation(this)
                    .addListener(r -> refExecutionListener.forEach(l -> l.afterCompletedExecutionResponseRef(requestRef, action, r))).build();
            refExecutionListener.forEach(l -> l.afterAsyncResponseRef(response));
            scheduler.addAsyncResponse(response);
            return response;
        }
    }

    protected AsyncExecutionResponseRef createAsyncResponseRef(K requestRef, RefExecutionAction action) {
        return AsyncExecutionResponseRef.newBuilder().setAction(action)
            .setExecutionRequestRef(requestRef).build();
    }

}
