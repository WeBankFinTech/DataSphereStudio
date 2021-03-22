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

package com.webank.wedatasphere.dss.appconn.schedulis.parser;

import com.webank.wedatasphere.dss.appconn.schedulis.entity.AzkabanSchedulerProject;
import com.webank.wedatasphere.dss.appconn.schedule.core.entity.AbstractSchedulerProject;
import com.webank.wedatasphere.dss.appconn.schedule.core.parser.AbstractProjectParser;
import com.webank.wedatasphere.dss.appconn.schedule.core.parser.FlowParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by allenlliu on 2019/9/16.
 */

public class AzkabanProjectParser extends AbstractProjectParser {



    public AzkabanProjectParser(){
        FlowParser[] flowParsers = {new AzkabanFlowParser()};
        setFlowParsers(flowParsers);
    }

    private static final Logger logger = LoggerFactory.getLogger(AzkabanProjectParser.class);

    @Override
    protected AbstractSchedulerProject createSchedulerProject() {
        return new AzkabanSchedulerProject();
    }

//    @Override
//    public SchedulerProject parseProject(DSSProject dssProject, List<DSSFlow> dssFlowList) {
//        AzkabanSchedulerProject schedulerProject = new AzkabanSchedulerProject();
//        schedulerProject.setDSSProject(dssProject);
//        schedulerProject.setCreateBy(dssProject.getCreateBy());
//        schedulerProject.setStorePath(generateStorePath(dssProject));
//
//        schedulerProject.setSchedulerFlows(dssFlowList);
//        return schedulerProject;
//    }









    @Override
    public void setFlowParsers(FlowParser[] flowParsers) {
        super.setFlowParsers(flowParsers);
    }

}
