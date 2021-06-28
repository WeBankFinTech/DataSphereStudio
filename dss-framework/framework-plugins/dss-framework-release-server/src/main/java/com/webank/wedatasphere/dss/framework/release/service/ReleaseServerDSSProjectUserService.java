package com.webank.wedatasphere.dss.framework.release.service;

public interface ReleaseServerDSSProjectUserService {

    /**
     * 是否有发布权限
     * 
     * @param projectId
     * @param username
     * @return
     */
    public boolean isPublishAuth(Long projectId, String username);

}
