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

package com.webank.wedatasphere.dss.framework.workspace.util;


import com.webank.wedatasphere.dss.framework.workspace.exception.DSSWorkspaceLoginFailException;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;


@Component
public class RestulHelper {

    @Autowired
    private WorkspaceDBHelper workspaceDBHelper;

    public String getLoginUser(HttpServletRequest request)throws ErrorException {
        try{
            return SecurityFilter.getLoginUsername(request);
        }catch(Exception e){
            throw new DSSWorkspaceLoginFailException(80013, "You are not logged in");
        }
    }



}
