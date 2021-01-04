#!/bin/bash

user=$1
dic=$2
echo $1 $2;
sudo mkdir -p $dic
sudo chown $user:$user $dic