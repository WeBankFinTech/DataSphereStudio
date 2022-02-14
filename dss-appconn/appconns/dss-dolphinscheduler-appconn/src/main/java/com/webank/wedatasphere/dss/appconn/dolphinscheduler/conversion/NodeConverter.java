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
     *
     *            the scheduler node
     * @return the dolphin scheduler task
     */
    public DolphinSchedulerTask conversion(DSSNode dssNode) {
        DolphinSchedulerTask task = new DolphinSchedulerTask();
        task.setId(dssNode.getId());
        task.setName(dssNode.getName());
        task.setPreTasks(dssNode.getDependencys());
        task.setType("SHELL");
        task.setTenantCode("hadoop");
task.setTenantId(8);
        DolphinSchedulerTaskParam taskParams = new DolphinSchedulerTaskParam();
        String jobExecuteType = DolphinSchedulerConf.JOB_EXECUTE_TYPE.getValue().trim().toLowerCase();

        try {
            String taskType = dssNode.getNodeType();


            if("linkis".equals(jobExecuteType)){
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
            }else if("dolphin".equals(jobExecuteType)){
                //cli客户端执行作业
               Resource resource = dssNode.getResources().get(0);
               String fileName = resource.getFileName();
               String resourceId = resource.getResourceId();
               String  version = resource.getVersion();


               String requestParam = "/api/rest_j/v1/filesystem/openScriptFromBML?fileName="+fileName+"&resourceId="+resourceId+"&version="+version+"&creator=hadoop"+"&projectName=";
               String openFileUrl = DolphinSchedulerConf.NGINX_URL.getValue()+requestParam;
               logger.info(">>>>>>openFileur"+openFileUrl);

                Request getRequest = new Request.Builder()
                        .url(openFileUrl)
                        .addHeader("Content-Type", "application/json")
                        .get()
                        .build();
                Response response = OkHttpHelper.syncGet(getRequest);
                logger.info("open file request: ",response);

                JsonObject returnData = new JsonParser().parse(response.body().string()).getAsJsonObject();
                logger.info("open file request: ",returnData);
                String scriptContent = returnData.getAsJsonObject("data").get("scriptContent").getAsString();

//                String taskType = dssNode.getNodeType();
//                linkis.spark.sql
//                linkis.spark.scala
//                linkis.hive.hql
//                linkis.shell.sh
//                linkis.python.python
//                linkis.spark.py
                StringBuffer contentBuffer = new StringBuffer();

                if("linkis.shell.sh".equals(taskType)){
                    contentBuffer.append("source /etc/profile").append("\n");
                    contentBuffer.append(scriptContent);
                    taskParams.setRawScript(contentBuffer.toString());
                }else if ("linkis.hive.hql".equals(taskType)){
                    contentBuffer.append("source /etc/profile").append("\n");
                    contentBuffer.append("hive -e \"\n ")
                                 .append(scriptContent)
                                  .append("\n\"");
                    taskParams.setRawScript(contentBuffer.toString());
                }else if("linkis.spark.sql".equals(taskType)){
                    contentBuffer.append("source /etc/profile").append("\n");
                    contentBuffer.append("spark-sql   --executor-memory 512m --master yarn --total-executor-cores 1 -e \"\n")
                                 .append(scriptContent)
                                 .append("\n\"");
                    taskParams.setRawScript(contentBuffer.toString());
                }else if("linkis.spark.scala".equals(taskType)){
                    contentBuffer.append("source /etc/profile").append("\n");
                    contentBuffer.append("spark-shell -- master yarn-client -- executor-memory 512m -- num-executors 1 <<EOF\n")
                                  .append(":paste\n")
                                  .append("try{\n")
                                  .append(scriptContent)
                                  .append("\n}catch {\n")
                                  .append("case ex: Exception => \n")
                                  .append("  ex.printStackTrace();System.exit(-1) }\n")
                                  .append("EOF");
                    taskParams.setRawScript(contentBuffer.toString());
                }else if("linkis.python.python".equals(taskType)){
                    task.setType("PYTHON");
                    contentBuffer.append(scriptContent);
                    taskParams.setRawScript(contentBuffer.toString());

                }else if("linkis.spark.py".equals(taskType)){

                    contentBuffer.append("source /etc/profile").append("\n");
                    contentBuffer.append("pyspark --master yarn -- executor-memory 512m -- num-executors 1 <<EOF\n");
                    contentBuffer.append("from pyspark.sql import SparkSession\n");
                    contentBuffer.append("spark = SparkSession.builder.appName('pyspark').getOrCreate()\n");
                    contentBuffer.append(scriptContent+"\n");
                    contentBuffer.append("EOF");
                    taskParams.setRawScript(contentBuffer.toString());
                }

                logger.info(">>>>>>script",contentBuffer.toString());


            }

        } catch (Exception e) {
            logger.error("任务转换失败", e);
        }

        task.setParams(taskParams);
        return task;
    }

}
