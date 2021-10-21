package com.webank.wedatasphere.dss.datamodel.center.common.ujes;


import com.webank.wedatasphere.linkis.ujes.client.response.ResultSetResult;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Component
public class DataExistsDataModelUJESJobLauncher extends AbstractDataModelUJESJobLauncher<Boolean>{

    @Override
    Boolean callBack(ResultSetResult resultSetResult) {
        Object fileContents = resultSetResult.getFileContent();
        return CollectionUtils.isEmpty((List)fileContents);
    }
}
