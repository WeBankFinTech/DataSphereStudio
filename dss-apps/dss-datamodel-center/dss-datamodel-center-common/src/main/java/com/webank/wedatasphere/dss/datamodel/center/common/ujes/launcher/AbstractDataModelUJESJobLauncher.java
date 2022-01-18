package com.webank.wedatasphere.dss.datamodel.center.common.ujes.launcher;


import com.webank.wedatasphere.dss.datamodel.center.common.ujes.task.DataModelUJESJobTask;
import org.apache.linkis.common.utils.Utils;
import org.apache.linkis.ujes.client.UJESClient;
import org.apache.linkis.ujes.client.request.JobExecuteAction;
import org.apache.linkis.ujes.client.response.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

public abstract class AbstractDataModelUJESJobLauncher<E> implements DataModelUJESJobLauncher<E>{

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDataModelUJESJobLauncher.class);

    @Resource
    protected UJESClient client;


    protected void launch0(DataModelUJESJobTask task, JobExecuteResult jobExecuteResult){
            LOGGER.info("exec code : {}", task.getCode());
            //System.out.println("execId: " + jobExecuteResult.getExecID() + ", taskId: " + jobExecuteResult.taskID());
            LOGGER.info("execId : {}, taskId : {}",jobExecuteResult.getExecID(),jobExecuteResult.taskID());
            JobStatusResult status = client.status(jobExecuteResult);
            while (!status.isCompleted()) {
                JobProgressResult progress = client.progress(jobExecuteResult);
                //System.out.println("progress: " + progress.getProgress());
                LOGGER.info("progress : {}",progress.getProgress());
                Utils.sleepQuietly(500);
                status = client.status(jobExecuteResult);
            }

    }

    @Override
    public E launch(DataModelUJESJobTask task) {
        try {
            JobExecuteResult jobExecuteResult = client.execute(JobExecuteAction.builder().setCreator("hdfs")
                    .addExecuteCode(task.getCode())
                    .setEngineType((JobExecuteAction.EngineType) JobExecuteAction.EngineType$.MODULE$.HIVE()).setEngineTypeStr("hql")
                    .setUser(task.getUser()).build());
            launch0(task,jobExecuteResult);
            return callBack(jobExecuteResult);
        }catch (Exception e){
            LOGGER.error(e.getMessage(),e);
            throw e;
        }finally {
            //IOUtils.closeQuietly(client);
        }
    }

    abstract E callBack(JobExecuteResult jobExecuteResult);
}
