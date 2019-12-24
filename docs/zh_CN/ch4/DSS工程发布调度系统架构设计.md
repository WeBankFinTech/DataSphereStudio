# DataSphere Studio发布调度系统架构设计



## 一、背景

   目前在大数据领域存在许多种批量定时调度系统，如Azkaban、Airflow、EasyScheduler等，DSS支持将设计好的DAG工作流
发布到不同的调度系统，系统默认支持了发布到Azkaban的实现。在DSS中主要完工作流的编排设计，节点的参数设置，
脚本代码编写，图表设计等需要交互式的操作，还可以在DSS中实时执行，并调试好所有节点的可执行代码。发布到调度系统后
，由调度系统根据定时任务的配置，定时调度执行。

## 二、架构设计

![发布调度架构图](../../../images/zh_CN/charpter3/publish/publichtoscheduling.png)

## 三、发布流程

（1）从数据库读取最新版本的工程、工作流信息，获取所有的保存在BML库工作流JSON文件。

（2）将上面的数据库内容，JSON文件内容分别转成DSS中的DWSProject，DWSFlow，如果存在子flow，则需要一并设置到flow中，保持原来的层级关系和依赖关系，构建好DWSProject，其中包含了工程下所有的DWSFlow。
     一个工作流JSON包含了所有节点的定义，并存储了节点之间的依赖关系，以及工作流自身的属性信息。

（3）将DWSProject经过工程转换器转成SchedulerProject，转成SchedulerProject的过程中，同时完成了DWSJSONFlow到SchedulerFlow的转换，也完成了DWSNode到SchedulerNode的转换。

（4）使用ProjectTuning对整个SchedulerProject工程进行tuning操作，用于完成工程发布前的整体调整操作，在Azkaban的实现中主要完成了工程的路径设置和工作流的存储路径设置。

（5）ProjectPublishHook操作，hook可以根据不同的调度系统进行实现，且可分为发布前的hook和发布后的hook,这些都会被统一执行。
     发布前的hook包含对工程的解析，工作流的解析，节点的解析，以及生成对应的资源文件，属性文件，节点描述文件等。这个需要根据不同的调度系统进行实现。

（6）发布工程，打包好经过转换、解析生成的工程目录文件，并上传到对应的调度系统。
