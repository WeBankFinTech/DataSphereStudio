#!/bin/bash

#当前路径
shellDir=`dirname $0`
workDir=`cd ${shellDir}/..;pwd`

echo "dss web install start"

dss_web_port=$DSS_WEB_PORT
linkis_gateway_url=$LINKIS_GATEWAY_URL
dss_nginx_ip=$DSS_NGINX_IP
#linkis_eureka_url=$LINKIS_EUREKA_URL

if [[ "$dss_web_port" == "" ]]; then
source ${workDir}/conf/config.sh
fi



# 前端放置目录，默认为解压目录
dss_basepath=$workDir
base_path='/luban'

#To be compatible with MacOS and Linux
if [[ "$OSTYPE" == "darwin"* ]]; then
    # Mac OSX
    echo "dss  install not support Mac OSX operating system"
    exit 1
elif [[ "$OSTYPE" == "linux-gnu" ]]; then
    # linux
    echo "linux"
elif [[ "$OSTYPE" == "cygwin" ]]; then
    # POSIX compatibility layer and Linux environment emulation for Windows
    echo "dss   not support Windows operating system"
    exit 1
elif [[ "$OSTYPE" == "msys" ]]; then
    # Lightweight shell and GNU utilities compiled for Windows (part of MinGW)
    echo "dss  not support Windows operating system"
    exit 1
elif [[ "$OSTYPE" == "win32" ]]; then
    echo "dss  not support Windows operating system"
    exit 1
elif [[ "$OSTYPE" == "freebsd"* ]]; then
    # ...
    echo "freebsd"
else
    # Unknown.
    echo "Operating system unknown, please tell us(submit issue) for better service"
    exit 1
fi

# 区分版本
version=`cat /etc/redhat-release|sed -r 's/.* ([0-9]+)\..*/\1/'`


echo "========================================================================配置信息======================================================================="

echo "DSS web访问端口：${dss_web_port}"
echo "Linkis gateway 的地址：${linkis_gateway_url}"
echo "DSS web 静态文件地址：${dss_basepath}/dist"
echo "DSS web 安装路径：${workDir}"
echo "DSS nginx ip：${dss_nginx_ip}"

echo "========================================================================配置信息======================================================================="
echo ""


# 创建文件并配置nginx
dssConf(){
    s_host='$host'
    s_remote_addr='$remote_addr'
    s_proxy_add_x_forwarded_for='$proxy_add_x_forwarded_for'
    s_http_upgrade='$http_upgrade'
    sudo sh -c "echo '
        server {
            listen       $dss_web_port;# 访问端口
            server_name  localhost;
            #charset koi8-r;
            access_log  /var/log/nginx/${dss_web_port}.access.log  main;
            error_log  /var/log/nginx/${dss_web_port}.error.log;

            location /luban/schedule {
            port_in_redirect off;
            proxy_set_header Host $s_host;
            proxy_set_header X-Real-IP $s_remote_addr;
            proxy_set_header x_real_ipP $s_remote_addr;
            proxy_set_header remote_addr $s_remote_addr;
            proxy_set_header X-Forwarded-For $s_proxy_add_x_forwarded_for;
            proxy_http_version 1.1;
            proxy_connect_timeout 4s;
            proxy_read_timeout 600s;
            proxy_send_timeout 12s;
            proxy_set_header Upgrade $s_http_upgrade;
            proxy_set_header Connection upgrade;
            proxy_pass http://127.0.0.1:8091;
            }

            location /luban/exchangis {
            port_in_redirect off;
            proxy_set_header Host $s_host;
            proxy_set_header X-Real-IP $s_remote_addr;
            proxy_set_header x_real_ipP $s_remote_addr;
            proxy_set_header remote_addr $s_remote_addr;
            proxy_set_header X-Forwarded-For $s_proxy_add_x_forwarded_for;
            proxy_http_version 1.1;
            proxy_connect_timeout 4s;
            proxy_read_timeout 600s;
            proxy_send_timeout 12s;
            proxy_set_header Upgrade $s_http_upgrade;
            proxy_set_header Connection upgrade;
            proxy_pass http://127.0.0.1:9503;
            }

            location /luban/datav {
            port_in_redirect off;
            proxy_set_header Host $s_host;
            proxy_set_header X-Real-IP $s_remote_addr;
            proxy_set_header x_real_ipP $s_remote_addr;
            proxy_set_header remote_addr $s_remote_addr;
            proxy_set_header X-Forwarded-For $s_proxy_add_x_forwarded_for;
            proxy_http_version 1.1;
            proxy_connect_timeout 4s;
            proxy_read_timeout 600s;
            proxy_send_timeout 12s;
            proxy_set_header Upgrade $s_http_upgrade;
            proxy_set_header Connection upgrade;
            proxy_pass http://127.0.0.1:8000;
            }

            location /luban/qualitis {
            port_in_redirect off;
            proxy_set_header Host $s_host;
            proxy_set_header X-Real-IP $s_remote_addr;
            proxy_set_header x_real_ipP $s_remote_addr;
            proxy_set_header remote_addr $s_remote_addr;
            proxy_set_header X-Forwarded-For $s_proxy_add_x_forwarded_for;
            proxy_http_version 1.1;
            proxy_connect_timeout 4s;
            proxy_read_timeout 600s;
            proxy_send_timeout 12s;
            proxy_set_header Upgrade $s_http_upgrade;
            proxy_set_header Connection upgrade;
            proxy_pass http://127.0.0.1:8090;
            }

            location ${base_path}/dss/visualis {
            alias   ${dss_basepath}/dss/visualis; # 静态文件目录
            autoindex on;
            }
            location ${base_path} {
            alias  ${dss_basepath}/dist; # 静态文件目录
            index  index.html index.html;
            }
            location ${base_path}/ws {
            proxy_pass $linkis_gateway_url/ws;#后端Linkis的地址
            proxy_http_version 1.1;
            proxy_set_header Upgrade $s_http_upgrade;
            proxy_set_header Connection "upgrade";
            }

            location ${base_path}/api {
            proxy_pass $linkis_gateway_url/api; #后端Linkis的地址
            proxy_set_header Host $s_host;
            proxy_set_header X-Real-IP $s_remote_addr;
            proxy_set_header x_real_ipP $s_remote_addr;
            proxy_set_header remote_addr $s_remote_addr;
            proxy_set_header X-Forwarded-For $s_proxy_add_x_forwarded_for;
            proxy_http_version 1.1;
            proxy_connect_timeout 4s;
            proxy_read_timeout 600s;
            proxy_send_timeout 12s;
            proxy_set_header Upgrade $s_http_upgrade;
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
    ' > /etc/nginx/conf.d/dss.conf"

}


centos7(){
    # nginx是否安装
    #sudo rpm -Uvh http://nginx.org/packages/centos/7/noarch/RPMS/nginx-release-centos-7-0.el7.ngx.noarch.rpm
    sudo yum install -y nginx
    sudo echo "nginx 安装成功"

    # 配置nginx
    dssConf

    # 解决 0.0.0.0:8888 问题
    sudo  yum -y install policycoreutils-python
    sudo semanage port -a -t http_port_t -p tcp $dss_web_port

    # 开放前端访问端口
    sudo firewall-cmd --zone=public --add-port=$dss_web_port/tcp --permanent

    # 重启防火墙
    sudo firewall-cmd --reload

    # 启动nginx
    sudo systemctl restart nginx

    # 调整SELinux的参数
    sudo sed -i "s/SELINUX=enforcing/SELINUX=disabled/g" /etc/selinux/config
    # 临时生效
    sudo setenforce 0

}


centos6(){
    # yum
    S_basearch='$basearch'
    S_releasever='$releasever'
    sudo echo "
    [nginx]
    name=nginx repo
    baseurl=http://nginx.org/packages/centos/$E_releasever/$S_basearch/
    gpgcheck=0
    enabled=1
    " >> /etc/yum.repos.d/nginx.repo

    # install nginx
    sudo yum install nginx -y

    # 配置nginx
    dssConf

    # 防火墙
    S_iptables=`lsof -i:$dss_web_port | wc -l`
    if [ "$S_iptables" -gt "0" ];then
    # 已开启端口防火墙重启
    sudo service iptables restart
    else
    # 未开启防火墙添加端口再重启
    sudo iptables -I INPUT 5 -i eth0 -p tcp --dport $dss_web_port -m state --state NEW,ESTABLISHED -j ACCEPT
    sudo service iptables save
    sudo service iptables restart
    fi

    # start
    sudo /etc/init.d/nginx start

    # 调整SELinux的参数
    sudo sed -i "s/SELINUX=enforcing/SELINUX=disabled/g" /etc/selinux/config

    # 临时生效
    sudo setenforce 0

}

# centos 6
if [[ $version -eq 6 ]]; then
    centos6
fi

# centos 7
if [[ $version -eq 7 ]]; then
    centos7
fi

if ! test -e $dss_basepath/dss/visualis/build.zip; then
echo "Error*************:用户自行编译安装DSS WEB时，则需要把visualis的前端安装包build.zip放置于$dss_basepath/dss/visualis用于自动化安装"
exit 1
fi

cd $dss_basepath/dss/visualis;unzip -o build.zip  > /dev/null
#echo "请浏览器访问：http://${dss_nginx_ip}:${dss_web_port}"
