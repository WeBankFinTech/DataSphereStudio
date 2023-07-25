#!/bin/sh
#Actively load user env
source ~/.bashrc
shellDir=`dirname $0`
workDir=`cd ${shellDir}/..;pwd`

SOURCE_ROOT=${workDir}

#load config
source ${SOURCE_ROOT}/conf/config.sh
source ${SOURCE_ROOT}/conf/db.sh

APPCONN_NAME=''

LOCAL_IP="`ifconfig | grep 'inet' | grep -v '127.0.0.1' | cut -d: -f2 | awk '{ print $2}'`"

function isSuccess(){
  if [ $? -ne 0 ]; then
      echo "Failed to " $1
      exit 1
  else
      echo "Succeed to " $1
  fi
}


PROC_NAME=DSSProjectServerApplication
ProcNumber=`ps -ef | grep -w $PROC_NAME | grep -v grep | wc -l`
if [ $ProcNumber -le 0 ];then
   echo "${PROC_NAME} is not running, please ensure whether DSS is installed and started."
   exit 10
else
   echo "Begin to uninstall AppConn plugin..."
fi


function getUninstallAppConn() {
  echo "Please input the name of uninstallation AppConn, e.g: schedulis."
  read -p "Please input the AppConn name:"  idx
  if [[ 'exit' = "$idx" ]];then
    echo "exit!"
    exit 1
  else
    APPCONN_NAME=$idx
  fi
  echo "Current uninstallation AppConn is ${APPCONN_NAME}"
  echo ""
  echo -e "\e[1;31m Are you sure you want to uninstall AppConn ${APPCONN_NAME}, the workflow associated with the AppConn will not be available after uninstallation.\e[0m"
  echo -e "\e[1;31mIf you want to uninstall AppConn ${APPCONN_NAME}, please enter 1, otherwise enter 0.\e[0m"
  echo ""
  read -p "Please input your choice:" idx
  
  if [[ '0' = "$idx" ]]; then
    echo "exit!"
    exit 1
  fi
}

##choose execute mysql mode
function executeSQL() {
  TEMP_DB_DML_PATH=${SOURCE_ROOT}/dss-appconns/${APPCONN_NAME}/db
  DB_DML_PATH=$TEMP_DB_DML_PATH/uninstall.sql
  mysql -h$MYSQL_HOST -P$MYSQL_PORT -u$MYSQL_USER -p$MYSQL_PASSWORD -D$MYSQL_DB --default-character-set=utf8 -e "source $DB_DML_PATH"
  isSuccess "complete the cleanup of the database $DB_DML_PATH"
  echo "Implement the $TEMP_DB_DML_PATH/uninstall.sql for $APPCONN_NAME succeed."
}


function deleteDML() {
  echo ""
  echo -e "\e[1;31m If you want to delete AppConn ${APPCONN_NAME} file, please enter 1, otherwise enter 0.\e[0m"
  echo ""
  read -p "Please input your choice:" idx
  if [[ '1' = "$idx" ]];then
    DML_PATH=${SOURCE_ROOT}/dss-appconns/${APPCONN_NAME}
    suffix=.zip
    rm -rf $DML_PATH$suffix
    rm -rf $DML_PATH
    isSuccess "complete the cleanup of the ${APPCONN_NAME} file."
  fi
}


echo ""
echo "Step1: Get the uninstall AppConn name."
getUninstallAppConn
echo ""

echo "Step2: Delete AppConn $APPCONN_NAME database info."
executeSQL
echo ""

echo "Step3: Clear the plugin of $APPCONN_NAME AppConn in DSS."
deleteDML
echo ""
echo "Now try to delete the plugin of ${APPCONN_NAME} AppConn in all DSS micro-services."
echo "The following 2 ways can take effect:"
echo "1. Restart DSS, we will use sh $SOURCE_ROOT/sbin/dss-start-all.sh to restart, it will spend 1 minute."
echo "2. Do nothing, just wait for 5 minutes. Since the DSS micro-services will refresh all the AppConn plugins every 10 minutes."
echo ""
read -p "Please input the choise: "  choise
if [[ '1' = "$choise" ]]; then
  echo "You chose to restart dss-framework-project micro-services, now try to restart ..."
  sh $SOURCE_ROOT/sbin/dss-start-all.sh
else
  echo "You chose to wait for 5 minutes."
fi