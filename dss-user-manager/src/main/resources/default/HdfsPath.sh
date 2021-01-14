#!/bin/bash

user=$1
dir=$2
echo $1 $2
hdfs dfs -mkdir $dir
hdfs dfs -chown $user:$user $dir