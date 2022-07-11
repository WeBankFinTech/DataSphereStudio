#!/bin/bash

shellDir=`dirname $0`
workDir=`cd ${shellDir}/..;pwd`
baseDir=`cd ${workDir}/..;pwd`
k8sDir=$baseDir/k8s
yamlDir=$baseDir/k8s/yaml
tmpDir=$baseDir/k8s/tmp
cd $baseDir

# 复制文件到tmp目录
cp -rf $baseDir/web/dist $tmpDir/dist
mkdir -p $tmpDir/dss-web

# 构建镜像
docker build -f $k8sDir/base/dss-web.Dockerfile -t dss-web:1.2.0 $tmpDir