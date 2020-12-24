package com.webank.wedatasphere.dss.appjoint.scheduler.airflow.entity;

import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.linkisjob.LinkisJobConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Xudong Zhang on 2020/8/6.
 */
public class LinkisAirflowSchedulerNode extends AirflowSchedulerNode {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public String toJobString(LinkisJobConverter converter) {
        return converter.conversion(this);
    }
}
