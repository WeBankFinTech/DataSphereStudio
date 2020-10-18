package com.webank.wedatasphere.dss.appjoint.scheduler.airflow.parser;


import com.webank.wedatasphere.dss.appjoint.scheduler.entity.SchedulerEdge;
import com.webank.wedatasphere.dss.appjoint.scheduler.entity.SchedulerNode;
import com.webank.wedatasphere.dss.appjoint.scheduler.parser.AbstractNodeParser;

import java.util.List;

public abstract class AirflowNodeParser extends AbstractNodeParser {

    protected Boolean generateDependencies(SchedulerNode node, List<SchedulerEdge> flowEdges){return null;};

    protected Boolean assertNodeType(){return null;};

    protected String getNodeResourcesStr(){return null;};
}
