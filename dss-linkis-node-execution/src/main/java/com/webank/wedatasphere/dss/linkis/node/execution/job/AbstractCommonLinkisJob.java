/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package com.webank.wedatasphere.dss.linkis.node.execution.job;

import com.webank.wedatasphere.dss.linkis.node.execution.entity.BMLResource;
import com.webank.wedatasphere.dss.linkis.node.execution.log.LinkisJobExecutionLog;
import com.webank.wedatasphere.linkis.ujes.client.response.JobExecuteResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by peacewong on 2019/11/3.
 */
public abstract class AbstractCommonLinkisJob extends CommonLinkisJob {

    private Map<String, String> jobProps;



    private Map<String, String> source;

    private JobTypeEnum jobType;

    private Map<String, Object> variables;

    private Map<String, Object> configuration;

    private String code;

    private String engineType;

    private String runType;



    private Map<String, Object> params;

    private Map<String, Object> runtimeParams;

    private JobExecuteResult jobExecuteResult;

    private LinkisJobExecutionLog logObj;

    private int logFromLine;


    private List<BMLResource> jobResourceList;

    private List<BMLResource> projectResourceList;

    private Map<String, List<BMLResource>> fLowNameAndResources;




    @Override
    public Map<String, String> getSource() {
        return this.source;
    }

    @Override
    public JobTypeEnum getJobType() {
        return this.jobType;
    }


    @Override
    public Map<String, Object> getVariables() {
        return this.variables;
    }

    @Override
    public Map<String, Object> getConfiguration() {
        return this.configuration;
    }

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
        return this.engineType;
    }

    @Override
    public String getRunType() {
        return this.runType;
    }



    @Override
    public Map<String, Object> getParams() {
        return this.params;
    }

    @Override
    public Map<String, Object> getRuntimeParams() {
        return this.runtimeParams;
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
        return this.jobProps;
    }

    @Override
    public void setJobProps(Map<String, String> jobProps) {
        this.jobProps = jobProps;
    }

    @Override
    public LinkisJobExecutionLog getLogObj() {
        return this.logObj;
    }

    @Override
    public int getLogFromLine() {
        return this.logFromLine;
    }

    @Override
    public void setLogFromLint(int index) {
        this.logFromLine = index;
    }


    @Override
    public void setSource(Map<String, String> source) {
        this.source = source;
    }

    @Override
    public void setJobType(JobTypeEnum jobType) {
        this.jobType = jobType;
    }

    @Override
    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    @Override
    public void setConfiguration(Map<String, Object> configuration) {
        this.configuration = configuration;
    }

    @Override
    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }

    @Override
    public void setRunType(String runType) {
        this.runType = runType;
    }

    @Override
    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    @Override
    public void setRuntimeParams(Map<String, Object> runtimeParams) {
        this.runtimeParams = runtimeParams;
    }

    @Override
    public void setLogObj(LinkisJobExecutionLog logObj) {
        this.logObj = logObj;
    }


    @Override
    public List<BMLResource> getJobResourceList() {
        return this.jobResourceList;
    }



    @Override
    public List<BMLResource> getProjectResourceList() {
        return this.projectResourceList;
    }

    @Override
    public Map<String, List<BMLResource>> getFlowNameAndResources() {
        return this.fLowNameAndResources;
    }

    @Override
    public void setJobResourceList(List<BMLResource> jobResourceList) {
        this.jobResourceList = jobResourceList;
    }

    @Override
    public void setProjectResourceList(List<BMLResource> projectResourceList) {
        this.projectResourceList = projectResourceList;
    }

    @Override
    public void setFlowNameAndResources(Map<String, List<BMLResource>> fLowNameAndResources) {
        this.fLowNameAndResources = fLowNameAndResources;
    }

}
