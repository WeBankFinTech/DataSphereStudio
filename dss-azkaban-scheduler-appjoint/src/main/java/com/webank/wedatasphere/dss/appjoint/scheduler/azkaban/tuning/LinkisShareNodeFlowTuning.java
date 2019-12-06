package com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.tuning;


import com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.entity.AzkabanSchedulerFlow;
import com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.entity.LinkisAzkabanShareNode;
import com.webank.wedatasphere.dss.appjoint.scheduler.entity.SchedulerFlow;
import com.webank.wedatasphere.dss.appjoint.scheduler.entity.ShareNode;
import com.webank.wedatasphere.dss.appjoint.scheduler.tuning.AbstractShareNodeFlowTuning;
import com.webank.wedatasphere.dss.appjoint.scheduler.tuning.NodeTuning;



public class LinkisShareNodeFlowTuning extends AbstractShareNodeFlowTuning {

    @Override
    public Boolean ifFlowCanTuning(SchedulerFlow schedulerFlow) {
      return schedulerFlow instanceof AzkabanSchedulerFlow;
    }

    @Override
    public int getOrder() {
        return 2;
    }

    @Override
    protected ShareNode createShareNode() {
        return new LinkisAzkabanShareNode();
    }

    @Override
    public NodeTuning[] getNodeTunings() {
        return new NodeTuning[0];
    }
}
