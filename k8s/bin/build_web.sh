#!/bin/bash

shellDir=`dirname $0`
workDir=`cd ${shellDir}/..;pwd`
baseDir=`cd ${workDir}/..;pwd`
k8sDir=$baseDir/k8s
yamlDir=$baseDir/k8s/yaml

cd $baseDir/web
pwd

npm config set registry=https://registry.npmmirror.com/
npm config set sass_binary_site=https://registry.npmmirror.com/binary.html?path=node-sass/
npm install lerna -g --registry=https://registry.npmmirror.com/ 
lerna bootstrap 
npm run build