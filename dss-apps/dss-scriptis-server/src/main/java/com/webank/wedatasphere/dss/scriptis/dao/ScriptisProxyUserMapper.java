package com.webank.wedatasphere.dss.scriptis.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.wedatasphere.dss.scriptis.pojo.entity.ScriptisProxyUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ScriptisProxyUserMapper extends BaseMapper<ScriptisProxyUser> {

    List<ScriptisProxyUser> selectProxyUserList(String userName);

    int insertUser(ScriptisProxyUser user);

    void deleteProxyUser(@Param("userName") String userName, @Param("proxyUserNames") String[] proxyUserNames);

}
