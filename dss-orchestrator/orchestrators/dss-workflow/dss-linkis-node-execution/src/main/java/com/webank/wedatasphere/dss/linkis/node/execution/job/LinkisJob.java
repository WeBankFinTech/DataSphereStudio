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


import java.util.Map;

/**
 * Created by johnnwang on 2019/10/31.
 */
public abstract class LinkisJob implements Job{


    public abstract Map<String, String> getSource();

    public abstract void setSource(Map<String, String> source);

    public abstract JobTypeEnum  getJobType();
    public abstract void setJobType(JobTypeEnum jobType);

    public abstract String getSubmitUser();

    public abstract  Map<String, Object>  getVariables();
    public abstract void setVariables(Map<String, Object> variables);


    public abstract  Map<String, Object>  getConfiguration();
    public abstract void setConfiguration(Map<String, Object> configuration);


}
