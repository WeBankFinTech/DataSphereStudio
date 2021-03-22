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
import com.webank.wedatasphere.dss.appconn.schedulis.entity.AzkabanSchedulerNode;
import com.webank.wedatasphere.dss.appconn.schedulis.entity.LinkisAzkabanSchedulerNode;
import com.webank.wedatasphere.dss.appconn.schedulis.linkisjob.LinkisJobConverter;
import com.webank.wedatasphere.dss.appconn.schedule.core.entity.SchedulerNode;
import com.webank.wedatasphere.dss.appconn.schedule.core.hooks.AbstractNodePublishHook;
import com.webank.wedatasphere.dss.common.entity.Resource;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;


public class LinkisAzkabanNodePublishHook extends AbstractNodePublishHook {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinkisAzkabanNodePublishHook.class);
    private LinkisJobConverter linkisJobConverter;

    public LinkisAzkabanNodePublishHook(){
        this.linkisJobConverter = new LinkisJobConverter();
    }

    @Override
    public void prePublish(SchedulerNode schedulerNode) throws DSSErrorException {
        writeNodeTojobLocal(schedulerNode);
        writeNodeResourcesToLocal(schedulerNode);
    }

    private void writeNodeTojobLocal(SchedulerNode schedulerNode) throws DSSErrorException {
        LinkisAzkabanSchedulerNode azkabanSchedulerNode = (LinkisAzkabanSchedulerNode) schedulerNode;
        FileOutputStream os = null;
        try {
            String storePath = azkabanSchedulerNode.getStorePath();
            File jobDirFile = new File(storePath);
            FileUtils.forceMkdir(jobDirFile);
            File jobFile = new File(storePath,schedulerNode.getName() + AzkabanConstant.AZKABAN_JOB_SUFFIX);
            jobFile.createNewFile();
            String nodeString = azkabanSchedulerNode.toJobString(linkisJobConverter);
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
        List<Resource> nodeResources = schedulerNode.getDSSNode().getResources();
        if(nodeResources == null || nodeResources.isEmpty()) {return;}
        FileOutputStream os = null;
        try {
            String storePath = ((AzkabanSchedulerNode)schedulerNode).getStorePath();
            File jobFile = new File(storePath,schedulerNode.getName() + AzkabanConstant.AZKABAN_JOB_SUFFIX);
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

}
