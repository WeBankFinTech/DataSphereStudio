package com.webank.wedatasphere.dss.appconn.sso;

import com.webank.wedatasphere.dss.appconn.core.impl.AbstractOnlySSOAppConn;

/**
 * @author enjoyyin
 * @date 2022-03-31
 * @since 1.1.0
 */
public class SSOAppConn extends AbstractOnlySSOAppConn {
    @Override
    protected void initialize() {
        if(getAppDesc() != null) {
            logger.info("Load a SSOAppConn " + getAppDesc().getAppName());
        }
    }
}
