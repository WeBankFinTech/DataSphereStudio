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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

public abstract class AbstractEventCheckReceiver extends AbstractEventCheck{
    String todayStartTime;
    String todayEndTime;
    String allStartTime;
    String allEndTime;
    String nowStartTime;

    public AbstractEventCheckReceiver(Properties props) {
        initECParams(props);
        initReceiverTimes();
    }

    void initReceiverTimes(){
        todayStartTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd 00:00:00");
        todayEndTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd 23:59:59");
        allStartTime = DateFormatUtils.format(new Date(), "10000-01-01  00:00:00");
        allEndTime = DateFormatUtils.format(new Date(), "9999-12-31  23:59:59");
        nowStartTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
    }
    /**
     * Fill the result into the source
     */
    String setConsumedMsg(Properties props, Logger log, String[] consumedMsgInfo){
        String vNewMsgID = "";
        try {
            if(consumedMsgInfo!=null && consumedMsgInfo.length == 4){
                vNewMsgID = consumedMsgInfo[0];
                String vMsgName = consumedMsgInfo[1];
                String vReceiver = consumedMsgInfo[2];
                String vMsg = consumedMsgInfo[3];
                if (null == vMsg) {
                    props.put(EventChecker.MSG, "NULL");
                } else {
                    props.put(EventChecker.MSG, vMsg);
                }
                log.info("Received message : messageID: " + vNewMsgID + ", messageName: " + vMsgName + ", receiver: " + vReceiver
                        + ", messageBody: " + vMsg);
            }
        }catch (Exception e) {
            log.error("Error set consumed message failed {} setConsumedMsg failed" + e);
            return vNewMsgID;
        }
        return vNewMsgID;
    }

    /**
     * Update consumption status
     */
    boolean updateMsgOffset(int jobId, Properties props, Logger log, String[] consumedMsgInfo,String lastMsgId){
        boolean result = false;
        String vNewMsgID = "-1";
        PreparedStatement updatePstmt = null;
        Connection msgConn = null;
        vNewMsgID = setConsumedMsg(props,log,consumedMsgInfo);
        try {
            if(StringUtils.isNotEmpty(vNewMsgID) && StringUtils.isNotBlank(vNewMsgID) && !"-1".equals(vNewMsgID)){
                msgConn = getEventCheckerConnection(props,log);
                if(msgConn == null) return false;
                int vProcessID = jobId;
                String vReceiveTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");;
                String sqlForUpdateMsg = "INSERT INTO event_status(receiver,topic,msg_name,receive_time,msg_id) VALUES(?,?,?,?,?) ON DUPLICATE KEY UPDATE receive_time=VALUES(receive_time),msg_id= CASE WHEN msg_id= " + lastMsgId + " THEN VALUES(msg_id) ELSE msg_id END";
                log.info("last message offset {} is:" + lastMsgId);
                updatePstmt = msgConn.prepareCall(sqlForUpdateMsg);
                updatePstmt.setString(1, receiver);
                updatePstmt.setString(2, topic);
                updatePstmt.setString(3, msgName);
                updatePstmt.setString(4, vReceiveTime);
                updatePstmt.setString(5, vNewMsgID);
                int updaters = updatePstmt.executeUpdate();
                log.info("updateMsgOffset successful {} update result is:" + updaters);
                if(updaters != 0){
                    log.info("Received message successfully , update message status succeeded, consumed flow execution ID: " + vProcessID);
                    //return true after update success
                    result = true;
                }else{
                    log.info("Received message successfully , update message status failed, consumed flow execution ID: " + vProcessID);
                    result = false;
                }
            }else{
                result = false;
            }
        }catch (SQLException e){
            log.error("Error update Msg Offset" + e);
            return false;
        }finally {
            closeQueryStmt(updatePstmt, log);
            closeConnection(msgConn, log);
        }
        return result;
    }

    /**
     * get consumption progress
     */
    String getOffset(int jobId, Properties props, Logger log){
        String sqlForReadMsgID = "SELECT msg_id FROM event_status WHERE receiver=? AND topic=? AND msg_name=?";
        PreparedStatement pstmtForGetID = null;
        Connection msgConn = null;
        ResultSet rs = null;
        boolean flag = false;
        String lastMsgId = "0";
        try {
            msgConn = getEventCheckerConnection(props,log);
            pstmtForGetID = msgConn.prepareCall(sqlForReadMsgID);
            pstmtForGetID.setString(1, receiver);
            pstmtForGetID.setString(2, topic);
            pstmtForGetID.setString(3, msgName);
            rs = pstmtForGetID.executeQuery();
            while (rs.next()) {
                lastMsgId = rs.getString("msg_id");
            }
//            lastMsgId = rs.last()==true ? rs.getString("msg_id"):"0";
        } catch (SQLException e) {
            throw new RuntimeException("get Offset failed " + e);
        }finally {
            closeQueryStmt(pstmtForGetID,log);
            closeConnection(msgConn,log);
            closeQueryRef(rs,log);
        }
        log.info("The last record id was " + lastMsgId);
        return lastMsgId;
    }
   @Override
    public boolean reciveMsg(int jobId, Properties props, Logger log)  {
        boolean result = false;
        try{
            String lastMsgId = getOffset(jobId,props,log);
            String[] executeType = createExecuteType(jobId,props,log,lastMsgId);
            log.info("event receiver executeType[]:{},{},{},{},{}",executeType[0],executeType[1],executeType[2],executeType[3],executeType[4]);
            if(executeType!=null && executeType.length ==5){
                String[] consumedMsgInfo = getMsg(jobId, props, log,executeType);
                if(consumedMsgInfo!=null && consumedMsgInfo.length == 4){
                    result = updateMsgOffset(jobId,props,log,consumedMsgInfo,lastMsgId);
                }
            }else{
                log.error("executeType error {} " + Arrays.toString(executeType));
                return result;
            }
        }catch (Exception e){
            log.error("EventChecker failed to receive the message {}" + e);
            throw e;
        }
        return result;
    }


    private String[] createExecuteType(int jobId, Properties props, Logger log,String lastMsgId){
        boolean receiveTodayFlag = (null != receiveToday && "true".equals(receiveToday.trim().toLowerCase()));
        boolean afterSendFlag = (null != afterSend && "true".equals(afterSend.trim().toLowerCase()));
        //只有receiveTodayFlag为true时，useRunDateFlag才有意义。
        Boolean useRunDateFlag = receiveTodayFlag && (null == useRunDate || "true".equalsIgnoreCase(useRunDate.trim()));
        String[] executeType = null;
        try {
            if (receiveTodayFlag && !useRunDateFlag) {
                if (afterSendFlag) {
                    executeType = new String[]{nowStartTime, todayEndTime, lastMsgId, useRunDateFlag.toString(), runDate};
                } else {
                    executeType = new String[]{todayStartTime, todayEndTime, lastMsgId, useRunDateFlag.toString(), runDate};
                }
            } else {
                if (afterSendFlag) {
                    executeType = new String[]{nowStartTime, allEndTime, lastMsgId, useRunDateFlag.toString(), runDate};
                } else {
                    executeType = new String[]{allStartTime, allEndTime, lastMsgId, useRunDateFlag.toString(), runDate};
                }
            }
        }catch(Exception e){
            log.error("create executeType failed {}" + e);
        }
        return executeType;
    }
    public abstract String[] getMsg(int jobId,Properties props, Logger log,String ... params) ;
}
