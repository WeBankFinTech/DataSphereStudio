package com.webank.wedatasphere.dss.appconn.dolphinscheduler.parser;

import com.webank.wedatasphere.dss.appconn.dolphinscheduler.entity.DolphinSchedulerFlow;
import com.webank.wedatasphere.dss.appconn.schedule.core.entity.SchedulerFlow;
import com.webank.wedatasphere.dss.appconn.schedule.core.parser.AbstractFlowParser;
import com.webank.wedatasphere.dss.appconn.schedule.core.parser.NodeParser;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSJsonFlow;

import java.util.ArrayList;

/**
 * The type Dolphin scheduler flow parser.
 *
 * @author yuxin.yuan
 * @date 2021/04/29
 */
public class DolphinSchedulerFlowParser extends AbstractFlowParser {

    public DolphinSchedulerFlowParser() {
        ArrayList<NodeParser> list = new ArrayList<>();
        list.add(new DolphinSchedulerNodeParser());
        NodeParser[] nodeParsers = new NodeParser[list.size()];
        list.toArray(nodeParsers);
        setNodeParsers(nodeParsers);
    }

    @Override
    protected SchedulerFlow createSchedulerFlow() {
        return new DolphinSchedulerFlow();
    }

    @Override
    public void setNodeParsers(NodeParser[] nodeParsers) {
        super.setNodeParsers(nodeParsers);
    }

    @Override
    public Boolean ifFlowCanParse(DSSJsonFlow flow) {
        return true;
    }

}
