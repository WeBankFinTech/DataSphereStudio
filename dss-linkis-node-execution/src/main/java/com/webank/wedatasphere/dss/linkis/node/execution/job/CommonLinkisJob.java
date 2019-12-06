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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by peacewong on 2019/11/2.
 */
public abstract class CommonLinkisJob extends LinkisJob {

    public  abstract List<BMLResource> getJobResourceList();

    public  abstract void setJobResourceList(List<BMLResource> jobResourceList);

    public  abstract List<BMLResource> getProjectResourceList();

    public  abstract void setProjectResourceList(List<BMLResource> projectResourceList);

    public abstract Map<String,  List<BMLResource>> getFlowNameAndResources();

    public abstract void setFlowNameAndResources(Map<String, List<BMLResource>> fLowNameAndResources);
}
