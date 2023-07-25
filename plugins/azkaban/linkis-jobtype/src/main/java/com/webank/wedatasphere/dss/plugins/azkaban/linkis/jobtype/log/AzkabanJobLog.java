/*
 * Copyright 2019 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

package com.webank.wedatasphere.dss.plugins.azkaban.linkis.jobtype.log;

import azkaban.jobExecutor.AbstractJob;
import com.webank.wedatasphere.dss.linkis.node.execution.log.LinkisJobExecutionLog;


public class AzkabanJobLog extends LinkisJobExecutionLog {

    private AbstractJob job;

    public AzkabanJobLog(AbstractJob job){
        this.job = job;
    }

    public void info(Object message) {
        job.info(message.toString());
    }

    public void warn(Object message) {
        job.warn(message.toString());
    }

    public void error(Object message) {
        job.error(message.toString());
    }

    @Override
    public void info(Object message, Throwable t) {
        job.info(message.toString(), t);
    }

    @Override
    public void warn(Object message, Throwable t) {
        job.warn(message.toString(), t);
    }

    @Override
    public void error(Object message, Throwable t) {
        job.error(message.toString(), t);
    }
}
