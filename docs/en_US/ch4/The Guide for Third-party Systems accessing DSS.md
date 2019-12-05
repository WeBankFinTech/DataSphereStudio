### The Guide for Third-Party System Accessing DSS
#### 1. Introduction
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;This guide is used to introduce the design scheme, specific implementation and  a concrete examples of third-party system access to DSS
system.
This guide is suitable for users who want to connect their third-party system to DSS.

#### 2. Design scheme for third-party system accessing DSS
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;DSS (DataSphere Studio) has been designed to be an open,
highly scalable system from the beginning.
We hope that the third-party system is able to access DSS via a pluggable way. In order to realize the above ideas,
DSS has proposed the concept of AppJoint (application joint). AppJoint is used to connect two systems and provide necessary services
for the coordinated operation of the two systems.<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;We must consider and implement the following functions when a task is
submitted to the DSS system and forwarded by the DSS system to a third-party system for execution.

- 1). Solve the authentication problem of users on both systems.
- 2). Both systems need to process the metadata submitted by the user correctly
- 3). The DSS system needs to submit tasks to a third-party system for execution correctly in a synchronous or asynchronous manner
- 4). After submitting the task to the third-party system, the third-parts system needs to be able to feed back
 information such as logs and status to the DSS system.
- 5). When the task execution is completed,
the third-party system is able to persist the task results and other information in the specific file path.

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;In order to facilitate the access of third-party systems,
DSS provides the SDK method and maven dependency is as follows.

```
<dependency>
    <groupId>com.webank.wedatasphere.dss</groupId>
    <artifactId>dss-appjoint-core</artifactId>
    <version>${dss.version}</version>
</dependency>
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The top-level interface of AppJoint is provided by the dss-appjoint-core module.
A Third-party system which wannas access the DSS system needs to implement the top-level interface.
This interface has the following methods that users need to implement.

- 1.getSecurityService

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;SecurityService is used to solve the authentication problem between
DSS system and third-party systems. When a user logins to the DSS system and wants to submit a task to a third-party system,
the third-party system needs to be able to authenticate this user at first.

- 2.getProjectSerivice

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;ProjectService is used to solve the problems of project management between DSS
system and third-party systems.
When a user adds, deletes, or modifies a project in the DSS system, the third-party system also needs to do the same
actions synchronously. This is to make projects unified.

- 3.getNodeService

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;NodeService is used to solve the problem which is when a user submits a task
in DSS, third-party system needs to generate corresponding tasks at the same time.
For example, If a user adds a new workflow node and edits it, the third-party system needs to be aware of this action and
generates a corresponding task.



- 4.getNodeExecution

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;NodeExecution is an interface used to submit
tasks to the third-party system for execution. There are two differnt types of NodeExecution. One is NodeExecution whtich
supports short-term tasks and the other one is LongTermNodeExecution which supports long-term tasks. For example,
for short-term tasks, such as email sending, you can directly implement the NodeExecution interface and rewrite
 the execute method and the DSS system will wait for the task to finish. For the other long-term tasks,
  such as data quality detection, you can implement the LongTermNodeExecution interface and rewrite the submit method and this method needs to return
  a NodeExecutionAction. The DSS system can obtain task logs, statuses, etc. from third-party systems via this NodeExecutionAction.


#### 3. Implementation of Third Party System Access DSS (Take Visualis as an example)

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;DSS can be integrated with Visualis to obtain data visualization capabilities. The code of Visualis accessing the DSS system has been synchronized and open sourced in the DSS project. The following will take the open source code as an example to list and analyze the steps.
The steps for Visualis to access the DSS system are as follows:

**3.1. Implementation of AppJoint interface in Visualis**

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The implementation class of the AppJoint interface implemented by Visualis is VisualisAppjoint.
According to the code of VisualisAppjoint, it can be seen that in the `init` method,
VisualisAppjoint initializes the SecurityService, NodeService, and NodeExecution.

**3.2. Implementation of SecureService interface in Visualis**

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The class name of the SecurityService interface implemented by Visualis
is VisualisSecurityService. We can see the login method is overrided. In order to achieve login authorization,
Visualis uses a way of providing a token, and the DSS gateway authorizes the token so that the problem of login authorization
can be resolved;

**3.3. Implementation of NodeService interface in Visualis**

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The class of the NodeService interface implemented by Visualis is VisualisNodeService,
and it overrides the three methods which are `createNode`, `deleteNode`, and `updateNode`. These three methods
are to synchronize task metadata on the third-party system.
For example, the `createNode` method generates a Visualis task under the same project
in the Visualis system by sending a HTTP request to visualis to do the corresponding things.

**3.4. Implementation of NodeExecution interface in Visualis**

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The class of the NodeExecution interface implemented by Visualis is VisualisNodeExecution. It overrides the `execute` method.
The two parameters passed in to this method are Node and NodeContext. From the NodeContext, we can get
the gateway address of DSS and authrized tokens.  Via these, we can encapsulate an HTTP request to
send to Visualis and get the response results from it.
The NodeContext provides a method for writing the result set.
For example, the result set of Visualis is usually displayed in the form of pictures.
So we can obtain a PictureResultSetWriter which supports persists picture result and write the result set in the proper path.

**3.5. Database Operation (dss-application module)**

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;There is a dss-application module in the DSS system. This module is to be able to store some basic parameters
of the third-party system, so that the microservices in the DSS system can easily obtain the information of the third-party
system.
For example, when DSS needs to submit a request to Visualis,
it needs to know the URL of Visualis' request.
Therefore, it is necessary to insert the corresponding data into two tables `dss_application` and `dss_workflow_node`
in the dss-applition module. The table fields of dss_application are as follows.

|  name | meaning |  remark |
| ------------ | ------------ | ------------ |
| id  | primary key|  auto increment |
| name  | name of system |  such as Visualis |
| url  | url of system  |  such as http://127.0.0.1:8080 |
| is_user_need_init  |  needed to init |  default false |
| user_init_url  | inited url  |  defualt null |
| exists_project_service  | if exists project service  |   |
| enhance_json  | enhanced json which will be passed to appjoint while inited  |   |
| homepage_url  | homepage url  |   |
| direct_url  | redirect url  |   |

|  name | meaning |  remark |
| ------------ | ------------ | ------------ |
| id  | primary key|  auto increment |
| node_type  | task type  |  such as linkis.appjoint.visualis.display和linkis.appjoint.visualis.dashboard can be executed in Visualis |
| url  | url of system  |  such as http://127.0.0.1:8080 |
| application_id  | foreigh key of id in dss_application  | not null  |
| support_jump  | can support jump  |  1 is yes, 0 is no |
| enable_copy  | can support copy  |  1 is yes, 0 is no |
| jump_url  | jump url of node  | such as http://127.0.0.1:8080  |

<br>

![dss_application Table Demo](/images/zh_CN/chapter4/dss_application.png)<br>

Figure3-1 dss_application table demo

![dss_application Table Demo](/images/zh_CN/chapter4/dss_workflow_node.png)<br>

Figure3-2 dss_workflow_node table demo<br>

Figure 3-1 and Figure 3-2 are test data in the dss_application table 
and dss_workflow_node table of visualis. You can insert the proper data that your 
system needs to specify into the corresponding table.


**3.6. Front-end modification**

- 3.6.1 Add Node Type
Modify the src / js / service / nodeType.js file to increase the Visualis node type

- 3.6.2 Add node icon
Copy the node icon to the src / js / module / process / images / path. Currently, only the SVG format is supported.

- 3.6.3 New Node Configuration
Modify the src / js / module / process / shape.js file to add Visualis's node configuration information.

- 3.6.4 Modify the Home Click Node Event
Modify the src / js / module / process / index.vue file to add node click events and processing logic for click events.

- 3.6.5 Modify double click event of workflow node
Modify src / js / view / process / index.vue and src / js / module / process / index.vue, add node double-click events and processing logic for double-click events.

** 3.7. Compile and package into a jar package and place it in the specified location **

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;After implementing the above interfaces, an AppJoint is comleted. After packaging using mvn or gradle,
it needs to be placed in the specified location. The jar package needs to be placed in directory `dss-appjoints`.The hierarchy of directory is shown in Figure 3-3.

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Create a new visualis directory under the dss-appjoints directory.
The `visualis` directory requires a lib directory.

![appjoints dir demo](/images/zh_CN/chapter4/appjoints.png)<br>
Figure3-3 appjoints dir demo

The lib directory stores the jar package compiled by visualis to implement VisualisAppJoint. Of course,
if there is a jar package that is not included in the dss system , it needs to be placed in the lib directory.
In addition, some configuration parameters required by this AppJoint can be
written in appjoints.properties. The AppJointLoader provided by the DSS system will read these configuration parameters into
a Map and pass them in when AppJoint calls the init method.
