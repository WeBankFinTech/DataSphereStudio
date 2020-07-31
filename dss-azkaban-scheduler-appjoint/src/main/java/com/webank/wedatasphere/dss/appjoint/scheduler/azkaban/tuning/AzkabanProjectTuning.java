package com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.tuning;

import com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.conf.AzkabanConf;
import com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.entity.AzkabanSchedulerFlow;
import com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.entity.AzkabanSchedulerProject;
import com.webank.wedatasphere.dss.appjoint.scheduler.entity.SchedulerFlow;
import com.webank.wedatasphere.dss.appjoint.scheduler.entity.SchedulerProject;
import com.webank.wedatasphere.dss.appjoint.scheduler.tuning.AbstractProjectTuning;
import com.webank.wedatasphere.dss.appjoint.scheduler.tuning.FlowTuning;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by cooperyang on 2019/9/26.
 */

public class AzkabanProjectTuning extends AbstractProjectTuning {

    public AzkabanProjectTuning(){
        ArrayList<FlowTuning> list = new ArrayList<>();
        list.add(new LinkisAzkabanFlowTuning());
        list.add(new LinkisShareNodeFlowTuning());
        FlowTuning[] flowTunings =new FlowTuning[list.size()];
        setFlowTunings(list.toArray(flowTunings));
    }

    @Override
    public SchedulerProject tuningSchedulerProject(SchedulerProject schedulerProject) {
        if(ifProjectCanTuning(schedulerProject)){
            AzkabanSchedulerProject project = (AzkabanSchedulerProject) schedulerProject;
            //1.给AzkabanSchedulerProject的storepath赋值
            assignStorePath(project);
            //2.给他的rootflow的storepath也赋值
            List<SchedulerFlow> schedulerFlows = project.getSchedulerFlows();
            schedulerFlows.forEach(flow ->setRootFlowStorePath(project.getStorePath(),flow));
        }
        return super.tuningSchedulerProject(schedulerProject);
    }

    public void setRootFlowStorePath(String projectStorePath,SchedulerFlow schedulerFlow){
        AzkabanSchedulerFlow azkabanSchedulerFlow = (AzkabanSchedulerFlow) schedulerFlow;
        azkabanSchedulerFlow.setStorePath(projectStorePath + File.separator + azkabanSchedulerFlow.getName());
    }


    @Override
    public void setFlowTunings(FlowTuning[] flowTunings) {
        super.setFlowTunings(flowTunings);
    }


    public boolean ifProjectCanTuning(SchedulerProject schedulerProject){
        return schedulerProject instanceof AzkabanSchedulerProject;
    }

    private void assignStorePath(AzkabanSchedulerProject azkabanSchedulerProject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(AzkabanSchedulerProject.DATE_FORMAT);
        Date date = new Date();
        String dataStr = dateFormat.format(date);
        String userName = azkabanSchedulerProject.getDssProject().getUserName();
        String name = azkabanSchedulerProject.getName();
        String storePath = AzkabanConf.DEFAULT_STORE_PATH.getValue() + File.separator + userName
                + File.separator + dataStr + File.separator +name;
        azkabanSchedulerProject.setStorePath(storePath);
    }
}
