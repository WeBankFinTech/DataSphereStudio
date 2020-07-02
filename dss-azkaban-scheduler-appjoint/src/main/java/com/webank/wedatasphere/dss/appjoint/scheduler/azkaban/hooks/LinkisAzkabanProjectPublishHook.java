package com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.hooks;

import com.google.gson.Gson;
import com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.entity.AzkabanSchedulerProject;
import com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.util.AzkabanUtilsScala;
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
 * Created by cooperyang on  2019/9/18
 */

public class LinkisAzkabanProjectPublishHook extends AbstractProjectPublishHook {

    private static final Logger LOGGER = LoggerFactory.getLogger( LinkisAzkabanProjectPublishHook.class);
    public LinkisAzkabanProjectPublishHook(){
        FlowPublishHook[] flowPublishHooks = {new LinkisAzkabanFlowPublishHook()};
        setFlowPublishHooks(flowPublishHooks);
    }

    @Override
    public void setFlowPublishHooks(FlowPublishHook[] flowPublishHooks) {
        super.setFlowPublishHooks(flowPublishHooks);
    }

    @Override
    public void prePublish(SchedulerProject project) throws DSSErrorException {
        //1.检查重复的节点名
        AzkabanSchedulerProject publishProject = (AzkabanSchedulerProject)project;
        SchedulerProjectVersionForFlows schedulerProjectVersionForFlows = publishProject.getProjectVersions().get(0);
        List<SchedulerFlow> allSchedulerFlows = schedulerProjectVersionForFlows.getFlows();
        List<String> repeatNode = AzkabanUtilsScala.getRepeatNodeName(getAllNodeName(allSchedulerFlows));
        if(repeatNode.size()>0){
            throw new DSSErrorException(80001,"重复的节点名称："+repeatNode.toString());
        }
        //删除可能未处理的zip包和文件夹
        removeProjectStoreDirAndzip(publishProject);
        // 2.处理资源
        writeProjectResourcesToLocal(publishProject);
        //3.调用flowPublishHooks
        super.prePublish(project);
    }

    private void removeProjectStoreDirAndzip(AzkabanSchedulerProject publishProject) throws DSSErrorException {
        String storePath = publishProject.getStorePath();
        File projectDir = new File(storePath);
        try {
            if(projectDir.exists()) {
                LOGGER.info("exist project dir{} before publish ,now remove it",storePath);
                FileUtils.deleteDirectory(projectDir);
            }
            String projectZip = projectDir.getParent() + File.separator +  publishProject.getName() + ".zip";
            File zipFile = new File(projectZip);
            if(zipFile.exists()){
                LOGGER.info("exist project zip{} before publish ,now remove it",projectZip);
                zipFile.delete();
            }
        }catch (Exception e){
            LOGGER.error("delete project dir or zip failed,reaseon:",e);
            throw new DSSErrorException(90020,e.getMessage());
        }
    }

    private void writeProjectResourcesToLocal(AzkabanSchedulerProject publishProject)throws DSSErrorException {
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
            LOGGER.error("write projectResources to local failed,reason:",e);
           throw new DSSErrorException(90015,e.getMessage());
        }finally {
            IOUtils.closeQuietly(os);
        }
    }

    @Override
    public void postPublish(SchedulerProject project) throws DSSErrorException {
        // TODO: 2019/9/26  1.删除zip包和文件夹
        AzkabanSchedulerProject publishProject = (AzkabanSchedulerProject)project;
        String storePath = publishProject.getStorePath();
        try {
            String projectName = publishProject.getName();
            File file = new File(storePath);
            new File(file.getParent() + File.separator + projectName + ".zip" ).delete();
        }catch (Exception e){
            LOGGER.error("删除发布后的zip包失败",e);
            throw new DSSErrorException(90001,"发布后删除工程目录失败");
        }
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
