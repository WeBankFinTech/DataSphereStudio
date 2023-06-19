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

package com.webank.wedatasphere.dss.framework.project.conf;

import com.webank.wedatasphere.dss.common.service.BMLService;
import com.webank.wedatasphere.dss.framework.project.service.DSSFrameworkProjectService;
import com.webank.wedatasphere.dss.framework.project.service.DSSProjectUserService;
import com.webank.wedatasphere.dss.framework.project.service.impl.DSSFrameworkProjectServiceImpl;
import com.webank.wedatasphere.dss.framework.project.service.impl.DSSProjectUserServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ProjectSpringConf {

    @Bean
    @ConditionalOnMissingBean
    public DSSProjectUserService createDSSProjectUserService() {
        return new DSSProjectUserServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public DSSFrameworkProjectService createDSSFrameworkProjectServiceImpl() {
        return new DSSFrameworkProjectServiceImpl();
    }

    @Bean(name = "projectBmlService")
    public BMLService createBmlService() {
        return BMLService.getInstance();
    }
}
