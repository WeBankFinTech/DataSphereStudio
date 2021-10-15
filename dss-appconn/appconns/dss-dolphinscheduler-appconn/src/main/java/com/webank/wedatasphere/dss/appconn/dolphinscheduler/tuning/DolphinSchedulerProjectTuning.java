package com.webank.wedatasphere.dss.appconn.dolphinscheduler.tuning;

import com.webank.wedatasphere.dss.appconn.schedule.core.tuning.AbstractProjectTuning;
import com.webank.wedatasphere.dss.appconn.schedule.core.tuning.FlowTuning;

import java.util.ArrayList;

/**
 * The type Dolphin scheduler project tuning.
 *
 * @author yuxin.yuan
 * @date 2021/04/29
 */
public class DolphinSchedulerProjectTuning extends AbstractProjectTuning {

    public DolphinSchedulerProjectTuning() {
        ArrayList<FlowTuning> list = new ArrayList<>();
        list.add(new LinkisDolphinSchedulerFlowTuning());
        FlowTuning[] flowTunings = new FlowTuning[list.size()];
        setFlowTunings(list.toArray(flowTunings));
    }

}
