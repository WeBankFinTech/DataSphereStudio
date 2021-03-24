#!/bin/bash
source /etc/profile
user=$1
dir=$2
echo $1 $2;
id $user
if [ $? -ne 0 ]; then
  useradd $user -s /sbin/nologin
  echo "create user successfully"
fi
sudo mkdir -p $dir
sudo chown $user:$user $dir
