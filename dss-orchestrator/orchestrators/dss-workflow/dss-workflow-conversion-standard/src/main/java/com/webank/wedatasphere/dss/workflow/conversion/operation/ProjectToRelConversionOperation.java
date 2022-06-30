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

package com.webank.wedatasphere.dss.workflow.conversion.operation;

import com.webank.wedatasphere.dss.orchestrator.converter.standard.ref.DSSToRelConversionRequestRef;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.dss.workflow.conversion.entity.PreConversionRel;
import com.webank.wedatasphere.dss.workflow.conversion.entity.ProjectPreConversionRelImpl;
import com.webank.wedatasphere.dss.workflow.core.WorkflowFactory;
import com.webank.wedatasphere.dss.workflow.core.entity.Workflow;

import java.util.List;
import java.util.stream.Collectors;


public class ProjectToRelConversionOperation
        extends AbstractDSSToRelConversionOperation<DSSToRelConversionRequestRef.ProjectToRelConversionRequestRefImpl> {

    @Override
    protected PreConversionRel getPreConversionRel(DSSToRelConversionRequestRef.ProjectToRelConversionRequestRefImpl ref) {
        List<Workflow> workflows = ref.getDSSOrcList().stream().map(flow -> WorkflowFactory.INSTANCE.getJsonToFlowParser()
                .parse((DSSFlow) flow)).collect(Collectors.toList());
        ProjectPreConversionRelImpl rel = new ProjectPreConversionRelImpl();
        rel.setWorkflows(workflows);
        rel.setDSSToRelConversionRequestRef(ref);
        return rel;
    }
}
