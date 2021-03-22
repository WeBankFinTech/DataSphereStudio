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

package com.webank.wedatasphere.dss.standard.app.development.execution.common;

import com.webank.wedatasphere.dss.standard.common.entity.ref.AbstractResponseRef;
import java.util.HashMap;
import java.util.Map;

/**
 * created by cooperyang on 2019/9/25
 * Description:
 */
public class CompletedExecutionResponseRef extends AbstractResponseRef {

    private Throwable exception;

    public CompletedExecutionResponseRef(int status) {
        super(null, status);
    }

    public CompletedExecutionResponseRef(String responseBody, int status) {
        super(responseBody, status);
    }

    public void setIsSucceed(boolean isSucceed) {
        if(isSucceed) {
            status = 200;
        } else {
            status = 400;
        }
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    @Override
    public String getErrorMsg() {
        return errorMsg;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    @Override
    public Map<String, Object> toMap() {
        if(responseMap == null || (Integer) responseMap.get("status") == -1) {
            responseMap = new HashMap<>(3);
            responseMap.put("errorMsg", errorMsg);
            responseMap.put("status", status);
            responseMap.put("exception", exception);
        }
        return responseMap;
    }
}
