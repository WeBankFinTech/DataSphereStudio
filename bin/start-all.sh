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
source ~/.bash_profile

workDir=`dirname "${BASH_SOURCE-$0}"`
workDir=`cd "$workDir"; pwd`


CONF_DIR="${workDir}"/../conf
CONF_FILE=${CONF_DIR}/config.sh

function isSuccess(){
if [ $? -ne 0 ]; then
    echo "ERROR:  " + $1
    exit 1
else
    echo "INFO:" + $1
fi
}

sudo yum -y install dos2unix


local_host="`hostname --fqdn`"

#if there is no LINKIS_INSTALL_HOME，we need to source config again
if [ -z ${DSS_INSTALL_HOME} ];then
    echo "Warning: DSS_INSTALL_HOME does not exist, we will source config"
    if [ ! -f "${CONF_FILE}" ];then
        echo "Error: can not find config file, start applications failed"
        exit 1
    else
        source ${CONF_FILE}
    fi
fi

function startApp(){
echo "<-------------------------------->"
echo "Begin to start $SERVER_NAME"
SERVER_BIN=${DSS_INSTALL_HOME}/${SERVER_NAME}/bin
SERVER_START_CMD="source ~/.bash_profile;cd ${SERVER_BIN}; dos2unix ./* > /dev/null 2>&1; dos2unix ../conf/* > /dev/null 2>&1;sh start-${SERVER_NAME}.sh > /dev/null 2>&1 &"

if [ -n "${SERVER_IP}"  ];then
    ssh ${SERVER_IP} "${SERVER_START_CMD}"
else
    ssh ${local_host} "${SERVER_START_CMD}"
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

SERVER_NAME=visualis-server
SERVER_IP=$VISUALIS_SERVER_INSTALL_IP
startApp

