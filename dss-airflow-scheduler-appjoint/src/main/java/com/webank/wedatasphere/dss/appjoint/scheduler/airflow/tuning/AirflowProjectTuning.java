package com.webank.wedatasphere.dss.appjoint.scheduler.airflow.tuning;

import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.conf.AirflowConf;
import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.entity.AirflowSchedulerFlow;
import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.entity.AirflowSchedulerProject;
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
 * Created by Xudong Zhang on 2020/8/6.
 */

public class AirflowProjectTuning extends AbstractProjectTuning {

    public AirflowProjectTuning(){
        ArrayList<FlowTuning> list = new ArrayList<>();
        list.add(new LinkisAirflowFlowTuning());
        list.add(new LinkisShareNodeFlowTuning());
        FlowTuning[] flowTunings =new FlowTuning[list.size()];
        setFlowTunings(list.toArray(flowTunings));
    }

    @Override
    public SchedulerProject tuningSchedulerProject(SchedulerProject schedulerProject) {
        if(ifProjectCanTuning(schedulerProject)){
            AirflowSchedulerProject project = (AirflowSchedulerProject) schedulerProject;
            //1.给AirflowSchedulerProject的storepath赋值
            assignStorePath(project);
            //2.给他的rootflow的storepath也赋值
            List<SchedulerFlow> schedulerFlows = project.getSchedulerFlows();
            schedulerFlows.forEach(flow ->setRootFlowStorePath(project.getStorePath(),flow));
        }
        return super.tuningSchedulerProject(schedulerProject);
    }

    public void setRootFlowStorePath(String projectStorePath,SchedulerFlow schedulerFlow){
        AirflowSchedulerFlow airflowSchedulerFlow = (AirflowSchedulerFlow) schedulerFlow;
        airflowSchedulerFlow.setStorePath(projectStorePath + File.separator + airflowSchedulerFlow.getName());
    }


    @Override
    public void setFlowTunings(FlowTuning[] flowTunings) {
        super.setFlowTunings(flowTunings);
    }


    public boolean ifProjectCanTuning(SchedulerProject schedulerProject){
        return schedulerProject instanceof AirflowSchedulerProject;
    }

    private void assignStorePath(AirflowSchedulerProject airflowSchedulerProject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(AirflowSchedulerProject.DATE_FORMAT);
        Date date = new Date();
        String dataStr = dateFormat.format(date);
        String userName = airflowSchedulerProject.getDssProject().getUserName();
        String name = airflowSchedulerProject.getName();
        String storePath = AirflowConf.DEFAULT_STORE_PATH.getValue() + File.separator + userName
                + File.separator + dataStr + File.separator +name;
        airflowSchedulerProject.setStorePath(storePath);
    }
}
