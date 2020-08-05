package com.webank.wedatasphere.dss.server.service;

import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.server.entity.CtyunUser;

/**
 * The interface Ctyun user service.
 *
 * @author yuxin.yuan
 * @date 2020/07/31
 */
public interface CtyunUserService {

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
     * 创建账户.
     *
     * @param ctyunUser
     *            the ctyun user
     * @return the boolean
     * @throws DSSErrorException
     *             the dss error exception
     */
    boolean createUser(CtyunUser ctyunUser) throws DSSErrorException;

    /**
     * 添加用户到数据库.
     *
     * @param ctyunUser
     *            the ctyun user
     * @return the boolean
     */
    boolean addUser(CtyunUser ctyunUser);

}
