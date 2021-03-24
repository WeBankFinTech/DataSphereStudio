#!/bin/bash

user=$1
password=$2
installDir=$3


if grep -i "^${user}=" $installDir/token.properties;
  then
      sed -i '' "s/^$user=.*/$user=$password/" $installDir/token.properties
else
    echo "$user=$password" >> $installDir/token.properties
fi
