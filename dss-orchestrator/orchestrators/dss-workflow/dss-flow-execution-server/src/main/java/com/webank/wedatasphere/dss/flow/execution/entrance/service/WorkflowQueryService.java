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

package com.webank.wedatasphere.dss.flow.execution.entrance.service;

import com.webank.wedatasphere.dss.flow.execution.entrance.entity.WorkflowQueryTask;
import com.webank.wedatasphere.dss.flow.execution.entrance.entity.WorkflowQueryTaskVO;
import com.webank.wedatasphere.linkis.governance.common.entity.task.*;


import java.util.Date;
import java.util.List;

/**
 * Created by johnnwang on 2019/2/25.
 */
public interface WorkflowQueryService {

    ResponsePersist add(RequestInsertTask requestInsertTask);

    ResponsePersist change(RequestUpdateTask requestUpdateTask);

    ResponsePersist query(RequestQueryTask requestQueryTask);

    WorkflowQueryTaskVO getTaskByID(Long taskID, String userName);

    List<WorkflowQueryTask> search(Long taskID, String username, String status, Date sDate, Date eDate, String executeApplicationName);

    RequestPersistTask searchOne(String instance, String execId, Date sDate, Date eDate, String executeApplicationName);

    List<WorkflowQueryTaskVO> getQueryVOList(List<WorkflowQueryTask> list);
}
