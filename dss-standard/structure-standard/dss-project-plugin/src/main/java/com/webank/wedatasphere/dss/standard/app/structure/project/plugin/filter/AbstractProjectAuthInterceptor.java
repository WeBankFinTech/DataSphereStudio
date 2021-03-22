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

package com.webank.wedatasphere.dss.standard.app.structure.project.plugin.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.webank.wedatasphere.dss.standard.app.structure.project.plugin.ProjectAuth;
import com.webank.wedatasphere.dss.standard.app.sso.origin.client.HttpClient;
import com.webank.wedatasphere.dss.standard.common.exception.AppStandardWarnException;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by enjoyyin on 2020/8/12.
 */
public abstract class AbstractProjectAuthInterceptor implements ProjectAuthInterceptor {

    private List<String> projectUris = new ArrayList<>();

    @Override
    public boolean isProjectRequest(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        return projectUris.stream().anyMatch(requestUri::matches);
    }

    protected void addProjectUri(String projectUri) {
        projectUris.add(projectUri);
    }

    @Override
    public String getForbiddenMsg(ProjectAuth projectAuth, ProjectRequestType projectRequestType,
                                  HttpServletRequest request) {
        String message = "You have no permission to " + projectRequestType.toString().toLowerCase() +
            " the content of project " + projectAuth.getProjectName();
        Object returnObj = getForbiddenMsg(message);
        if(returnObj instanceof String) {
            return (String) returnObj;
        } else {
            try {
                return HttpClient.objectMapper().writeValueAsString(returnObj);
            } catch (JsonProcessingException e) {
                throw new AppStandardWarnException(50002, "Serialize object to string failed! Object: " + returnObj, e);
            }
        }
    }

    protected abstract Object getForbiddenMsg(String message);
}
