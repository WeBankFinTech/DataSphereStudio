package com.webank.wedatasphere.dss.data.api.server.util;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.webank.wedatasphere.dss.data.api.server.entity.DataSource;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


@Slf4j
public class PoolManager {

    private static Lock lock = new ReentrantLock();

    private static Lock deleteLock = new ReentrantLock();

    //所有数据源的连接池存在map里
    static Map<Integer, DruidDataSource> map = new HashMap<>();

    public static DruidDataSource getJdbcConnectionPool(DataSource ds) {
        if (map.containsKey(ds.getDatasourceId())) {
            return map.get(ds.getDatasourceId());
        } else {
            lock.lock();
            try {
                log.info(Thread.currentThread().getName() + "获取锁");
                if (!map.containsKey(ds.getDatasourceId())) {
                    DruidDataSource druidDataSource = new DruidDataSource();
                    druidDataSource.setName(ds.getName());
                    druidDataSource.setUrl(ds.getUrl());
                    druidDataSource.setUsername(ds.getUsername());
                    druidDataSource.setPassword(ds.getPwd());
                    druidDataSource.setDriverClassName(ds.getClassName());
                    druidDataSource.setConnectionErrorRetryAttempts(3);       //失败后重连次数
                    druidDataSource.setBreakAfterAcquireFailure(true);

                    map.put(ds.getDatasourceId(), druidDataSource);
                    log.info("创建Druid连接池成功：{}", ds.getName());
                }
                return map.get(ds.getDatasourceId());
            } catch (Exception e) {
                return null;
            } finally {
                lock.unlock();
            }
        }
    }

    //删除数据库连接池
    public static void removeJdbcConnectionPool(Integer id) {
        deleteLock.lock();
        try {
            DruidDataSource druidDataSource = map.get(id);
            if (druidDataSource != null) {
                druidDataSource.close();
                map.remove(id);
            }
        } catch (Exception e) {
            log.error(e.toString());
        } finally {
            deleteLock.unlock();
        }

    }

    public static DruidPooledConnection getPooledConnection(DataSource ds) throws SQLException {
        ds.setPwd(CryptoUtils.string2Object(ds.getPwd()).toString());
        DruidDataSource pool = PoolManager.getJdbcConnectionPool(ds);
        DruidPooledConnection connection = pool.getConnection();
        return connection;
    }
}
