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

package com.webank.wedatasphere.dss.workflow.core.json2flow.parser;

import com.google.gson.JsonObject;
import com.webank.wedatasphere.dss.workflow.core.entity.Workflow;
import com.webank.wedatasphere.dss.workflow.core.entity.WorkflowWithContextImpl;
import org.springframework.beans.BeanUtils;

public class WorkflowContextParser implements WorkflowParser {

    @Override
    public Workflow parse(JsonObject flowJson, Workflow workflow) {
        if (flowJson.has("contextID")) {
            String contextID = flowJson.get("contextID").getAsString();
            WorkflowWithContextImpl workflowWithContext = new WorkflowWithContextImpl();
            BeanUtils.copyProperties(workflow, workflowWithContext);
            workflowWithContext.setContextID(contextID);
            return workflowWithContext;
        } else {
            return workflow;
        }
    }

    @Override
    public int getOrder() {
        return 10;
    }
}
