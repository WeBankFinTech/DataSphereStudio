package com.webank.wedatasphere.dss.scriptis.service.impl;

import com.webank.wedatasphere.dss.common.entity.DSSWorkspace;
import com.webank.wedatasphere.dss.framework.proxy.pojo.entity.DssProxyUser;
import com.webank.wedatasphere.dss.framework.proxy.service.DssProxyUserService;
import com.webank.wedatasphere.dss.scriptis.dao.ScriptisProxyUserMapper;
import com.webank.wedatasphere.dss.scriptis.pojo.entity.ScriptisProxyUser;
import com.webank.wedatasphere.dss.scriptis.service.ScriptisProxyUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ScriptisProxyUserServiceImpl implements DssProxyUserService, ScriptisProxyUserService {

    @Resource
    private ScriptisProxyUserMapper dssProxyUserMapper;

    @Override
    public List<DssProxyUser> selectProxyUserList(String userName, DSSWorkspace workspace) {
        String expireTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        return new ArrayList<>(dssProxyUserMapper.selectProxyUserList(userName,expireTime));
    }

    @Override
    public void revokeProxyUser(String userName, String[] proxyUserNames) {
        dssProxyUserMapper.deleteProxyUser(userName, proxyUserNames);
    }

    @Override
    public int insertProxyUser(ScriptisProxyUser dssProxyUser) {
        return dssProxyUserMapper.insertUser(dssProxyUser);
    }

}
