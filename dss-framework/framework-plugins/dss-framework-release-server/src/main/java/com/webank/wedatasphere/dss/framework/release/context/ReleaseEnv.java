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

package com.webank.wedatasphere.dss.framework.release.context;

import com.webank.wedatasphere.dss.framework.release.job.listener.ReleaseJobListener;
import com.webank.wedatasphere.dss.framework.release.service.ExportService;
import com.webank.wedatasphere.dss.framework.release.service.ImportService;
import com.webank.wedatasphere.dss.framework.release.service.OrchestratorReleaseInfoService;
import com.webank.wedatasphere.dss.framework.release.service.ProjectService;
import com.webank.wedatasphere.dss.framework.release.service.PublishService;
import com.webank.wedatasphere.dss.framework.release.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * created by cooperyang on 2020/12/10
 * Description:
 */
@Component
public class ReleaseEnv {

    @Autowired
    private ImportService importService;

    @Autowired
    private ExportService exportService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private PublishService publishService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private OrchestratorReleaseInfoService orchestratorReleaseInfoService;

    @Autowired
    @Qualifier("releaseJobListener")
    private ReleaseJobListener releaseJobListener;

    public ImportService getImportService() {
        return importService;
    }

    public ExportService getExportService() {
        return exportService;
    }

    public ProjectService getProjectService() {
        return projectService;
    }

    public PublishService getPublishService() {
        return publishService;
    }

    public TaskService getTaskService() {
        return taskService;
    }

    public OrchestratorReleaseInfoService getOrchestratorReleaseInfoService() {
        return orchestratorReleaseInfoService;
    }

    public ReleaseJobListener getReleaseJobListener() {
        return releaseJobListener;
    }

}
