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
import java.util.Date;
import java.util.Properties;

public class AbstractEventCheckReceiver extends AbstractEventCheck{
    /**
     * Fill the result into the source
     */
    String setConsumedMsg(Properties props, Logger log, String[] consumedMsgInfo){
        String vNewMsgID = "";
        try {
            if(consumedMsgInfo!=null && consumedMsgInfo.length == 4){
                vNewMsgID = consumedMsgInfo[0];
                String vMsgName = consumedMsgInfo[1];
                String vSender = consumedMsgInfo[2];
                String vMsg = consumedMsgInfo[3];
                if (null == vMsg) {
                    props.put(EventChecker.MSG, "NULL");
                } else {
                    props.put(EventChecker.MSG, vMsg);
                }
                log.info("Received message : messageID: " + vNewMsgID + ", messageName: " + vMsgName + ", receiver: " + vSender
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

    /**
     * Consistent entrance to consumer message
     */
    String[] getMsg(Properties props, Logger log,String ... params){
        boolean useRunDate=Boolean.getBoolean(params[3]);
        String sqlForReadTMsg;
        if(useRunDate){
            sqlForReadTMsg ="SELECT * FROM event_queue WHERE topic=? AND msg_name=? AND send_time >=? AND send_time <=? AND msg_id >?  AND run_date =?ORDER BY msg_id ASC LIMIT 1";
        } else{
            sqlForReadTMsg="SELECT * FROM event_queue WHERE topic=? AND msg_name=? AND send_time >=? AND send_time <=? AND msg_id >? ORDER BY msg_id ASC LIMIT 1";
        }

        PreparedStatement pstmt = null;
        Connection msgConn = null;
        ResultSet rs = null;
        String[] consumedMsgInfo = null;
        try {
            msgConn = getEventCheckerConnection(props,log);
            pstmt = msgConn.prepareCall(sqlForReadTMsg);
            pstmt.setString(1, topic);
            pstmt.setString(2, msgName);
            pstmt.setString(3, params[0]);
            pstmt.setString(4, params[1]);
            pstmt.setString(5, params[2]);
            if(useRunDate){
                pstmt.setString(6,params[4]);
            }
            log.info("param {} StartTime: " + params[0] + ", EndTime: " + params[1]
                    + ", Topic: " + topic + ", MessageName: " + msgName + ", LastMessageID: " + params[2]);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                consumedMsgInfo = new String[4];
                String[] msgKey = new String[]{"msg_id", "msg_name", "sender", "msg"};
                for (int i = 0; i < msgKey.length; i++) {
                    try {
                        consumedMsgInfo[i] = rs.getString(msgKey[i]);
                    } catch (SQLException e) {
                        throw new RuntimeException("Error while reading data from ResultSet", e);
                    }
                }
            }

//            if(rs.last()){
//                consumedMsgInfo = new String[4];
//                String[] msgKey = new String[]{"msg_id","msg_name","sender","msg"};
//                for (int i = 0;i <= 3;i++) {
//                    consumedMsgInfo[i] = rs.getString(msgKey[i]);
//                }
//            }
        } catch (SQLException e) {
            throw new RuntimeException("EventChecker failed to receive message" + e);
        } finally {
            closeQueryStmt(pstmt, log);
            closeConnection(msgConn, log);
            closeQueryRef(rs, log);
        }
        return consumedMsgInfo;
    }

    @Override
    public boolean reciveMsg(int jobId, Properties props, Logger log) {
        return super.reciveMsg(jobId, props, log);
    }
}
