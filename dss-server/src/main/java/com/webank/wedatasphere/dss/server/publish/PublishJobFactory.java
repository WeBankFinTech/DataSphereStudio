/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package com.webank.wedatasphere.dss.server.publish;


import com.webank.wedatasphere.dss.server.conf.DSSServerConf;
import com.webank.wedatasphere.dss.server.service.DWSProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;


@Component
public class PublishJobFactory {
    @Autowired
    private PublishManager publishManager;
    @Autowired
    private DWSProjectService projectService;

    public PublishSubmitJob createSubmitPublishJob(Long projectVersionID, String user, String comment){
        PublishSubmitJob job = new PublishSubmitJob(projectService, user, comment, projectVersionID);
        job.setPublishListner(publishManager);
        return job;
    }

    public PublishSubmitJobDeamon createSubmitPublishJobDeamon(Future future,PublishSubmitJob publishSubmitJob){
        PublishSubmitJobDeamon job = new PublishSubmitJobDeamon(future, (int) DSSServerConf.PUBLISH_TIMEOUT.getValue());
        job.setProjectVersionID(publishSubmitJob.getProjectVersionID());
        return job;
    }
}
