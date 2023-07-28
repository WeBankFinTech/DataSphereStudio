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

package com.webank.wedatasphere.dss.workflow.conf;

import com.webank.wedatasphere.dss.common.service.BMLService;
import com.webank.wedatasphere.dss.workflow.common.parser.WorkFlowParser;
import com.webank.wedatasphere.dss.workflow.service.DSSFlowService;
import com.webank.wedatasphere.dss.workflow.service.PublishService;
import com.webank.wedatasphere.dss.workflow.service.SaveFlowHook;
import com.webank.wedatasphere.dss.workflow.service.impl.PublishServiceImpl;
import com.webank.wedatasphere.dss.workflow.service.impl.SaveFlowHookImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class WorkflowServerSpringConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public PublishService createPublishService(@Autowired DSSFlowService dssFlowService,
                                               @Autowired WorkFlowParser workFlowParser) {
        PublishServiceImpl publishService = new PublishServiceImpl();
        publishService.setDssFlowService(dssFlowService);
        publishService.setWorkFlowParser(workFlowParser);
        return publishService;
    }

    @Bean(name = "workflowBmlService")
    public BMLService createBmlService() {
        return BMLService.getInstance();
    }

    @Bean
    @ConditionalOnMissingBean
    public SaveFlowHook createSaveFlowHook(){
        return new SaveFlowHookImpl();
    }

}
