#!/bin/bash
source /etc/profile
user=$1
dir=$2
echo $1 $2
id $user
if [ $? -ne 0 ]; then
  sudo useradd $user -s /sbin/nologin
  echo "create user successfully"
fi

hdfs dfs -mkdir -p $dir
hdfs dfs -chown $user:$user $dir
