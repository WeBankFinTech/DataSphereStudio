package com.webank.wedatasphere.dss.appconn.dolphinscheduler.parser;

import com.webank.wedatasphere.dss.appconn.dolphinscheduler.entity.DolphinSchedulerSchedulerNode;
import com.webank.wedatasphere.dss.appconn.schedule.core.entity.SchedulerNode;
import com.webank.wedatasphere.dss.appconn.schedule.core.parser.AbstractNodeParser;
import com.webank.wedatasphere.dss.common.entity.node.DSSNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The type Dolphin scheduler node parser.
 *
 * @author yuxin.yuan
 * @date 2021/04/29
 */
public class DolphinSchedulerNodeParser extends AbstractNodeParser {

    private static final Logger logger = LoggerFactory.getLogger(DolphinSchedulerNodeParser.class);

    @Override
    public SchedulerNode parseNode(DSSNode dwsNode) {
        DolphinSchedulerSchedulerNode schedulerNode = new DolphinSchedulerSchedulerNode();
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
