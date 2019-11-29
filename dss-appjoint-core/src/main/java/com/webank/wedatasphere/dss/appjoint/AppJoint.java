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

package com.webank.wedatasphere.dss.appjoint;

import com.webank.wedatasphere.dss.appjoint.exception.AppJointErrorException;
import com.webank.wedatasphere.dss.appjoint.execution.NodeExecution;
import com.webank.wedatasphere.dss.appjoint.service.NodeService;
import com.webank.wedatasphere.dss.appjoint.service.ProjectService;
import com.webank.wedatasphere.dss.appjoint.service.SecurityService;

import java.util.Map;

/**
 * Created by enjoyyin on 2019/10/10.
 */
public interface AppJoint {

    /**
     * 每一个AppJoint都需要有一个自己的标识名<br/>
     * 这个标识名是唯一的，如qualitis子系统，可以将名字设置为qualitis; visualis子系统，可以设置为visualis
     */
    String getAppJointName();

    void init(String baseUrl, Map<String, Object> params) throws AppJointErrorException;

    /**
     * 如果不存在，直接返回null即可
     * @return
     */
    default SecurityService getSecurityService() {
        return null;
    }

    /**
     * 如果不存在，直接返回null即可
     * @return
     */
    default ProjectService getProjectService() {
        return null;
    }

    /**
     * 如果不存在，直接返回null即可
     * @return
     */
    default NodeService getNodeService() {
        return null;
    }

    /**
     * 如果不存在，直接返回null即可
     * @return
     */
    default NodeExecution getNodeExecution() {
        return null;
    }

}
