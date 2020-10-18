package com.webank.wedatasphere.dss.appjoint.scheduler.airflow.parser;

import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.entity.LinkisAirflowReadNode;
import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.entity.LinkisAirflowSchedulerNode;
import com.webank.wedatasphere.dss.appjoint.scheduler.entity.ReadNode;
import com.webank.wedatasphere.dss.appjoint.scheduler.entity.SchedulerNode;
import com.webank.wedatasphere.dss.appjoint.scheduler.parser.SendEmailNodeParser;



public class LinkisAirflowSendEmailNodeParser extends SendEmailNodeParser {

    @Override
    protected ReadNode createReadNode() {
        return new LinkisAirflowReadNode();
    }

    @Override
    protected SchedulerNode createSchedulerNode() {
        return new LinkisAirflowSchedulerNode();
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
