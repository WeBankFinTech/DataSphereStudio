# How to quickly install and use DataSphereStudio

## DataSphereStudio installation involves both frontend and backend separately. You should prepare the installation environment before installation.

## First: Determine your installation environment.

Due to the richness of its components, DataSphereStudio can be installed as a simple version or a standard version, different with other softwares. The differences are mentioned as below:

1. **Simple Version**
Minimal environment dependencies and only basic components need to be prepared：[Linkis](https://github.com/WeBankFinTech/Linkis)、JAVA、MYSQL、[Nginx](https://www.nginx.com/). Then you can immediately enjoy the integrated Scriptis(for data development), real-time workflow execution, visualization, email delivery, datacheck and eventcheck services. Click to enter the [simple version DSS environment configuration preparation](https://github.com/WeBankFinTech/DataSphereStudio/blob/master/docs/en_US/ch2/DSS%20Quick%20Installation%20Guide.md#second-simple-version-dss-environment-configuration)

2. **Standard Version**
DSS also implements the integration of many external systems, such as [Qualitis](https://github.com/WeBankFinTech/Qualitis) and [Azkaban](https://github.com/azkaban/azkaban). If you want to use these external systems, you need to install and start the Qualities and Azkaban services before installing the simple version, and ensure that they can provide services properly. The IP and port of the corresponding service should be specified in the configuration. Click me to enter the [standard version DSS environment configuration preparation](https://github.com/WeBankFinTech/DataSphereStudio/blob/master/docs/en_US/ch2/DSS%20Quick%20Installation%20Guide.md#three-standard-dss-environment-configuration-preparation)

## Second: Simple version DSS environment configuration

DSS environment configuration can be divided into three steps, including basic software installation, backend environment configuration, and frontend environment configuration. The details are as below:

### 2.1 Frontend and backend basic software installation
Linkis standard version (above 0.9.1). How to install [Linkis](https://github.com/WeBankFinTech/Linkis/blob/master/docs/en_US/ch1/deploy.md)

JDK (above 1.8.0_141). How to install [JDK](https://www.runoob.com/java/java-environment-setup.html)

MySQL (5.5+). How to Install [MySQL](https://www.runoob.com/mysql/mysql-install.html)

Nginx. How to Install [Nginx](https://www.tecmint.com/install-nginx-on-centos-7/)

### 2.2 BackEnd environment configuration
### a. User creation
- For example: the deployment user is hadoop.
Create a deployment user on the deployment machine and use this user for installation.
```
     sudo useradd hadoop
```
(Note: It is better for users to gain sudo permission and login to the machine without password). [How to configure SSH passwordless login](https://www.jianshu.com/p/0922095f69f3)

### b. Installation package preparation
On the DSS release page [click here to enter the download page](https://github.com/WeBankFinTech/DataSphereStudio/releases), download the corresponding installation package. First decompress the installation package to the installation directory and then modify the configuration of the decompressed file

    tar -xvf  wedatasphere-dss-x.x.x-dist.tar.gz

**Note: If the installation package was compiled by the user, you need to copy the [visualis-server](https://github.com/WeBankFinTech/Visualis/releases) installation package to the share/visualis-server folder in the DSS installation directory for automated installation.**

###  c. Modify the basic configuration

```
     vi conf/config.sh
```

```properties
    <!--Note: The following are the basic DSS configuration items. If you want to experience more enterprise features, please install the standard DSS.-->
    deployUser=hadoop  #Specify the deployment user

    DSS_INSTALL_HOME=/appcom/Install/DSS    #Specify the DSS installation directory

    EUREKA_INSTALL_IP=127.0.0.1 #Linkis EUREKA Service host IP address

    EUREKA_PORT=20303  #Linkis EUREKA Service port number

    WORKSPACE_USER_ROOT_PATH=file:///tmp/Linkis   #Specify the user's root directory to store the user's script files and log files. This is the user's working space.
    RESULT_SET_ROOT_PATH=hdfs:///tmp/linkis  #Result set file path, used to store the result set file of the job

    #Used for DATACHECK
    HIVE_META_URL=jdbc:mysql://127.0.0.1:3306/linkis?characterEncoding=UTF-8
    HIVE_META_USER=xxx
    HIVE_META_PASSWORD=xxx
```
### d. Modify database configuration
```bash
    vi conf/db.sh
```

```properties
    #Set the database connection information of DSS-Server and Eventchecker AppJoint.
    MYSQL_HOST=
    MYSQL_PORT=
    MYSQL_DB=
    MYSQL_USER=
    MYSQL_PASSWORD=
```

## 2.3 Frontend environment configuration preparation
### a、Download the installation package
Click [release](https://github.com/WeBankFinTech/DataSphereStudio/releases) to download the corresponding installation package and extract it to the installation directory:
```bash
 unzip wedatasphere-dss-web-0.5.0-dist.zip
```

**Note: If the DSS frontend installation package was compiled by the user, you need to copy the [visualis frontEnd](https://github.com/WeBankFinTech/Visualis/releases) installation package to the dss/visualis folder in the DSS frontEnd-end installation directory for automated installation.**

### b、Configuration modification
    Enter the frontend working directory and edit the file in this directory

    vi conf/config.sh

Change the dss frontend port and backend linkis gateway IP address/port

```
# Configuring frontEnd-end ports
dss_port="8088"

# URL of the backend linkis gateway
linkis_url="http://127.0.0.1:9001"

# dss ip address
dss_ipaddr=$(ip addr | awk '/^[0-9]+: / {}; /inet.*global/ {print gensub(/(.*)\/(.*)/, "\\1", "g", $2)}')
```

The environment is ready, click me to enter ****[4. Installation and use](https://github.com/WeBankFinTech/DataSphereStudio/blob/master/docs/en_US/ch2/DSS%20Quick%20Installation%20Guide.md#four-installation-and-use)**

## Three Standard DSS environment configuration preparation
The standard DSS environment preparation is also divided into three parts, the frontEnd-end and back-end basic software installation, back-end environment preparation, and frontEnd-end environment preparation. The details are as follows:
### 3.1 frontEnd and BackEnd basic software installation
Linkis standard version (above 0.9.1), [How to install Linkis](https://github.com/WeBankFinTech/Linkis/blob/master/docs/en_US/ch1/deploy.md)

JDK (above 1.8.0_141), How to install [JDK](https://www.runoob.com/java/java-environment-setup.html)

MySQL (5.5+), How to Install [MySQL](https://www.runoob.com/mysql/mysql-install.html)

Nginx, How to Install [Nginx](https://www.tecmint.com/install-nginx-on-centos-7/)

Qualitis, How to Install [Qualitis](https://github.com/WeBankFinTech/Qualitis/blob/master/docs/zh_CN/ch1/%E5%BF%AB%E9%80%9F%E6%90%AD%E5%BB%BA%E6%89%8B%E5%86%8C.md)

Azkaban, How to Install [Azkaban](https://github.com/azkaban/azkaban)

**Note: To support Azkaban scheduling, linkis-jobtype needs to be installed. Please click the [Linkis jobType installation document](https://github.com/WeBankFinTech/DataSphereStudio/blob/master/docs/en_US/ch2/Azkaban_LinkisJobType_Deployment_Manual.md).**

### 3.2 BackEnd environment configuration preparation
### a. User creation
- For example: the deployment user is hadoop account
Create a deployment user on the deployment machine and use that user for installation.
```
 sudo useradd hadoop
```

**Note: It is best for users to have sudo permissions and log in to this machine without password. [How to configure SSH passwordless login](https://www.jianshu.com/p/0922095f69f3)**

### b. Installation package preparation
From the DSS released [click here to enter the download page](https://github.com/WeBankFinTech/DataSphereStudio/releases), download the corresponding installation package. First decompress the installation package to the installation directory and modify the configuration of the decompressed file
```
 tar -xvf  wedatasphere-dss-x.x.x-dist.tar.gz
```

**Note: If the installation package is compiled by the user, you need to copy the [visualis-server](https://github.com/WeBankFinTech/Visualis/releases) installation package to the share/visualis-server folder in the DSS installation directory for automated installation.**

### c. Modify the basic configuration

```
 vi conf/config.sh
```

```properties
    <!--Note: The following are mandatory configuration items for DSS. Please ensure that external services are available.-->

    deployUser=hadoop  #Specify the deployment user

    DSS_INSTALL_HOME=/appcom/Install/DSS    #Specify DSS installation directory

    EUREKA_INSTALL_IP=127.0.0.1 #Linkis EUREKA Service host IP address

    EUREKA_PORT=20303  #Linkis EUREKA Service port number

    WORKSPACE_USER_ROOT_PATH=file:///tmp/Linkis   #Specify the user's root directory and store the user's script files and log files. This is the user's working space.

    RESULT_SET_ROOT_PATH=hdfs:///tmp/linkis  # Result set file path, used to store the result set file of the job

    WDS_SCHEDULER_PATH=file:///appcom/tmp/wds/scheduler #Azkaban project storage directory

    #1、for DATACHECK
    HIVE_META_URL=jdbc:mysql://127.0.0.1:3306/linkis?characterEncoding=UTF-8
    HIVE_META_USER=xxx
    HIVE_META_PASSWORD=xxx
    #2、for Qualitis
    QUALITIS_ADRESS_IP=127.0.0.1 #QUALITIS service IP address
    QUALITIS_ADRESS_PORT=9991 #QUALITIS service port number
    #3、for AZKABAN
    AZKABAN_ADRESS_IP=127.0.0.1 #AZKABAN service IP address
    AZKABAN_ADRESS_PORT=9987 #AZKABAN service port number
```

### d. Modify database configuration

```bash
    vi conf/db.sh
```

```properties
    # Set the connection information between the DSS-Server and the EventChecker AppJoint database。
    MYSQL_HOST=
    MYSQL_PORT=
    MYSQL_DB=
    MYSQL_USER=
    MYSQL_PASSWORD=
```
### 3.3 frontEnd environment configuration preparation
### a、Download the installation package
Click [release](https://github.com/WeBankFinTech/DataSphereStudio/releases)to select the installation package to download and extract it in the installation directory:

```
 unzip wedatasphere-DataSphereStudio-x.x.x-dist.zip
```
**Note: If the DSS frontEnd-end installation package is compiled by the user, you need to copy the [visualis frontEnd](https://github.com/WeBankFinTech/Visualis/releases)  installation package to the dss/visualis folder in the DSS frontEnd installation directory for automated installation.**

### b、Configuration modification

Enter the frontEnd working directory and edit in this directory

```
 vi conf/config.sh
```

Change the dss frontEnd port and BackEnd linkis gateway IP address and port

```
# Configuring frontEnd-end ports
dss_port="8088"

# URL of the backend linkis gateway
linkis_url="http://127.0.0.1:9001"

# dss ip address
dss_ipaddr=$(ip addr | awk '/^[0-9]+: / {}; /inet.*global/ {print gensub(/(.*)\/(.*)/, "\\1", "g", $2)}')
```

The environment is ready, click me to enter **[Four  Installation and use](https://github.com/WeBankFinTech/DataSphereStudio/blob/master/docs/en_US/ch2/DSS%20Quick%20Installation%20Guide.md#four-installation-and-use)**

## Four Installation and use
### 4.1. DataSphereStudio BackEnd installation：
### a. Execute the installation script:

```bash
    sh bin/install.sh
```

### b. installation steps
- The install.sh script will ask you about the installation mode. The installation mode is the simple mode or the standard mode. Please select the appropriate installation mode according to your prepared environment. Both the simple mode and the standard mode will check the mysql service. The standard mode will also check the Qualities service and the Azkaban service.

- The install.sh script will ask if you need to initialize the database and import metadata. Because I am worried that the user repeatedly executes the install.sh script to clear the user data in the database, when the install.sh is executed, the user will be asked whether to initialize the database and import metadata. You must select Yes for the first installation.

- The install.sh script will ask you if you need to initialize the library tables that davinci depends on. If you have not installed davinci, you need to initialize the table. If you have already installed davinci, you don't need to initialize it again. Because I worry that the user will clear the davinci data already installed in mysql, so when install.sh is executed, the user will be asked if it needs to be initialized. You must select Yes for the first installation.
### c. Whether the installation was successful:
   Check whether the installation is successful by viewing the log information printed on the console.
   If there is an error message, you can view the specific cause of the error.
   You can also get answers to your questions by viewing our [FAQ](https://github.com/WeBankFinTech/DataSphereStudio/wiki/FAQ).

### d. 启动DataSphereStudio BackEnd service

### (1)、Start the service:

Execute the following command in the installation directory to start all services:
```bash
    ./bin/start-all.sh > start.log 2>start_error.log
```

### (2)、See if the startup was successful
You can view the success of the service startup on the Eureka interface, and check the method:

- Use http://${EUREKA_INSTALL_IP}:${EUREKA_PORT}, Open it in your browser to see if the service is successfully registered.

- If you did not specify EUREKA_INSTALL_IP and EUREKA_INSTALL_IP in config.sh, the HTTP address is: http://127.0.0.1:20303

- As shown in the figure below, if the following microservices appear on your Eureka homepage, the services are successfully started and services can be provided normally:

 ![Eureka](https://github.com/WeBankFinTech/DataSphereStudio/blob/master/images/zh_CN/chapter2/quickInstallUse/quickInstall.png)

### 4.2 DataSphereStudio frontEnd installation
### a、Download the installation package
Click release to select the installation package to download and extract it in the installation directory:

```bash
     unzip wedatasphere-dss-web-0.5.0-dist.zip
```
### b、deploy:

Enter the frontEnd-end working directory and execute the installation script

```bash
    sh bin/install.sh
```

After execution can be accessed directly through Google Chrome：
```bash
    http://dss_ipaddr:dss_port
```

*dss_port is the port configured in config.sh, and dss_ipaddr is the IP of the installation machine.*

If the access failed: You can check the log of install.log to see which step went wrong

**If you want to manually install the frontend, the steps are as follows:**

### 1.Modify the configuration file:
```bash
    sudo vi /etc/nginx/conf.d/dss.conf
```

 Add the following configurations:

```
server {
            listen       8080;# Access port
            server_name  localhost;
            #charset koi8-r;
            #access_log  /var/log/nginx/host.access.log  main;
			location /dss/visualis {
            root   /appcom/Install/DSS/frontEnd/dss/visualis; # visualis Static file directory
            autoindex on;
            }
            location / {
            root   /appcom/Install/DSS/frontEnd/dist; # Directory for unpacking the frontEnd package
            index  index.html index.html;
            }
            location /ws {#webSocket configuration support
            proxy_pass http://192.168.xxx.xxx:9001;#linkis-gateway Service ip port
            proxy_http_version 1.1;
            proxy_set_header Upgrade $http_upgrade;
            proxy_set_header Connection "upgrade";
            }
            location /api {
            proxy_pass http://192.168.xxx.xxx:9001; # linkis-gateway Service ip port
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header x_real_ipP $remote_addr;
            proxy_set_header remote_addr $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_http_version 1.1;
            proxy_connect_timeout 4s;
            proxy_read_timeout 600s;
            proxy_send_timeout 12s;
            proxy_set_header Upgrade $http_upgrade;
            proxy_set_header Connection upgrade;
            }
            #error_page  404              /404.html;
            # redirect server error pages to the static page /50x.html
            #
            error_page   500 502 503 504  /50x.html;
            location = /50x.html {
            root   /usr/share/nginx/html;
            }
        }
```

### 2.Copy the frontend package to the corresponding directory:

```
/appcom/Install/DSS/frontEnd; # frontend package installation directory
```
**Note: To manually install the DSS frontend , you need to go to the dss/visualis folder of the DSS frontend installation directory and decompress the visualis frontEnd installation package.**

### 3.Start service
sudo systemctl restart nginx

### 4.Google Chrome access:
```
http://nginx_ip:nginx_port
```

How to use DSS in detail, click me to enter **[DSS detailed usage document.](https://github.com/WeBankFinTech/DataSphereStudio/blob/master/docs/en_US/ch3/DataSphereStudio_quick_start.md)**

### 4.3、common problem

(1)User token is empty

```
sudo vi dss-server/conf/token.properties
```

Add login user

```
xxx=xxx
```

(2)visualis execution error

```
Caused by: java.lang.Exception: /data/DSSInstall/visualis-server/bin/phantomjsis not executable!
```

Download the [driver driver](https://phantomjs.org/download.html) and put the phantomjs binary file in the bin directory of visualis-server.


(3)Upload file size limit

```
sudo vi /etc/nginx/nginx.conf
```

Change upload size

```
client_max_body_size 200m
```

 (4)Interface timed out

```
sudo vi /etc/nginx/conf.d/dss.conf
```


Change the interface timeout

```
proxy_read_timeout 600s
```

