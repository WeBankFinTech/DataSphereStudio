package com.webank.wedatasphere.dss.datamodel.center.common.ujes.launcher;


import com.webank.wedatasphere.linkis.ujes.client.request.ResultSetAction;
import com.webank.wedatasphere.linkis.ujes.client.response.JobExecuteResult;
import com.webank.wedatasphere.linkis.ujes.client.response.JobInfoResult;
import com.webank.wedatasphere.linkis.ujes.client.response.ResultSetResult;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataExistsDataModelUJESJobLauncher extends AbstractDataModelUJESJobLauncher<Boolean>{

    @Override
    Boolean callBack( JobExecuteResult jobExecuteResult) {
        JobInfoResult jobInfo = client.getJobInfo(jobExecuteResult);
        String resultSet = jobInfo.getResultSetList(client)[0];
        ResultSetResult resultSetResult = client.resultSet(ResultSetAction.builder().setPath(resultSet).setUser(jobExecuteResult.getUser()).build());

        Object fileContents = resultSetResult.getFileContent();
        return !CollectionUtils.isEmpty((List)fileContents);
    }
}
