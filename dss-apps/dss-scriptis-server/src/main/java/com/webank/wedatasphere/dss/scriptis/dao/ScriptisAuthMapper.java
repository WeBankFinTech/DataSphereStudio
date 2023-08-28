package com.webank.wedatasphere.dss.scriptis.dao;


import com.webank.wedatasphere.dss.scriptis.pojo.entity.DssConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public class ScriptisAuthMapper {

    List<DssConfig> getUserLimits(@Param("limitName") String limitName);

}
