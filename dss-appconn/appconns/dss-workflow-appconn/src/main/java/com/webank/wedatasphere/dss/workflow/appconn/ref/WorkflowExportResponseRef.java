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

package com.webank.wedatasphere.dss.workflow.appconn.ref;

import com.webank.wedatasphere.dss.standard.common.entity.ref.AbstractResponseRef;


import java.util.HashMap;
import java.util.Map;

/**
 * @author allenlliu
 * @date 2020/11/19 17:13
 */
public class WorkflowExportResponseRef extends AbstractResponseRef {

    private String resourceId;

    private String version;

    private Long flowID;


    private String errorMsg;

    private Map<String, Object> responseMap = new HashMap<>();


    public WorkflowExportResponseRef(String responseBody, int status) {
        super(responseBody, status);
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Long getFlowID() {
        return flowID;
    }

    public void setFlowID(Long flowID) {
        this.flowID = flowID;
    }

    @Override
    public Object getValue(String key) {
        return this.responseMap.get(key);
    }

    public void addResponse(String key, Object value){
        this.responseMap.put(key, value);
    }

    @Override
    public Map<String, Object> toMap() {
        return this.responseMap;
    }

    @Override
    public String getResponseBody() {
        return this.responseBody;
    }

    @Override
    public int getStatus() {
        return 0;
    }

    @Override
    public String getErrorMsg() {
        return this.errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

}
