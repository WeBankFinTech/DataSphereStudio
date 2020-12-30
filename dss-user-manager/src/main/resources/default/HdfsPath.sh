#!/bin/bash

user=$1
dic=$2
hdfs dfs -mkdir $dic
hdfs dfs -chown $user:$user