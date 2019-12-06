package com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.parser;

import com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.entity.AzkabanSchedulerProject;
import com.webank.wedatasphere.dss.appjoint.scheduler.entity.AbstractSchedulerProject;
import com.webank.wedatasphere.dss.appjoint.scheduler.parser.AbstractProjectParser;
import com.webank.wedatasphere.dss.appjoint.scheduler.parser.FlowParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Created by allenlliu on 2019/9/16.
 */

public class AzkabanProjectParser extends AbstractProjectParser {

    public AzkabanProjectParser(){
        FlowParser[] flowParsers = {new AzkabanFlowParser()};
        setFlowParsers(flowParsers);
    }

    private static final Logger logger = LoggerFactory.getLogger(AzkabanProjectParser.class);

    @Override
    protected AbstractSchedulerProject createSchedulerProject() {
        return new AzkabanSchedulerProject();
    }

    @Override
    public void setFlowParsers(FlowParser[] flowParsers) {
        super.setFlowParsers(flowParsers);
    }

}
