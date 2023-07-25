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

package com.webank.wedatasphere.dss.flow.execution.entrance.dao;

import com.webank.wedatasphere.dss.flow.execution.entrance.entity.WorkflowQueryTask;
import org.apache.ibatis.annotations.Param;
import java.util.Date;
import java.util.List;


public interface TaskMapper {

    List<WorkflowQueryTask> selectTask(WorkflowQueryTask queryTask);

    void insertTask(WorkflowQueryTask queryTask);

    void updateTask(WorkflowQueryTask queryTask);

    void batchUpdateTasks(@Param("failedJobs") List<WorkflowQueryTask> failedJobs);

    List<WorkflowQueryTask> search(@Param("taskID") Long taskID, @Param("umUser") String username, @Param("status") List<String> status,
                           @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("executeApplicationName") String executeApplicationName,
                           @Param("instance") String instance, @Param("execId") String execId);

    String selectTaskStatusForUpdate(Long taskID);

}
