#!/bin/bash

user=$1
dir=$2
hdfs dfs -mkdir -p $dir
hdfs dfs -chown $user:$user $dir