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
import com.webank.wedatasphere.dss.standard.app.development.stage.GetRefStage;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;

/**实现三种类型的Stage,project,flow,单任务
 * created by cooperyang on 2020/11/18
 * Description:
 */
public abstract class AbstractPublishToSchedulerStage implements PublishToSchedulerStage {





   protected abstract GetRefStage createGetRefStage();



}
