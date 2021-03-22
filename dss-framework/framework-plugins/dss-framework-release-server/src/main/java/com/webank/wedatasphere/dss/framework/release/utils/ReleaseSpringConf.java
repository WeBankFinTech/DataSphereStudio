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

package com.webank.wedatasphere.dss.framework.release.utils;

import com.webank.wedatasphere.dss.orchestrator.core.ref.OrchestratorExportRequestRef;
import com.webank.wedatasphere.dss.orchestrator.core.ref.OrchestratorImportRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.publish.scheduler.ProjectPublishToSchedulerRef;
import com.webank.wedatasphere.dss.standard.app.development.publish.scheduler.PublishToSchedulerRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.DefaultRefFactory;
import com.webank.wedatasphere.dss.standard.common.entity.ref.RefFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * created by cooperyang on 2020/12/13
 * Description:
 */
@Configuration
public class ReleaseSpringConf {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReleaseSpringConf.class);

    static {
        LOGGER.info("load all beans for release server");
    }

    @Bean(name = "orcExportRefFactory")
    public RefFactory<OrchestratorExportRequestRef> buildExportRefFactory(){
        return new DefaultRefFactory<OrchestratorExportRequestRef>();
    }


    @Bean(name = "orcImportRefFactory")
    public RefFactory<OrchestratorImportRequestRef> buildImportRefFactory(){
        return new DefaultRefFactory<OrchestratorImportRequestRef>();
    }

    @Bean(name = "projectPublishRefFactory")
    public RefFactory<ProjectPublishToSchedulerRef> buildPublishRefFactory(){
        return new DefaultRefFactory<ProjectPublishToSchedulerRef>();
    }


}
