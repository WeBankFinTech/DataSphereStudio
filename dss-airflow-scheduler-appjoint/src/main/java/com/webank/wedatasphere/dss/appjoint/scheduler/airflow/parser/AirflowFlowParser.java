package com.webank.wedatasphere.dss.appjoint.scheduler.airflow.parser;

import com.webank.wedatasphere.dss.appjoint.scheduler.parser.AbstractFlowParser;
import com.webank.wedatasphere.dss.appjoint.scheduler.parser.NodeParser;
import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.entity.AirflowSchedulerFlow;
import com.webank.wedatasphere.dss.common.entity.flow.DSSJSONFlow;
import com.webank.wedatasphere.dss.appjoint.scheduler.entity.SchedulerFlow;
import java.util.ArrayList;


public class AirflowFlowParser extends AbstractFlowParser {

    public AirflowFlowParser(){
        ArrayList<NodeParser> list = new ArrayList<>();
        list.add(new LinkisAirflowNodeParser());
        list.add(new LinkisAirflowSendEmailNodeParser());
        NodeParser[] nodeParsers =new NodeParser[list.size()];
        list.toArray(nodeParsers);
        setNodeParsers(nodeParsers);
    }

    @Override
    protected SchedulerFlow createSchedulerFlow() {
        return new AirflowSchedulerFlow();
    }

    @Override
    public void setNodeParsers(NodeParser[] nodeParsers) {
        super.setNodeParsers(nodeParsers);
    }

    @Override
    public Boolean ifFlowCanParse(DSSJSONFlow flow) {
        return true;
    }


}
