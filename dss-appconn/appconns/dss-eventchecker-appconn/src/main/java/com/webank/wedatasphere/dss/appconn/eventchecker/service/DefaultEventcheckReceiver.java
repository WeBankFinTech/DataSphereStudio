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




import org.slf4j.Logger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class DefaultEventcheckReceiver extends AbstractEventCheckReceiver {

    public DefaultEventcheckReceiver(Properties props) {
       super(props);
    }
    /**
     * Consistent entrance to consumer message
     */
    @Override
    public String[] getMsg(int jobId,Properties props, Logger log,String ... params){
        boolean useRunDate=Boolean.parseBoolean(params[3]);
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
                log.info("use run_date, run_date:{}", params[4]);
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
                consumedMsgInfo[2] = receiver;
            }
        } catch (SQLException e) {
            log.error("EventChecker failed to receive message",e);
            throw new RuntimeException(e);
        } finally {
            closeQueryStmt(pstmt, log);
            closeConnection(msgConn, log);
            closeQueryRef(rs, log);
        }
        return consumedMsgInfo;
    }

}
