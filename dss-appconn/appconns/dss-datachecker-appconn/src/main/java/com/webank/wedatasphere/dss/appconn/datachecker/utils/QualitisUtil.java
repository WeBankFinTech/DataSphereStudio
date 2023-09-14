package com.webank.wedatasphere.dss.appconn.datachecker.utils;


import com.webank.wedatasphere.dss.appconn.datachecker.DataChecker;
import com.webank.wedatasphere.dss.appconn.datachecker.common.CheckDataObject;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import okhttp3.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Tools for qualitis
 *
 * @author lebronwang
 * @date 2022/08/04
 **/
public class QualitisUtil {

  private static Logger logger = LoggerFactory.getLogger(QualitisUtil.class);

  String SUBMIT_TASK_PATH = "";
  private final static String  GET_TASK_STATUS_PATH = "qualitis/outer/api/v1/application/{applicationId}/status/";
  private final static String createSubmitRulePath = "qualitis/outer/api/v1/bdp_client/create_and_submit";
  String baseUrl = "";
  String appId = "";
  String appToken = "";
  long getStatusTimeout;
  private Properties properties;



  public QualitisUtil(Properties properties) {
    this.properties = properties;
    this.baseUrl = this.properties.getProperty("qualitis.baseUrl");
    this.appId = this.properties.getProperty("qualitis.appId");
    this.appToken = this.properties.getProperty("qualitis.appToken");
    this.getStatusTimeout =Double
        .valueOf(this.properties.getProperty("qualitis.getStatus.timeout", "60000")).longValue();
  }

  /**
   * 提交 Qualitis 任务
   *
   * @param groupId       规则组 ID
   * @param createUser
   * @param executionUser
   * @return
   * @throws IOException
   */
  public String submitTask(long groupId, String createUser, String executionUser)
      throws Exception {

    logger.info("Submitting Qualitis task(groupId: " + groupId + ") ... ");
    String applicationId = "";
    String url = "";
    try {
      url = buildUrI(baseUrl, SUBMIT_TASK_PATH, appId, appToken,
          RandomStringUtils.randomNumeric(5), String.valueOf(System.currentTimeMillis()));
    } catch (NoSuchAlgorithmException | URISyntaxException e) {
      logger.error("Build Qualitis URL error: " + e.getMessage());
    }

    Map<String, Object> param = new HashMap<>();
    param.put("group_id", groupId);
    param.put("create_user", createUser);
    param.put("execution_user", executionUser);

    String json = DSSCommonUtils.COMMON_GSON.toJson(param);
    logger.info("Request Json: " + json);
    MediaType applicationJson = MediaType.parse("application/json;charset=utf-8");
    RequestBody requestBody = RequestBody.create(json, applicationJson);

    OkHttpClient okHttpClient = new OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build();

    Request request = new Request.Builder()
        .url(url)
        .post(requestBody)
        .build();

    Call call = okHttpClient.newCall(request);
    Response response = call.execute();
    String resultJson = response.body().string();
    logger.info("Response Json: " + resultJson);
    if (StringUtils.isNotEmpty(resultJson)) {
      Map<String, Object> resultMap = DSSCommonUtils.COMMON_GSON.fromJson(resultJson,Map.class);
      logger.info(String.valueOf(resultMap));
      String code = (String) resultMap.get("code");
      if ("200".equals(code)) {
        applicationId = (String) ((Map<String, Object>) resultMap.get("data")).get(
            "application_id");
        logger.info(resultMap.get("message").toString());
      } else {
        throw new RuntimeException(resultMap.get("message").toString());
      }
    }
    return applicationId;
  }

  public String createAndSubmitRule(CheckDataObject dataObject,String projectName,String ruleName,String user) throws IOException {
    logger.info("");
    String applicationId = "";
    String url = "";
    try {
      url = buildUrI(baseUrl, createSubmitRulePath, appId, appToken,
          RandomStringUtils.randomNumeric(5), String.valueOf(System.currentTimeMillis()));
    } catch (NoSuchAlgorithmException | URISyntaxException e) {
      logger.error("Build Qualitis URL error: " + e.getMessage());
    }

    // JSON 请求参数
    Map<String, Object> param = new HashMap<>();
    param.put("create_user", user);
    param.put("execution_user",user);
    // 集群名
    String clusterName = properties.getProperty("cluster.name");
    // 子系统名 WTSS-BDPWFM/WTSS-BDAPWFM
    StringBuilder sb = new StringBuilder("expectFileAmountCount(\"").append(clusterName)
        .append(".").append(dataObject.getDbName())
        .append(".").append(dataObject.getTableName())
        .append(dataObject.getType()== CheckDataObject.Type.PARTITION? ":" + dataObject.getPartitionName() : "")
        .append("\", null, false).addRuleMetricWithCheck(\"")
        .append(String.format(properties.getProperty("qualitis.rule.metric"),
                ruleName))
        .append("\", false, false, false).fixValueNotEqual(0)");
    logger.info("template_function:{}",sb);
    param.put("template_function", sb.toString());
    param.put("project_name", projectName);
    param.put("rule_name", ruleName);

    String json = DSSCommonUtils.COMMON_GSON.toJson(param);
    logger.info("start to call qualitis,url:{} ,Request Json:{} ", url, json );
    MediaType applicationJson = MediaType.parse("application/json;charset=utf-8");
    RequestBody requestBody = RequestBody.create(json, applicationJson);

    OkHttpClient okHttpClient = new OkHttpClient.Builder()
        .callTimeout(Double.valueOf(properties.getProperty("qualitis.submitTask.timeout")).longValue(), TimeUnit.MILLISECONDS)
        .connectTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build();

    Request request = new Request.Builder()
        .url(url)
        .post(requestBody)
        .build();

    Call call = okHttpClient.newCall(request);
    Response response = call.execute();
    String resultJson = response.body().string();
    logger.info("call qualitis end,Response Json:{} " , resultJson);
    if (StringUtils.isNotEmpty(resultJson)) {
      Map<String, Object> resultMap = DSSCommonUtils.COMMON_GSON.fromJson(resultJson,Map.class);
      String code = (String) resultMap.get("code");
      if ("200".equals(code)) {
        Map<String, Object> applicationDetail = (Map<String, Object>) ((Map<String, Object>) (resultMap.get(
            "data"))).get("application_detail");
        Map<String, Object> project_application = ((List<Map<String, Object>>) (applicationDetail.get(
            "project_applications"))).get(0);
        applicationId = (String) project_application.get("application_id");
        logger.info(resultMap.get("message").toString());
      } else {
        throw new RuntimeException(resultMap.get("message").toString());
      }
    }
    return applicationId;
  }

  /**
   * 获取 Qualitis 任务状态
   *
   * @param applicationId
   * @return
   */
  public String getTaskStatus(String applicationId){

    logger.info("Getting Qualitis task status(ApplicationId: " + applicationId + ") ... ");
    if (StringUtils.isEmpty(applicationId)) {
      logger.error("Empty Qualitis application ID");
    }
    String url = "";
    try {
      url = buildUrI(baseUrl, GET_TASK_STATUS_PATH.replace("{applicationId}", applicationId)
          , appId, appToken, RandomStringUtils.randomNumeric(5),
          String.valueOf(System.currentTimeMillis()));
    } catch (NoSuchAlgorithmException | URISyntaxException e) {
      logger.error("Build Qualitis URL error: " + e.getMessage());
    }

    OkHttpClient okHttpClient = new OkHttpClient.Builder()
        .callTimeout(this.getStatusTimeout, TimeUnit.MILLISECONDS)
        .connectTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build();

    Request request = new Request.Builder()
        .url(url)
        .get()
        .build();

    Call call = okHttpClient.newCall(request);
    Response response = null;
    String resultJson = "";
    String status = "";
    try {
      response = call.execute();
      resultJson = response.body().string();
      logger.info("Response Json: " + resultJson);
      if (StringUtils.isNotEmpty(resultJson)) {
        Map<String, Object> resultMap = DSSCommonUtils.COMMON_GSON.fromJson(resultJson,Map.class);
        String code = (String) resultMap.get("code");
        if ("200".equals(code)) {
          status = ((Map<String, Object>) (resultMap.get("data"))).get(
              "application_status") + "";
        }
      }
    } catch (IOException e) {
      logger.error("Request to Qualitis failed: " + e.getMessage());
    }

    return status;
  }

  public static String buildUrI(String baseUrl, String path, String appId, String appToken,
      String nonce, String timestamp) throws NoSuchAlgorithmException, URISyntaxException {
    String signature = getSignature(appId, appToken, nonce, timestamp);
    StringBuilder uriBuffer = new StringBuilder(baseUrl);
    uriBuffer.append(path).append("?")
        .append("app_id=").append(appId).append("&")
        .append("nonce=").append(nonce).append("&")
        .append("timestamp=").append(timestamp).append("&")
        .append("signature=").append(signature);

    return uriBuffer.toString();
  }

  public static String getSignature(String appId, String appToken, String nonce, String timestamp)
      throws NoSuchAlgorithmException {
    return Sha256Utils.getSHA256L32(Sha256Utils.getSHA256L32(appId + nonce + timestamp) + appToken);
  }

}
