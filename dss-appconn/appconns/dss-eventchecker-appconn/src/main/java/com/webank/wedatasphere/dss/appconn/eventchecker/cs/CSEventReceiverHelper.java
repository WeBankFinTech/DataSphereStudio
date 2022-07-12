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

package com.webank.wedatasphere.dss.appconn.eventchecker.cs;

import org.apache.linkis.cs.client.service.CSVariableService;
import org.apache.linkis.cs.client.utils.ContextServiceUtils;
import org.apache.linkis.cs.client.utils.SerializeHelper;
import org.apache.linkis.cs.common.entity.enumeration.ContextScope;
import org.apache.linkis.cs.common.entity.enumeration.ContextType;
import org.apache.linkis.cs.common.entity.object.LinkisVariable;
import org.apache.linkis.cs.common.entity.source.CommonContextKey;
import org.apache.linkis.cs.common.entity.source.ContextKey;
import org.apache.linkis.cs.common.utils.CSCommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class CSEventReceiverHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(CSEventReceiverHelper.class);

    public static void putVariable(Properties properties, String msgBody, String saveKey) {
        String contextIDStr = ContextServiceUtils.getContextIDStrByProperties(properties);
        String nodeNameStr = ContextServiceUtils.getNodeNameStrByProperties(properties);
        try {

            String key = saveKey;
            String value = msgBody;
            ContextKey contextKey = new CommonContextKey();
            contextKey.setContextScope(ContextScope.PUBLIC);
            contextKey.setContextType(ContextType.OBJECT);
            contextKey.setKey(CSCommonUtils.getVariableKey(nodeNameStr, key));
            LinkisVariable varValue = new LinkisVariable();
            varValue.setKey(key);
            varValue.setValue(value);
            CSVariableService.getInstance().putVariable(contextIDStr, SerializeHelper.serializeContextKey(contextKey), varValue);
        } catch (Exception e) {
            LOGGER.error("Failed to put variable to cs", e);
        }
    }
}
