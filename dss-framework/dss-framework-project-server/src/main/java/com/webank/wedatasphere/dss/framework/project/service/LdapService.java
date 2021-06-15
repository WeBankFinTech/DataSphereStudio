package com.webank.wedatasphere.dss.framework.project.service;

import javax.naming.NamingException;

public interface LdapService {

    public  void addUser(String adminName,String adminPassword,String ldapUrl,String baseDN,String userName,String pwd) throws NamingException;
}
