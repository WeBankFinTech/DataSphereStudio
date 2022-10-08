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

package com.webank.wedatasphere.dss.appconn.datachecker.connector;

import com.alibaba.druid.pool.DruidDataSource;

import com.webank.wedatasphere.dss.appconn.datachecker.DataChecker;
import com.webank.wedatasphere.dss.appconn.datachecker.common.MaskCheckNotExistException;
import com.webank.wedatasphere.dss.appconn.datachecker.utils.HttpUtils;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.RefExecutionAction;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DataCheckerDao {

    private static final String SQL_SOURCE_TYPE_JOB_TABLE =
            "SELECT * FROM DBS d JOIN TBLS t ON t.DB_ID = d.DB_ID WHERE d.NAME=? AND t.TBL_NAME=?";

    private static final String SQL_SOURCE_TYPE_JOB_PARTITION =
            "SELECT * FROM DBS d JOIN TBLS t ON t.DB_ID = d.DB_ID JOIN PARTITIONS p ON p.TBL_ID = t.TBL_ID WHERE d.NAME=? AND t.TBL_NAME=? AND p.PART_NAME=?";

    private static final String SQL_SOURCE_TYPE_BDP =
            "SELECT * FROM desktop_bdapimport WHERE bdap_db_name = ? AND bdap_table_name = ? AND target_partition_name = ? AND status = '1';";

    private static final String SQL_SOURCE_TYPE_BDP_WITH_TIME_CONDITION =
            "SELECT * FROM desktop_bdapimport WHERE bdap_db_name = ? AND bdap_table_name = ? AND target_partition_name = ? " +
                    "AND (UNIX_TIMESTAMP() - UNIX_TIMESTAMP(STR_TO_DATE(modify_time, '%Y-%m-%d %H:%i:%s'))) <= ? AND status = '1';";
    private static final String HIVE_SOURCE_TYPE = "hivedb";
    private static final String MASK_SOURCE_TYPE = "maskdb";

    private static DataSource jobDS;
    private static DataSource bdpDS;
    private static volatile DataCheckerDao instance;

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

    public boolean validateTableStatusFunction(Properties props, Logger log, RefExecutionAction action) {
        if (jobDS == null) {
            jobDS = DataDruidFactory.getJobInstance(props, log);
            if (jobDS == null) {
                log.error("Error getting Druid DataSource instance");
                return false;
            }
        }
        if (bdpDS == null) {
            bdpDS = DataDruidFactory.getBDPInstance(props, log);
            if (bdpDS == null) {
                log.warn("Error getting Druid DataSource instance");
                return false;
            }
        }
        removeBlankSpace(props);
        log.info("=============================Data Check Start==========================================");

        String dataCheckerInfo = props.getProperty(DataChecker.DATA_OBJECT);
        if (null != action.getExecutionRequestRefContext()) {
            action.getExecutionRequestRefContext().appendLog("=============================Data Check Start==========================================");
//            action.getExecutionRequestRefContext().appendLog("Database table partition info : " + dataCheckerInfo);
        }
        log.info("(DataChecker info) database table partition info : " + dataCheckerInfo);
        long waitTime = Long.valueOf(props.getProperty(DataChecker.WAIT_TIME, "1")) * 3600 * 1000;
        int queryFrequency = Integer.valueOf(props.getProperty(DataChecker.QUERY_FREQUENCY, "30000"));
//		String timeScape = props.getProperty(DataChecker.TIME_SCAPE, "NULL");
        log.info("(DataChecker info) wait time : " + waitTime);
        log.info("(DataChecker info) query frequency : " + queryFrequency);
//		log.info("(DataChecker info) time scape : " + timeScape);
        List<Map<String, String>> dataObjectList = extractProperties(props);
        log.info("DataObjectList size is " + dataObjectList.size());
        dataObjectList.forEach(checkObject -> {
            log.info(checkObject.keySet().toString());
        });

        try (Connection jobConn = jobDS.getConnection();
             Connection bdpConn = bdpDS.getConnection()) {
            List<Boolean> allCheckRes = dataObjectList
                    .stream()
                    .map(proObjectMap -> {
                        log.info("Begin to Check dataObject:" + proObjectMap.entrySet().toString());
                        boolean checkRes = getDataCheckResult(proObjectMap, jobConn, bdpConn, props, log);
                        if (null != action.getExecutionRequestRefContext()) {
                            if (checkRes) {
                                action.getExecutionRequestRefContext().appendLog("Database table partition info : " + proObjectMap.get(DataChecker.DATA_OBJECT) + " has arrived");
                                log.info("sourceType：" + proObjectMap.get(DataChecker.SOURCE_TYPE) + ", Database table partition info : " + proObjectMap.get(DataChecker.DATA_OBJECT) + " has arrived");
                            } else {
                                action.getExecutionRequestRefContext().appendLog("sourceType：" + proObjectMap.get(DataChecker.SOURCE_TYPE) + ", Database table partition info : " + proObjectMap.get(DataChecker.DATA_OBJECT) + " not arrived");
                                log.info("sourceType：" + proObjectMap.get(DataChecker.SOURCE_TYPE) + ", Database table partition info : " + proObjectMap.get(DataChecker.DATA_OBJECT) + " not arrived");
                            }
                        }
                        return checkRes;
                    }).collect(Collectors.toList());
            boolean flag = allCheckRes.stream().allMatch(res -> res.equals(true));
            if (flag) {
                log.info("=============================Data Check End==========================================");
                if (null != action.getExecutionRequestRefContext()) {
                    action.getExecutionRequestRefContext().appendLog("=============================Data Check End==========================================");
                }
                return true;
            }

        } catch (SQLException e) {
            throw new RuntimeException("get DataChecker result failed", e);
        }

        log.info("=============================Data Check End==========================================");
        if (null != action.getExecutionRequestRefContext()) {
            action.getExecutionRequestRefContext().appendLog("=============================Data Check End==========================================");
        }
        return false;
    }

    private boolean getDataCheckResult(Map<String, String> proObjectMap, Connection jobConn, Connection bdpConn, Properties props, Logger log) {
        Predicate<Map<String, String>> hasDataSource = p -> {
            if (StringUtils.isEmpty(proObjectMap.get(DataChecker.SOURCE_TYPE))) {
                return false;
            } else {
                return true;
            }
        };
        Predicate<Map<String, String>> hasNotDataSource = hasDataSource.negate();
        Supplier<String> sourceType = () -> proObjectMap.get(DataChecker.SOURCE_TYPE).toLowerCase();
        Predicate<Map<String, String>> isJobDataSource = p -> sourceType.get().equals("hivedb") || sourceType.get().equals("job");
        Predicate<Map<String, String>> isBdpDataSource = p -> sourceType.get().equals("maskdb") || sourceType.get().equals("bdp");
        Predicate<Map<String, String>> isOdsDB = p -> {
            String dataObject = proObjectMap.get(DataChecker.DATA_OBJECT)
                    .replace(" ", "").trim();
            String dbName = dataObject.split("\\.")[0];
            return dbName.contains("_ods");
        };
        Predicate<Map<String, String>> isNotOdsDB = isOdsDB.negate();
        Predicate<Map<String, String>> isCheckMetadata = (hasDataSource.and(isJobDataSource)).or(hasNotDataSource.and(isNotOdsDB));
        Predicate<Map<String, String>> isCheckMask = (hasDataSource.and(isBdpDataSource)).or(hasNotDataSource.and(isOdsDB));
        if (isCheckMetadata.test(proObjectMap)) {
            proObjectMap.put(DataChecker.SOURCE_TYPE, HIVE_SOURCE_TYPE);
            return getJobTotalCount(proObjectMap, jobConn, log) > 0;
        } else {
            if (isCheckMask.test(proObjectMap)) {
                proObjectMap.put(DataChecker.SOURCE_TYPE, MASK_SOURCE_TYPE);
                return (getBdpTotalCount(proObjectMap, bdpConn, log, props) > 0 || "success".equals(fetchMaskCode(proObjectMap, log, props).get("maskStatus")));
            }
            return false;
        }

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
        } catch (Exception e) {
            throw new RuntimeException("remove job space char failed", e);
        }
    }

    private List<Map<String, String>> extractProperties(Properties p) {
        return p.keySet().stream()
                .map(key -> key2Map(key, p)).filter(x -> x.size() > 0)
                .collect(Collectors.toList());
    }

    private Map<String, String> key2Map(Object key, Properties p) {
        Map<String, String> proMap = new HashMap<>();
        String skey = String.valueOf(key);
        if (skey.contains(DataChecker.DATA_OBJECT)) {
            String[] keyArr = skey.split("\\.");
            if (keyArr.length == 3) {
                String keyNum = keyArr[2];
                String stKey = DataChecker.SOURCE_TYPE + "." + keyNum;
                String doKey = DataChecker.DATA_OBJECT + "." + keyNum;
                if (null != p.get(stKey)) {
                    proMap.put(DataChecker.SOURCE_TYPE, String.valueOf(p.get(stKey)));
                }
                proMap.put(DataChecker.DATA_OBJECT, String.valueOf(p.get(doKey)));
            } else {
                String stKey = DataChecker.SOURCE_TYPE;
                String doKey = DataChecker.DATA_OBJECT;
                if (null != p.get(stKey)) {
                    proMap.put(DataChecker.SOURCE_TYPE, String.valueOf(p.get(stKey)));
                }
                proMap.put(DataChecker.DATA_OBJECT, String.valueOf(p.get(doKey)));
            }
        }

        return proMap;
    }

    private PreparedStatement getJobStatement(Connection conn, String dataObject) throws SQLException {
        String dataScape = dataObject.contains("{") ? "Partition" : "Table";
        String[] dataObjectArray = dataObject.split("\\.");
        String dbName = dataObject.split("\\.")[0];
        String tableName = dataObject.split("\\.")[1];
        if (dataScape.equals("Partition")) {
            Pattern pattern = Pattern.compile("\\{([^\\}]+)\\}");
            Matcher matcher = pattern.matcher(dataObject);
            String partitionName = "";
            if (matcher.find()) {
                partitionName = matcher.group(1);
            }
            partitionName = partitionName.replace("\'", "").replace("\"", "");
            tableName = tableName.split("\\{")[0];
            PreparedStatement pstmt = conn.prepareCall(SQL_SOURCE_TYPE_JOB_PARTITION);
            pstmt.setString(1, dbName);
            pstmt.setString(2, tableName);
            pstmt.setString(3, partitionName);
            return pstmt;
        } else if (dataObjectArray.length == 2) {
            PreparedStatement pstmt = conn.prepareCall(SQL_SOURCE_TYPE_JOB_TABLE);
            pstmt.setString(1, dbName);
            pstmt.setString(2, tableName);
            return pstmt;
        } else {
            throw new SQLException("Error for  DataObject format!");
        }
    }

    private PreparedStatement getBdpStatement(Connection conn, String dataObject, String timeScape) throws SQLException {
        String dataScape = dataObject.contains("{") ? "Partition" : "Table";
        String dbName = dataObject.split("\\.")[0];
        String tableName = dataObject.split("\\.")[1];
        String partitionName = "";
        Pattern pattern = Pattern.compile("\\{([^\\}]+)\\}");
        if (dataScape.equals("Partition")) {
            Matcher matcher = pattern.matcher(dataObject);
            if (matcher.find()) {
                partitionName = matcher.group(1);
            }
            partitionName = partitionName.replace("\'", "").replace("\"", "");
            tableName = tableName.split("\\{")[0];
        }
        PreparedStatement pstmt = null;
        if (timeScape.equals("NULL")) {
            pstmt = conn.prepareCall(SQL_SOURCE_TYPE_BDP);
        } else {
            pstmt = conn.prepareCall(SQL_SOURCE_TYPE_BDP_WITH_TIME_CONDITION);
            pstmt.setInt(4, Integer.valueOf(timeScape) * 3600);
        }
        pstmt.setString(1, dbName);
        pstmt.setString(2, tableName);
        pstmt.setString(3, partitionName);
        return pstmt;
    }

    private long getJobTotalCount(Map<String, String> proObjectMap, Connection conn, Logger log) {
        String dataObject = proObjectMap.get(DataChecker.DATA_OBJECT) == null ? "" : proObjectMap.get(DataChecker.DATA_OBJECT);
        if (StringUtils.isNotBlank(dataObject)) {
            dataObject = dataObject.replace(" ", "").trim();
        }
        log.info("-------------------------------------- search hive/spark/mr data ");
        log.info("-------------------------------------- : " + dataObject);
        try (PreparedStatement pstmt = getJobStatement(conn, dataObject)) {
            ResultSet rs = pstmt.executeQuery();
            return rs.last() ? rs.getRow() : 0;
        } catch (SQLException e) {
            log.error("fetch data from Hive MetaStore error", e);
            return 0;
        }
    }

    private long getBdpTotalCount(Map<String, String> proObjectMap, Connection conn, Logger log, Properties props) {
        String dataObject = proObjectMap.get(DataChecker.DATA_OBJECT) == null ? "": proObjectMap.get(DataChecker.DATA_OBJECT);
        if (StringUtils.isNotBlank(dataObject)) {
            dataObject = dataObject.replace(" ", "").trim();
        }
        String timeScape = props.getOrDefault(DataChecker.TIME_SCAPE, "NULL").toString();
        log.info("-------------------------------------- search bdp data ");
        log.info("-------------------------------------- : " + dataObject);
        try (PreparedStatement pstmt = getBdpStatement(conn, dataObject, timeScape)) {
            ResultSet rs = pstmt.executeQuery();
            return rs.last() ? rs.getRow() : 0;
        } catch (SQLException e) {
            log.error("fetch data from Hive MetaStore error", e);
            return 0;
        }
    }

    private Map<String, String> fetchMaskCode(Map<String, String> proObjectMap, Logger log, Properties props) {
        log.info("=============================调用BDP MASK接口查询数据状态==========================================");
        Map<String, String> resultMap = new HashMap();
        String maskUrl = props.getProperty(DataChecker.MASK_URL);
        String dataObject = proObjectMap.get(DataChecker.DATA_OBJECT) == null ? "":proObjectMap.get(DataChecker.DATA_OBJECT);
        if ( StringUtils.isNotBlank(dataObject)) {
            dataObject = dataObject.replace(" ", "").trim();
        }
        String dataScape = dataObject.contains("{") ? "Partition" : "Table";
        String dbName = dataObject.split("\\.")[0];
        String tableName = dataObject.split("\\.")[1];
        String partitionName = "";
        Pattern pattern = Pattern.compile("\\{([^\\}]+)\\}");
        if (dataScape.equals("Partition")) {
            Matcher matcher = pattern.matcher(dataObject);
            if (matcher.find()) {
                partitionName = matcher.group(1);
            }
            partitionName = partitionName.replace("\'", "").replace("\"", "");
            tableName = tableName.split("\\{")[0];
        }
        try {
            RequestBody requestBody = new FormBody.Builder()
                    .add("targetDb", dbName)
                    .add("targetTable", tableName)
                    .add("partition", partitionName)
                    .build();
            Map<String, String> dataMap = HttpUtils.initSelectParams(props);
            log.info("request body:dbName--" + dbName + " tableName--" + tableName + " partitionName--" + partitionName);
            Response response = HttpUtils.httpClientHandleBase(maskUrl, requestBody, dataMap);
            handleResponse(response, resultMap, log);
        } catch (IOException e) {
            log.error("fetch data from BDP MASK failed ");
            resultMap.put("maskStatus", "noPrepare");
        } catch (MaskCheckNotExistException e) {
            String errorMessage = "fetch data from BDP MASK failed" +
                    "please check database: " + dbName + ",table: " + tableName + "is exist";
            log.error(errorMessage);
            throw new RuntimeException(errorMessage, e);
        }
        return resultMap;
    }

    private void handleResponse(Response response, Map<String, String> proObjectMap, Logger log)
            throws IOException, MaskCheckNotExistException {
        int responseCode = response.code();
        ResponseBody body = response.body();
        if (responseCode == 200) {
            handleResponseBody(body, proObjectMap, log);
        } else {
            proObjectMap.put("maskStatus", "noPrepare");
        }
    }

    private void handleResponseBody(ResponseBody body, Map<String, String> proObjectMap, Logger log)
            throws IOException, MaskCheckNotExistException {
        String bodyStr = body.string();
        log.info("mask interface response body：" + bodyStr);
        Map entityMap = HttpUtils.getReturnMap(bodyStr);
        String codeValue = (String) entityMap.get("code");
        if ("200".equals(codeValue)) {
            proObjectMap.put("maskStatus", "success");
        } else if ("1011".equals(codeValue)) {
            throw new MaskCheckNotExistException("Mask check failed");
        } else {
            proObjectMap.put("maskStatus", "noPrepare");
        }
    }

    public static void closeDruidDataSource() {
        DruidDataSource jobDSObject = (DruidDataSource) jobDS;
        if (jobDSObject != null) {
            jobDSObject.close();
        }
    }

}
