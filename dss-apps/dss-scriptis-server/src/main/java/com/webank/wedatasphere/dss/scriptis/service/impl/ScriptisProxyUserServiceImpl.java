package com.webank.wedatasphere.dss.scriptis.service.impl;

import com.webank.wedatasphere.dss.common.entity.DSSWorkspace;
import com.webank.wedatasphere.dss.framework.proxy.pojo.entity.DssProxyUser;
import com.webank.wedatasphere.dss.framework.proxy.service.DssProxyUserService;
import com.webank.wedatasphere.dss.scriptis.dao.ScriptisProxyUserMapper;
import com.webank.wedatasphere.dss.scriptis.pojo.entity.ScriptisProxyUser;
import com.webank.wedatasphere.dss.scriptis.service.ScriptisProxyUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScriptisProxyUserServiceImpl implements DssProxyUserService, ScriptisProxyUserService {

    @Resource
    private ScriptisProxyUserMapper dssProxyUserMapper;

    @Override
    public List<DssProxyUser> selectProxyUserList(String userName, DSSWorkspace workspace) {
        return new ArrayList<>(dssProxyUserMapper.selectProxyUserList(userName));
    }

    @Override
    public int insertProxyUser(ScriptisProxyUser dssProxyUser) {
        return dssProxyUserMapper.insertUser(dssProxyUser);
    }

}
