#!/bin/sh
#
# Copyright 2019 WeBank
#
# Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

#Actively load user env
source ~/.bash_profile

local_host="`hostname --fqdn`"

ipaddr=$(ip addr | awk '/^[0-9]+: / {}; /inet.*global/ {print gensub(/(.*)\/(.*)/, "\\1", "g", $2)}'  | awk 'NR==1')

function isLocal(){
    if [ "$1" == "127.0.0.1" ];then
        return 0
    elif [ "$1" == "" ]; then
        return 0
    elif [ "$1" == "localhost" ]; then
        return 0
    elif [ "$1" == $local_host ]; then
        return 0
    elif [ "$1" == $ipaddr ]; then
        return 0
    fi
        return 1
}

function executeCMD(){
   isLocal $1
   flag=$?
   if [ $flag == "0" ];then
      echo "Is local execution:$2"
      eval $2
   else
      echo "Is remote execution:$2"
      ssh -p $SSH_PORT $1 $2
   fi

}
function copyFile(){
   isLocal $1
   flag=$?
   src=$2
   dest=$3
   if [ $flag == "0" ];then
      echo "Is local cp "
      cp -r "$src" "$dest"
   else
      echo "Is remote cp "
      scp -r -P $SSH_PORT  "$src" $1:"$dest"
   fi

}

function isSuccess(){
if [ $? -ne 0 ]; then
    echo "Failed to $1"
    exit 1
else
    echo "Succeed to $1"
fi
}