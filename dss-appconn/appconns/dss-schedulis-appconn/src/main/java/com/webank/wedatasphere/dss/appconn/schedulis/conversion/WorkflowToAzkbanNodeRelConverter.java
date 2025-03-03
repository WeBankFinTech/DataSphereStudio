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
import com.webank.wedatasphere.dss.appconn.schedulis.entity.AzkabanWorkflow;
import com.webank.wedatasphere.dss.appconn.schedulis.linkisjob.LinkisJobConverter;
import com.webank.wedatasphere.dss.appconn.schedulis.linkisjob.WTSSJobConverter;
import com.webank.wedatasphere.dss.common.entity.Resource;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.utils.ClassUtils;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.workflow.conversion.entity.ConvertedRel;
import com.webank.wedatasphere.dss.workflow.conversion.entity.PreConversionRel;
import com.webank.wedatasphere.dss.workflow.conversion.entity.ProjectPreConversionRel;
import com.webank.wedatasphere.dss.workflow.conversion.operation.WorkflowToRelConverter;
import com.webank.wedatasphere.dss.workflow.core.entity.Workflow;
import com.webank.wedatasphere.dss.workflow.core.entity.WorkflowNode;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;


public class WorkflowToAzkbanNodeRelConverter implements WorkflowToRelConverter {

    public static final Logger LOGGER = LoggerFactory.getLogger(WorkflowToAzkbanNodeRelConverter.class);

    private NodeConverter nodeConverter;

    public WorkflowToAzkbanNodeRelConverter() {
        nodeConverter = ClassUtils.getInstanceOrDefault(NodeConverter.class, new LinkisJobConverter());
    }

    @Override
    public ConvertedRel convertToRel(PreConversionRel rel) {
        ((ProjectPreConversionRel) rel).getWorkflows().forEach(this::convertNode);
        return (ConvertedRel) rel;
    }

    private void convertNode(Workflow workflow) {
        workflow.getWorkflowNodes().forEach(DSSExceptionUtils.handling(workflowNode -> {
            String nodeStorePath = getNodeStorePath(((AzkabanWorkflow)workflow).getStorePath(), workflowNode);
            writeNodeToJobLocal(workflowNode, nodeStorePath);
            writeNodeResourcesToLocal(workflowNode, nodeStorePath);
        }));
        if(workflow.getChildren() != null) {
            workflow.getChildren().forEach(flow -> convertNode((Workflow) flow));
        }
    }

    private String getNodeStorePath(String flowStorePath, WorkflowNode schedulerNode) {
        return flowStorePath + File.separator + "jobs" + File.separator + schedulerNode.getName();
    }

    private void writeNodeToJobLocal(WorkflowNode workflowNode, String storePath) throws DSSErrorException {
        FileOutputStream os = null;
        try {
            File jobDirFile = new File(storePath);
            FileUtils.forceMkdir(jobDirFile);
            File jobFile = new File(storePath,workflowNode.getName() + AzkabanConstant.AZKABAN_JOB_SUFFIX);
            jobFile.createNewFile();
            if(workflowNode.getNodeType()!=null && workflowNode.getNodeType().startsWith(AzkabanConstant.WTSS_PREFIX)){
                nodeConverter = ClassUtils.getInstanceOrDefault(NodeConverter.class, new WTSSJobConverter());
            }else{
                nodeConverter = ClassUtils.getInstanceOrDefault(NodeConverter.class, new LinkisJobConverter());
            }
            String nodeString = nodeConverter.conversion(workflowNode);
            os = FileUtils.openOutputStream(jobFile,true);
            os.write(nodeString.getBytes());
        }catch (Exception e){
            LOGGER.error("write AppConnNode to jobLocal failed,reason:",e);
            throw new DSSErrorException(90017,e.getMessage());
        } finally {
            IOUtils.closeQuietly(os);
        }
    }

    private void writeNodeResourcesToLocal(WorkflowNode workflowNode, String storePath) throws DSSErrorException {
        List<Resource> nodeResources = workflowNode.getDSSNode().getResources();
        if(nodeResources == null || nodeResources.isEmpty()) {return;}
        FileOutputStream os = null;
        try {
            File jobFile = new File(storePath,workflowNode.getName() + AzkabanConstant.AZKABAN_JOB_SUFFIX);
            String nodeResourceString = AzkabanConstant.LINKIS_JOB_RESOURCES_KEY + new Gson().toJson(nodeResources);
            os = FileUtils.openOutputStream(jobFile,true);
            os.write(nodeResourceString.getBytes());
        }catch (Exception e){
            LOGGER.error("write nodeResources to local failed,reason:",e);
            throw new DSSErrorException(90018,e.getMessage());
        }finally {
            IOUtils.closeQuietly(os);
        }
    }

    @Override
    public int getOrder() {
        return 100;
    }
}
