#!/bin/sh

if [ -n "$1" ]; then
  echo 'start building images'
  docker pull base:1.0.0
  cat k8s/build.info | while read line; do
    {
      imageName="${line}:$1"
      echo "build image: ${imageName}"

      docker build -t $imageName -f k8s/dockerfile/${line}.Dockerfile assembly/dss-package/target/out/dss-1.0.1
      docker push ${imageName}
    }
  done
  echo 'build finished'
else
  echo "Usage: sh build.sh {versionNumber}"
fi