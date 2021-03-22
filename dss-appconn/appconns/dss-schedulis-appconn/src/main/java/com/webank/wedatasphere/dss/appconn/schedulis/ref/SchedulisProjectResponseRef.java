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

package com.webank.wedatasphere.dss.appconn.schedulis.ref;

import com.webank.wedatasphere.dss.appconn.schedulis.conf.SchedulisConf;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.entity.ref.AbstractResponseRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * created by cooperyang on 2020/11/16
 * Description:
 */
public class SchedulisProjectResponseRef extends AbstractResponseRef implements ProjectResponseRef {


    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulisProjectResponseRef.class);

    private String errorMsg;

    private Long projectRefId;

    public SchedulisProjectResponseRef(){
        super("", 0);
    }


    public SchedulisProjectResponseRef(String responseBody, int status, String errorMsg) {
        super(responseBody, status);
        this.errorMsg = errorMsg;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> toMap() {
        try{
           return SchedulisConf.gson().fromJson(this.getResponseBody(), Map.class);
        }catch(Exception e){
            LOGGER.error("failed to covert {} to a map", this.getResponseBody(), e);
            return new HashMap<String, Object>();
        }
    }

    @Override
    public String getErrorMsg() {
        return this.errorMsg;
    }

    public void setErrorMsg(String errorMsg){
        this.errorMsg = errorMsg;
    }


    @Override
    public Long getProjectRefId() {
        return this.projectRefId;
    }

    @Override
    public Map<AppInstance, Long> getProjectRefIds() {
        return null;
    }

    public void setProjectRefId(Long projectRefId){
        this.projectRefId = projectRefId;
    }

}
