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

package com.webank.wedatasphere.dss.appconn.qualitis.ref.operation;

import com.webank.wedatasphere.dss.standard.app.development.operation.RefQueryOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.OpenRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.dss.appconn.qualitis.ref.entity.QualitisOpenResponseRef;
import com.webank.wedatasphere.dss.appconn.qualitis.ref.entity.QualitisOpenRequestRef;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.linkis.server.BDPJettyServerHelper;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import java.util.Map;

public class QualitisRefQueryOperation implements RefQueryOperation<OpenRequestRef> {

    DevelopmentService developmentService;

    @Override
    public ResponseRef query(OpenRequestRef ref) throws ExternalOperationFailedException {
        QualitisOpenRequestRef qualitisOpenRequestRef = (QualitisOpenRequestRef) ref;
        try {
            String externalContent = BDPJettyServerHelper.jacksonJson().writeValueAsString(qualitisOpenRequestRef.getJobContent());
            Long projectId = (Long) qualitisOpenRequestRef.getParameter("projectId");
            String baseUrl = qualitisOpenRequestRef.getParameter("redirectUrl").toString();
            String jumpUrl = baseUrl;

            return new QualitisOpenResponseRef(getEnvUrl(jumpUrl, qualitisOpenRequestRef), 0);
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90177, "Failed to parse jobContent ", e);
        }
    }

    public String getEnvUrl(String url, QualitisOpenRequestRef qualitisOpenRequestRef ){
        String env = ((Map<String, Object>) qualitisOpenRequestRef.getParameter("params")).get(DSSCommonUtils.DSS_LABELS_KEY).toString();
        return url + "?env=" + env.toLowerCase();
    }


    @Override
    public void setDevelopmentService(DevelopmentService service) {
        this.developmentService = service;
    }
}
