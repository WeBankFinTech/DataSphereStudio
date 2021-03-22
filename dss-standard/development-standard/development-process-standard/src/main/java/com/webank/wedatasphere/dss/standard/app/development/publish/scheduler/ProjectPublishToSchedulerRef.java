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

package com.webank.wedatasphere.dss.standard.app.development.publish.scheduler;

import com.webank.wedatasphere.dss.common.entity.PublishType;
import com.webank.wedatasphere.dss.standard.common.entity.project.Project;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;

import java.util.List;

/**
 * created by cooperyang on 2020/11/18
 * Description:
 */
public  interface ProjectPublishToSchedulerRef extends PublishToSchedulerRef {

    void setOrcIds(List<Long> orcIds);

    List<Long> getOrcIds();

    void setOrcAppIds(List<Long> orcAppIds);

    List<Long> getOrcAppIds();

    Project  getProject();

    void setProject(Project project);

    List<DSSFlow> getDSSFlows();

    void setDSSFlows(List<DSSFlow> dssFlows);

    PublishType getPublishType();

    void setPublishType(PublishType publishType);

}
