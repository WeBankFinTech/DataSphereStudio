ALL IN ONE DSS DOCKER
### 镜像制作方法
采用docker commit方式制作  
1.运行base镜像，连接到SSH  
2.安装java、nginx、hadoop、hive、spark
2.拷贝DSS&Linkis一键安装包到容器中，进行安装
3.导出容器tar包
4.压缩tar包

### 镜像编辑命令
docker run -d --name dss --privileged=true -p2200:22 -p8088:8088 -p20303:20303  --entrypoint /usr/sbin/init  dss:v6 
docker exec -it dss bash  
docker commit -a 'jack' -m 'fix bug' dss dss:v6
docker export -o dss.tar dss

### 镜像使用
1.下载容器包  
百度网盘，约6.8G  
2.加载容器  
docker import dss.tar dss:v4  
3.运行容器  
docker run -d --name dss --privileged=true -p2200:22 -p8088:8088 -p20303:20303  --entrypoint /usr/sbin/init  dss:v6
4.执行启动脚本   
docker exec -it  dss /bin/bash -c /start.sh  
启动大约10分钟  

### 卸载Docker
docker stop dss  
docker rm dss 

### 注意
由于一键安装包依赖systemctl,需要启动容器特权模式并重新指定入口

### 后续计划
逐步实现脚本化自动构建

### 临时地址
dss+linkis 单容器体验版
链接: https://pan.baidu.com/s/1PjiWj0PyKzc_h8MRr91vpA 提取码: 8x4g
