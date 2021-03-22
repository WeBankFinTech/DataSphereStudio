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

package com.webank.wedatasphere.dss.workflow.io.scheduler;

import com.webank.wedatasphere.dss.workflow.scheduler.DssJob;

public class PublishJob extends DssJob {



    private String comment;

    private Long[] flowIDs;


    @Override
    public void run() {
        try {
            dssJobListener.onJobRunning(this, "导出job正在运行：" + id);
            //todo 工作流发布逻辑

            dssJobListener.onJobSucceed(this, "导出job执行成功：" + id);
        } catch (Throwable e) {
            logger.error("导出job执行失败，原因：", e);
            Throwable cause = e.getCause();
            String errorMsg = cause == null ? e.getMessage() : cause.getMessage();
            dssJobListener.onJobFailed(this, "导出job执行失败：" + errorMsg);
        }
    }



    public Long[] getFlowIDs() {
        return flowIDs;
    }

    public void setFlowIDs(Long[] flowIDs) {
        this.flowIDs = flowIDs;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


}