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
APPCONN_INSTALL_IP=127.0.0.1
APPCONN_INSTALL_PORT=8088

#echo "Current path of init sql is ${DB_DML_PATH}"
LOCAL_IP="`ifconfig | grep 'inet' | grep -v '127.0.0.1' | cut -d: -f2 | awk '{ print $2}'`"

function isSuccess(){
  if [ $? -ne 0 ]; then
      echo "Failed to " + $1
      exit 1
  else
      echo "Succeed to" + $1
  fi
}

PROC_NAME=DSSProjectServerApplication
ProcNumber=`ps -ef |grep -w $PROC_NAME|grep -v grep|wc -l`
if [ $ProcNumber -le 0 ];then
   echo "${PROC_NAME} is not running,Please check whether DSS is installed"
   exit 1000
else
   echo "Begine to install appconn"
fi

##choose install mysql mode
function initInstallAppConn() {
  echo "Please select the type of installation component?"
  echo " 1: schedulis"
  echo " 2: visualis"
  echo " 3：Your AppConn Name"
  echo " 4：exit"
  read -p "Please input the choice:"  idx
  if [[ '1' = "$idx" ]];then
   APPCONN_NAME="schedulis"
  elif [[ '2' = "$idx" ]];then
    APPCONN_NAME="visualis"
  elif [[ '4' = "$idx" ]];then
    echo "no choice,exit!"
    exit 1
  else
     APPCONN_NAME=$idx
  fi
  echo "Current installation component is ${APPCONN_NAME}"

  echo ""
  echo "If this machine(127.0.0.1) is installed, enter 1"
  echo "For others, you need to enter a complete IP address."
  read -p "Please enter the ip of appconn: "  ip
  APPCONN_INSTALL_IP=$ip
  if [[ '1' = "$ip" ]];then
   APPCONN_INSTALL_IP="127.0.0.1"
  fi
  echo "You input ip is ${APPCONN_INSTALL_IP}"

  echo ""
  read -p "Please enter the port of appconn:"  port
  APPCONN_INSTALL_PORT=$port
  echo "You input ip is ${APPCONN_INSTALL_PORT}"
}

function replaceCommonIp() {
 if [[ $APPCONN_INSTALL_IP == "127.0.0.1" ]] || [[ $APPCONN_INSTALL_IP == "0.0.0.0" ]];then
    echo "APPCONN_INSTALL_IP is equals $APPCONN_INSTALL_IP, we will change it to ip address"
    APPCONN_INSTALL_IP=$LOCAL_IP
 fi
}

##choose execute mysql mode
function executeSQL() {
  TEMP_DB_DML_PATH=${SOURCE_ROOT}/dss-appconns/${APPCONN_NAME}/db
  DB_DML_PATH=$TEMP_DB_DML_PATH/init_real.sql
  cp -rf $TEMP_DB_DML_PATH/init.sql $DB_DML_PATH
  sed -i "s/APPCONN_INSTALL_IP/$APPCONN_INSTALL_IP/g"       $DB_DML_PATH
  sed -i "s/APPCONN_INSTALL_PORT/$APPCONN_INSTALL_PORT/g"   $DB_DML_PATH
  sed -i "s#DSS_INSTALL_HOME_VAL#$DSS_INSTALL_HOME#g" $DB_DML_PATH
  mysql -h$MYSQL_HOST -P$MYSQL_PORT -u$MYSQL_USER -p$MYSQL_PASSWORD -D$MYSQL_DB --default-character-set=utf8 -e "source $DB_DML_PATH"
  isSuccess "source $DB_DML_PATH"
  echo "the table update finished"
}

echo ""
echo "step1:Initialize installation settings"
initInstallAppConn
echo ""

echo "step2:replaceIp"
replaceCommonIp
echo ""

echo "step3:update database"
executeSQL
echo ""

echo "step4:refresh appconn load"
curl -H "Token-Code:BML-AUTH" -H "Token-User:hadoop" -X GET http://${GATEWAY_INSTALL_IP}:${GATEWAY_PORT}/api/rest_j/v1/dss/framework/project/appconn/${APPCONN_NAME}/load
echo ""