package com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.entity;

import com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.linkisjob.LinkisJobConverter;
import com.webank.wedatasphere.dss.appjoint.scheduler.entity.ReadNode;
import com.webank.wedatasphere.dss.appjoint.scheduler.entity.SchedulerNode;

public class LinkisAzkabanReadNode extends LinkisAzkabanSchedulerNode implements ReadNode {

  private SchedulerNode schedulerNode;
  private String[] nodeIds;

  @Override
  public String[] getShareNodeIds() {
    return this.nodeIds;
  }

  @Override
  public void setShareNodeIds(String[] nodeIds) {
    this.nodeIds = nodeIds;
  }

  @Override
  public SchedulerNode getSchedulerNode() {
    return schedulerNode;
  }

  @Override
  public void setSchedulerNode(SchedulerNode schedulerNode) {
    this.schedulerNode = schedulerNode;
  }

  @Override
  public String toJobString(LinkisJobConverter converter) {
    return converter.conversion(this);
  }

}
