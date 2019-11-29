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

package com.webank.wedatasphere.dss.appjoint.scheduler.parser;

import com.webank.wedatasphere.dss.appjoint.scheduler.constant.SchedulerAppJointConstant;
import com.webank.wedatasphere.dss.common.entity.node.DWSNode;

import java.util.Map;

/**
 * Created by enjoyyin on 2019/10/31.
 */
public abstract class SendEmailNodeParser extends AbstractReadNodeParser {

    @Override
    public Boolean ifNodeCanParse(DWSNode dwsNode) {
        //判断是sendemail  并且category是node
        Map<String, Object> params = dwsNode.getParams();
        if(params != null && !params.isEmpty()){
            Object configuration = params.get(SchedulerAppJointConstant.CONFIGURATION);
            if(configuration instanceof Map){
                Object runtime = ((Map) configuration).get(SchedulerAppJointConstant.RUNTIME);
                if(runtime instanceof Map){
                    Object category = ((Map) runtime).get(SchedulerAppJointConstant.CATEGORY);
                    if(category != null && SchedulerAppJointConstant.NODE.equals(category.toString())){
                        return SchedulerAppJointConstant.SENDEMAIL_NODE_TYPE.equals(dwsNode.getNodeType());
                    }
                }
            }
        }
        return false;
    }
}
