#!/bin/bash
#
# description:  Starts and stops Server
#
# @name:        dss-demo
# @author:      peacewong
# @created:     01.16.2021
#
# Modified for dss 1.0.0

cd `dirname $0`
cd ..
INSTALL_HOME=`pwd`

source $INSTALL_HOME/conf/config.sh
local_host="`hostname --fqdn`"


function print_usage(){
  echo "Usage: dss-daemon [start | stop | restart] [serverName]"
  echo " serverName            The service name of the operation"
  echo "Most commands print help when invoked w/o parameters."
}

function isSuccess(){
  if [ $? -ne 0 ]; then
      echo "Failed to " + $1
      exit 1
  else
      echo "Succeed to" + $1
  fi
}

#if there is no LINKIS_INSTALL_HOME，we need to source config again
if [ -z ${DSS_INSTALL_HOME} ]; then
    echo "Warning: DSS_INSTALL_HOME does not exist, we will source config"
    if [ ! -f "${CONF_FILE}" ]; then
        echo "Error: can not find config file, start applications failed"
        exit 1
    else
        source ${CONF_FILE}
    fi
fi

typeset -l PROJECT_NAME
PROJECT_NAME=$2
function startDssProject(){
  if [[ $PROJECT_NAME == *"project"* ]]; then
		SERVER_NAME=dss-framework-project-server
		SERVER_IP=$DSS_FRAMEWORK_PROJECT_SERVER_INSTALL_IP
		startApp
	elif [[ $PROJECT_NAME == *"orchestrator"* ]]; then
		SERVER_NAME=dss-framework-orchestrator-server
		SERVER_IP=$DSS_FRAMEWORK_ORCHESTRATOR_SERVER_INSTALL_IP
		startApp
  elif [[ $PROJECT_NAME == *"apiservice"* ]]; then
		SERVER_NAME=dss-apiservice-server
		SERVER_IP=$DSS_APISERVICE_SERVER_INSTALL_IP
		startApp
	elif [[ $PROJECT_NAME == *"workflow"* ]]; then
		SERVER_NAME=dss-workflow-server
		SERVER_IP=$DSS_WORKFLOW_SERVER_INSTALL_IP
		startApp
	elif [[ $PROJECT_NAME == *"execution"* ]]; then
		SERVER_NAME=dss-flow-execution-server
		SERVER_IP=$DSS_FLOW_EXECUTION_SERVER_INSTALL_IP
		startApp
	##elif [[ $PROJECT_NAME == *"visualis"* ]]; then
	##	SERVER_NAME=visualis-server
	##	SERVER_IP=$VISUALIS_SERVER_INSTALL_IP
	##	startApp
    else
		echo "please input： sh dss-daemon.sh [start,restart,stop] [server name]; for example : sh dss-daemon.sh workspece "
		echo "server name :  project、orchestrator、apiservice、workflow、execution"
		exit 1
	fi
}

function startApp(){
if [[ $SERVER_IP == "127.0.0.1" ]]; then
  SERVER_IP=$local_host
fi
echo "<-------------------------------->"
echo "Begin to $COMMAND $SERVER_NAME"
SERVER_BIN=$DSS_INSTALL_HOME/sbin
SERVER_START_CMD="source ~/.bash_profile;cd ${SERVER_BIN}; dos2unix ./* > /dev/null 2>&1; dos2unix ../conf/* > /dev/null 2>&1;sh $SERVER_BIN/daemon.sh $COMMAND $SERVER_NAME > /dev/null 2>&1 &"
ssh  $SERVER_IP $SERVER_START_CMD
isSuccess "End to $COMMAND $SERVER_NAME"
echo "<-------------------------------->"
sleep 1
}

COMMAND=$1
if [ $COMMAND != "start" ] && [ $COMMAND != "stop" ] && [ $COMMAND != "restart" ] ; then
  print_usage
  exit 1
fi

startDssProject

