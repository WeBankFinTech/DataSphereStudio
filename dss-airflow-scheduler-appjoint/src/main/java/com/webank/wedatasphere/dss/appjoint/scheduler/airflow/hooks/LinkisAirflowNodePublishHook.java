package com.webank.wedatasphere.dss.appjoint.scheduler.airflow.hooks;

import com.google.gson.Gson;
import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.constant.AirflowConstant;
import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.entity.AirflowSchedulerNode;
import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.entity.LinkisAirflowSchedulerNode;
import com.webank.wedatasphere.dss.appjoint.scheduler.hooks.AbstractNodePublishHook;
import com.webank.wedatasphere.dss.appjoint.scheduler.entity.SchedulerNode;
import com.webank.wedatasphere.dss.common.entity.Resource;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.linkisjob.LinkisJobConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;


public class LinkisAirflowNodePublishHook extends AbstractNodePublishHook {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinkisAirflowNodePublishHook.class);
    private LinkisJobConverter linkisJobConverter;

    public LinkisAirflowNodePublishHook(){
        this.linkisJobConverter = new LinkisJobConverter();
    }

    @Override
    public void prePublish(SchedulerNode schedulerNode) throws DSSErrorException {
        writeNodeTojobLocal(schedulerNode);
        writeNodeResourcesToLocal(schedulerNode);
    }


    private void writeNodeTojobLocal(SchedulerNode schedulerNode) throws DSSErrorException {
        LinkisAirflowSchedulerNode airflowSchedulerNode = (LinkisAirflowSchedulerNode) schedulerNode;
        FileOutputStream os = null;
        try {
            String storePath = airflowSchedulerNode.getStorePath();
            File jobDirFile = new File(storePath);
            FileUtils.forceMkdir(jobDirFile);
            File jobFile = new File(storePath,schedulerNode.getName() + AirflowConstant.AIRFLOW_JOB_SUFFIX);
            jobFile.createNewFile();
            String nodeString = airflowSchedulerNode.toJobString(linkisJobConverter);
            os = FileUtils.openOutputStream(jobFile,true);
            os.write(nodeString.getBytes());
        }catch (Exception e){
            LOGGER.error("write AppJointNode to jobLocal failed,reason:",e);
            throw new DSSErrorException(90017,e.getMessage());
        }finally {
            IOUtils.closeQuietly(os);
        }
    }

    @Override
    public void postPublish(SchedulerNode schedulerNode) {

    }

    private void writeNodeResourcesToLocal(SchedulerNode schedulerNode) throws DSSErrorException {
        List<Resource> nodeResources = schedulerNode.getDssNode().getResources();
        if(nodeResources == null || nodeResources.isEmpty()) {return;}
        FileOutputStream os = null;
        try {
            String storePath = ((AirflowSchedulerNode)schedulerNode).getStorePath();
            File jobFile = new File(storePath,schedulerNode.getName() + AirflowConstant.AIRFLOW_JOB_SUFFIX);
            String nodeResourceString = AirflowConstant.LINKIS_JOB_RESOURCES_KEY + new Gson().toJson(nodeResources);
            os = FileUtils.openOutputStream(jobFile,true);
            os.write(nodeResourceString.getBytes());
        }catch (Exception e){
            LOGGER.error("write nodeResources to local failed,reason:",e);
            throw new DSSErrorException(90018,e.getMessage());
        }finally {
            IOUtils.closeQuietly(os);
        }
    }



}
