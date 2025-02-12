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

package com.webank.wedatasphere.dss.workflow.scheduler;

import org.apache.linkis.common.utils.Utils;

import java.util.concurrent.ExecutorService;

import static com.webank.wedatasphere.dss.workflow.constant.DSSWorkFlowConstant.NODE_EXPORT_IMPORT_THREAD_NUM;

public class DssJobThreadPool {

    private static ExecutorService executorService = Utils.newFixedThreadPool(100, "project-publish", false);
    private static ExecutorService executorServiceDeamon = Utils.newFixedThreadPool(100, "project-publish-deamon", true);
    public static ExecutorService nodeExportThreadPool = Utils.newFixedThreadPool(NODE_EXPORT_IMPORT_THREAD_NUM.getValue(), "workflowNode-Export-Thread-", true);
    public static ExecutorService nodeImportThreadPool = Utils.newFixedThreadPool(NODE_EXPORT_IMPORT_THREAD_NUM.getValue(), "workflowNode-Import-Thread-", true);
    public static ExecutorService nodeUploadThreadPool = Utils.newFixedThreadPool(NODE_EXPORT_IMPORT_THREAD_NUM.getValue(), "workflowNode-Upload-Thread-", true);
    public static ExecutorService get() {
        return executorService;
    }

    public static ExecutorService getDeamon() {
        return executorServiceDeamon;
    }
}
