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

package com.webank.wedatasphere.dss.linkis.node.execution.job;

import com.webank.wedatasphere.dss.linkis.node.execution.log.LinkisJobExecutionLog;
import com.webank.wedatasphere.linkis.ujes.client.response.JobExecuteResult;

import java.util.Map;

/**
 * Created by johnnwang on 2019/10/15.
 */
public interface Job {

     String getCode();

     void setCode(String code);

     String getEngineType();

    void setEngineType(String engineType);

     String getRunType();

    void setRunType(String runType);

    String getUser();

     String getJobName();

     Map<String, Object> getParams();

    void setParams(Map<String, Object> params);

    Map<String,Object> getRuntimeParams();
    void setRuntimeParams(Map<String, Object> runtimeParams);

    JobExecuteResult getJobExecuteResult();

    void setJobExecuteResult(JobExecuteResult jobExecuteResult);

    Map<String, String>  getJobProps();

    void setJobProps(Map<String, String> jobProps);

    LinkisJobExecutionLog getLogObj();
    void setLogObj(LinkisJobExecutionLog logObj);

    int getLogFromLine();

    void setLogFromLine(int index);

}
