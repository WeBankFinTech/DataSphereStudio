#!/bin/bash
source /etc/profile
echo $2
cd $2
echo current path`pwd`
source ../venv/bin/activate
python $3 add_with_pw $4 -p $5
echo "******************LDAP USER CREATED***********************"
