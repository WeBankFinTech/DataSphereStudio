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

package com.webank.wedatasphere.dss.workflow.core.builder;

import com.webank.wedatasphere.dss.common.entity.Resource;
import com.webank.wedatasphere.dss.workflow.core.entity.Workflow;
import com.webank.wedatasphere.dss.workflow.core.entity.WorkflowNode;
import java.util.Map;

/**
 * More conveniently to build a workflow.
 */
public interface WorkflowBuilder<T extends WorkflowBuilder> {

    T create(String name, String description);

    T addWorkflowNode(WorkflowNode node);

    T addWorkflowNode(String parentNode, WorkflowNode node);

    T addChild(Workflow child);

    T addChild(String parentNode, Workflow child);

    T addFlowResource(Resource resource);

    T addFlowProperties(Map<String, Object> properties);

    Workflow build();
    
}
