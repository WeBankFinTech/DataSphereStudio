package com.webank.wedatasphere.dss.framework.workspace.conf;

import com.webank.wedatasphere.linkis.common.conf.CommonVars;


public interface LdapConf {

    CommonVars<String> LDAP_URL = CommonVars.apply("wds.linkis.ldap.proxy.url", "ldap://localhost:389/");

    CommonVars<String> INITIAL_CONTEXT_FACTORY = CommonVars.apply("wds.linis.ldap.initial.context.factory", "com.sun.jndi.ldap.LdapCtxFactory");

    CommonVars<String> SECURITY_PRINCIPAL = CommonVars.apply("wds.linis.ldap.security.principal", "cn=admin,dc=shineweng,dc=com");

    CommonVars<String> SECURITY_CREDENTIALS = CommonVars.apply("wds.linis.ldap.initial.security.credentials", "");

    CommonVars<String> USERS_BASE_DN = CommonVars.apply("wds.linkis.ldap.proxy.users.baseDN", "");

    CommonVars<String> GROUPS_BASE_DN = CommonVars.apply("wds.linkis.ldap.proxy.groups.baseDN", "");

    CommonVars<String> DEFAULT_USER_PASSWORD = CommonVars.apply("wds.linis.ldap.default.userPassword", "");
}
