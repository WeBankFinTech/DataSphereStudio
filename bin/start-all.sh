#!/usr/bin/env bash
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

# Start all dss applications
info="We will start all dss applications, it will take some time, please wait"
echo ${info}

#Actively load user env
source /etc/profile
source ~/.bash_profile

shellDir=`dirname $0`

workDir=`cd ${shellDir}/..;pwd`

CONF_DIR="${workDir}"/conf

export LINKIS_DSS_CONF_FILE=${LINKIS_DSS_CONF_FILE:-"${CONF_DIR}/config.sh"}
export DISTRIBUTION=${DISTRIBUTION:-"${CONF_DIR}/config.sh"}
source $LINKIS_DSS_CONF_FILE
source ${DISTRIBUTION}
function isSuccess(){
if [ $? -ne 0 ]; then
    echo "ERROR:  " + $1
    exit 1
else
    echo "INFO:" + $1
fi
}
local_host="`hostname --fqdn`"

ipaddr="`hostname -i`"

function isLocal(){
    if [ "$1" == "127.0.0.1" ];then
        return 0
    elif [ $1 == "localhost" ]; then
        return 0
    elif [ $1 == $local_host ]; then
        return 0
    elif [ $1 == $ipaddr ]; then
        return 0
    fi
        return 1
}

function executeCMD(){
   isLocal $1
   flag=$?
   echo "Is local "$flag
   if [ $flag == "0" ];then
      eval $2
   else
      ssh -p $SSH_PORT $1 $2
   fi

}

#if there is no LINKIS_INSTALL_HOME，we need to source config again
if [ -z ${DSS_INSTALL_HOME} ];then
    echo "Warning: DSS_INSTALL_HOME does not exist, we will source config"
    if [ ! -f "${LINKIS_DSS_CONF_FILE}" ];then
        echo "Error: can not find config file, start applications failed"
        exit 1
    else
        source ${LINKIS_DSS_CONF_FILE}
    fi
fi

function startApp(){
echo "<-------------------------------->"
echo "Begin to start $SERVER_NAME"
SERVER_BIN=${DSS_INSTALL_HOME}/${SERVER_NAME}/bin
#echo $SERVER_BIN
SERVER_LOCAL_START_CMD="dos2unix ${SERVER_BIN}/* > /dev/null 2>&1; dos2unix ${SERVER_BIN}/../conf/* > /dev/null 2>&1;sh ${SERVER_BIN}/start-${SERVER_NAME}.sh > /dev/null 2>&1 &"
SERVER_REMOTE_START_CMD="source /etc/profile;source ~/.bash_profile;cd ${SERVER_BIN}; dos2unix ./* > /dev/null 2>&1; dos2unix ../conf/* > /dev/null 2>&1; sh start-${SERVER_NAME}.sh > /dev/null 2>&1"

if test -z "$SERVER_IP"
then
  SERVER_IP=$local_host
fi

if ! executeCMD $SERVER_IP "test -e $SERVER_BIN"; then
  echo "<-------------------------------->"
  echo "$SERVER_NAME is not installed,the start steps will be skipped"
  echo "<-------------------------------->"
  return
fi

isLocal $SERVER_IP
flag=$?
echo "Is local "$flag
if [ $flag == "0" ];then
   eval $SERVER_LOCAL_START_CMD
else
   ssh -p $SSH_PORT $SERVER_IP $SERVER_REMOTE_START_CMD
fi
isSuccess "End to start $SERVER_NAME"
echo "<-------------------------------->"
sleep 15 #for Eureka register
}

#dss-server
SERVER_NAME=dss-server
SERVER_IP=$DSS_SERVER_INSTALL_IP
startApp

#dss-flow-execution-entrance
SERVER_NAME=dss-flow-execution-entrance
SERVER_IP=$FLOW_EXECUTION_INSTALL_IP
startApp

#dss-flow-execution-entrance
SERVER_NAME=linkis-appjoint-entrance
SERVER_IP=$APPJOINT_ENTRANCE_INSTALL_IP
startApp

#visualis-server
SERVER_NAME=visualis-server
SERVER_IP=$VISUALIS_SERVER_INSTALL_IP
startApp

echo ""
echo "Start to check all dss microservice"
echo ""

function checkServer(){
echo "<-------------------------------->"
echo "Begin to check $SERVER_NAME"
if test -z "$SERVER_IP"
then
  SERVER_IP=$local_host
fi

SERVER_BIN=${SERVER_HOME}/${SERVER_NAME}/bin

if ! executeCMD $SERVER_IP "test -e ${DSS_INSTALL_HOME}/${SERVER_NAME}"; then
  echo "$SERVER_NAME is not installed,the checkServer steps will be skipped"
  return
fi

sh $workDir/bin/checkServices.sh $SERVER_NAME $SERVER_IP $SERVER_PORT
isSuccess "start $SERVER_NAME "
sleep 3
echo "<-------------------------------->"
}

#check dss-server
SERVER_NAME=dss-server
SERVER_IP=$DSS_SERVER_INSTALL_IP
SERVER_PORT=$DSS_SERVER_PORT
checkServer


#check dss-flow-execution-entrance
SERVER_NAME=dss-flow-execution-entrance
SERVER_IP=$FLOW_EXECUTION_INSTALL_IP
SERVER_PORT=$FLOW_EXECUTION_PORT
checkServer

#check linkis-appjoint-entrance
SERVER_NAME=linkis-appjoint-entrance
SERVER_IP=$APPJOINT_ENTRANCE_INSTALL_IP
SERVER_PORT=$APPJOINT_ENTRANCE_PORT
checkServer


#check visualis-server
sleep 10 #visualis service need more time to register
SERVER_NAME=visualis-server
SERVER_IP=$VISUALIS_SERVER_INSTALL_IP
SERVER_PORT=$VISUALIS_SERVER_PORT
checkServer

echo "DSS started successfully"
