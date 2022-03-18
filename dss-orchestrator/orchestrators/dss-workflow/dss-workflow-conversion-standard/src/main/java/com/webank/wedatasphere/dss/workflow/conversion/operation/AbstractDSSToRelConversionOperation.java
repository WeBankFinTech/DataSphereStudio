package com.webank.wedatasphere.dss.workflow.conversion.operation;

import com.webank.wedatasphere.dss.orchestrator.converter.standard.operation.DSSToRelConversionOperation;
import com.webank.wedatasphere.dss.orchestrator.converter.standard.ref.DSSToRelConversionRequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.utils.AppStandardClassUtils;
import com.webank.wedatasphere.dss.workflow.conversion.entity.ConvertedRel;
import com.webank.wedatasphere.dss.workflow.conversion.entity.PreConversionRel;
import com.webank.wedatasphere.dss.workflow.core.WorkflowFactory;
import com.webank.wedatasphere.dss.workflow.core.json2flow.AbstractJsonToFlowParser;
import com.webank.wedatasphere.dss.workflow.core.json2flow.JsonToFlowParser;
import com.webank.wedatasphere.dss.workflow.core.json2flow.parser.WorkflowParser;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author enjoyyin
 * @date 2022-03-16
 * @since 0.5.0
 */
public abstract class AbstractDSSToRelConversionOperation<K extends DSSToRelConversionRequestRef<K>>
    extends DSSToRelConversionOperation<K, ResponseRef> {

    private List<WorkflowToRelConverter> workflowToRelConverters;
    private WorkflowToRelSynchronizer workflowToRelSynchronizer;

    @Override
    protected String getAppConnName() {
        return getConversionService().getAppStandard().getAppConnName();
    }

    @Override
    public void init() {
        super.init();
        workflowToRelConverters = AppStandardClassUtils.getInstance(getAppConnName()).getInstances(WorkflowToRelConverter.class).stream().sorted(Comparator.comparingInt(WorkflowToRelConverter::getOrder))
                .collect(Collectors.toList());
        workflowToRelSynchronizer = AppStandardClassUtils.getInstance(getAppConnName()).getInstanceOrWarn(WorkflowToRelSynchronizer.class);
        workflowToRelSynchronizer.setDSSToRelConversionOperation(this);
        JsonToFlowParser parser = WorkflowFactory.INSTANCE.getJsonToFlowParser();
        if(parser instanceof AbstractJsonToFlowParser) {
            String packageName = WorkflowParser.class.getPackage().getName();
            List<WorkflowParser> workflowParsers = AppStandardClassUtils.getInstance(getAppConnName()).getInstances(WorkflowParser.class).stream()
                    .filter(p -> !p.getClass().getName().startsWith(packageName)).collect(Collectors.toList());
            ((AbstractJsonToFlowParser) parser).addWorkflowParsers(workflowParsers);
        }
    }

    protected abstract PreConversionRel getPreConversionRel(K ref);

    @Override
    public ResponseRef convert(K ref) {
        PreConversionRel preConversionRel = getPreConversionRel(ref);
        ConvertedRel convertedRel = tryConvert(preConversionRel);
        trySync(convertedRel);
        return ResponseRef.newInternalBuilder().success();
    }

    protected ConvertedRel tryConvert(PreConversionRel rel){
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
