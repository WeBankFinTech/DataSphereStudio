> 本文主要讲解Linkis的Azkaban的jobType自动化部署安装步骤，如果手动安装请参考Azkaban的jobType[安装步骤](https://azkaban.github.io/azkaban/docs/latest/#job-types)


## 1. 准备工作
1.点击release 选择对应的安装包进行下载:

- linkis-jobtype-$version.zip

2.解压安装包
```
unzip linkis-jobtype-$version.zip
```
## 2. 修改配置
1.进入bin目录：

```
cd linkis/bin/
```
2.修改配置：
```
##Linkis gateway url 
LINKIS_GATEWAY_URL=http://127.0.0.1:9001 ## linkis的GateWay地址

##Linkis gateway token defaultWS-AUTH 
LINKIS_GATEWAY_TOKEN=WS-AUTH  ## Linkis的代理Token，该参数可以用默认的

##Azkaban executor host 
AZKABAN_EXECUTOR_HOST=127.0.0.1 ## AZKABAN执行器机器IP

### SSH Port 
SSH_PORT=22 ## SSH端口

##Azkaban executor  dir 
AZKABAN_EXECUTOR_DIR=/tmp/Install/AzkabanInstall/executor ## 执行器的安装目录，最后不需要带上/

##Azkaban executor plugin reload url 
AZKABAN_EXECUTOR_URL=http://127.0.0.1:12321/executor?action=reloadJobTypePlugins ##这里只需要修改IP和端口即可
```
## 3. 执行安装脚本
```
sh install.sh
```
如果安装成功最后会打印：```{"status":"success"}```

