package com.webank.wedatasphere.dss.framework.appconn.service;

import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.framework.appconn.exception.AppConnQualityErrorException;

/**
 * @author enjoyyin
 * @date 2022-04-14
 * @since 0.5.0
 */
public interface AppConnQualityChecker {

    /**
     * 检查用户实现的 AppConn 是否存在质量问题
     * @throws AppConnQualityErrorException 如果存在质量问题，请抛出该异常
     */
    void checkQuality(AppConn appConn) throws AppConnQualityErrorException;

}
