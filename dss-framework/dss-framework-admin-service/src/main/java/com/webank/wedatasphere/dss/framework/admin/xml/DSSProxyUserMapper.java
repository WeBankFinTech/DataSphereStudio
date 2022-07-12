package com.webank.wedatasphere.dss.framework.admin.xml;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.wedatasphere.dss.framework.admin.pojo.entity.DssAdminUser;
import com.webank.wedatasphere.dss.framework.admin.pojo.entity.DssProxyUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DSSProxyUserMapper extends BaseMapper<DssProxyUser> {
    public List<DssProxyUser> selectProxyUserList(String  userName);
    public int insertUser(DssProxyUser user);
    public List<DssProxyUser> getProxyUserList(@Param("userName") String  userName, @Param("proxyUserName") String proxyUserName);


}
