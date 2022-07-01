package com.webank.wedatasphere.dss.appconn.dolphinscheduler.operation;

import com.webank.wedatasphere.dss.appconn.dolphinscheduler.DolphinSchedulerAppConn;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.sso.DolphinSchedulerTokenManager;
import com.webank.wedatasphere.dss.common.utils.MapUtils;
import com.webank.wedatasphere.dss.standard.app.structure.StructureRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.optional.AbstractOptionalOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;

/**
 * @author enjoyyin
 * @date 2022-03-18
 * @since 1.1.0
 */
public class DolphinSchedulerTokenGetOperation extends AbstractOptionalOperation<StructureRequestRef, ResponseRef> {

    @Override
    protected String getAppConnName() {
        return DolphinSchedulerAppConn.DOLPHINSCHEDULER_APPCONN_NAME;
    }

    @Override
    public String getOperationName() {
        return "getToken";
    }

    @Override
    public ResponseRef apply(StructureRequestRef ref) {
        String token = DolphinSchedulerTokenManager.getDolphinSchedulerTokenManager(getBaseUrl()).getToken(ref.getUserName());
        long expireTime = DolphinSchedulerTokenManager.getDolphinSchedulerTokenManager(getBaseUrl()).getTokenExpireTime(ref.getUserName());
        return ResponseRef.newExternalBuilder().setResponseMap(MapUtils.newCommonMap("token", token, "expireTime", expireTime)).success();
    }

}
