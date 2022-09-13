package com.webank.wedatasphere.dss.scriptis.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.webank.wedatasphere.dss.scriptis.dao.DssAuditMapper;
import com.webank.wedatasphere.dss.scriptis.pojo.entity.DssScriptDownloadAudit;
import com.webank.wedatasphere.dss.scriptis.service.DssScriptDownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class DssScriptDownloadServiceImpl extends ServiceImpl<DssAuditMapper, DssScriptDownloadAudit> implements DssScriptDownloadService {
    @Autowired
    public DssAuditMapper dssAuditMapper;

    @Override
    public List<DssScriptDownloadAudit> getDownloadAuditList(String userName, String startIme, String endTime) {
        return dssAuditMapper.getDownloadAuditList(userName, startIme, endTime);
    }

}
