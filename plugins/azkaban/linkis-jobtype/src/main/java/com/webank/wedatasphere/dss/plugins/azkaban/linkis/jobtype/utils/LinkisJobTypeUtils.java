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

package com.webank.wedatasphere.dss.plugins.azkaban.linkis.jobtype.utils;

import com.webank.wedatasphere.dss.linkis.node.execution.WorkflowContext;
import com.webank.wedatasphere.dss.linkis.node.execution.conf.LinkisJobExecutionConfiguration;
import com.webank.wedatasphere.dss.plugins.azkaban.linkis.jobtype.conf.LinkisJobTypeConf;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by peacewong on 2019/11/3.
 */
public class LinkisJobTypeUtils {

    public static boolean isSignalNode(String jobType){
        String[] nodes = LinkisJobTypeConf.SIGNAL_NODES.getValue().split(",");
        for(String node : nodes){
            if (node.equalsIgnoreCase(jobType)) {
                return true;
            }
        }
        return false;
    }

}
