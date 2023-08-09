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

package com.webank.wedatasphere.dss.appconn.eventchecker.connector;

import com.alibaba.druid.pool.DruidDataSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.Base64;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class EventDruidFactory {
	private static ConcurrentHashMap<String, DruidDataSource> instanceMap = new ConcurrentHashMap<>();
	private static final String EVENT_DRUID_USERNAME = "msg.eventchecker.jdo.option.username";
	private static final String EVENT_DRUID_URL = "msg.eventchecker.jdo.option.url";

	public static DruidDataSource getMsgInstance(Properties props, Logger log) {
		String eventDruidUsername =props.getProperty(EVENT_DRUID_USERNAME);
		String eventDruidUrl = props.getProperty(EVENT_DRUID_URL);
		log.info("EVENT_DRUID_USERNAME：" + eventDruidUsername+ "");
		log.info("EVENT_DRUID_URL：" + eventDruidUrl + "");
		String key = eventDruidUsername + eventDruidUrl;
		if (instanceMap.containsKey(key)) {
			return instanceMap.get(key);
		} else {
			synchronized (EventDruidFactory.class) {
				if (instanceMap.containsKey(key)) {
					return instanceMap.get(key);
				}
				DruidDataSource msgInstance = createDataSource(props, log, "Msg");
				instanceMap.put(key, msgInstance);
				return instanceMap.get(key);
			}
		}
	}

	private static DruidDataSource createDataSource(Properties props, Logger log, String type) {
		String name = null;
		String url = null;
		String username = null;
		String password = null;
		String loginType = null;

		if(type.equals("Msg")){
			name = props.getProperty("msg.eventchecker.jdo.option.name");
			url = props.getProperty("msg.eventchecker.jdo.option.url");
			username = props.getProperty("msg.eventchecker.jdo.option.username");
			loginType = props.getProperty("msg.eventchecker.jdo.option.login.type");
			try {
				if("base64".equals(loginType)) {
					password = new String(Base64.getDecoder().decode(props.getProperty("msg.eventchecker.jdo.option.password").getBytes()), "UTF-8");
				}else{
					password = props.getProperty("msg.eventchecker.jdo.option.password");
				}
			} catch (Exception e){
				log.error("password decore failed" + e);
			}
		}

		int initialSize = Integer.valueOf(props.getProperty("option.initial.size", "1"));
		int maxActive = Integer.valueOf(props.getProperty("option.max.active", "100"));
		int minIdle = Integer.valueOf(props.getProperty("option.min.idle", "1"));
		long maxWait = Long.valueOf(props.getProperty("option.max.wait", "60000"));
		String validationQuery = props.getProperty("option.validation.quert", "SELECT 'x'");
		long timeBetweenEvictionRunsMillis = Long.valueOf(props.getProperty("option.time.between.eviction.runs.millis", "6000"));
		long minEvictableIdleTimeMillis = Long.valueOf(props.getProperty("option.evictable.idle,time.millis", "300000"));
		boolean testOnBorrow = Boolean.valueOf(props.getProperty("option.test.on.borrow", "true"));
		int maxOpenPreparedStatements = Integer.valueOf(props.getProperty("option.max.open.prepared.statements", "-1"));

		if (timeBetweenEvictionRunsMillis > minEvictableIdleTimeMillis) {
			timeBetweenEvictionRunsMillis = minEvictableIdleTimeMillis;
		}

		DruidDataSource ds = new DruidDataSource();

		if (StringUtils.isNotBlank(name)) {
			ds.setName(name);
		}

		ds.setUrl(url);
		ds.setDriverClassName("com.mysql.jdbc.Driver");
	    ds.setUsername(username);
	    ds.setPassword(password);
	    ds.setInitialSize(initialSize);
	    ds.setMinIdle(minIdle);
	    ds.setMaxActive(maxActive);
	    ds.setMaxWait(maxWait);
	    ds.setTestOnBorrow(testOnBorrow);
	    ds.setValidationQuery(validationQuery);
	    ds.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
	    ds.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
	    if (maxOpenPreparedStatements > 0) {
	      ds.setPoolPreparedStatements(true);
	      ds.setMaxPoolPreparedStatementPerConnectionSize(
	          maxOpenPreparedStatements);
	    } else {
	      ds.setPoolPreparedStatements(false);
	    }
	    log.info("Druid data source initialed!");
	    return ds;
	}
}
