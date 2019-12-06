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


import com.webank.wedatasphere.dss.server.service.DWSProjectService;


public class PublishSubmitJob extends PublishJob {

    private DWSProjectService projectService;

    private String comment;

    public PublishSubmitJob(DWSProjectService projectService, String user, String comment, Long projectVersionID) {
        this.projectService = projectService;
        this.user = user;
        this.comment = comment;
        this.projectVersionID = projectVersionID;
    }

    @Override
    public void run() {
        try {
            publishListner.onPublishRunning(projectVersionID,"开始发布工程："+projectVersionID);
            projectService.publish(projectVersionID, user, comment);
            publishListner.onPublishSucceed(projectVersionID,"工程发布成功："+ projectVersionID);
        } catch (Exception e) {
            logger.info("发布失败，原因：",e);
            Throwable cause = e.getCause();
            String errorMsg = cause == null?e.getMessage():cause.getMessage();
            publishListner.onPublishFailed(projectVersionID,"工程发布失败："+ errorMsg);
        }
    }
}
