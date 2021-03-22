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

package com.webank.wedatasphere.dss.appconn.schedule.core.hooks;

import com.webank.wedatasphere.dss.appconn.schedule.core.entity.AbstractSchedulerProject;
import com.webank.wedatasphere.dss.appconn.schedule.core.entity.SchedulerFlow;
import com.webank.wedatasphere.dss.appconn.schedule.core.entity.SchedulerProject;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by v_wbjftang on 2019/9/25.
 */
public abstract class AbstractProjectPublishHook implements ProjectPublishHook {

    @Override
    public FlowPublishHook[] getFlowPublishHooks() {
        return flowPublishHooks;
    }

    @Override
    public void setFlowPublishHooks(FlowPublishHook[] flowPublishHooks) {
        this.flowPublishHooks = flowPublishHooks;
    }

    private FlowPublishHook[] flowPublishHooks;

    @Override
    public void prePublish(SchedulerProject project) throws DSSErrorException {
        //这里强转也是因为SchedulerProject ，
        //如果azkaban有实现，应该要继承AbstractSchedulerProject 而不是直接继承SchedulerProject
        //调用flowHook的prePublish
        AbstractSchedulerProject schedulerProject = (AbstractSchedulerProject) project;
        List<SchedulerFlow> schedulerFlows = schedulerProject.getSchedulerFlows();
        schedulerFlows.forEach(schedulerFlow -> schedulerFlow.setDwsProject(schedulerProject.getDSSProject()));
        Arrays.stream(getFlowPublishHooks()).forEach(h -> invokePreFlowPublishHook(schedulerFlows,h));
    }

    @Override
    public void postPublish(SchedulerProject project) throws DSSErrorException {
        //// TODO: 2019/9/25 递归调用flowHooks  的post

    }

    private void invokePreFlowPublishHook(List<SchedulerFlow> schedulerFlows,FlowPublishHook flowPublishHook){
        schedulerFlows.forEach(DSSExceptionUtils.handling(flowPublishHook::prePublish));
    }

    private void invokePostFlowPublishHook(List<SchedulerFlow> schedulerFlows,FlowPublishHook flowPublishHook){
        schedulerFlows.forEach(DSSExceptionUtils.handling(flowPublishHook::postPublish));
    }
}
