#!/bin/sh
#
# Copyright 2019 WeBank
#
# Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

#Actively load user env
source ~/.bash_profile

export local_host="`hostname --fqdn`"

#ipaddr=$(ip addr | awk '/^[0-9]+: / {}; /inet.*global/ {print gensub(/(.*)\/(.*)/, "\\1", "g", $2)}')

function isLocal(){
    if [ "$1" == "127.0.0.1" ];then
        return 0
    elif [ "$1" == "" ]; then
        return 0
    elif [ "$1" == "localhost" ]; then
        return 0
    elif [ "$1" == $local_host ]; then
        return 0
    #elif [ "$1" == $ipaddr ]; then
     #   return 0
    fi
        return 1
}

function executeCMD(){
   isLocal $1
   flag=$?
   if [ $flag == "0" ];then
      echo "Is local execution:$2"
      eval $2
   else
      echo "Is remote execution:$2"
      ssh -p $SSH_PORT $1 $2
   fi

}
function copyFile(){
   isLocal $1
   flag=$?
   src=$2
   dest=$3
   if [ $flag == "0" ];then
      echo "Is local cp "
      cp -r "$src" "$dest"
   else
      echo "Is remote cp "
      scp -r -P $SSH_PORT  "$src" $1:"$dest"
   fi

}

function isSuccess(){
if [ $? -ne 0 ]; then
    echo "Failed to " + $1
    exit 1
else
    echo "Succeed to" + $1
fi
}


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
 stop
 sleep 2
 start
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

function setServerName(){
	if [[ $PROJECT_NAME == *"dss-server"* ]]; then
		SERVER_NAME=dss-server
	elif [[ $PROJECT_NAME == *"apps"* ]]; then
		SERVER_NAME=dss-apps-server
  else
		echo "please input： sh dss-daemon.sh [start,restart,stop] [server name]; for example : sh dss-daemon.sh restart dss-server "
		echo "server name :  dss-server、apps-server"
		exit 1
	fi
}

