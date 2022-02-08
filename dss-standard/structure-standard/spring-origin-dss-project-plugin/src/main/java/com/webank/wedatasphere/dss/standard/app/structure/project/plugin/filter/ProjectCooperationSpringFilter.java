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

package com.webank.wedatasphere.dss.standard.app.structure.project.plugin.filter;

import com.webank.wedatasphere.dss.standard.app.sso.plugin.filter.UserInterceptor;
import com.webank.wedatasphere.dss.standard.app.structure.project.plugin.ProjectCooperationPlugin;
import com.webank.wedatasphere.dss.standard.app.structure.project.plugin.origin.OriginProjectCooperationPlugin;

import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.FilterConfig;

@Component
public class ProjectCooperationSpringFilter extends ProjectCooperationFilter {

    @Override
    public ProjectAuthInterceptor getProjectAuthInterceptor(FilterConfig filterConfig) {
        WebApplicationContext webApplicationContext =
                WebApplicationContextUtils.getRequiredWebApplicationContext(
                        filterConfig.getServletContext());
        return webApplicationContext.getBean(ProjectAuthInterceptor.class);
    }

    @Override
    public UserInterceptor getUserAuthInterceptor(FilterConfig filterConfig) {
        WebApplicationContext webApplicationContext =
                WebApplicationContextUtils.getRequiredWebApplicationContext(
                        filterConfig.getServletContext());
        return webApplicationContext.getBean(UserInterceptor.class);
    }

    @Override
    public ProjectCooperationPlugin getProjectCooperationPlugin(FilterConfig filterConfig) {
        return OriginProjectCooperationPlugin.getProjectCooperatePlugin();
    }
}
