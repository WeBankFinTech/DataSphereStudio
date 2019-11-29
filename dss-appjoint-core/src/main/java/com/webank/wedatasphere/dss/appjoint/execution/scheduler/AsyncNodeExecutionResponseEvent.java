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

package com.webank.wedatasphere.dss.appjoint.execution.scheduler;

import com.webank.wedatasphere.dss.appjoint.execution.common.AsyncNodeExecutionResponse;
import com.webank.wedatasphere.dss.appjoint.execution.core.LongTermNodeExecution;
import com.webank.wedatasphere.linkis.common.listener.Event;

/**
 * Created by enjoyyin on 2019/11/13.
 */
public class AsyncNodeExecutionResponseEvent implements Event {

    private AsyncNodeExecutionResponse response;
    private long lastAskTime = 0;

    public AsyncNodeExecutionResponseEvent(AsyncNodeExecutionResponse response) {
        this.response = response;
    }

    public AsyncNodeExecutionResponse getResponse() {
        return response;
    }

    public void setResponse(AsyncNodeExecutionResponse response) {
        this.response = response;
    }

    public long getLastAskTime() {
        return lastAskTime;
    }

    public void setLastAskTime() {
        this.lastAskTime = System.currentTimeMillis();
    }
}
