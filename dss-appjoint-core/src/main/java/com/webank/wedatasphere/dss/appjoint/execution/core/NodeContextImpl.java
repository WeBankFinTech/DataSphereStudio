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

package com.webank.wedatasphere.dss.appjoint.execution.core;

import com.webank.wedatasphere.dss.appjoint.service.session.Session;

import java.util.Map;

/**
 * created by enjoyyin on 2019/10/12
 * Description:
 */
public abstract class NodeContextImpl implements NodeContext{


    private boolean isReadNode;

    private AppJointNode appJointNode;

    private Map<String, Object> runtimeMap;

    private long[] jobIdsOfShareNode;

    private String user;

    private Session session;


    public void setIsReadNode(boolean isReadNode){
        this.isReadNode = isReadNode;
    }

    public void setAppJointNode(AppJointNode appJointNode){
        this.appJointNode = appJointNode;
    }

    public void setRuntimeMap(Map<String, Object> runtimeMap){
        this.runtimeMap = runtimeMap;
    }

    public void setJobIdsOfShareNode(long[] jobIds){
        this.jobIdsOfShareNode = jobIds;
    }

    public void setUser(String user){
        this.user = user;
    }



    @Override
    public boolean isReadNode() {
        return this.isReadNode;
    }

    @Override
    public AppJointNode getAppJointNode() {
        return this.appJointNode;
    }

    @Override
    public Map<String, Object> getRuntimeMap() {
        return this.runtimeMap;
    }

    @Override
    public long[] getJobIdsOfShareNode() {
        return this.jobIdsOfShareNode;
    }

    @Override
    public String getUser() {
        return this.user;
    }




}
