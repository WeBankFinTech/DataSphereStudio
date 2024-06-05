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

package com.webank.wedatasphere.dss.appconn.sparketl.execution;

import com.webank.wedatasphere.dss.standard.app.development.listener.common.AbstractRefExecutionAction;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.LongTermRefExecutionAction;

public class SparkEtlRefExecutionAction extends AbstractRefExecutionAction implements LongTermRefExecutionAction {

    private String applicationId;
    private String executionUser;
    private int schedulerId;

    public SparkEtlRefExecutionAction() {
    }

    public SparkEtlRefExecutionAction(String applicationId) {
        this.applicationId = applicationId;
    }

    public SparkEtlRefExecutionAction(String applicationId, String executionUser) {
        this.applicationId = applicationId;
        this.executionUser = executionUser;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getExecutionUser() {
        return executionUser;
    }

    public void setExecutionUser(String executionUser) {
        this.executionUser = executionUser;
    }

    @Override
    public void setSchedulerId(int schedulerId) {
        this.schedulerId = schedulerId;
    }

    @Override
    public int getSchedulerId() {
        return schedulerId;
    }
}
