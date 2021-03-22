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

package com.webank.wedatasphere.dss.standard.app.sso.origin.filter.spring;

import com.webank.wedatasphere.dss.standard.app.sso.origin.plugin.OriginSSOPluginFilter;
import com.webank.wedatasphere.dss.standard.app.sso.plugin.filter.UserInterceptor;

import javax.servlet.FilterConfig;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Created by enjoyyin on 2020/8/12.
 */
@Component
public class SpringOriginSSOPluginFilter extends OriginSSOPluginFilter {

    @Override
    public UserInterceptor getUserInterceptor(FilterConfig filterConfig) {
        WebApplicationContext webApplicationContext =
            WebApplicationContextUtils.getRequiredWebApplicationContext(filterConfig.getServletContext());
        return webApplicationContext.getBean(UserInterceptor.class);
    }
}
