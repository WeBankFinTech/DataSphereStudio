package com.webank.wedatasphere.dss.framework.admin.service;

import javax.naming.NamingException;

public interface LdapService {

    void addUser(String adminName, String adminPassword, String ldapUrl, String baseDN, String userName, String pwd) throws NamingException;

    void update(String adminName, String adminPassword, String ldapUrl, String baseDN, String userName, String pwd) throws NamingException;

    boolean exist(String adminName, String adminPassword, String ldapUrl, String baseDN, String userName) throws NamingException;
}
