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

package com.webank.wedatasphere.dss.apiservice.core.execute;

import com.webank.wedatasphere.linkis.ujes.client.response.JobExecuteResult;

import java.util.Map;

/**
 * @author allenlliu
 * @version 2.0.0
 * @date 2020/08/12 09:01 PM
 */
public interface ApiServiceExecuteJob {
        String getCode();

        void setCode(String code);

        String getEngineType();

        void setEngineType(String engineType);

        String getRunType();

        void setRunType(String runType);

        String getUser();

        void  setUser(String user);

        String getJobName();

        Map<String, Object> getParams();

        void setParams(Map<String, Object> params);

        Map<String,Object> getRuntimeParams();
        void setRuntimeParams(Map<String, Object> runtimeParams);

        JobExecuteResult getJobExecuteResult();

        void setJobExecuteResult(JobExecuteResult jobExecuteResult);

        Map<String, String>  getJobProps();

        void setJobProps(Map<String, String> jobProps);

        String getScriptPath();
        void setScriptePath(String path);
}
