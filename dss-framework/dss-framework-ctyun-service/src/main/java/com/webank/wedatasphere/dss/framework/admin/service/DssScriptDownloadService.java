package com.webank.wedatasphere.dss.framework.admin.service;

import com.webank.wedatasphere.dss.framework.admin.pojo.entity.DssScriptDownloadAudit;

import java.util.List;


import com.baomidou.mybatisplus.extension.service.IService;

/** @Auther: Han Tang @Date: 2022/1/11-01-11-15:11 */
public interface DssScriptDownloadService extends IService<DssScriptDownloadAudit> {

    List<DssScriptDownloadAudit> getDownloadAuditList(
            String userName, String startIme, String endTime);
}
