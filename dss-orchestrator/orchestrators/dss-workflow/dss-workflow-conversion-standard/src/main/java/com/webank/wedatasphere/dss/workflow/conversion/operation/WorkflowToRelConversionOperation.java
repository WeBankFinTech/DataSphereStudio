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

import com.webank.wedatasphere.dss.orchestrator.converter.standard.operation.DSSToRelConversionOperation;
import com.webank.wedatasphere.dss.orchestrator.converter.standard.ref.DSSToRelConversionRequestRef;
import com.webank.wedatasphere.dss.orchestrator.converter.standard.ref.ProjectToRelConversionRequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.CommonResponseRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.utils.AppStandardClassUtils;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.dss.workflow.conversion.entity.ConvertedRel;
import com.webank.wedatasphere.dss.workflow.conversion.entity.PreConversionRelImpl;
import com.webank.wedatasphere.dss.workflow.core.WorkflowFactory;
import com.webank.wedatasphere.dss.workflow.core.entity.Workflow;
import com.webank.wedatasphere.dss.workflow.core.json2flow.AbstractJsonToFlowParser;
import com.webank.wedatasphere.dss.workflow.core.json2flow.JsonToFlowParser;
import com.webank.wedatasphere.dss.workflow.core.json2flow.parser.WorkflowParser;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class WorkflowToRelConversionOperation extends DSSToRelConversionOperation {

    private List<WorkflowToRelConverter> workflowToRelConverters;
    private WorkflowToRelSynchronizer workflowToRelSynchronizer;

    public WorkflowToRelConversionOperation(){
    }

    @Override
    public void init() {
        String appConnName = getConversionService().getAppStandard().getAppConnName();
        workflowToRelConverters = AppStandardClassUtils.getInstance(appConnName).getInstances(WorkflowToRelConverter.class).stream().sorted(Comparator.comparingInt(WorkflowToRelConverter::getOrder))
            .collect(Collectors.toList());
        workflowToRelSynchronizer = AppStandardClassUtils.getInstance(appConnName).getInstanceOrWarn(WorkflowToRelSynchronizer.class);
        workflowToRelSynchronizer.setAppInstance(getConversionService().getAppInstance());
        workflowToRelSynchronizer.setSSORequestService(getConversionService().getSSORequestService());
        JsonToFlowParser parser = WorkflowFactory.INSTANCE.getJsonToFlowParser();
        if(parser instanceof AbstractJsonToFlowParser) {
            String packageName = WorkflowParser.class.getPackage().getName();
            List<WorkflowParser> workflowParsers = AppStandardClassUtils.getInstance(appConnName).getInstances(WorkflowParser.class).stream()
                .filter(p -> !p.getClass().getName().startsWith(packageName)).collect(Collectors.toList());
            ((AbstractJsonToFlowParser) parser).addWorkflowParsers(workflowParsers);
        }
    }

    @Override
    public ResponseRef convert(DSSToRelConversionRequestRef ref) {
        List<Workflow> workflows;
        if(ref instanceof ProjectToRelConversionRequestRef) {
            ProjectToRelConversionRequestRef projectRef = (ProjectToRelConversionRequestRef) ref;
            workflows = projectRef.getDSSOrcList().stream().map(flow -> WorkflowFactory.INSTANCE.getJsonToFlowParser().parse((DSSFlow) flow))
                .collect(Collectors.toList());
        } else {
            return CommonResponseRef.error("Not support ref " + ref.getClass().getSimpleName());
        }
        ConvertedRel convertedRel = tryConvert(workflows, ref);
        trySync(convertedRel);
        return CommonResponseRef.success("All workflow convert succeed!");
    }

    protected ConvertedRel tryConvert(List<Workflow> workflows, DSSToRelConversionRequestRef ref){
        PreConversionRelImpl rel = new PreConversionRelImpl();
        rel.setWorkflows(workflows);
        rel.setDSSToRelConversionRequestRef(ref);
        ConvertedRel convertedRel = null;
        for (WorkflowToRelConverter workflowToRelConverter: workflowToRelConverters) {
            if(convertedRel == null) {
                convertedRel = workflowToRelConverter.convertToRel(rel);
            } else {
                convertedRel = workflowToRelConverter.convertToRel(convertedRel);
            }
        }
        return convertedRel;
    }

    protected void trySync(ConvertedRel convertedRel) {
        workflowToRelSynchronizer.syncToRel(convertedRel);
    }
}
