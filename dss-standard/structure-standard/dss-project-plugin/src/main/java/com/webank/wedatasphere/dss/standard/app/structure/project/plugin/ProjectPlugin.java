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

package com.webank.wedatasphere.dss.standard.app.structure.project.plugin;

import com.webank.wedatasphere.dss.standard.app.sso.SSOIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.sso.origin.OriginSSOIntegrationStandardFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;


public interface ProjectPlugin {

    //TODO 如果一个用户从dss空间进入到qualitis建立工程，添加一个是否是创建工程的请求，添加一个开关参数，是否允许用户建工程
    List<String> getProjects(HttpServletRequest request);

    default <T> List<T> filterProjects(List<T> projects, HttpServletRequest request,
                                        Function<T, String> getProjectId) {
        SSOIntegrationStandard ssoIntegrationStandard = new OriginSSOIntegrationStandardFactory().getSSOIntegrationStandard();
        if(!ssoIntegrationStandard.getSSOPluginService().createSSOMsgParseOperation().isDssRequest(request)) {
            return projects;
        }
        List<String> projectList = getProjects(request);
        if(projectList == null) {
            return new ArrayList<>();
        }
        List<T> filteredList =  projects.stream().filter(t -> projectList.contains(getProjectId.apply(t)))
            .collect(Collectors.toList());
        return filteredList;
    }

}
