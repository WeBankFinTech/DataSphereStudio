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

package com.webank.wedatasphere.dss.standard.common.entity.ref;

import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;


public class CommonResponseRef extends AbstractResponseRef {

    public CommonResponseRef(String responseBody, int status) {
        super(responseBody, status);
        init();
    }

    protected void init() {
        if(StringUtils.isNotBlank(responseBody)) {
            responseMap = DSSCommonUtils.COMMON_GSON.fromJson(responseBody, Map.class);
        } else {
            responseMap = new HashMap<>();
        }
    }

    public CommonResponseRef(){
        this("",0);
    }

    @Override
    public Map<String, Object> toMap() {
        return responseMap;
    }

    @Override
    public String getErrorMsg() {
        return errorMsg;
    }

    @Override
    public Object getValue(String key) {
        return this.responseMap.get(key);
    }

    public void addResponse(String key, Object value){
        this.responseMap.put(key, value);
    }

    public static CommonResponseRef success() {
        return new CommonResponseRef();
    }

    public static CommonResponseRef success(String message) {
        CommonResponseRef commonResponseRef = new CommonResponseRef();
        commonResponseRef.responseBody = message;
        return commonResponseRef;
    }

    public static CommonResponseRef error(String errorMsg, Throwable cause) {
        CommonResponseRef commonResponseRef = new CommonResponseRef();
        commonResponseRef.status = 1;
        commonResponseRef.errorMsg = errorMsg;
        return commonResponseRef;
    }

    public static CommonResponseRef error(String errorMsg) {
        return error(errorMsg, null);
    }
}
