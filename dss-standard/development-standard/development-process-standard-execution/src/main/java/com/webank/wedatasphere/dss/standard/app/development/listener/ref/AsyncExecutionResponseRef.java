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

package com.webank.wedatasphere.dss.standard.app.development.listener.ref;

import com.webank.wedatasphere.dss.standard.app.development.listener.async.RefExecutionResponseListener;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.RefExecutionAction;
import com.webank.wedatasphere.dss.standard.app.development.listener.core.LongTermRefExecutionOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.AsyncResponseRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRefImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;


public interface AsyncExecutionResponseRef extends AsyncResponseRef {

    RefExecutionAction getAction();

    RefExecutionRequestRef getExecutionRequestRef();

    LongTermRefExecutionOperation getRefExecutionOperation();

    long getAskStatePeriod();

    void setCompleted(ExecutionResponseRef response);

    long getMaxLoopTime();

    static Builder newBuilder() {
        return new Builder();
    }

    class Builder {

        private final AsyncExecutionResponseRefImpl asyncResponseRef = new AsyncExecutionResponseRefImpl();

        public Builder setAction(RefExecutionAction action) {
            this.asyncResponseRef.action = action;
            return this;
        }

        public Builder addListener(RefExecutionResponseListener listener) {
            this.asyncResponseRef.listeners.add(listener);
            return this;
        }

        public Builder setRefExecutionOperation(LongTermRefExecutionOperation refExecutionOperation) {
            this.asyncResponseRef.refExecutionOperation = refExecutionOperation;
            return this;
        }

        public Builder setExecutionRequestRef(RefExecutionRequestRef requestRef) {
            this.asyncResponseRef.requestRef = requestRef;
            return this;
        }

        public Builder setMaxLoopTime(long maxLoopTime) {
            this.asyncResponseRef.maxLoopTime = maxLoopTime;
            return this;
        }

        public Builder setAskStatePeriod(long askStatePeriod) {
            this.asyncResponseRef.askStatePeriod = askStatePeriod;
            return this;
        }

        public Builder setAsyncExecutionResponseRef(AsyncExecutionResponseRef responseRef) {
            if(!(responseRef instanceof AsyncExecutionResponseRefImpl)) {
                return this;
            }
            AsyncExecutionResponseRefImpl asyncResponseRef = (AsyncExecutionResponseRefImpl) responseRef;
            if(this.asyncResponseRef.action == null) {
                this.asyncResponseRef.action = asyncResponseRef.action;
            }
            if(!asyncResponseRef.listeners.isEmpty()) {
                this.asyncResponseRef.listeners.addAll(asyncResponseRef.listeners);
            }
            if(this.asyncResponseRef.refExecutionOperation == null) {
                this.asyncResponseRef.refExecutionOperation = asyncResponseRef.refExecutionOperation;
            }
            if(this.asyncResponseRef.requestRef == null) {
                this.asyncResponseRef.requestRef = asyncResponseRef.requestRef;
            }
            if(this.asyncResponseRef.maxLoopTime <= 0) {
                this.asyncResponseRef.maxLoopTime = asyncResponseRef.maxLoopTime;
            }
            if(this.asyncResponseRef.askStatePeriod == 1000) {
                this.asyncResponseRef.askStatePeriod = asyncResponseRef.askStatePeriod;
            }
            if(this.asyncResponseRef.response == null) {
                this.asyncResponseRef.response = asyncResponseRef.response;
            }
            this.asyncResponseRef.startTime = asyncResponseRef.startTime;
            if(!this.asyncResponseRef.isCompleted) {
                this.asyncResponseRef.isCompleted = asyncResponseRef.isCompleted;
            }
            return this;
        }

        public AsyncExecutionResponseRef build() {
            return this.asyncResponseRef;
        }

        class AsyncExecutionResponseRefImpl extends ResponseRefImpl implements AsyncExecutionResponseRef {

            RefExecutionAction action;
            List<RefExecutionResponseListener> listeners = new ArrayList<>();
            LongTermRefExecutionOperation refExecutionOperation;
            RefExecutionRequestRef requestRef;
            ExecutionResponseRef response;
            long maxLoopTime;
            long askStatePeriod = 1000;
            long startTime = System.currentTimeMillis();
            boolean isCompleted = false;
            private final Object lock = new Object();

            public AsyncExecutionResponseRefImpl() {
                super("", 1, "", new HashMap<>(0));
            }

            @Override
            public RefExecutionAction getAction() {
                return action;
            }

            @Override
            public RefExecutionRequestRef getExecutionRequestRef() {
                return requestRef;
            }

            @Override
            public LongTermRefExecutionOperation getRefExecutionOperation() {
                return refExecutionOperation;
            }

            @Override
            public long getAskStatePeriod() {
                return askStatePeriod;
            }

            @Override
            public void setCompleted(ExecutionResponseRef response) {
                isCompleted = true;
                this.response = response;
                status = response.getStatus();
                responseBody = response.getResponseBody();
                synchronized (lock) {
                    lock.notifyAll();
                }
                listeners.forEach(l -> l.onRefExecutionCompleted(response));
            }

            @Override
            public long getMaxLoopTime() {
                return maxLoopTime;
            }

            @Override
            public long getStartTime() {
                return startTime;
            }

            @Override
            public ResponseRef getResponse() {
                return response;
            }

            @Override
            public void waitForCompleted() throws InterruptedException {
                synchronized (lock) {
                    while(!isCompleted) {
                        lock.wait(2000);
                    }
                }
            }

            @Override
            public void notifyMe(Consumer<ResponseRef> notifyListener) {
                addListener(notifyListener::accept);
            }

            @Override
            public Map<String, Object> toMap() {
                if(response != null) {
                    return response.toMap();
                }
                return super.toMap();
            }

            @Override
            public String getErrorMsg() {
                if(response != null) {
                    return response.getErrorMsg();
                }
                return super.errorMsg;
            }

            @Override
            public boolean isSucceed() {
                return response != null && response.isSucceed();
            }

            @Override
            public boolean isFailed() {
                return response != null && response.isFailed();
            }
        }

    }

}
