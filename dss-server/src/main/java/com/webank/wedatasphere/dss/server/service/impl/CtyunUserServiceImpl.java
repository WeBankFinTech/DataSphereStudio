package com.webank.wedatasphere.dss.server.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.server.dao.CtyunUserMapper;
import com.webank.wedatasphere.dss.server.entity.CtyunUser;
import com.webank.wedatasphere.dss.server.service.CtyunUserService;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.DES;

/**
 * The type Ctyun user service.
 *
 * @author yuxin.yuan
 * @date 2020/07/31
 */
@Service
public class CtyunUserServiceImpl implements CtyunUserService {

    private static final Integer PASSWORD_MIN_LENGTH = 10;

    private Lock addUserLock = new ReentrantLock();

    @Autowired
    private CtyunUserMapper ctyunUserMapper;

    @Override
    public CtyunUser getByCtyunUsername(String ctyunUsername) {
        return ctyunUserMapper.getByCtyunUsername(ctyunUsername);
    }

    @Override
    public CtyunUser getByUsername(String username) {
        return ctyunUserMapper.getByUsername(username);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createUser(CtyunUser ctyunUser) throws DSSErrorException {
        if (StringUtils.isEmpty(ctyunUser.getPassword())) {
            // 设置随机密码（包含字母、数字、特殊字符）
            ctyunUser.setPassword(generatePassword(PASSWORD_MIN_LENGTH));
        }
        // 校验密码
        String regex = String.format("^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[~!@#$%%^&*.?])[0-9a-zA-Z~!@#$%%^&*.?]{%s,}$",
            PASSWORD_MIN_LENGTH);
        if (!ctyunUser.getPassword().matches(regex)) {
            throw new DSSErrorException(160001, "密码应含有至少10位字母、数字、特殊字符（~!@#$%^&*.?）");
        }

        // 用户信息存储到数据库中
        if (!addUser(ctyunUser)) {
            return false;
        }

        // todo 调用LDAP接口添加用户

        return true;
    }

    @Override
    public boolean addUser(CtyunUser ctyunUser) {
        // 密码加密
        byte[] key = ctyunUser.getCtyunUsername().getBytes();
        Arrays.sort(key);
        DES des = SecureUtil.des(key);
        String encryptPassword = des.encryptHex(ctyunUser.getPassword());
        ctyunUser.setPassword(encryptPassword);

        String username = StringUtils.substringBefore(ctyunUser.getCtyunUsername(), "@");
        addUserLock.lock();
        try {
            // 如果该用户名不存在，则新建用户
            if (ctyunUserMapper.getByUsername(username) == null) {
                ctyunUser.setUsername(username);
                return 1 == ctyunUserMapper.insert(ctyunUser);
            }
            // 如果该用户名存在，则添加后缀（_{no})，生成新用户名
            String prefix = username + "_";
            List<CtyunUser> ctyunUsers = listByUsernameLeftLike(prefix);
            for (int index = 2;; ++index) {
                String newUsername = prefix + index;
                Optional<CtyunUser> filteredUser =
                    ctyunUsers.stream().filter(user -> newUsername.equals(user.getUsername())).findFirst();
                if (!filteredUser.isPresent()) {
                    ctyunUser.setUsername(newUsername);
                    return 1 == ctyunUserMapper.insert(ctyunUser);
                }
            }
        } finally {
            addUserLock.unlock();
        }
    }

    public List<CtyunUser> listByUsernameLeftLike(String prefix) {
        return ctyunUserMapper.listByUsernameLeftLike(prefix);
    }

    private String generatePassword(int pwdLength) {
        List<String> charSetList = new ArrayList<>();
        charSetList.add("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
        charSetList.add("1234567890");
        charSetList.add("~!@#$%^&*.?");

        Integer[] indexArray = new Integer[pwdLength];
        Arrays.fill(indexArray, 0);
        indexArray[0] = 1;
        indexArray[1] = 1;
        indexArray[2] = 2;
        List<Integer> index = Arrays.asList(indexArray);
        Collections.shuffle(index);

        StringBuffer password = new StringBuffer();
        Random r = new Random();
        for (int i = 0; i < index.size(); i++) {
            String charSet = charSetList.get(index.get(i));
            password.append(charSet.charAt(r.nextInt(charSet.length())));
        }

        return password.toString();
    }

}
