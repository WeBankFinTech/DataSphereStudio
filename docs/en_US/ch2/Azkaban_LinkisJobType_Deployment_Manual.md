> This article mainly explains the automatic deployment and installation steps of Linkis's Azkaban's jobType. For manual installation, please refer to Azkaban's jobType [installation steps](https://azkaban.github.io/azkaban/docs/latest/#job-types)


## 1. Ready work
1.Click [release](https://github.com/WeBankFinTech/DataSphereStudio/releases/download/0.5.0/linkis-jobtype-0.5.0.zip) to select the corresponding installation package to download:

- linkis-jobtype-$version.zip

2.Extract the installation package
```
unzip linkis-jobtype-$version.zip
```
## 2. Change setting
1.Enter the bin directory：

```
cd linkis/bin/
```
2.Change setting：
```
##Linkis gateway url 
LINKIS_GATEWAY_URL=http://127.0.0.1:9001 ## Linkis' GateWay address

##Linkis gateway token default172.0.0.1 
LINKIS_GATEWAY_TOKEN=172.0.0.1  ## Linkis proxy token, this parameter can use the default

##Azkaban executor host 
AZKABAN_EXECUTOR_HOST=127.0.0.1 ## AZKABAN actuator machine IP

### SSH Port 
SSH_PORT=22 ## SSH port

##Azkaban executor  dir 
AZKABAN_EXECUTOR_DIR=/tmp/Install/AzkabanInstall/executor ## The installation directory of the actuator

##Azkaban executor plugin reload url (请注意：Azkaban的执行器地址，不是Azkaban的webServer地址)
AZKABAN_EXECUTOR_URL=http://127.0.0.1:12321/executor?action=reloadJobTypePlugins ##Only need to modify the IP and port here
```
## 3. Execute the installation script
```
sh install.sh
```
If the installation is successful, it will print:
```{"status":"success"}```

