#!/bin/sh
#Actively load user env
source ~/.bashrc
shellDir=`dirname $0`
workDir=`cd ${shellDir}/..;pwd`

SERVER_IP=""
SSH_PORT=0
SERVER_HOME=""

local_host="`hostname --fqdn`"

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
      echo "Succeed to" + $1
  fi
}

function checkJava(){
	java -version
	isSuccess "execute java --version"
}

function checkExternalServer(){
  echo "telnet check for your $SERVER_NAME, if you wait for a long time,may be your $SERVER_NAME does not prepared"
  result=`echo -e "\n" | telnet $EXTERNAL_SERVER_IP $EXTERNAL_SERVER_PORT 2>/dev/null | grep Connected | wc -l`
  if [ $result -eq 1 ]; then
   echo "$SERVER_NAME is OK."
  else
    echo "$SERVER_NAME is Bad. You need to prepare the' $SERVER_NAME ' environment in advance"
    exit 1
  fi
}

##install env:expect,
sudo yum install -y expect
isSuccess "install expect"

##install env:telnet,
sudo yum install -y telnet
isSuccess "install telnet"

echo "step1:load config"
source ${workDir}/config/config.sh
source ${workDir}/config/db.sh

dos2unix ${workDir}/config/*
dos2unix ${workDir}/db/*

DSS_FILE_PATH="$workDir/$DSS_FILE_NAME"
if [ -z $DSS_FILE_NAME ]; then
  echo "DSS_FILE_NAME is null "
  exit 1
fi
function createDssFilePath(){
  if ! test -e ${DSS_FILE_PATH}; then
   sudo mkdir -p ${DSS_FILE_PATH};sudo chown -R $deployUser:$deployUser ${workDir}
   isSuccess "Create the dir of  ${DSS_FILE_PATH}"
   else
     echo "delete under ${DSS_FILE_PATH} "
     rm -rf ./$DSS_FILE_NAME/*
  fi
  if ! test -e ${DSS_FILE_PATH}.tar.gz; then
    echo "**********Error: please put ${DSS_FILE_PATH}.tar.gz in $workDir! "
    exit 1
  else
    echo "Start to unzip dss server package."
    tar -xvf ${DSS_FILE_PATH}.tar.gz -C ./$DSS_FILE_NAME/
    #tar -xvf ${DSS_FILE_PATH}.tar.gz
    isSuccess "Unzip dss server package to ${DSS_FILE_PATH}"
  fi
}
#createDssFilePath

## choose install mode
function chooseInstallMode() {
    echo "Simple installation mode"
    #check for Java
    checkJava
    #check for mysql
    SERVER_NAME=MYSQL
    EXTERNAL_SERVER_IP=$MYSQL_HOST
    EXTERNAL_SERVER_PORT=$MYSQL_PORT
    checkExternalServer
}

if [[ $EUREKA_INSTALL_IP == "127.0.0.1" ]];then
  EUREKA_INSTALL_IP=$local_host
fi
EUREKA_URL=http://$EUREKA_INSTALL_IP:$EUREKA_PORT/eureka/

if [[ $VISUALIS_NGINX_IP == "127.0.0.1" ]] || [[ $VISUALIS_NGINX_IP == "0.0.0.0" ]];then
    echo "VISUALIS_NGINX_IP is equals $VISUALIS_NGINX_IP ,we will change it to ip address"
    VISUALIS_NGINX_IP_2=$LOCAL_IP
else
    VISUALIS_NGINX_IP_2=$VISUALIS_NGINX_IP
fi
echo $VISUALIS_NGINX_IP_2

##choose install mysql mode
function chooseInstallMySQLMode() {
  echo "Do you want to clear Dss table information in the database?"
  echo " 1: Do not execute table-building statements"
  echo " 2: Dangerous! Clear all data and rebuild the tables."
  echo ""
  MYSQL_INSTALL_MODE=1
  read -p "Please input the choice:"  idx
  if [[ '2' = "$idx" ]];then
    MYSQL_INSTALL_MODE=2
    echo "You chose Rebuild the table"
  elif [[ '1' = "$idx" ]];then
    MYSQL_INSTALL_MODE=1
    echo "You chose not execute table-building statements"
  else
    echo "no choice,exit!"
    exit 1
  fi

  ##init db
  if [[ '2' = "$MYSQL_INSTALL_MODE" ]];then
    DB_CONF_PATH=${workDir}/db
    replaceAppConnInstanceSQL
    executeSQL
  fi
}

##choose execute mysql mode
function executeSQL() {
  chooseInstallMode
  mysql -h$MYSQL_HOST -P$MYSQL_PORT -u$MYSQL_USER -p$MYSQL_PASSWORD -D$MYSQL_DB --default-character-set=utf8 -e "source $DB_CONF_PATH/dss_ddl.sql"
  isSuccess "source dss_ddl.sql"
  mysql -h$MYSQL_HOST -P$MYSQL_PORT -u$MYSQL_USER -p$MYSQL_PASSWORD -D$MYSQL_DB --default-character-set=utf8 -e "source $DB_CONF_PATH/davinci.sql"
  isSuccess "source davinci.sql"
  LOCAL_IP="`ifconfig | grep 'inet' | grep -v '127.0.0.1' | cut -d: -f2 | awk '{ print $2}'`"

  if [[ $GATEWAY_INSTALL_IP == "127.0.0.1" ]];then
        echo "GATEWAY_INSTALL_IP is equals 127.0.0.1 ,we will change it to ip address"
        GATEWAY_INSTALL_IP_2=$LOCAL_IP
  else
      GATEWAY_INSTALL_IP_2=$GATEWAY_INSTALL_IP
  fi
  echo $GATEWAY_INSTALL_IP_2
  sed -i "s/GATEWAY_INSTALL_IP_2/$GATEWAY_INSTALL_IP_2/g" $DB_DML_PATH
  sed -i "s/GATEWAY_PORT/$GATEWAY_PORT/g" $DB_DML_PATH

  if [ $AZKABAN_ADRESS_IP == "127.0.0.1" ];then
      echo "AZKABAN_ADRESS_IP is equals 127.0.0.1 ,we will change it to ip address"
      AZKABAN_ADRESS_IP_2=$LOCAL_IP
  else
      AZKABAN_ADRESS_IP_2=$AZKABAN_ADRESS_IP
  fi
  echo $AZKABAN_ADRESS_IP_2
  sed -i "s/AZKABAN_ADRESS_IP_2/$AZKABAN_ADRESS_IP_2/g" $DB_DML_PATH
  sed -i "s/AZKABAN_ADRESS_PORT/$AZKABAN_ADRESS_PORT/g" $DB_DML_PATH

  sed -i "s/VISUALIS_NGINX_IP_2/$VISUALIS_NGINX_IP_2/g" $DB_DML_PATH
  sed -i "s/VISUALIS_NGINX_PORT/$VISUALIS_NGINX_PORT/g" $DB_DML_PATH

  sed -i "s#DSS_INSTALL_HOME_VAL#$DSS_INSTALL_HOME#g" $DB_DML_PATH

  mysql -h$MYSQL_HOST -P$MYSQL_PORT -u$MYSQL_USER -p$MYSQL_PASSWORD -D$MYSQL_DB --default-character-set=utf8 -e "source $DB_DML_PATH"

  isSuccess "source dss_dml.sql"
  echo "Rebuild the table"
}

function replaceAppConnInstanceSQL() {
  DB_DML_PATH=$DB_CONF_PATH/dss_dml_bak.sql
  cp -rf $DB_CONF_PATH/dss_dml.sql $DB_DML_PATH
  sed -i "s#ORCHESTRATOR_IP#$DSS_FRAMEWORK_ORCHESTRATOR_SERVER_INSTALL_IP#g" $DB_DML_PATH
  sed -i "s#ORCHESTRATOR_PORT#$DSS_FRAMEWORK_ORCHESTRATOR_SERVER_PORT#g" $DB_DML_PATH

  sed -i "s#WORKFLOW_IP#$DSS_WORKFLOW_SERVER_INSTALL_IP#g" $DB_DML_PATH
  sed -i "s#WORKFLOW_PORT#$DSS_WORKFLOW_SERVER_PORT#g" $DB_DML_PATH

  sed -i "s#VISUALIS_IP#${VISUALIS_SERVER_INSTALL_IP}#g" $DB_DML_PATH
  sed -i "s#VISUALIS_PORT#${VISUALIS_SERVER_SERVER_PORT}#g" $DB_DML_PATH

  sed -i "s#EVENTCHECKER_JDBC_URL#$EVENTCHECKER_JDBC_URL#g" $DB_DML_PATH
  sed -i "s#EVENTCHECKER_JDBC_USERNAME#$EVENTCHECKER_JDBC_USERNAME#g" $DB_DML_PATH
  sed -i "s#EVENTCHECKER_JDBC_PASSWORD#$EVENTCHECKER_JDBC_PASSWORD#g" $DB_DML_PATH

  sed -i "s#DATACHECKER_JOB_JDBC_URL#$DATACHECKER_JOB_JDBC_URL#g" $DB_DML_PATH
  sed -i "s#DATACHECKER_JOB_JDBC_USERNAME#$DATACHECKER_JOB_JDBC_USERNAME#g" $DB_DML_PATH
  sed -i "s#DATACHECKER_JOB_JDBC_PASSWORD#$DATACHECKER_JOB_JDBC_PASSWORD#g" $DB_DML_PATH

  sed -i "s#DATACHECKER_BDP_JDBC_URL#$DATACHECKER_BDP_JDBC_URL#g" $DB_DML_PATH
  sed -i "s#DATACHECKER_BDP_JDBC_USERNAME#$DATACHECKER_BDP_JDBC_USERNAME#g" $DB_DML_PATH
  sed -i "s#DATACHECKER_BDP_JDBC_PASSWORD#$DATACHECKER_BDP_JDBC_PASSWORD#g" $DB_DML_PATH

  sed -i "s#BDP_MASK_IP#${BDP_MASK_IP}#g" $DB_DML_PATH
  sed -i "s#BDP_MASK_PORT#${BDP_MASK_PORT}#g" $DB_DML_PATH
}

chooseInstallMySQLMode

##function start
function createDir(){
  echo "start to install $SERVER_NAME"
  echo "$SERVER_NAME-step2: create dir"
  if test -z "$SERVER_IP"
  then
    SERVER_IP=$local_host
  fi

  if [ -z $SERVER_HOME ]; then
    echo "SERVER_HOME is null "
    exit 1
  fi

  # SERVER_HOME
  if ! ssh -p $SSH_PORT $SERVER_IP test -e "$SERVER_HOME"; then
    ssh  -p $SSH_PORT $SERVER_IP "sudo mkdir -p $SERVER_HOME;sudo chown -R $deployUser:$deployUser $SERVER_HOME"
    isSuccess "create the lib dir of  $SERVER_HOME"
  fi

  # lib
  if ! ssh -p $SSH_PORT $SERVER_IP test -e "$LIB_PATH"; then
    ssh  -p $SSH_PORT $SERVER_IP "sudo mkdir -p $LIB_PATH;sudo chown -R $deployUser:$deployUser $SERVER_HOME"
    isSuccess "create the lib dir of  $SERVER_NAME"
  fi

  echo "delete rm -rf $LIB_PATH/*"
  # delete files under lib
  ssh  -p $SSH_PORT $SERVER_IP "rm -rf $LIB_PATH/$SERVER_NAME"


  #logs
  if ! ssh -p $SSH_PORT $SERVER_IP test -e "$LOG_PATH"; then
    ssh  -p $SSH_PORT $SERVER_IP "sudo mkdir -p $LOG_PATH;sudo chown -R $deployUser:$deployUser $SERVER_HOME"
    isSuccess "create the logs dir of  $SERVER_NAME"
  fi

   #pid
  if ! ssh -p $SSH_PORT $SERVER_IP test -e "$SERVER_HOME/pid"; then
    ssh  -p $SSH_PORT $SERVER_IP "sudo mkdir -p $SERVER_HOME/pid;sudo chown -R $deployUser:$deployUser $SERVER_HOME/pid"
    isSuccess "create the bin pid of  $SERVER_NAME"
  fi
}

function changeCommonConf(){
  ssh  -p $SSH_PORT $SERVER_IP "sed -i  \"s#defaultZone:.*#defaultZone: $EUREKA_URL#g\" $CONF_APPLICATION_YML"
  ssh  -p $SSH_PORT $SERVER_IP "sed -i  \"s#hostname:.*#hostname: $SERVER_IP#g\" $CONF_APPLICATION_YML"
  ssh  -p $SSH_PORT $SERVER_IP "sed -i  \"s#wds.linkis.server.mybatis.datasource.url.*#wds.linkis.server.mybatis.datasource.url=jdbc:mysql://${MYSQL_HOST}:${MYSQL_PORT}/${MYSQL_DB}?characterEncoding=UTF-8#g\" $CONF_DSS_PROPERTIES"
  ssh  -p $SSH_PORT $SERVER_IP "sed -i  \"s#wds.linkis.server.mybatis.datasource.username.*#wds.linkis.server.mybatis.datasource.username=$MYSQL_USER#g\" $CONF_DSS_PROPERTIES"
  ssh  -p $SSH_PORT $SERVER_IP "sed -i  \"s#wds.linkis.server.mybatis.datasource.password.*#wds.linkis.server.mybatis.datasource.password=$MYSQL_PASSWORD#g\" $CONF_DSS_PROPERTIES"
  ssh  -p $SSH_PORT $SERVER_IP "sed -i  \"s#wds.linkis.gateway.ip.*#wds.linkis.gateway.ip=$GATEWAY_INSTALL_IP#g\" $CONF_DSS_PROPERTIES"
  ssh  -p $SSH_PORT $SERVER_IP "sed -i  \"s#wds.linkis.gateway.port.*#wds.linkis.gateway.port=$GATEWAY_PORT#g\" $CONF_DSS_PROPERTIES"
  ssh  -p $SSH_PORT $SERVER_IP "sed -i  \"s#wds.linkis.gateway.url.*#wds.linkis.gateway.url=http://$GATEWAY_INSTALL_IP:$GATEWAY_PORT/#g\" $CONF_DSS_PROPERTIES"
}

##function start
function changeConf(){
  echo "$SERVER_NAME-step5:subsitution conf"
  isSuccess "subsitution conf of $SERVER_NAME"
  ssh  -p $SSH_PORT $SERVER_IP "sed -i  \"s#spring.server.port=.*#spring.server.port=$SERVER_PORT#g\" $CONF_SERVER_PROPERTIES"
  if [[ $SERVER_NAME == "dss-framework-orchestrator-server" ]] || [[ $SERVER_NAME == "dss-workflow-server" ]]; then
      SERVER_FULL_NAME=$SERVER_NAME
      SERVER_FULL_NAME=$SERVER_NAME-$ENV_FLAG
      ssh  -p $SSH_PORT $SERVER_IP "sed -i  \"s#spring.spring.application.name=.*#spring.spring.application.name=$SERVER_FULL_NAME#g\" $CONF_SERVER_PROPERTIES"
  fi
  ssh  -p $SSH_PORT $SERVER_IP "sed -i  \"s#wds.dss.appjoint.scheduler.azkaban.address.*#wds.dss.appjoint.scheduler.azkaban.address=http://${AZKABAN_ADRESS_IP}:${AZKABAN_ADRESS_PORT}#g\" $CONF_SERVER_PROPERTIES"
  ssh  -p $SSH_PORT $SERVER_IP "sed -i  \"s#wds.dss.appjoint.scheduler.project.store.dir.*#wds.dss.appjoint.scheduler.project.store.dir=$WDS_SCHEDULER_PATH#g\" $CONF_SERVER_PROPERTIES"
  isSuccess "subsitution $CONF_SERVER_PROPERTIES of $SERVER_NAME"
}
##function end


UPLOAD_PUBLIC_IPS=""
##function start
function uploadProjectFile(){
  if [[ $SERVER_IP == "127.0.0.1" ]];then
    SERVER_IP=$local_host
  fi

  echo "$SERVER_NAME-step3:copy install package"

  # upload project lib
  scp -r -p $SSH_PORT $UPLOAD_LIB_FILES $SERVER_IP:$LIB_PATH

  # upload project conf
  # scp -r -p $SSH_PORT ${workDir}/config/{$SERVER_NAME}.properties $SERVER_IP:$CONF_PATH

  if [[ $UPLOAD_PUBLIC_IPS == *",${ENV_FLAG}-$SERVER_IP,"* ]]; then
	  return 0
  fi

  echo ""
  echo ""
  echo "UPLOAD_PUBLIC_IPS===>$UPLOAD_PUBLIC_IPS"
  echo ""
  echo ""
  # upload conf
  scp -r -p $SSH_PORT ${DSS_FILE_PATH}/conf $SERVER_IP:$SERVER_HOME
   # upload config.sh
  scp -r -p $SSH_PORT ${workDir}/config/config.sh $SERVER_IP:$SERVER_HOME/conf/
  # upload common lib
  scp -r -p $SSH_PORT ${DSS_FILE_PATH}/lib/dss-commons $SERVER_IP:$SERVER_HOME/lib
  # upload common sbin
  scp -r -p $SSH_PORT ${DSS_FILE_PATH}/sbin $SERVER_IP:$SERVER_HOME
  # upload common bin
  #scp -r -p $SSH_PORT ${workDir}/bin $SERVER_IP:$SERVER_HOME
  # upload dss-appconns
  scp -r -p $SSH_PORT ${DSS_FILE_PATH}/dss-appconns $SERVER_IP:$SERVER_HOME

  ssh  -p $SSH_PORT $SERVER_IP "sudo chown -R $deployUser:$deployUser $SERVER_HOME"
  UPLOAD_PUBLIC_IPS="$UPLOAD_PUBLIC_IPS,${ENV_FLAG}-$SERVER_IP,"
  changeCommonConf
  echo "UPLOAD_PUBLIC_IPS-->$UPLOAD_PUBLIC_IPS"
}

##function start
function installPackage(){
  if [[ $SERVER_IP == "127.0.0.1" ]];then
    SERVER_IP=$local_host
  fi

  if [ -z $SERVER_NAME ]; then
     echo "ERROR:SERVER_NAME is null "
    exit 1
  fi

  createDir
  uploadProjectFile
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
  sed -i  s#linkis_url.*#linkis_url=${LINKIS_GATEWAY_URL}#g ${LINKIS_DSS_HOME}/web/config.sh
  isSuccess "Unzip dss web package to ${LINKIS_DSS_HOME}/web"
fi
}


##function end

##Install dss projects
function installDssProject() {
  echo ""
  echo ""
	echo "<----------------framework project server install start------------------->"
	SERVER_NAME=dss-framework-project-server
	SERVER_IP=$DSS_FRAMEWORK_PROJECT_SERVER_INSTALL_IP
	SERVER_PORT=$DSS_FRAMEWORK_PROJECT_SERVER_PORT
	SERVER_HOME=$DSS_INSTALL_HOME
	UPLOAD_LIB_FILES=$DSS_FILE_PATH/lib/dss-framework/$SERVER_NAME
	LIB_PATH=$SERVER_HOME/lib/dss-framework
	LOG_PATH=$SERVER_HOME/logs/dss-framework/$SERVER_NAME
	CONF_SERVER_PROPERTIES=$SERVER_HOME/conf/$SERVER_NAME.properties
	CONF_DSS_PROPERTIES=$SERVER_HOME/conf/dss.properties
	CONF_APPLICATION_YML=$SERVER_HOME/conf/application-dss.yml
	###install project-Server
	installPackage
	echo "<----------------$SERVER_NAME:end------------------->"
	echo ""

	echo ""
	echo "<----------------framework orchestrator server install start------------------->"
	SERVER_NAME=dss-framework-orchestrator-server
	SERVER_IP=$DSS_FRAMEWORK_ORCHESTRATOR_SERVER_INSTALL_IP
	SERVER_PORT=$DSS_FRAMEWORK_ORCHESTRATOR_SERVER_PORT
	SERVER_HOME=$DSS_INSTALL_HOME
	UPLOAD_LIB_FILES=$DSS_FILE_PATH/lib/dss-framework/$SERVER_NAME
	LIB_PATH=$SERVER_HOME/lib/dss-framework
	LOG_PATH=$SERVER_HOME/logs/dss-framework/$SERVER_NAME
	CONF_SERVER_PROPERTIES=$SERVER_HOME/conf/$SERVER_NAME.properties
	CONF_DSS_PROPERTIES=$SERVER_HOME/conf/dss.properties
	CONF_APPLICATION_YML=$SERVER_HOME/conf/application-dss.yml
	###install project-Server
	installPackage
	echo "<----------------$SERVER_NAME:end------------------->"
	echo ""


	echo ""
	echo "<---------------- dss-apiservice-server install start------------------->"
	SERVER_NAME=dss-apiservice-server
	SERVER_IP=$DSS_APISERVICE_SERVER_INSTALL_IP
	SERVER_PORT=$DSS_APISERVICE_SERVER_PORT
	SERVER_HOME=$DSS_INSTALL_HOME
	UPLOAD_LIB_FILES=$DSS_FILE_PATH/lib/dss-apps/$SERVER_NAME
	LIB_PATH=$SERVER_HOME/lib/dss-apps
	LOG_PATH=$SERVER_HOME/logs/dss-apps/$SERVER_NAME
	CONF_SERVER_PROPERTIES=$SERVER_HOME/conf/$SERVER_NAME.properties
	CONF_DSS_PROPERTIES=$SERVER_HOME/conf/dss.properties
	CONF_APPLICATION_YML=$SERVER_HOME/conf/application-dss.yml
	###install dss-apiservice-server
	installPackage
	echo "<----------------$SERVER_NAME:end------------------->"
	echo ""


	echo "<----------------$ENV_FLAG: dss-flow-execution-server install start------------------->"
	##Flow execution Install
	PACKAGE_DIR=dss
	SERVER_NAME=dss-flow-execution-server
	SERVER_IP=$DSS_FLOW_EXECUTION_SERVER_INSTALL_IP
	SERVER_PORT=$DSS_FLOW_EXECUTION_SERVER_PORT
	SERVER_HOME=$DSS_INSTALL_HOME
	UPLOAD_LIB_FILES=$DSS_FILE_PATH/lib/dss-orchestrator/$SERVER_NAME
	LIB_PATH=$SERVER_HOME/lib/dss-orchestrator
	LOG_PATH=$SERVER_HOME/logs/dss-orchestrator/$SERVER_NAME
	CONF_SERVER_PROPERTIES=$SERVER_HOME/conf/$SERVER_NAME.properties
	CONF_DSS_PROPERTIES=$SERVER_HOME/conf/dss.properties
	CONF_APPLICATION_YML=$SERVER_HOME/conf/application-dss.yml
	###Install flow execution
	installPackage
	###Update flow execution linkis.properties
	echo "$SERVER_NAME-step4:update linkis.properties"
	SERVER_CONF_PATH=$SERVER_HOME/conf/$SERVER_NAME.properties
	ssh  -p $SSH_PORT $SERVER_IP  "sed -i  \"s#wds.linkis.entrance.config.logPath.*#wds.linkis.entrance.config.logPath=$WORKSPACE_USER_ROOT_PATH#g\" $SERVER_CONF_PATH"
	ssh  -p $SSH_PORT $SERVER_IP  "sed -i  \"s#wds.linkis.resultSet.store.path.*#wds.linkis.resultSet.store.path=$RESULT_SET_ROOT_PATH#g\" $SERVER_CONF_PATH"
	#ssh  -p $SSH_PORT $SERVER_IP  "sed -i  \"s#wds.linkis.gateway.url.*#wds.linkis.gateway.url=http://${GATEWAY_INSTALL_IP}:${GATEWAY_PORT}#g\" $SERVER_CONF_PATH"
	isSuccess "subsitution linkis.properties of $SERVER_NAME"
	echo "<----------------$SERVER_NAME:end------------------->"
	echo ""


	echo ""
	echo "<---------------- dss-workflow-server install start------------------->"
	SERVER_NAME=dss-workflow-server
	SERVER_IP=$DSS_WORKFLOW_SERVER_INSTALL_IP
	SERVER_PORT=$DSS_WORKFLOW_SERVER_PORT
	SERVER_HOME=$DSS_INSTALL_HOME
	UPLOAD_LIB_FILES=$DSS_FILE_PATH/lib/dss-orchestrator/$SERVER_NAME
	LIB_PATH=$SERVER_HOME/lib/dss-orchestrator
	LOG_PATH=$SERVER_HOME/logs/dss-orchestrator/$SERVER_NAME
	CONF_SERVER_PROPERTIES=$SERVER_HOME/conf/$SERVER_NAME.properties
	CONF_DSS_PROPERTIES=$SERVER_HOME/conf/dss.properties
	CONF_APPLICATION_YML=$SERVER_HOME/conf/application-dss.yml
	###install dss-workflow-server
	installPackage
	echo "<----------------$SERVER_NAME:end------------------->"
	echo ""

}
ENV_FLAG="dev"
installDssProject