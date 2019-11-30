package com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.parser;

import com.webank.wedatasphere.dss.appjoint.scheduler.parser.AbstractFlowParser;
import com.webank.wedatasphere.dss.appjoint.scheduler.parser.NodeParser;
import com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.entity.AzkabanSchedulerFlow;
import com.webank.wedatasphere.dss.common.entity.flow.DWSJSONFlow;
import com.webank.wedatasphere.dss.appjoint.scheduler.entity.SchedulerFlow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AzkabanFlowParser extends AbstractFlowParser {

    @Override
    protected SchedulerFlow createSchedulerFlow() {
        return new AzkabanSchedulerFlow();
    }

    @Override
    @Autowired
    public void setNodeParsers(NodeParser[] nodeParsers) {
        super.setNodeParsers(nodeParsers);
    }

    @Override
    public Boolean ifFlowCanParse(DWSJSONFlow flow) {
        return true;
    }


}
