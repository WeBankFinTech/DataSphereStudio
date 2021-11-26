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

package com.webank.wedatasphere.dss.appconn.schedulis.entity;

import com.webank.wedatasphere.dss.orchestrator.converter.standard.ref.DSSToRelConversionRequestRef;
import com.webank.wedatasphere.dss.orchestrator.converter.standard.ref.ProjectToRelConversionRequestRef;
import com.webank.wedatasphere.dss.workflow.conversion.entity.ConvertedRel;
import com.webank.wedatasphere.dss.workflow.conversion.entity.PreConversionRel;
import com.webank.wedatasphere.dss.workflow.conversion.entity.PreConversionRelImpl;

public class AzkabanConvertedRel extends PreConversionRelImpl implements ConvertedRel {

    private String storePath;
    public static final String DATE_FORMAT = "yyyyMMddHHmmss";

    public AzkabanConvertedRel(PreConversionRel rel) {
        setWorkflows(rel.getWorkflows());
        setDSSToRelConversionRequestRef(rel.getDSSToRelConversionRequestRef());
    }

    public AzkabanConvertedRel(AzkabanConvertedRel rel) {
        this((PreConversionRel) rel);
        storePath = rel.getStorePath();
    }

    @Override
    public ProjectToRelConversionRequestRef getDSSToRelConversionRequestRef() {
        return (ProjectToRelConversionRequestRef) super.getDSSToRelConversionRequestRef();
    }

    public String getStorePath() {
        return storePath;
    }

    public void setStorePath(String storePath) {
        this.storePath = storePath;
    }

}
