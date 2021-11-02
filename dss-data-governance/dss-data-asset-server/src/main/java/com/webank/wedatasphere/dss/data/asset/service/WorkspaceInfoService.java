package com.webank.wedatasphere.dss.data.asset.service;


import com.webank.wedatasphere.dss.data.common.exception.DataGovernanceException;


import java.util.List;

/**
 * created by cooperyang on 2020/3/15
 * Description:
 */
public interface WorkspaceInfoService {

    public List<String> getWorkspaceUsers(int workspaceId,String search) throws DataGovernanceException;

}
