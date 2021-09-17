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

package com.webank.wedatasphere.dss.appconn.visualis.ref;

import com.webank.wedatasphere.dss.appconn.visualis.utils.VisualisNodeUtils;
import com.webank.wedatasphere.dss.standard.app.development.ref.DSSCommonResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;

public class VisualisCommonResponseRef extends DSSCommonResponseRef {


    public VisualisCommonResponseRef(String responseBody) throws Exception {
        super(responseBody);
    }

    public String getWidgetId() throws ExternalOperationFailedException {
        return VisualisNodeUtils.getWidgetId(responseBody);
    }

    public String getDisplayId() throws ExternalOperationFailedException {
        return VisualisNodeUtils.getDisplayId(responseBody);
    }

    public String getDashboardId() throws ExternalOperationFailedException {
        return VisualisNodeUtils.getDashboardPortalId(responseBody);
    }
}
