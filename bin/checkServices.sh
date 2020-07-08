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
#!/bin/sh
source ~/.bash_profile

shellDir=`dirname $0`
workDir=`cd ${shellDir}/..;pwd`

##load config
export LINKIS_DSS_CONF_FILE=${LINKIS_DSS_CONF_FILE:-"${workDir}/conf/config.sh"}
export DISTRIBUTION=${DISTRIBUTION:-"${workDir}/conf/config.sh"}
source ${LINKIS_DSS_CONF_FILE}
source ${DISTRIBUTION}

MICRO_SERVICE_NAME=$1
MICRO_SERVICE_IP=$2
MICRO_SERVICE_PORT=$3

local_host="`hostname --fqdn`"

ipaddr=$(ip addr | awk '/^[0-9]+: / {}; /inet.*global/ {print gensub(/(.*)\/(.*)/, "\\1", "g", $2)}'|awk 'NR==1')

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

#echo "<--------------------------------------------------------------------------->"
#echo "Start to Check if your microservice:$MICRO_SERVICE_NAME is normal via telnet"
#echo ""
#if ! executeCMD $SERVER_IP "test -e $DSS_INSTALL_HOME/$MICRO_SERVICE_NAME"; then
#  echo "$MICRO_SERVICE_NAME is not installed,the check steps will be skipped"
#  exit 0
#fi
echo "==========================================================="
echo $MICRO_SERVICE_NAME
echo $MICRO_SERVICE_IP
echo $MICRO_SERVICE_PORT
echo "==========================================================="

if [ $MICRO_SERVICE_NAME == "visualis-server" ] && [ $MICRO_SERVICE_IP == "127.0.0.1" ]; then
    MICRO_SERVICE_IP=$ipaddr
fi

result=`echo -e "\n" | telnet $MICRO_SERVICE_IP $MICRO_SERVICE_PORT 2>/dev/null | grep Connected | wc -l`
if [ $result -eq 1 ]; then
      echo "$MICRO_SERVICE_NAME is ok."
else
      echo "<--------------------------------------------------------------------------->"
      echo "ERROR your $MICRO_SERVICE_NAME microservice is not start successful !!! ERROR logs as follows :"
      echo "PLEAESE CHECK  DETAIL LOG,LOCATION:$DSS_INSTALL_HOME/$MICRO_SERVICE_NAME/logs/linkis.out"
      echo '<------------------------------------------------------------->'
      executeCMD $MICRO_SERVICE_IP "tail -n 50 $DSS_INSTALL_HOME/$MICRO_SERVICE_NAME/logs/*.out"
      echo '<-------------------------------------------------------------->'
      echo "PLEAESE CHECK DETAIL LOG,LOCATION:$DSS_INSTALL_HOME/$MICRO_SERVICE_NAME/logs/linkis.out"
      exit 1
fi

