#!/bin/bash
echo "----------------- set env  ---------------------"
source /etc/profile
echo "----------------- set ssh  ---------------------"
echo -e  'y\n'|ssh-keygen -q -t rsa -N "" -f ~/.ssh/id_rsa
/ssh_copy_id.sh root pass localhost
echo "----------------- start mysql  ---------------------"
nohup /opt/mysql-5.7.34-el7-x86_64/bin/mysqld --defaults-file=/opt/mysql-5.7.34-el7-x86_64/my.cnf --user=root &
echo "----------------- start hadoop  ---------------------"
/opt/hadoop-2.7.2/sbin/start-all.sh
echo "----------------- start spark  ---------------------"
/opt/spark-2.4.8-bin-hadoop2.7/sbin/start-all.sh
echo "----------------- start hive  ---------------------"
nohup /opt/apache-hive-1.2.1-bin/bin/hive --service metastore &
echo "----------------- start dss  ---------------------"
/opt/dss/bin/start-all.sh
