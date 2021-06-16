package com.webank.wedatasphere.dss.framework.project.service.impl;

import com.webank.wedatasphere.dss.framework.project.service.LdapService;
import com.webank.wedatasphere.dss.framework.project.utils.LdapUtils;
import org.springframework.stereotype.Service;

import javax.naming.NamingException;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.ldap.LdapContext;
@Service
public class LdapServiceImpl implements LdapService {


    /** add user to AD */
    @Override
    public void addUser(String adminName,String adminPassword,String ldapUrl,String baseDN,String userName,String pwd) throws NamingException {
        LdapContext ctx = LdapUtils.connectLDAP(adminName, adminPassword, ldapUrl);
        BasicAttributes basicAttributes = new BasicAttributes();
        BasicAttribute objectClassSet = new BasicAttribute("objectclass");
        objectClassSet.add("inetOrgPerson");
        basicAttributes.put(objectClassSet);
        basicAttributes.put("sn", userName);
        basicAttributes.put("cn", userName);
        basicAttributes.put("uid", userName);
        basicAttributes.put("userPassword", pwd);
        ctx.createSubcontext("uid="+userName+","+baseDN, basicAttributes);
        LdapUtils.closeContext(ctx);
    }



}
