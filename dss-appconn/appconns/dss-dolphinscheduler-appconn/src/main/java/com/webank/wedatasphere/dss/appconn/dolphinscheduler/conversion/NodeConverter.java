package com.webank.wedatasphere.dss.appconn.dolphinscheduler.conversion;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.conf.DolphinSchedulerConf;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils.OkHttpHelper;
import com.webank.wedatasphere.dss.common.entity.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.constant.Constant;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.entity.DolphinSchedulerTask;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.entity.DolphinSchedulerTaskParam;
import com.webank.wedatasphere.dss.common.entity.node.DSSNode;

/**
 * The type Node converter.
 *
 * @author yuxin.yuan
 * @date 2021/04/29
 */
public class NodeConverter {

    private static final Logger logger = LoggerFactory.getLogger(NodeConverter.class);

    public NodeConverter() {
    }

    /**
     * 将DSS中节点转为Dolphin Scheduler中节点task.
     * <p>
     * the scheduler node
     *
     * @return the dolphin scheduler task
     */
    public DolphinSchedulerTask conversion(DSSNode dssNode) {
        DolphinSchedulerTask task = new DolphinSchedulerTask();
        task.setId(dssNode.getId());
        task.setName(dssNode.getName());
        task.setPreTasks(dssNode.getDependencys());
        task.setType("SHELL");
        DolphinSchedulerTaskParam taskParams = new DolphinSchedulerTaskParam();
        String jobExecuteType = DolphinSchedulerConf.JOB_EXECUTE_TYPE.getValue().trim().toLowerCase();

        try {
            String taskType = dssNode.getNodeType();
            if ("linkis".equals(jobExecuteType)) {
                //linkis方式执行作业
                HashMap<String, String> map = new HashMap<>();
                map.put(Constant.JOB_TYPE, "linkis");
                map.put(Constant.LINKIS_TYPE, dssNode.getNodeType());
                map.put(Constant.PROXY_USER, dssNode.getUserProxy());
                map.put(Constant.JOB_COMMAND, new Gson().toJson(dssNode.getJobContent()));
                map.put("params", new Gson().toJson(dssNode.getParams()));
                map.put("resources", new Gson().toJson(dssNode.getResources()));
                Map<String, Object> nodeParams = dssNode.getParams();
                if (nodeParams != null && !nodeParams.isEmpty()) {
                    Object configuration = nodeParams.get("configuration");
                    String confprefix = "node.conf.";
                    ((Map<String, Map<String, Object>>)configuration)
                            .forEach((k, v) -> v.forEach((k2, v2) -> map.put(confprefix + k + "." + k2, v2.toString())));
                }

                // TODO 改为参数配置路径
                String dolphinScript =
                        "java -jar /usr/local/dolphin/linkis-dolphinscheduler-client.jar '" + new Gson().toJson(map) + "'";
                taskParams.setRawScript(dolphinScript);
            } else if ("dolphin".equals(jobExecuteType)) {
                //cli客户端执行作业
                Resource resource = dssNode.getResources().get(0);
                String fileName = resource.getFileName();
                String resourceId = resource.getResourceId();
                String version = resource.getVersion();
                String requestParam = "/api/rest_j/v1/filesystem/openScriptFromBML?fileName=" + fileName + "&resourceId=" + resourceId + "&version=" + version + "&creator=hadoop" + "&projectName=";
                String openFileUrl = DolphinSchedulerConf.NGINX_URL.getValue() + requestParam;
                logger.info(">>>>>>openFileur" + openFileUrl);
                Request getRequest = new Request.Builder()
                        .url(openFileUrl)
                        .addHeader("Content-Type", "application/json")
                        .get()
                        .build();
                Response response = OkHttpHelper.syncGet(getRequest);
                logger.info("open file request: ", response);
                JsonObject returnData = new JsonParser().parse(response.body().string()).getAsJsonObject();
                logger.info("open file request: ", returnData);
                String scriptContent = returnData.getAsJsonObject("data").get("scriptContent").getAsString();
                StringBuffer contentBuffer = new StringBuffer();
                String executorMemory = "1G";
                String executorCores = "2";
                String executorInstances = "3";
                String driverMemory = "512M";
                String driverCores = "3";
                String queueName = "default";
                String proxyUser = dssNode.getUserProxy();

//                    {\"spark.driver.memory\":\"5\",\"spark.executor.memory\":\"5\",\"spark.executor.cores\":\"6\",\"spark.executor.instances\":\"6\",\"wds.linkis.rm.yarnqueue\
                Map<String, Object> nodeParams = dssNode.getParams();
                if (nodeParams != null && !nodeParams.isEmpty()) {
                    Map<String, Map<String, Object>> configuration = (Map<String, Map<String, Object>>) nodeParams.get("configuration");
                    Map<String, Object> startParas = configuration.get("startup");
                    if (startParas != null && !startParas.isEmpty()) {
                        executorMemory = startParas.get("spark.executor.memory") != null ? startParas.get("spark.executor.memory").toString() + "G" : executorMemory;
                        executorCores = startParas.get("spark.executor.cores") != null ? startParas.get("spark.executor.cores").toString() : executorCores;
                        executorInstances = startParas.get("spark.executor.instances") != null ? startParas.get("spark.executor.instances").toString() : executorInstances;
                        driverMemory = startParas.get("spark.driver.memory") != null ? startParas.get("spark.driver.memory").toString() + "G" : driverMemory;
                        driverCores = startParas.get("spark.driver.cores") != null ? startParas.get("spark.driver.cores").toString() : driverCores;
                        queueName = startParas.get("wds.linkis.rm.yarnqueue") != null ? startParas.get("wds.linkis.rm.yarnqueue").toString() : queueName;
                    }
                }

                if ("linkis.shell.sh".equals(taskType)) {
//                    contentBuffer.append("source /etc/profile").append("\n");
                    contentBuffer.append(scriptContent);
                    taskParams.setRawScript(contentBuffer.toString());
                } else if ("linkis.hive.hql".equals(taskType)) {
//                    contentBuffer.append("source /etc/profile").append("\n");
//                    beeline -u "jdbc:hive2://10.30.33.24:10000/default;principal=hive/nm-bigdata-030033024.ctc.local@EWS.BIGDATA.CHINATELECOM.CN.UAT;hive.server2.proxy.user=luban_test" -e
                    contentBuffer.append("kinit -kt " + DolphinSchedulerConf.DS_DOLPHIN_KERBEROS_KEYTAB.getValue() + " " + DolphinSchedulerConf.DS_DOLPHIN_KERBEROS_PRINCIPALS.getValue() + "\n");
                    contentBuffer.append("beeline -u");
                    contentBuffer.append(" \"");
                    contentBuffer.append(DolphinSchedulerConf.DS_HIVE_SERVER2_URL.getValue()+";");
                    contentBuffer.append("hive.server2.proxy.user="+proxyUser+"\"");
                    contentBuffer.append(" -e \"\n");
                    contentBuffer.append(scriptContent);
                    contentBuffer.append("\n\"");
                    taskParams.setRawScript(contentBuffer.toString());
                } else if ("linkis.spark.sql".equals(taskType)) {
//                    contentBuffer.append("source /etc/profile").append("\n");
                    contentBuffer.append("kinit -kt " + DolphinSchedulerConf.DS_DOLPHIN_KERBEROS_KEYTAB.getValue() + " " + DolphinSchedulerConf.DS_DOLPHIN_KERBEROS_PRINCIPALS.getValue() + "\n");
                    contentBuffer.append("spark-sql --master yarn --deploy-mode client");
                    contentBuffer.append(" --executor-memory " + executorMemory);
                    contentBuffer.append(" --num-executors " + executorInstances);
                    contentBuffer.append(" --executor-cores " + executorCores);
                    contentBuffer.append(" --driver-memory " + driverMemory);
                    contentBuffer.append(" --driver-cores " + driverCores);
                    contentBuffer.append(" --proxy-user " + proxyUser);
                    contentBuffer.append(" --queue " + queueName);
                    contentBuffer.append(" --name " + proxyUser + "_spark-sql");
                    contentBuffer.append(" -e \"\n");
                    contentBuffer.append(scriptContent)
                            .append("\n\"");
                    taskParams.setRawScript(contentBuffer.toString());
                } else if ("linkis.spark.scala".equals(taskType)) {
//                    contentBuffer.append("source /etc/profile").append("\n");
                    contentBuffer.append("kinit -kt " + DolphinSchedulerConf.DS_DOLPHIN_KERBEROS_KEYTAB.getValue() + " " + DolphinSchedulerConf.DS_DOLPHIN_KERBEROS_PRINCIPALS.getValue() + "\n");
                    contentBuffer.append("spark-shell --master yarn");
                    contentBuffer.append(" --executor-memory " + executorMemory);
                    contentBuffer.append(" --num-executors " + executorInstances);
                    contentBuffer.append(" --executor-cores " + executorCores);
                    contentBuffer.append(" --driver-memory " + driverMemory);
                    contentBuffer.append(" --driver-cores " + driverCores);
                    contentBuffer.append(" --proxy-user " + proxyUser);
                    contentBuffer.append(" --queue " + queueName);
                    contentBuffer.append(" --name " + proxyUser + "_spark-shell");
                    contentBuffer.append(" <<EOF\n");
                    contentBuffer.append(":paste\n");
                    contentBuffer.append("try{\n");
                    contentBuffer.append(scriptContent);
                    contentBuffer.append("\n}catch {\n");
                    contentBuffer.append("case ex: Exception => \n");
                    contentBuffer.append("  ex.printStackTrace();System.exit(-1) }\n");
                    contentBuffer.append("EOF");
                    taskParams.setRawScript(contentBuffer.toString());
                } else if ("linkis.python.python".equals(taskType)) {
                    task.setType("PYTHON");
                    contentBuffer.append(scriptContent);
                    taskParams.setRawScript(contentBuffer.toString());

                } else if ("linkis.spark.py".equals(taskType)) {
//                    contentBuffer.append("source /etc/profile").append("\n");
                    contentBuffer.append("kinit -kt " + DolphinSchedulerConf.DS_DOLPHIN_KERBEROS_KEYTAB.getValue() + " " + DolphinSchedulerConf.DS_DOLPHIN_KERBEROS_PRINCIPALS.getValue() + "\n");
                    contentBuffer.append("pyspark --master yarn");
                    contentBuffer.append(" --executor-memory " + executorMemory);
                    contentBuffer.append(" --num-executors " + executorInstances);
                    contentBuffer.append(" --executor-cores " + executorCores);
                    contentBuffer.append(" --driver-memory " + driverMemory);
                    contentBuffer.append(" --driver-cores " + driverCores);
                    contentBuffer.append(" --proxy-user " + proxyUser);
                    contentBuffer.append(" --queue " + queueName);
                    contentBuffer.append(" --name " + proxyUser + "_pyspark");
                    contentBuffer.append(" <<EOF\n");
                    contentBuffer.append("from pyspark.sql import SparkSession\n");
                    contentBuffer.append("spark = SparkSession.builder.appName('" + proxyUser + "_pyspark" + "').getOrCreate()\n");
                    contentBuffer.append(scriptContent + "\n");
                    contentBuffer.append("EOF");
                    taskParams.setRawScript(contentBuffer.toString());
                }

                logger.info(">>>>>>script", contentBuffer.toString());
            }

        } catch (Exception e) {
            logger.error("任务转换失败", e);
        }

        task.setParams(taskParams);
        return task;
    }

}
