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

package com.webank.wedatasphere.dss.appconn.datachecker;


import com.webank.wedatasphere.dss.appconn.datachecker.connector.DataCheckerDao;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.RefExecutionAction;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.RefExecutionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class DataChecker {
    public final static String SOURCE_TYPE = "source.type";
    public final static String DATA_OBJECT = "check.object";
    public final static String WAIT_TIME = "max.check.hours";
    public final static String QUERY_FREQUENCY = "query.frequency";
    public final static String TIME_SCAPE = "time.scape";
    public final static String MASK_URL = "bdp.mask.url";
    public final static String MASK_APP_ID = "bdp.mask.app.id";
    public final static String MASK_APP_TOKEN = "bdp.mask.app.token";

    private Properties p;
    private static final Logger logger = LoggerFactory.getLogger(DataChecker.class);;
    DataCheckerDao wbDao = DataCheckerDao.getInstance();
    DataCheckerExecutionAction dataCheckerAction = null;
    public long maxWaitTime;
    public int queryFrequency;

    public DataChecker(Properties p, DataCheckerExecutionAction action) {
        this.p = p;
        dataCheckerAction = action;
        maxWaitTime = Long.valueOf(p.getProperty(DataChecker.WAIT_TIME, "1")) * 3600 * 1000;
        //test over time
//        maxWaitTime = Long.valueOf(p.getProperty(DataChecker.WAIT_TIME, "1")) * 120 * 1000;
        queryFrequency = Integer.valueOf(p.getProperty(DataChecker.QUERY_FREQUENCY, "30000"));

    }

    public void run() {
        dataCheckerAction.setState(RefExecutionState.Running);
        try {
            if(p == null) {
                throw new RuntimeException("Properties is null. Can't continue");
            }
            if (!p.containsKey(SOURCE_TYPE)) {
                logger.info("Properties "  + SOURCE_TYPE + " value is Null !");
            }
            if (!p.containsKey(DATA_OBJECT)) {
                logger.info("Properties " + DATA_OBJECT + " value is Null !");
            }
        }catch (Exception ex){
            dataCheckerAction.setState(RefExecutionState.Failed);
            throw new  RuntimeException("get DataChecker result failed", ex);
        }

    }

    public void begineCheck(RefExecutionAction action){
        boolean success=false;
        try {
            success= wbDao.validateTableStatusFunction(p, logger,action);
        }catch (Exception ex){
            dataCheckerAction.setState(RefExecutionState.Failed);
            logger.error("datacheck error",ex);
            throw new  RuntimeException("get DataChecker result failed", ex);
        }
        if(success) {
            dataCheckerAction.setState(RefExecutionState.Success);
        }else {
            dataCheckerAction.setState(RefExecutionState.Running);
        }
    }

    public void cancel() {
    }

}