package com.webank.wedatasphere.dss.appjoint.scheduler.airflow.hooks;

import com.google.gson.Gson;
import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.entity.AirflowSchedulerProject;
import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.util.AirflowUtilsScala;
import com.webank.wedatasphere.dss.appjoint.scheduler.entity.SchedulerFlow;
import com.webank.wedatasphere.dss.appjoint.scheduler.entity.SchedulerProject;
import com.webank.wedatasphere.dss.appjoint.scheduler.entity.SchedulerProjectVersionForFlows;
import com.webank.wedatasphere.dss.appjoint.scheduler.hooks.AbstractProjectPublishHook;
import com.webank.wedatasphere.dss.appjoint.scheduler.hooks.FlowPublishHook;
import com.webank.wedatasphere.dss.common.entity.Resource;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xudong Zhang on 2020/8/6.
 */

public class LinkisAirflowProjectPublishHook extends AbstractProjectPublishHook {

    private static final Logger LOGGER = LoggerFactory.getLogger( LinkisAirflowProjectPublishHook.class);
    public LinkisAirflowProjectPublishHook(){
        FlowPublishHook[] flowPublishHooks = {new LinkisAirflowFlowPublishHook()};
        setFlowPublishHooks(flowPublishHooks);
    }

    @Override
    public void setFlowPublishHooks(FlowPublishHook[] flowPublishHooks) {
        super.setFlowPublishHooks(flowPublishHooks);
    }

    @Override
    public void prePublish(SchedulerProject project) throws DSSErrorException {
        //1.检查重复的节点名 (airflow的flow之间独立，无需考虑同一个project下不同flow中存在同名节点的情况)
        AirflowSchedulerProject publishProject = (AirflowSchedulerProject)project;
//        SchedulerProjectVersionForFlows schedulerProjectVersionForFlows = publishProject.getProjectVersions().get(0);
//        List<SchedulerFlow> allSchedulerFlows = schedulerProjectVersionForFlows.getFlows();
//        List<String> repeatNode = AirflowUtilsScala.getRepeatNodeName(getAllNodeName(allSchedulerFlows));
//        if(repeatNode.size()>0){
//            throw new DSSErrorException(80001,"重复的节点名称："+repeatNode.toString());
//        }
        //删除可能未处理的文件夹
        LOGGER.info("Try remove existed project dir {} before publish", publishProject.getStorePath());
        removeProjectStoreDir(publishProject);
        // 2.处理资源
        writeProjectResourcesToLocal(publishProject);
        //3.调用flowPublishHooks
        super.prePublish(project);
    }

    private void removeProjectStoreDir(AirflowSchedulerProject publishProject) throws DSSErrorException {
        String storePath = publishProject.getStorePath();
        File projectDir = new File(storePath);
        try {
            if(projectDir.exists()) {
                FileUtils.deleteDirectory(projectDir);
            }
        }catch (Exception e){
            LOGGER.error("delete project dir failed,reason:",e);
            throw new DSSErrorException(90020,e.getMessage());
        }
    }

    private void writeProjectResourcesToLocal(AirflowSchedulerProject publishProject)throws DSSErrorException {
        List<Resource> resources = publishProject.getDssProject().getProjectResources();
        FileOutputStream os = null;
        try {
            String storePath = publishProject.getStorePath();
            File projectDir = new File(storePath);
            FileUtils.forceMkdir(projectDir);
            File projectResourcesFile = new File(storePath, "project.properties");
            projectResourcesFile.createNewFile();
            if(resources == null || resources.isEmpty()) {return;}
            String projectResourceStringPrefix = "project." + publishProject.getName() + ".resources=";
            String projectResourceString = projectResourceStringPrefix + new Gson().toJson(resources) + "\n";
            os = FileUtils.openOutputStream(projectResourcesFile);
            os.write(projectResourceString.getBytes());
        }catch (Exception e){
            LOGGER.error("write projectResources to local failed, reason:",e);
           throw new DSSErrorException(90015,e.getMessage());
        }finally {
            IOUtils.closeQuietly(os);
        }
    }

    @Override
    public void postPublish(SchedulerProject project) throws DSSErrorException {
        AirflowSchedulerProject publishProject = (AirflowSchedulerProject)project;
        //LOGGER.info("Try remove existed project dir {} after publish, now remove it", publishProject.getStorePath());
        //removeProjectStoreDir(publishProject);
        // TODO: 2019/9/26  flowPublishHooks ...
    }

    /**
     * 直接从allFlows 中获取所有节点的名字，免去一次递归
     * @param allSchedulerFlows
     * @return
     */
    public List<String> getAllNodeName(List<SchedulerFlow> allSchedulerFlows) {
        List<String> nodeNames = new ArrayList<>();
        allSchedulerFlows.forEach(flow ->flow.getSchedulerNodes().forEach(node ->nodeNames.add(node.getName())));
        return nodeNames;
    }


}
