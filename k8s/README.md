# Dss on K8S

## 1.持续集成环境
centos 7  
docker  18.06.3  
kubernetes 1.19.7  
jdk 8    

## 2.使用方法
### （1）修改配置文件
k8s/config/config.sh 配置dss基本参数  
k8s/config/db.sh 配置数据库连接  
### （2）执行脚本自动部署  
k8s/bin/install.sh

## 3.分布执行
### (1) 编译源码
k8s/bin/buikd_maven.sh  
k8s/bin/build_web.sh  
### (2) 构建容器
k8s/bin/build_docker.sh  
k8s/bin/build_docker_web.sh  
### (3) 推送容器
k8s/bin/push_docker.sh
### (4) 初始化数据库
k8s/bin/init_database.sh
### (6) 部署到k8s
k8s/bin/deploy_kubernetes.sh