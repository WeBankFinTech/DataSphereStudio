package com.webank.wedatasphere.dss.framework.admin.service.impl;

import com.webank.wedatasphere.dss.framework.admin.restful.DssAuditController;
import com.webank.wedatasphere.dss.framework.admin.service.LdapService;
import com.webank.wedatasphere.dss.framework.admin.util.LdapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.naming.ldap.LdapContext;
import java.util.ArrayList;
import java.util.Collections;

@Service
public class LdapServiceImpl implements LdapService {



    /**
     * add user to AD
     */
    @Override
    public void addUserWithPwd(String adminName, String adminPassword, String ldapUrl, String baseDN, String userName, String pwd, String group) throws NamingException {
        boolean groupIsExist = exist(adminName, adminPassword, ldapUrl,baseDN, group, "group");
        if(!groupIsExist){
            LdapUtils.addGroup(adminName, adminPassword, ldapUrl, baseDN, group);
        }
        LdapUtils.addUser(adminName, adminPassword, ldapUrl, baseDN, userName, pwd, group);

    }


    @Override
    public void updatePwd(String adminName, String adminPassword, String ldapUrl, String baseDN, String userName, String pwd) throws NamingException {
        LdapContext ctx = LdapUtils.connectLDAP(adminName, adminPassword, ldapUrl);
        ModificationItem[] mods = new ModificationItem[1];
        Attribute attr = new BasicAttribute("userPassword", pwd);
        // Support add, replace and remove an attribute.
        baseDN="ou=user,"+baseDN;
        mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, attr);
        ctx.modifyAttributes("uid=" + userName + "," + baseDN, mods);
        LdapUtils.closeContext(ctx);

    }

    @Override
    public boolean exist(String adminName, String adminPassword, String ldapUrl, String baseDN, String cn, String type) throws NamingException {
        LdapContext ctx = LdapUtils.connectLDAP(adminName, adminPassword, ldapUrl);
        SearchControls searchCtls = new SearchControls();
        searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        String searchFilter = null;
        String returnedAtts[] = null;

        if ("user".equals(type)) {
            searchFilter = "uid=" + cn;
            baseDN = "ou=group," + baseDN;
            returnedAtts = new String[]{"uid"};

        } else if ("group".equals(type)) {
            searchFilter = "cn=" + cn;
            baseDN = "ou=group," + baseDN;
            returnedAtts = new String[]{"cn"};

        }
        String searchBase = baseDN;
        searchCtls.setReturningAttributes(returnedAtts);
        boolean exist = false;
        NamingEnumeration entries = ctx.search(searchBase, searchFilter, searchCtls);
        if (entries.hasMore()) {
            exist = true;
        }
        LdapUtils.closeContext(ctx);
        return exist;
    }


    public static void main(String[] args) throws NamingException {
        LdapServiceImpl ldapService = new LdapServiceImpl();
//        ldapService.addUserWithPwd("cn=Manager,dc=example,dc=com", "hantang", "ldap://xxxxx:389", "dc=example,dc=com", "ht010023", "Th34082519861!","ht010014");
//        ldapService.updatePwd("cn=Manager,dc=example,dc=com", "hantang", "ldap://localhost:389", "dc=example,dc=com", "ht010023", "Th34082519861!");
    }

}
