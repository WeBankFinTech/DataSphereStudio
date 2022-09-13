package com.webank.wedatasphere.dss.scriptis.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.wedatasphere.dss.scriptis.pojo.entity.ScriptisProxyUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ScriptisProxyUserMapper extends BaseMapper<ScriptisProxyUser> {

    List<ScriptisProxyUser> selectProxyUserList(String userName);

    int insertUser(ScriptisProxyUser user);

}
