package com.webank.wedatasphere.dss.plugins.dolphinscheduler.linkis.server.worker.tasks.sql;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.webank.wedatasphere.dss.plugins.dolphinscheduler.linkis.server.worker.tasks.AbstractLinkisTask;
import org.apache.dolphinscheduler.server.entity.TaskExecutionContext;
import com.webank.wedatasphere.linkis.computation.client.ResultSetIterable;
import com.webank.wedatasphere.linkis.computation.client.ResultSetIterator;
import com.webank.wedatasphere.linkis.governance.common.entity.task.RequestPersistTask;
import com.webank.wedatasphere.linkis.ujes.client.exception.UJESJobException;
import org.apache.commons.lang.StringUtils;
import org.apache.dolphinscheduler.alert.utils.MailUtils;
import org.apache.dolphinscheduler.common.Constants;
import org.apache.dolphinscheduler.common.enums.ShowType;
import org.apache.dolphinscheduler.common.task.AbstractParameters;
import org.apache.dolphinscheduler.common.task.sql.SqlParameters;
import org.apache.dolphinscheduler.common.task.sql.SqlType;
import org.apache.dolphinscheduler.common.utils.EnumUtils;
import org.apache.dolphinscheduler.common.utils.JSONUtils;
import org.apache.dolphinscheduler.dao.AlertDao;
import org.apache.dolphinscheduler.dao.entity.User;
import org.apache.dolphinscheduler.service.bean.SpringApplicationContext;
import org.slf4j.Logger;
import java.util.*;
import static org.apache.dolphinscheduler.common.Constants.COMMA;
import static org.apache.dolphinscheduler.common.Constants.STATUS;

/**
 * sql task
 */
public class LinkisSqlTask extends AbstractLinkisTask {

    private SqlParameters sqlParameters;
    private AlertDao alertDao;
    private TaskExecutionContext taskExecutionContext;

    /**
     * constructor
     *
     * @param taskExecutionContext taskExecutionContext
     * @param logger               logger
     */
    public LinkisSqlTask(TaskExecutionContext taskExecutionContext, Logger logger) {
        super(taskExecutionContext, logger);

        this.taskExecutionContext = taskExecutionContext;

        logger.info("linkis sql task params {}", taskExecutionContext.getTaskParams());
        this.sqlParameters = JSONUtils.parseObject(taskExecutionContext.getTaskParams(), SqlParameters.class);

        this.alertDao = SpringApplicationContext.getBean(AlertDao.class);
    }

    @Override
    public void handle() throws Exception {
        try {
            String threadLoggerInfoName = String.format(Constants.TASK_LOG_INFO_FORMAT, taskExecutionContext.getTaskAppId());
            Thread.currentThread().setName(threadLoggerInfoName);
            // 1.执行linkis任务
            try {
                super.handle();
            } catch (UJESJobException e) {
                // 任务kill
                if (super.isLinkisJobCompleted()) {
                    setExitStatusCode(Constants.EXIT_CODE_KILL);
                    logger.warn("Linkis: sql task has been killed successful.");
                    return;
                }
                throw e;
            }
            // 2.获取linkisJobInfo
            RequestPersistTask linkisJobInfo = super.getLinkisJobInfo();
            logger.info("dolphin taskInstanceId {}, taskName {}, linkis taskId {}, linkis executeId {}.", taskExecutionContext.getTaskInstanceId(), taskExecutionContext.getTaskName(),
                    linkisJobInfo.getTaskID(), linkisJobInfo.getExecId());
            // 3.执行返回结果逻辑
            if (sqlParameters.getSqlType() == SqlType.QUERY.ordinal()) {
                resultProcess(super.getResultSetIterables());
            }
            setExitStatusCode(Constants.EXIT_CODE_SUCCESS);
        } catch (Exception e) {
            setExitStatusCode(Constants.EXIT_CODE_FAILURE);
            logger.error("Linkis: linkis sql task error: {}", e.toString());
            throw new RuntimeException("Linkis: linkis sql task failed!");
        }
    }

    @Override
    public AbstractParameters getParameters() {
        return this.sqlParameters;
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
            mapOfColValues.put("metadata", metadata);
            logger.info("Linkis: metadata {}", metadata);
            while (resultSetIterator.hasNext()) {
                Object data = resultSetIterator.next();
                dataJSONArray.add(data);
                logger.info("Linkis: row {}", data);
            }
            mapOfColValues.put("data", dataJSONArray);
            resultJSONArray.add(mapOfColValues);
        }
        String result = JSONUtils.toJsonString(resultJSONArray);
        logger.info("Linkis: execute sql result : {}", result);
        // 发送邮件
        if (sqlParameters.getSendEmail() == null || sqlParameters.getSendEmail()) {
            sendAttachment(StringUtils.isNotEmpty(sqlParameters.getTitle()) ? sqlParameters.getTitle() : taskExecutionContext.getTaskName() + " query result sets", result);
        }
    }

    /**
     * send mail as an attachment
     *
     * @param title   title
     * @param content content
     */
    public void sendAttachment(String title, String content) {

        List<User> users = alertDao.queryUserByAlertGroupId(taskExecutionContext.getSqlTaskExecutionContext().getWarningGroupId());

        // receiving group list
        List<String> receiversList = new ArrayList<>();
        for (User user : users) {
            receiversList.add(user.getEmail().trim());
        }
        // custom receiver
        String receivers = sqlParameters.getReceivers();
        if (StringUtils.isNotEmpty(receivers)) {
            String[] splits = receivers.split(COMMA);
            for (String receiver : splits) {
                receiversList.add(receiver.trim());
            }
        }

        // copy list
        List<String> receiversCcList = new ArrayList<>();
        // Custom Copier
        String receiversCc = sqlParameters.getReceiversCc();
        if (StringUtils.isNotEmpty(receiversCc)) {
            String[] splits = receiversCc.split(COMMA);
            for (String receiverCc : splits) {
                receiversCcList.add(receiverCc.trim());
            }
        }

        String showTypeName = sqlParameters.getShowType().replace(COMMA, "").trim();
        if (EnumUtils.isValidEnum(ShowType.class, showTypeName)) {
            Map<String, Object> mailResult = MailUtils.sendMails(receiversList,
                    receiversCcList, title, content, ShowType.valueOf(showTypeName).getDescp());
            if (!(boolean) mailResult.get(STATUS)) {
                throw new RuntimeException("send mail failed!");
            }
        } else {
            logger.error("showType: {} is not valid ", showTypeName);
            throw new RuntimeException(String.format("showType: %s is not valid ", showTypeName));
        }
    }
}

