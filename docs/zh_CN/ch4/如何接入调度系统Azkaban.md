## DSS如何手动安装接入调度系统Azkaban
 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Azkaban目前是作为一个SchedulerAppJoint在DSS-SERVER中使用，通过AzkabanSchedulerAppJoint实现了Azkaban的工程服务和安全认证服务，
 主要提供了工程的创建、更新、发布、删除，以及安全认证服务相关的代理登录，Cookie保存等。
 
 **前提条件：用户已经安装部署好社区版Azkaban-3.X以上版本。**[如何安装Azkaban](https://github.com/azkaban/azkaban)  
#### **步骤：**
1、Azkaban APPJoint安装及配置
 进入DSS安装包解压目录，复制share/appjoints/schedulis/dss-azkaban-appjoint.zip到DSS安装目录的dss-appjoints/schedulis文件夹下，解压即可。

2、修改dss-server配置目录中linkis.properties配置，增加如下参数：
```
wds.dss.appjoint.scheduler.azkaban.address=http://IP地址:端口   #Azkaban的http地址
wds.dss.appjoint.scheduler.project.store.dir=file:///appcom/tmp/wds/scheduler  #Azkaban发布包临时存储目录
```

3、数据库中dss_application表修改
 修改DSS数据库dss_application表中schedulis记录行，修改url的连接IP地址和端口，保持与Azkaban Server实际地址一致。
 示例SQL：

```
INSERT INTO `dss_application` (`id`, `name`, `url`, `is_user_need_init`, `level`, `user_init_url`, `exists_project_service`, `project_url`, `enhance_json`, `if_iframe`, `homepage_url`, `redirect_url`) VALUES (NULL, 'schedulis', NULL, '0', '1', NULL, '0', NULL, NULL, '1', NULL, NULL);

UPDATE `dss_application` SET url = 'http://IP地址:端口', project_url = 'http://IP地址:端口/manager?project=${projectName}',homepage_url = 'http://IP地址:端口/homepage' WHERE `name` in
  ('schedulis');
```

4、Azkaban JobType插件安装
您还需为Azkaban安装一个JobType插件： linkis-jobtype，请点击[Linkis jobType安装文档](https://github.com/WeBankFinTech/DataSphereStudio/blob/master/docs/zh_CN/ch2/Azkaban_LinkisJobType_Deployment_Manual.md)

5、用户token配置

##### 请在DSS-SERVER服务conf目录的token.properties文件中，配置用户名和密码信息，关联DSS和Azkaban用户，因为用户通过DSS创建工程后，要发布到azkaban，用户必须保持一致。示例：
 
```
 用户名=密码
```
 
说明：由于每个公司都有各自的登录认证系统，这里只提供简单实现，用户可以实现SchedulerSecurityService定义自己的登录认证方法。azkaban用户管理可参考[Azkaban-3.x 用户管理](https://cloud.tencent.com/developer/article/1492734)及[官网](https://azkaban.readthedocs.io/en/latest/userManager.html)