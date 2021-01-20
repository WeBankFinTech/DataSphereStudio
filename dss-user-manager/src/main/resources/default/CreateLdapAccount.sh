#!/bin/bash
source /etc/profile
server_host=$1
server_login_user=$2
server_login_password=$3
server_python_path=$4
server_ldap_source_path=$5
ldap_user=$6
ldap_password=$7
echo "$server_login_password ssh $server_login_user@$server_host sudo python $server_python_path add_with_pw $ldap_user -p $ldap_password"
sshpass -p $server_login_password ssh $server_login_user@$server_host "sudo python $server_python_path add_with_pw $ldap_user -p $ldap_password"

echo "******************LDAP USER CREATED***********************"
