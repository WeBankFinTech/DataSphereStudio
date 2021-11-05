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

package com.webank.wedatasphere.dss.orchestrator.loader;

import com.webank.wedatasphere.dss.common.utils.ClassUtils;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.orchestrator.core.impl.AbstractDSSOrchestratorContext;
import com.webank.wedatasphere.dss.orchestrator.core.plugin.DSSOrchestratorPlugin;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class SpringDSSOrchestratorContext extends AbstractDSSOrchestratorContext {

    private final Logger LOGGER = LoggerFactory.getLogger(SpringDSSOrchestratorContext.class);

    @Autowired
    private List<DSSOrchestratorPlugin> dssOrchestratorPlugins;

    @Override
    @PostConstruct
    public void initialize() {
        if(dssOrchestratorPlugins == null || dssOrchestratorPlugins.isEmpty()) {
            dssOrchestratorPlugins = ClassUtils.getInstances(DSSOrchestratorPlugin.class);
        } else {
            List<DSSOrchestratorPlugin> others = ClassUtils.getClasses(DSSOrchestratorPlugin.class).stream()
                .filter(clazz -> dssOrchestratorPlugins.stream().noneMatch(clazz::isInstance))
                .map(DSSExceptionUtils.map(Class::newInstance)).collect(Collectors.toList());
            dssOrchestratorPlugins.addAll(others);
        }
        dssOrchestratorPlugins.forEach(DSSOrchestratorPlugin::init);
        LOGGER.info("The DSSOrchestratorPlugin list is {}.", dssOrchestratorPlugins);
    }

    @Override
    public List<DSSOrchestratorPlugin> getOrchestratorPlugins() {
        return dssOrchestratorPlugins;
    }

    @Override
    @PreDestroy
    public void close() {
        super.close();
    }
}
