package com.webank.wedatasphere.dss.scriptis.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.wedatasphere.dss.scriptis.pojo.entity.ScriptisProxyUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ScriptisProxyUserMapper extends BaseMapper<ScriptisProxyUser> {

    List<ScriptisProxyUser> selectProxyUserList(@Param("userName")String userName,@Param("expireTime") String expireTime);

    int insertUser(ScriptisProxyUser user);

    void deleteProxyUser(@Param("userName") String userName, @Param("proxyUserNames") String[] proxyUserNames);


    ScriptisProxyUser selectProxyUserByUser(@Param("userName") String userName,@Param("proxyUserName") String proxyUserName);


    int updateByUser(ScriptisProxyUser user);

}
