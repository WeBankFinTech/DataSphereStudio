### deploy user
deployUser=hadoop

## max memory for services
SERVER_HEAP_SIZE=512M

### The install home path of DSS，Must provided
LINKIS_DSS_HOME=/data/Install/dss_install

DSS_VERSION=1.1.2

DSS_FILE_NAME=dss-1.1.2

DSS_WEB_PORT=8085

###  Linkis EUREKA information.  # Microservices Service Registration Discovery Center
EUREKA_INSTALL_IP=127.0.0.1
EUREKA_PORT=20303
### If EUREKA  has safety verification, please fill in username and password
#EUREKA_USERNAME=
#EUREKA_PASSWORD=

### Linkis Gateway information
GATEWAY_INSTALL_IP=127.0.0.1
GATEWAY_PORT=9001

### Linkis BML Token
BML_AUTH=BML-AUTH

################### The install Configuration of all Micro-Services start #####################
#
#    NOTICE:
#       1. If you just wanna try, the following micro-service configuration can be set without any settings.
#            These services will be installed by default on this machine.
#       2. In order to get the most complete enterprise-level features, we strongly recommend that you install
#          the following microservice parameters
#

### DSS_SERVER
### This service is used to provide dss-server capability.
### dss-server
DSS_SERVER_INSTALL_IP=127.0.0.1
DSS_SERVER_PORT=9043

### dss-apps-server
DSS_APPS_SERVER_INSTALL_IP=127.0.0.1
DSS_APPS_SERVER_PORT=9044
################### The install Configuration of all Micro-Services end #####################


############## ############## dss_appconn_instance configuration start ############## ##############
####eventchecker表的地址，一般就是dss数据库
EVENTCHECKER_JDBC_URL=jdbc:mysql://172.16.16.16:3305/linkis_kinghao?characterEncoding=UTF-8
EVENTCHECKER_JDBC_USERNAME=linkis
EVENTCHECKER_JDBC_PASSWORD=linkis@Wds

#### hive地址
DATACHECKER_JOB_JDBC_URL=jdbc:mysql://172.16.16.10:3306/hivemeta?useUnicode=true
DATACHECKER_JOB_JDBC_USERNAME=hivemeta
DATACHECKER_JOB_JDBC_PASSWORD=Linkishivemeta@123
#### 元数据库，可配置成和DATACHECKER_JOB的一致
DATACHECKER_BDP_JDBC_URL=jdbc:mysql://172.16.16.10:3306/hivemeta?useUnicode=true
DATACHECKER_BDP_JDBC_USERNAME=hivemeta
DATACHECKER_BDP_JDBC_PASSWORD=Linkishivemeta@123

### 邮件节点配置
EMAIL_HOST=smtp.163.com
EMAIL_PORT=25
EMAIL_USERNAME=xxx@163.com
EMAIL_PASSWORD=xxxxx
EMAIL_PROTOCOL=smtp
############## ############## dss_appconn_instance configuration end ############## ##############
