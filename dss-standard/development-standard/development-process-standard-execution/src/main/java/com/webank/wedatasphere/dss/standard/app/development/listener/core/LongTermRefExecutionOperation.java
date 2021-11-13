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

import com.webank.wedatasphere.dss.standard.app.development.ref.ExecutionRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefExecutionOperation;
import com.webank.wedatasphere.dss.standard.app.development.listener.async.RefExecutionStatusListener;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.AbstractRefExecutionAction;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.AsyncExecutionRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.AsyncExecutionResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.AsyncResponseRefImpl;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.CompletedExecutionResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.RefExecutionAction;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.RefExecutionState;
import com.webank.wedatasphere.dss.standard.app.development.listener.exception.AppConnExecutionWarnException;
import com.webank.wedatasphere.dss.standard.app.development.listener.scheduler.LongTermRefExecutionScheduler;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description: LongTermRefExecutionOperation is used to execute long-term tasks in external application systems.
 * Long-term task usually supports to execute asyncly and can fetch status and logs when it is submitted.
 *
 */
public abstract class LongTermRefExecutionOperation implements RefExecutionOperation {

    private static final Logger logger = LoggerFactory.getLogger(LongTermRefExecutionOperation.class);

    private List<RefExecutionStatusListener> refExecutionListener = new ArrayList<RefExecutionStatusListener>();
    private LongTermRefExecutionScheduler scheduler = SchedulerManager.getScheduler();

    public void addRefExecutionStatusListener(RefExecutionStatusListener refExecutionListener) {
        for(RefExecutionStatusListener listener : this.refExecutionListener){
            if (listener.getClass().equals(refExecutionListener.getClass())){
                return;
            }
        }
        this.refExecutionListener.add(refExecutionListener);
    }

    public LongTermRefExecutionScheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(LongTermRefExecutionScheduler scheduler) {
        this.scheduler = scheduler;
    }

    protected abstract RefExecutionAction submit(ExecutionRequestRef requestRef);

    public abstract RefExecutionState state(RefExecutionAction action);

    public abstract CompletedExecutionResponseRef result(RefExecutionAction action);


    protected ExecutionRequestRefContext createExecutionRequestRefContext(ExecutionRequestRef requestRef) {
        if(requestRef instanceof AsyncExecutionRequestRef) {
            return ((AsyncExecutionRequestRef) requestRef).getExecutionRequestRefContext();
        } else {
            throw new AppConnExecutionWarnException(75536, "Cannot find a available ExecutionRequestRefContext.");
        }
    }

    @Override
    public ResponseRef execute(ExecutionRequestRef requestRef) {
        refExecutionListener.forEach(l -> l.beforeSubmit(requestRef));
        RefExecutionAction action = submit(requestRef);
        if(action instanceof AbstractRefExecutionAction) {
            ((AbstractRefExecutionAction) action).setExecutionRequestRefContext(createExecutionRequestRefContext(requestRef));
        }
        refExecutionListener.forEach(l -> l.afterSubmit(requestRef, action));
        RefExecutionState state = state(action);
        if(state != null && state.isCompleted()) {
            CompletedExecutionResponseRef response = result(action);
            refExecutionListener.forEach(l -> l.afterCompletedExecutionResponseRef(requestRef, action, response));
            return response;
        } else {
            AsyncExecutionResponseRef response = createAsyncResponseRef(requestRef, action);
            response.setRefExecution(this);
            refExecutionListener.forEach(l -> l.afterAsyncResponseRef(response));
            response.addListener(r -> refExecutionListener.forEach(l -> l.afterCompletedExecutionResponseRef(requestRef, action, (CompletedExecutionResponseRef) r)));
            if(scheduler != null) {
                scheduler.addAsyncResponse(response);
            }
            return response;
        }
    }

    protected AsyncExecutionResponseRef createAsyncResponseRef(ExecutionRequestRef requestRef, RefExecutionAction action) {
        AsyncResponseRefImpl response =  new AsyncResponseRefImpl();
        response.setAction(action);
        response.setExecutionRequestRef(requestRef);
        return response;
    }

}
