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

package com.webank.wedatasphere.dss.standard.app.sso.builder.impl;

import com.webank.wedatasphere.dss.standard.app.sso.builder.DssMsgBuilderOperation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class DssMsgBuilderOperationImpl implements DssMsgBuilderOperation {

    private static final Logger LOGGER = LoggerFactory.getLogger(DssMsgBuilderOperationImpl.class);

    private String queryString;
    private Map<String, String[]> parameterMap;

    @Override
    public DssMsgBuilderOperation setQueryString(String queryString) {
        this.queryString = queryString;
        return this;
    }

    @Override
    public DssMsgBuilderOperation setParameterMap(Map<String, String[]> parameterMap) {
        this.parameterMap = parameterMap;
        return this;
    }

    @Override
    public boolean isDSSMsgRequest() {
        return parameterMap.containsKey("dssurl");
    }

    private String getOrNull(String key) {
        String[] values = parameterMap.get(key);
        if(values == null || values.length == 0) {
            return null;
        } else {
            return values[0];
        }
    }

    @Override
    public DSSMsg getBuiltMsg() {
        DSSMsgImpl dssMsg = new DSSMsgImpl();
        dssMsg.setRedirectUrl(getOrNull("redirect"));
        dssMsg.setDSSUrl(getOrNull("dssurl"));
        dssMsg.setWorkspaceName(getOrNull("workspace"));
        dssMsg.setAppName(getOrNull("appName"));
        Map<String, String> cookies = new HashMap<>();
        String cookiesStr = getOrNull("cookies");
        if(StringUtils.isNotBlank(cookiesStr)) {
            Arrays.stream(cookiesStr.split(";")).forEach(cookie -> {
                int index = cookie.indexOf('=');
                String key = cookie.substring(0, index).trim();
                String value = cookie.substring(index + 1).trim();
                cookies.put(key, value);
            });
        }
//        LOGGER.info("Set cookies from dssMsg: "+cookies.toString());
        dssMsg.setCookies(cookies);
        return dssMsg;
    }
}
