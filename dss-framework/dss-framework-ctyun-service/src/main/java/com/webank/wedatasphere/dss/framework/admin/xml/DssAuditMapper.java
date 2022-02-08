package com.webank.wedatasphere.dss.framework.admin.xml;

import com.webank.wedatasphere.dss.framework.admin.pojo.entity.DssScriptDownloadAudit;

import org.apache.ibatis.annotations.Param;

import java.util.List;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface DssAuditMapper extends BaseMapper<DssScriptDownloadAudit> {

    List<DssScriptDownloadAudit> getDownloadAuditList(
            @Param("creator") String creator,
            @Param("startTime") String startIme,
            @Param("endTime") String endTime);
}
