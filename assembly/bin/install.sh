#!/bin/sh
#Actively load user env
if [ -f "~/.bashrc" ];then
  echo "Warning! user bashrc file does not exist."
else
  source ~/.bashrc
fi

shellDir=`dirname $0`
workDir=`cd ${shellDir}/..;pwd`

SERVER_IP=""
SERVER_HOME=""

local_host="`hostname --fqdn`"
LOCAL_IP=$(hostname -I | awk '{print $1}')

#To be compatible with MacOS and Linux
txt=""
if [[ "$OSTYPE" == "darwin"* ]]; then
    txt="''"
elif [[ "$OSTYPE" == "linux-gnu" ]]; then
    # linux
    txt=""
elif [[ "$OSTYPE" == "cygwin" ]]; then
    echo "dss not support Windows operating system"
    exit 1
elif [[ "$OSTYPE" == "msys" ]]; then
    echo "dss not support Windows operating system"
    exit 1
elif [[ "$OSTYPE" == "win32" ]]; then
    echo "dss not support Windows operating system"
    exit 1
elif [[ "$OSTYPE" == "freebsd"* ]]; then
    txt=""
else
    echo "Operating system unknown, please tell us(submit issue) for better service"
    exit 1
fi

function isSuccess(){
  if [ $? -ne 0 ]; then
      echo "Failed to " + $1
      exit 1
  else
      echo "Succeed to " + $1
  fi
}

function checkJava(){
  java -version
  isSuccess "execute java --version"
}

checkJava

dos2unix -q ${workDir}/config/*
isSuccess "execute dos2unix -q ${workDir}/config/*"
dos2unix -q ${workDir}/bin/*

echo "step1:load config"
source ${workDir}/config/config.sh
source ${workDir}/config/db.sh

DSS_FILE_PATH="$workDir/$DSS_FILE_NAME"
#dos2unix ${DSS_FILE_PATH}/sbin/*
#dos2unix ${DSS_FILE_PATH}/sbin/ext/*
if [ -z $DSS_FILE_NAME ]; then
  echo "DSS_FILE_NAME is null "
  exit 1
fi

function replaceCommonIp() {
  if [ -z "$DSS_FRAMEWORK_PROJECT_SERVER_INSTALL_IP" ]; then
    DSS_FRAMEWORK_PROJECT_SERVER_INSTALL_IP=$LOCAL_IP
  fi
  if [ -z "$DSS_FRAMEWORK_PROJECT_SERVER_PORT" ]; then
    DSS_FRAMEWORK_PROJECT_SERVER_PORT=9002
  fi

  if [ -z "$DSS_FRAMEWORK_ORCHESTRATOR_SERVER_INSTALL_IP" ]; then
    DSS_FRAMEWORK_ORCHESTRATOR_SERVER_INSTALL_IP=$LOCAL_IP
  fi
  if [ -z "$DSS_FRAMEWORK_ORCHESTRATOR_SERVER_PORT" ]; then
    DSS_FRAMEWORK_ORCHESTRATOR_SERVER_PORT=9003
  fi

  if [ -z "$DSS_APISERVICE_SERVER_INSTALL_IP" ]; then
    DSS_APISERVICE_SERVER_INSTALL_IP=$LOCAL_IP
  fi
  if [ -z "$DSS_APISERVICE_SERVER_PORT" ]; then
    DSS_APISERVICE_SERVER_PORT=9004
  fi

  if [ -z "$DSS_WORKFLOW_SERVER_INSTALL_IP" ]; then
    DSS_WORKFLOW_SERVER_INSTALL_IP=$LOCAL_IP
  fi
  if [ -z "$DSS_WORKFLOW_SERVER_PORT" ]; then
    DSS_WORKFLOW_SERVER_PORT=9005
  fi

  if [ -z "$DSS_FLOW_EXECUTION_SERVER_INSTALL_IP" ]; then
    DSS_FLOW_EXECUTION_SERVER_INSTALL_IP=$LOCAL_IP
  fi
  if [ -z "$DSS_FLOW_EXECUTION_SERVER_PORT" ]; then
    DSS_FLOW_EXECUTION_SERVER_PORT=9006
  fi

  if [ -z "$DSS_SCRIPTIS_SERVER_INSTALL_IP" ]; then
    DSS_SCRIPTIS_SERVER_INSTALL_IP=$LOCAL_IP
  fi
  if [ -z "$DSS_SCRIPTIS_SERVER_PORT" ]; then
    DSS_SCRIPTIS_SERVER_PORT=9008
  fi

  if [ -z "$DSS_DATA_API_SERVER_INSTALL_IP" ]; then
    DSS_DATA_API_SERVER_INSTALL_IP=$LOCAL_IP
  fi
  if [ -z "$DSS_DATA_API_SERVER_PORT" ]; then
    DSS_DATA_API_SERVER_PORT=9208
  fi

  if [ -z "$DSS_DATA_GOVERNANCE_SERVER_INSTALL_IP" ]; then
    DSS_DATA_GOVERNANCE_SERVER_INSTALL_IP=$LOCAL_IP
  fi
  if [ -z "$DSS_DATA_GOVERNANCE_SERVER_PORT" ]; then
    DSS_DATA_GOVERNANCE_SERVER_PORT=9209
  fi

  if [ -z "$DSS_GUIDE_SERVER_INSTALL_IP" ]; then
    DSS_GUIDE_SERVER_INSTALL_IP=$LOCAL_IP
  fi
  if [ -z "$DSS_GUIDE_SERVER_PORT" ]; then
    DSS_GUIDE_SERVER_PORT=9210
  fi

 if [[ $GATEWAY_INSTALL_IP == "127.0.0.1" ]] || [ -z "$GATEWAY_INSTALL_IP" ]; then
   #echo "GATEWAY_INSTALL_IP is equals $GATEWAY_INSTALL_IP ,we will change it to ip address"
   GATEWAY_INSTALL_IP=$LOCAL_IP
 fi
 if [[ $EUREKA_INSTALL_IP == "127.0.0.1" ]] || [ -z "$EUREKA_INSTALL_IP" ]; then
    #echo "EUREKA_INSTALL_IP is equals $EUREKA_INSTALL_IP ,we will change it to ip address"
    EUREKA_INSTALL_IP=$LOCAL_IP
 fi
}
##替换真实的IP
replaceCommonIp

EUREKA_URL=http://$EUREKA_INSTALL_IP:$EUREKA_PORT/eureka/

## excecute sql
source ${workDir}/bin/executeSQL.sh

function changeCommonConf(){
  sed -i "s#defaultZone:.*#defaultZone: $EUREKA_URL#g" $CONF_APPLICATION_YML
  sed -i "s#hostname:.*#hostname: $SERVER_IP#g" $CONF_APPLICATION_YML
  sed -i "s#wds.linkis.server.mybatis.datasource.url.*#wds.linkis.server.mybatis.datasource.url=jdbc:mysql://${MYSQL_HOST}:${MYSQL_PORT}/${MYSQL_DB}?characterEncoding=UTF-8#g" $CONF_DSS_PROPERTIES
  sed -i "s#wds.linkis.server.mybatis.datasource.username.*#wds.linkis.server.mybatis.datasource.username=$MYSQL_USER#g" $CONF_DSS_PROPERTIES
  sed -i "s#wds.linkis.server.mybatis.datasource.password.*#wds.linkis.server.mybatis.datasource.password=$MYSQL_PASSWORD#g" $CONF_DSS_PROPERTIES
  sed -i "s#wds.linkis.gateway.ip.*#wds.linkis.gateway.ip=$GATEWAY_INSTALL_IP#g" $CONF_DSS_PROPERTIES
  sed -i "s#wds.linkis.gateway.port.*#wds.linkis.gateway.port=$GATEWAY_PORT#g" $CONF_DSS_PROPERTIES
  sed -i "s#wds.linkis.gateway.url.*#wds.linkis.gateway.url=http://$GATEWAY_INSTALL_IP:$GATEWAY_PORT/#g" $CONF_DSS_PROPERTIES
  sed -i "s#wds.linkis.gateway.wtss.url.*#wds.linkis.gateway.wtss.url=http://$GATEWAY_INSTALL_IP:$GATEWAY_PORT/#g" $CONF_DSS_PROPERTIES
}

##function start
function changeConf(){
  sed -i "s#spring.server.port=.*#spring.server.port=$SERVER_PORT#g" $CONF_SERVER_PROPERTIES
  if [[ $SERVER_NAME == "dss-framework-orchestrator-server" ]] || [[ $SERVER_NAME == "dss-workflow-server" ]]; then
      SERVER_FULL_NAME=$SERVER_NAME
      SERVER_FULL_NAME=$SERVER_NAME-$ENV_FLAG
      sed -i "s#spring.spring.application.name=.*#spring.spring.application.name=$SERVER_FULL_NAME#g" $CONF_SERVER_PROPERTIES
  fi
  sed -i "s#wds.dss.appconn.scheduler.project.store.dir.*#wds.dss.appconn.scheduler.project.store.dir=$WDS_SCHEDULER_PATH#g" $CONF_SERVER_PROPERTIES
  isSuccess "subsitution $CONF_SERVER_PROPERTIES"
}
##function end


UPLOAD_PUBLIC_IPS=""
##function start
function uploadServiceFile(){
  if [[ $SERVER_IP == "127.0.0.1" ]]; then
    SERVER_IP=$local_host
  fi
  #echo "$SERVER_NAME-step3:copy install package"
  # upload project conf
  # cp -rfp $SSH_PORT ${workDir}/config/{$SERVER_NAME}.properties $CONF_PATH
  if [[ $UPLOAD_PUBLIC_IPS == *",${ENV_FLAG}-$SERVER_IP,"* ]]; then
	  return 0
  fi
  cp -rfp ${DSS_FILE_PATH}/* $SERVER_HOME
  cp -rfp ${workDir}/bin $SERVER_HOME
  cp -rfp ${workDir}/config/* $SERVER_HOME/conf
  sudo chown -R $deployUser:$deployUser $SERVER_HOME
  UPLOAD_PUBLIC_IPS="$UPLOAD_PUBLIC_IPS,${ENV_FLAG}-$SERVER_IP,"
  changeCommonConf
#  echo "UPLOAD_PUBLIC_IPS-->$UPLOAD_PUBLIC_IPS"
}

##function start
function installPackage(){
  if [[ $SERVER_IP == "127.0.0.1" ]]; then
    SERVER_IP=$local_host
  fi
  if [ -z $SERVER_NAME ]; then
    echo "ERROR:SERVER_NAME is null "
    exit 1
  fi
  uploadServiceFile
  # change configuration
  changeConf
}

function dssWebInstall(){
if ! test -e ${LINKIS_DSS_HOME}/wedatasphere-dss-web*.zip; then
  echo "**********Error: please put wedatasphere-dss-web-xxx.zip in ${LINKIS_DSS_HOME}! "
  exit 1
else
  echo "Start to unzip dss web package."
  unzip  -d ${LINKIS_DSS_HOME}/web/ -o ${LINKIS_DSS_HOME}/wedatasphere-dss-web-*.zip > /dev/null 2>&1
  sed -i "s#linkis_url.*#linkis_url=${LINKIS_GATEWAY_URL}#g" ${LINKIS_DSS_HOME}/web/config.sh
  isSuccess "Unzip dss web package to ${LINKIS_DSS_HOME}/web"
fi
}

##Install dss projects
function installDssProject() {
  echo "step2:update config"
  SERVER_HOME=$DSS_INSTALL_HOME
  if [ "$SERVER_HOME" == "" ]
  then
    export SERVER_HOME=${workDir}/DSSInstall
  fi
  if [ -d $SERVER_HOME ] && [ "$SERVER_HOME" != "$workDir" ]; then
    echo "mv  $SERVER_HOME  $SERVER_HOME-bak"
    mv  $SERVER_HOME  $SERVER_HOME-bak
  fi
  echo "create dir SERVER_HOME: $SERVER_HOME"
  sudo mkdir -p $SERVER_HOME
  isSuccess "Create the dir of  $SERVER_HOME"
  sudo chown -R $deployUser:$deployUser $SERVER_HOME
  isSuccess "chown -R $deployUser:$deployUser $SERVER_HOME"

  #echo ""
  SERVER_NAME=dss-framework-project-server
  SERVER_IP=$DSS_FRAMEWORK_PROJECT_SERVER_INSTALL_IP
  SERVER_PORT=$DSS_FRAMEWORK_PROJECT_SERVER_PORT

  UPLOAD_LIB_FILES=$DSS_FILE_PATH/lib/dss-framework/$SERVER_NAME
  LIB_PATH=$SERVER_HOME/lib/dss-framework
  LOG_PATH=$SERVER_HOME/logs/dss-framework/$SERVER_NAME
  CONF_SERVER_PROPERTIES=$SERVER_HOME/conf/$SERVER_NAME.properties
  CONF_DSS_PROPERTIES=$SERVER_HOME/conf/dss.properties
  CONF_APPLICATION_YML=$SERVER_HOME/conf/application-dss.yml
  ###install project-Server
  installPackage
  #echo ""

  SERVER_NAME=dss-framework-orchestrator-server
  SERVER_IP=$DSS_FRAMEWORK_ORCHESTRATOR_SERVER_INSTALL_IP
  SERVER_PORT=$DSS_FRAMEWORK_ORCHESTRATOR_SERVER_PORT
  UPLOAD_LIB_FILES=$DSS_FILE_PATH/lib/dss-framework/$SERVER_NAME
  LIB_PATH=$SERVER_HOME/lib/dss-framework
  LOG_PATH=$SERVER_HOME/logs/dss-framework/$SERVER_NAME
  CONF_SERVER_PROPERTIES=$SERVER_HOME/conf/$SERVER_NAME.properties
  CONF_DSS_PROPERTIES=$SERVER_HOME/conf/dss.properties
  CONF_APPLICATION_YML=$SERVER_HOME/conf/application-dss.yml
  ###install orchestrator-Server
  installPackage
  #echo ""

  SERVER_NAME=dss-apiservice-server
  SERVER_IP=$DSS_APISERVICE_SERVER_INSTALL_IP
  SERVER_PORT=$DSS_APISERVICE_SERVER_PORT
  UPLOAD_LIB_FILES=$DSS_FILE_PATH/lib/dss-apps/$SERVER_NAME
  LIB_PATH=$SERVER_HOME/lib/dss-apps
  LOG_PATH=$SERVER_HOME/logs/dss-apps/$SERVER_NAME
  CONF_SERVER_PROPERTIES=$SERVER_HOME/conf/$SERVER_NAME.properties
  CONF_DSS_PROPERTIES=$SERVER_HOME/conf/dss.properties
  CONF_APPLICATION_YML=$SERVER_HOME/conf/application-dss.yml
  ###install dss-apiservice-server
  installPackage
  #echo ""

  SERVER_NAME=dss-scriptis-server
  SERVER_IP=$DSS_SCRIPTIS_SERVER_INSTALL_IP
  SERVER_PORT=$DSS_SCRIPTIS_SERVER_PORT
  UPLOAD_LIB_FILES=$DSS_FILE_PATH/lib/dss-apps/$SERVER_NAME
  LIB_PATH=$SERVER_HOME/lib/dss-apps
  LOG_PATH=$SERVER_HOME/logs/dss-apps/$SERVER_NAME
  CONF_SERVER_PROPERTIES=$SERVER_HOME/conf/$SERVER_NAME.properties
  CONF_DSS_PROPERTIES=$SERVER_HOME/conf/dss.properties
  CONF_APPLICATION_YML=$SERVER_HOME/conf/application-dss.yml
  ###install dss-scriptis-server
  installPackage
  #echo ""

  ##Flow execution Install
  PACKAGE_DIR=dss
  SERVER_NAME=dss-flow-execution-server
  SERVER_IP=$DSS_FLOW_EXECUTION_SERVER_INSTALL_IP
  SERVER_PORT=$DSS_FLOW_EXECUTION_SERVER_PORT
  UPLOAD_LIB_FILES=$DSS_FILE_PATH/lib/dss-orchestrator/$SERVER_NAME
  LIB_PATH=$SERVER_HOME/lib/dss-orchestrator
  LOG_PATH=$SERVER_HOME/logs/dss-orchestrator/$SERVER_NAME
  CONF_SERVER_PROPERTIES=$SERVER_HOME/conf/$SERVER_NAME.properties
  CONF_DSS_PROPERTIES=$SERVER_HOME/conf/dss.properties
  CONF_APPLICATION_YML=$SERVER_HOME/conf/application-dss.yml
  ###Install flow execution
  installPackage
  #echo ""

  SERVER_NAME=dss-workflow-server
  SERVER_IP=$DSS_WORKFLOW_SERVER_INSTALL_IP
  SERVER_PORT=$DSS_WORKFLOW_SERVER_PORT
  UPLOAD_LIB_FILES=$DSS_FILE_PATH/lib/dss-orchestrator/$SERVER_NAME
  LIB_PATH=$SERVER_HOME/lib/dss-orchestrator
  LOG_PATH=$SERVER_HOME/logs/dss-orchestrator/$SERVER_NAME
  CONF_SERVER_PROPERTIES=$SERVER_HOME/conf/$SERVER_NAME.properties
  CONF_DSS_PROPERTIES=$SERVER_HOME/conf/dss.properties
  CONF_APPLICATION_YML=$SERVER_HOME/conf/application-dss.yml
  ###install dss-workflow-server
  installPackage

  ###install dss-data-api-server
  SERVER_NAME=dss-data-api-server
  SERVER_IP=$DSS_DATA_API_SERVER_INSTALL_IP
  SERVER_PORT=$DSS_DATA_API_SERVER_PORT
  UPLOAD_LIB_FILES=$DSS_FILE_PATH/lib/dss-data-api/$SERVER_NAME
  LIB_PATH=$SERVER_HOME/lib/dss-data-api
  LOG_PATH=$SERVER_HOME/logs/dss-data-api/$SERVER_NAME
  CONF_SERVER_PROPERTIES=$SERVER_HOME/conf/$SERVER_NAME.properties
  CONF_DSS_PROPERTIES=$SERVER_HOME/conf/dss.properties
  CONF_APPLICATION_YML=$SERVER_HOME/conf/application-dss.yml
  installPackage

  ###install dss-data-governance-server
  SERVER_NAME=dss-data-governance-server
  SERVER_IP=$DSS_DATA_GOVERNANCE_SERVER_INSTALL_IP
  SERVER_PORT=$DSS_DATA_GOVERNANCE_SERVER_PORT
  UPLOAD_LIB_FILES=$DSS_FILE_PATH/lib/dss-data-governance/$SERVER_NAME
  LIB_PATH=$SERVER_HOME/lib/dss-data-governance
  LOG_PATH=$SERVER_HOME/logs/dss-data-governance/$SERVER_NAME
  CONF_SERVER_PROPERTIES=$SERVER_HOME/conf/$SERVER_NAME.properties
  CONF_DSS_PROPERTIES=$SERVER_HOME/conf/dss.properties
  CONF_APPLICATION_YML=$SERVER_HOME/conf/application-dss.yml
  installPackage

  ###install dss-guide-server
  SERVER_NAME=dss-guide-server
  SERVER_IP=$DSS_GUIDE_SERVER_INSTALL_IP
  SERVER_PORT=$DSS_GUIDE_SERVER_PORT
  UPLOAD_LIB_FILES=$DSS_FILE_PATH/lib/dss-guide/$SERVER_NAME
  LIB_PATH=$SERVER_HOME/lib/dss-guide
  LOG_PATH=$SERVER_HOME/logs/dss-guide/$SERVER_NAME
  CONF_SERVER_PROPERTIES=$SERVER_HOME/conf/$SERVER_NAME.properties
  CONF_DSS_PROPERTIES=$SERVER_HOME/conf/dss.properties
  CONF_APPLICATION_YML=$SERVER_HOME/conf/application-dss.yml
  installPackage

  #echo "-----------------DSS install end--------------------"
  #echo ""

}
ENV_FLAG="dev"
installDssProject

echo "Congratulations! You have installed DSS $DSS_VERSION successfully, please use sbin/dss-start-all.sh to start it!"

