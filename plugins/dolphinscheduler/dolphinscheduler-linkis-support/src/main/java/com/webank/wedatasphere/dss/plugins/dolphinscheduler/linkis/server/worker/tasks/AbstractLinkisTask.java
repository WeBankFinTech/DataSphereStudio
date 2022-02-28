package com.webank.wedatasphere.dss.plugins.dolphinscheduler.linkis.server.worker.tasks;

import com.webank.wedatasphere.dss.plugins.dolphinscheduler.linkis.server.builder.LinkisInteractiveJobBuilder;
import com.webank.wedatasphere.dss.plugins.dolphinscheduler.linkis.server.entity.LinkisTaskExecutionContext;
import org.apache.dolphinscheduler.server.entity.TaskExecutionContext;
import com.webank.wedatasphere.dss.plugins.dolphinscheduler.linkis.server.utils.LinkisParamConvertUtil;
import com.webank.wedatasphere.dss.plugins.dolphinscheduler.linkis.server.utils.ProcessUtils;
import com.webank.wedatasphere.dss.plugins.dolphinscheduler.linkis.server.worker.config.LinkisPluginConfig;
import com.webank.wedatasphere.dss.plugins.dolphinscheduler.linkis.server.worker.exception.LinkisJobFailedException;
import com.webank.wedatasphere.linkis.computation.client.ResultSetIterable;
import com.webank.wedatasphere.linkis.computation.client.interactive.SubmittableInteractiveJob;
import com.webank.wedatasphere.linkis.governance.common.entity.task.RequestPersistTask;
import org.apache.dolphinscheduler.common.enums.TaskType;
import org.apache.dolphinscheduler.common.utils.EnumUtils;
import org.apache.dolphinscheduler.server.worker.task.AbstractTask;
import org.apache.dolphinscheduler.service.bean.SpringApplicationContext;
import org.slf4j.Logger;

/**
 * abstract linkis task
 */
public abstract class AbstractLinkisTask extends AbstractTask {

    private LinkisTaskExecutionContext linkisTaskExecutionContext;
    private LinkisInteractiveJobBuilder linkisInteractiveJobBuilder;
    private SubmittableInteractiveJob linkisJob;
    private ResultSetIterable[] resultSetIterables;
    private TaskExecutionContext taskExecutionContext;

    /**
     * constructor
     *
     * @param taskExecutionContext taskExecutionContext
     * @param logger               logger
     */
    protected AbstractLinkisTask(TaskExecutionContext taskExecutionContext, Logger logger) {
        super(taskExecutionContext, logger);
        this.taskExecutionContext = taskExecutionContext;
    }

    /**
     * 初始化 linkisJob
     *
     * @throws Exception
     */
    @Override
    public void init() throws Exception {
        super.init();
        linkisTaskExecutionContext = LinkisParamConvertUtil.getLinkisTaskExecuionContext(taskExecutionContext, SpringApplicationContext.getBean(LinkisPluginConfig.class), logger);
        logger.info("Linkis: LinkisTaskExecuionContext is {}.", linkisTaskExecutionContext);
        linkisInteractiveJobBuilder = LinkisInteractiveJobBuilder.getLinkisInteractiveJobBuilder().setLinkisTaskExecuionContext(linkisTaskExecutionContext);
        linkisJob = linkisInteractiveJobBuilder.build();
    }

    /**
     * 执行任务提交、默认会堵塞直到任务完成
     * sql任务可能会提交多个linkis job
     * getRemaintime获取任务执行时间，若用户配置了超时时间，则会获取执行时间
     *
     * @throws Exception
     */
    @Override
    public void handle() throws Exception {
        // 前置逻辑处理
        if (!linkisTaskExecutionContext.getPreExecuteCode().isEmpty()) {
            linkisTaskExecutionContext.setExecuteCode(linkisTaskExecutionContext.getPreExecuteCode());
            SubmittableInteractiveJob preJob = LinkisInteractiveJobBuilder.getLinkisInteractiveJobBuilder().setLinkisTaskExecuionContext(linkisTaskExecutionContext).build();
            preJob.submit();
            preJob.waitFor(getRemaintime());
            logger.debug("Linkis: preJob is complete.");
            if (!preJob.isSucceed()) {
                throw new LinkisJobFailedException("Linkis: preJob is failed.");
            }
        }
        // 执行任务，缓存linkisjob信息到taskExecutionContext
        linkisJob.submit();
        logger.debug("Linkis: linkisJob is submit.");
        // todo 替换 TaskExecutionContext
        this.taskExecutionContext.setLinkisJob(linkisJob);
        linkisJob.waitFor(getRemaintime());
        logger.debug("Linkis: linkisJob is complete.");
        if (!linkisJob.isSucceed()) {
            throw new LinkisJobFailedException("Linkis: linkisJob is failed.");
        }
        // 后置逻辑处理
        if (!linkisTaskExecutionContext.getPostExecuteCode().isEmpty()) {
            linkisTaskExecutionContext.setExecuteCode(linkisTaskExecutionContext.getPostExecuteCode());
            SubmittableInteractiveJob postJob = LinkisInteractiveJobBuilder.getLinkisInteractiveJobBuilder().setLinkisTaskExecuionContext(linkisTaskExecutionContext).build();
            postJob.submit();
            postJob.waitFor(getRemaintime());
            logger.debug("Linkis: postJob is complete.");
            if (!postJob.isSucceed()) {
                throw new LinkisJobFailedException("Linkis: postJob is failed.");
            }
        }
    }

    /**
     * 停止作业
     *
     * @param status status
     * @throws Exception
     */
    @Override
    public void cancelApplication(boolean status) throws Exception {
        linkisJob.kill();
        //当任务为mr、spark、flink任务时杀掉yarn的application任务
        TaskType taskType = EnumUtils.getEnum(TaskType.class, taskExecutionContext.getTaskType());
        if(taskType.equals(TaskType.MR)|| taskType.equals(TaskType.FLINK) || taskType.equals(TaskType.SPARK)){
            ProcessUtils.killYarnJob(taskExecutionContext);
        }
        super.cancelApplication(status);
    }

    /**
     * 获取结果集，可能返回多组数据
     *
     * @return
     */
    public ResultSetIterable[] getResultSetIterables() {
        try {
            resultSetIterables = linkisJob.getResultSetIterables();
        } catch (Exception e) {
            // linkis未返回结果
            if (!e.getMessage().contains("errCode=47000")) {
                throw new RuntimeException("Linkis: linkis 获取结果出错.");
            }
        }
        return resultSetIterables;
    }

    /**
     * 获取linkisJob
     *
     * @return
     */
    public SubmittableInteractiveJob getLinkisJob() {
        return linkisJob;
    }

    /**
     * 获取 LinkisJobInfo
     * 包括：
     * log路径、result路径、taskId、execId、progress、参数信息等
     *
     * @return
     */
    public RequestPersistTask getLinkisJobInfo() {
        return linkisJob.getJobInfo();
    }

    /**
     * 获取 linkisJob 的日志信息
     *
     * @return
     */
    public String[] getLinkisJobLog() {
        return linkisJob.getAllLogs();
    }

    /**
     * 获取执行进度
     *
     * @return
     */
    public Float getLinkisJobProgerss() {
        RequestPersistTask jobInfo = linkisJob.getJobInfo();
        if (jobInfo == null) {
            throw new RuntimeException("Linkis: 未获取 Linkis Job 信息，请确认任务已经提交！");
        } else {
            return jobInfo.getProgress();
        }
    }

    /**
     * linkis 任务是否完成
     *
     * @return
     */
    public Boolean isLinkisJobCompleted() {
        return linkisJob.isCompleted();
    }

    /**
     * linkis 任务是否成功
     *
     * @return
     */
    public Boolean isLinkisJobSucceed() {
        return linkisJob.isSucceed();
    }

    /**
     * get remain time
     * 单位为毫秒
     *
     * @return remain time
     */
    private long getRemaintime() {
        long usedTime = (System.currentTimeMillis() - taskExecutionContext.getStartTime().getTime()) / 1000;
        long remainTime = taskExecutionContext.getTaskTimeout() - usedTime;

        if (remainTime < 0) {
            throw new RuntimeException("Linkis: task execution time out");
        }
        return remainTime * 1000;
    }
}
