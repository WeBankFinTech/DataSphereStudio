#!/bin/bash

cd `dirname $0`
cd ..
HOME=`pwd`

export FLOW_ENTRANCE_HOME_PID=$HOME/bin/linkis.pid

function wait_for_FLOW_ENTRANCE_HOME_to_die() {
  local pid
  local count
  pid=$1
  timeout=$2
  count=0
  timeoutTime=$(date "+%s")
  let "timeoutTime+=$timeout"
  currentTime=$(date "+%s")
  forceKill=1

  while [[ $currentTime -lt $timeoutTime ]]; do
    $(kill ${pid} > /dev/null 2> /dev/null)
    if kill -0 ${pid} > /dev/null 2>&1; then
      sleep 3
    else
      forceKill=0
      break
    fi
    currentTime=$(date "+%s")
  done

  if [[ forceKill -ne 0 ]]; then
    $(kill -9 ${pid} > /dev/null 2> /dev/null)
  fi
}

if [[ ! -f "${FLOW_ENTRANCE_HOME_PID}" ]]; then
    echo "DSS SERVER is not running"
else
    pid=$(cat ${FLOW_ENTRANCE_HOME_PID})
    if [[ -z "${pid}" ]]; then
      echo "DSS SERVER is not running"
    else
      wait_for_FLOW_ENTRANCE_HOME_to_die $pid 40
      $(rm -f ${FLOW_ENTRANCE_HOME_PID})
      echo "DSS SERVER is stopped."
    fi
fi
