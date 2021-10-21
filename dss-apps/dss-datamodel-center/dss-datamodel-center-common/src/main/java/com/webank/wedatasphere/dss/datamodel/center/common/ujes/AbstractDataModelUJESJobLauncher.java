package com.webank.wedatasphere.dss.datamodel.center.common.ujes;


import com.webank.wedatasphere.linkis.common.utils.Utils;
import com.webank.wedatasphere.linkis.ujes.client.UJESClient;
import com.webank.wedatasphere.linkis.ujes.client.request.JobExecuteAction;
import com.webank.wedatasphere.linkis.ujes.client.request.ResultSetAction;
import com.webank.wedatasphere.linkis.ujes.client.response.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

public abstract class AbstractDataModelUJESJobLauncher<E> implements DataModelUJESJobLauncher<E>{

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDataModelUJESJobLauncher.class);

    @Resource
    protected UJESClient client;


    protected ResultSetResult launch0(DataModelUJESJobTask task){
            LOGGER.info("exec code : {}", task.getCode());
            JobExecuteResult jobExecuteResult = client.execute(JobExecuteAction.builder().setCreator("hdfs")
                    .addExecuteCode(task.getCode())
                    .setEngineType((JobExecuteAction.EngineType) JobExecuteAction.EngineType$.MODULE$.HIVE()).setEngineTypeStr("hql")
                    .setUser("hdfs").build());
            System.out.println("execId: " + jobExecuteResult.getExecID() + ", taskId: " + jobExecuteResult.taskID());
            LOGGER.info("execId : {}, taskId : {}",jobExecuteResult.getExecID(),jobExecuteResult.taskID());
            JobStatusResult status = client.status(jobExecuteResult);
            while (!status.isCompleted()) {
                JobProgressResult progress = client.progress(jobExecuteResult);
                System.out.println("progress: " + progress.getProgress());
                LOGGER.info("progress : {}",progress.getProgress());
                Utils.sleepQuietly(500);
                status = client.status(jobExecuteResult);
            }
            JobInfoResult jobInfo = client.getJobInfo(jobExecuteResult);
            String resultSet = jobInfo.getResultSetList(client)[0];
            return client.resultSet(ResultSetAction.builder().setPath(resultSet).setUser(jobExecuteResult.getUser()).build());

    }

    @Override
    public E launch(DataModelUJESJobTask task) {
        try {
            return callBack(launch0(task));
        }catch (Exception e){
            LOGGER.error(e.getMessage(),e);
            throw e;
        }finally {
            IOUtils.closeQuietly(client);
        }
    }

    abstract E callBack(ResultSetResult resultSetResult);
}
