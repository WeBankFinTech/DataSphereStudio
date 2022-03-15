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

package com.webank.wedatasphere.dss.orchestrator.common.ref;

import com.webank.wedatasphere.dss.orchestrator.common.entity.OrchestratorVo;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRefBuilder;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRefImpl;

import java.util.List;


public interface OrchestratorQueryResponseRef extends ResponseRef {

    List<OrchestratorVo> getOrchestratorVos();

    static OrchestratorQueryResponseRefBuilder newBuilder() {
        return new OrchestratorQueryResponseRefBuilder();
    }

    class OrchestratorQueryResponseRefBuilder
            extends ResponseRefBuilder.ExternalResponseRefBuilder<OrchestratorQueryResponseRefBuilder, OrchestratorQueryResponseRef> {

        private List<OrchestratorVo> orchestratorVoList;

        public OrchestratorQueryResponseRefBuilder setOrchestratorVoList(List<OrchestratorVo> orchestratorVoList) {
            this.orchestratorVoList = orchestratorVoList;
            return this;
        }

        class OrchestratorQueryResponseRefImpl extends ResponseRefImpl implements OrchestratorQueryResponseRef {
            public OrchestratorQueryResponseRefImpl() {
                super(OrchestratorQueryResponseRefBuilder.this.responseBody, OrchestratorQueryResponseRefBuilder.this.status,
                        OrchestratorQueryResponseRefBuilder.this.errorMsg, OrchestratorQueryResponseRefBuilder.this.responseMap);
            }
            @Override
            public List<OrchestratorVo> getOrchestratorVos() {
                return orchestratorVoList;
            }
        }

        @Override
        protected OrchestratorQueryResponseRefImpl createResponseRef() {
            return new OrchestratorQueryResponseRefImpl();
        }
    }
}
