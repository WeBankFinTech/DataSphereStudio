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
import com.webank.wedatasphere.dss.appconn.datachecker.DataCheckerExecutionAction;
import com.webank.wedatasphere.dss.appconn.datachecker.common.CheckDataObject;
import com.webank.wedatasphere.dss.appconn.datachecker.common.MaskCheckNotExistException;
import com.webank.wedatasphere.dss.appconn.datachecker.utils.HttpUtils;
import com.webank.wedatasphere.dss.appconn.datachecker.utils.QualitisUtil;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.RefExecutionAction;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.RefExecutionState;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import javax.sql.DataSource;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
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

    private static final String SQL_DOPS_CHECK_TABLE =
            "SELECT * FROM dops_clean_task_list WHERE db_name = ? AND tb_name = ? AND part_name is null AND task_state NOT IN (10,13) order by order_id desc limit 1";
    private static final String SQL_DOPS_CHECK_PARTITION =
            "SELECT * FROM dops_clean_task_list WHERE db_name = ? AND tb_name = ? AND part_name = ?     AND task_state NOT IN  (10,13) order by order_id desc limit 1";

    private static final String SQL_DOPS_CHECK_ALL_PARTITION =
            "SELECT * FROM dops_clean_task_list WHERE db_name = ? AND tb_name = ? AND part_name is not null  AND task_state != 13 order by order_id desc limit 1";
    private static final String HIVE_SOURCE_TYPE = "hivedb";
    private static final String MASK_SOURCE_TYPE = "maskdb";

    private static DataSource jobDS;
    private static DataSource bdpDS;

    private static DataSource dopsDS;
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

    public boolean validateTableStatusFunction(Properties props, Logger log, DataCheckerExecutionAction action) {
        if (jobDS == null) {
            jobDS = DataDruidFactory.getJobInstance(props, log);
            if (jobDS == null) {
                log.error("Error getting  bdp  Druid DataSource instance");
                return false;
            }
        }
        if (bdpDS == null) {
            bdpDS = DataDruidFactory.getBDPInstance(props, log);
            if (bdpDS == null) {
                log.warn("Error getting job Druid DataSource instance");
                return false;
            }
        }
        boolean systemCheck = Boolean.valueOf(props.getProperty(DataChecker.QUALITIS_SWITCH));
        if (systemCheck && dopsDS == null) {
            dopsDS = DataDruidFactory.getDopsInstance(props, log);//通过alibaba的druid数据库连接池获取JOB数据库连接
            if (dopsDS == null) {
                log.error("Error getting Druid DataSource instance");
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
        QualitisUtil qualitisUtil = new QualitisUtil(props);
        try (Connection jobConn = jobDS.getConnection();
             Connection bdpConn = bdpDS.getConnection();
             Connection dopsConn = dopsDS != null ? dopsDS.getConnection() : null) {
            List<Boolean> allCheckRes = dataObjectList
                    .parallelStream()
                    .map(proObjectMap -> {
                        log.info("Begin to Check dataObject:" + proObjectMap.entrySet().toString());
                        boolean checkRes = getDataCheckResult(proObjectMap, jobConn, bdpConn, dopsConn, props, log,action,qualitisUtil);
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
                log.info("=============================Data Check End,check result:true==========================================");
                if (null != action.getExecutionRequestRefContext()) {
                    action.getExecutionRequestRefContext().appendLog("=============================Data Check End,check result:true==========================================");
                }
                return true;
            }

        } catch (SQLException e) {
            throw new RuntimeException("get DataChecker result failed", e);
        }

        log.info("=============================Data Check End,check result:false==========================================");
        if (null != action.getExecutionRequestRefContext()) {
            action.getExecutionRequestRefContext().appendLog("=============================Data Check End,,check result:false==========================================");
        }
        return false;
    }




    private boolean getDataCheckResult(Map<String, String> proObjectMap,
                                       Connection jobConn,
                                       Connection bdpConn,
                                       Connection dopsConn,
                                       Properties props,
                                       Logger log,
                                       DataCheckerExecutionAction action,
                                       QualitisUtil qualitisUtil ) {
        String dataObjectStr = proObjectMap.get(DataChecker.DATA_OBJECT) == null ? "" : proObjectMap.get(DataChecker.DATA_OBJECT);
        if (StringUtils.isNotBlank(dataObjectStr)) {
            dataObjectStr = dataObjectStr.replace(" ", "").trim();
        }
        String objectNum = proObjectMap.get(DataChecker.DATA_OBJECT_NUM);
        CheckDataObject dataObject;
        try {
            dataObject = parseDataObject(dataObjectStr);
        } catch (SQLException e) {
            log.error("parse dataObject failed", e);
            return false;
        }
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
        Predicate<Map<String, String>> isOdsDB = p ->  dataObject.getDbName().contains("_ods");
        Predicate<Map<String, String>> isNotOdsDB = isOdsDB.negate();
        Predicate<Map<String, String>> isCheckMetadata = (hasDataSource.and(isJobDataSource)).or(hasNotDataSource.and(isNotOdsDB));
        Predicate<Map<String, String>> isCheckMask = (hasDataSource.and(isBdpDataSource)).or(hasNotDataSource.and(isOdsDB));
        boolean normalCheck;
        if (isCheckMetadata.test(proObjectMap)) {
            if (null != action.getExecutionRequestRefContext()){
                action.getExecutionRequestRefContext().appendLog(dataObjectStr+" start to check hive meta");
            }
            log.info("start to check hive meta");
            proObjectMap.put(DataChecker.SOURCE_TYPE, HIVE_SOURCE_TYPE);
            normalCheck= getJobTotalCount(dataObject, jobConn, log) > 0;
            if (null != action.getExecutionRequestRefContext()){
                action.getExecutionRequestRefContext().appendLog(dataObjectStr+" check hive meta end,check result:"+normalCheck);
            }
            log.info("check hive meta end,check result:"+normalCheck);

        } else {
            if (isCheckMask.test(proObjectMap)) {
                if (null != action.getExecutionRequestRefContext()){
                    action.getExecutionRequestRefContext().appendLog(dataObjectStr+" start to check maskis");
                }
                log.info("start to check maskis");
                proObjectMap.put(DataChecker.SOURCE_TYPE, MASK_SOURCE_TYPE);
                normalCheck= (getBdpTotalCount(dataObject, bdpConn, log, props) > 0 || "success".equals(fetchMaskCode(dataObject, log, props).get("maskStatus")));
                if (null != action.getExecutionRequestRefContext()){
                    action.getExecutionRequestRefContext().appendLog(dataObjectStr+" check maskis end,check result:"+normalCheck);
                }
                log.info("check maskis end,check result:"+normalCheck);
            }else {
                normalCheck = false;
            }
        }
        if(!normalCheck){
            return false;
        }

        boolean qualitisCheck = checkQualitisData(objectNum, dataObject, log, action, props, dopsConn, qualitisUtil, dataObjectStr );
        if(!qualitisCheck){
            //如果是qualitis校验失败，则直接终止任务
            throw new RuntimeException(dataObjectStr+ " does not pass qualitis check(qualitis校验未通过)");
        }
        return qualitisCheck;
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
                proMap.put(DataChecker.DATA_OBJECT_NUM, keyNum);
            } else {
                String stKey = DataChecker.SOURCE_TYPE;
                String doKey = DataChecker.DATA_OBJECT;
                if (null != p.get(stKey)) {
                    proMap.put(DataChecker.SOURCE_TYPE, String.valueOf(p.get(stKey)));
                }
                proMap.put(DataChecker.DATA_OBJECT, String.valueOf(p.get(doKey)));
                proMap.put(DataChecker.DATA_OBJECT_NUM, "0");
            }
        }

        return proMap;
    }

    /**
     * 构造查询hive元数据库的查询
     */
    private PreparedStatement getJobStatement(Connection conn, CheckDataObject dataObject) throws SQLException {
        if (CheckDataObject.Type.PARTITION==dataObject.getType()) {
            PreparedStatement pstmt = conn.prepareCall(SQL_SOURCE_TYPE_JOB_PARTITION);
            pstmt.setString(1, dataObject.getDbName());
            pstmt.setString(2, dataObject.getTableName());
            pstmt.setString(3, dataObject.getPartitionName());
            return pstmt;
        } else {
            PreparedStatement pstmt = conn.prepareCall(SQL_SOURCE_TYPE_JOB_TABLE);
            pstmt.setString(1, dataObject.getDbName());
            pstmt.setString(2, dataObject.getTableName());
            return pstmt;
        }
    }

    /**
     * 构造查询maskis的查询
     */
    private PreparedStatement getBdpStatement(Connection conn, CheckDataObject dataObject, String timeScape) throws SQLException {
        PreparedStatement pstmt = null;
        if (timeScape.equals("NULL")) {
            pstmt = conn.prepareCall(SQL_SOURCE_TYPE_BDP);
        } else {
            pstmt = conn.prepareCall(SQL_SOURCE_TYPE_BDP_WITH_TIME_CONDITION);
            pstmt.setInt(4, Integer.valueOf(timeScape) * 3600);
        }
        if (dataObject.getPartitionName() == null) {
            dataObject.setPartitionName("");
        }
        pstmt.setString(1, dataObject.getDbName());
        pstmt.setString(2, dataObject.getTableName());
        pstmt.setString(3, dataObject.getPartitionName());
        return pstmt;
    }

    /**
     * 构造查询dops库的查询
     */
    private PreparedStatement getDopsStatement(Connection conn, CheckDataObject dataObject) throws SQLException {
        if (CheckDataObject.Type.PARTITION==dataObject.getType()) {
            PreparedStatement pstmt = conn.prepareCall(SQL_DOPS_CHECK_PARTITION);
            pstmt.setString(1, dataObject.getDbName());
            pstmt.setString(2, dataObject.getTableName());
            pstmt.setString(3, dataObject.getPartitionName());
            return pstmt;
        } else {
            PreparedStatement pstmt = conn.prepareCall(SQL_DOPS_CHECK_TABLE);
            pstmt.setString(1, dataObject.getDbName());
            pstmt.setString(2, dataObject.getTableName());
            return pstmt;
        }
    }

    /**
     * 构造查询dops库的查询，分区表全表校验场景
     */
    private PreparedStatement getDopsStatementCheckAllPartition(Connection conn, CheckDataObject dataObject) throws SQLException {
            PreparedStatement pstmt = conn.prepareCall(SQL_DOPS_CHECK_ALL_PARTITION);
            pstmt.setString(1, dataObject.getDbName());
            pstmt.setString(2, dataObject.getTableName());
            return pstmt;

    }


    /**
     * 反序列化检查对象
     * @param dataObjectStr 字符串形式的对象
     * @return 发序列化后的对象
     */
    private CheckDataObject parseDataObject(String dataObjectStr)throws SQLException{
        CheckDataObject dataObject;
        if(!dataObjectStr.contains(".")){
            throw new SQLException("Error for  DataObject format!"+dataObjectStr);
        }
        String dbName = dataObjectStr.split("\\.")[0];
        String tableName = dataObjectStr.split("\\.")[1];
        if (dataObjectStr.contains("{")) {
            String partitionName = "";
            Pattern pattern = Pattern.compile("\\{([^\\}]+)\\}");
            Matcher matcher = pattern.matcher(dataObjectStr);
            if (matcher.find()) {
                partitionName = matcher.group(1);
            }
            partitionName = partitionName.replace("\'", "").replace("\"", "");
            tableName = tableName.split("\\{")[0];
            dataObject = new CheckDataObject(dbName, tableName, partitionName);
        }else{
            dataObject=new CheckDataObject(dbName,tableName);
        }
        return dataObject;
    }
    /**
     * 查hive 元数据库
     */
    private long getJobTotalCount(CheckDataObject dataObject, Connection conn, Logger log) {
        log.info("-------------------------------------- search hive/spark/mr data ");
        log.info("-------------------------------------- dataObject: " + dataObject);
        try (PreparedStatement pstmt = getJobStatement(conn, dataObject)) {
            ResultSet rs = pstmt.executeQuery();
            long ret = rs.last() ? rs.getRow() : 0;
            log.info("-------------------------------------- hive/spark/mr data result:"+ret);
            return ret;
        } catch (SQLException e) {
            log.error("fetch data from Hive MetaStore error", e);
            return 0;
        }
    }

    /**
     * 查mask db
     */
    private long getBdpTotalCount(CheckDataObject dataObject, Connection conn, Logger log, Properties props) {
        String timeScape = props.getOrDefault(DataChecker.TIME_SCAPE, "NULL").toString();
        log.info("-------------------------------------- search bdp data ");
        log.info("-------------------------------------- dataObject: " + dataObject.toString());
        try (PreparedStatement pstmt = getBdpStatement(conn, dataObject, timeScape)) {
            ResultSet rs = pstmt.executeQuery();
            long ret=rs.last() ? rs.getRow() : 0;
            log.info("-------------------------------------- bdp data result:"+ret);
            return ret;
        } catch (SQLException e) {
            log.error("fetch data from bdp error", e);
            return 0;
        }
    }

    /**
     * - 返回0表示未找到任何记录 ；
     * - 返回1表示非分区表的全表校验场景找到了记录；
     * - 返回2表示分区表的分区校验场景找到了记录；
     * - 返回3表示分区表的全表校验场景找到了记录；
     * - 返回4表示查询出错了
     */
    private int checkDops(CheckDataObject dataObject, Connection conn, Logger log){
        log.info("-------------------------------------- search dops data ");
        log.info("-------------------------------------- dataObject: " + dataObject.toString());
        try (PreparedStatement pstmt = getDopsStatement(conn, dataObject)) {
            ResultSet rs = pstmt.executeQuery();
            long count = rs.last() ? rs.getRow() : 0;
            log.info("-------------------------------------- dops data check table or partition,count:"+count);
            if(count>0){
                return CheckDataObject.Type.PARTITION == dataObject.getType() ? 2 : 1;
            }else if(CheckDataObject.Type.PARTITION == dataObject.getType()){
                //分区校验没找到记录，直接返回0。
                return 0;
            }
        } catch (SQLException e) {
            log.error("fetch data from dops error while check table or partition", e);
            //如果查询出错，还是认为dops处理过这个表/分区
            return 4;
        }

        try(PreparedStatement pstmt = getDopsStatementCheckAllPartition(conn, dataObject)){
            ResultSet rs = pstmt.executeQuery();
            long count = rs.last() ? rs.getRow() : 0;
            log.info("-------------------------------------- dops data check all partition result count:"+count);
            if(count>0){
                return 3;
            }
        }catch (SQLException e) {
            log.error("fetch data from dops error while check all partition", e);
            //如果查询出错，还是认为dops处理过这个表/分区
            return 4;
        }
        return 0;
    }

    /**
     * 从qualitis去check数据
     */
    private boolean checkQualitisData(String objectNum,CheckDataObject dataObject, Logger log,
                                      DataCheckerExecutionAction action,Properties props,Connection conn,
                                      QualitisUtil qualitisUtil,String dataObjectStr  ) {
        boolean systemCheck = Boolean.valueOf(props.getProperty(DataChecker.QUALITIS_SWITCH));
        String userCheckDefault=props.getProperty(DataChecker.QUALITIS_CHECK_DEFAULT);
        String userCheckStr = StringUtils.isBlank(props.getProperty(DataChecker.QUALITIS_CHECK)) ?
                userCheckDefault :
                props.getProperty(DataChecker.QUALITIS_CHECK);
        boolean userCheck = Boolean.valueOf(userCheckStr);
        log.info("systemCheck:{},userCheckDefault:{},userCheck:{}",systemCheck,userCheckDefault,userCheck);
        if (systemCheck && userCheck ) {
            if (null != action.getExecutionRequestRefContext()){
                action.getExecutionRequestRefContext().appendLog(dataObjectStr+" start to check dops");
            }
            log.info("start to check dops");
            int dopsState=checkDops(dataObject,conn,log);
            if (null != action.getExecutionRequestRefContext()){
                action.getExecutionRequestRefContext().appendLog(dataObjectStr+" check dops end,check result:"+dopsState);
            }
            log.info("check dops end,check result:"+dopsState);
            if(dopsState==0){
                //没找到记录，直接通过校验
                return true;
            } else if (dopsState == 3 || dopsState == 4) {
                //找记录失败、或者是找到了分区表的全表校验记录，直接校验不通过。
                return false;
            }
            // 其他情况，继续走qualitis校验
            if (null != action.getExecutionRequestRefContext()){
                action.getExecutionRequestRefContext().appendLog(dataObjectStr+" Data Check Qualitis Start");
            }
            log.info(
                    "=============================Data Check Qualitis Start==========================================");
            try {
                String projectName = props.getProperty(DataChecker.CONTEXTID_PROJECT_NAME);
                String user = props.getProperty(DataChecker.CONTEXTID_USER);
                String flowName = props.getProperty(DataChecker.CONTEXTID_FLOW_NAME);
                String nodeName=props.getProperty(DataChecker.NAME_NAME);

                String ruleName = getMD5Str(projectName + flowName + nodeName + objectNum);
                String applicationId = qualitisUtil
                        .createAndSubmitRule(dataObject,projectName,ruleName,user);
                if (StringUtils.isEmpty(applicationId)) {
                    throw new SQLException("applicationId is empty");
                }
                long startTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - startTime < Integer
                        .parseInt(props.getProperty("qualitis.getStatus.all.timeout"))) {
                    int status = new BigDecimal(qualitisUtil.getTaskStatus(applicationId)).intValue();
                    switch (status) {
                        case 1:
                        case 3:
                        case 10:
                        case 12:
                            try {
                                Thread
                                        .sleep(Double.valueOf(props.getProperty("qualitis.getStatus.interval")).longValue());
                            } catch (InterruptedException e) {
                                log.error("get datachecker result from qualitis InterruptedException", e);
                            }
                            break;
                        case 4:
                            if (null != action.getExecutionRequestRefContext()){
                                action.getExecutionRequestRefContext().appendLog(dataObjectStr+" check qualitis end,check result:true");
                            }
                            log.info("check qualitis end,check result:true");
                            return true;
                        default:
                            if (null != action.getExecutionRequestRefContext()){
                                action.getExecutionRequestRefContext().appendLog(dataObjectStr+" check qualitis end,check result:false");
                            }
                            log.info("check qualitis end,check result:false");
                            return false;
                    }

                }
                if (null != action.getExecutionRequestRefContext()){
                    action.getExecutionRequestRefContext().appendLog(dataObjectStr+" Data Check Qualitis time out,check result set to false");
                }
                log.info(
                        "=============================Data Check Qualitis time out,check result set to false==========================================");
                return false;
            } catch (Exception e) {
                if (null != action.getExecutionRequestRefContext()){
                    action.getExecutionRequestRefContext().appendLog(dataObjectStr+" check qualitis failed,check result set to false.cause by:"+e.getMessage());
                }
                log.error("get datachecker result from qualitis failed", e);
                return false;
            }

        } else {
            return true;
        }
    }
    public static String getMD5Str(String str){
        return DigestUtils.md5Hex(str);
    }

    private Map<String, String> fetchMaskCode(CheckDataObject dataObject, Logger log, Properties props) {
        log.info("=============================调用BDP MASK接口查询数据状态==========================================");
        Map<String, String> resultMap = new HashMap();
        String maskUrl = props.getProperty(DataChecker.MASK_URL);
        String dbName = dataObject.getDbName();
        String tableName = dataObject.getTableName();
        String partitionName = dataObject.getPartitionName() == null ? "" : dataObject.getPartitionName();
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
