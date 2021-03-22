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

import com.webank.wedatasphere.linkis.ujes.client.request.JobExecuteAction;
import com.webank.wedatasphere.linkis.ujes.client.response.JobExecuteResult;

import java.util.Map;

/**
 * @author allenlliu
 * @version 2.0.0
 * @date 2020/08/12 09:06 PM
 */
public class DefaultApiServiceJob implements ApiServiceExecuteJob {
    private Map<String, String> jobProps;
    private String user;
    private Map<String, String> source;
    private Map<String, Object> variables;

    private Map<String, Object> configuration;

    private String code;

    private String engineType;

    private String runType;

    private Map<String, Object> params;

    private Map<String, Object> runtimeParams;

    private JobExecuteResult jobExecuteResult;

    private  String scriptPath;

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public void setCode(String code) {
       this.code = code;
    }

    @Override
    public String getEngineType() {
        return engineType;
    }

    @Override
    public void setEngineType(String engineType) {
       this.engineType = engineType;
    }

    @Override
    public String getRunType() {
        return runType;
    }

    @Override
    public void setRunType(String runType) {
       this.runType = runType;
    }

    @Override
    public String getUser() {
        return user;
    }

    @Override
    public void setUser(String user){
        this.user =user;
    }

    @Override
    public String getJobName() {
        return null;
    }

    @Override
    public Map<String, Object> getParams() {
        return params;
    }

    @Override
    public void setParams(Map<String, Object> params) {
       this.params = params;
    }

    @Override
    public Map<String, Object> getRuntimeParams() {
        return runtimeParams;
    }

    @Override
    public void setRuntimeParams(Map<String, Object> runtimeParams) {
        this.runtimeParams = runtimeParams;
    }

    @Override
    public JobExecuteResult getJobExecuteResult() {
        return this.jobExecuteResult;
    }

    @Override
    public void setJobExecuteResult(JobExecuteResult jobExecuteResult) {
        this.jobExecuteResult = jobExecuteResult;
    }

    @Override
    public Map<String, String> getJobProps() {
        return jobProps;
    }

    @Override
    public void setJobProps(Map<String, String> jobProps) {
          this.jobProps = jobProps;
    }

    @Override
    public String getScriptPath() {
        return scriptPath;
    }

    @Override
    public void setScriptePath(String path) {
             this.scriptPath = path;
    }
}
