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

package com.webank.wedatasphere.dss.plugins.airflow.linkis.client.log;

import com.webank.wedatasphere.dss.linkis.node.execution.log.LinkisJobExecutionLog;
import org.apache.log4j.Logger;

/**
 * Created by peacewong on 2019/11/3.
 */
public class AirflowAppjointLog extends LinkisJobExecutionLog {

    private  Logger log;

    public AirflowAppjointLog(Logger log){
        this.log = log;
    }



    @Override
    public void info(Object message, Throwable t) {
        log.info(message, t);
    }

    @Override
    public void warn(Object message, Throwable t) {
        log.warn(message, t);
    }

    @Override
    public void error(Object message, Throwable t) {
        log.error(message, t);
    }
}
