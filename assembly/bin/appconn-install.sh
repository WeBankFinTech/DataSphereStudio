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
      echo "Succeed to " + $1
  fi
}

PROC_NAME=DSSServerApplication
ProcNumber=`ps -ef | grep -w $PROC_NAME | grep -v grep | wc -l`
if [ $ProcNumber -le 0 ];then
   echo "${PROC_NAME} is not running, please ensure whether DSS is installed and started."
   exit 10
else
   echo "Begine to install new AppConn plugin..."
fi

##choose install mysql mode
function initInstallAppConn() {
  if [ ! -z $1 ];then
    APPCONN_NAME=$1
    echo "Begin to install default appconn:$APPCONN_NAME"
  else
    echo "Please input the name of installation AppConn, e.g: schedulis."
    read -p "Please input the AppConn name:"  idx
    if [[ 'exit' = "$idx" ]];then
      echo "exit!"
      exit 1
    else
      APPCONN_NAME=$idx
    fi
    echo "Current installation AppConn is ${APPCONN_NAME}"
    echo ""
    echo "Please input the installed IP address of $APPCONN_NAME."
    echo "e.g: if you have installed $APPCONN_NAME with 192.168.1.1:8080, please input ip in 192.168.1.1"
    read -p "Please input the IP: "  ip
    APPCONN_INSTALL_IP=$ip
    echo ""
    echo "e.g: if you have installed $APPCONN_NAME with 192.168.1.1:8080, please input port in 8080"
    read -p "Please input the port:"  port
    APPCONN_INSTALL_PORT=$port
  fi

  echo ""
  replaceCommonIp
  echo "The base url of $APPCONN_NAME is http://${APPCONN_INSTALL_IP}:${APPCONN_INSTALL_PORT}/."
}

function replaceCommonIp() {
 if [[ $APPCONN_INSTALL_IP == "127.0.0.1" ]] || [[ $APPCONN_INSTALL_IP == "0.0.0.0" ]];then
    echo "since you input the ip to $APPCONN_INSTALL_IP, we will change it to real ip $LOCAL_IP."
    APPCONN_INSTALL_IP=$LOCAL_IP
 fi
}

function replaceDefaultAppconnSql() {
    sed -i "s#EVENTCHECKER_JDBC_URL#$EVENTCHECKER_JDBC_URL#g" $DB_DML_PATH
    sed -i "s#EVENTCHECKER_JDBC_USERNAME#$EVENTCHECKER_JDBC_USERNAME#g" $DB_DML_PATH
    sed -i "s#EVENTCHECKER_JDBC_PASSWORD#$EVENTCHECKER_JDBC_PASSWORD#g" $DB_DML_PATH

    sed -i "s#DATACHECKER_JOB_JDBC_URL#$DATACHECKER_JOB_JDBC_URL#g" $DB_DML_PATH
    sed -i "s#DATACHECKER_JOB_JDBC_USERNAME#$DATACHECKER_JOB_JDBC_USERNAME#g" $DB_DML_PATH
    sed -i "s#DATACHECKER_JOB_JDBC_PASSWORD#$DATACHECKER_JOB_JDBC_PASSWORD#g" $DB_DML_PATH

    sed -i "s#DATACHECKER_BDP_JDBC_URL#$DATACHECKER_BDP_JDBC_URL#g" $DB_DML_PATH
    sed -i "s#DATACHECKER_BDP_JDBC_USERNAME#$DATACHECKER_BDP_JDBC_USERNAME#g" $DB_DML_PATH
    sed -i "s#DATACHECKER_BDP_JDBC_PASSWORD#$DATACHECKER_BDP_JDBC_PASSWORD#g" $DB_DML_PATH

    sed -i "s#BDP_MASK_IP#127.0.0.1#g" $DB_DML_PATH
    sed -i "s#BDP_MASK_PORT#8087#g" $DB_DML_PATH

    sed -i "s#EMAIL_HOST#${EMAIL_HOST}#g" $DB_DML_PATH
    sed -i "s#EMAIL_PORT#${EMAIL_PORT}#g" $DB_DML_PATH
    sed -i "s#EMAIL_USERNAME#${EMAIL_USERNAME}#g" $DB_DML_PATH
    sed -i "s#EMAIL_PASSWORD#${EMAIL_PASSWORD}#g" $DB_DML_PATH
    sed -i "s#EMAIL_PROTOCOL#${EMAIL_PROTOCOL}#g" $DB_DML_PATH
}

##choose execute mysql mode
function executeSQL() {
  TEMP_DB_DML_PATH=${SOURCE_ROOT}/dss-appconns/${APPCONN_NAME}/db
  DB_DML_PATH=$TEMP_DB_DML_PATH/init_real.sql
  cp -rf $TEMP_DB_DML_PATH/init.sql $DB_DML_PATH
  sed -i "s/APPCONN_INSTALL_IP/$APPCONN_INSTALL_IP/g"       $DB_DML_PATH
  sed -i "s/APPCONN_INSTALL_PORT/$APPCONN_INSTALL_PORT/g"   $DB_DML_PATH
  sed -i "s#DSS_INSTALL_HOME_VAL#$DSS_INSTALL_HOME#g" $DB_DML_PATH
  if [ ! -z $1 ];then
    replaceDefaultAppconnSql
  fi
  mysql -h$MYSQL_HOST -P$MYSQL_PORT -u$MYSQL_USER -p$MYSQL_PASSWORD -D$MYSQL_DB --default-character-set=utf8 -e "source $DB_DML_PATH"
  isSuccess "initialize the database settings $DB_DML_PATH"
  echo "initialize the $TEMP_DB_DML_PATH/init.sql for $APPCONN_NAME succeed."
}

echo ""
echo "Step1: get the AppConn basic settings."
initInstallAppConn $1
echo ""

echo "Step2: initialize the database settings."
executeSQL $1
echo ""

echo "Step3: load the plugin of $APPCONN_NAME AppConn in DSS."
sh $SOURCE_ROOT/bin/appconn-refresh.sh $APPCONN_NAME