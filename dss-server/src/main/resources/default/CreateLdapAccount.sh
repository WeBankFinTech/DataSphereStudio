#!/bin/bash
source /etc/profile
LDAP_SCRIPTSERVER=$1
LDAP_SCRIPT_ROOT=$2
LDAP_SCRIPT=$3
NEW_USER=$4
PASSWORD=$5

ssh -t $LDAP_SCRIPTSERVER "sudo su - root -c 'cd $LDAP_SCRIPT_ROOT && source ../venv/bin/activate  && python $LDAP_SCRIPT add_with_pw $NEW_USER -p $PASSWORD && deactivate'"
echo "******************LDAP USER CREATED***********************"

#python ldap_user.py add_with_pw wuwenchao -p 8BFZhZbiVDrDQrWF