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

package com.webank.wedatasphere.dss.appconn.qualitis.ref.entity;

import com.webank.wedatasphere.dss.standard.app.development.ref.ExportRequestRef;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import java.util.Map;

public class QualitisExportRequestRef implements ExportRequestRef {

    private SSOUrlBuilderOperation ssoUrlBuilderOperation;

    public SSOUrlBuilderOperation getSsoUrlBuilderOperation() {
        return ssoUrlBuilderOperation;
    }

    public void setSsoUrlBuilderOperation(SSOUrlBuilderOperation ssoUrlBuilderOperation) {
        this.ssoUrlBuilderOperation = ssoUrlBuilderOperation;
    }

    @Override
    public Object getParameter(String key) {
        return null;
    }

    @Override
    public void setParameter(String key, Object value) {

    }

    @Override
    public Map<String, Object> getParameters() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getType() {
        return null;
    }
}
