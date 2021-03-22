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

package com.webank.wedatasphere.dss.framework.release.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created by cooperyang on 2020/12/9
 * Description: 批量发布的任务
 */
public class BatchReleaseReleaseJob extends AbstractReleaseJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchReleaseReleaseJob.class);



    @Override
    public void run() {

    }


    @Override
    public void close() {

    }

    @Override
    boolean supportMultiEnv() {
        return true;
    }
}
