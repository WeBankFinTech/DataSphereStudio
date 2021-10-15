package com.webank.wedatasphere.dss.appconn.dolphinscheduler.parser;

import com.webank.wedatasphere.dss.appconn.dolphinscheduler.entity.DolphinSchedulerNode;
import com.webank.wedatasphere.dss.appconn.schedule.core.entity.SchedulerNode;
import com.webank.wedatasphere.dss.appconn.schedule.core.parser.AbstractNodeParser;
import com.webank.wedatasphere.dss.common.entity.node.DSSNode;

/**
 * The type Dolphin scheduler node parser.
 *
 * @author yuxin.yuan
 * @date 2021/04/29
 */
public class DolphinSchedulerNodeParser extends AbstractNodeParser {

    @Override
    public SchedulerNode parseNode(DSSNode dwsNode) {
        DolphinSchedulerNode schedulerNode = new DolphinSchedulerNode();
        schedulerNode.setDSSNode(dwsNode);
        return schedulerNode;
    }

    @Override
    public Boolean ifNodeCanParse(DSSNode dwsNode) {
        return true;
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
