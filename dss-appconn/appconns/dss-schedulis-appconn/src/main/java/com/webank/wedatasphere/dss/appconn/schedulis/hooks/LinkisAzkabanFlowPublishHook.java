/*
 *
 *  * Copyright 2019 WeBank
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.appconn.schedulis.hooks;


import com.google.gson.Gson;
import com.webank.wedatasphere.dss.appconn.schedulis.constant.AzkabanConstant;
import com.webank.wedatasphere.dss.appconn.schedulis.entity.AzkabanSchedulerFlow;
import com.webank.wedatasphere.dss.appconn.schedule.core.entity.SchedulerFlow;
import com.webank.wedatasphere.dss.appconn.schedule.core.hooks.AbstractFlowPublishHook;
import com.webank.wedatasphere.dss.appconn.schedule.core.hooks.NodePublishHook;
import com.webank.wedatasphere.dss.common.entity.Resource;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.contextservice.service.ContextService;
import com.webank.wedatasphere.dss.contextservice.service.impl.ContextServiceImpl;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by allenlliu on 2019/9/20.
 */

public class LinkisAzkabanFlowPublishHook extends AbstractFlowPublishHook {
    private static ContextService contextService = ContextServiceImpl.getInstance();

    private static final Logger LOGGER = LoggerFactory.getLogger(LinkisAzkabanFlowPublishHook.class);

    public LinkisAzkabanFlowPublishHook() {
        NodePublishHook[] nodePublishHooks = {new LinkisAzkabanNodePublishHook()};
        setNodeHooks(nodePublishHooks);
    }

    @Override
    public void prePublish(SchedulerFlow flow) throws DSSErrorException {
        // 处理资源生成文件等等
        writeFlowResourcesToLocal(flow);
        writeFlowPropertiesToLocal(flow);
        super.prePublish(flow);
    }

    private void writeFlowResourcesToLocal(SchedulerFlow flow) throws DSSErrorException {
        List<Resource> flowResources = flow.getFlowResources();
        FileOutputStream os = null;
        try {
            String storePath = ((AzkabanSchedulerFlow) flow).getStorePath();
            File flowDir = new File(storePath);
            FileUtils.forceMkdir(flowDir);
            if (flowResources == null || flowResources.isEmpty()) {
                return;
            }
            String projectStorePath = getProjectStorePath(storePath);
            String flowResourceStringPrefix = getFlowResourceStringPrefix(projectStorePath, storePath);
            String flowtResourceString = flowResourceStringPrefix + new Gson().toJson(flowResources) + "\n";
            File projectResourcesFile = new File(projectStorePath, "project.properties");
            os = FileUtils.openOutputStream(projectResourcesFile, true);
            os.write(flowtResourceString.getBytes());
        } catch (Exception e) {
            LOGGER.error("write FlowResources to local failed,reason:", e);
            throw new DSSErrorException(90006, e.getMessage());
        } finally {
            IOUtils.closeQuietly(os);
        }
    }

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

    private void writeFlowPropertiesToLocal(SchedulerFlow flow) throws DSSErrorException {
        List<Map<String, Object>> flowProperties = flow.getFlowProperties();
        if (flowProperties == null || flowProperties.isEmpty()) {
            return;
        }
        FileOutputStream os = null;
        try {
            String storePath = ((AzkabanSchedulerFlow) flow).getStorePath();
            File flowPrpsFile = new File(storePath, flow.getName() + AzkabanConstant.AZKABAN_PROPERTIES_SUFFIX);
            flowPrpsFile.createNewFile();
            os = FileUtils.openOutputStream(flowPrpsFile, true);
            StringBuilder stringBuilder = new StringBuilder();
            flowProperties.forEach(p -> p.forEach((k, v) -> {
                stringBuilder.append(AzkabanConstant.LINKIS_FLOW_VARIABLE_KEY + k + "=" + v + "\n");
            }));
            //update by peaceWong add contextID to Flow properties
            //这里和已有的工作流信息没有变化的地方
//            String flowVersion = flow.getJsonFlow().getLatestVersion().getVersion()+"az";
//            flow.getJsonFlow().getLatestVersion().setVersion(flowVersion);
            String contextID = flow.getContextID();
            if (StringUtils.isNotBlank(contextID)) {
                contextID = contextID.replace("\\", "/");
                LOGGER.info("after replace contextID is {}", contextID);
                stringBuilder.append(AzkabanConstant.FLOW_CONTEXT_ID + contextID + "\n");
            }
            //update end
            os.write(stringBuilder.toString().getBytes());
        } catch (Exception e) {
            LOGGER.error("write flowProperties to local faailed,reason:", e);
            throw new DSSErrorException(90007, e.getMessage());
        } finally {
            IOUtils.closeQuietly(os);
        }
    }

    @Override
    public void postPublish(SchedulerFlow flow) {

        super.postPublish(flow);
    }


    @Override
    public void setNodeHooks(NodePublishHook[] nodePublishHooks) {
        super.setNodeHooks(nodePublishHooks);
    }


}
