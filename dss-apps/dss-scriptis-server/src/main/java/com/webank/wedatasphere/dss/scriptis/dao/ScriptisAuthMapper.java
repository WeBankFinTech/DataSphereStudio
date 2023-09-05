package com.webank.wedatasphere.dss.scriptis.dao;


import com.webank.wedatasphere.dss.scriptis.pojo.entity.DssUserLimit;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ScriptisAuthMapper {

    List<DssUserLimit> getUserLimits(@Param("limitName") String limitName);

}
