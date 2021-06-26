### 镜像构建命令
docker build -f base_dockerfile -t base:0.0.1 .  
docker run -d --name dss -p 2200:22 base:0.0.1  
docker exec -it dss /bin/bash  
