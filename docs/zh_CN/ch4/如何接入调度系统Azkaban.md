## 如何接入调度系统Azkaban：
 Azkaban目前是作为一个SchedulerAppJoint在DSS-SERVER中使用，通过AzkabanSchedulerAppJoint实现了Azkaban的工程服务和安全认证服务，
 主要提供了工程的创建、更新、发布、删除，以及安全认证服务相关的代理登录，Cookie保存等。
 
 **前提条件：用户已经安装部署好社区版本的Azkaban-3.69.X环境**
 
 (1) 安装DSS前配置Azkaban的环境信息 
 
   在安装DSS之前，在工程的conf目录下配置Azkaban的IP地址和端口信息:
    
```
    #azkaban.address
    AZKABAN_ADRESS_IP=127.0.0.1
    AZKABAN_ADRESS_PORT=99887
```
 
   用户使用DSS一键安装,会自动配置以下两个参数内容：
    
```
    wds.dss.appjoint.scheduler.azkaban.address=            //Azkaban 的http地址
    wds.dss.appjoint.scheduler.project.store.dir=          //Azkaban发布包临时存储目录
```
 (2) 安装DSS后配置Azkaban用户信息
 
     在DSS-SERVER服务的conf目录下放置token.properties属性文件，配置用户名和密码信息，用于登录Azkaban.示例：
     user01=1234
     说明：由于每个公司都有各自的登录认证系统，这里只提供简单实现，用户可以实现SchedulerSecurityService定义自己的登录认证方法。
     关联后DSS和Azkaban的用户必须是同一个。
     
 (3) 在DSS数据库中配置Azkaban的appjoint信息（一键安装时默认已执行不需要重复执行,单独安装需要）
 
```
    INSERT INTO `dss_application` (`id`, `name`, `url`, `is_user_need_init`, `level`, `user_init_url`, `exists_project_service`, `project_url`, `enhance_json`) VALUES (NULL, 'azkaban', NULL, '0', '1', NULL, '0', NULL, NULL);
```
    检查dss-appjoints目录下是否已经安装了schedulis的appjoint。
    
 (4) 在Azkaban上安装Linkis任务执行插件
 
   由于现在DSS的任务基本都是提交给Linkis来执行的，所以需要在Azkaban上安装一个插件，用于DSS发布到Azkaban后的调度执行。
   1、获取插件包
    
```
    /wedatasphere-dss-x.x.x-dist/share/plugins/azkaban/linkis-jobtype/linkis-jobtype-x.x.x-linkis-jobtype.zip
```
   2、安装插件
   
   把安装包解压到指定的目录下：
```
    /AzkabanInstall/wtss-exec/plugins/jobtypes/linkis
```
    
   3、配置插件
   
      private.properties（azkaban的jobtype配置）和 plugin.properties(额外的配置)
      请根据实际环境设置两个配置文件的内容
      
   4、刷新生效
   
    curl http://IP:PORT/executor?action=reloadJobTypePlugins