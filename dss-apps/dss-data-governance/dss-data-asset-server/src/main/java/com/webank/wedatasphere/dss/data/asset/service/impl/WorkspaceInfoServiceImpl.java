package com.webank.wedatasphere.dss.data.asset.service.impl;

import com.webank.wedatasphere.dss.data.asset.dao.WorkspaceInfoMapper;
import com.webank.wedatasphere.dss.data.common.exception.DataGovernanceException;
import com.webank.wedatasphere.dss.data.asset.service.WorkspaceInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class WorkspaceInfoServiceImpl implements WorkspaceInfoService {

    @Autowired
    private WorkspaceInfoMapper workspaceInfoMapper;


    @Override
    public List<String> getWorkspaceUsers(int workspaceId,String search) throws DataGovernanceException {

        List<String> workspaceUsers = workspaceInfoMapper.getWorkspaceUsersName(workspaceId,search);
        return  workspaceUsers;
    }
}
