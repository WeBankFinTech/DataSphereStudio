package com.webank.wedatasphere.dss.scriptis.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.wedatasphere.dss.scriptis.pojo.entity.DssScriptDownloadAudit;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DssAuditMapper extends BaseMapper<DssScriptDownloadAudit> {

    List<DssScriptDownloadAudit> getDownloadAuditList(@Param("creator") String creator, @Param("startTime") String startIme, @Param("endTime") String endTime);

}
