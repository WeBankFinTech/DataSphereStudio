#!/bin/sh
#Actively load user env

#source ~/.bash_profile

if [ -f "~/.bashrc" ];then
  echo "Warning! user bashrc file does not exist."
else
  source ~/.bashrc
fi

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


#######设置为当前路径，如果不需要直接注掉这执行函数##########
setCurrentRoot

echo ""
echo ""
echo "########################################################################"
echo "###################### Begin to install DSS Default Appconn ######################"
echo "########################################################################"
echo "now begin to install default appconn: datachecker."
sh ${workDir}/bin/appconn-install.sh datachecker
echo "now begin to install default appconn: eventchecker."
sh ${workDir}/bin/appconn-install.sh eventchecker
echo "now begin to install default appconn: sendemail."
sh ${workDir}/bin/appconn-install.sh sendemail
isSuccess "install DSS Default Appconn"