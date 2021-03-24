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


package com.webank.wedatasphpere.dss.user.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.webank.wedatasphpere.dss.user.conf.DSSUserManagerConfig;
import com.webank.wedatasphpere.dss.user.dto.request.AuthorizationBody;
import com.webank.wedatasphpere.dss.user.service.AbsCommand;
import com.webank.wedatasphpere.dss.user.service.Command;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.DES;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author litongtong
 * @date 2021/1/11
 */
public class SchedulisCommand extends AbsCommand {

    private static final Logger logger = LoggerFactory.getLogger(SchedulisCommand.class);

    @Override
    public String authorization(AuthorizationBody body) {
        String username = body.getUsername();
        logger.info("开始新增调度用户：{}", username);
        String password = body.getPassword();
        String encryptionPwd = getEncryptionPwd(username, password);
        Connection connection;
        Statement stmt;
        try {
            logger.info("开始插入ctyun_user表");
            connection = getConnection();
            stmt = connection.createStatement();
            String sql = "INSERT INTO `ctyun_user` (`id`,`name`,`username`,`email`,`password`,`work_order_item_config`) VALUES (?,?,?,?,?,NULL) ON DUPLICATE KEY UPDATE `password` = ?";
            PreparedStatement statement = connection.prepareCall(sql);
            statement.setString(1, username);
            statement.setString(2, username);
            statement.setString(3, username);
            statement.setString(4, username);
            statement.setString(5, encryptionPwd);
            statement.setString(6, encryptionPwd);
            statement.executeUpdate();
            stmt.close();
            connection.close();
            logger.info("完成插入ctyun_user表");
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        logger.info("开始调用接口新增schedulis用户");
        addSchedulisUser(username, password);
        logger.info("结束调用接口新增schedulis用户");
        return Command.SUCCESS;
    }

    private static Connection getConnection() {
        try {
            //注册数据库的驱动
            Class.forName("com.mysql.jdbc.Driver");
            //获取数据库连接（里面内容依次是：主机名和端口、用户名、密码）
            String url = DSSUserManagerConfig.BDP_SERVER_MYBATIS_DATASOURCE_URL;
            String user = DSSUserManagerConfig.BDP_SERVER_MYBATIS_DATASOURCE_USERNAME;
            String password = DSSUserManagerConfig.BDP_SERVER_MYBATIS_DATASOURCE_PASSWORD;
            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    private boolean addSchedulisUser(String username, String password) {
        String schedulerUrl = DSSUserManagerConfig.DSS_SCHEDULER_URL;
        RestTemplate restTemplate = new RestTemplate();
        String schedulisUrl = DSSUserManagerConfig.SCHEDULER_ADDRESS;
        String url = new StringBuilder().append(schedulisUrl)
            .append(schedulerUrl)
            .toString();
        Map<String, String> params = new HashMap<>(4);
        params.put("userId", username);
        params.put("password", password);
        params.put("ajax", "addSystemUserViaFastTrackCtyun");
        String fullUrl = addParams(url, params);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(fullUrl);
        URI uri = builder.build().encode().toUri();
        ResponseEntity<JsonNode> responseEntity = restTemplate.getForEntity(uri, JsonNode.class);
        return HttpStatus.OK.equals(responseEntity.getStatusCode());
    }

    private static String addParams(String url, Map<String, String> params) {
        if (params.isEmpty()) {
            return url;
        }
        StringBuilder sb = new StringBuilder().append(url).append("?");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (StringUtils.hasText(entry.getValue())) {
                sb.append(entry.getKey())
                    .append("=")
                    .append(entry.getValue())
                    .append("&");
            }
        }
        return sb.deleteCharAt(sb.length()-1).toString();
    }

    private String getEncryptionPwd(String username, String password) {
        int minSize = 8;
        while (username.length() < minSize) {
            username += username;
        }
        byte[] keyBytes = username.getBytes();
        Arrays.sort(keyBytes);
        DES des = SecureUtil.des(keyBytes);
        return des.encryptHex(password);
    }
}
