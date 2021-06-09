package com.webank.wedatasphere.dss.appconn.dolphinscheduler.hooks;

import com.webank.wedatasphere.dss.appconn.schedule.core.entity.SchedulerFlow;
import com.webank.wedatasphere.dss.appconn.schedule.core.hooks.AbstractFlowPublishHook;
import com.webank.wedatasphere.dss.appconn.schedule.core.hooks.NodePublishHook;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.contextservice.service.ContextService;
import com.webank.wedatasphere.dss.contextservice.service.impl.ContextServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * The type Linkis dolphin scheduler flow publish hook.
 *
 * @author yuxin.yuan
 * @date 2021/04/29
 */
public class LinkisDolphinSchedulerFlowPublishHook extends AbstractFlowPublishHook {
    private static final Logger logger = LoggerFactory.getLogger(LinkisDolphinSchedulerFlowPublishHook.class);

    private static ContextService contextService = ContextServiceImpl.getInstance();

    public LinkisDolphinSchedulerFlowPublishHook() {
        NodePublishHook[] nodePublishHooks = {new LinkisDolphinSchedulerNodePublishHook()};
        setNodeHooks(nodePublishHooks);
    }

    @Override
    public void prePublish(SchedulerFlow flow) throws DSSErrorException {
        // 处理资源生成文件等等
        //        writeFlowResourcesToLocal(flow);
        //        writeFlowPropertiesToLocal(flow);
        super.prePublish(flow);
    }

    //    private void writeFlowResourcesToLocal(SchedulerFlow flow) throws DSSErrorException {
    //        List<Resource> flowResources = flow.getFlowResources();
    //        FileOutputStream os = null;
    //        try {
    //            String storePath = ((DolphinSchedulerFlow)flow).getStorePath();
    //            File flowDir = new File(storePath);
    //            FileUtils.forceMkdir(flowDir);
    //            if (flowResources == null || flowResources.isEmpty()) {
    //                return;
    //            }
    //            String projectStorePath = getProjectStorePath(storePath);
    //            String flowResourceStringPrefix = getFlowResourceStringPrefix(projectStorePath, storePath);
    //            String flowtResourceString = flowResourceStringPrefix + new Gson().toJson(flowResources) + "\n";
    //            File projectResourcesFile = new File(projectStorePath, "project.properties");
    //            os = FileUtils.openOutputStream(projectResourcesFile, true);
    //            os.write(flowtResourceString.getBytes());
    //        } catch (Exception e) {
    //            LOGGER.error("write FlowResources to local failed,reason:", e);
    //            throw new DSSErrorException(90006, e.getMessage());
    //        } finally {
    //            IOUtils.closeQuietly(os);
    //        }
    //    }

    private String getFlowResourceStringPrefix(String projectStorePath, String storePath) {
        // TODO: 2019/9/30 需要做用户工作流命名为subFlows的情况的判断
        String substring = storePath.substring(projectStorePath.length() + 1);
        String prefix = substring.replaceAll("\\" + File.separator + "subFlows" + "\\" + File.separator, ".");
        return "flow." + prefix + "_.resources=";
    }

    private String getProjectStorePath(String storePath) {
        int indexOf = storePath.indexOf("subFlows");
        if (indexOf != -1) {
            storePath = storePath.substring(0, indexOf - 1);
        }
        return storePath.substring(0, storePath.lastIndexOf(File.separator));
    }

    //    private void writeFlowPropertiesToLocal(SchedulerFlow flow) throws DSSErrorException {
    //        List<Map<String, Object>> flowProperties = flow.getFlowProperties();
    //        if (flowProperties == null || flowProperties.isEmpty()) {
    //            return;
    //        }
    //        FileOutputStream os = null;
    //        try {
    //            String storePath = ((DolphinSchedulerFlow)flow).getStorePath();
    //            File flowPrpsFile = new File(storePath, flow.getName() + AzkabanConstant.AZKABAN_PROPERTIES_SUFFIX);
    //            flowPrpsFile.createNewFile();
    //            os = FileUtils.openOutputStream(flowPrpsFile, true);
    //            StringBuilder stringBuilder = new StringBuilder();
    //            flowProperties.forEach(p -> p.forEach((k, v) -> {
    //                stringBuilder.append(AzkabanConstant.LINKIS_FLOW_VARIABLE_KEY + k + "=" + v + "\n");
    //            }));
    //            //update by peaceWong add contextID to Flow properties
    //            //这里和已有的工作流信息没有变化的地方
    //            //            String flowVersion = flow.getJsonFlow().getLatestVersion().getVersion()+"az";
    //            //            flow.getJsonFlow().getLatestVersion().setVersion(flowVersion);
    //            String contextID = flow.getContextID();
    //            if (StringUtils.isNotBlank(contextID)) {
    //                contextID = contextID.replace("\\", "/");
    //                LOGGER.info("after replace contextID is {}", contextID);
    //                stringBuilder.append(AzkabanConstant.FLOW_CONTEXT_ID + contextID + "\n");
    //            }
    //            //update end
    //            os.write(stringBuilder.toString().getBytes());
    //        } catch (Exception e) {
    //            LOGGER.error("write flowProperties to local faailed,reason:", e);
    //            throw new DSSErrorException(90007, e.getMessage());
    //        } finally {
    //            IOUtils.closeQuietly(os);
    //        }
    //    }

    @Override
    public void postPublish(SchedulerFlow flow) {
        super.postPublish(flow);
    }

    @Override
    public void setNodeHooks(NodePublishHook[] nodePublishHooks) {
        super.setNodeHooks(nodePublishHooks);
    }

}
