### deploy user
deployUser=root

## max memory for services
SERVER_HEAP_SIZE="512M"

### The install home path of DSS，Must provided
DSS_INSTALL_HOME=/appcom/Install/DSSInstall

DSS_VERSION=1.1.0

DSS_FILE_NAME="dss-$DSS_VERSION"

###  Linkis EUREKA  information.  # Microservices Service Registration Discovery Center
EUREKA_INSTALL_IP=127.0.0.1
EUREKA_PORT=9600
### If EUREKA  has safety verification, please fill in username and password
#EUREKA_USERNAME=
#EUREKA_PASSWORD=

### Linkis Gateway  information
GATEWAY_INSTALL_IP=127.0.0.1
GATEWAY_PORT=9001

################### The install Configuration of all Micro-Services #####################
#
#    NOTICE:
#       1. If you just wanna try, the following micro-service configuration can be set without any settings.
#            These services will be installed by default on this machine.
#       2. In order to get the most complete enterprise-level features, we strongly recommend that you install
#          the following microservice parameters
#

### DSS_SERVER
### This service is used to provide dss-server capability.

### project-server
DSS_FRAMEWORK_PROJECT_SERVER_INSTALL_IP=127.0.0.1
DSS_FRAMEWORK_PROJECT_SERVER_PORT=9002
### orchestrator-server
DSS_FRAMEWORK_ORCHESTRATOR_SERVER_INSTALL_IP=127.0.0.1
DSS_FRAMEWORK_ORCHESTRATOR_SERVER_PORT=9003
### apiservice-server
DSS_APISERVICE_SERVER_INSTALL_IP=127.0.0.1
DSS_APISERVICE_SERVER_PORT=9004
### dss-workflow-server
DSS_WORKFLOW_SERVER_INSTALL_IP=127.0.0.1
DSS_WORKFLOW_SERVER_PORT=9005
### dss-flow-execution-server
DSS_FLOW_EXECUTION_SERVER_INSTALL_IP=127.0.0.1
DSS_FLOW_EXECUTION_SERVER_PORT=9006
###dss-scriptis-server
DSS_SCRIPTIS_SERVER_INSTALL_IP=127.0.0.1
DSS_SCRIPTIS_SERVER_PORT=9008

###dss-data-api-server
DSS_DATA_API_SERVER_INSTALL_IP=127.0.0.1
DSS_DATA_API_SERVER_PORT=9208
###dss-data-governance-server
DSS_DATA_GOVERNANCE_SERVER_INSTALL_IP=127.0.0.1
DSS_DATA_GOVERNANCE_SERVER_PORT=9209
###dss-guide-server
DSS_GUIDE_SERVER_INSTALL_IP=127.0.0.1
DSS_GUIDE_SERVER_PORT=9210

############## ############## dss_appconn_instance configuration   start   ############## ##############
####eventchecker表的地址，一般就是dss数据库
EVENTCHECKER_JDBC_URL="jdbc:mysql://$MYSQL_HOST:$MYSQL_PORT/$MYSQL_DB?characterEncoding=UTF-8"
EVENTCHECKER_JDBC_USERNAME=$MYSQL_USER
EVENTCHECKER_JDBC_PASSWORD=$MYSQL_PASSWORD

#### hive地址
DATACHECKER_JOB_JDBC_URL="jdbc:mysql://127.0.0.1:3306/hive_gz_bdap_test_01?useUnicode=true"
DATACHECKER_JOB_JDBC_USERNAME=hadoop
DATACHECKER_JOB_JDBC_PASSWORD=hadoop
#### 元数据库，可配置成和DATACHECKER_JOB的一致
DATACHECKER_BDP_JDBC_URL="jdbc:mysql://127.0.0.1:3306/uat2_metastore?characterEncoding=UTF-8"
DATACHECKER_BDP_JDBC_USERNAME=hadoop
DATACHECKER_BDP_JDBC_PASSWORD=hadoop

EMAIL_HOST=smtp.163.com
EMAIL_PORT=25
EMAIL_USERNAME=xxx@163.com
EMAIL_PASSWORD=xxxxx
EMAIL_PROTOCOL=smtp
############## ############## dss_appconn_instance configuration   end   ############## ##############

