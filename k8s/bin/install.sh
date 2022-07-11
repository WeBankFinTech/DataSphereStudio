#!/bin/bash

shellDir=`dirname $0`
workDir=`cd ${shellDir}/..;pwd`
baseDir=`cd ${workDir}/..;pwd`
k8sDir=$baseDir/k8s
yamlDir=$baseDir/k8s/yaml
cd $baseDir

$workDir/bin/build_maven.sh
$workDir/bin/build_docker.sh
$workDir/bin/build_web.sh
$workDir/bin/push_docker.sh
$workDir/bin/push_docker_web.sh
$workDir/bin/init_database.sh
$workDir/bin/deploy_kubernetes.sh