package com.webank.wedatasphere.dss.appjoint.scheduler.airflow.parser;

import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.entity.LinkisAirflowSchedulerNode;
import com.webank.wedatasphere.dss.common.entity.node.DSSNode;
import com.webank.wedatasphere.dss.appjoint.scheduler.entity.SchedulerNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LinkisAirflowNodeParser extends AirflowNodeParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinkisAirflowNodeParser.class);


    @Override
    public SchedulerNode parseNode(DSSNode dssNode) {
        LinkisAirflowSchedulerNode schedulerNode = new LinkisAirflowSchedulerNode();
        schedulerNode.setDssNode(dssNode);
        return schedulerNode;
    }

    @Override
    public Boolean ifNodeCanParse(DSSNode dssNode) {
        //预留
        return true;
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
