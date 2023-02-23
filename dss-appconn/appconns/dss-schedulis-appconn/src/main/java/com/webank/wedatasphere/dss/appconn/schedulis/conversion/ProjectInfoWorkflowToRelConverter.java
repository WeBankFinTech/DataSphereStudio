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

import com.webank.wedatasphere.dss.appconn.schedulis.conf.AzkabanConf;
import com.webank.wedatasphere.dss.appconn.schedulis.entity.AzkabanConvertedRel;
import com.webank.wedatasphere.dss.appconn.schedulis.entity.AzkabanWorkflow;
import com.webank.wedatasphere.dss.common.exception.DSSRuntimeException;
import com.webank.wedatasphere.dss.workflow.conversion.entity.ConvertedRel;
import com.webank.wedatasphere.dss.workflow.conversion.entity.PreConversionRel;
import com.webank.wedatasphere.dss.workflow.conversion.entity.ProjectPreConversionRel;
import com.webank.wedatasphere.dss.workflow.conversion.operation.WorkflowToRelConverter;
import com.webank.wedatasphere.dss.workflow.core.entity.Workflow;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


public class ProjectInfoWorkflowToRelConverter implements WorkflowToRelConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectInfoWorkflowToRelConverter.class);

    @Override
    public ConvertedRel convertToRel(PreConversionRel rel) {
        ProjectPreConversionRel projectPreConversionRel = (ProjectPreConversionRel) rel;
        List<String> nodeNames = getAllNodeName(projectPreConversionRel.getWorkflows());
        String repeatNodes = nodeNames.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream().filter(entry -> entry.getValue() > 1).map(Map.Entry::getKey).collect(Collectors.joining(", "));
        if (StringUtils.isNotEmpty(repeatNodes)) {
            throw new DSSRuntimeException(80001, "重复的节点名称。项目中不同工作流(或子工作流）里存在重名节点，请修改节点名避免重名。重名节点：" + repeatNodes);
        }
        AzkabanConvertedRel azkabanConvertedRel = new AzkabanConvertedRel(projectPreConversionRel);
        //1. Assign a value to the storepath of azkabanschedulerproject.
        assignStorePath(azkabanConvertedRel);
        //2. The storepath of his rootflow is also assigned a value.
        List<Workflow> workflows = projectPreConversionRel.getWorkflows();
        workflows.forEach(flow -> setRootFlowStorePath(azkabanConvertedRel.getStorePath(), flow));
        //3. Delete zip packages and folders that may not have been processed.
        removeProjectStoreDirAndZip(azkabanConvertedRel);
        return azkabanConvertedRel;
    }

    private void setRootFlowStorePath(String projectStorePath, Workflow workflow){
        AzkabanWorkflow azkabanWorkflow = (AzkabanWorkflow) workflow;
        azkabanWorkflow.setStorePath(projectStorePath + File.separator + workflow.getName());
    }

    private void assignStorePath(AzkabanConvertedRel rel) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(AzkabanConvertedRel.DATE_FORMAT);
        Date date = new Date();
        String dateStr = dateFormat.format(date);
        String userName = rel.getDSSToRelConversionRequestRef().getDSSProject().getUsername();
        String name = rel.getDSSToRelConversionRequestRef().getDSSProject().getName();
        String storePath = AzkabanConf.DEFAULT_STORE_PATH.getValue() + File.separator + userName
            + File.separator + dateStr + File.separator +name;
        rel.setStorePath(storePath);
    }

    private void removeProjectStoreDirAndZip(AzkabanConvertedRel rel) {
        String storePath = rel.getStorePath();
        File projectDir = new File(storePath);
        try {
            if (projectDir.exists()) {
                LOGGER.info("exist project dir{} before publish ,now remove it", storePath);
                FileUtils.deleteDirectory(projectDir);
            }
            String projectZip = projectDir.getParent() + File.separator +
                rel.getDSSToRelConversionRequestRef().getDSSProject().getName() + ".zip";
            File zipFile = new File(projectZip);
            if (zipFile.exists()) {
                LOGGER.info("exist project zip{} before publish ,now remove it", projectZip);
                boolean flag = zipFile.delete();
                if(flag){
                    LOGGER.info("zip file delete success!");
                }else {
                    LOGGER.info("zip file delete failed!");
                }
            }
        } catch (Exception e) {
            LOGGER.error("delete project dir or zip failed,reaseon:", e);
            throw new DSSRuntimeException(90020, e.getMessage());
        }
    }

    /**
     * Get the names of all nodes directly from all flows without recursion.
     */
    private List<String> getAllNodeName(List<Workflow> workflows) {
        List<String> nodeNames = new ArrayList<>();
        workflows.forEach(flow -> flow.getWorkflowNodes().forEach(node -> nodeNames.add(node.getName())));
        return nodeNames;
    }

    @Override
    public int getOrder() {
        return 5;
    }
}
