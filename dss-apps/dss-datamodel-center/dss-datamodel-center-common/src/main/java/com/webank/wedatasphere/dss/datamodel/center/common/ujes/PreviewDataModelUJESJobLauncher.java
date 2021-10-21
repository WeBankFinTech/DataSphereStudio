package com.webank.wedatasphere.dss.datamodel.center.common.ujes;



import com.webank.wedatasphere.dss.datamodel.center.common.dto.PreviewDataDTO;
import com.webank.wedatasphere.linkis.ujes.client.response.ResultSetResult;
import org.springframework.stereotype.Component;

@Component
public class PreviewDataModelUJESJobLauncher extends AbstractDataModelUJESJobLauncher<PreviewDataDTO> {

    @Override
    PreviewDataDTO callBack(ResultSetResult resultSetResult) {
        Object fileContents = resultSetResult.getFileContent();
        Object metadata = resultSetResult.getMetadata();
        PreviewDataDTO preview = new  PreviewDataDTO();
        preview.setMetadata(metadata);
        preview.setData(fileContents);
        return preview;
    }
}
