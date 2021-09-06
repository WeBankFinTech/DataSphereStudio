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

package com.webank.wedatasphere.dss.orchestrator.common.protocol;


public class ResponseConvertOrchestrator {

    private String id;
    private ResponseOperateOrchestrator response;

    public ResponseConvertOrchestrator() {
    }

    public ResponseConvertOrchestrator(String id, ResponseOperateOrchestrator response) {
        this.id = id;
        this.response = response;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ResponseOperateOrchestrator getResponse() {
        return response;
    }

    public void setResponse(ResponseOperateOrchestrator response) {
        this.response = response;
    }
}
