#!/bin/bash
source /etc/profile
server_user_strs=$1
echo $server_user_strs
add_user_name=$2
add_user_password=$3
server_user_array=(${server_user_strs//,/ })
for server_user_str in ${server_user_array[@]}
do
   server_user_info=(${server_user_str//#/ })
   server_host=${server_user_info[0]}
   server_user_name=${server_user_info[1]}
   server_user_password=${server_user_info[2]}
   echo "${server_host},${server_user_name},${server_user_password}"

   sudo sshpass -p $server_user_password ssh -o ConnectTimeout=1  $server_user_name@$server_host "echo success"
   [ $? -ne 0 ] && echo "登录主机${server_host}失败" && exit 254
done

echo "************服务器网络校验通过,开始创建用户*****************"

for server_user_str in ${server_user_array[@]}
do

   server_user_info=(${server_user_str//#/ })
   server_host=${server_user_info[0]}
   server_user_name=${server_user_info[1]}
   server_user_password=${server_user_info[2]}
   sshpass -p $server_user_password ssh $server_user_name@$server_host "sudo useradd $add_user_name -s /sbin/nologin"
    #sshpass -p $server_user_password ssh $server_user_name@$server_host "sudo useradd $add_user_name && echo $add_user_password |sudo -i  passwd --stdin $add_user_name"

 #|sudo -i  passwd --stdin $add_user_name
   [ $? -ne 0 ] && echo "创建用户失败：${host}失败" && exit 254
done

