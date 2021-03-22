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

package com.webank.wedatasphere.dss.workflow.cs.service;

import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.workflow.cs.DSSCSHelper;
import com.webank.wedatasphere.linkis.cs.client.utils.SerializeHelper;
import com.webank.wedatasphere.linkis.cs.common.entity.metadata.CSColumn;
import com.webank.wedatasphere.linkis.cs.common.entity.metadata.CSTable;
import com.webank.wedatasphere.linkis.cs.common.entity.source.ContextKeyValue;
import com.webank.wedatasphere.linkis.cs.common.entity.source.ContextValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author peacewong
 * @date 2020/3/9 17:15
 */
@Service
public class CSTableServiceImpl implements CSTableService {

    private final static Logger logger = LoggerFactory.getLogger(CSTableServiceImpl.class);


    @Override
    public List<Map<String, Object>> queryTables(String dbName, String contextIDStr, String nodeName) throws DSSErrorException {
        List<ContextKeyValue> contextKeyValueList = DSSCSHelper.getTableContextKeyValueList(contextIDStr, nodeName);
        List<Map<String, Object>> tables = new ArrayList<>();
        if (null == contextKeyValueList || contextKeyValueList.isEmpty()) {
            return tables;
        }
        for(ContextKeyValue contextKeyValue : contextKeyValueList){
            Map<String, Object> tableNode = new HashMap<>();
            ContextValue contextValue = contextKeyValue.getContextValue();
            if (null != contextKeyValue && null != contextValue.getValue()) {
                try {
                    CSTable table = (CSTable) (contextValue.getValue()) ;
                    tableNode.put("tableName", table.getName());
                    tableNode.put("isView", table.isView());
                    tableNode.put("databaseName", dbName);
                    tableNode.put("createdBy", table.getCreator());
                    tableNode.put("createdAt", table.getCreateTime());
                    //contextKey 需要序列化
                    tableNode.put("contextKey", SerializeHelper.serializeContextKey(contextKeyValue.getContextKey()));
                    tables.add(tableNode);
                } catch (Exception e) {
                    logger.error("Failed to get table ", e);
                }
            }

        }
        return tables;
    }

    @Override
    public List<Map<String, Object>>  queryTableMeta(String dbName, String contextIDStr, String contextKeyStr) throws DSSErrorException {
        CSTable csTable = DSSCSHelper.getCSTable(contextIDStr, contextKeyStr);
        CSColumn[] columns = csTable.getColumns();
        List<Map<String, Object>> responseTables = new ArrayList<>();
        for (CSColumn column : columns) {
            Map<String, Object> columnNode = new HashMap<>();
            columnNode.put("columnName", column.getName());
            columnNode.put("columnType", column.getType());
            columnNode.put("columnComment", column.getComment());
            columnNode.put("partitioned", false);
            responseTables.add(columnNode);
        }
        return responseTables;
    }

}
