package com.webank.wedatasphere.dss.plugins.dolphinscheduler.linkis.server.worker.tasks.shell;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.webank.wedatasphere.dss.plugins.dolphinscheduler.linkis.server.worker.tasks.AbstractLinkisTask;
import org.apache.dolphinscheduler.server.entity.TaskExecutionContext;
import com.webank.wedatasphere.linkis.computation.client.ResultSetIterable;
import com.webank.wedatasphere.linkis.computation.client.ResultSetIterator;
import com.webank.wedatasphere.linkis.governance.common.entity.task.RequestPersistTask;
import org.apache.dolphinscheduler.common.Constants;
import org.apache.dolphinscheduler.common.enums.TaskType;
import org.apache.dolphinscheduler.common.task.AbstractParameters;
import org.apache.dolphinscheduler.common.task.shell.ShellParameters;
import org.apache.dolphinscheduler.common.utils.EnumUtils;
import org.apache.dolphinscheduler.common.utils.JSONUtils;
import org.apache.dolphinscheduler.server.worker.cache.TaskExecutionContextCacheManager;
import org.apache.dolphinscheduler.server.worker.cache.impl.TaskExecutionContextCacheManagerImpl;
import org.apache.dolphinscheduler.server.worker.task.CommandExecuteResult;
import org.apache.dolphinscheduler.service.bean.SpringApplicationContext;
import org.slf4j.Logger;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import static org.apache.dolphinscheduler.common.Constants.EXIT_CODE_KILL;

/**
 * shell task
 */
public class LinkisShellTask extends AbstractLinkisTask {

    /**
     * shell parameters
     */
    private ShellParameters shellParameters;

    /**
     * taskExecutionContext
     */
    private TaskExecutionContext taskExecutionContext;

    /**
     * log handler
     */
    private Consumer<LinkedBlockingQueue<String>> logHandler;

    /**
     * logger
     */
    private Logger logger;

    /**
     * log collection
     */
    private final LinkedBlockingQueue<String> logBuffer = new LinkedBlockingQueue<>();

    /**
     * taskExecutionContextCacheManager
     */
    private TaskExecutionContextCacheManager taskExecutionContextCacheManager;

    /**
     * constructor
     *
     * @param taskExecutionContext taskExecutionContext
     * @param logger               logger
     */
    public LinkisShellTask(TaskExecutionContext taskExecutionContext, Logger logger) {
        super(taskExecutionContext, logger);

        this.taskExecutionContext = taskExecutionContext;
        this.logHandler = this::logHandle;
        this.logger = logger;
        this.taskExecutionContextCacheManager = SpringApplicationContext.getBean(TaskExecutionContextCacheManagerImpl.class);
    }

    @Override
    public void init() throws Exception {
        TaskType taskType = EnumUtils.getEnum(TaskType.class, taskExecutionContext.getTaskType());
        logger.info("Linkis: " + taskType.getDescp() + " task params {}", taskExecutionContext.getTaskParams());
        shellParameters = JSONUtils.parseObject(taskExecutionContext.getTaskParams(), ShellParameters.class);
//        if (!shellParameters.checkParameters()) {
//            throw new RuntimeException("Linkis: shell task params is not valid");
//        }
        super.init();
    }

    @Override
    public void handle() throws Exception {
        TaskType taskType = EnumUtils.getEnum(TaskType.class, taskExecutionContext.getTaskType());
        try {
            // 1. 前置判断
            CommandExecuteResult result = new CommandExecuteResult();
            int taskInstanceId = taskExecutionContext.getTaskInstanceId();
            // If the task has been killed, then the task in the cache is null
            if (null == taskExecutionContextCacheManager.getByTaskInstanceId(taskInstanceId)) {
                result.setExitStatusCode(EXIT_CODE_KILL);
                return;
            }
            // 2. 提交linkis执行，可能在waitForCompleted时，存在kill掉job的情况，需要自行处理
            try {
                super.handle();
            } catch (Exception e) {
                // 任务kill
                if (super.isLinkisJobCompleted()) {
                    setExitStatusCode(EXIT_CODE_KILL);
                    setAppIds(result.getAppIds());
                    setProcessId(0);
                    logger.warn("Linkis: " + taskType.getDescp() +" task has been killed successful.");
                    return;
                }
                throw e;
            }
            // 3. 结果输出
            // 获取linkisJobInfo
            RequestPersistTask linkisJobInfo = super.getLinkisJobInfo();
            logger.info("dolphin taskInstanceId {}, taskName {}, linkis taskId {}, linkis executeId {}.", taskExecutionContext.getTaskInstanceId(), taskExecutionContext.getTaskName(),
                    linkisJobInfo.getTaskID(), linkisJobInfo.getExecId());
            // 执行返回结果逻辑
            resultProcess(super.getResultSetIterables());

            setExitStatusCode(result.getExitStatusCode());
            setAppIds(result.getAppIds());
            setProcessId(linkisJobInfo.getTaskID().intValue());
        } catch (Exception e) {
            logger.error("Linkis: "+ taskType.getDescp() +" task error", e);
            setExitStatusCode(Constants.EXIT_CODE_FAILURE);
            throw e;
        }
    }

    @Override
    public void cancelApplication(boolean cancelApplication) throws Exception {
        super.cancelApplication(cancelApplication);
    }

    @Override
    public AbstractParameters getParameters() {
        return shellParameters;
    }

    /**
     * 解析linkis返回结果，打印日志、发送邮件等
     *
     * @param resultSetIterables
     */
    public void resultProcess(ResultSetIterable[] resultSetIterables) {
        // linkis支持一个脚本中存在多条语句，故可能会有多条结果。
        Iterator<ResultSetIterable> resultSetIterable = Arrays.stream(resultSetIterables).iterator();
        JSONArray resultJSONArray = new JSONArray();
        while (resultSetIterable.hasNext()) {
            JSONObject mapOfColValues = new JSONObject(true);
            JSONArray dataJSONArray = new JSONArray();
            ResultSetIterator resultSetIterator = resultSetIterable.next().iterator();
            Object metadata = resultSetIterator.getMetadata();
            if (metadata != null) {
                mapOfColValues.put("metadata", metadata);
                logger.info("Linkis: metadata {}", metadata);
            }
            while (resultSetIterator.hasNext()) {
                Object data = resultSetIterator.next();
                if (data != null) {
                    dataJSONArray.add(data);
                    logger.info("Linkis: row {}", data);
                }
            }
            mapOfColValues.put("data", dataJSONArray);
            resultJSONArray.add(mapOfColValues);
        }
        String result = JSONUtils.toJsonString(resultJSONArray);
        logger.info("Linkis: execute sql result : {}", result);

    }
}
