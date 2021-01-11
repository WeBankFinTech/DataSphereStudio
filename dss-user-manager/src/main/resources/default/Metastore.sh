#!/bin/bash

user_name = $1
db_name = $2
path = $3

#一、kerberos认证
kinit -kt /etc/security/keytabs/${user_name}.keytab ${user_name}/nm-bigdata-030066029.ctc.local@EWS.BIGDATA.CHINATELECOM.CN.UAT

#二、metastore操作
hive -e "create database is not exists $db_name"
if [[ $? -ne 0 ]]; then
    echo "create database failed"
else
   #修改数据库所属，需要hive metastore开启赋权功能，并将hdfs用户添加到admin中
   hive -e "set role admin ; grant all on database $db_name to user $user_name"
fi

#三、hdfs操作
if [[ $? -ne 0 ]]; then
  #修改hdfs路径所属
  hdfs dfs -chown $user_name:$user_name $path
  #修改hdfs路径权限
  hdfs dfs -chmod -R 700 $user_name
else
  #回滚
  hive -e "drop database $db_name"
fi