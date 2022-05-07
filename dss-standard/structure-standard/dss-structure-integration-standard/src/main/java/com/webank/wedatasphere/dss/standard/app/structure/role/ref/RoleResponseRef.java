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

package com.webank.wedatasphere.dss.standard.app.structure.role.ref;

import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRefBuilder;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRefImpl;

public interface RoleResponseRef extends ResponseRef {

    Long getRefRoleId();

    class Builder extends ResponseRefBuilder.ExternalResponseRefBuilder<Builder, RoleResponseRef> {
        protected Long refRoleId;

        public Builder setRefRoleId(Long refRoleId) {
            this.refRoleId = refRoleId;
            return this;
        }

        @Override
        protected RoleResponseRef createResponseRef() {
            return new RoleResponseRefImpl();
        }

        class RoleResponseRefImpl extends ResponseRefImpl implements RoleResponseRef {
            public RoleResponseRefImpl() {
                super(Builder.this.responseBody, Builder.this.status,
                        Builder.this.errorMsg, Builder.this.responseMap);
            }
            @Override
            public Long getRefRoleId() {
                return refRoleId;
            }
        }
    }

}
