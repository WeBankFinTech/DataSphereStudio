package com.webank.wedatasphere.dss.plugins.dolphinscheduler.linkis.server.worker.tasks;

import org.apache.dolphinscheduler.server.entity.TaskExecutionContext;
import com.webank.wedatasphere.dss.plugins.dolphinscheduler.linkis.server.worker.config.LinkisPluginConfig;
import com.webank.wedatasphere.dss.plugins.dolphinscheduler.linkis.server.worker.tasks.shell.LinkisShellTask;
import com.webank.wedatasphere.dss.plugins.dolphinscheduler.linkis.server.worker.tasks.sql.LinkisSqlTask;
import org.apache.dolphinscheduler.common.enums.DbType;
import org.apache.dolphinscheduler.common.enums.TaskType;
import org.apache.dolphinscheduler.common.task.sql.SqlParameters;
import org.apache.dolphinscheduler.common.utils.EnumUtils;
import org.apache.dolphinscheduler.common.utils.JSONUtils;
import org.apache.dolphinscheduler.server.worker.task.AbstractTask;
import org.apache.dolphinscheduler.server.worker.task.datax.DataxTask;
import org.apache.dolphinscheduler.server.worker.task.flink.FlinkTask;
import org.apache.dolphinscheduler.server.worker.task.http.HttpTask;
import org.apache.dolphinscheduler.server.worker.task.mr.MapReduceTask;
import org.apache.dolphinscheduler.server.worker.task.processdure.ProcedureTask;
import org.apache.dolphinscheduler.server.worker.task.python.PythonTask;
import org.apache.dolphinscheduler.server.worker.task.shell.ShellTask;
import org.apache.dolphinscheduler.server.worker.task.spark.SparkTask;
import org.apache.dolphinscheduler.server.worker.task.sql.SqlTask;
import org.apache.dolphinscheduler.server.worker.task.sqoop.SqoopTask;
import org.apache.dolphinscheduler.service.bean.SpringApplicationContext;
import org.slf4j.Logger;

import java.util.Objects;

/**
 * task manasger
 */
public class TaskManager {

    /**
     * create new task
     *
     * @param taskExecutionContext taskExecutionContext
     * @param logger               logger
     * @return AbstractTask
     * @throws IllegalArgumentException illegal argument exception
     */
    public static AbstractTask newTask(TaskExecutionContext taskExecutionContext,
                                       Logger logger)
            throws IllegalArgumentException {
        LinkisPluginConfig linkisPluginConfig = SpringApplicationContext.getBean(LinkisPluginConfig.class);
        SqlParameters sqlParameters = JSONUtils.parseObject(taskExecutionContext.getTaskParams(), SqlParameters.class);
        switch (EnumUtils.getEnum(TaskType.class, taskExecutionContext.getTaskType())) {
            case SHELL:
                return linkisPluginConfig.isLinkisEngineSupport() ? new LinkisShellTask(taskExecutionContext, logger) : new ShellTask(taskExecutionContext, logger);
            case PROCEDURE:
                return new ProcedureTask(taskExecutionContext, logger);
            case SQL:
                switch (DbType.valueOf(Objects.requireNonNull(sqlParameters).getType())) {
                    case HIVE:
                    case SPARK:
                    case MYSQL:
                    case POSTGRESQL:
                        return linkisPluginConfig.isLinkisEngineSupport() ? new LinkisSqlTask(taskExecutionContext, logger) : new SqlTask(taskExecutionContext, logger);
                    default:
                        return new SqlTask(taskExecutionContext, logger);
                }
            case MR:
                return new MapReduceTask(taskExecutionContext, logger);
            case SPARK:
                return linkisPluginConfig.isLinkisEngineSupport() ? new LinkisShellTask(taskExecutionContext, logger) : new SparkTask(taskExecutionContext, logger);
            case FLINK:
                return new FlinkTask(taskExecutionContext, logger);
            case PYTHON:
                return linkisPluginConfig.isLinkisEngineSupport() ? new LinkisShellTask(taskExecutionContext, logger) : new PythonTask(taskExecutionContext, logger);
            case HTTP:
                return new HttpTask(taskExecutionContext, logger);
            case DATAX:
                return new DataxTask(taskExecutionContext, logger);
            case SQOOP:
                return new SqoopTask(taskExecutionContext, logger);
            default:
                logger.error("unsupport task type: {}", taskExecutionContext.getTaskType());
                throw new IllegalArgumentException("not support task type");
        }
    }
}
