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

package com.webank.wedatasphere.dss.appconn.eventchecker.service;

import com.webank.wedatasphere.dss.appconn.eventchecker.entity.EventChecker;
import org.slf4j.Logger;

import java.util.Properties;

public class EventCheckerService {
    private static volatile EventCheckerService instance;

    public static EventCheckerService getInstance() {
        if (instance == null) {
            synchronized (EventCheckerService.class) {
                if (instance == null) {
                    instance = new EventCheckerService();
                }
            }
        }
        return instance;
    }

    public boolean sendMsg(int jobId, Properties props, Logger log) {
        if (props != null) {
            String channelType = props.containsKey(EventChecker.CHANNEL_TYPE) ? props.getProperty(EventChecker.CHANNEL_TYPE)
                    : "DSS";
            AbstractEventCheck eventCheck;
            if("DSS".equalsIgnoreCase(channelType)) {
                eventCheck = new EventCheckSender(props);
                log.info("this is dss send task");
            }else {
                eventCheck = new HttpEventCheckSender(props);
                log.info("this is kgas receive task");
            }
            return eventCheck.sendMsg(jobId, props, log);
        } else {
            log.error("create EventCheckSender failed {}");
            return false;
        }
    }

    /**
     * Receiving a message first queries the consumption record,
     * and then starts to consume after the last consumption, and no consumption
     * starts after the job starts. The received message is performed in an active
     * query manner, and the target message is repeatedly queried within a time period
     * when the set target is not exceeded.
     */
    public boolean reciveMsg(int jobId, Properties props, Logger log) {
        if (props != null) {
            String channelType = props.containsKey(EventChecker.CHANNEL_TYPE) ? props.getProperty(EventChecker.CHANNEL_TYPE)
                    : "DSS";
            AbstractEventCheck eventCheck;
            if("DSS".equalsIgnoreCase(channelType)){
                eventCheck= new DefaultEventcheckReceiver(props);
                log.info("this is dss receive task");
            }else{
                eventCheck = new HttpEventcheckerReceiver(props);
                log.info("this is kgas receive task");
            }
            return eventCheck.reciveMsg(jobId, props, log);
        } else {
            log.error("create EventCheckSender failed {}");
            return false;
        }
    }

}
