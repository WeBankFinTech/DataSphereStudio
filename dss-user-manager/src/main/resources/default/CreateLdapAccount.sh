#!/bin/bash
source /etc/profile
ldap_user=$1
ldap_password=$2
ldap_script_path=$3
source $ldap_script_path/tools/venv/bin/activate && sudo $ldap_script_path/tools/venv/bin/python $ldap_script_path/tools/bin/ldap_user.py add_with_pw $ldap_user -p $ldap_password
echo "******************LDAP USER CREATED***********************"
