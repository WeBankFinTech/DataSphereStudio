#!/bin/bash
#
# description:  dss-dolphinscheduler-client start cmd
#

# get log directory
cd `dirname $0`
cd ..
DSS_DOLPHINSCHEDULER_CLIENT_HOME=`pwd`

NODE_NAME=$1

# set DSS_CONF_DIR
if [ "$DSS_DOLPHINSCHEDULER_CLIENT_CONF_DIR" = "" ]; then
  export DSS_DOLPHINSCHEDULER_CLIENT_CONF_DIR=$DSS_DOLPHINSCHEDULER_CLIENT_HOME/conf
fi

SERVER_SUFFIX="dss-dophinscheduler-client-$NODE_NAME"
## set log
if [ "$DSS_DOLPHINSCHEDULER_CLIENT_LOG_DIR" = "" ]; then
  export DSS_DOLPHINSCHEDULER_CLIENT_LOG_DIR="$DSS_DOLPHINSCHEDULER_CLIENT_HOME/logs"
fi
export SERVER_LOG_PATH=$DSS_DOLPHINSCHEDULER_CLIENT_LOG_DIR
if [ ! -w "$SERVER_LOG_PATH" ] ; then
  mkdir -p "$SERVER_LOG_PATH"
fi

if test -z "$SERVER_HEAP_SIZE"
then
  export SERVER_HEAP_SIZE="256M"
fi

if test -z "$SERVER_JAVA_OPTS"
then
  export SERVER_JAVA_OPTS="-DserviceName=$SERVER_SUFFIX -Xmx$SERVER_HEAP_SIZE -XX:+UseG1GC -Xloggc:$SERVER_LOG_PATH/$SERVER_SUFFIX.gc"
fi

export SERVER_CLASS=com.webank.wedatasphere.dss.plugins.dolphinscheduler.linkis.client.DSSDolphinSchedulerClient

## conf dir
export SERVER_CONF_PATH=$DSS_DOLPHINSCHEDULER_CLIENT_CONF_DIR

## commons lib
export DSS_DOLPHINSCHEDULER_CLIENT_COMMONS_LIB="$DSS_DOLPHINSCHEDULER_CLIENT_HOME/lib"

if [ ! -r "$DSS_DOLPHINSCHEDULER_CLIENT_COMMONS_LIB" ] ; then
    echo "the dss-dolphinscheduler-client lib '$DSS_DOLPHINSCHEDULER_CLIENT_COMMONS_LIB' is not exists."
    exit 1
fi

## set class path
export SERVER_CLASS_PATH=$SERVER_CONF_PATH:$DSS_DOLPHINSCHEDULER_CLIENT_COMMONS_LIB/*

which java
r=$?

if [ $r == 0 ]; then
  echo "command: java $SERVER_JAVA_OPTS -cp $SERVER_CLASS_PATH $SERVER_CLASS"
  java $SERVER_JAVA_OPTS -cp $SERVER_CLASS_PATH $SERVER_CLASS
else
  echo "command: $JAVA_HOME/java $SERVER_JAVA_OPTS -cp $SERVER_CLASS_PATH $SERVER_CLASS"
  $JAVA_HOME/java $SERVER_JAVA_OPTS -cp $SERVER_CLASS_PATH $SERVER_CLASS
fi