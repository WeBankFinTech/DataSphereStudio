package com.webank.wedatasphere.dss.datamodel.center.common.ujes.launcher;

import com.webank.wedatasphere.dss.datamodel.center.common.dto.CreateTableDTO;
import org.apache.linkis.ujes.client.response.JobExecuteResult;
import org.apache.linkis.ujes.client.response.JobInfoResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CreateTableDataModelUJESJobLauncher extends AbstractDataModelUJESJobLauncher<CreateTableDTO>{
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateTableDataModelUJESJobLauncher.class);

    @Override
    CreateTableDTO callBack(JobExecuteResult jobExecuteResult) {
        JobInfoResult jobInfo = client.getJobInfo(jobExecuteResult);

        CreateTableDTO createTableDTO = new CreateTableDTO();
        createTableDTO.setStatus(jobInfo.getStatus());
        createTableDTO.setTaskId(jobExecuteResult.getTaskID());
        LOGGER.info("create table status : {}, info : {}",createTableDTO,jobInfo);
        return createTableDTO;
    }
}