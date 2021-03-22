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

package com.webank.wedatasphere.dss.appconn.eventchecker.cs;

import com.google.gson.Gson;
import com.webank.wedatasphere.linkis.cs.client.service.CSVariableService;
import com.webank.wedatasphere.linkis.cs.client.utils.ContextServiceUtils;
import com.webank.wedatasphere.linkis.cs.client.utils.SerializeHelper;
import com.webank.wedatasphere.linkis.cs.common.entity.enumeration.ContextScope;
import com.webank.wedatasphere.linkis.cs.common.entity.enumeration.ContextType;
import com.webank.wedatasphere.linkis.cs.common.entity.object.LinkisVariable;
import com.webank.wedatasphere.linkis.cs.common.entity.source.CommonContextKey;
import com.webank.wedatasphere.linkis.cs.common.entity.source.ContextKey;
import com.webank.wedatasphere.linkis.cs.common.utils.CSCommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * @author peacewong
 * @date 2020/3/7 22:36
 */
public class CSEventReceiverHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(CSEventReceiverHelper.class);

    private static Gson gson = new Gson();

    //update by peaceWong
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
       /* if (null != msgMap) {
            Iterator<Map.Entry<String, Object>> iterator = msgMap.entrySet().iterator();
            while (iterator.hasNext()) {
                try {
                    Map.Entry<String, Object> next = iterator.next();
                    String key = next.getKey();
                    Object value = next.getKey();
                    ContextKey contextKey = new CommonContextKey();
                    contextKey.setContextScope(ContextScope.PUBLIC);
                    contextKey.setContextType(ContextType.OBJECT);
                    contextKey.setKey(CSCommonUtils.getVariableKey(nodeNameStr, key));
                    LinkisVariable varValue = new LinkisVariable();
                    varValue.setKey(key);
                    varValue.setValue(value.toString());

                    CSVariableService.getInstance().putVariable(contextIDStr, nodeNameStr, varValue);
                } catch (Exception e) {
                    LOGGER.error("Failed to put variable to cs", e);
                }
            }
        }*/

    }
}
