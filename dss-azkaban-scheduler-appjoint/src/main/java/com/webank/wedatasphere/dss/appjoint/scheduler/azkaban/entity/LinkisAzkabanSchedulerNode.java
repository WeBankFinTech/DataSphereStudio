package com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.entity;

import com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.linkisjob.LinkisJobConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by cooperyang on 2019/9/29.
 */
public class LinkisAzkabanSchedulerNode extends AzkabanSchedulerNode {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public String toJobString(LinkisJobConverter converter) {
        return converter.conversion(this);
    }
}
