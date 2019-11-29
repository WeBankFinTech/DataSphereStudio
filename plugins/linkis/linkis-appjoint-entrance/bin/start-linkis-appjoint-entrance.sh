#!/bin/bash

cd `dirname $0`
cd ..
HOE=`pwd`
    export DWS_ENTRANCE_HOE=$HOE

export DWS_ENTRANCE_PID=$HOE/bin/linkis-appjoint-entrance.pid

if [[ -f "${DWS_ENTRANCE_PID}" ]]; then
    pid=$(cat ${DWS_ENTRANCE_PID})
    if kill -0 ${pid} >/dev/null 2>&1; then
      echo "Entrance is already running."
      return 0;
    fi
fi

export DWS_ENTRANCE_LOG_PATH=$HOE/logs
export DWS_ENTRANCE_DEBUG="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=19959"
export DWS_ENTRANCE_HEAP_SIZE="2G"
export DWS_ENTRANCE_JAVA_OPTS="-Xms$DWS_ENTRANCE_HEAP_SIZE -Xmx$DWS_ENTRANCE_HEAP_SIZE -XX:+UseG1GC -XX:MaxPermSize=500m $DWS_ENTRANCE_DEBUG"

cmd="nohup java $DWS_ENTRANCE_JAVA_OPTS -cp $HOE/conf:$HOE/lib/* com.webank.wedatasphere.linkis.DataWorkCloudApplication 2>&1 > $DWS_ENTRANCE_LOG_PATH/linkis.out &"
#echo "CMD IS $cmd"


nohup java $DWS_ENTRANCE_JAVA_OPTS -cp $HOE/conf:$HOE/lib/* com.webank.wedatasphere.linkis.DataWorkCloudApplication 2>&1 > $DWS_ENTRANCE_LOG_PATH/linkis.out &
pid=$!
if [[ -z "${pid}" ]]; then
    echo "AppJoint Entrance start failed!"
    exit 1
else
    echo "AppJoint Entrance start succeeded!"
    echo $pid > $DWS_ENTRANCE_PID
    sleep 1
fi
