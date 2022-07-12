package com.webank.wedatasphere.dss.data.asset.service;


import com.webank.wedatasphere.dss.data.common.exception.DataGovernanceException;


import java.util.List;

public interface WorkspaceInfoService {

    public List<String> getWorkspaceUsers(int workspaceId,String search) throws DataGovernanceException;

}
