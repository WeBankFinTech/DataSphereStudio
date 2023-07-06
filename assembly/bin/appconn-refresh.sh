#!/bin/sh

if [ -z $SOURCE_ROOT ]; then
  #Actively load user env
  source ~/.bashrc
  shellDir=`dirname $0`
  workDir=`cd ${shellDir}/..;pwd`
  SOURCE_ROOT=${workDir}
  #load config
  source ${SOURCE_ROOT}/conf/config.sh
  source ${SOURCE_ROOT}/conf/db.sh
fi

function isSuccess(){
  if [ $? -ne 0 ]; then
      echo "Failed to " + $1
      exit 1
  else
      echo "Succeed to " + $1
  fi
}

function refresh() {
  while true; do
    response=$(curl -H "Token-Code:BML-AUTH" -H "Token-User:hadoop" -X GET http://${GATEWAY_INSTALL_IP}:${GATEWAY_PORT}/api/rest_j/v1/dss/framework/project/appconn/${APPCONN_NAME}/load)
    if [[ $response == *"succeed"* ]]; then
      break
    else
      sleep 1
    fi
  done
}

if [ -z $1 ];then
  if [ -z $APPCONN_NAME ]; then
    APPCONN_NAME=''
    echo "Please input the name of refreshing AppConn, e.g: schedulis."
    read -p "Please input the AppConn name: "  APPCONN_NAME
  fi

  echo ""
  echo "Try to refresh the plugin of $APPCONN_NAME AppConn in all DSS micro-services."
  echo "The following 2 ways can take effect:"
  echo "1. restart DSS, we will use ${SOURCE_ROOT}/sbin/dss-start-all.sh to restart it!"
  echo "2. do nothing, just wait for 5 minutes. Since the DSS micro-services will refresh all the AppConn plugins every 10 minutes."
  echo ""
  read -p "Please input the choise: "  choise
  if [[ '1' = "$choise" ]]; then
    echo "You chose to restart DSS, now try to restart DSS..."
    sh $SOURCE_ROOT/sbin/dss-start-all.sh
  else
    echo "You chose to wait for 5 minutes."
    echo "Now try to call dss-server to reload the plugin of $APPCONN_NAME AppConn. Please wait!"
    refresh
    isSuccess "reload the plugin of $APPCONN_NAME AppConn in dss-server."
    echo "Now please wait for 5 minutes, then all of the DSS micro-services will refresh the ${APPCONN_NAME} AppConn plugin."
    echo ""
    exit 0
  fi
else
  APPCONN_NAME=$1
  echo "Now try to call dss-server to reload the plugin of $APPCONN_NAME AppConn. Please wait!"
  refresh
  isSuccess "reload the plugin of $APPCONN_NAME AppConn in dss-server."
  echo "Now please wait for 5 minutes, then all of the DSS micro-services will refresh the ${APPCONN_NAME} AppConn plugin."
  echo ""
  exit 0
fi

