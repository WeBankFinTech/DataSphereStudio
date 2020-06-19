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

package com.webank.wedatasphere.dss.appjoint.schedulis.jobtype.connector;

import com.alibaba.druid.pool.DruidDataSource;
import com.webank.wedatasphere.dss.appjoint.schedulis.jobtype.entity.DataChecker;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DataCheckerDao {

	private static final String SQL_SOURCE_TYPE_JOB_TABLE =
			"SELECT * FROM DBS d JOIN TBLS t ON t.DB_ID = d.DB_ID WHERE d.NAME=? AND t.TBL_NAME=?";

	private static final String SQL_SOURCE_TYPE_JOB_PARTITION =
			"SELECT * FROM DBS d JOIN TBLS t ON t.DB_ID = d.DB_ID JOIN PARTITIONS p ON p.TBL_ID = t.TBL_ID WHERE d.NAME=? AND t.TBL_NAME=? AND p.PART_NAME=?";

	private static DataSource jobDS;
	private static DataCheckerDao instance;

	public static DataCheckerDao getInstance() {
		if (instance == null) {
			synchronized (DataCheckerDao.class) {
				if (instance == null) {
					instance = new DataCheckerDao();
				}
			}
		}
		return instance;
	}

	public boolean validateTableStatusFunction(Properties props, Logger log) {
		if (jobDS == null) {
			jobDS = DataDruidFactory.getJobInstance(props, log);
			if (jobDS == null) {
				log.error("Error getting Druid DataSource instance");
				return false;
			}
		}
		removeBlankSpace(props);
		log.info("=============================Data Check Start==========================================");
		String dataCheckerInfo = props.getProperty(DataChecker.DATA_OBJECT);
		log.info("(DataChecker info) database table partition info : " + dataCheckerInfo);
		long waitTime = Long.valueOf(props.getProperty(DataChecker.WAIT_TIME, "1")) * 3600 * 1000;
		int queryFrequency = Integer.valueOf(props.getProperty(DataChecker.QUERY_FREQUENCY, "30000"));
//		String timeScape = props.getProperty(DataChecker.TIME_SCAPE, "NULL");
		log.info("(DataChecker info) wait time : " + waitTime);
		log.info("(DataChecker info) query frequency : " + queryFrequency);
//		log.info("(DataChecker info) time scape : " + timeScape);
		List<Map<String, String>> dataObjectList = extractProperties(props);
		try (Connection conn = jobDS.getConnection()) {
			boolean flag = dataObjectList
					.stream()
					.allMatch(proObjectMap -> {
						long count = getTotalCount(proObjectMap, conn, log);
						return count > 0;
					});
			if (flag){
				log.info("=============================Data Check End==========================================");
				return true;
			}

		} catch (SQLException e) {
			throw new RuntimeException("get DataChecker result failed", e);
		}

		log.info("=============================Data Check End==========================================");
		return false;
	}

	private void sleep(long sleepTime) {
		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void removeBlankSpace(Properties props) {
		try {
			props.entrySet().forEach(entry -> {
				String value = entry.getValue().toString().replaceAll(" ", "").trim();
				entry.setValue(value);
			});
		}catch (Exception e){
			throw new RuntimeException("remove job space char failed",e);
		}
	}

	private List<Map<String, String>> extractProperties(Properties p) {
		return p.keySet().stream()
				.map(key -> key2Map(key, p)).filter(x -> x.size() >0)
				.collect(Collectors.toList());
	}

	private Map<String, String> key2Map(Object key, Properties p) {
		Map<String, String> proMap = new HashMap<>();
		String skey = String.valueOf(key);
		if(skey.contains(DataChecker.DATA_OBJECT)){
			String[] keyArr = skey.split("\\.");
			if(keyArr.length == 3){
				String keyNum = keyArr[2];
				String doKey = DataChecker.DATA_OBJECT + "." + keyNum;
				proMap.put(DataChecker.DATA_OBJECT, String.valueOf(p.get(doKey)));
			}else{
				String doKey = DataChecker.DATA_OBJECT;
				proMap.put(DataChecker.DATA_OBJECT, String.valueOf(p.get(doKey)));
			}
		}
		return proMap;
	}

	private long getTotalCount(Map<String, String>  proObjectMap, Connection conn, Logger log) {
		String dataObject = proObjectMap.get(DataChecker.DATA_OBJECT);
		if(dataObject != null) {
			dataObject = dataObject.replace(" ", "").trim();
		}else{
			log.error("DataObject is null");
			return 0;
		}
		log.info("-------------------------------------- search hive/spark/mr data ");
		log.info("-------------------------------------- : " + dataObject);
		try (PreparedStatement pstmt = getStatement(conn, dataObject)) {
			ResultSet rs = pstmt.executeQuery();
			return rs.last() ? rs.getRow() : 0;
		} catch (SQLException e) {
			log.error("fetch data from Hive MetaStore error", e);
			return 0;
		}
	}

	private PreparedStatement getStatement(Connection conn, String dataObject) throws SQLException {
		String dataScape =  dataObject.contains("{") ? "Partition" : "Table";
		String[] dataObjectArray = dataObject.split("\\.");
		String dbName = dataObjectArray[0];
		String tableName = dataObjectArray[1];
		if(dataScape.equals("Partition")) {
			Pattern pattern = Pattern.compile("\\{([^\\}]+)\\}");
			Matcher matcher = pattern.matcher(dataObject);
			String partitionName = null;
			if(matcher.find()){
				partitionName = matcher.group(1);
			}
			partitionName = partitionName.replace("\'", "").replace("\"", "");
			tableName = tableName.split("\\{")[0];
			PreparedStatement pstmt = conn.prepareCall(SQL_SOURCE_TYPE_JOB_PARTITION);
			pstmt.setString(1, dbName);
			pstmt.setString(2, tableName);
			pstmt.setString(3, partitionName);
			return pstmt;
		} else if(dataObjectArray.length == 2){
			PreparedStatement pstmt = conn.prepareCall(SQL_SOURCE_TYPE_JOB_TABLE);
			pstmt.setString(1, dbName);
			pstmt.setString(2, tableName);
			return pstmt;
		}else {
			throw new SQLException("Incorrect input format for dataObject "+ dataObject);
		}
	}

	public static void closeDruidDataSource(){
		DruidDataSource jobDSObject = (DruidDataSource)jobDS;
		if(jobDSObject != null){
			jobDSObject.close();
		}
	}

}
