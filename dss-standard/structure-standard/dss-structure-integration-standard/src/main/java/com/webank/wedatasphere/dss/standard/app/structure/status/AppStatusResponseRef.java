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

package com.webank.wedatasphere.dss.standard.app.structure.status;

import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRefBuilder;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRefImpl;

public interface AppStatusResponseRef extends ResponseRef {

    AppStatus getAppStatus();

    static Builder newBuilder() {
        return new Builder();
    }

    class Builder extends ResponseRefBuilder.ExternalResponseRefBuilder<Builder, AppStatusResponseRef> {
        protected AppStatus appStatus;

        public Builder setAppStatus(AppStatus appStatus) {
            this.appStatus = appStatus;
            return this;
        }

        @Override
        protected AppStatusResponseRef createResponseRef() {
            return new AppStatusResponseRefImpl();
        }

        class AppStatusResponseRefImpl extends ResponseRefImpl implements AppStatusResponseRef {
            public AppStatusResponseRefImpl() {
                super(Builder.this.responseBody, Builder.this.status,
                        Builder.this.errorMsg, Builder.this.responseMap);
            }

            @Override
            public AppStatus getAppStatus() {
                return appStatus;
            }
        }
    }

}
