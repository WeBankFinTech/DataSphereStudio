# DataSphere Studio快速安装使用文档

由于DataSphere Studio依赖于[Linkis](https://github.com/WeBankFinTech/Linkis)，本文档提供了以下两种部署方式供您选择：

1. DSS & Linkis 一键部署

&nbsp; &nbsp; &nbsp;该模式适合于DSS和Linkis都没有安装的情况。
&nbsp; &nbsp; &nbsp;进入[DSS & Linkis安装环境准备]()

2. DSS 一键部署

&nbsp; &nbsp; &nbsp;该模式适合于Linkis已经安装，需要安装DSS的情况。

&nbsp; &nbsp; &nbsp;进入[DSS快速安装文档](https://github.com/WeBankFinTech/DataSphereStudio/blob/master/docs/zh_CN/ch2/DSS%E5%BF%AB%E9%80%9F%E5%AE%89%E8%A3%85%E4%BD%BF%E7%94%A8%E6%96%87%E6%A1%A3.md)

&nbsp; &nbsp; &nbsp;**请根据实际情况，选择合理安装方式**。

## 一、DSS & Linkis安装环境准备

**根据安装难度，我们提供了以下两种环境准备方式，请根据需要选择：**

1. **精简版**

&nbsp; &nbsp; &nbsp;没有任何安装难度，适合于调研和学习，10分钟即可部署起来。

&nbsp; &nbsp; &nbsp;支持的功能有：

- 数据开发IDE - Scriptis，仅支持：执行Python和JDBC脚本
- Linkis管理台

**进入[DSS & Linkis精简版环境准备]()**

2. **标准版**：

&nbsp; &nbsp; &nbsp;有一定的安装难度，体现在Hadoop、Hive和Spark版本不同时，可能需要重新编译，可能会出现包冲突问题。

适合于试用和生产使用，2~3小时即可部署起来。

&nbsp; &nbsp; &nbsp;支持的功能有：

- 数据开发IDE - Scriptis

- 工作流实时执行(**单机版**)

- 信号功能和邮件功能

- 数据可视化 - Visualis(**单机版**)

- 数据质量 - Qualitis(**单机版**)

- 工作流定时调度 - Azkaban(**单机版**)

- Linkis管理台

**进入[DSS & Linkis标准版环境准备]()**

----

## 二、DSS & Linkis精简版环境准备

### a. 基础软件安装

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;下面的软件必装：

- MySQL (5.5+)，[如何安装MySQL](https://www.runoob.com/mysql/mysql-install.html)
- JDK (1.8.0_141以上)，[如何安装JDK](https://www.runoob.com/java/java-environment-setup.html)
- Python(2.x和3.x都支持)，[如何安装Python](https://www.runoob.com/python/python-install.html)
- Nginx，[如何安装Nginx](https://www.tecmint.com/install-nginx-on-centos-7/) 

### b. 创建用户

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;例如: **部署用户是hadoop账号**

1. 在部署机器上创建部署用户，用于安装

```bash
    sudo useradd hadoop  
```
        
2. 因为Linkis的服务是以 sudo -u ${linux-user} 方式来切换引擎，从而执行作业，所以部署用户需要有 sudo 权限，而且是免密的。

```bash
    vi /etc/sudoers
```

         hadoop  ALL=(ALL)       NOPASSWD: NOPASSWD: ALL

3. **如果您的Python想拥有画图功能，则还需在安装节点，安装画图模块**。命令如下：

```bash
    python -m pip install matplotlib
```

### c. 安装包准备
DSS_LINKIS一键部署，安装包准备有两种方式:
1. 下载DataSphereStudio官方预编译且打包在一起的安装包，进行安装.
2. 用户自行编译或者去各个组件的release页面下载后替换一键部署目录中预编译的安装包。

（1）使用DSS社区预编译好的release安装包

请访问[点我进入下载页面](https://github.com/WeBankFinTech/DataSphereStudio/issues/90)）进行下载，解压后的安装目录结构如下

```text
├── dss_linkis # 一键部署主目录
 ├── backup # 用于兼容Linkis老版本的安装启动脚本
 ├── bin # 用于一键安装启动DSS+Linkis
 ├── conf # 一键部署的配置文件
 ├── azkaban-solo-server-x.x.x.tar.gz #azkaban安装包
 ├── linkis-jobtype-x.x.x.zip #linkis jobtype安装包
 ├── wedatasphere-dss-x.x.x-dist.tar.gz # DSS后台安装包
 ├── wedatasphere-dss-web-x.x.x-dist.zip # DSS前端安装包
 ├── wedatasphere-linkis-x.x.x-dist.tar.gz # Linkis安装包
 ├── wedatasphere-qualitis-x.x.x.zip # Qualitis安装包
```


（2）用户自行编译或者去各个组件release页面下载得到的安装包

- [wedatasphere-linkis-x.x.x-dist.tar.gz](https://github.com/WeBankFinTech/Linkis/releases)
- [wedatasphere-dss-x.x.x-dist.tar.gz](https://github.com/WeBankFinTech/DataSphereStudio/releases)
- [wedatasphere-dss-web-x.x.x-dist.zip](https://github.com/WeBankFinTech/DataSphereStudio/releases)
- [linkis-jobtype-x.x.x.zip](https://github.com/WeBankFinTech/DataSphereStudio/releases)
- azkaban-solo-server-x.x.x.tar.gz
- [wedatasphere-qualitis-x.x.x.zip](https://github.com/WeBankFinTech/Qualitis/releases)


先下载[一键部署脚本](https://github.com/WeBankFinTech/DataSphereStudio/releases)，并解压，再将自行编译或单独下载的安装包放置于该解压目录下，目录层级结构与上述保持一致：

```text
├── dss_linkis # 一键部署主目录
 ├── backup # 用于兼容Linkis老版本的安装启动脚本
 ├── bin # 用于一键安装启动DSS+Linkis
 ├── conf # 一键部署的配置文件
 ├── azkaban-solo-server-x.x.x.tar.gz #azkaban安装包
 ├── linkis-jobtype-x.x.x.zip #linkis jobtype安装包
 ├── wedatasphere-dss-x.x.x-dist.tar.gz # DSS后台安装包
 ├── wedatasphere-dss-web-x.x.x-dist.zip # DSS前端安装包
 ├── wedatasphere-linkis-x.x.x-dist.tar.gz # Linkis安装包
 ├── wedatasphere-qualitis-x.x.x.zip # Qualitis安装包
```
**注意事项：**
1. Azkaban: 社区没有提供单独的release安装包，用户需要自行编译后的将安装包放置于安装目录下。
2. DSS: 用户自行编译DSS安装包，会缺失visualis-server部分，因此，visualis-server也需要用户自行编译。从[visualis项目](https://github.com/WeBankFinTech/Visualis)编译打包后放置于wedatasphere-dss-x.x.x-dist.tar.gz的share/visualis-server目录下,否则dss安装时可能报找不到visualis安装包。

### d. 修改配置

将conf目录下的config.sh.lite.template，修改为config.sh 

```shell
    cp conf/config.sh.lite.template conf/config.sh
```

**精简版可以不修改任何配置参数**，当然您也可以按需修改相关配置参数。

```
    vi conf/config.sh  
    
    SSH_PORT=22 #ssh默认端口
    deployUser="`whoami`" #默认获取当前用户为部署用户
    WORKSPACE_USER_ROOT_PATH=file:///tmp/linkis/ ##工作空间路径，默认为本地路径，尽量提前创建并授于写权限
    RESULT_SET_ROOT_PATH=file:///tmp/linkis ##结果集路径，默认为本地路径，尽量提前创建并授于写权限
    DSS_NGINX_IP=127.0.0.1 #DSS Nginx访问IP
    DSS_WEB_PORT=8088 #DSS Web页面访问端口
    
```

```properties
    # 说明：通常情况下，精简版，上述参数默认情况均可不做修改，即可直接安装使用
    
```

### e. 修改数据库配置

```bash
    vi conf/db.sh 
```

```properties
    # 设置数据库的连接信息
    MYSQL_HOST=
    MYSQL_PORT=
    MYSQL_DB=
    MYSQL_USER=
    MYSQL_PASSWORD=
```


```properties
    # 说明：此为必须配置参数，并确保可以从本机进行访问，验证方式：
    mysql -h$MYSQL_HOST -P$MYSQL_PORT -u$MYSQL_USER -p$MYSQL_PASSWORD
```

精简版配置修改完毕，进入[安装和使用]()

## 三、DSS & Linkis标准版环境准备

### a. 基础软件安装

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;下面的软件必装：

- MySQL (5.5+)，[如何安装MySQL](https://www.runoob.com/mysql/mysql-install.html)

- JDK (1.8.0_141以上)，[如何安装JDK](https://www.runoob.com/java/java-environment-setup.html)

- Python(2.x和3.x都支持)，[如何安装Python](https://www.runoob.com/python/python-install.html)

- Nginx，[如何安装Nginx](https://www.tecmint.com/install-nginx-on-centos-7/) 

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;下面的服务必须可从本机访问：

- Hadoop(**2.7.2，Hadoop其他版本需自行编译Linkis**) 

- Hive(**1.2.1，Hive其他版本需自行编译Linkis**)

- Spark(**支持2.0以上所有版本**) 

### b. 创建用户
        
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;例如: **部署用户是hadoop账号**
   
1. 在所有需要部署的机器上创建部署用户，用于安装

```bash
    sudo useradd hadoop
```  
        
2. 因为Linkis的服务是以 sudo -u ${linux-user} 方式来切换引擎，从而执行作业，所以部署用户需要有 sudo 权限，而且是免密的。

```bash
    vi /etc/sudoers
```

```properties
    hadoop  ALL=(ALL)       NOPASSWD: NOPASSWD: ALL
```

3. 确保部署 DSS 和 Linkis 的服务器可正常访问Hadoop、Hive和Spark。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**部署DSS 和 Linkis 的服务器，不要求必须安装Hadoop，但要求hdfs命令必须可用，如：hdfs dfs -ls /**。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**如果想使用Linkis的Spark，部署 Linkis 的服务器，要求spark-sql命令必须可以正常启动一个spark application**。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**在每台安装节点设置如下的全局环境变量**，以便Linkis能正常读取Hadoop、Hive和Spark的配置文件，具备访问Hadoop、Hive和Spark的能力。
  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;修改安装用户hadoop的.bash_rc，命令如下：

```bash     
    vim /home/hadoop/.bash_rc
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;下方为环境变量示例：

```bash
    #JDK
    export JAVA_HOME=/nemo/jdk1.8.0_141
    #HADOOP
    export HADOOP_CONF_DIR=/appcom/config/hadoop-config
    #Hive
    export HIVE_CONF_DIR=/appcom/config/hive-config
    #Spark
    export SPARK_HOME=/appcom/Install/spark
    export SPARK_CONF_DIR=/appcom/config/spark-config
    export PYSPARK_ALLOW_INSECURE_GATEWAY=1  # Pyspark必须加的参数
```

4. **如果您的Pyspark想拥有画图功能，则还需在所有安装节点，安装画图模块**。命令如下：

```bash
    python -m pip install matplotlib
```

### c. 安装包准备

（1）使用DSS社区预编译好的release安装包

请访问[点我进入下载页面](https://github.com/WeBankFinTech/DataSphereStudio/issues/90)）进行下载，解压后的安装目录结构如下

```text
├── dss_linkis # 一键部署主目录
 ├── backup # 用于兼容Linkis老版本的安装启动脚本
 ├── bin # 用于一键安装启动DSS+Linkis
 ├── conf # 一键部署的配置文件
 ├── azkaban-solo-server-x.x.x.tar.gz #azkaban安装包
 ├── linkis-jobtype-x.x.x.zip #linkis jobtype安装包
 ├── wedatasphere-dss-x.x.x-dist.tar.gz # DSS后台安装包
 ├── wedatasphere-dss-web-x.x.x-dist.zip # DSS前端安装包
 ├── wedatasphere-linkis-x.x.x-dist.tar.gz # Linkis安装包
 ├── wedatasphere-qualitis-x.x.x.zip # Qualitis安装包
```


（2）用户自行编译或通过各个组件release下载得到的安装包

- [wedatasphere-linkis-x.x.x-dist.tar.gz](https://github.com/WeBankFinTech/Linkis/releases)
- [wedatasphere-dss-x.x.x-dist.tar.gz](https://github.com/WeBankFinTech/DataSphereStudio/releases)
- [wedatasphere-dss-web-x.x.x-dist.zip](https://github.com/WeBankFinTech/DataSphereStudio/releases)
- [linkis-jobtype-x.x.x.zip](https://github.com/WeBankFinTech/DataSphereStudio/releases)
- azkaban-solo-server-x.x.x.tar.gz
- [wedatasphere-qualitis-x.x.x.zip](https://github.com/WeBankFinTech/Qualitis/releases)

先下载[一键部署脚本](https://github.com/WeBankFinTech/DataSphereStudio/releases)，并解压，再将自行编译或单独下载的安装包放置于该解压目录下，目录层级结构与上述保持一致：

```text
├── dss_linkis # 一键部署主目录
 ├── backup # 用于兼容Linkis老版本的安装启动脚本
 ├── bin # 用于一键安装启动DSS+Linkis
 ├── conf # 一键部署的配置文件
 ├── azkaban-solo-server-x.x.x.tar.gz #azkaban安装包
 ├── linkis-jobtype-x.x.x.zip #linkis jobtype安装包
 ├── wedatasphere-dss-x.x.x-dist.tar.gz # DSS后台安装包
 ├── wedatasphere-dss-web-x.x.x-dist.zip # DSS前端安装包
 ├── wedatasphere-linkis-x.x.x-dist.tar.gz # Linkis安装包
 ├── wedatasphere-qualitis-x.x.x.zip # Qualitis安装包
```
**注意事项：**
1. Azkaban: 社区没有提供单独的release安装包，用户需要自行编译后的将安装包放置于安装目录下。
2. DSS: 用户自行编译DSS安装包，会缺失visualis-server部分，因此，visualis-server也需要用户自行编译。从[visualis项目](https://github.com/WeBankFinTech/Visualis)编译打包后放置于wedatasphere-dss-x.x.x-dist.tar.gz的share/visualis-server目录下,否则安装时可能报找不到visualis安装包。

### d. 修改配置

将conf目录下的config.sh.stand.template，修改为config.sh

```shell
    cp conf/config.sh.stand.template conf/config.sh
```

您可以按需修改相关配置参数：

```
    vi conf/config.sh   
```

参数说明如下：
```properties
    WORKSPACE_USER_ROOT_PATH=file:///tmp/linkis/ ##本地工作空间路径，默认为本地路径，尽量提前创建并授于写权限
    HDFS_USER_ROOT_PATH=hdfs:///tmp/linkis ##hdfs工作空间路径，默认为本地路径，尽量提前创建并授于写权限
    RESULT_SET_ROOT_PATH=hdfs:///tmp/linkis ##结果集路径，默认为本地路径，尽量提前创建并授于写权限
    WDS_SCHEDULER_PATH=file:///appcom/tmp/wds/scheduler ##DSS工程转换为azkaban工程后的存储路径
    #DSS Web，注意distribution.sh中VISUALIS_NGINX的IP和端口必须和此处保持一致
    DSS_NGINX_IP=127.0.0.1 #DSS Nginx访问IP
    DSS_WEB_PORT=8088 #DSS Web页面访问端口
    ##hive metastore的地址
    HIVE_META_URL=jdbc:mysql://127.0.0.1:3306/metastore?useUnicode=true
    HIVE_META_USER=xxx
    HIVE_META_PASSWORD=xxx
    ###hadoop配置文件目录
    HADOOP_CONF_DIR=/appcom/config/hadoop-config
    ###hive配置文件目录
    HIVE_CONF_DIR=/appcom/config/hive-config
    ###spark配置文件目录
    SPARK_CONF_DIR=/appcom/config/spark-config
   
```

### e. 使用分布式模式

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;如果您打算将DSS和Linkis都部署在同一台服务器上， 本步骤可以跳过。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;如果您打算将 DSS 和 Linkis 部署在多台服务器上，首先，您需要为这些服务器配置ssh免密登陆。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[如何配置SSH免密登陆](https://www.jianshu.com/p/0922095f69f3)

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;同时，您还需要修改分布式部署模式下的distribution.sh配置文件，使分布式部署生效。

```shell script
    vi conf/distribution.sh
```

```说明：IP地址和端口
   LINKIS和DSS的微服务IP地址和端口，可配置成远程地址，例如您想把LINKIS和DSS安装在不同的机器上，那么只需把linkis各项微服务的IP地址修改成与DSS不同的IP即可。
    
```

### f. 修改数据库配置

```bash
    vi conf/db.sh 
```

```properties
    # 设置数据库的连接信息
    MYSQL_HOST=
    MYSQL_PORT=
    MYSQL_DB=
    MYSQL_USER=
    MYSQL_PASSWORD=
```

```properties
    # 说明：此为必须配置参数，并确保可以从本机进行访问，验证方式：
    mysql -h$MYSQL_HOST -P$MYSQL_PORT -u$MYSQL_USER -p$MYSQL_PASSWORD
```

标准版配置修改完毕，进入[安装和使用]()

----


## 四、安装和使用

### 1. 执行安装脚本：

```bash
    sh bin/install.sh
```

### 2. 安装步骤

- 该安装脚本会检查各项集成环境命令，如果没有请按照提示进行安装，以下命令为必须项：
_yum java mysql unzip expect telnet tar sed dos2unix nginx_

- 安装过程如果有很多cp 命令提示您是否覆盖安装，说明您的系统配置有别名，输入alias，如果有cp、mv、rm的别名，如果有可以去掉，就不会再有大量提示。

- install.sh脚本会询问您安装模式。
安装模式分为精简版、标准版，请根据您准备的环境情况，选择合适的安装模式。

- install.sh脚本会询问您是否需要初始化数据库并导入元数据，linkis和dss 均会询问。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**第一次安装**必须选是。

### 3. 是否安装成功：

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;通过查看控制台打印的日志信息查看是否安装成功。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;如果有错误信息，可以查看具体报错原因。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;您也可以通过查看我们的[安装常见问题](https://github.com/WeBankFinTech/DataSphereStudio/wiki/FAQ)，获取问题的解答。

### 4. 启动服务

#### (1) 启动服务：
  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在安装目录执行以下命令，启动所有服务：    

```shell script
    sh bin/start-all.sh > start.log 2>start_error.log
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;如果启动产生了错误信息，可以查看具体报错原因。启动后，各项微服务都会进行**通信检测**，如果有异常则可以帮助用户定位异常日志和原因。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;您可以通过查看我们的[启动常见问题](https://github.com/WeBankFinTech/DataSphereStudio/wiki/FAQ)，获取问题的解答。

#### (2) 查看是否启动成功
    
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;可以在Eureka界面查看 Linkis & DSS 后台各微服务的启动情况。如下图，如您的Eureka主页**启动日志会打印此访问地址**，出现以下微服务，则表示服务都启动成功，可以正常对外提供服务了：
    
 ![Eureka](https://github.com/WeBankFinTech/DataSphereStudio/blob/master/images/zh_CN/chapter2/quickInstallUse/quickInstall.png)
  
#### (3) 谷歌浏览器访问：

请使用**谷歌浏览器**访问以下前端地址：

`http://DSS_NGINX_IP:DSS_WEB_PORT` **启动日志会打印此访问地址**。登陆时管理员的用户名和密码均为部署用户名，如部署用户为hadoop，则管理员的用户名/密码为：hadoop/hadoop。

如果您想支持更多用户登录，详见 [Linkis LDAP](https://github.com/WeBankFinTech/Linkis/wiki/%E9%83%A8%E7%BD%B2%E5%92%8C%E7%BC%96%E8%AF%91%E9%97%AE%E9%A2%98%E6%80%BB%E7%BB%93)

如何快速使用DSS, 点我进入 [DSS快速使用文档](https://github.com/WeBankFinTech/DataSphereStudio/blob/master/docs/zh_CN/ch3/DSS_User_Manual.md)

【DSS用户手册】提供了更加详尽的使用方法，点我进入 [DSS用户手册](https://github.com/WeBankFinTech/DataSphereStudio/blob/master/docs/zh_CN/ch3/DSS_User_Manual.md)
#### (4) 停止服务：
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在安装目录执行以下命令，停止所有服务：sh bin/stop-all.sh

**注意**
1. 如果用户想启动和停止**单个应用**，可修改启动脚本注释掉其他应用的启动和停止命令即可。

2. 如果用户想启动和停止**单个微服务**，则可进入该微服务安装目录下执行sh bin/start-微服务名称.sh或sh bin/stop-微服务名称.sh

## 五、云资源
## 云资源

**我们提供了DSS + Linkis + Qualitis + Visualis + Azkaban【全家桶一键部署安装包】，由于安装包过大（1.3GB），Github下载缓慢，请通过以下方式获取**：

#### Baidu cloud:
*   百度云链接：https://pan.baidu.com/s/1hmxuJtyY72D5X_dZoQIE5g 
*   Password: p82h 

#### Tencent Cloud:
*   腾讯云链接：https://share.weiyun.com/5vpLr9t
*   Password: upqgib

**以下为Linkis安装包资源：**
*   腾讯云链接：https://share.weiyun.com/5Gjz0zU
*   密码：9vctqg 
*   百度云链：https://pan.baidu.com/s/1uuogWgLE9r8EcGROkRNeKg 
*   密码：pwbz 

**以下为DSS安装包资源：**
*   腾讯云链接：https://share.weiyun.com/5n2GD0h
*   密码：p8f4ug
*   百度云链接：https://pan.baidu.com/s/18H8P75Y-cSEsW-doVRyAJQ 
*   密码：pnnj

**附Qualitis及Azkaban单机版安装包资源：**
*   腾讯云链接：https://share.weiyun.com/5fBPVIV
*   密码：cwnhgw
*   百度云链接：https://pan.baidu.com/s/1DYvm_KTljQpbdk6ZPx6K9g 
*   密码：3lnk
