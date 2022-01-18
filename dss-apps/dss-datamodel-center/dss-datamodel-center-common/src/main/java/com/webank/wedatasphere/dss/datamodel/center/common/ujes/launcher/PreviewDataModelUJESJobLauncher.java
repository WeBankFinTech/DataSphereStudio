package com.webank.wedatasphere.dss.datamodel.center.common.ujes.launcher;



import com.webank.wedatasphere.dss.datamodel.center.common.dto.PreviewDataDTO;
import org.apache.linkis.ujes.client.request.ResultSetAction;
import org.apache.linkis.ujes.client.response.JobExecuteResult;
import org.apache.linkis.ujes.client.response.JobInfoResult;
import org.apache.linkis.ujes.client.response.ResultSetResult;
import org.springframework.stereotype.Component;

@Component
public class PreviewDataModelUJESJobLauncher extends AbstractDataModelUJESJobLauncher<PreviewDataDTO> {

    @Override
    PreviewDataDTO callBack(JobExecuteResult jobExecuteResult) {
        JobInfoResult jobInfo = client.getJobInfo(jobExecuteResult);
        String resultSet = jobInfo.getResultSetList(client)[0];
        ResultSetResult resultSetResult = client.resultSet(ResultSetAction.builder().setPath(resultSet).setUser(jobExecuteResult.getUser()).build());

        Object fileContents = resultSetResult.getFileContent();
        Object metadata = resultSetResult.getMetadata();
        PreviewDataDTO preview = new  PreviewDataDTO();
        preview.setMetadata(metadata);
        preview.setData(fileContents);
        return preview;
    }
}
