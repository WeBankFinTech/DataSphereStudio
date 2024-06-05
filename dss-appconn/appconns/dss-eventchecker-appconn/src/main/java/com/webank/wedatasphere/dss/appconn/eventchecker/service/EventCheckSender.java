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

import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;

public class EventCheckSender extends AbstractEventCheck {

    public EventCheckSender(Properties props) {
        initECParams(props);
    }

    @Override
    public boolean sendMsg(int jobId, Properties props, Logger log) {
            boolean result = false;
            PreparedStatement pstmt = null;
            Connection msgConn = null;
            String sendTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
            String runDate = props.getProperty("run_date");
            String sqlForSendMsg = "INSERT INTO event_queue (sender,send_time,topic,msg_name,msg,send_ip,run_date) VALUES(?,?,?,?,?,?,?)";
            try {
                String vIP = getLinuxLocalIp(log);
                msgConn = getEventCheckerConnection(props,log);
                if(msgConn==null) return false;
                pstmt = msgConn.prepareCall(sqlForSendMsg);
                pstmt.setString(1, sender);
                pstmt.setString(2, sendTime);
                pstmt.setString(3, topic);
                pstmt.setString(4, msgName);
                pstmt.setString(5, msg);
                pstmt.setString(6, vIP);
                pstmt.setString(7, runDate);
                int rs = pstmt.executeUpdate();
                if (rs == 1) {
                    result = true;
                    log.info("Send msg success!");
                } else {
                    log.error("Send msg failed for update database!");
                }
            } catch (SQLException e) {
                throw new RuntimeException("Send EventChecker msg failed!" + e);
            } finally {
                closeQueryStmt(pstmt, log);
                closeConnection(msgConn, log);
            }
            return result;
    }
}
