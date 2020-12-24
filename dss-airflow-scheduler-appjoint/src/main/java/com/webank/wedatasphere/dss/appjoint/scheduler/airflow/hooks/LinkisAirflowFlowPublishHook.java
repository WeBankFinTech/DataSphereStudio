package com.webank.wedatasphere.dss.appjoint.scheduler.airflow.hooks;


import com.google.gson.Gson;
import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.conf.AirflowConf;
import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.entity.LinkisAirflowSchedulerNode;
import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.linkisjob.LinkisJobConverter;
import com.webank.wedatasphere.dss.appjoint.scheduler.entity.SchedulerNode;
import com.webank.wedatasphere.dss.appjoint.scheduler.hooks.AbstractFlowPublishHook;
import com.webank.wedatasphere.dss.appjoint.scheduler.hooks.NodePublishHook;
import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.constant.AirflowConstant;
import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.entity.AirflowSchedulerFlow;
import com.webank.wedatasphere.dss.common.entity.Resource;
import com.webank.wedatasphere.dss.appjoint.scheduler.entity.SchedulerFlow;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Xudong Zhang on 2020/8/6.
 */

public class LinkisAirflowFlowPublishHook extends AbstractFlowPublishHook {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinkisAirflowFlowPublishHook.class);
    public static final Set<String> AIRFLOW_INTERVAL_CANDIDATE_SET = new HashSet<>(Arrays.asList(
            "once", "hourly", "daily", "weekly", "monthly", "yearly"
    ));
    private LinkisJobConverter linkisJobConverter;

    public LinkisAirflowFlowPublishHook(){
        NodePublishHook[] nodePublishHooks = {new LinkisAirflowNodePublishHook()};
        setNodeHooks(nodePublishHooks);
        this.linkisJobConverter = new LinkisJobConverter();
    }

    @Override
    public void prePublish(SchedulerFlow flow) throws DSSErrorException {
        // 处理资源生成文件等等
        writeFlowResourcesToLocal(flow);
        writeFlowPropertiesToLocal(flow);
        writeFlowDagToLocal(flow);
        super.prePublish(flow);
    }

    private void writeFlowResourcesToLocal(SchedulerFlow flow) throws DSSErrorException {
        List<Resource> flowResources = flow.getFlowResources();
        FileOutputStream os = null;
        try {
            String storePath = ((AirflowSchedulerFlow)flow).getStorePath();
            File flowDir = new File(storePath);
            FileUtils.forceMkdir(flowDir);
            if(flowResources == null || flowResources.isEmpty()) {return;}
            String projectStorePath = getProjectStorePath(storePath);
            String flowResourceStringPrefix = getFlowResourceStringPrefix(projectStorePath,storePath);
            String flowtResourceString = flowResourceStringPrefix + new Gson().toJson(flowResources) + "\n";
            File projectResourcesFile = new File(projectStorePath, "project.properties");
            os = FileUtils.openOutputStream(projectResourcesFile,true);
            os.write(flowtResourceString.getBytes());
        }catch (Exception e){
            LOGGER.error("write FlowResources to local failed,reason:",e);
            throw new DSSErrorException(90006,e.getMessage());
        }finally {
            IOUtils.closeQuietly(os);
        }
    }

    private String getFlowResourceStringPrefix(String projectStorePath, String storePath) {
        // TODO: 2019/9/30 需要做用户工作流命名为subFlows的情况的判断
        String substring = storePath.substring(projectStorePath.length() + 1);
        String prefix = substring.replaceAll("\\" + File.separator +"subFlows" + "\\" + File.separator,".");
        return "flow." + prefix + "_.resources=";
    }

    private String getProjectStorePath(String storePath) {
        int indexOf = storePath.indexOf("subFlows");
        if(indexOf != -1){
            storePath = storePath.substring(0, indexOf-1);
        }
        return storePath.substring(0, storePath.lastIndexOf(File.separator));
    }

    private String getProjectName(String storePath) {
        String projectStorePath = getProjectStorePath(storePath);
        if (projectStorePath.endsWith(File.separator)) {
            projectStorePath = projectStorePath.substring(0, projectStorePath.length() - 1);
        }
        projectStorePath = projectStorePath.substring(projectStorePath.lastIndexOf(File.separator) + 1);
        return projectStorePath;
    }

    private void writeFlowPropertiesToLocal(SchedulerFlow flow)throws DSSErrorException {
        List<Map<String, Object>> flowProperties = flow.getFlowProperties();
        if(flowProperties == null || flowProperties.isEmpty()) {return;}
        FileOutputStream os = null;
        try {
            String storePath = ((AirflowSchedulerFlow)flow).getStorePath();
            File flowPrpsFile = new File(storePath,flow.getName() + AirflowConstant.AIRFLOW_PROPERTIES_SUFFIX);
            flowPrpsFile.createNewFile();
            os = FileUtils.openOutputStream(flowPrpsFile,true);
            StringBuilder stringBuilder = new StringBuilder();
            flowProperties.forEach(p ->p.forEach((k,v)->{
                stringBuilder.append(AirflowConstant.LINKIS_FLOW_VARIABLE_KEY + k + "=" + v + "\n");
            }));
            os.write(stringBuilder.toString().getBytes());
        }catch (Exception e){
            LOGGER.error("write flowProperties to local failed,reason:",e);
            throw new DSSErrorException(90007,e.getMessage());
        }finally {
            IOUtils.closeQuietly(os);
        }
    }

    private void writeFlowDagToLocal(SchedulerFlow flow)throws DSSErrorException {
        FileOutputStream os = null;
        try {
            String dagContent = genAirflowDagCode(flow);
            String storePath = ((AirflowSchedulerFlow)flow).getStorePath();
            String projectStorePath = getProjectName(storePath);
            String scheduleName = String.format(AirflowConstant.AIRFLOW_DAG_NAME_FORMAT, projectStorePath, flow.getId());
            File flowDagFile = new File(storePath,scheduleName + AirflowConstant.PYTHON_FILE_NAME_SUFFIX);
            LOGGER.info("Writing dag content to file {}", flowDagFile);
            flowDagFile.createNewFile();
            os = FileUtils.openOutputStream(flowDagFile,false); // overwrite
            os.write(dagContent.getBytes());
        }catch (Exception e){
            LOGGER.error("write flow dag to local failed,reason:",e);
            throw new DSSErrorException(90024,e.getMessage());
        }finally {
            IOUtils.closeQuietly(os);
        }
    }


    @Override
    public void postPublish(SchedulerFlow flow) {
        super.postPublish(flow);
    }


    @Override
    public void setNodeHooks(NodePublishHook[] nodePublishHooks) {
        super.setNodeHooks(nodePublishHooks);
    }

    private String genAirflowDagCode(SchedulerFlow flow) throws DSSErrorException {
        List<String> nodeDefList = new ArrayList<>();
        String storePath = ((AirflowSchedulerFlow)flow).getStorePath();
        String projectName = getProjectName(storePath);
        String scheduleName = String.format(AirflowConstant.AIRFLOW_DAG_NAME_FORMAT, projectName, flow.getId());

        String linkisAirflowClientLogPropertiesPath = String.format("file://%s/conf/log4j.properties",
                AirflowConf.LINKIS_AIRFLOW_CLIENT_BASE_PATH.getValue());
        String linkisAirflowClientJavaClassPath = String.format(".:%s/conf/*:%s/lib/*",
                AirflowConf.LINKIS_AIRFLOW_CLIENT_BASE_PATH.getValue(),
                AirflowConf.LINKIS_AIRFLOW_CLIENT_BASE_PATH.getValue());
        LOGGER.info("Generating dag {}", scheduleName);
        StringBuilder tmpBuf = new StringBuilder();

        tmpBuf.setLength(0);
        List<Map<String, Object>> flowProperties = flow.getFlowProperties();
        if (flowProperties != null && !flowProperties.isEmpty()) {
            flowProperties.forEach(p ->p.forEach((k,v)->{
                tmpBuf.append(String.format(" \"%s%s=%s\"", AirflowConstant.LINKIS_FLOW_VARIABLE_KEY, k,
                        v.toString().replace("'", "\\'").replace("\"", "\\\"")));
            }));
        }
        String bashFlowVariablesStr = tmpBuf.toString();

        for (SchedulerNode node: flow.getSchedulerNodes()) {
            LOGGER.info("Generating node def for node {}", node.getName());
            if (node.getName().endsWith(AirflowConstant.FLOW_DUMMY_START_NODE_SUFFIX)) {
                nodeDefList.add(genDummyTask(projectName, scheduleName, flow, node, "  ", tmpBuf));
//                nodeDefList.add(TemplatingClient.getInstance().render("common/dummy.ftl",
//                        ImmutableMap.<String, Object>builder()
//                                .put("task_name", AirflowConstant.AIRFLOW_TASK_PREFIX + node.getName())
//                                .build()));
            } else if (node.getName().endsWith(AirflowConstant.FLOW_DUMMY_END_NODE_SUFFIX)) {
                nodeDefList.add(genDummyTask(projectName, scheduleName, flow, node, "  ", tmpBuf));
//                nodeDefList.add(TemplatingClient.getInstance().render("common/dummy.ftl",
//                        ImmutableMap.<String, Object>builder()
//                                .put("task_name", AirflowConstant.AIRFLOW_TASK_PREFIX + node.getName())
//                                .build()));
            } else {
                String nodePropertiesStr = ((LinkisAirflowSchedulerNode)node).toJobString(linkisJobConverter);

                StringBuilder commandBuilder = tmpBuf;
                commandBuilder.setLength(0);
                commandBuilder.append(String.format("cd %s; java %s -Dlog4j.configuration=%s -cp \"%s\" ",
                        AirflowConf.LINKIS_AIRFLOW_CLIENT_BASE_PATH.getValue(),
                        AirflowConf.LINKIS_AIRFLOW_CLIENT_JVM_OPTIONS.getValue(),
                        linkisAirflowClientLogPropertiesPath, linkisAirflowClientJavaClassPath));
                commandBuilder.append(AirflowConf.LINKIS_AIRFLOW_CLIENT_JAVA_MAIN_CLASS.getValue());
                commandBuilder.append(" ");
                commandBuilder.append(Arrays.stream(nodePropertiesStr.split("\n"))
                        .map(x -> String.format("\"%s\"", x.replace("'", "\\'").replace("\"", "\\\"")))
                        .collect(Collectors.joining(" ")));

                List<Resource> nodeResources = node.getDssNode().getResources();
                String nodeResourceString = AirflowConstant.LINKIS_JOB_RESOURCES_KEY + new Gson().toJson(nodeResources);
                commandBuilder.append(String.format(" \"%s\"", nodeResourceString.replace("'", "\\'").replace("\"", "\\\"")));

                commandBuilder.append(bashFlowVariablesStr);

                commandBuilder.append(String.format(" \"%s=%s\"", AirflowConstant.AIRFLOW_PROJECT_NAME, projectName));
                commandBuilder.append(String.format(" \"%s=%d\"", AirflowConstant.AIRFLOW_FLOW_ID, flow.getId()));
                commandBuilder.append(String.format(" \"%s=%s\"", AirflowConstant.AIRFLOW_FLOW_NAME, flow.getName()));

                // submit user占位符号，上传到airflow前替换
                commandBuilder.append(String.format(" \"%s=%s\"", AirflowConstant.AIRFLOW_SUBMIT_USER, AirflowConstant.AIRFLOW_SUBMIT_USER_PLACEHOLDER));
                commandBuilder.append(String.format(" \"%s=%s\"", AirflowConstant.AIRFLOW_USER_TO_PROXY, AirflowConstant.AIRFLOW_SUBMIT_USER_PLACEHOLDER));

                // append airflow dag context param using jinja template, see https://cloud.tencent.com/developer/article/1019700
                commandBuilder.append(String.format(" \"%s={{ %s }}\"", AirflowConstant.AIRFLOW_CONTEXT_RUN_ID, "run_id"));
                commandBuilder.append(String.format(" \"%s={{ %s }}\"", AirflowConstant.AIRFLOW_CONTEXT_TASK_INSTANCE_KEY_STR, "task_instance_key_str"));

                String commandStr = commandBuilder.toString();

                nodeDefList.add(genBashOperatorTask(projectName, scheduleName, flow, node, commandStr, "  ", tmpBuf));
//                nodeDefList.add(TemplatingClient.getInstance().render("common/bash.ftl",
//                        ImmutableMap.<String, Object>builder()
//                                .put("task_name", AirflowConstant.AIRFLOW_TASK_PREFIX + node.getName())
//                                .put("bash_command", commandBuilder.toString())
//                                .build()));
            }
        }

        LOGGER.info("Generating node dependencies taxonomy for {}", scheduleName);
        Map<String, Set<String>> nodeDownstreamMap = new HashMap<>();
        for (SchedulerNode node: flow.getSchedulerNodes()) {
            for (String dependNodeName: node.getDependencys()) {
                if (!nodeDownstreamMap.containsKey(dependNodeName)) {
                    nodeDownstreamMap.put(dependNodeName, new HashSet<>());
                }
                nodeDownstreamMap.get(dependNodeName).add(node.getName());
            }
        }

        List<String> depsList = new ArrayList<>();
        nodeDownstreamMap.entrySet().stream().forEach(x -> {
            String upStream = AirflowConstant.AIRFLOW_TASK_PREFIX + x.getKey();
            List<String> downStreams = x.getValue().stream().map(y -> AirflowConstant.AIRFLOW_TASK_PREFIX + y).collect(Collectors.toList());
            depsList.add(String.format("%s >> [%s]\n", upStream, String.join(",", downStreams)));
        });

        String dagContent = genDagContent(projectName, scheduleName, flow, nodeDefList, depsList, tmpBuf);
//        ImmutableMap<String, Object> root = ImmutableMap.<String, Object>builder()
//                .put(AirflowConstant.AIRFLOW_PROJECT_NAME, scheduleName)
//                .put(AirflowConstant.AIRFLOW_FLOW_ID, flow.getId())
//                .put(AirflowConstant.AIRFLOW_FLOW_NAME, flow.getName())
//                .put("scheduleName", scheduleName)
//                .put("nodeList", nodeDefList)
//                .put("depsList", depsList)
//                .build();
//        String dagContent = TemplatingClient.getInstance().render("schedule.ftl", root);
        LOGGER.info("Generate dag content success for dag {}", scheduleName);
        return dagContent;
    }

    /*
    ${task_name} = DummyOperator(task_id='${task_name}')
     */
    private String genDummyTask(String projectName, String schedulerName, SchedulerFlow flow, SchedulerNode node, String blankPrefix, StringBuilder buffer) {
        buffer.setLength(0);
        String taskName = AirflowConstant.AIRFLOW_TASK_PREFIX + node.getName();
        buffer.append(String.format("%s%s = DummyOperator(task_id='%s')\n", blankPrefix, taskName, taskName));
        return buffer.toString();
    }

    /*
    ${task_name} = BashOperator(
             task_id='${task_name}',
             bash_command='${bash_command}',
             dag=dag,
         )
     */
    private String genBashOperatorTask(String projectName, String schedulerName, SchedulerFlow flow, SchedulerNode node,
                                       String commandStr, String blankPrefix, StringBuilder buffer) {
        buffer.setLength(0);
        String taskName = AirflowConstant.AIRFLOW_TASK_PREFIX + node.getName();

        buffer.append(String.format("%s%s = BashOperator(\n", blankPrefix, taskName));
        buffer.append(String.format("%s  task_id='%s',\n", blankPrefix, taskName));
        buffer.append(String.format("%s  bash_command='%s',\n", blankPrefix, commandStr));
        buffer.append(String.format("%s  dag=dag\n", blankPrefix));
        buffer.append(String.format("%s)\n", blankPrefix));
        return buffer.toString();
    }

    /*
from datetime import timedelta, datetime
from airflow import DAG
from airflow.operators.dummy_operator import DummyOperator
from airflow.operators.bash_operator import BashOperator

# metadata
project_name = '${project.name}'
flow_id = ${flow.id}
flow_name = '${flow.name}'


default_args = {
  'owner': '${userId}',
  'depends_on_past': False,
  'start_date': datetime.utcfromtimestamp(${startTime}),
  <#if endTime != 0>'end_date': datetime.utcfromtimestamp(${endTime}),</#if>
  'email': ['${userId}@nio.com'],
  'email_on_failure': False,
  'email_on_retry': False,
  'retries': 1,
  'retry_delay': timedelta(minutes=2),
}
<#assign intervals = ['once', 'hourly', 'daily', 'weekly', 'monthly', 'yearly']>
dag = DAG('${scheduleName}',
          schedule_interval=<#if intervals?seq_contains(scheduleInterval)>'@${scheduleInterval}'<#else>'${scheduleInterval}'</#if>,
          default_args=default_args,
          max_active_runs=1,
          is_paused_upon_creation=False)

with dag:
  <#list nodeList as node>
  ${indent(node, 2)}
  </#list>
  <#list depsList as deps>
  ${indent(deps, 2)}
  </#list>
         */

    private String genDagContent(String projectName, String schedulerName, SchedulerFlow flow,
                                 List<String> nodeDefList, List<String> depsList, StringBuilder buffer) throws DSSErrorException {
        buffer.setLength(0);

        Map<String, String> parsedFlowPropertyMap = new HashMap<>();
        List<Map<String, Object>> flowProperties = flow.getFlowProperties();
        if (flowProperties != null && !flowProperties.isEmpty()) {
            flowProperties.forEach(p ->p.forEach((k,v)->{
                parsedFlowPropertyMap.put(k, v.toString());
            }));
        }

        String airflowStartTime = parsedFlowPropertyMap.getOrDefault(AirflowConstant.FLOW_PROPERTIES_KEY_AIRFLOW_START_TIME, "");
        if (airflowStartTime.isEmpty() || !isDateTimeStrValid(airflowStartTime, "yyyy-MM-dd HH:mm:ss")) {
            throw new DSSErrorException(90008, String.format("需要在flow全局参数中配置变量 %s 以供airflow调度器使用，e.g. 2020-08-06 11:00:00", AirflowConstant.FLOW_PROPERTIES_KEY_AIRFLOW_START_TIME));
        }
        String airflowEndTime = parsedFlowPropertyMap.getOrDefault(AirflowConstant.FLOW_PROPERTIES_KEY_AIRFLOW_END_TIME, "");
        if (airflowEndTime.isEmpty() || !isDateTimeStrValid(airflowEndTime, "yyyy-MM-dd HH:mm:ss")) {
            throw new DSSErrorException(90008, String.format("需要在flow全局参数中配置变量 %s 以供airflow调度器使用，e.g. 2020-08-06 11:00:00", AirflowConstant.FLOW_PROPERTIES_KEY_AIRFLOW_END_TIME));
        }
        String airflowScheduleInterval = parsedFlowPropertyMap.getOrDefault(AirflowConstant.FLOW_PROPERTIES_KEY_AIRFLOW_SCHEDULE_INTERVAL, "");
        if (airflowScheduleInterval.isEmpty() || ! AIRFLOW_INTERVAL_CANDIDATE_SET.contains(airflowScheduleInterval)) {
            throw new DSSErrorException(90008, String.format("需要在flow全局参数中配置变量 %s 以供airflow调度器使用, e.g. once/hourly/daily/weekly/monthly/yearly", AirflowConstant.FLOW_PROPERTIES_KEY_AIRFLOW_SCHEDULE_INTERVAL));
        }

        buffer.append("from datetime import timedelta, datetime\n");
        buffer.append("from airflow import DAG\n");
        buffer.append("from airflow.operators.dummy_operator import DummyOperator\n");
        buffer.append("from airflow.operators.bash_operator import BashOperator\n");
        buffer.append("\n");
        buffer.append(String.format("project_name = '%s'\n", projectName));
        buffer.append(String.format("flow_id = %d\n", flow.getId()));
        buffer.append(String.format("flow_name = '%s'\n", flow.getName()));
        buffer.append("\n");
        buffer.append("default_args = {\n");
        buffer.append(String.format("  'owner': '%s',\n", AirflowConstant.AIRFLOW_SUBMIT_USER_PLACEHOLDER));
        buffer.append("  'depends_on_past': False,\n");
        buffer.append("  'start_date': datetime.strptime('" + airflowStartTime
                + "', '%Y-%m-%d %H:%M:%S'),\n");
        buffer.append("  'end_date': datetime.strptime('" + airflowEndTime
                + "', '%Y-%m-%d %H:%M:%S'),\n");
        buffer.append(String.format("  'email': ['%s@%s'],", AirflowConstant.AIRFLOW_SUBMIT_USER_PLACEHOLDER, AirflowConf.LINKIS_AIRFLOW_CLIENT_DEFAULT_MAIL_TO_SUFFIX.getValue()));
        buffer.append("  'email_on_failure': False,\n");
        buffer.append("  'email_on_retry': False,\n");
        buffer.append("  'retries': 1,\n");
        buffer.append("  'retry_delay': timedelta(minutes=2),\n");
        buffer.append("}\n");
        buffer.append("\n");
        buffer.append("\n");
        buffer.append(String.format("dag = DAG('%s',\n", schedulerName));
        buffer.append(String.format("  schedule_interval='@%s',\n", airflowScheduleInterval));
        buffer.append("  default_args=default_args,\n");
        buffer.append("  max_active_runs=1,\n");
        buffer.append("  is_paused_upon_creation=False)\n");
        buffer.append("\n");
        buffer.append("with dag:\n");
        for (String nodeDefStr: nodeDefList) {
            buffer.append(nodeDefStr);
            buffer.append("\n");
        }
        for (String depStr: depsList) {
            buffer.append("  ");
            buffer.append(depStr);
            buffer.append("\n");
        }
        return buffer.toString();
    }

    public static boolean isDateTimeStrValid(String dateTimeStr, String timeFormat)
    {
        try {
            DateFormat df = new SimpleDateFormat(timeFormat);
            df.setLenient(false);
            df.parse(dateTimeStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}
