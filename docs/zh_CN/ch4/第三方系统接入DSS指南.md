### 第三方系统接入DSS指南
#### 1.指南介绍
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;本指南用于介绍第三方系统接入DSS系统的设计方案、具体实现以及举例进行说明。本指南适合于希望将自己的第三方系统接入DSS的用户阅读。
#### 2.第三方系统接入DSS的设计方案
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;DSS(DataSphere Studio)从一开始就被设计成为一个开放的、具有强扩展能力的系统。DSS系统希望第三方系统是能以插拔式的方式接入，为了实现上述的理念，DSS提出了AppJoint(应用关节)的概念。AppJoint从作用上来说是连接两个系统，并为两个系统的协调运行提供服务。
任务提交到DSS系统，并由DSS系统转发给第三方外部系统进行执行，必须要考虑并实现下面的几点功能。
- 1).解决双方系统用户的鉴权问题。
- 2).双方系统都需要对用户提交任务的元数据进行正确处理
- 3).DSS系统要能以同步或者异步的方式正确地提交任务给第三方系统进行执行
- 4).任务提交到第三方系统之后，外部系统需要能将日志、状态等信息返回给DSS系统
- 5).第三方系统在任务执行完毕之后，将可能产生的任务结果等信息持久化到执行的路径

为了方便外部系统的接入，DSS提供了SDK的方式,maven依赖引入如下
```
<dependency>
    <groupId>com.webank.wedatasphere.dss</groupId>
    <artifactId>dss-appjoint-core</artifactId>
    <version>${dss.version}</version>
</dependency>
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;dss-appjoint-core提供了的AppJoint的顶级接口，想要接入DSS系统的第三方系统都需要实现该顶层接口，此接口有以下方法需要用户进行实现
- 1.getSecurityService

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;SecurityService是用来进行解决DSS系统与第三方系统的鉴权问题。用户在DSS系统进行登录之后，并希望提交任务到第三方系统，首先第三方系统需要能够将这个用户进行鉴权。
- 2.getProjectSerivice

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;ProjectService是用来进行解决DSS系统与第三方系统的工程管理问题。用户在DSS系统进行新增、删除、修改工程的时候，第三方系统也需要进行同步进行相同的动作，这样的目的是为了双方系统能够在工程层面实现统一。
- 3.getNodeService

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;NodeService是用来解决用户在DSS提交的任务在第三方系统生成相应的任务的问题。用户如果在DSS系统的工作流中新建了一个工作流节点并进行任务的编辑，第三方系统需要同步感知到
- 4.getNodeExecution

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;NodeExecution接口是用来将任务提交到第三方系统进行执行的接口,NodeExecution接口有支持短时间任务的NodeExecution和支持长时间任务的LongTermNodeExecution。一般短时间任务，如邮件发送等，可以直接实现NodeExecution接口，并重写execute方法，DSS系统同步等待任务结束。另外的长时间任务，如数据质量检测等，可以实现LongTermNodeExecution接口，并重写submit方法，返回一个NodeExecutionAction，DSS系统通过这个NodeExecutionAction可以向第三方系统获取任务的日志、状态等。

#### 3.第三方系统接入DSS的实现(以Visualis为例)

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Visualis是微众银行WeDataSphere开源的一款商业BI工具，DSS集成Visualis系统之后可以获得数据可视化的能力。Visualis接入DSS系统的代码在DSS项目中已经同步开源，下面将以开源代码为例，对步骤进行罗列分析。
Visualis接入的DSS系统的步骤如下:

**3.1.Visualis实现AppJoint接口**

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Visualis实现的 AppJoint接口的实现类是VisualisAppjoint。查看VisualisAppjoint的代码可知，它在init方法时候，初始化了自己实现的SecurityService、 NodeService以及NodeExecution。

**3.2.Visualis实现SecurtyService接口**

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Visualis实现的SecurityService接口的类名是VisualisSecurityService，并重写了login方法，为了能够进行授权登陆，Visualis采用了提供token的方式，DSS的网关对该token进行授权，这样就能够做到用户鉴权。

**3.3.Visualis实现的NodeService接口**

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Visualis实现的NodeService接口的类是VisualisNodeService，并重写了createNode，deleteNode和updateNode三个方法，这三个方法是进行在第三方系统同步生成任务元数据。例如createNode方法是通过调用visualis的HTTP接口在Visualis系统生成同一工程下面的Visualis任务。

**3.4.Visualis实现NodeExecution接口**

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Visualis实现的NodeExecution接口的类是VisualisNodeExecution，并重写了execute方法，该方法传入的两个参数为Node和NodeContext，从NodeContext中我们可以拿到用户、DSS的网关地址，还有网关验证的Token。通过这些，我们可以封装成一个HTTP的请求发送到第三方系统Visualis并从Visualis获取响应结果，NodeContext提供写入结果集的方法，如Visualis的结果集一般是以图片的形式展示，在execute方法的最后，Visualis通过nodeContext获取到一个支持图片写入的PictureResultSetWriter方法，并将结果集进行写入。

**3.5.数据库内容的更新(dss-application模块)**

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;DSS系统中有一个dss-application模块，该模块是为了能够将第三方系统的一些基本参数进行存储，例如持久化到数据库中，以方便DSS系统的中的微服务获取第三方系统的信息。
例如DSS要向Visualis进行提交请求的时候需要知道Visualis的请求的url,所以需要在dss-applition模块中的两张表dss_application和dss_workflow_node中插入相应的数据，dss_application的表字段为

|  字段名 | 字段意义 |  备注 |
| ------------ | ------------ | ------------ |
| id  | 主键|  自增主键 |
| name  | 20 |  如Visualis |
| url  | 10  |  如 http://127.0.0.1:8080 |
| is_user_need_init  | 是否需要用户初始化  |  默认否 |
| user_init_url  | 用户初始化url  |  默认空 |
| exists_project_service  | 是否存在自己的projectService服务, 存在的话要自己写appjoint实现projectService0  |   |
| enhance_json  | 加强json，在appjoint初始化的时候会作为map进行传入  |   |
| homepage_url  | 接入的系统主页url  |   |
| direct_url  | 接入的系统重定向url  |   |
表3-1 dss_application表

|  字段名 | 字段意义 |  备注 |
| ------------ | ------------ | ------------ |
| id  | 主键|  自增主键 |
| node_type  | 接入的系统可以运行的任务类型 |  如visualis可以支持执行linkis.appjoint.visualis.display和linkis.appjoint.visualis.dashboard |
| url  | 10  |  如 http://127.0.0.1:8080 |
| application_id  | dss_application表id字段的外键  |  默认否 |
| support_jump  | 是否支持跳转页面  |  1表示支持，0表示不支持 |
| enable_copy  | 是否支持复制该节点  |  1表示支持，0表示不支持 |
| jump_url  | 双击工作流节点跳转的url  | 如 http://127.0.0.1:8080  |
<br>
表3-2 dss_workflow_node表

![dss_application表示例](/images/zh_CN/chapter4/dss_application.png)<br>

图3-1 dss_application表示例图

![dss_application表示例](/images/zh_CN/chapter4/dss_workflow_node.png)<br>

图3-2 dss_workflow_node表示例图

图3-1以及图3-2是visualis中插入dss_application表和dss_workflow_node 两个表中的测试数据。您可以将您系统需要指定的参数插入到对应的表中

**3.6.前端的修改**

- 3.6.1 增加节点类型
修改src/js/service/nodeType.js文件，增加Qualitis节点类型
- 3.6.2 增加节点图标
将节点图标复制到src/js/module/process/images/路径下，目前只支持SVG格式。
- 3.6.3 新增节点配置
修改src/js/module/process/shape.js文件，增加Qualitis的节点配置信息。
- 3.6.4 修改首页单击节点事件
修改src/js/module/process/index.vue文件，增加节点单击事件以及单击事件的处理逻辑。
- 3.6.5 修改工作流节点双击事件
修改src/js/view/process/index.vue以及src/js/module/process/index.vue，增加节点双击事件以及双击事件的处理逻辑。

**3.7.编译打包成jar包放置到指定位置**

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;实现了上述的接口之后，一个AppJoint就已经实现了。打包之后，需要放置到指定的位置。jar包需要放置到dss-server和linkis-appjoint-entrance两个微服务中，以linkis-appjoint-entrance 为例(dss-server与linkis-appjoint-entrance一致)，在linkis-appjont-entrance下面的lib的同级目录有一个appjoints目录，目录下面层次如图3-3所示。
![appjoints目录示例](/images/zh_CN/chapter4/appjoints.png)<br>
图3-3 appjoints目录示例
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在appjoints目录下面新建一个visualis目录。visualis目录下面要求有lib目录，lib目录存放的是visualis在实现VisualisAppJoint的编译的jar包，当然如果有引入dss系统没有带入的jar包，也需要放置到lib目录中，如sendemail Appjoint需要发送邮件功能的依赖包，所以需要将这些依赖包和已经实现的jar包统一放置到lib目录中。另外可以将本AppJoint所需要的一些配置参数放置到appjoints.properties,DSS系统提供的AppJointLoader会将这些配置的参数读取，放置到一个Map中，在AppJoint调用init方法的时候传入。
