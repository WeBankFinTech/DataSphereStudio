#!/bin/bash
cd `dirname $0`
cd ..
HOME=`pwd`

export SERVER_LOG_PATH=$HOME/logs
export SERVER_CLASS=com.webank.wedatasphere.linkis.DataWorkCloudApplication


if test -z "$SERVER_HEAP_SIZE"
then
  export SERVER_HEAP_SIZE="512M"
fi

if test -z "$SERVER_JAVA_OPTS"
then
  export SERVER_JAVA_OPTS=" -Xmx$SERVER_HEAP_SIZE -XX:+UseG1GC -Xloggc:$HOME/logs/dsss-apiservice-server-gc.log"
fi

if test -z "$START_PORT"
then
  export START_PORT=21000
fi

export SERVER_PID=$HOME/bin/linkis.pid

if [[ -f "${SERVER_PID}" ]]; then
    pid=$(cat ${SERVER_PID})
    if kill -0 ${pid} >/dev/null 2>&1; then
      echo "Server is already running."
      exit 1
    fi
fi

nohup java $SERVER_JAVA_OPTS -Duser.timezone=Asia/Shanghai -cp $HOME/conf:$HOME/lib/* $SERVER_CLASS --server.port=$START_PORT 2>&1 > $SERVER_LOG_PATH/dss-apiservice-server.log &

pid=$!
if [[ -z "${pid}" ]]; then
    echo "server $SERVER_NAME start failed!"
    exit 1
else
    echo "server $SERVER_NAME start succeeded!"
    echo $pid > $SERVER_PID
    sleep 1
fi

tail -f /dev/null
