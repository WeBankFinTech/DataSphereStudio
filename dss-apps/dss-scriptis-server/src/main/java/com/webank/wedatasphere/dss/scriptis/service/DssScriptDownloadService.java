package com.webank.wedatasphere.dss.scriptis.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.webank.wedatasphere.dss.scriptis.pojo.entity.DssScriptDownloadAudit;

import java.util.List;


public interface DssScriptDownloadService extends IService<DssScriptDownloadAudit> {

    List<DssScriptDownloadAudit> getDownloadAuditList(String userName, String startIme, String endTime);

}
