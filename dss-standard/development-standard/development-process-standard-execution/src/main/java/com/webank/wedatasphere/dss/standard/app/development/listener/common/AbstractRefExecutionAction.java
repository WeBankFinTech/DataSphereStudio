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

package com.webank.wedatasphere.dss.standard.app.development.listener.common;

import com.webank.wedatasphere.dss.standard.app.development.listener.core.ExecutionRequestRefContext;


public abstract class AbstractRefExecutionAction implements RefExecutionAction, LongTermRefExecutionAction {

    private String id;
    private ExecutionRequestRefContext executionRequestRefContext;
    private RefExecutionState state;
    private int schedulerId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isKilledFlag() {
        return killedFlag;
    }

    public void setKilledFlag(boolean killedFlag) {
        this.killedFlag = killedFlag;
    }

    private boolean killedFlag = false;

    @Override
    public ExecutionRequestRefContext getExecutionRequestRefContext() {
        return executionRequestRefContext;
    }

    public void setExecutionRequestRefContext(ExecutionRequestRefContext executionRequestRefContext) {
        this.executionRequestRefContext = executionRequestRefContext;
    }

    public RefExecutionState getState() {
        return state;
    }

    public void setState(RefExecutionState state) {
        this.state = state;
    }

    @Override
    public int getSchedulerId() {
        return schedulerId;
    }

    @Override
    public void setSchedulerId(int schedulerId) {
        this.schedulerId = schedulerId;
    }
}
