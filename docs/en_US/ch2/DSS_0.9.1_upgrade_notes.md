# DSS 0.9.1 upgrade notes

## Environmental description

------

1. The user who installs the node machine deployment of DSS must have the permission to create the directory in hdfs
   1)If the hadoop cluster adopts the kerberos authentication mechanism, in order to prevent the ticket from expiring, you need to execute the knit -kt command on the client, for example:kinit -kt /etc/security/keytabs/hdfs.keytab  yarn/xxxxxxxx
   2)If the hadoop cluster uses the simple authentication mechanism, use hdfs dfs chmod authorization, such as: hdfs dfs chmod 775 /user/hive/xx
2. The user who deploys DSS on the DSS node has the permission to create hive database:
   1)If hadoop cluster using simple authentication mechanism, you can try the following manner authorized:
   hive>set system:user:name=dss;
   hive> grant all to user dss;

Currently, there is no automatic authorization in the script, and the user needs to execute the command manually.
2)If the Hadoop cluster adopts kerberos authentication, the kinit command is automatically executed in our script to obtain the ticket, and there is no need to execute the command manually. The user only needs to configure the kerberos related parameters. For the specific configuration, see the kerberos configuration chapter.

The newly created user should be configured in hive.users.in.admin.role in hive-site.xml.

1. LDAP must be installed (user authentication only supports LDAP), and there must be ou=user and ou=group entries in ldap, such as:  ou=user,dc=baidu,dc=com和ou=group,dc=baidu,dc=com.
   The ldap version supports 2.4.x, and the support of other versions is to be verified
2. Install sshpass service, yum -y install sshpass

## Upgrade installation instructions

------

The jar package involved in this change: Under the dss-server/lib directory:dss-application-0.9.1.jar,dss-server-0.9.1.jar,dss-user-manager-0.9.1.jar
Front-end static files: web
After replacing the above file with the latest one, then modify the following configuration file

### Installation and configuration file instructions

1. kerberos related

Function description: If the Hadoop cluster uses the kerberos authentication mechanism, the newly created user will be granted kerberos permissions
Configuration file path: dss-server/conf/linkis.properties

```
  	Parameters:
         wds.linkis.kerberos.enable.switch --Whether the cluster adopts the kerberos authentication mechanism, 0-do not use kerberos 1-use kerberos. If the Hadoop cluster does not use the kerberos authentication mechanism, none of the following parameters need to be configured.
         wds.linkis.kerberos.keytab.path  --The storage location of keytab on the DSS installation node can be arbitrarily specified, such as /etc/security/keytabs
         wds.linkis.kerberos.kdc.node  --Deploy the KDC service node IP, such as 192.168.1.1
         wds.linkis.kerberos.ssh.port  --Deploy the KDC service node SSH port number, generally 22
         wds.linkis.kerberos.kdc.user.name --Deploy a linux user name on the KDC node, the user must have sudo permission (very important!!!) for ssh operation
         wds.linkis.kerberos.kdc.user.password  --The login password of the kdc node user mentioned above, used for ssh operation
         wds.linkis.kerberos.realm --Kerberos manages the domain name of the hadoop cluster, please consult the cluster operation and maintenance personnel
         wds.linkis.kerberos.admin--A user granted the admin role on kerberos (such as hdfs, very important!!!, otherwise the authorization cannot be completed)
```

1. metastore related

Function description:  create  hive databases for newly created user and grant the newly created user permission
Parameter configuration file: dss-server/conf/linkis.properties

```
Parameters:
      wds.linkis.metastore.hive.hdfs.base.path  --The path where hive warehouse data is stored on hdfs, such as /user/hive/warehouse
      wds.dss.deploy.path --dss_linkis installation package path, such as /usr/local/dss_linkis
```

1. ldap related
   Function description: Create a new Entry under ou=user and ou=group of ldap for user login authentication
   Parameter configuration file path: tools/bin/ldap_user.py

```
LDAP_HOST -- Install the ldap service IP, such as 192.168.11.11
LDAP_BASEDN --The upper dn of ou=user or ou=group, such as dc=example,dc=com
LDAP_ADMIN -- The dn of the user logging in to the ldap service, such as cn=admin,dc=example,dc=cn
LDAP_PASS --Password for logging in to the ldap service
The first line in the ldap_user.py file #!/usr/local/tools/venv/bin/python, replace /user/local with the installation path of dss_linkis
```

## User manual

------

1. Access address [http://url](http://url/):port/#/userManger, you need to login as a super user (installation user)
2. Server configuration:
   If the hadoop cluster uses the simple authentication mechanism, the user needs to add the ip, login user name (with sudo permission), and password of each server in the yarn cluster.The underlying principle is that the server where dss is installed will ssh to each server in the yarn cluster, and then create a linux user.

If the kerberos authentication mechanism adopted by the hadoop cluster, just add an ip (such as 127.0.0.1), username, and password. If not added, the interface will report an exception, and subsequent versions will fix this bug.

1. WorkspaceRootPath, hdfsRootPath, resultRootPath, schedulerPath, DSS installation directory, Azkaban installation directory.The default value is consistent with the configuration in the installation configuration file config.sh. The directory can be either the hdfs directory, starting with hdfs:///, or the linux directory, starting with file:///.
2. The bottom layer will create a hive database for the user, the database name: xx_default, and give the permission to add, delete, modify, and select.