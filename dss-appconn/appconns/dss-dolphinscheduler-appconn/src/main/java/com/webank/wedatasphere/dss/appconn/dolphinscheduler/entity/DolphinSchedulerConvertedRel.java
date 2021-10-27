package com.webank.wedatasphere.dss.appconn.dolphinscheduler.entity;

import com.webank.wedatasphere.dss.orchestrator.converter.standard.ref.ProjectToRelConversionRequestRef;
import com.webank.wedatasphere.dss.workflow.conversion.entity.ConvertedRel;
import com.webank.wedatasphere.dss.workflow.conversion.entity.PreConversionRel;
import com.webank.wedatasphere.dss.workflow.conversion.entity.PreConversionRelImpl;

/**
 * The type Dolphin scheduler converted rel.
 *
 * @author yuxin.yuan
 * @date 2021/10/27
 */
public class DolphinSchedulerConvertedRel extends PreConversionRelImpl implements ConvertedRel {

    public DolphinSchedulerConvertedRel(PreConversionRel rel) {
        setWorkflows(rel.getWorkflows());
        setDSSToRelConversionRequestRef(rel.getDSSToRelConversionRequestRef());
    }

    @Override
    public ProjectToRelConversionRequestRef getDSSToRelConversionRequestRef() {
        return (ProjectToRelConversionRequestRef)super.getDSSToRelConversionRequestRef();
    }

}
