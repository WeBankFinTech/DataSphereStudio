# DSS-0.9.1升级说明

## 环境说明

1. 安装DSS节点上部署DSS用户必须有hdfs创建目录的权限
   1)如果hadoop集群采用kerberos认证机制，为防止票据过期，则需要在客户端执行 knit -kt命令,比如：kinit -kt /etc/security/keytabs/hdfs.keytab  yarn/xxxxxxxx
   2)如果hadoop集群采用simple认证机制，则使用hdfs dfs chmod 授权，比如:hdfs dfs chmod 775 /user/hive/xx
2. 安装DSS节点上部署DSS的用户具有创建hive database权限问题:
   1)如果hadoop集群采用simple认证机制，可以尝试如下方式授权：
   hive>set system:user:name=dss;
   hive> grant all to user dss
   目前并未在脚本中自动授权，需要用户手动执行命令。
   2)如果hadoop集群采用kerberos认证，在我们的脚本中自动执行了kinit命令以获取票据，不需要手工执行命令，用户只需要要配置kerberos相关的参数，具体配置见kerberos配置章节。
   新建的用户要在 hive-site.xml 中hive.users.in.admin.role配置。
3. 必须安装有LDAP（用户认证只支持LDAP），ldap中必须有ou=user和ou=group条目，比如：ou=user,dc=baidu,dc=com和ou=group,dc=baidu,dc=com。ldap版本支持2.4.x,其他版本的支持情况待验证
4. 安装sshpass服务，yum -y install sshpass

## 版本升级安装说明

本次改动涉及的的jar包:dss-server/lib目录下: dss-application-0.9.1.jar,dss-server-0.9.1.jar,dss-user-manager-0.9.1.jar
前端静态文件：web
将以上文件替换成最新的后，然后修改下面的配置文件

### 安装及配置文件说明

1. kerberos相关
   功能说明：如果hadoop集群采用kerberos认证机制，则会给新建的用户授予kerberos权限
   配置文件路径：dss-server/conf/linkis.properties

```
  	参数：
         wds.linkis.kerberos.enable.switch --集群是否采用kerberos认证机制，0-不采用kerberos 1-采用kerberos。如果hadoop集群不采用kerberos认证机制，则下面的参数都不需要配置。
         wds.linkis.kerberos.keytab.path  --keytab在DSS安装节点上的存放位置，可以任意指定，比如 /etc/security/keytabs  
         wds.linkis.kerberos.kdc.node  --部署KDC服务节点IP,比如192.168.1.1
         wds.linkis.kerberos.ssh.port  --部署KDC服务节点SSH端口号，一般都是22
         wds.linkis.kerberos.kdc.user.name --部署KDC节点上的一个linux用户名,该用用户必须有sudo权限(重要，重要！！！)，用于ssh操作
         wds.linkis.kerberos.kdc.user.password  --上面提到的kdc节点用户的登录密码，用于ssh操作
         wds.linkis.kerberos.realm --kerberos管理hadoop集群的域名，请咨询集群运维人员。
         wds.linkis.kerberos.admin--kerberos上的一个被授予admin角色的用户（如hdfs,非常重要！！！！，否则无法完成授权）
```

1. metastore相关
   功能说明：给新建用户hive库，并授予新建用户权限
   参数配置文件：dss-server/conf/linkis.properties

```
	参数：
      wds.linkis.metastore.hive.hdfs.base.path  --hive仓库数据存储在在hdfs上的路径，比如 /user/hive/warehouse
      wds.dss.deploy.path --dss_linkis安装包路径，比如 /usr/local/dss_linkis
```

3.ldap相关
功能说明：在ldap的ou=user和ou=group下新建一个Entry，用于用户登录验证
参数配置文件路径：安装包下的tools/bin/ldap_user.py

```
LDAP_HOST -- 安装ldap服务IP，比如192.168.11.11
LDAP_BASEDN --ou=user或ou=group的上层dn，比如 dc=example,dc=com
LDAP_ADMIN -- 登录ldap服务的用户dn 比如 'cn=admin,dc=example,dc=cn'
LDAP_PASS --登录ldap服务的密码
ldap_user.py文件中第一行 #!/usr/local/tools/venv/bin/python,将/user/local换成dss_linkis的安装路径
```

## 使用说明

1. 访问地址 http://url:port/#/userManger,需要用超级用户(安装用户)登录
2. 服务器配置：
   如果hadoop集群采用的是simple认证机制，则用户需要添加yarn集群各服务器的ip、登录用户名（具有sudo权限）、密码。其底层的原理是安装dss的服务器会ssh到yarn集群的各服务器,然后创建linux用户。
   如果hadoop集群采用的kerberos认证机制，则随便添加一个ip（比如127.0.0.1）,用户名,密码。如果不添加接口会报异常，后续版本会修复此bug
3. workspaceRootPath,hdfsRootPath,resultRootPath,schedulerPath,DSS安装目录，Azkaban安装目录。
   其默认值和安装配置文件config.sh里配置的保持一致。其目录既可以是hdfs目录,以hdfs:///开头，也可以是linux目录，以file:///开头
4. 底层会给用户创建hive库,库名：xx_default,并赋予增删改查权限