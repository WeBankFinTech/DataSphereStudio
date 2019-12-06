package com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.parser;

import com.webank.wedatasphere.dss.appjoint.scheduler.parser.AbstractFlowParser;
import com.webank.wedatasphere.dss.appjoint.scheduler.parser.NodeParser;
import com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.entity.AzkabanSchedulerFlow;
import com.webank.wedatasphere.dss.common.entity.flow.DWSJSONFlow;
import com.webank.wedatasphere.dss.appjoint.scheduler.entity.SchedulerFlow;
import java.util.ArrayList;


public class AzkabanFlowParser extends AbstractFlowParser {

    public AzkabanFlowParser(){
        ArrayList<NodeParser> list = new ArrayList<>();
        list.add(new LinkisAzkabanNodeParser());
        list.add(new LinkisAzkabanSendEmailNodeParser());
        NodeParser[] nodeParsers =new NodeParser[list.size()];
        list.toArray(nodeParsers);
        setNodeParsers(nodeParsers);
    }

    @Override
    protected SchedulerFlow createSchedulerFlow() {
        return new AzkabanSchedulerFlow();
    }

    @Override
    public void setNodeParsers(NodeParser[] nodeParsers) {
        super.setNodeParsers(nodeParsers);
    }

    @Override
    public Boolean ifFlowCanParse(DWSJSONFlow flow) {
        return true;
    }


}
