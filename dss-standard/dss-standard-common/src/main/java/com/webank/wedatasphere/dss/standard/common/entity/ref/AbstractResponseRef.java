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

package com.webank.wedatasphere.dss.standard.common.entity.ref;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by enjoyyin on 2020/11/16.
 */
public abstract class AbstractResponseRef implements ResponseRef {

    protected String responseBody;
    protected int status = -1;
    protected String errorMsg;
    protected Map<String, Object> responseMap;

    protected AbstractResponseRef(String responseBody, int status) {
        this.responseBody = responseBody;
        this.status = status;
    }

    @Override
    public Object getValue(String key) {
        return toMap().get(key);
    }

    @Override
    public String getResponseBody() {
        return responseBody;
    }

    @Override
    public int getStatus() {
        return status;
    }
}
