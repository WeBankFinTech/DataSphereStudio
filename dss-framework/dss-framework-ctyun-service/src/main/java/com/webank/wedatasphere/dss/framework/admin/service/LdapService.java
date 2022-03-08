package com.webank.wedatasphere.dss.framework.admin.service;

import javax.naming.NamingException;

public interface LdapService {

    void addUserWithPwd(String adminName, String adminPassword, String ldapUrl, String baseDN, String userName, String pwd,String group) throws NamingException;

    void updatePwd(String adminName, String adminPassword, String ldapUrl, String baseDN, String userName, String pwd) throws NamingException;

    boolean exist(String adminName, String adminPassword, String ldapUrl, String baseDN, String userName,String type) throws NamingException;
}
