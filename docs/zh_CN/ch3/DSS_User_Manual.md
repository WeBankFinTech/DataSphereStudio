## 快速登录
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;为了方便用户使用，系统默认通过使用Linkis的部署用户名进行登录，比如是hadoop部署的可以直接通过 用户：hadoop，密码：hadoop(密码就是用户名)来进行登录。 首先输入前端容器地址：192.168.xx.xx:8888 接着输入用户名密码：hadoop/hadoop
![quick_start00](/images/zh_CN/chapter3/quickstart/quick_start00.png)

__注意：__ 如果要支持多用户登录，DSS的用户登录依赖Linkis，需要在linkis-GateWay的配置里面进行配置，Linkis-GateWay默认支持LDAP。

## 1 功能简介
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;DSS作为一站式数据应用开发门户，定位为闭环涵盖数据应用的全流程，满足从数据ETL、数据研发、可视化展现、数据治理、数据输出到工作流调度的数据应用全生命周期开发场景,现已经开源的组件包括如下图所示：

![functions](/images/zh_CN/chapter3/manual/functions.png)

- 工作流服务——Workflow: 支持工作流的托拉拽生成，支持实时执行，支持发布到调度服务
- 数据研发———Scriptis：为DSS集成数据开发能力，支持各类型脚本语言的在线开发,详细介绍请点击[Scriptis使用手册](https://github.com/WeBankFinTech/Scriptis/blob/master/docs/zh_CN/ch4/Scriptis%E4%BD%BF%E7%94%A8%E6%89%8B%E5%86%8C.md)
- 数据可视化——Visualis: 提供可视化能力，允许数据大屏和仪表盘作为DSS应用开发节点，详细介绍请点击[Visualis使用手册](https://github.com/WeBankFinTech/Visualis)；
- 数据发送——SendEmail: 为DSS集成数据发送能力，支持table，DashBoard，文本等的发送；
- 数据质量——Qualitis： 为DSS集成数据校验能力，将数据质量系统集成到DSS工作流开发中，对数据完整性、正确性等进行校验[Qualitis使用手册](https://github.com/WeBankFinTech/Qualitis/blob/master/docs/zh_CN/ch1/%E7%94%A8%E6%88%B7%E6%89%8B%E5%86%8C.md)；
- 信号节点：事件节点（发送和接收），用于信号的传递，需要配对使用。DataChecker:用于检查Hive中的表是否准备好，如果准备后则开始执行下面的流程；
- 功能节点：连接节点（空节点），用于做连接用，子工作流，用于工作流嵌套。

下面将对这些组件的功能进行详细介绍

## 2 DSS首页介绍
### 2.1 已经集成组件展示：
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;首页对DSS集成的组件的全流程进行了展示和示意，其中每个组件的展示都支持直接跳转到对应的服务：

![components](/images/zh_CN/chapter3/manual/components.png)

如点击Scriptis图标会跳转到数据开发组件Scriptis的首页：

![components02](/images/zh_CN/chapter3/manual/components02.png)


### 2.2 DSS 工程：
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;DSS为用户构建了工程业务流程两层结构，当用户选择一个工程进行进入后，在所有组件中的操作都只与该工程相关，让用户更加清晰的去定义工程和业务的关系。
- 工程：您可以通过将一个产品对应到一个工程，用来涵盖该产品下的多个业务流程
- 工作流：对应一个业务流程，让您在开发的过程中以业务的视角来组织和系统开发

1. 现阶段工程支持版本控制，发布等功能，后续会对协同开发进行支持：

![project](/images/zh_CN/chapter3/manual/project.png)

2. 工程版本新建（回滚）：可以通过设置当前版本作为源版本复制为最新版本。

3. 工程复制：以工程的最新版本为源工程，复制出新工程，初始版本工作流内容为源工程最新版本的工作流。注意：**工程名是唯一，不可重复**

## 3工作流——workflow
### 3.1 工作流spark节点
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;spark节点分别支持sql、pyspark、scala三种方式执行spark任务，使用时只需将节点拖拽至工作台后编写代码即可。
### 3.2 工作流hive节点
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;hive节点支持sql方式执行hive任务，使用时只需将节点拖拽至工作台后编写hivesql代码即可。
### 3.3 工作流python节点
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;python节点支持执行python任务，使用时只需将节点拖拽至工作台后编写python代码即可。
### 3.4 工作流shell节点
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;shell节点支持执行shell命令或者脚本运行，使用时只需将节点拖拽至工作台后编写shell命令即可。
### 3.5 工作流jdbc节点
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;jdbc节点支持以jdbc方式运行sql命令，使用时只需将节点拖拽至工作台后编写sql即可，**注意需要提前在linkis console管理台配置jdbc连接信息。**
### 3.6 工作流编排
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;当点击一个对应的工程后，既可以进入工程首页，在工程首页可以做工作流的编排。
1. 首先需要创建工作流
![workflow01](/images/zh_CN/chapter3/manual/workflow01.png)
2. 建立好工作流好就可以进行组件节点的托拉拽了，支持第一章所提到的组件节点编排：
![workflow02](/images/zh_CN/chapter3/manual/workflow02.png)
3. 节点支持右键功能包括，删除、依赖选择、复制等基本功能，同时数据开发节点还支持脚本关联
![workflow03](/images/zh_CN/chapter3/manual/workflow03.png)

### 3.7 工作流节点打开
节点支持双击打开：
1. 数据开发节点：点开后即可进入Scriptis进行脚本编辑
![workflow04](/images/zh_CN/chapter3/manual/workflow04.png)
2. 子工作流节点：双击后跳转到子工作流编辑页面
![workflow05](/images/zh_CN/chapter3/manual/workflow05.png)
3. 数据质量节点：跳转到数据质量规则配置页面
![workflow06](/images/zh_CN/chapter3/manual/workflow06.png)
4. 数据可视化节点：跳转到对应的可视化编辑页面
![workflow07](/images/zh_CN/chapter3/manual/workflow07.png)

### 3.8 层级切换
1. 支持多层级切换：支持快速工程切换、支持在工作流页面切换工作流、支持在单个工作流中切换节点
![workflow08](/images/zh_CN/chapter3/manual/workflow08.png)

2. 右上脚支持多组件快速切换，在切换后进入的组件的内容都只与该工程相关，让用户更加清晰的去定义工程和业务的内容：
![functions](/images/zh_CN/chapter3/manual/functions.png)

### 3.9 参数和资源设置

1. 工作流上下文信息设置，支持工作流参数、变量、代理用户等

![workflow_09](/images/zh_CN/chapter3/manual/workflow_09.png)

2. 支持工作流资源文件设置
![workflow10](/images/zh_CN/chapter3/manual/workflow10.png)
支持工程级别、工作流级别、节点级别资源文件使用，您只需要在脚本中指定对应的级别就好：比如有一个test.txt 在脚本python脚本打开一个文件：
```
open("project://test.txt", encoding="utf-8") #工程级资源文件使用project://开头
open("flow://test.txt", encoding="utf-8")  #工作流级资源文件使用flow://开头
open("node://test.txt", encoding="utf-8") #节点级资源文件使用node://开头
```

### 3.10 工作流实时执行
1. 除了功能节点中的subflow会跳过执行，连接节点会作为空节点运行，其他都支持实时执行
![workflow11](/images/zh_CN/chapter3/manual/workflow11.png)
2. 用户编辑好工作流后点击执行就可以将工作流进行运行，您将看到实时的工作流运行起来可以看到现在运行节点的时间，同时可以右键节点打开节点的管理台去展示该节点的进度，运行结果，运行日志等。支持任务停止等功能
![workflow12](/images/zh_CN/chapter3/manual/workflow12.png)


### 3.11 工作流调度执行
1. DSS的工程支持发布调度，默认支持发布到Azkaban，同样DSS的调度部分做了深层次的抽象可以做到对其他的调度系统快速支持。发布前会对工作流进行解析，以确保工作流是可以调度运行的：
![workflow13](/images/zh_CN/chapter3/manual/workflow13.png)
2. 发布后即可到调度系统中进行查看，比如去Azkaban页面上进行查看：
![workflow14](/images/zh_CN/chapter3/manual/workflow14.png)
3. DSS如何对接调度系统可以参考：[]()

### 3.12 工作流版本
1. 工作流创建完成后，具有初始版本，版本号为v000001，直接点击工作流图标时，默认打开工作流的最新版本
2. 可以查看工作流的版本，方便您进行历史版本查看：
![workflow15](/images/zh_CN/chapter3/manual/workflow15.png)

### 3.13 工作流布局修改
1. 工作流格式化：当工作流节点过多，界面太乱时。可以点击节点编辑页的右上方第四个“格式化”按钮。快速美化节点界面：
![workflow16](/images/zh_CN/chapter3/manual/workflow16.png)
如果格式化后不满意，可再次点击节点编辑页的右上方第五个“恢复”按钮，恢复到之前的状态：
![workflow17](/images/zh_CN/chapter3/manual/workflow17.png)

2. 支持放大、缩小、全屏/取消全屏 按钮可调整界面大小比例

3. 多个节点移动：
 - 第一种方式是按住键盘“shift”+鼠标拖动，完全包括住的节点为选中节点，选中的节点有一圈灰色轮廓色。此时即可进行移动了
 - 第二种方式为按住键盘“ctrl”+鼠标点击选中的节点，选中的节点有一圈灰色轮廓色。此时即可进行移动了
![workflow18](/images/zh_CN/chapter3/manual/workflow18.png)



## 4 信号节点的使用：

### 4.1 eventSender节点：
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;EventSender节点用于进行信息发送，将一段信息事件进行发送给eventReceiver。常见场景工程与工程间有依赖，工作流与工作流间有信息依赖。比如B任务依赖A任务的某些信息（A任务成功B节点才能开始执行），eventSender支持如下参数：
```
1. msg.type: 用来指定Job的类型，SEND用于发送消息，RECEIVE用于接收消息。

2. msg.sender: 指定消息的发送者，需使用ProjectName@WFName@JobName的格式进行定义。

3. msg.topic: 指定消息的主题，建议使用如下格式： 一级分类编码+“”+二级分类编码+“”+三级分类编码。

4. msg.name: 指定消息名称，由用户自定义。

5. msg.body: 指定消息的内容，没有内容发送可以为空。

6. **注意：msg.type默认不可变为SEND，msg.sender、msg.topic、msg.name是必填。**
```
![signal_01](/images/zh_CN/chapter3/manual/signal_01.png)


示例：
```
msg.type=SEND

msg.sender=project01@flow@job01

msg.topic=bdp_tac_test

msg.name=TestDynamicReceive

msg.body=${msg.mycontent}
```

### 4.2 eventReceiver节点：

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;EventReceiver节点用于接收eventSender发送过来的消息，并将接收过来的消息内容存放到工作流的上下文中，后续的节点会根据前缀去找该信息进行使用比如作为自定义变量进行使用，eventReceiver支持如下参数：
```
1. msg.type: 用来指定Job的类型，SEND用于发送消息，RECEIVE用于接收消息。

2. msg.receiver: 指定消息的接收者，需使用projectname@jobname@rtxname的格式进行定义。

3. msg.topic: 指定消息的主题，建议使用如下格式： 一级分类编码+“”+二级分类编码+“”+三级分类编码。

4. msg.name: 指定消息名称，由用户自定义。

5. query.frequency: 由于接收消息使用的是主动轮询的方式，wait.time期间的查询次数，。

6. max.receive.hours: 最长的接收时长，以小时为单位，超过时长未接收到消息返回失败，。

7. msg.savekey: 用于保存消息内容key值，单个flow内多个接收job，需指定不同的msg.savekey保存消息内容，默认值为msg.body，后续Job可以使用该key值获取消息内容。

8. only.receive.today: 如果为true 有且只能接收job启动当天发来的消息

9. **注意：msg.type默认不可变为RECEIVE，msg.receiver、msg.topic、msg.name是必填。**
```
示例使用4.1节的eventSender的信息：
1. 配置reveive接收对应的topic信息，并通过msg.savekey进行保存
![signal02](/images/zh_CN/chapter3/manual/signal02.png)

2. 在hql节点中使用receiver的msg.savekey作为自定义变量
![signal03](/images/zh_CN/chapter3/manual/signal03.png)

3. 通过上图的运行可以知道整个流程下来hql节点读取到了eventsender发送的信息。信号节点支持跨工程和工作流，这里只是示例使用


### 4.3 dataCheck节点：
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;DataCheck节点用于检测数据是否ready，可以判断hive库中某个表或者分区是否存在，如果存在则进行下游的执行，在有数据依赖的任务中扮演十分重要的作用替换掉以前口头约定好的时间开始运行。
dataCheck支持如下参数：
```
1. source.type: 依赖的数据来源，job表示是由其他job产生

2. check.object: 依赖数据的名称例如：data.object.1=dbname.tablename{partitionlist}

3. max.check.hours: 描述任务的等待时间，单位是小时

4. job.desc: 追加多源信息配置。
```
![signal04](/images/zh_CN/chapter3/manual/signal04.png)


## 5.数据输出节点
### 5.1 SendEmail节点
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;SendEmail节点一般作为工作流的最后一个节点，用于将工作流前面的结果信息进行发送，支持发送表格、文本、DashBoard、Display、图片等，用户在使用的时候直接选择发送的对应节点就行：
sendEmail支持如下参数：
```
类型：支持节点、文字、文件、链接
邮件标题：指定邮件表提
发送项：发送的具体内容，例如：类型是节点则这里选择节点
关联审批单：该邮件是否走过审批，如果未则不会进行发送
其他邮件基本属性：收件人、抄送、秘密抄送
```
![sendemail01](/images/zh_CN/chapter3/manual/sendemail01.png)

## 6.功能节点
### 6.1 连接节点：
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Connector节点的作用是为了作为节点与节点的连接，让工作流更加好看：
![connector](/images/zh_CN/chapter3/manual/connector.png)

### 6.2 子工作流：
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Subflow节点是您可以在一条工作流中嵌入一条子工作流，子工作流支持发布调度，但是在实时执行时父工作流的subflow节点会跳过执行，需要跳到子工作流编辑页面进行执行：
![subflow01](/images/zh_CN/chapter3/manual/subflow01.png)




