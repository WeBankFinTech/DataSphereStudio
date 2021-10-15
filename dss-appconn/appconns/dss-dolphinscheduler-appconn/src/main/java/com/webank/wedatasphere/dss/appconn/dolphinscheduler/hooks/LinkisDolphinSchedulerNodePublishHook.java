package com.webank.wedatasphere.dss.appconn.dolphinscheduler.hooks;

import com.webank.wedatasphere.dss.appconn.schedule.core.entity.SchedulerNode;
import com.webank.wedatasphere.dss.appconn.schedule.core.hooks.AbstractNodePublishHook;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The type Linkis dolphin scheduler node publish hook.
 *
 * @author yuxin.yuan
 * @date 2021/04/29
 */
public class LinkisDolphinSchedulerNodePublishHook extends AbstractNodePublishHook {

    private static final Logger logger = LoggerFactory.getLogger(LinkisDolphinSchedulerNodePublishHook.class);

    public LinkisDolphinSchedulerNodePublishHook() {

    }

    @Override
    public void prePublish(SchedulerNode schedulerNode) throws DSSErrorException {
        //        writeNodeTojobLocal(schedulerNode);
        //        writeNodeResourcesToLocal(schedulerNode);
    }

    //    private void writeNodeTojobLocal(SchedulerNode schedulerNode) throws DSSErrorException {
    //        LinkisAzkabanSchedulerNode azkabanSchedulerNode = (LinkisAzkabanSchedulerNode)schedulerNode;
    //        FileOutputStream os = null;
    //        try {
    //            String storePath = azkabanSchedulerNode.getStorePath();
    //            File jobDirFile = new File(storePath);
    //            FileUtils.forceMkdir(jobDirFile);
    //            File jobFile = new File(storePath, schedulerNode.getName() + AzkabanConstant.AZKABAN_JOB_SUFFIX);
    //            jobFile.createNewFile();
    //            String nodeString = azkabanSchedulerNode.toJobString(linkisJobConverter);
    //            os = FileUtils.openOutputStream(jobFile, true);
    //            os.write(nodeString.getBytes());
    //        } catch (Exception e) {
    //            LOGGER.error("write AppJointNode to jobLocal failed,reason:", e);
    //            throw new DSSErrorException(90017, e.getMessage());
    //        } finally {
    //            IOUtils.closeQuietly(os);
    //        }
    //    }

    @Override
    public void postPublish(SchedulerNode schedulerNode) {

    }

    //    private void writeNodeResourcesToLocal(SchedulerNode schedulerNode) throws DSSErrorException {
    //        List<Resource> nodeResources = schedulerNode.getDSSNode().getResources();
    //        if (nodeResources == null || nodeResources.isEmpty()) {
    //            return;
    //        }
    //        FileOutputStream os = null;
    //        try {
    //            String storePath = ((AzkabanSchedulerNode)schedulerNode).getStorePath();
    //            File jobFile = new File(storePath, schedulerNode.getName() + AzkabanConstant.AZKABAN_JOB_SUFFIX);
    //            String nodeResourceString = AzkabanConstant.LINKIS_JOB_RESOURCES_KEY + new Gson().toJson(nodeResources);
    //            os = FileUtils.openOutputStream(jobFile, true);
    //            os.write(nodeResourceString.getBytes());
    //        } catch (Exception e) {
    //            LOGGER.error("write nodeResources to local failed,reason:", e);
    //            throw new DSSErrorException(90018, e.getMessage());
    //        } finally {
    //            IOUtils.closeQuietly(os);
    //        }
    //    }

}
