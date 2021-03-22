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

package com.webank.wedatasphere.dss.standard.app.development.execution.common;

import com.webank.wedatasphere.dss.standard.app.development.execution.ExecutionRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.execution.async.RefExecutionResponseListener;
import com.webank.wedatasphere.dss.standard.app.development.execution.core.LongTermRefExecutionOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.AbstractResponseRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.AsyncResponseRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * created by cooperyang on 2019/9/25
 * Description: AsyncResponseRefImpl
 */
public class AsyncResponseRefImpl extends AbstractResponseRef implements AsyncResponseRef, AsyncExecutionResponseRef {

    private RefExecutionAction action;
    private ExecutionRequestRef requestRef;
    private LongTermRefExecutionOperation refExecution;
    private boolean isCompleted = false;
    private CompletedExecutionResponseRef response;
    private long startTime = System.currentTimeMillis();
    private long askStatePeriod = 1000;
    private long maxLoopTime = -1;
    private final Object lock = new Object();

    /**
     * 通过监听的方式，任务一旦完成，我们就通知上层
     */
    private List<RefExecutionResponseListener> listeners = new ArrayList<>();

    public AsyncResponseRefImpl(String responseBody, int status) {
        super(responseBody, status);
    }

    public AsyncResponseRefImpl() {
        super(null, -1);
    }

    @Override
    public RefExecutionAction getAction() {
        return action;
    }

    @Override
    public void setAction(RefExecutionAction action) {
        this.action = action;
    }

    @Override
    public void addListener(RefExecutionResponseListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void setExecutionRequestRef(ExecutionRequestRef requestRef) {
        this.requestRef = requestRef;
    }

    @Override
    public ExecutionRequestRef getExecutionRequestRef() {
        return requestRef;
    }

    @Override
    public LongTermRefExecutionOperation getRefExecution() {
        return refExecution;
    }

    @Override
    public void setRefExecution(LongTermRefExecutionOperation refExecution) {
        this.refExecution = refExecution;
    }

    @Override
    public long getAskStatePeriod() {
        return askStatePeriod;
    }

    @Override
    public void setAskStatePeriod(long askStatePeriod) {
        this.askStatePeriod = askStatePeriod;
    }

    @Override
    public boolean isCompleted() {
        return isCompleted;
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
        addListener(response -> notifyListener.accept(response));
    }

    @Override
    public void setCompleted(CompletedExecutionResponseRef response) {
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
    public void setMaxLoopTime(long maxLoopTime) {
        this.maxLoopTime = maxLoopTime;
    }

    @Override
    public long getStartTime() {
        return startTime;
    }

    @Override
    public Map<String, Object> toMap() {
        if(response != null) {
            return response.toMap();
        }
        return null;
    }

    @Override
    public String getErrorMsg() {
        if(response != null) {
            return response.getErrorMsg();
        }
        return null;
    }
}
