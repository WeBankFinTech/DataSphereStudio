/*
 *
 *  * Copyright 2019 WeBank
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package com.webank.wedatasphere.dss.apiservice.core.jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * DatasourceConfiguration
 *
 * @author lidongzhang
 */
@Configuration
public class DatasourceService {
    private static final Map<String, NamedParameterJdbcTemplate> CLIENT_MAP = new HashMap<>();


    private DataSource getDatasource(String url, String username, String password) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setMaximumPoolSize(100);
        hikariConfig.setMinimumIdle(10);
        return new HikariDataSource(hikariConfig);
    }


    public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate(String url,
                                                                    String username, String password) {
        String key = genKey(url, username);
        NamedParameterJdbcTemplate jdbcTemplate = CLIENT_MAP.get(key);

        if (jdbcTemplate == null) {
            synchronized (this) {
                jdbcTemplate = CLIENT_MAP.get(key);
                if (jdbcTemplate == null) {
                    jdbcTemplate = new NamedParameterJdbcTemplate(getDatasource(url, username, password));
                    CLIENT_MAP.put(key, jdbcTemplate);
                }
            }
        }

        return jdbcTemplate;
    }

    private String genKey(String jdbcUrl, String username) {
        return String.join("-", jdbcUrl, username);
    }
}
