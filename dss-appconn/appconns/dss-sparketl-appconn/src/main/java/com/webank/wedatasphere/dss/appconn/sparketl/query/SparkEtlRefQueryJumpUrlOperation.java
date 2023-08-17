package com.webank.wedatasphere.dss.appconn.sparketl.query;

import com.webank.wedatasphere.dss.standard.app.development.operation.AbstractDevelopmentOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefQueryJumpUrlOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.QueryJumpUrlResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.OnlyDevelopmentRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.utils.QueryJumpUrlConstant;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.apache.commons.lang3.StringUtils;

public class SparkEtlRefQueryJumpUrlOperation extends AbstractDevelopmentOperation<OnlyDevelopmentRequestRef.QueryJumpUrlRequestRefImpl, QueryJumpUrlResponseRef>
        implements RefQueryJumpUrlOperation<OnlyDevelopmentRequestRef.QueryJumpUrlRequestRefImpl, QueryJumpUrlResponseRef> {

    @Override
    public QueryJumpUrlResponseRef query(OnlyDevelopmentRequestRef.QueryJumpUrlRequestRefImpl requestRef) throws ExternalOperationFailedException {
        String jumpUrl = mergeBaseUrl("#/sparketl?resourceId=%s&version=%s&%s=%s&%s=%s");
        String resourceId = (String) requestRef.getRefJobContent().get("resourceId");
        String version = (String) requestRef.getRefJobContent().get("version");
        if(StringUtils.isBlank(resourceId) || StringUtils.isBlank(version)) {
            logger.info("resourceId or version is empty, maybe user {} want to create a new node.", requestRef.getUserName());
            resourceId = "";
            version = "";
        }
        jumpUrl = String.format(jumpUrl, resourceId, version, QueryJumpUrlConstant.NODE_ID.getKey(),
                QueryJumpUrlConstant.NODE_ID.getValue(), QueryJumpUrlConstant.PROJECT_NAME.getKey(), QueryJumpUrlConstant.PROJECT_NAME.getValue());
        return QueryJumpUrlResponseRef.newBuilder().setJumpUrl(jumpUrl).build();
    }

}
