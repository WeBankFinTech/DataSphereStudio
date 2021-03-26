#!/bin/bash
source /etc/profile
user_name=$1
db_name=$2
path=$3
realm=$4
admin=$5
ktPath=$6
host_name=`hostname`
kerberos_enable=$7
echo $kerberos_enable
if [ $kerberos_enable = "0" ]; then
   echo "kerberos is disabled"
else
 echo "kerberos is enabled"
 kinit -kt $ktPath/$admin.keytab $admin/${host_name}@${realm}
fi

  #二、metastore操作
hive -e "create database if not exists $db_name"
if [[ $? -ne 0 ]]; then
    echo "create database failed!"
else
     #修改数据库所属，将principal用户添加到metastore侧hive-site.xml hive.users.in.admin.role中
   if [ $kerberos_enable = "0" ]; then
       hive -e "grant all on database $db_name to user $user_name"
   else
     hive -e "set role admin; grant all on database $db_name to user $user_name"
   fi
fi

  #三、hdfs操作
if [[ $? -ne 0 ]]; then
    #回滚
  hive -e "drop database $db_name"
  echo "grant database failed,rollback finished!"
else
  echo "grant database $db_name successfully!"
    #修改hdfs路径所属
  hdfs dfs -chown $user_name:$user_name $path
    #修改hdfs路径权限
  hdfs dfs -chmod -R 700 $path
  echo "hdfs operation successfully!"
fi
