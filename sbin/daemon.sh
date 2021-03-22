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

function print_usage(){
  echo "Usage: dss-daemon [start | stop | restart | status] [serverName]"
  echo " serverName            The service name of the operation"
  echo "Most commands print help when invoked w/o parameters."
}

if [ $# != 2 ]; then
  print_usage
  exit 2
fi

# set DSS_HOME
if [ "$DSS_HOME" = "" ]; then
  export DSS_HOME=$INSTALL_HOME
fi

# set DSS_CONF_DIR
if [ "$DSS_CONF_DIR" = "" ]; then
  export DSS_CONF_DIR=$DSS_HOME/conf
fi

source "$DSS_CONF_DIR"/config.sh

# get pid directory
if [ "$DSS_PID_DIR" = "" ]; then
  export DSS_PID_DIR="$DSS_HOME/pid"
fi
if [ ! -w "$DSS_PID_DIR" ] ; then
  mkdir -p "$DSS_PID_DIR"
fi


function start()
{
  echo "Start to check whether the $SERVER_NAME is running"
  if [[ -f "${SERVER_PID}" ]]; then
      pid=$(cat ${SERVER_PID})
      if kill -0 ${pid} >/dev/null 2>&1; then
        echo "$SERVER_NAME is already running."
        exit 1
      fi
  fi
  export SERVER_START_BIN=$DSS_HOME/sbin/ext/$SERVER_NAME
  if [[ $SERVER_NAME == "visualis-server" ]];then
    SERVER_START_BIN=$DSS_HOME/$SERVER_NAME/bin/start-${SERVER_NAME}.sh
  fi
  if [[ ! -f "${SERVER_START_BIN}" ]]; then
      echo "The $SERVER_NAME is wrong or the corresponding startup script does not exist: "
      echo "$SERVER_START_BIN"
      exit 1
  else
      echo "Start to start server, startup script:  $SERVER_START_BIN"
      sh  $SERVER_START_BIN
  fi
}

function wait_for_server_to_die() {
  local pid
  local count
  pid=$1
  timeout=$2
  count=0
  timeoutTime=$(date "+%s")
  let "timeoutTime+=$timeout"
  currentTime=$(date "+%s")
  forceKill=1

  while [[ $currentTime -lt $timeoutTime ]]; do
    $(kill ${pid} > /dev/null 2> /dev/null)
    if kill -0 ${pid} > /dev/null 2>&1; then
      sleep 3
    else
      forceKill=0
      break
    fi
    currentTime=$(date "+%s")
  done

  if [[ forceKill -ne 0 ]]; then
    $(kill -9 ${pid} > /dev/null 2> /dev/null)
  fi
}


function stop()
{
  if [[ ! -f "${SERVER_PID}" ]]; then
      echo "server $SERVER_NAME is not running"
  else
      pid=$(cat ${SERVER_PID})
      if [[ -z "${pid}" ]]; then
        echo "server $SERVER_NAME is not running"
      else
        wait_for_server_to_die $pid 40
        $(rm -f ${SERVER_PID})
        echo "server $SERVER_NAME is stopped."
      fi
  fi
}

function restart()
{
  if [[ $SERVER_NAME == "visualis-server" ]];then
    sh $DSS_HOME/$SERVER_NAME/bin/stop-${SERVER_NAME}.sh
    sleep 2
    sh $DSS_HOME/$SERVER_NAME/bin/start-${SERVER_NAME}.sh
  else
    stop
    sleep 2
    start
  fi
}

status()
{
  if [[ ! -f "${SERVER_PID}" ]]; then
      echo "server $SERVER_NAME is stopped"
  else
      pid=$(cat ${SERVER_PID})
      if [[ -z "${pid}" ]]; then
        echo "server $SERVER_NAME is not running"
      else
        echo "server $SERVER_NAME is running."
      fi
  fi
}


typeset -l PROJECT_NAME
PROJECT_NAME=$2
function setServerName(){
  if [[ $PROJECT_NAME == *"project"* ]]; then
		SERVER_NAME=dss-framework-project-server
	elif [[ $PROJECT_NAME == *"orchestrator"* ]]; then
		SERVER_NAME=dss-framework-orchestrator-server
  elif [[ $PROJECT_NAME == *"apiservice"* ]]; then
		SERVER_NAME=dss-apiservice-server
	elif [[ $PROJECT_NAME == *"workflow"* ]]; then
		SERVER_NAME=dss-workflow-server
	elif [[ $PROJECT_NAME == *"execution"* ]]; then
		SERVER_NAME=dss-flow-execution-server
	##elif [[ $PROJECT_NAME == *"visualis"* ]]; then
	##	SERVER_NAME=visualis-server
    else
		echo "please input： sh daemon.sh [start,restart,stop] [server name]; for example : sh dss-daemon.sh workspece "
		echo "server name :  workspace、project、orchestrator、release、apiservice、workflow、execution"
		exit 1
	fi
}

## get project full name
setServerName

COMMAND=$1
export SERVER_PID=$DSS_PID_DIR/$SERVER_NAME.pid
if [[ $SERVER_NAME == "visualis-server" ]];then
  export SERVER_PID=$DSS_HOME/$SERVER_NAME/bin/linkis.pid
fi

case $COMMAND in
  start|stop|restart|status)
    $COMMAND $SERVER_NAME
    ;;
  *)
    print_usage
    exit 2
    ;;
esac