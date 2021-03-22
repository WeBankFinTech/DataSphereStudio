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


import com.webank.wedatasphere.dss.appconn.schedulis.entity.AzkabanSchedulerFlow;
import com.webank.wedatasphere.dss.appconn.schedule.core.entity.SchedulerFlow;
import com.webank.wedatasphere.dss.appconn.schedule.core.parser.AbstractFlowParser;
import com.webank.wedatasphere.dss.appconn.schedule.core.parser.NodeParser;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSJsonFlow;

import java.util.ArrayList;


public class AzkabanFlowParser extends AbstractFlowParser {

    public AzkabanFlowParser(){
        ArrayList<NodeParser> list = new ArrayList<>();
        list.add(new LinkisAzkabanNodeParser());
        list.add(new LinkisAzkabanSendEmailNodeParser());
        NodeParser[] nodeParsers =new NodeParser[list.size()];
        list.toArray(nodeParsers);
        setNodeParsers(nodeParsers);
    }

    @Override
    protected SchedulerFlow createSchedulerFlow() {
        return new AzkabanSchedulerFlow();
    }

    @Override
    public void setNodeParsers(NodeParser[] nodeParsers) {
        super.setNodeParsers(nodeParsers);
    }

    @Override
    public Boolean ifFlowCanParse(DSSJsonFlow flow) {
        return true;
    }


}
