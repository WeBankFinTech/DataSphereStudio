package com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.parser;

import com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.entity.LinkisAzkabanReadNode;
import com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.entity.LinkisAzkabanSchedulerNode;
import com.webank.wedatasphere.dss.appjoint.scheduler.entity.ReadNode;
import com.webank.wedatasphere.dss.appjoint.scheduler.entity.SchedulerNode;
import com.webank.wedatasphere.dss.appjoint.scheduler.parser.SendEmailNodeParser;
import org.springframework.stereotype.Component;

@Component
public class LinkisAzkabanSendEmailNodeParser extends SendEmailNodeParser {

    @Override
    protected ReadNode createReadNode() {
        return new LinkisAzkabanReadNode();
    }

    @Override
    protected SchedulerNode createSchedulerNode() {
        return new LinkisAzkabanSchedulerNode();
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
