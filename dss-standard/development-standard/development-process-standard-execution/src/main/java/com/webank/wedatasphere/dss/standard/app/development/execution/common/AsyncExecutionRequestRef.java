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
import com.webank.wedatasphere.dss.standard.app.development.execution.core.ExecutionRequestRefContext;
import java.util.Map;

/**
 * Created by enjoyyin on 2021/1/25.
 */
public interface AsyncExecutionRequestRef extends ExecutionRequestRef {

    ExecutionRequestRefContext getExecutionRequestRefContext();

    void setExecutionRequestRefContext(ExecutionRequestRefContext executionRequestRefContext);

    void setProjectName(String projectName);

    void setOrchestratorName(String orchestratorName);

    void setJobContent(Map<String, Object> jobContent);

    void setName(String name);

    void setType(String type);

}
