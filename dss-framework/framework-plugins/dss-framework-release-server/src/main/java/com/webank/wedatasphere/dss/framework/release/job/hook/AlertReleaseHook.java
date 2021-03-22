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

package com.webank.wedatasphere.dss.framework.release.job.hook;

import com.webank.wedatasphere.dss.framework.release.job.AbstractReleaseJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * created by cooperyang on 2021/1/5
 * Description: 可以借助ims告警方式将我们发布之前和发布之后进行告警，然用户更加了解和明确
 */
@Component
public class AlertReleaseHook implements ReleaseHook{

    private static final Logger LOGGER = LoggerFactory.getLogger(AlertReleaseHook.class);

    @Override
    public void preRelease(AbstractReleaseJob releaseJob) {

    }

    @Override
    public void postRelease(AbstractReleaseJob releaseJob) {

    }
}
