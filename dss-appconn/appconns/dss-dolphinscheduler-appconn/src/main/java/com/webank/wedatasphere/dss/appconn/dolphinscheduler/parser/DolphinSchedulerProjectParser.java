package com.webank.wedatasphere.dss.appconn.dolphinscheduler.parser;

import com.webank.wedatasphere.dss.appconn.dolphinscheduler.entity.DolphinSchedulerProject;
import com.webank.wedatasphere.dss.appconn.schedule.core.entity.AbstractSchedulerProject;
import com.webank.wedatasphere.dss.appconn.schedule.core.parser.AbstractProjectParser;
import com.webank.wedatasphere.dss.appconn.schedule.core.parser.FlowParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The type Dolphin scheduler project parser.
 *
 * @author yuxin.yuan
 * @date 2021/04/29
 */
public class DolphinSchedulerProjectParser extends AbstractProjectParser {

    private static final Logger logger = LoggerFactory.getLogger(DolphinSchedulerProjectParser.class);

    public DolphinSchedulerProjectParser() {
        FlowParser[] flowParsers = {new DolphinSchedulerFlowParser()};
        setFlowParsers(flowParsers);
    }

    @Override
    protected AbstractSchedulerProject createSchedulerProject() {
        return new DolphinSchedulerProject();
    }

}
