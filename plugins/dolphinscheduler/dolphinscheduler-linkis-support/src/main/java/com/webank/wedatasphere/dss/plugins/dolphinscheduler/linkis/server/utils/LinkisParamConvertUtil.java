package com.webank.wedatasphere.dss.plugins.dolphinscheduler.linkis.server.utils;

import com.webank.wedatasphere.dss.plugins.dolphinscheduler.linkis.server.entity.LinkisTaskExecutionContext;
import com.webank.wedatasphere.dss.plugins.dolphinscheduler.linkis.server.worker.config.LinkisPluginConfig;
import org.apache.dolphinscheduler.server.entity.TaskExecutionContext;
import com.webank.wedatasphere.linkis.computation.client.utils.LabelKeyUtils;
import com.webank.wedatasphere.linkis.manager.label.entity.engine.RunType;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.dolphinscheduler.common.Constants;
import org.apache.dolphinscheduler.common.enums.CommandType;
import org.apache.dolphinscheduler.common.enums.DbType;
import org.apache.dolphinscheduler.common.enums.TaskType;
import org.apache.dolphinscheduler.common.process.Property;
import org.apache.dolphinscheduler.common.process.ResourceInfo;
import org.apache.dolphinscheduler.common.task.flink.FlinkParameters;
import org.apache.dolphinscheduler.common.task.mr.MapReduceParameters;
import org.apache.dolphinscheduler.common.task.python.PythonParameters;
import org.apache.dolphinscheduler.common.task.shell.ShellParameters;
import org.apache.dolphinscheduler.common.task.spark.SparkParameters;
import org.apache.dolphinscheduler.common.task.sql.SqlParameters;
import org.apache.dolphinscheduler.common.utils.EnumUtils;
import org.apache.dolphinscheduler.common.utils.JSONUtils;
import org.apache.dolphinscheduler.common.utils.OSUtils;
import org.apache.dolphinscheduler.common.utils.ParameterUtils;
import org.apache.dolphinscheduler.dao.datasource.BaseDataSource;
import org.apache.dolphinscheduler.dao.datasource.HiveDataSource;
import org.apache.dolphinscheduler.dao.datasource.MySQLDataSource;
import org.apache.dolphinscheduler.dao.datasource.SparkDataSource;
import org.apache.dolphinscheduler.dao.entity.Resource;
import org.apache.dolphinscheduler.server.entity.SQLTaskExecutionContext;
import org.apache.dolphinscheduler.server.utils.FlinkArgsUtils;
import org.apache.dolphinscheduler.server.utils.MapReduceArgsUtils;
import org.apache.dolphinscheduler.server.utils.ParamUtils;
import org.apache.dolphinscheduler.server.utils.SparkArgsUtils;
import org.apache.dolphinscheduler.service.bean.SpringApplicationContext;
import org.apache.dolphinscheduler.service.process.ProcessService;
import org.slf4j.Logger;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * linkis param convert util
 */
public class LinkisParamConvertUtil {

    private static final String SYMBOL_SPLIT = ";";
    private static final String SYMBOL_HYPHEN = "-";
    private static final String SYMBOL_EQUAL = "=";
    private static final String SQL_USE = "use ";
    private static final String SQL_LIMIT = " LIMIT ";
    private static final String LINKIS_JOB_NAME = "jobName";
    private static final String LINKIS_YARN_QUEUE = "wds.linkis.yarnqueue";
    private static String preSql = "";
    private static String postSql = "";
    private static final String SPARK_COMMAND = "${SPARK_HOME}/bin/spark-submit";
    private static final String MAPREDUCE_COMMAND = Constants.HADOOP;
    private static final String FLINK_COMMAND = "flink";
    private static final String FLINK_RUN = "run";

    /**
     * 将dolphinscheduler的参数转换成linkis
     *
     * @param taskExecutionContext
     * @return
     */
    public static LinkisTaskExecutionContext getLinkisTaskExecuionContext(TaskExecutionContext taskExecutionContext, LinkisPluginConfig linkisPluginConfig, Logger logger) {

        return convert(taskExecutionContext, linkisPluginConfig, logger);
    }

    /**
     * 将调度侧的参数转换为linkis的参数
     * sqlTask的HIVE数据源的连接参数，暂不处理
     *
     * @param taskExecutionContext
     * @param linkisPluginConfig
     * @param logger
     * @return
     */
    public static LinkisTaskExecutionContext convert(TaskExecutionContext taskExecutionContext, LinkisPluginConfig linkisPluginConfig, Logger logger) {
        LinkisTaskExecutionContext linkisTaskExecutionContext = new LinkisTaskExecutionContext();
        switch (EnumUtils.getEnum(TaskType.class, taskExecutionContext.getTaskType())) {
            case SHELL:
                if (taskExecutionContext.getTaskParams() == null || taskExecutionContext.getTaskParams().isEmpty()) {
                    return new LinkisTaskExecutionContext();
                }
                ShellParameters shellParameters = JSONUtils.parseObject(taskExecutionContext.getTaskParams(), ShellParameters.class);
                if (!shellParameters.checkParameters()) {
                    throw new RuntimeException("shell task params is not valid");
                }
                Map<String, Property> shellParamsMap = ParamUtils.convert(ParamUtils.getUserDefParamsMap(taskExecutionContext.getDefinedParams()),
                        taskExecutionContext.getDefinedParams(),
                        shellParameters.getLocalParametersMap(),
                        CommandType.of(taskExecutionContext.getCmdTypeIfComplement()),
                        taskExecutionContext.getScheduleTime());
                linkisTaskExecutionContext = new LinkisTaskExecutionContext(linkisPluginConfig.getLinkisClientTokenKey(),
                        linkisPluginConfig.getLinkisClientTokenValue(),
                        getExcuteUser(taskExecutionContext, linkisPluginConfig),
                        linkisPluginConfig.getLinkisGatewayUrl(),
                        buildPythonAndShell(taskExecutionContext, shellParamsMap,shellParameters.getRawScript()),
                        preSql,
                        postSql,
                        RunType.SHELL().toString(),
                        buildStartParamMap(taskExecutionContext, linkisPluginConfig, null),
                        buildLabelMap(taskExecutionContext, linkisPluginConfig, linkisPluginConfig.getLinkisEngineShellType()),
                        buildVariableMap(taskExecutionContext, null),
                        buildSourceMap(linkisPluginConfig),
                        linkisPluginConfig.getLinkisPluginSchedulerClass(),
                        linkisPluginConfig.getLinkisPluginSchedulerPath());
                break;
            case SQL:
                SQLTaskExecutionContext sqlTaskExecutionContext = taskExecutionContext.getSqlTaskExecutionContext();
                if (sqlTaskExecutionContext == null) {
                    return new LinkisTaskExecutionContext();
                }
                logger.info("sql task params {}", taskExecutionContext.getTaskParams());
                SqlParameters sqlParameters = JSONUtils.parseObject(taskExecutionContext.getTaskParams(), SqlParameters.class);
                assert sqlParameters != null;
                if (!sqlParameters.checkParameters()) {
                    throw new RuntimeException("sql task params is not valid");
                }
                BaseDataSource dataSource = null;
                switch (DbType.valueOf(Objects.requireNonNull(sqlParameters).getType())) {
                    case HIVE:
                        dataSource = JSONUtils.parseObject(taskExecutionContext.getSqlTaskExecutionContext().getConnectionParams(), HiveDataSource.class);
                        assert dataSource != null;
                        linkisTaskExecutionContext = new LinkisTaskExecutionContext(linkisPluginConfig.getLinkisClientTokenKey(),
                                linkisPluginConfig.getLinkisClientTokenValue(),
                                getExcuteUser(taskExecutionContext, linkisPluginConfig),
                                linkisPluginConfig.getLinkisGatewayUrl(),
                                buildSqlCode(sqlParameters, dataSource, true, taskExecutionContext, linkisPluginConfig),
                                preSql,
                                postSql,
                                RunType.HIVE().toString(),
                                buildStartParamMap(taskExecutionContext, linkisPluginConfig, parseConnParams(sqlParameters)),
                                buildLabelMap(taskExecutionContext, linkisPluginConfig, linkisPluginConfig.getLinkisEngineHiveType()),
                                buildVariableMap(taskExecutionContext, sqlParameters.getLocalParametersMap()),
                                buildSourceMap(linkisPluginConfig),
                                linkisPluginConfig.getLinkisPluginSchedulerClass(),
                                linkisPluginConfig.getLinkisPluginSchedulerPath());
                        break;
                    case SPARK:
                        dataSource = JSONUtils.parseObject(taskExecutionContext.getSqlTaskExecutionContext().getConnectionParams(), SparkDataSource.class);
                        assert dataSource != null;
                        linkisTaskExecutionContext = new LinkisTaskExecutionContext(linkisPluginConfig.getLinkisClientTokenKey(),
                                linkisPluginConfig.getLinkisClientTokenValue(),
                                getExcuteUser(taskExecutionContext, linkisPluginConfig),
                                linkisPluginConfig.getLinkisGatewayUrl(),
                                buildSqlCode(sqlParameters, dataSource, true, taskExecutionContext, linkisPluginConfig),
                                preSql,
                                postSql,
                                RunType.SQL().toString(),
                                buildStartParamMap(taskExecutionContext, linkisPluginConfig, parseConnParams(sqlParameters)),
                                buildLabelMap(taskExecutionContext, linkisPluginConfig, linkisPluginConfig.getLinkisEngineSparkType()),
                                buildVariableMap(taskExecutionContext, sqlParameters.getLocalParametersMap()),
                                buildSourceMap(linkisPluginConfig),
                                linkisPluginConfig.getLinkisPluginSchedulerClass(),
                                linkisPluginConfig.getLinkisPluginSchedulerPath());
                        break;
                    case MYSQL:
                    case POSTGRESQL:
                        // 前置sql、后置sql、执行sql分开执行，避免MultiQueries无法正确返回结果
                        dataSource = JSONUtils.parseObject(taskExecutionContext.getSqlTaskExecutionContext().getConnectionParams(), MySQLDataSource.class);
                        assert dataSource != null;
                        linkisTaskExecutionContext = new LinkisTaskExecutionContext(linkisPluginConfig.getLinkisClientTokenKey(),
                                linkisPluginConfig.getLinkisClientTokenValue(),
                                getExcuteUser(taskExecutionContext, linkisPluginConfig),
                                linkisPluginConfig.getLinkisGatewayUrl(),
                                buildSqlCode(sqlParameters, dataSource, false, taskExecutionContext, linkisPluginConfig),
                                preSql,
                                postSql,
                                RunType.JDBC().toString(),
                                buildStartParamMap(taskExecutionContext, linkisPluginConfig, parseConnParams(sqlParameters)),
                                buildLabelMap(taskExecutionContext, linkisPluginConfig, linkisPluginConfig.getLinkisEngineJdbcType()),
                                buildVariableMap(taskExecutionContext, sqlParameters.getLocalParametersMap()),
                                buildSourceMap(linkisPluginConfig),
                                linkisPluginConfig.getLinkisPluginSchedulerClass(),
                                linkisPluginConfig.getLinkisPluginSchedulerPath());
                        break;
                    default:
                }
                break;
            case SPARK:
                if (taskExecutionContext.getTaskParams() == null || taskExecutionContext.getTaskParams().isEmpty()) {
                    return new LinkisTaskExecutionContext();
                }
                SparkParameters sparkParameters = JSONUtils.parseObject(taskExecutionContext.getTaskParams(), SparkParameters.class);
                if (null == sparkParameters ||!sparkParameters.checkParameters()) {
                    throw new RuntimeException("spark task params is not valid");
                }
                linkisTaskExecutionContext = new LinkisTaskExecutionContext(linkisPluginConfig.getLinkisClientTokenKey(),
                        linkisPluginConfig.getLinkisClientTokenValue(),
                        getExcuteUser(taskExecutionContext, linkisPluginConfig),
                        linkisPluginConfig.getLinkisGatewayUrl(),
                        buildSparkCode(taskExecutionContext, sparkParameters,logger),
                        preSql,
                        postSql,
                        RunType.SHELL().toString(),
                        buildStartParamMap(taskExecutionContext, linkisPluginConfig, null),
                        buildLabelMap(taskExecutionContext, linkisPluginConfig, linkisPluginConfig.getLinkisEngineShellType()),
                        buildVariableMap(taskExecutionContext, null),
                        buildSourceMap(linkisPluginConfig),
                        linkisPluginConfig.getLinkisPluginSchedulerClass(),
                        linkisPluginConfig.getLinkisPluginSchedulerPath());
                break;
            case MR:
                if (taskExecutionContext.getTaskParams() == null || taskExecutionContext.getTaskParams().isEmpty()) {
                    return new LinkisTaskExecutionContext();
                }
                MapReduceParameters mapReduceParameters = JSONUtils.parseObject(taskExecutionContext.getTaskParams(), MapReduceParameters.class);
                if (null == mapReduceParameters ||!mapReduceParameters.checkParameters()) {
                    throw new RuntimeException("mapReduce task params is not valid");
                }
                linkisTaskExecutionContext = new LinkisTaskExecutionContext(linkisPluginConfig.getLinkisClientTokenKey(),
                        linkisPluginConfig.getLinkisClientTokenValue(),
                        getExcuteUser(taskExecutionContext, linkisPluginConfig),
                        linkisPluginConfig.getLinkisGatewayUrl(),
                        buildMapReduce(taskExecutionContext, mapReduceParameters,logger),
                        preSql,
                        postSql,
                        RunType.SHELL().toString(),
                        buildStartParamMap(taskExecutionContext, linkisPluginConfig, null),
                        buildLabelMap(taskExecutionContext, linkisPluginConfig, linkisPluginConfig.getLinkisEngineShellType()),
                        buildVariableMap(taskExecutionContext, null),
                        buildSourceMap(linkisPluginConfig),
                        linkisPluginConfig.getLinkisPluginSchedulerClass(),
                        linkisPluginConfig.getLinkisPluginSchedulerPath());
                break;
            case FLINK:
                if (taskExecutionContext.getTaskParams() == null || taskExecutionContext.getTaskParams().isEmpty()) {
                    return new LinkisTaskExecutionContext();
                }
                FlinkParameters flinkParameters = JSONUtils.parseObject(taskExecutionContext.getTaskParams(), FlinkParameters.class);
                if (null == flinkParameters ||!flinkParameters.checkParameters()) {
                    throw new RuntimeException("mapReduce task params is not valid");
                }
                linkisTaskExecutionContext = new LinkisTaskExecutionContext(linkisPluginConfig.getLinkisClientTokenKey(),
                        linkisPluginConfig.getLinkisClientTokenValue(),
                        getExcuteUser(taskExecutionContext, linkisPluginConfig),
                        linkisPluginConfig.getLinkisGatewayUrl(),
                        buildFlink(taskExecutionContext, flinkParameters,logger),
                        preSql,
                        postSql,
                        RunType.SHELL().toString(),
                        buildStartParamMap(taskExecutionContext, linkisPluginConfig, null),
                        buildLabelMap(taskExecutionContext, linkisPluginConfig, linkisPluginConfig.getLinkisEngineShellType()),
                        buildVariableMap(taskExecutionContext, null),
                        buildSourceMap(linkisPluginConfig),
                        linkisPluginConfig.getLinkisPluginSchedulerClass(),
                        linkisPluginConfig.getLinkisPluginSchedulerPath());
                break;
            case PYTHON:
                if (taskExecutionContext.getTaskParams() == null || taskExecutionContext.getTaskParams().isEmpty()) {
                    return new LinkisTaskExecutionContext();
                }
                PythonParameters pythonParameters = JSONUtils.parseObject(taskExecutionContext.getTaskParams(), PythonParameters.class);
                if (null == pythonParameters ||!pythonParameters.checkParameters()) {
                    throw new RuntimeException("python task params is not valid");
                }
                Map<String, Property> pythonParamsMap = ParamUtils.convert(ParamUtils.getUserDefParamsMap(taskExecutionContext.getDefinedParams()),
                        taskExecutionContext.getDefinedParams(),
                        pythonParameters.getLocalParametersMap(),
                        CommandType.of(taskExecutionContext.getCmdTypeIfComplement()),
                        taskExecutionContext.getScheduleTime());
                String pythonExecute = RunType.PYTHON().toString();
                linkisTaskExecutionContext = new LinkisTaskExecutionContext(linkisPluginConfig.getLinkisClientTokenKey(),
                        linkisPluginConfig.getLinkisClientTokenValue(),
                        getExcuteUser(taskExecutionContext, linkisPluginConfig),
                        linkisPluginConfig.getLinkisGatewayUrl(),
                        buildPythonAndShell(taskExecutionContext, pythonParamsMap,pythonParameters.getRawScript()),
                        preSql,
                        postSql,
                        pythonExecute,
                        buildStartParamMap(taskExecutionContext, linkisPluginConfig, null),
                        buildLabelMap(taskExecutionContext, linkisPluginConfig, linkisPluginConfig.getLinkisEnginePythonType()),
                        buildVariableMap(taskExecutionContext, null),
                        buildSourceMap(linkisPluginConfig),
                        linkisPluginConfig.getLinkisPluginSchedulerClass(),
                        linkisPluginConfig.getLinkisPluginSchedulerPath());
                break;
            default:
        }
        return linkisTaskExecutionContext;
    }

    /**
     * 解析python和shell的执行命令
     * @param taskExecutionContext
     * @param paramsMap
     * @param rawScript
     * @return
     */
    public static String buildPythonAndShell(TaskExecutionContext taskExecutionContext, Map<String, Property> paramsMap,String rawScript) {
        String rawPythonScript = rawScript.replaceAll("\\r\\n", "\n");
        // replace placeholder
        if(MapUtils.isEmpty(paramsMap)){
            paramsMap=new HashMap<>(16);
        }
        if (MapUtils.isNotEmpty(taskExecutionContext.getParamsMap())){
            paramsMap.putAll(taskExecutionContext.getParamsMap());
        }
        if (!paramsMap.isEmpty()){
            //将shell和python脚本中带有${}的变量替换,相当向脚本中传入参数
            rawPythonScript = ParameterUtils.convertParameterPlaceholders(rawPythonScript, ParamUtils.convert(paramsMap));
        }
        return rawPythonScript;
    }

    /**
     * 构建linkis需要的全局参数，可能会存在多余的参数
     * 不需要替换，只需要构建全局参数的map，linkis侧会处理替换
     *
     * @param taskExecutionContext
     * @param localParametersMap
     * @return
     */
    private static Map<String, Object> buildVariableMap(TaskExecutionContext taskExecutionContext, Map<String, Property> localParametersMap) {
        HashMap<String, Object> stringObjectHashMap = new HashMap<>(4);
        Map<String, Property> mergeParam = mergeParam(taskExecutionContext, localParametersMap);
        // 不存在全局参数、自定义参数，返回空map
        if (mergeParam == null || mergeParam.isEmpty()) {
            return stringObjectHashMap;
        }
        // map value 类型转换
        ParamUtils.convert(mergeParam).forEach(stringObjectHashMap::put);
        return stringObjectHashMap;
    }

    /**
     * 合并全局参数、自定义参数，完成自定义参数引用全局参数的替换
     *
     * @param taskExecutionContext
     * @return
     */
    private static Map<String, Property> mergeParam(TaskExecutionContext taskExecutionContext, Map<String, Property> localParametersMap) {
        Map<String, Property> convert = ParamUtils.convert(ParamUtils.getUserDefParamsMap(taskExecutionContext.getDefinedParams()),
                taskExecutionContext.getDefinedParams(),
                localParametersMap,
                CommandType.of(taskExecutionContext.getCmdTypeIfComplement()),
                taskExecutionContext.getScheduleTime());
        // 添加system.time等参数
        if (MapUtils.isNotEmpty(taskExecutionContext.getParamsMap()) && convert != null) {
            convert.putAll(taskExecutionContext.getParamsMap());
        } else if (convert == null) {
            return taskExecutionContext.getParamsMap();
        }
        return convert;
    }


    /**
     *
     * @param sqlParameters
     * @param dataSource
     * @param isMultiStatement     是否支持多语句，hive、spark支持多语句；mysql、pg等数据库在连接信息中已经指定数据库，且多语句的情况不能正确返回结果，故采用前置、后置方式，提交多个job
     * @param taskExecutionContext
     * @param linkisPluginConfig
     * @return
     */
    private static String buildSqlCode(SqlParameters sqlParameters, BaseDataSource dataSource, boolean isMultiStatement, TaskExecutionContext taskExecutionContext, LinkisPluginConfig linkisPluginConfig) {
        StringBuffer stringBuffer = new StringBuffer();
        if (isMultiStatement) {
            stringBuffer.append(SQL_USE).append(dataSource.getDatabase()).append(SYMBOL_SPLIT);
        }
        if (isMultiStatement) {
            buildStatement(stringBuffer, taskExecutionContext, sqlParameters.getPreStatements());
        } else {
            StringBuffer preStringBuffer = new StringBuffer();
            buildStatement(preStringBuffer, taskExecutionContext, sqlParameters.getPreStatements());
            preSql = preStringBuffer.toString();
        }

        String excuteSql = convertScheduleTime(sqlParameters.getSql(), taskExecutionContext.getScheduleTime());
        // limit 处理
        stringBuffer.append(dealSQLLimit(excuteSql, sqlParameters.getLimit(), linkisPluginConfig.getLinkisSqlDefaultLimit())).append(SYMBOL_SPLIT);

        if (isMultiStatement) {
            buildStatement(stringBuffer, taskExecutionContext, sqlParameters.getPostStatements());
        } else {
            StringBuffer postStringBuffer = new StringBuffer();
            buildStatement(postStringBuffer, taskExecutionContext, sqlParameters.getPostStatements());
            postSql = postStringBuffer.toString();
        }
        return stringBuffer.toString();
    }

    /**
     * 构建spark的jar包提交的命令
     * @param taskExecutionContext
     * @param sparkParameters
     * @return
     */
    private static String buildSparkCode(TaskExecutionContext taskExecutionContext, SparkParameters sparkParameters,Logger logger){
        List<String> args = new ArrayList<>();
        String commandPathFile = taskExecutionContext.getExecutePath() + "/" + taskExecutionContext.getTaskAppId() + ".command";
        sparkParameters.setQueue(taskExecutionContext.getQueue());
        ResourceInfo mainJar = sparkParameters.getMainJar();
        sparkParameters.setMainJar(setMainJarName(mainJar,taskExecutionContext));
        args.add(SPARK_COMMAND);
        args.addAll(SparkArgsUtils.buildArgs(sparkParameters));
        String command = ParameterUtils.convertParameterPlaceholders(String.join(" ", args),
                taskExecutionContext.getDefinedParams());
        return createCommandFileIfNotExists(command,commandPathFile,taskExecutionContext,logger);
    }

    /**
     * 构建mapReduce的jar包提交的命令
     * @param taskExecutionContext
     * @param mapReduceParameters
     * @return
     */
    public static String buildMapReduce(TaskExecutionContext taskExecutionContext, MapReduceParameters mapReduceParameters,Logger logger){
        mapReduceParameters.setQueue(taskExecutionContext.getQueue());
        ResourceInfo mainJar = mapReduceParameters.getMainJar();
        List<String> args = new ArrayList<>();
        String commandPathFile = taskExecutionContext.getExecutePath() + "/" + taskExecutionContext.getTaskAppId() + ".command";
        mapReduceParameters.setMainJar(setMainJarName(mainJar,taskExecutionContext));
        args.add(MAPREDUCE_COMMAND);
        // other parameters
        args.addAll(MapReduceArgsUtils.buildArgs(mapReduceParameters));
        String command = ParameterUtils.convertParameterPlaceholders(String.join(" ", args),
                taskExecutionContext.getDefinedParams());
        return createCommandFileIfNotExists(command,commandPathFile,taskExecutionContext,logger);
    }

    /**
     * 查询并设置mainJar
     * @param mainJar
     * @param taskExecutionContext
     * @return
     */
    public static ResourceInfo setMainJarName(ResourceInfo mainJar,TaskExecutionContext taskExecutionContext){
        ProcessService processService = SpringApplicationContext.getBean(ProcessService.class);
        int resourceId = mainJar.getId();
        String resourceName;
        if (resourceId == 0) {
            resourceName = mainJar.getRes();
        } else {
            Resource resource = processService.getResourceById(resourceId);
            if (resource == null) {
                throw new RuntimeException(String.format("resource id: %d not exist", resourceId));
            }
            resourceName = taskExecutionContext.getExecutePath() + resource.getFullName();
        }
        mainJar.setRes(resourceName);
        return mainJar;
    }

    /**
     * 构建flink提交jar的命令
     * @param taskExecutionContext
     * @param flinkParameters
     * @return
     */
    public static String buildFlink(TaskExecutionContext taskExecutionContext, FlinkParameters flinkParameters,Logger logger){
        ResourceInfo mainJar = flinkParameters.getMainJar();
        String commandPathFile = taskExecutionContext.getExecutePath() + "/" + taskExecutionContext.getTaskAppId() + ".command";
        List<String> args = new ArrayList<>();
        flinkParameters.setMainJar(setMainJarName(mainJar,taskExecutionContext));
        flinkParameters.setQueue(taskExecutionContext.getQueue());
        args.add(FLINK_COMMAND);
        args.add(FLINK_RUN);
        args.addAll(FlinkArgsUtils.buildArgs(flinkParameters));
        String command = ParameterUtils.convertParameterPlaceholders(String.join(" ", args),
                taskExecutionContext.getDefinedParams());
        return createCommandFileIfNotExists(command,commandPathFile,taskExecutionContext,logger);
    }

    /**
     * 将命令写入command文件中
     * @param execCommand
     * @param commandFile
     * @param taskExecutionContext
     * @throws IOException
     */
    public static String createCommandFileIfNotExists(String execCommand, String commandFile,TaskExecutionContext taskExecutionContext,Logger logger){
        // create if non existence
        if (!Files.exists(Paths.get(commandFile))) {
            StringBuilder sb = new StringBuilder();
            if (OSUtils.isWindows()) {
                sb.append("@echo off\n");
                if (taskExecutionContext.getEnvFile() != null) {
                    sb.append("call ").append(taskExecutionContext.getEnvFile()).append("\n");
                }
            } else {
                sb.append("#!/bin/sh\n");
                if (taskExecutionContext.getEnvFile() != null) {
                    sb.append("source ").append(taskExecutionContext.getEnvFile()).append("\n");
                }
            }
            sb.append(execCommand);
            logger.info("command : {}", sb.toString());
            // write data to file
            try {
                FileUtils.writeStringToFile(new File(commandFile), sb.toString(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                logger.error("command write Error");
            }
        }
        return "bash " + commandFile + ";echo $?;";
    }

    /**
     * 将前置、后置sql打平
     *
     * @param stringBuffer
     * @param taskExecutionContext
     * @param list
     */
    private static void buildStatement(StringBuffer stringBuffer, TaskExecutionContext taskExecutionContext, List<String> list) {
        list.forEach(sql -> {
            stringBuffer.append(convertScheduleTime(sql, taskExecutionContext.getScheduleTime()));
            if (!sql.trim().endsWith(SYMBOL_SPLIT)) {
                stringBuffer.append(SYMBOL_SPLIT);
            }
        });
    }

    /**
     * 转换脚本中的 scheduleTime 参数
     * $[yyyyMMdd] 替换为时间，需要调度侧单独处理
     *
     * @param text
     * @param scheduleTime
     * @return
     */
    private static String convertScheduleTime(String text, Date scheduleTime) {
        return ParameterUtils.replaceScheduleTime(text, scheduleTime);
    }

    /**
     * 处理sql中limit限制
     *
     * @param sql
     * @param dolphinMax 海豚最大返回条目数限制
     * @param linkisMax  linkis最大返回条目限制
     * @return
     */
    private static String dealSQLLimit(String sql, int dolphinMax, int linkisMax) {
        // 1. 排除子查询中含有limit关键字
        // 2. 含有limit关键字且值小于dolphinMax
        // 3. 含有limit关键字且值大于dolphinMax
        // 4. 不含有limit关键字
        if (!sql.trim().toLowerCase(Locale.ROOT).contains("limit")) {
            sql = sql + SQL_LIMIT + Math.min(dolphinMax, linkisMax);
        }
        return sql;
    }

    /**
     * 构建linkis引擎的启动参数，如spark的资源配置等
     *
     * @param taskExecutionContext
     * @param linkisPluginConfig
     * @param collect
     * @return
     */
    private static Map<String, Object> buildStartParamMap(TaskExecutionContext taskExecutionContext, LinkisPluginConfig linkisPluginConfig, Map<String, String> collect) {
        Map<String, Object> startup = new HashMap<>(1);
        String queue = (taskExecutionContext.getQueue() == null || taskExecutionContext.getQueue().isEmpty()) ? linkisPluginConfig.getLinkisEngineDefaultYarnQueue() : taskExecutionContext.getQueue();
        startup.put(LINKIS_YARN_QUEUE, queue);
        // 获取数据源连接参数，此配置用于生成hive的配置
        if (collect != null && !collect.isEmpty()) {
            startup.putAll(collect);
        }
        return startup;
    }

    /**
     * 解析hive连接参数
     *
     * @param sqlParameters
     * @return
     */
    private static Map<String, String> parseConnParams(SqlParameters sqlParameters) {
        if (sqlParameters == null || sqlParameters.getConnParams() == null || sqlParameters.getConnParams().isEmpty()) {
            return new HashMap<String, String>();
        }
        return Arrays.stream(sqlParameters.getConnParams().split(SYMBOL_SPLIT)).collect(Collectors.toMap(str -> str.substring(0, str.indexOf(SYMBOL_EQUAL)), str -> str.substring(str.indexOf(SYMBOL_EQUAL) + 1)));
    }

    /**
     * 构建linkis的source信息，不是必须的
     *
     * @param linkisPluginConfig
     * @return
     */
    private static Map<String, Object> buildSourceMap(LinkisPluginConfig linkisPluginConfig) {
        Map<String, Object> sourceMap = new HashMap<>(1);
        sourceMap.put(LINKIS_JOB_NAME, linkisPluginConfig.getLinkisJobName());
        return sourceMap;
    }

    /**
     * 构建linkis引擎的label信息
     *
     * @param taskExecutionContext
     * @param linkisPluginConfig
     * @param engineType
     * @return
     */
    private static Map<String, Object> buildLabelMap(TaskExecutionContext taskExecutionContext, LinkisPluginConfig linkisPluginConfig, String engineType) {
        Map<String, Object> labelMap = new HashMap<>(2);
        String excuteuser = getExcuteUser(taskExecutionContext, linkisPluginConfig);
        labelMap.put(LabelKeyUtils.USER_CREATOR_LABEL_KEY(), excuteuser + SYMBOL_HYPHEN + linkisPluginConfig.getLinkisEngineUseCreater());
        labelMap.put(LabelKeyUtils.ENGINE_TYPE_LABEL_KEY(), engineType);
        return labelMap;
    }

    /**
     * 获取执行用户，优先使用当前租户
     *
     * @param taskExecutionContext
     * @param linkisPluginConfig
     * @return
     */
    private static String getExcuteUser(TaskExecutionContext taskExecutionContext, LinkisPluginConfig linkisPluginConfig) {
        return taskExecutionContext.getTenantCode() == null ? linkisPluginConfig.getLinkisDeployUser() : taskExecutionContext.getTenantCode();
    }
}
