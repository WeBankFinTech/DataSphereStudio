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

package com.webank.wedatasphere.dss.workflow.cs;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.webank.wedatasphere.linkis.cs.client.service.CSTableService;
import com.webank.wedatasphere.linkis.cs.common.entity.metadata.CSTable;
import com.webank.wedatasphere.linkis.cs.common.entity.source.ContextKeyValue;
import com.webank.wedatasphere.linkis.cs.common.exception.CSErrorException;
import com.webank.wedatasphere.linkis.cs.common.utils.CSCommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author peacewong
 * @date 2020/3/9 16:27
 */
public class DSSCSHelper {

    private final static Logger logger = LoggerFactory.getLogger(DSSCSHelper.class);

    private final static Gson gson = new Gson();

    public static List<ContextKeyValue> getTableContextKeyValueList(String contextIDStr, String nodeName) {
        try {
            return CSTableService.getInstance().searchUpstreamTableKeyValue(contextIDStr, nodeName);
        } catch (CSErrorException e) {
            logger.error("Failed to get getTableContextKeyValueList: " + nodeName, e);
        }

        logger.info("upstream tmp table is null");
        return null;
    }


    public static CSTable getCSTable(String contextIDStr, String contextKeyStr) {


        try {
            return CSTableService.getInstance().getCSTable(contextIDStr, contextKeyStr);
        } catch (CSErrorException e) {
            logger.error("Failed to get CSTable contextkey: " + contextKeyStr, e);
        }
        logger.info("This contextKey{} has no csTable ", contextKeyStr);
        return null;
    }

    public static String getContextIDStrByJson(String flowJson) {

        JsonObject jsonObject = gson.fromJson(flowJson, JsonObject.class);
        if (!jsonObject.has(CSCommonUtils.CONTEXT_ID_STR) || null == jsonObject.get(CSCommonUtils.CONTEXT_ID_STR)){
            return null;
        }
        return jsonObject.get(CSCommonUtils.CONTEXT_ID_STR).getAsString();
    }
}
