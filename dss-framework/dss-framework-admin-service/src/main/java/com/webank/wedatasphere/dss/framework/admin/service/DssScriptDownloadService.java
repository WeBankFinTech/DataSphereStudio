package com.webank.wedatasphere.dss.framework.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.webank.wedatasphere.dss.framework.admin.pojo.entity.DssScriptDownloadAudit;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Auther: Han Tang
 * @Date: 2022/1/11-01-11-15:11
 */
public interface DssScriptDownloadService extends IService<DssScriptDownloadAudit> {

    List<DssScriptDownloadAudit> getDownloadAuditList( String userName, String startIme,  String endTime);

}
