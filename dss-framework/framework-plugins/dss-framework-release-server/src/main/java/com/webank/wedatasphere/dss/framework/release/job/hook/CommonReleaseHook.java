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
import org.springframework.stereotype.Component;

/**
 * created by cooperyang on 2020/12/24
 * Description:
 */
@Component
public class CommonReleaseHook implements ReleaseHook{

    @Override
    public void preRelease(AbstractReleaseJob releaseJob) {

    }

    @Override
    public void postRelease(AbstractReleaseJob releaseJob) {

    }
}
