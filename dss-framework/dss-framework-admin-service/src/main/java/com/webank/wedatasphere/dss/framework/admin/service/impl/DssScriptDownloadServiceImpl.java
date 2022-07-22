package com.webank.wedatasphere.dss.framework.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.webank.wedatasphere.dss.framework.admin.pojo.entity.DssScriptDownloadAudit;
import com.webank.wedatasphere.dss.framework.admin.service.DssScriptDownloadService;
import com.webank.wedatasphere.dss.framework.admin.xml.DssAuditMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Auther: Han Tang
 * @Date: 2022/1/11-01-11-15:29
 */
@Service
public class DssScriptDownloadServiceImpl extends ServiceImpl<DssAuditMapper, DssScriptDownloadAudit> implements DssScriptDownloadService {
    @Autowired
    public DssAuditMapper dssAuditMapper;

    @Override
    public List<DssScriptDownloadAudit> getDownloadAuditList(String userName, String startIme, String endTime) {
        return dssAuditMapper.getDownloadAuditList(userName, startIme, endTime);
    }



}
