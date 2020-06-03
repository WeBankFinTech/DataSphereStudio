## DSS安装常见问题列表

**本文档汇总DSS安装过程中所有问题列表及解决方式，为社区用户安装DSS提供参考。**


#### (1) 创建工程失败:add scheduler project用户token为空

```
{"method":null,"status":1,"message":"error code(错误码): 90002, error message(错误信息): add scheduler project failederrCode: 90019 ,desc: errCode: 90020 ,desc: 用户token为空 ,ip: dss.com ,port: 9004 ,serviceKind: dss-server ,ip: dss.com ,port: 9004 ,serviceKind: dss-server.","data":{"errorMsg":{"serviceKind":"dss-server","level":2,"port":9004,"errCode":90002,"ip":"dss.com","desc":"add scheduler project failederrCode: 90019 ,desc: errCode: 90020 ,desc: 用户token为空 ,ip: dss.com ,port: 9004 ,serviceKind: dss-server ,ip: dss.com ,port: 9004 ,serviceKind: dss-server"}}}

```

确保dss-server的token.properties中添加了此用户，并保持与 azkaban 的 azkaban-users.xml用户一致
以hadoop用户为例:
1、在dss-server的token.properties添加
hadoop=hadoop
2、在azkaban azkaban-users.xml 文件 添加
<user groups="hadoop" password="hadoop" roles="admin" username="hadoop"/
用户名密码务必保持一致

#### (2) visualis执行报错，找不到driver驱动

```
Caused by: java.lang.Exception: /data/DSSInstall/visualis-server/bin/phantomjsis not executable!
```

下载 [driver驱动](https://phantomjs.org/download.html)，把phantomjs二进制文件放入visualis-server的bin目录下即可。


#### (3) dss-0.5.0简单版DSS创建工程失败


删除数据库中表dss_application的schedulis和qualitis记录


#### (4) DSS多次重复安装后报错:TooManyResultsException:Expected on result


删除数据库中表linkis_user、dss_user、linkis_application中的重复记录

#### (5) 访问前端出错

```
Unexpected token { in JSON at position 4
```

检查前端配置文件中linkis gateway的url配置

#### (6) DSS创建工程报表linkis.linkis_resources_task不存在

```
Cause: com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException: Table 'linkis.linkis_resources_task' doesn't exist
```

进入mysql，选择linkis的数据库，手动执行linkis安装目录中,db/moudle/linkis-bml.sql文件。

#### (7)如何配置ladp登陆

修改linkis-gateway配置目录下的linkis.properties文件，增加LDAP登陆认证。


#### (8) visualis可视化服务访问报错

a) 确保visualis-server已经启动。

b)检查visualis-server安装目录下的application.yml配置，确保以下配置准确无误

```
  url: http://0.0.0.0:0000/dss/visualis  此处url中的IP和端口必须保持与DSS前端Nginx访问的IP地址和端口一致
  access:
    address: 0.0.0.0 #frontend address，此处保持与DSS前端Nginx访问IP地址一致
    port: 0000#frontend port，此处保持与DSS前端Nginx访问端口一致
```

c) 确保数据库表dss_application中 visualis记录行，访问地址与DSS前端Nginx访问IP地址和端口一致。

d) 访问visualis出现404错误，确保Nginx配置文件中关于visualis的访问路径配置正确。

```
            location /dss/visualis {
            root   /data/DSSFront; # 示例visualis前端静态文件目录
            autoindex on;
            }
            location / {
            root   /data/DSSFront/dist; # 示例DSS前端静态文件目录
            index  index.html index.html;
            }            
```


#### (9)上传文件大小限制

```
sudo vi /etc/nginx/nginx.conf
```

更改上传大小

```
client_max_body_size 200m
```

#### (10)接口超时

```
sudo vi /etc/nginx/conf.d/dss.conf
```


更改接口超时时间

```
proxy_read_timeout 600s
```

**如果您在安装和使用DSS过程中遇到Linkis相关问题，请访问**
[linkis常见问题列表](https://github.com/WeBankFinTech/Linkis/wiki/%E9%83%A8%E7%BD%B2%E5%92%8C%E7%BC%96%E8%AF%91%E9%97%AE%E9%A2%98%E6%80%BB%E7%BB%93)
