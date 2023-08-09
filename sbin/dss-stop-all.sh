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

# stop all dss applications
info="We will stop all dss applications, it will take some time, please wait"
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
local_host="`hostname --fqdn`"
source $DSS_HOME/sbin/common.sh

function stopApp(){
echo "<-------------------------------->"
echo "Begin to stop $SERVER_NAME"
SERVER_STOP_CMD="sh $DSS_HOME/sbin/dss-daemon.sh stop $SERVER_NAME"
if test -z "$SERVER_IP"
then
  SERVER_IP=$local_host
fi
executeCMD $SERVER_IP "$SERVER_STOP_CMD"
echo "<-------------------------------->"
}

function stopDssProject(){
 	SERVER_NAME=dss-server
	SERVER_IP=$DSS_SERVER_INSTALL_IP
	stopApp

  SERVER_NAME=dss-apps-server
	SERVER_IP=$DSS_APPS_SERVER_INSTALL_IP
  stopApp
}

stopDssProject