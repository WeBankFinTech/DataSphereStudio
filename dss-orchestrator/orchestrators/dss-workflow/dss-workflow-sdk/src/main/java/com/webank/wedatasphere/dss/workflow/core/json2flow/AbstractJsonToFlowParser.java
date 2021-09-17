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

package com.webank.wedatasphere.dss.workflow.core.json2flow;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.webank.wedatasphere.dss.common.utils.ClassUtils;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.dss.workflow.core.entity.Workflow;
import com.webank.wedatasphere.dss.workflow.core.entity.WorkflowImpl;
import com.webank.wedatasphere.dss.workflow.core.json2flow.parser.WorkflowParser;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;


public abstract class AbstractJsonToFlowParser implements JsonToFlowParser {

    private List<WorkflowParser> workflowParsers;

    public List<WorkflowParser> getWorkflowParsers() {
        return workflowParsers;
    }

    public synchronized void addWorkflowParsers(List<WorkflowParser> workflowParsers) {
        if(workflowParsers == null || workflowParsers.isEmpty()) {
            return;
        }
        workflowParsers.forEach(WorkflowParser::init);
        if(this.workflowParsers == null) {
            this.workflowParsers  = new ArrayList<>();
        }
        this.workflowParsers.addAll(workflowParsers);
        this.workflowParsers = this.workflowParsers.stream().sorted(Comparator.comparingInt(WorkflowParser::getOrder)).collect(Collectors.toList());
    }

    @Override
    public void init() {
        if(workflowParsers == null) {
            workflowParsers = ClassUtils.getInstances(WorkflowParser.class);
        } else {
            workflowParsers.addAll(ClassUtils.getInstances(WorkflowParser.class));
        }
        workflowParsers = workflowParsers.stream().sorted(Comparator.comparingInt(WorkflowParser::getOrder)).collect(Collectors.toList());
        workflowParsers.forEach(WorkflowParser::init);
    }

    @Override
    public Workflow parse(DSSFlow dssFlow) {
        Workflow workflow = createWorkflow();
        BeanUtils.copyProperties(dssFlow, workflow, "children");
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(dssFlow.getFlowJson()).getAsJsonObject();
        for(WorkflowParser workflowParser : workflowParsers) {
            workflow = workflowParser.parse(jsonObject, workflow);
        }
        if(dssFlow.getChildren() != null) {
            Workflow parentWorkflow = workflow;
            List<Workflow> children = dssFlow.getChildren().stream().map(childFlow -> {
                Workflow childWorkflow = parse(childFlow);
                if(parentWorkflow instanceof WorkflowImpl) {
                    ((WorkflowImpl) childWorkflow).setParentWorkflow((WorkflowImpl) parentWorkflow);
                }
                return childWorkflow;
            }).collect(Collectors.toList());
            workflow.setChildren(children);
        }
        return workflow;
    }

    protected abstract Workflow createWorkflow();
}
