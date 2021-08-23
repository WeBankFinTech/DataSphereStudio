![DSS](images/en_US/readme/DSS_logo.png)
====

[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

[English](README.md) | 中文

## 引言

DataSphere Studio（简称DSS）是微众银行自研的数据应用开发管理集成框架。

基于插拔式的集成框架设计，及计算中间件 [**Linkis**](https://github.com/WeBankFinTech/Linkis) ，可轻松接入上层各种数据应用系统，让数据开发变得简洁又易用。

在统一的UI下，DataSphere Studio以工作流式的图形化拖拽开发体验，将满足从数据交换、脱敏清洗、分析挖掘、质量检测、可视化展现、定时调度到数据输出应用等，数据应用开发全流程场景需求。

**DSS通过插拔式的集成框架设计，让用户可以根据需要，简单快速替换DSS已集成的各种功能组件，或新增功能组件。**

借助于 [**Linkis**](https://github.com/WeBankFinTech/Linkis) 计算中间件的连接、复用与简化能力，DSS天生便具备了金融级高并发、高可用、多租户隔离和资源管控等执行与调度能力。

## 界面预览

请您耐心等待，加载gif需要一些时间。

![DSS-V1.0 GIF](images/en_US/readme/DSS_gif.gif)

## 核心特点

DSS主要特点：

### 一、一站式、全流程的应用开发管理界面

&nbsp; &nbsp; &nbsp; &nbsp;DSS集成度极高，目前已集成的系统有：
 
 &nbsp; &nbsp; &nbsp; &nbsp;1、数据开发IDE工具——[Scriptis](https://github.com/WeBankFinTech/Scriptis)
 
 &nbsp; &nbsp; &nbsp; &nbsp;2、数据可视化工具——[Visualis](https://github.com/WeBankFinTech/Visualis)（基于宜信[Davinci](https://github.com/edp963/davinci)二次开发）
 
 &nbsp; &nbsp; &nbsp; &nbsp;3、数据质量管理工具——[Qualitis](https://github.com/WeBankFinTech/Qualitis)
 
 &nbsp; &nbsp; &nbsp; &nbsp;4、工作流调度工具——[Schedulis](https://github.com/WeBankFinTech/Schedulis)
 
 &nbsp; &nbsp; &nbsp; &nbsp;5、数据交换工具——[Exchangis](https://github.com/WeBankFinTech/Exchangis) (**已支持免密跳转，等待Exchangis发版**)

 &nbsp; &nbsp; &nbsp; &nbsp;6、数据Api服务——[DataApiService]()
 
 &nbsp; &nbsp; &nbsp; &nbsp;**DSS插拔式的框架设计模式，允许用户快速替换DSS已集成的各个Web系统**。如：将 Scriptis 替换成Zeppelin，将 Schedulis 替换成DolphinScheduler。
 
![DSS一站式](images/zh_CN/readme/onestop.gif) 

### 二、基于Linkis计算中间件，打造独有的AppConn设计理念

 &nbsp; &nbsp; &nbsp; &nbsp;AppConn，是DSS可以简单快速集成各种上层Web系统的核心概念。

 &nbsp; &nbsp; &nbsp; &nbsp;AppConn——应用连接器，定义了一套统一的前后台接入协议，总共分为三级规范，可让外部数据应用系统快速简单地接入，成为DSS数据应用开发中的一环。
 
 &nbsp; &nbsp; &nbsp; &nbsp;AppConn的三级规范即：一级SSO规范，二级组织结构规范，三级开发流程规范；

 &nbsp; &nbsp; &nbsp; &nbsp;DSS通过串联多个 AppConn，编排成一条支持实时执行和定时调度的工作流，用户只需简单拖拽即可完成数据应用的全流程开发。

 &nbsp; &nbsp; &nbsp; &nbsp;由于 AppConn 对接了Linkis，外部数据应用系统因此具备了资源管控、并发限流、用户资源管理等能力，且允许上下文信息跨系统级共享，彻底告别应用孤岛。

### 三、Workspace级管理单元

 &nbsp; &nbsp; &nbsp; &nbsp;以 Workspace 为管理单元，组织和管理各数据应用系统的业务应用，定义了一套跨数据应用系统的工作空间协同开发通用标准，并提供了用户角色管理能力。

### 四、已集成的数据应用组件

 &nbsp; &nbsp; &nbsp; &nbsp;DSS通过实现多个AppConn，已集成了丰富多样的各种上层数据应用系统，基本可满足用户的数据开发需求。

 &nbsp; &nbsp; &nbsp; &nbsp;**用户如果有需要，也可以轻松集成新的数据应用系统，以替换或丰富DSS的数据应用开发流程。**

 &nbsp; &nbsp; &nbsp; &nbsp;1、DSS的调度能力——Schedulis AppConn

&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;用户的很多数据应用，通常希望具备周期性的调度能力。
                                                 
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;目前市面上已有的开源调度系统，与上层的其他数据应用系统整合度低，且难以融通。
                                                 
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;DSS通过实现Schedulis AppConn，允许用户将一个编排好的工作流，一键发布到 Schedulis 中进行定时调度。
                                                 
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;DSS还为调度系统定义了一套标准且通用的DSS工作流解析发布规范，让其他调度系统可以轻松与DSS实现低成本对接。
                                                 
![Azkaban](images/zh_CN/readme/Azkaban_AppConn.gif)

 &nbsp; &nbsp; &nbsp; &nbsp;2、数据开发——Scriptis AppConn

&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;什么是[Scriptis](https://github.com/WeBankFinTech/Scriptis)?
                                                 
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;Scriptis是一款支持在线写SQL、Pyspark、HiveQL等脚本，提交给[Linkis](https://github.com/WeBankFinTech/Linkis)执行的数据分析Web工具，且支持UDF、函数、资源管控和智能诊断等企业级特性。
                                                
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;Scriptis AppConn为DSS集成了Scriptis的数据开发能力，并允许Scriptis的各种脚本类型，作为DSS工作流的节点，参与到应用开发的流程中。
                                                
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;目前已支持HiveSQL、SparkSQL、Pyspark、Scala等脚本节点类型。
                                                
![Scriptis](images/zh_CN/readme/Scriptis_AppConn.gif)

 &nbsp; &nbsp; &nbsp; &nbsp;3、数据可视化——Visualis AppConn

&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;什么是Visualis?
                                                 
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;Visualis是一个基于宜信开源项目Davinci二次开发的数据可视化BI工具，为用户在数据安全和权限方面，提供金融级数据可视化能力。
                                                
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;Visualis AppConn为DSS集成了Visualis的数据可视化能力，并允许数据大屏和仪表盘，作为DSS工作流的节点，与上游的数据集市关联起来。
                                                
![Visualis](images/zh_CN/readme/Visualis_AppConn.gif)

 &nbsp; &nbsp; &nbsp; &nbsp;4、数据质量——Qualitis AppConn

&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;Qualitis AppConn 为DSS集成数据质量校验能力，将数据质量系统集成到DSS工作流开发中，对数据完整性、正确性等进行校验。
                                                
![Qualitis](images/zh_CN/readme/Qualitis_AppConn.gif)

 &nbsp; &nbsp; &nbsp; &nbsp;5、数据发送——Sender AppConn

&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;Sender AppConn为DSS集成数据发送能力，目前支持SendEmail节点类型，所有其他节点的结果集，都可以通过邮件发送。
                                                 
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;例如：SendEmail节点可直接将Display数据大屏作为邮件发送出来。
  
 &nbsp; &nbsp; &nbsp; &nbsp;6、信号节点——Signal AppConn

&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;EventChecker AppConn用于强化业务与流程之间的解耦和相互关联。
                                                
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;DataChecker节点：检查库表分区是否存在。
                                                    
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;EventSender: 跨工作流和工程的消息发送节点。
                                                 
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;EventReceiver: 跨工作流和工程的消息接收节点。
   
 &nbsp; &nbsp; &nbsp; &nbsp;7、功能节点
   
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;空节点、子工作流节点。

 &nbsp; &nbsp; &nbsp; &nbsp;8、**节点扩展**
 
 &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;**根据需要，用户可以简单快速替换DSS已集成的各种功能组件，或新增功能组件。**

## 已集成应用工具

| 应用工具     | 描述                                                          | DSS0.X 版本要求                                                           | DSS1.0 版本要求    | 版本规划 |
| --------------- | -------------------------------------------------------------------- | --------------------------------------------------------------------- | ---------- | ------ |
| **ApiService**  | 数据API服务。可快速将脚本发布为一个Restful接口，提供访问能力                                  | 不支持 | >=1.0.0 | 已发布 |
| **Airflow**     | 支持将DSS工作流发布到Airflow进行定时调度                                            | >=0.9.1，尚未合并 | on going | [待规划]() |
| **Streamis**  | 流式应用平台。支持发布Flink Jar 和 Flink SQL ，提供流式应用的开发调试和生产管理能力，如：启停、状态监控、checkpoint等。 | 不支持 | >=1.0.0 | [即将发布]() |
| **UserManager** | 自动初始化一个DSS新用户所必须的所有用户环境，包含：创建Linux用户、各种用户路径、目录授权等                    |  >=0.9.1 | on going | 待规划 |
| **EventCheck**  | 提供跨业务、跨工程和跨工作流的信号发送能力。 | >=0.5.0 | >=1.0.0 | 已发布      |
| **SendEmail**   | 提供数据发送能力，所有其他节点的结果集，都可以通过邮件发送 | >=0.5.0 | >=1.0.0 | 已发布  |
| [**Scriptis**](https://github.com/WeBankFinTech/Scriptis)   | 支持在线写SQL、Pyspark、HiveQL等脚本，提交给[Linkis](https://github.com/WeBankFinTech/Linkis)执行的数据分析Web工具。 | >=0.5.0 | >=1.0.0 | 已发布      |
| [**Visualis**](https://github.com/WeBankFinTech/Visualis)   | 基于宜信开源项目Davinci二次开发的数据可视化BI工具，为用户在数据安全方面提供金融级数据可视化能力。 | >=0.5.0 | >=1.0.0 | 已发布      |
| [**Qualitis**](https://github.com/WeBankFinTech/Qualitis)   | 数据质量校验工具，提供数据完整性、正确性等数据校验能力 | >=0.5.0 | >=1.0.0 |  [待发布]()      |
| [**Schedulis**](https://github.com/WeBankFinTech/Schedulis) | 基于Azkaban二次开发的工作流任务调度系统,具备高性能，高可用和多租户资源隔离等金融级特性。 | >=0.5.0 | >=1.0.0 | 已发布      |
| [**Exchangis**](https://github.com/WeBankFinTech/Exchangis) | 支持对结构化及无结构化的异构数据源之间的数据传输的数据交换平台 | 不支持 | >=1.0.0 | [待发布]()      |


## Demo试用环境

&nbsp; &nbsp; &nbsp; &nbsp;由于 DataSphereStudio 支持执行脚本风险较高，WeDataSphere Demo环境的隔离没有做完，考虑到大家都在咨询Demo环境，决定向社区先定向发放邀请码，接受企业和组织的试用申请。

&nbsp; &nbsp; &nbsp; &nbsp;如果您想试用Demo环境，请加入DataSphere Studio社区用户群（**加群方式请翻到本文档末尾处**），联系团队成员获取邀请码。

&nbsp; &nbsp; &nbsp; &nbsp;DataSphereStudio Demo环境用户注册页面：[点我进入](https://www.ozone.space/wds/dss/#/register)

&nbsp; &nbsp; &nbsp; &nbsp;DataSphereStudio Demo环境登录页面：[点我进入](https://www.ozone.space/wds/dss/#/login)

&nbsp; &nbsp; &nbsp; &nbsp;**DataSphereStudio1.0 Demo环境将在近期开放，敬请期待**。

## 下载

 &nbsp; &nbsp; &nbsp; &nbsp;请前往 [DSS releases](https://github.com/WeBankFinTech/DataSphereStudio/releases) 页面下载 DSS 的已编译版本或源码包。

## 编译和安装部署

请参照 [编译指引](https://github.com/WeBankFinTech/DataSphereStudio-Doc/blob/main/zh_CN/%E5%BC%80%E5%8F%91%E6%96%87%E6%A1%A3/DSS%E7%BC%96%E8%AF%91%E6%96%87%E6%A1%A3.md) 来编译 DSS 源码。

请参考 [安装部署文档](https://github.com/WeBankFinTech/DataSphereStudio-Doc/blob/main/zh_CN/%E5%AE%89%E8%A3%85%E9%83%A8%E7%BD%B2/DSS%E5%8D%95%E6%9C%BA%E9%83%A8%E7%BD%B2%E6%96%87%E6%A1%A3.md) 来部署 DSS。

## 示例和使用指引

请到 [用户使用文档](https://github.com/WeBankFinTech/DataSphereStudio-Doc/blob/main/zh_CN/%E4%BD%BF%E7%94%A8%E6%96%87%E6%A1%A3/DSS%E7%94%A8%E6%88%B7%E6%89%8B%E5%86%8C.md) ，了解如何快速使用DSS。

## 文档

DSS1.0的完整文档列表，请参见 [DSS-Doc](https://github.com/WeBankFinTech/DataSphereStudio-Doc/tree/main/zh_CN)

以下为 DSS 相关 AppConn 插件的安装指南：

- [DSS的Visualis AppConn插件安装指南](https://github.com/WeBankFinTech/DataSphereStudio-Doc/blob/main/zh_CN/%E5%AE%89%E8%A3%85%E9%83%A8%E7%BD%B2/VisualisAppConn%E6%8F%92%E4%BB%B6%E5%AE%89%E8%A3%85%E6%96%87%E6%A1%A3.md)

- [DSS的Schedulis AppConn插件安装指南](https://github.com/WeBankFinTech/DataSphereStudio-Doc/blob/main/zh_CN/%E5%AE%89%E8%A3%85%E9%83%A8%E7%BD%B2/SchedulisAppConn%E6%8F%92%E4%BB%B6%E5%AE%89%E8%A3%85%E6%96%87%E6%A1%A3.md)

- [DSS的Qualitis AppConn插件安装指南](https://github.com/WeBankFinTech/DataSphereStudio-Doc/blob/main/zh_CN/%E5%AE%89%E8%A3%85%E9%83%A8%E7%BD%B2/QualitisAppConn%E6%8F%92%E4%BB%B6%E5%AE%89%E8%A3%85%E6%96%87%E6%A1%A3.md)

- [DSS的Exchangis AppConn插件安装指南](https://github.com/WeBankFinTech/DataSphereStudio-Doc/blob/main/zh_CN/%E5%AE%89%E8%A3%85%E9%83%A8%E7%BD%B2/ExchangisAppConn%E6%8F%92%E4%BB%B6%E5%AE%89%E8%A3%85%E6%96%87%E6%A1%A3.md)


## 架构

![DSS架构](images/zh_CN/readme/architecture.png)


## 使用场景

 &nbsp; &nbsp; &nbsp; &nbsp;DataSphere Studio适用于以下场景：

 &nbsp; &nbsp; &nbsp; &nbsp;1. 正在筹建或初步具备大数据平台能力，但无任何数据应用工具的场景。

 &nbsp; &nbsp; &nbsp; &nbsp;2. 已具备大数据基础平台能力，且仅有少数数据应用工具的场景。

 &nbsp; &nbsp; &nbsp; &nbsp;3. 已具备大数据基础平台能力，且拥有全部数据应用工具，但工具间尚未打通，用户使用隔离感强、学习成本高的场景。

 &nbsp; &nbsp; &nbsp; &nbsp;4. 已具备大数据基础平台能力，且拥有全部数据应用工具，部分工具已实现对接，但尚未定义统一规范的场景。


## 贡献

我们非常欢迎和期待更多的贡献者参与共建 DSS, 不论是代码、文档，或是其他能够帮助到社区的贡献形式。

## 联系我们

对 DSS 的任何问题和建议，敬请提交issue，以便跟踪处理和经验沉淀共享。

您也可以扫描下面的二维码，加入我们的微信/QQ群，以获得更快速的响应。

![交流](images/zh_CN/readme/communication.png)

## 谁在使用 DSS

我们创建了 [Who is using DSS](https://github.com/WeBankFinTech/DataSphereStudio/issues/1) issue 以便用户反馈和记录谁在使用 DSS，欢迎您注册登记.

DSS 自2019年开源发布以来，累计已有700多家试验企业和1000+沙盒试验用户，涉及金融、电信、制造、互联网等多个行业。

## License

DSS is under the Apache 2.0 license. See the [License](LICENSE) file for details.
