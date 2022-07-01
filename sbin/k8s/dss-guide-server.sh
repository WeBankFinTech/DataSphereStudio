#!/bin/bash

SERVER_SUFFIX="dss-guide-server"


export SERVER_CONF_PATH=/opt/dss/$SERVER_SUFFIX/conf
export SERVER_LOG_PATH=/opt/dss/$SERVER_SUFFIX/logs

if [ ! -w "$SERVER_LOG_PATH" ] ; then
  mkdir -p "$SERVER_LOG_PATH"
fi

export SERVER_JAVA_OPTS="-DserviceName=$SERVER_SUFFIX -Xmx2048M -XX:+UseG1GC -Xloggc:$SERVER_LOG_PATH/linkis.log"

export SERVER_CLASS=com.webank.wedatasphere.dss.guide.server.DSSGuideApplication


export DSS_COMMONS_LIB=/opt/dss/dss-commons

export SERVER_LIB=/opt/dss/$SERVER_SUFFIX/lib


export SERVER_CLASS_PATH=$SERVER_CONF_PATH:$DSS_COMMONS_LIB/*:$SERVER_LIB/*

java $SERVER_JAVA_OPTS -cp $SERVER_CLASS_PATH $SERVER_CLASS
