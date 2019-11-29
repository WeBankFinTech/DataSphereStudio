package com.webank.wedatasphere.dss.application.dao;

import com.webank.wedatasphere.dss.application.entity.LinkisUser;

/**
 * Created by chaogefeng on 2019/11/29.
 */
public interface LinkisUserMapper {
    LinkisUser getUserByName(String username);

    void registerLinkisUser(LinkisUser userDb);
}
