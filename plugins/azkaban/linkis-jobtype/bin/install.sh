#!/bin/sh

#Actively load user env
#source ~/.bash_profile

shellDir=`dirname $0`
workDir=`cd ${shellDir}/..;pwd`


function isSuccess(){
if [ $? -ne 0 ]; then
    echo "Failed to " + $1
    exit 1
else
    echo "Succeed to" + $1
fi
}

##load config
echo "step1:load config"
source ${workDir}/bin/config.sh
isSuccess "load config"

local_host="`hostname --fqdn`"


if test -z "$AZKABAN_EXECUTOR_HOST"
then
  AZKABAN_EXECUTOR_HOST=$local_host
fi

AZKABAN_JOBTYPE_DIR=$AZKABAN_EXECUTOR_DIR/plugins/jobtypes

if ! ssh -p $SSH_PORT $AZKABAN_EXECUTOR_HOST test -e $AZKABAN_JOBTYPE_DIR; then
  echo "ERROR:Azkaban's plugin directory does not exist!"
  exit 1
fi

echo "start to subsitution conf"
sed -i  "s#jobtype.lib.dir.*#jobtype.lib.dir=$AZKABAN_JOBTYPE_DIR/linkis/lib#g" ${workDir}/private.properties
sed -i  "s#wds.linkis.gateway.url.*#wds.linkis.gateway.url=$LINKIS_GATEWAY_URL#g" ${workDir}/plugin.properties
sed -i  "s#wds.linkis.client.flow.author.user.token.*#wds.linkis.client.flow.author.user.token=$LINKIS_GATEWAY_TOKEN#g" ${workDir}/plugin.properties
isSuccess "subsitution conf"

echo "$COPY Plugin"
ssh  -p $SSH_PORT $AZKABAN_EXECUTOR_HOST "cd $AZKABAN_JOBTYPE_DIR;rm -rf linkis-bak; mv -f linkis ../linkis-bak"

scp   -P $SSH_PORT  -r ${workDir} $AZKABAN_EXECUTOR_HOST:$AZKABAN_JOBTYPE_DIR

echo "reload jobType"

curl $AZKABAN_EXECUTOR_URL