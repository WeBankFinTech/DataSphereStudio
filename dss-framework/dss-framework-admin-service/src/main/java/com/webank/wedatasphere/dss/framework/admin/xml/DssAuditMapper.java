package com.webank.wedatasphere.dss.framework.admin.xml;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.wedatasphere.dss.framework.admin.pojo.entity.DssScriptDownloadAudit;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface DssAuditMapper extends BaseMapper<DssScriptDownloadAudit> {

    List<DssScriptDownloadAudit> getDownloadAuditList(@Param("creator") String creator,@Param("startTime") String startIme,@Param("endTime") String endTime);

}
