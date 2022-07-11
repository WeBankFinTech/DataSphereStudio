#!/bin/bash

shellDir=`dirname $0`
workDir=`cd ${shellDir}/..;pwd`
baseDir=`cd ${workDir}/..;pwd`
k8sDir=$baseDir/k8s
yamlDir=$baseDir/k8s/yaml
tmpDir=$baseDir/k8s/tmp
cd $baseDir

# 复制文件到tmp目录
rm -rf $tmpDir/*
cp -rf $baseDir/assembly/target/wedatasphere-dss-1.1.0-dist.tar.gz $tmpDir/
mkdir -p $tmpDir/dss
tar xzvf $baseDir/assembly/target/wedatasphere-dss-1.1.0-dist.tar.gz -C $tmpDir/dss


# 构建镜像
docker build -f $k8sDir/base/dss-base.Dockerfile -t dss-base:1.2.0 $tmpDir