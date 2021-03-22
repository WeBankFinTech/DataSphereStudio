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

package com.webank.wedatasphere.dss.standard.app.sso;

import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.builder.impl.SSOUrlBuilderOperationImpl;
import com.webank.wedatasphere.dss.standard.app.sso.plugin.SSOIntegrationConf;

import java.util.HashMap;
import java.util.Map;

/**
 * @author allenlliu
 * @date 2020/12/17 16:39
 */

public class Workspace {

    protected String workspaceName;

    protected transient SSOUrlBuilderOperation ssoUrlBuilderOperation;

    private String operationStr;

    public String getWorkspaceName() {
        return this.workspaceName;
    }


    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }


    public SSOUrlBuilderOperation getSSOUrlBuilderOperation() {
        if (this.ssoUrlBuilderOperation == null) {
            this.ssoUrlBuilderOperation = SSOUrlBuilderOperationImpl.restore(operationStr);
        }
        return this.ssoUrlBuilderOperation;
    }


    public void setSSOUrlBuilderOperation(SSOUrlBuilderOperation ssoUrlBuilderOperation) {
        this.ssoUrlBuilderOperation = ssoUrlBuilderOperation;
        this.operationStr = SSOIntegrationConf.gson().toJson(ssoUrlBuilderOperation);
    }

    public String getOperationStr() {
        return operationStr;
    }

    public void setOperationStr(String operationStr) {
        this.operationStr = operationStr;
    }
}