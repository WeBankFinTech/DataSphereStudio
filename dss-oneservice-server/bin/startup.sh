#!/bin/bash
cd `dirname $0`
cd ..
HOME=`pwd`
JVM_PARAMS=$1
START_PORT=$2

export SERVER_LOG_PATH=$HOME/logs
export SERVER_CLASS=com.webank.wedatasphere.linkis.DataWorkCloudApplication
export SERVER_JAVA_OPTS=" ${JVM_PARAMS} -XX:+UseG1GC -Xloggc:$HOME/logs/linkis-oneservice-server-gc.log"
java $SERVER_JAVA_OPTS -Dnacos.enabled=true -Deureka.client.enabled=false -DnacosUrl=$nacosUrl -Dnacos.namespace=$nacoSpace -Duser.timezone=Asia/Shanghai -cp $HOME/conf:$HOME/lib/* $SERVER_CLASS --server.port=$START_PORT 2>&1 > $SERVER_LOG_PATH/linkis-oneservice-server.log
