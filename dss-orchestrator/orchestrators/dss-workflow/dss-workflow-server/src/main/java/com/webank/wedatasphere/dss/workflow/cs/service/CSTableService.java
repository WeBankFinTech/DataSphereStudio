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

import java.util.List;
import java.util.Map;

/**
 * @author peacewong
 * @date 2020/3/9 16:27
 */
public interface CSTableService {

    List<Map<String, Object>> queryTables(String dbName, String contextIDStr, String nodeName) throws DSSErrorException;

    List<Map<String, Object>>  queryTableMeta(String dbName, String contextIDStr, String contextKeyStr) throws DSSErrorException;

}
