/*
 * Copyright 2019 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.appconn.schedulis.conversion;

import com.google.gson.Gson;
import com.webank.wedatasphere.dss.appconn.schedulis.constant.AzkabanConstant;
import com.webank.wedatasphere.dss.appconn.schedulis.entity.AzkabanConvertedRel;
import com.webank.wedatasphere.dss.appconn.schedulis.entity.AzkabanWorkflow;
import com.webank.wedatasphere.dss.common.entity.Resource;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.workflow.common.entity.Flow;
import com.webank.wedatasphere.dss.workflow.conversion.entity.ConvertedRel;
import com.webank.wedatasphere.dss.workflow.conversion.entity.PreConversionRel;
import com.webank.wedatasphere.dss.workflow.conversion.operation.WorkflowToRelConverter;
import com.webank.wedatasphere.dss.workflow.core.entity.Workflow;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkflowToAzkbanRelConverter implements WorkflowToRelConverter {

    public static final Logger LOGGER = LoggerFactory.getLogger(WorkflowToAzkbanRelConverter.class);

    @Override
    public ConvertedRel convertToRel(PreConversionRel rel) {
        AzkabanConvertedRel azkabanConvertedRel = (AzkabanConvertedRel) rel;
        azkabanConvertedRel.getWorkflows().forEach(DSSExceptionUtils.handling(workflow -> {
            //1. Set sub flow and node storage paths
            String flowStorePath = ((AzkabanWorkflow) workflow).getStorePath();
            if (workflow.getChildren() != null) {
                workflow.getChildren().forEach(flow -> setFlowStorePath(flowStorePath, flow));
            }
            // 2. Processing resources, generating files, and so on.
            writeWorkflowFiles(workflow, azkabanConvertedRel.getStorePath());
        }));
        return azkabanConvertedRel;
    }

    private void writeWorkflowFiles(Flow workflow, String projectStorePath) throws DSSErrorException {
        AzkabanWorkflow flow = (AzkabanWorkflow) workflow;
        writeFlowResourcesToLocal(flow, projectStorePath);
        writeFlowPropertiesToLocal(flow);
        if (workflow.getChildren() != null) {
            workflow.getChildren().forEach(DSSExceptionUtils.handling(f -> writeWorkflowFiles(f, projectStorePath)));
        }
    }

    private void setFlowStorePath(String flowStorePath, Flow workflow) {
        AzkabanWorkflow azkabanWorkflow = (AzkabanWorkflow) workflow;
        azkabanWorkflow.setStorePath(flowStorePath + File.separator + "subFlows" + File.separator + azkabanWorkflow.getName());
        if (workflow.getChildren() != null) {
            workflow.getChildren().forEach(flow -> setFlowStorePath(azkabanWorkflow.getStorePath(), flow));
        }
    }

    private void writeFlowResourcesToLocal(AzkabanWorkflow flow, String projectStorePath) throws DSSErrorException {
        List<Resource> flowResources = flow.getFlowResources();
        FileOutputStream os = null;
        try {
            String storePath = flow.getStorePath();
            File flowDir = new File(storePath);
            FileUtils.forceMkdir(flowDir);
            if (flowResources == null || flowResources.isEmpty()) {
                return;
            }
            String flowResourceStringPrefix = getFlowResourceStringPrefix(projectStorePath, storePath);
            String flowResourceString = flowResourceStringPrefix + new Gson().toJson(flowResources) + "\n";
            File projectResourcesFile = new File(projectStorePath, "project.properties");
            os = FileUtils.openOutputStream(projectResourcesFile, true);
            os.write(flowResourceString.getBytes());
        } catch (Exception e) {
            LOGGER.error("write FlowResources to local failed,reason:", e);
            throw new DSSErrorException(90006, e.getMessage());
        } finally {
            IOUtils.closeQuietly(os);
        }
    }

    private String getFlowResourceStringPrefix(String projectStorePath, String storePath) {
        String substring = storePath.substring(projectStorePath.length() + 1);
        String prefix = substring.replaceAll("\\" + File.separator + "subFlows" + "\\" + File.separator, ".");
        return "flow." + prefix + "_.resources=";
    }

    private void writeFlowPropertiesToLocal(AzkabanWorkflow flow) throws DSSErrorException {
        List<Map<String, Object>> flowProperties = flow.getFlowProperties();
        FileOutputStream os = null;
        try {
            String storePath = flow.getStorePath();
            File flowPrpsFile = new File(storePath, flow.getName() + AzkabanConstant.AZKABAN_PROPERTIES_SUFFIX);
            flowPrpsFile.createNewFile();
            os = FileUtils.openOutputStream(flowPrpsFile, true);
            StringBuilder stringBuilder = new StringBuilder();
            if(flowProperties != null) {
                flowProperties.forEach(p -> p.forEach((k, v) -> {
                    stringBuilder.append(AzkabanConstant.LINKIS_FLOW_VARIABLE_KEY + k + "=" + v + "\n");
                }));
            }
            // update by peaceWong add contextID to Flow properties
            String contextID = flow.getContextID();
            if (StringUtils.isNotBlank(contextID)) {
                contextID = contextID.replace("\\", "/");
                LOGGER.info("after replace contextID is {}", contextID);
                stringBuilder.append(AzkabanConstant.FLOW_CONTEXT_ID + contextID + "\n");
            }
            // update end
            os.write(stringBuilder.toString().getBytes());
        } catch (Exception e) {
            LOGGER.error("write flowProperties to local faailed,reason:", e);
            throw new DSSErrorException(90007, e.getMessage());
        } finally {
            IOUtils.closeQuietly(os);
        }
    }

    @Override
    public int getOrder() {
        return 10;
    }
}
