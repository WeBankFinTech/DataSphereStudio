/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package com.webank.wedatasphere.dss.linkis.node.execution;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.webank.wedatasphere.dss.linkis.node.execution.conf.LinkisJobExecutionConfiguration;
import com.webank.wedatasphere.dss.linkis.node.execution.entity.ContextInfo;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by johnnwang on 2019/9/26.
 */
public class WorkflowContextImpl implements WorkflowContext {

    private Cache<String, ContextInfo> contextCache = CacheBuilder.newBuilder()
            .maximumSize(LinkisJobExecutionConfiguration.LINKIS_CACHE_MAX_SIZE.getValue())
            .expireAfterWrite(LinkisJobExecutionConfiguration.LINKIS_CACHE_EXPIRE_TIME.getValue(), TimeUnit.HOURS)
            .build();


    @Override
    public Object getValue(String key) {
        ContextInfo contextInfo = contextCache.getIfPresent(key);
        if (contextInfo != null && contextInfo.getReadNum() > 0) {
            int readNum = contextInfo.getReadNum() - 1;
            Object value = contextInfo.getValue();
            if (readNum <= 0) {
                contextCache.invalidate(key);
            }
            contextInfo.setReadNum(contextInfo.getReadNum() - 1);
            return value;
        }
        if (contextInfo != null && contextInfo.getReadNum() < 0) {
            return contextInfo.getValue();
        }
        return null;
    }

    @Override
    public List<Object> getValues(String prefix) {
        List<Object> values = new ArrayList<>();
        Iterator<String> keys = keyIterator();
        while (keys.hasNext()) {
            String key = keys.next();
            if (key.startsWith(prefix)) {
                values.add(getValue(key));
            }
        }
        return values;
    }

    @Override
    public Iterator<String> keyIterator() {
        return contextCache.asMap().keySet().iterator();
    }

    @Override
    public Map<String, Object> getSubMapByPrefix(String keyPrefix) {
        Map<String, Object> map = new HashMap<>();
        Iterator<String> keys = keyIterator();
        while (keys.hasNext()) {
            String key = keys.next();
            if (key.startsWith(keyPrefix)) {
                map.put(StringUtils.substringAfter(key, keyPrefix), getValue(key));
            }
        }
        return map;
    }

    @Override
    public void setValue(String key, Object value, int readNum) {

        contextCache.put(key, new ContextInfo(value, readNum));

    }

}
