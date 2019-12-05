package com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.parser;

import com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.entity.LinkisAzkabanSchedulerNode;
import com.webank.wedatasphere.dss.common.entity.node.DWSNode;
import com.webank.wedatasphere.dss.appjoint.scheduler.entity.SchedulerNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LinkisAzkabanNodeParser extends AzkabanNodeParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinkisAzkabanNodeParser.class);


    @Override
    public SchedulerNode parseNode(DWSNode dwsNode) {
        LinkisAzkabanSchedulerNode schedulerNode = new LinkisAzkabanSchedulerNode();
        schedulerNode.setDWSNode(dwsNode);
        return schedulerNode;
    }

    @Override
    public Boolean ifNodeCanParse(DWSNode dwsNode) {
        //预留
        return true;
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
