package com.webank.wedatasphere.dss.framework.workspace.util;

import com.webank.wedatasphere.dss.framework.workspace.conf.LdapConf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.Properties;

/**
 * @author：lizhao
 * @date：2021/10/19 7:25 下午
 */
@Component
public class LdapServerHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(LdapServerHelper.class);

    DirContext connection;

    /**
     * 初始化ldap Root（admin）用户
     */
    @PostConstruct
    public void init() {
        Properties env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY, LdapConf.INITIAL_CONTEXT_FACTORY.value());
        env.put(Context.PROVIDER_URL, LdapConf.LDAP_URL.value());
        env.put(Context.SECURITY_PRINCIPAL, LdapConf.SECURITY_PRINCIPAL.value());
        env.put(Context.SECURITY_CREDENTIALS, LdapConf.SECURITY_CREDENTIALS.value());
        try {
            connection = new InitialDirContext(env);
        } catch (NamingException e) {
            LOGGER.error("Initial dirContext error!", e);
        }
    }

    public void addUser(String userDetail, String user) {
        Attributes attributes = new BasicAttributes();
        Attribute attribute = new BasicAttribute("objectClass");
        attribute.add("inetOrgPerson");

        attributes.put(attribute);
        attributes.put("sn", userDetail);
        try {
            connection.createSubcontext("cn=" + user + "," + LdapConf.USERS_BASE_DN.value(), attributes);
        } catch (NamingException e) {
            LOGGER.error("addUser error!", e);
        }
    }

    public void modifyUserToGroup(String username, String groupName) {
        ModificationItem[] mods = new ModificationItem[1];
        Attribute attribute = new BasicAttribute("uniqueMember", "cn=" + username + "," + LdapConf.USERS_BASE_DN.value());
        mods[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE, attribute);
        try {
            connection.modifyAttributes("cn=" + groupName + "," + LdapConf.GROUPS_BASE_DN.value(), mods);
        } catch (NamingException e) {
            LOGGER.error("modifyUserToGroup error!", e);
        }
    }


    public void deleteUserFromGroupName(String userName, String groupName) {
        ModificationItem[] mods = new ModificationItem[1];
        Attribute attribute = new BasicAttribute("uniqueMember", "cn=" + userName + "," + LdapConf.USERS_BASE_DN.value());
        mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, attribute);
        try {
            connection.modifyAttributes("cn=" + groupName + ",ou=groups,ou=system", mods);
        } catch (NamingException e) {
            LOGGER.error("deleteUserFromGroupName error!", e);
        }
    }

    public void updateUserPassword(String username, String password) {
        try {
            ModificationItem[] mods = new ModificationItem[1];
            mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("userPassword", password));
            connection.modifyAttributes("cn=" + username + "," + LdapConf.USERS_BASE_DN.value(), mods);
        } catch (NamingException e) {
            LOGGER.error("updateUserPassword error!", e);
        }
    }
}
