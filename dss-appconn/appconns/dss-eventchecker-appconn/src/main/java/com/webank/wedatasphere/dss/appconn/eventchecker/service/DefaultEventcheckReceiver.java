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



import com.webank.wedatasphere.dss.appconn.eventchecker.execution.EventCheckerExecutionAction;
import com.webank.wedatasphere.dss.appconn.eventchecker.utils.Utils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class DefaultEventcheckReceiver extends AbstractEventCheckReceiver {

    EventCheckerExecutionAction backAction;
    String todayStartTime;
    String todayEndTime;
    String allStartTime;
    String allEndTime;
    String nowStartTime;

    public DefaultEventcheckReceiver(Properties props, EventCheckerExecutionAction backAction) {
        this.backAction = backAction;
        initECParams(props);
        initReceiverTimes();
    }

    private void initReceiverTimes(){
        todayStartTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd 00:00:00");
        todayEndTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd 23:59:59");
        allStartTime = DateFormatUtils.format(new Date(), "10000-01-01  00:00:00");
        allEndTime = DateFormatUtils.format(new Date(), "9999-12-31  23:59:59");
        nowStartTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
    }

    @Override
    public boolean reciveMsg(int jobId, Properties props, Logger log) {
        boolean result = false;
        try{
            String lastMsgId = getOffset(jobId,props,log,backAction);
            String[] executeType = createExecuteType(jobId,props,log,lastMsgId);
            if(executeType!=null && executeType.length ==3){
                String[] consumedMsgInfo = getMsg(props, log,backAction,executeType);
                if(consumedMsgInfo!=null && consumedMsgInfo.length == 4){
                    result = updateMsgOffset(jobId,props,log,consumedMsgInfo,lastMsgId,backAction);
                }
            }else{
                log.error("executeType error {} " + executeType.toString());
                return result;
            }
        }catch (Exception e){
            Utils.log(backAction,e);
            log.error("EventChecker failed to receive the message {}" + e);
            return result;
        }
        return result;
    }

    private String[] createExecuteType(int jobId, Properties props, Logger log,String lastMsgId){
        boolean receiveTodayFlag = (null != receiveToday && "true".equals(receiveToday.trim().toLowerCase()));
        boolean afterSendFlag = (null != afterSend && "true".equals(afterSend.trim().toLowerCase()));
        String[] executeType = null;
        try {
            if ("0".equals(lastMsgId)){
                if(receiveTodayFlag){
                    if(afterSendFlag){
                        executeType = new String[]{nowStartTime,todayEndTime,"0"};
                    }else{
                        executeType = new String[]{todayStartTime,todayEndTime,"0"};
                    }
                }else{
                    if(afterSendFlag){
                        executeType = new String[]{nowStartTime,allEndTime,"0"};
                    }else{
                        executeType = new String[]{allStartTime,allEndTime,"0"};
                    }
                }
            }else{
                if(receiveTodayFlag){
                    if(afterSendFlag){
                        executeType = new String[]{nowStartTime,todayEndTime,lastMsgId};
                    }else{
                        executeType = new String[]{todayStartTime,todayEndTime,lastMsgId};
                    }
                }else{
                    if(afterSendFlag){
                        executeType = new String[]{nowStartTime,allEndTime,lastMsgId};
                    }else{
                        executeType = new String[]{allStartTime,allEndTime,lastMsgId};
                    }
                }
            }
        }catch(Exception e){
            Utils.log(backAction,e);
            log.error("create executeType failed {}" + e);
        }
        return executeType;
    }

    private void waitForTime(Logger log,Long waitTime){
        String waitForTime = wait_for_time;
        String formatWaitForTime = DateFormatUtils.format(new Date(),"yyyy-MM-dd " + waitForTime + ":00");
        DateFormat fmt =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date targetWaitTime = null;
        try {
            targetWaitTime = fmt.parse(formatWaitForTime);
        } catch (ParseException e) {
            Utils.log(backAction,e);
            log.error("parse date failed {}" + e);
        }

        log.info("It will success at a specified time: " + targetWaitTime);
        long wt = targetWaitTime.getTime() - System.currentTimeMillis();
        if(wt > 0){
            //wt must less than wait.time
            if(wt <= waitTime){
                log.info("EventChecker will wait "+ wt + " milliseconds before starting execution");
                try {
                    Thread.sleep(wt);
                } catch (InterruptedException e) {
                    Utils.log(backAction,e);
                    throw new RuntimeException("EventChecker throws an exception during the waiting time {}"+e);
                }
            }else{
                throw new RuntimeException("The waiting time from Job starttime to wait.for.time"+ wt +"(ms) greater than wait.time , unreasonable setting！");
            }
        }else{
            log.info("EventChecker has reached the specified time");
        }
    }

}
