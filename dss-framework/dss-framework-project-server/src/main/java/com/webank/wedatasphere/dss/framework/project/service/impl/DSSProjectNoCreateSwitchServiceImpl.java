package com.webank.wedatasphere.dss.framework.project.service.impl;

import com.webank.wedatasphere.dss.framework.project.dao.DSSProjectNoCreateSwitchMapper;
import com.webank.wedatasphere.dss.framework.project.service.DSSProjectNoCreateSwitchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Description
 */
@Service
public class DSSProjectNoCreateSwitchServiceImpl implements DSSProjectNoCreateSwitchService {
    @Autowired
    DSSProjectNoCreateSwitchMapper projectNameCheckSwitchMapper;

    @Override
    public Long getCountByAppconnInstanceId(Long appconnId) {
        return projectNameCheckSwitchMapper.getCountByAppconnInstanceId(appconnId);
    }
}
