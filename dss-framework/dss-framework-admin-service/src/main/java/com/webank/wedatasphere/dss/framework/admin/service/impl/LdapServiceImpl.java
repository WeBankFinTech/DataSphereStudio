package com.webank.wedatasphere.dss.framework.admin.service.impl;

import com.webank.wedatasphere.dss.framework.admin.service.LdapService;
import com.webank.wedatasphere.dss.framework.admin.util.LdapUtils;
import org.springframework.stereotype.Service;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
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
        ctx.createSubcontext("uid=" + userName + "," + baseDN, basicAttributes);
        LdapUtils.closeContext(ctx);
    }


    @Override
    public void update(String adminName,String adminPassword,String ldapUrl,String baseDN,String userName,String pwd) throws NamingException {
        LdapContext ctx = LdapUtils.connectLDAP(adminName, adminPassword, ldapUrl);
        ModificationItem[] mods = new ModificationItem[1];
        Attribute attr = new BasicAttribute("userPassword", pwd);
          // Support add, replace and remove an attribute.
        mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, attr);
        ctx.modifyAttributes("uid="+userName+","+baseDN, mods);
        LdapUtils.closeContext(ctx);

    }
    @Override
    public boolean exist(String adminName,String adminPassword,String ldapUrl,String baseDN,String userName) throws NamingException {
        LdapContext ctx = LdapUtils.connectLDAP(adminName, adminPassword, ldapUrl);
        SearchControls searchCtls = new SearchControls();
        searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        String searchFilter = "uid=" + userName;
        String[] returnedAtts = { "cn" };
        searchCtls.setReturningAttributes(returnedAtts);
        boolean exist = false;

        NamingEnumeration entries = ctx.search(baseDN, searchFilter, searchCtls);
        if(entries.hasMore()){
            exist = true;
        }
        LdapUtils.closeContext(ctx);
        return  exist;

        }

}
