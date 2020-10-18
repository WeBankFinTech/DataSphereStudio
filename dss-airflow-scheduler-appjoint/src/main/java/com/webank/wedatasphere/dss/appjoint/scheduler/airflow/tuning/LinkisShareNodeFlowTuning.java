package com.webank.wedatasphere.dss.appjoint.scheduler.airflow.tuning;


import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.entity.AirflowSchedulerFlow;
import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.entity.LinkisAirflowShareNode;
import com.webank.wedatasphere.dss.appjoint.scheduler.entity.SchedulerFlow;
import com.webank.wedatasphere.dss.appjoint.scheduler.entity.ShareNode;
import com.webank.wedatasphere.dss.appjoint.scheduler.tuning.AbstractShareNodeFlowTuning;
import com.webank.wedatasphere.dss.appjoint.scheduler.tuning.NodeTuning;



public class LinkisShareNodeFlowTuning extends AbstractShareNodeFlowTuning {

    @Override
    public Boolean ifFlowCanTuning(SchedulerFlow schedulerFlow) {
      return schedulerFlow instanceof AirflowSchedulerFlow;
    }

    @Override
    public int getOrder() {
        return 2;
    }

    @Override
    protected ShareNode createShareNode() {
        return new LinkisAirflowShareNode();
    }

    @Override
    public NodeTuning[] getNodeTunings() {
        return new NodeTuning[0];
    }
}
