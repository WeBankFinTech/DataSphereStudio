package com.webank.wedatasphere.linkis.oneservice.core.jdbc;

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
