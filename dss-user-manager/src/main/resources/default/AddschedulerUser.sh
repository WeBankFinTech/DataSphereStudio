#!/bin/bash

user=$1
password=$2
installDir=$3


if grep -i "^${user}=" $installDir/linkis.properties;
  then
      sed -i '' "s/^$user=.*/$user=$password/" $installDir/linkis.properties
else
    echo "$user=$password" >> $installDir/linkis.properties
fi
