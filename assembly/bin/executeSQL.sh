#!/bin/sh

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
    ENV_FLAG="dev"
    DB_CONF_PATH=${workDir}/db
    DB_DML_PATH=$DB_CONF_PATH/dss_dml_real.sql
    replaceAppConnInstanceSQL
    executeSQL
  fi
}

##choose execute mysql mode
function executeSQL() {
  chooseInstallMode

  sed -i "s/GATEWAY_INSTALL_IP/$GATEWAY_INSTALL_IP/g" $DB_DML_PATH
  sed -i "s/GATEWAY_PORT/$GATEWAY_PORT/g" $DB_DML_PATH

  sed -i "s#DSS_INSTALL_HOME_VAL#$DSS_INSTALL_HOME#g" $DB_DML_PATH

  mysql -h$MYSQL_HOST -P$MYSQL_PORT -u$MYSQL_USER -p$MYSQL_PASSWORD -D$MYSQL_DB --default-character-set=utf8 -e "source $DB_CONF_PATH/dss_ddl.sql"
  isSuccess "source dss_ddl.sql"
  mysql -h$MYSQL_HOST -P$MYSQL_PORT -u$MYSQL_USER -p$MYSQL_PASSWORD -D$MYSQL_DB --default-character-set=utf8 -e "source $DB_CONF_PATH/apps/dss_apiservice_ddl.sql"
  isSuccess "source dss_apiservice_ddl.sql"
  mysql -h$MYSQL_HOST -P$MYSQL_PORT -u$MYSQL_USER -p$MYSQL_PASSWORD -D$MYSQL_DB --default-character-set=utf8 -e "source $DB_CONF_PATH/apps/dss_dataapi_ddl.sql"
  isSuccess "source dss_dataapi_ddl.sql"
  mysql -h$MYSQL_HOST -P$MYSQL_PORT -u$MYSQL_USER -p$MYSQL_PASSWORD -D$MYSQL_DB --default-character-set=utf8 -e "source $DB_CONF_PATH/apps/dss_guide_ddl.sql"
  isSuccess "source dss_guide_ddl.sql"
  mysql -h$MYSQL_HOST -P$MYSQL_PORT -u$MYSQL_USER -p$MYSQL_PASSWORD -D$MYSQL_DB --default-character-set=utf8 -e "source $DB_DML_PATH"
  isSuccess "source dss_dml_real.sql"
  echo "Rebuild the table"
}

function replaceAppConnInstanceSQL() {
  DB_DML_PATH=$DB_CONF_PATH/dss_dml_real.sql
  cp -rf $DB_CONF_PATH/dss_dml.sql $DB_DML_PATH
#  sed -i "s#ORCHESTRATOR_IP#$DSS_FRAMEWORK_ORCHESTRATOR_SERVER_INSTALL_IP#g" $DB_DML_PATH
#  sed -i "s#ORCHESTRATOR_PORT#$DSS_FRAMEWORK_ORCHESTRATOR_SERVER_PORT#g" $DB_DML_PATH

  sed -i "s#WORKFLOW_IP#$DSS_WORKFLOW_SERVER_INSTALL_IP#g" $DB_DML_PATH
  sed -i "s#WORKFLOW_PORT#$DSS_WORKFLOW_SERVER_PORT#g" $DB_DML_PATH

}

chooseInstallMySQLMode
