package com.webank.wedatasphere.dss.datamodel.center.common.ujes.launcher;


import org.apache.linkis.ujes.client.response.JobExecuteResult;
import org.apache.linkis.ujes.client.response.JobInfoResult;
import org.springframework.stereotype.Component;

@Component
public class DropTableDataModelUJESJobLauncher extends AbstractDataModelUJESJobLauncher<Boolean>{

    @Override
    Boolean callBack(JobExecuteResult jobExecuteResult) {
        JobInfoResult jobInfo = client.getJobInfo(jobExecuteResult);
        return jobInfo.getStatus()==0;
    }
}
