package com.webank.wedatasphere.dss.framework.project.service;


import java.util.List;

/**
 * Description
 */

public interface DSSProjectNoCreateSwitchService {
    /**
     * 获取不创建工程appconnInstance配置数量
     * @param appconnInstanceId
     * @return
     */
    Long getCountByAppconnInstanceId(Long appconnInstanceId);
}
