#!/bin/bash

shellDir=`dirname $0`
workDir=`cd ${shellDir}/..;pwd`
baseDir=`cd ${workDir}/..;pwd`
k8sDir=$baseDir/k8s
yamlDir=$baseDir/k8s/yaml
tmpDir=$baseDir/k8s/tmp
echo $shellDir

$workDir/bin/build_docker.sh
docker image prune -f
docker run -it --rm dss-base:1.2.0 bash