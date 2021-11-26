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

package com.webank.wedatasphere.dss.workflow.core;

import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.utils.ClassUtils;
import com.webank.wedatasphere.dss.workflow.core.builder.WorkflowBuilder;
import com.webank.wedatasphere.dss.workflow.core.entity.Workflow;
import com.webank.wedatasphere.dss.workflow.core.entity.WorkflowImpl;
import com.webank.wedatasphere.dss.workflow.core.flow2json.FlowToJsonParser;
import com.webank.wedatasphere.dss.workflow.core.json2flow.AbstractJsonToFlowParser;
import com.webank.wedatasphere.dss.workflow.core.json2flow.JsonToFlowParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WorkflowFactoryImpl implements WorkflowFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowFactoryImpl.class);

    private JsonToFlowParser jsonToFlowParser;

    @Override
    public WorkflowBuilder createWorkflowBuilder() {
        return null;
    }

    @Override
    public JsonToFlowParser getJsonToFlowParser() {
        if(jsonToFlowParser != null) {
            return jsonToFlowParser;
        }
        synchronized (this) {
            if(jsonToFlowParser == null) {
                try {
                    jsonToFlowParser = ClassUtils.getInstance(JsonToFlowParser.class);
                } catch (DSSErrorException e) {
                    jsonToFlowParser = new AbstractJsonToFlowParser() {
                        @Override
                        protected Workflow createWorkflow() {
                            return new WorkflowImpl();
                        }
                    };
                }
                jsonToFlowParser.init();
                LOGGER.info("JsonToFlowParser is {}.", jsonToFlowParser.getClass().getSimpleName());
            }
        }
        return jsonToFlowParser;
    }

    @Override
    public FlowToJsonParser getFlowToJsonParser() {
        return null;
    }

    static WorkflowFactory createInstance() {
        WorkflowFactory workflowFactory = ClassUtils.getInstanceOrDefault(WorkflowFactory.class, new WorkflowFactoryImpl());
        LOGGER.info("WorkflowFactory is {}.", workflowFactory.getClass().getSimpleName());
        return workflowFactory;
    }
}
