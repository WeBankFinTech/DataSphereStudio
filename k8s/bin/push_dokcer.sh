#!/bin/bash

shellDir=`dirname $0`
workDir=`cd ${shellDir}/..;pwd`
baseDir=`cd ${workDir}/..;pwd`
k8sDir=$baseDir/k8s
yamlDir=$baseDir/k8s/yaml
tmpDir=$baseDir/k8s/tmp
cd $baseDir

docker tag dss-base:1.2.0 registry.mydomain.com/library/dss-base:1.2.0
docker push registry.mydomain.com/library/dss-base:1.2.0

docker tag dss-web:1.2.0 registry.mydomain.com/library/dss-web:1.2.0
docker push registry.mydomain.com/library/dss-web:1.2.0
