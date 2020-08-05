package com.webank.wedatasphere.dss.server.dao;

import java.util.List;

import com.webank.wedatasphere.dss.server.entity.CtyunUser;

/**
 * The interface Ctyun user mapper.
 *
 * @author yuxin.yuan
 * @date 2020/07/29
 */
public interface CtyunUserMapper {

    /**
     * Gets by ctyun username.
     *
     * @param ctyunUsername
     *            the ctyun username
     * @return the by ctyun user
     */
    CtyunUser getByCtyunUsername(String ctyunUsername);

    /**
     * Gets by username.
     *
     * @param username
     *            the username
     * @return the by user
     */
    CtyunUser getByUsername(String username);

    /**
     * 插入用户信息.
     *
     * @param ctyunUser
     *            the ctyun user
     * @return the int
     */
    int insert(CtyunUser ctyunUser);

    /**
     * List by username left like.
     *
     * @param prefix
     *            the prefix
     * @return the list
     */
    List<CtyunUser> listByUsernameLeftLike(String prefix);
}
