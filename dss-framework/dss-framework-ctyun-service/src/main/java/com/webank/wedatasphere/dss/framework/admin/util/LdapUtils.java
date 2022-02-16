package com.webank.wedatasphere.dss.framework.admin.util;

import com.webank.wedatasphere.dss.framework.admin.service.impl.LdapServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

public class LdapUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(LdapUtils.class);

    public static LdapContext connectLDAP(String adminName, String adminPassword, String ldapUrl) throws NamingException {
        Hashtable<String, String> HashEnv = new Hashtable<String, String>();
        HashEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        HashEnv.put(Context.SECURITY_AUTHENTICATION, "simple");// "none","simple","strong"
        HashEnv.put(Context.SECURITY_PRINCIPAL, adminName);
        HashEnv.put(Context.SECURITY_CREDENTIALS, adminPassword);
        HashEnv.put(Context.PROVIDER_URL, ldapUrl);
        LdapContext ctx = new InitialLdapContext(HashEnv, null);
        return ctx;
    }


    public static  void closeContext(LdapContext ctx) throws NamingException {
        if (ctx != null) {
            ctx.close();
        }
    }


    /*
    * group 为 * 代表查询所有group
     */
    public static  int getMaxId(String adminName, String adminPassword, String ldapUrl, String baseDN, String type, String group) throws NamingException {
        LdapContext ctx = LdapUtils.connectLDAP(adminName, adminPassword, ldapUrl);
        SearchControls searchCtls = new SearchControls();
        searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        String returnedAtts[] = null;

        String searchFilter = "";
        if ("user".equals(type)) {
            baseDN="ou=user,"+baseDN;
            searchFilter = "uid=*";
            returnedAtts = new String[]{"uidNumber"};

        } else if ("group".equals(type)) {
            baseDN="ou=group,"+baseDN;
            searchFilter = "cn=" + group;
            returnedAtts = new String[]{"gidNumber"};
        }
        String searchBase = baseDN;
        searchCtls.setReturningAttributes(returnedAtts);
        NamingEnumeration entries = ctx.search(searchBase, searchFilter, searchCtls);
        if (!entries.hasMore()) {
            return 10000;
        }
        ArrayList<Integer> ids = new ArrayList<>();
        while (entries.hasMore()) {
            SearchResult sr = (SearchResult) entries.next();
            Attributes attributes = sr.getAttributes();
            if ("user".equals(type)) {
                int id = Integer.parseInt(attributes.get("uidNumber").get().toString());
                ids.add(id);
            } else if ("group".equals(type)) {
                int id = Integer.parseInt(attributes.get("gidNumber").get().toString());
                ids.add(id);
            }
        }

        Collections.sort(ids);
        return ids.get(ids.size() - 1);
    }



    public static void addUser(String adminName, String adminPassword, String ldapUrl, String baseDN, String userName, String pwd, String group) throws NamingException {
        LdapContext ctx = LdapUtils.connectLDAP(adminName, adminPassword, ldapUrl);
        BasicAttributes basicAttributes = new BasicAttributes();
        BasicAttribute objectClassSet = new BasicAttribute("objectclass");
        objectClassSet.add("top");
        objectClassSet.add("account");
        objectClassSet.add("posixAccount");
        objectClassSet.add("shadowAccount");
        basicAttributes.put(objectClassSet);
        basicAttributes.put("cn", userName);
        basicAttributes.put("uid", userName);
        basicAttributes.put("userPassword", pwd);
        int userId = getMaxId(adminName, adminPassword, ldapUrl, baseDN, "user", null) + 1;
        int groupId = getMaxId(adminName, adminPassword, ldapUrl, baseDN, "group", group);
        LOGGER.info(">>>>>>>>>>>>>add user:groupId="+groupId+",userId="+userId);
        baseDN = "ou=user," + baseDN;
        basicAttributes.put("uidNumber", userId+"");
        basicAttributes.put("gidNumber", groupId+"");
        basicAttributes.put("homeDirectory", "home/" + userName);
        basicAttributes.put("loginShell", "/bin/bash");
        ctx.createSubcontext("uid=" + userName + "," + baseDN, basicAttributes);
        LdapUtils.closeContext(ctx);
    }


    public static void addGroup(String adminName, String adminPassword, String ldapUrl, String baseDN, String groupName) throws NamingException {
        LdapContext ctx = LdapUtils.connectLDAP(adminName, adminPassword, ldapUrl);
        BasicAttributes basicAttributes = new BasicAttributes();
        BasicAttribute objectClassSet = new BasicAttribute("objectclass");
        objectClassSet.add("top");
        objectClassSet.add("posixGroup");
        basicAttributes.put(objectClassSet);
        int groupId = getMaxId(adminName, adminPassword, ldapUrl, baseDN, "group", "*") + 1;
        LOGGER.info(">>>>>>>>>>>>>add group:groupId="+groupId);
        baseDN = "ou=group," + baseDN;
        basicAttributes.put("cn", groupName);
        basicAttributes.put("gidNumber", groupId+"");
        basicAttributes.put("memberUid", groupName+"");
        ctx.createSubcontext("cn=" + groupName + "," + baseDN, basicAttributes);
        LdapUtils.closeContext(ctx);
    }
}
