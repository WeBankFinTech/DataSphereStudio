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


cd `dirname $0`
cd ..
INSTALL_HOME=`pwd`

# set DSS_HOME
if [ "$DSS_HOME" = "" ]; then
  export DSS_HOME=$INSTALL_HOME
fi

# set DSS_CONF_DIR
if [ "$DSS_CONF_DIR" = "" ]; then
  export DSS_CONF_DIR=$DSS_HOME/conf
fi

source "$DSS_CONF_DIR"/config.sh

function isSuccess(){
  if [ $? -ne 0 ]; then
      echo "ERROR:  " + $1
      exit 1
  else
      echo "INFO:" + $1
  fi
}


#if there is no DSS_INSTALL_HOME，we need to source config again
if [ -z ${DSS_INSTALL_HOME} ]; then
    echo "Warning: DSS_INSTALL_HOME does not exist, we will source config"
    if [ ! -f "${CONF_FILE}" ]; then
        echo "Error: can not find config file, start applications failed"
        exit 1
    else
        source ${CONF_FILE}
    fi
fi


local_host="`hostname --fqdn`"
function startApp(){
echo "<-------------------------------->"
echo "Begin to start $SERVER_NAME"
if test -z "$SERVER_IP"
then
  SERVER_IP=$local_host
fi

#echo "Is local "$flag
#if [ $flag == "0" ];then
#   eval $SERVER_START_CMD
#else
#   SERVER_BIN=$INSTALL_HOME/sbin
#   SERVER_START_CMD="source ~/.bash_profile;cd ${SERVER_BIN}; dos2unix ./* > /dev/null 2>&1; dos2unix ../conf/* > /dev/null 2>&1;sh $INSTALL_HOME/sbin/daemon.sh $COMMAND $SERVER_NAME > /dev/null 2>&1 &"
#   ssh  $SERVER_IP $SERVER_START_CMD
#fi

if [[ $SERVER_IP == "127.0.0.1" ]];then
    SERVER_IP=$local_host
fi
SERVER_BIN=$DSS_INSTALL_HOME/sbin
SERVER_START_CMD="source ~/.bash_profile;cd ${SERVER_BIN}; dos2unix ./* > /dev/null 2>&1; dos2unix ../conf/$SERVER_NAME/* > /dev/null 2>&1;sh daemon.sh start $SERVER_NAME > /dev/null 2>&1 &"
ssh  $SERVER_IP $SERVER_START_CMD
isSuccess "End to start $SERVER_NAME"
echo "<-------------------------------->"
sleep 1
}


function startDssProject(){
  loadConfig  

	SERVER_NAME=dss-framework-project-server
	SERVER_IP=$DSS_FRAMEWORK_PROJECT_SERVER_INSTALL_IP
	startApp		
	
	SERVER_NAME=dss-framework-orchestrator-server
	SERVER_IP=$DSS_FRAMEWORK_ORCHESTRATOR_SERVER_INSTALL_IP
	startApp

	SERVER_NAME=dss-apiservice-server
	SERVER_IP=$DSS_APISERVICE_SERVER_INSTALL_IP
	startApp
	
	SERVER_NAME=dss-workflow-server
	SERVER_IP=$DSS_WORKFLOW_SERVER_INSTALL_IP
	startApp
	
	SERVER_NAME=dss-flow-execution-server
	SERVER_IP=$DSS_FLOW_EXECUTION_SERVER_INSTALL_IP
	startApp

	##SERVER_NAME=visualis-server
  ##SERVER_IP=$VISUALIS_SERVER_INSTALL_IP
  ##startApp
}

startDssProject