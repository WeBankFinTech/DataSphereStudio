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

package com.webank.wedatasphere.dss.standard.app.structure.project.ref;

import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRefBuilder;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRefImpl;

import java.util.Map;


public interface ProjectResponseRef extends ResponseRef {

    Long getRefProjectId();

    static InternalBuilder newInternalBuilder() {
        return new InternalBuilder();
    }

    static ExternalBuilder newExternalBuilder() {
        return new ExternalBuilder();
    }

    class InternalBuilder extends ResponseRefBuilder.InternalResponseRefBuilder<InternalBuilder, InternalProjectResponseRef> {
        protected Long refProjectId;
        public InternalBuilder setRefProjectId(Long refProjectId) {
            this.refProjectId = refProjectId;
            return this;
        }
        @Override
        protected InternalProjectResponseRef createResponseRef(String responseBody, int status, String errorMsg, Map<String, Object> responseMap) {
            return new InternalProjectResponseRef(responseBody, status, errorMsg, responseMap, refProjectId);
        }
    }

    class ExternalBuilder extends ResponseRefBuilder.ExternalResponseRefBuilder<ExternalBuilder, ProjectResponseRef> {
        protected Long refProjectId;

        public ExternalBuilder setRefProjectId(Long refProjectId) {
            this.refProjectId = refProjectId;
            return this;
        }

        @Override
        protected ProjectResponseRef createResponseRef() {
            return new ProjectResponseRefImpl();
        }

        class ProjectResponseRefImpl extends ResponseRefImpl implements ProjectResponseRef {
            public ProjectResponseRefImpl() {
                super(ExternalBuilder.this.responseBody, ExternalBuilder.this.status,
                        ExternalBuilder.this.errorMsg, ExternalBuilder.this.responseMap);
            }
            @Override
            public Long getRefProjectId() {
                return refProjectId;
            }
        }
    }

}
