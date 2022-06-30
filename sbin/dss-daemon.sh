#!/bin/bash
#
# description:  Starts and stops Server
#

cd `dirname $0`
cd ..
INSTALL_HOME=`pwd`

function print_usage(){
  echo "Usage: dss-daemon [start | stop | restart | status] [serverName]"
  echo " serverName            The service name of the operation"
  echo "Most commands print help when invoked w/o parameters."
}

if [ $# != 2 ]; then
  print_usage
  exit 2
fi

# set DSS_HOME
if [ "$DSS_HOME" = "" ]; then
  export DSS_HOME=$INSTALL_HOME
fi

# set DSS_CONF_DIR
if [ "$DSS_CONF_DIR" = "" ]; then
  export DSS_CONF_DIR=$DSS_HOME/conf
fi

# get pid directory
if [ "$DSS_PID_DIR" = "" ]; then
  export DSS_PID_DIR="$DSS_HOME/pid"
fi
if [ ! -w "$DSS_PID_DIR" ] ; then
  mkdir -p "$DSS_PID_DIR"
fi
source $DSS_HOME/sbin/common.sh
source $DSS_CONF_DIR/config.sh
typeset -l PROJECT_NAME
PROJECT_NAME=$2

## get project full name
setServerName

COMMAND=$1
export SERVER_PID=$DSS_PID_DIR/$SERVER_NAME.pid
case $COMMAND in
  start|stop|restart|status)
    $COMMAND $SERVER_NAME
    ;;
  *)
    print_usage
    exit 2
    ;;
esac