package com.webank.wedatasphere.dss.appjoint.scheduler.airflow.parser;

import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.entity.AirflowSchedulerProject;
import com.webank.wedatasphere.dss.appjoint.scheduler.entity.AbstractSchedulerProject;
import com.webank.wedatasphere.dss.appjoint.scheduler.parser.AbstractProjectParser;
import com.webank.wedatasphere.dss.appjoint.scheduler.parser.FlowParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Created by allenlliu on 2019/9/16.
 */

public class AirflowProjectParser extends AbstractProjectParser {

    public AirflowProjectParser(){
        FlowParser[] flowParsers = {new AirflowFlowParser()};
        setFlowParsers(flowParsers);
    }

    private static final Logger logger = LoggerFactory.getLogger(AirflowProjectParser.class);

    @Override
    protected AbstractSchedulerProject createSchedulerProject() {
        return new AirflowSchedulerProject();
    }

    @Override
    public void setFlowParsers(FlowParser[] flowParsers) {
        super.setFlowParsers(flowParsers);
    }

}
