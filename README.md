![DSS](images/en_US/readme/DSS_logo.png)
====

[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

English | [中文](README-ZH.md)

## Introduction

 &nbsp; &nbsp; &nbsp; &nbsp;DataSphere Studio (DSS for short) is WeDataSphere, a one-stop data application development management portal developed by WeBank.

 &nbsp; &nbsp; &nbsp; &nbsp;With the pluggable integrated framework design and the Linkis, a computing middleware, DSS can easily integrate  various upper-layer data application systems, making data development simple and easy to use.

 &nbsp; &nbsp; &nbsp; &nbsp;DataSphere Studio is positioned as a data application development portal, and the closed loop covers the entire process of data application development. With a unified UI, the workflow-like graphical drag-and-drop development experience meets the entire lifecycle of data application development from data import, desensitization cleaning, data analysis, data mining, quality inspection, visualization, scheduling to data output applications, etc.

 &nbsp; &nbsp; &nbsp; &nbsp;With the connection, reusability, and simplification capabilities of Linkis, DSS is born with financial-grade capabilities of high concurrency, high availability, multi-tenant isolation, and resource management.

## UI preview

 &nbsp; &nbsp; &nbsp; &nbsp;Please be patient, it will take some time to load gif.

![DSS-V1.0 GIF](images/en_US/readme/DSS_gif.gif)

## Core features

### 1. One-stop, full-process application development management UI

 &nbsp; &nbsp; &nbsp; &nbsp;DSS is highly integrated. Currently integrated components include(**DSS version compatibility for the above components, please visit: [Compatibility list of integrated components](README.md#4-integrated-data-application-components)**):

 &nbsp; &nbsp; &nbsp; &nbsp;1. Data Development IDE Tool - [Scriptis](https://github.com/WeBankFinTech/Scriptis)

 &nbsp; &nbsp; &nbsp; &nbsp;2. Data Visualization Tool - [Visualis](https://github.com/WeBankFinTech/Visualis) (Based on the open source project [Davinci](https://github.com/edp963/davinci ) contributed by CreditEase)

 &nbsp; &nbsp; &nbsp; &nbsp;3. Data Quality Management Tool - [Qualitis](https://github.com/WeBankFinTech/Qualitis)

 &nbsp; &nbsp; &nbsp; &nbsp;4. Workflow scheduling tool - [Schedulis](https://github.com/WeBankFinTech/Schedulis)

 &nbsp; &nbsp; &nbsp; &nbsp;5. Data Exchange Tool - [Exchangis](https://github.com/WeBankFinTech/Exchangis) 

 &nbsp; &nbsp; &nbsp; &nbsp;6. Data Api Service - [DataApiService](https://github.com/WeBankFinTech/DataSphereStudio-Doc/blob/main/en_US/Using_Document/DataApiService_Usage_Documentation.md)

 &nbsp; &nbsp; &nbsp; &nbsp;7. Streaming Application Development Management Tool - [Streamis](https://github.com/WeBankFinTech/Streamis)

 &nbsp; &nbsp; &nbsp; &nbsp;8. One-stop machine Learning Platform - [Prophecis](https://github.com/WeBankFinTech/Prophecis)

 &nbsp; &nbsp; &nbsp; &nbsp;9. Workflow Task Scheduling Tool - DolphinScheduler (**In Code Merging**)
 
 &nbsp; &nbsp; &nbsp; &nbsp;10. Help documentation and beginner's guide - UserGuide (**In Code Merging**)

 &nbsp; &nbsp; &nbsp; &nbsp;11. Data Model Center - DataModelCenter (**In development**)
 
 &nbsp; &nbsp; &nbsp; &nbsp;**DSS version compatibility for the above components, please visit: [Compatibility list of integrated components](README.md#4-integrated-data-application-components)**.

 &nbsp; &nbsp; &nbsp; &nbsp;With a pluggable framework architecture, DSS is designed to allow users to quickly integrate new data application tools, or replace various tools that DSS has integrated. For example, replace Scriptis with Zeppelin, and replace Schedulis with DolphinScheduler...

![DSS one-stop video](images/en_US/readme/onestop.gif) 

### 2. AppConn, based on Linkis，defines a unique design concept

 &nbsp; &nbsp; &nbsp; &nbsp;AppConn is the core concept that enables DSS to easily and quickly integrate various upper-layer web systems.

 &nbsp; &nbsp; &nbsp; &nbsp;AppConn, an application connector, defines a set of unified front-end and back-end three-level integration protocols, allowing external data application systems to easily and quickly becoming a part of DSS data application development. 

 &nbsp; &nbsp; &nbsp; &nbsp;The three-level specifications of AppConn are: the first-level SSO specification, the second-level organizational structure specification, and the third-level development process specification.

 &nbsp; &nbsp; &nbsp; &nbsp;DSS arranges multiple AppConns in series to form a workflow that supports real-time execution and scheduled execution. Users can complete the entire process development of data applications with simple drag and drop operations.

 &nbsp; &nbsp; &nbsp; &nbsp;Since AppConn is integrated with Linkis, the external data application system shares the capabilities of resource management, concurrent limiting, and high performance. AppConn also allows sharable context across system level and thus makes external data application completely gets away from application silos.

### 3. Workspace, as the management unit

 &nbsp; &nbsp; &nbsp; &nbsp;With Workspace as the management unit, it organizes and manages business applications of various data application systems, defines a set of common standards for collaborative development of workspaces across data application systems, and provides user role management capabilities.

### 4. Integrated data application components

 &nbsp; &nbsp; &nbsp; &nbsp;DSS has integrated a variety of upper-layer data application systems by implementing multiple AppConns, which can basically meet the data development needs of users.

 &nbsp; &nbsp; &nbsp; &nbsp;**If desired, new data application systems can also be easily integrated to replace or enrich DSS's data application development process.** [Click me to learn how to quickly integrate new application systems](https://github.com/WeBankFinTech/DataSphereStudio-Doc/blob/main/en_US/Development_Documentation/Third-party_System_Access_Development_Guide.md)

| Component | Description | DSS0.X Compatibility(recommend DSS0.9.1) | DSS1.0 Compatibility(recommend DSS1.0.1) |
| --------------- | -------------------------------------------------------------------- | --------- | ---------- |
| [**Linkis**](https://github.com/apache/incubator-linkis) | Apache Linkis, builds a layer of computation middleware, by using standard interfaces such as REST/WS/JDBC provided by Linkis, the upper applications can easily access the underlying engines such as MySQL/Spark/Hive/Presto/Flink, etc.  | recommend Linkis0.11.0 (**Released**) | >= Linkis1.1.1 (**Released**)|
| [**DataApiService**](https://github.com/WeBankFinTech/DataSphereStudio-Doc/blob/main/en_US/Using_Document/DataApiService_Usage_Documentation.md) | (Third-party applications built into DSS) Data API service. The SQL script can be quickly published as a Restful interface, providing Rest access capability to the outside world. | Not supported | recommend DSS1.0.1 (**Released**) |
| [**Scriptis**](https://github.com/WeBankFinTech/DataSphereStudio) | (Third-party applications built into DSS) Support online script writing such as SQL, Pyspark, HiveQL, etc., submit to [Linkis](https://github.com/WeBankFinTech/Linkis ) to perform data analysis web tools. | recommend DSS0.9.1 (**Released**) | recommend DSS1.0.1 (**Released**) |
| [**Schedulis**](https://github.com/WeBankFinTech/Schedulis) | Workflow task scheduling system based on Azkaban secondary development, with financial-grade features such as high performance, high availability and multi-tenant resource isolation. | recommend Schedulis0.6.1 (**Released**) | >= Schedulis0.6.2 (**Released**) |
| **EventCheck** | (Third-party applications built into DSS) Provides cross-business, cross-engineering, and cross-workflow signaling capabilities. | recommend DSS0.9.1 (**Released**) | recommend DSS1.0.1 (**Released**) |
| **SendEmail** | (Third-party applications built into DSS) Provides the ability to send data, all the result sets of other workflow nodes can be sent by email | recommend 0.9.1 (**Released**) | recommend 1.0.1 (**Released**) |
| [**Qualitis**](https://github.com/WeBankFinTech/Qualitis) | Data quality verification tool, providing data verification capabilities such as data integrity and correctness | recommend Qualitis0.8.0 (**Released**) | >= Qualitis0.9.1 (**Released**) |
| [**Streamis**](https://github.com/WeBankFinTech/Streamis) | Streaming application development management tool. It supports the release of Flink Jar and Flink SQL, and provides the development, debugging and production management capabilities of streaming applications, such as: start-stop, status monitoring, checkpoint, etc. | Not supported | >= Streamis0.1.0 (**Released**) |
| [**Prophecis**](https://github.com/WeBankFinTech/Prophecis) | A one-stop machine learning platform that integrates multiple open source machine learning frameworks. Prophecis' MLFlow can be connected to DSS workflow through AppConn. | Not supported | >= Prophecis 0.3.0 (**Released**) |
| [**Exchangis**](https://github.com/WeBankFinTech/Exchangis) | A data exchange platform that supports data transmission between structured and unstructured heterogeneous data sources, the upcoming Exchangis1. 0, will be connected with DSS workflow | not supported | = Exchangis1.0.0-RC1 (**Released**) |
| [**Visualis**](https://github.com/WeBankFinTech/Visualis) | A data visualization BI tool based on the second development of Davinci, an open source project of CreditEase, provides users with financial-level data visualization capabilities in terms of data security. | recommend Visualis0.5.0 (**Released**) | = Visualis1.0.0-RC1 (**Released**) |
| [**DolphinScheduler**](https://github.com/apache/dolphinscheduler) | Apache DolphinScheduler, a distributed and scalable visual workflow task scheduling platform, supports one-click publishing of DSS workflows to DolphinScheduler. | Not supported | >= DolphinScheduler1.3.6, planned in DSS1.1.0 (**Developing**) |
| **UserGuide**     | (Third-party applications to be built into DSS) It mainly provides help documentation, beginner's guide, Dark mode skinning, etc.      | Not supported | Planning in DSS1.1.0 (**Developing**) |
| **DataModelCenter** | (Third-party applications to be built into DSS) It mainly provides the capabilities of data warehouse planning, data model development and data asset management. Data warehouse planning includes subject domains, data warehouse layers, modifiers, etc.; data model development includes indicators, dimensions, metrics, wizard-based table building, etc.; data assets are connected to Apache Atlas to provide data lineage capabilities. | Not supported | Planning in DSS1.2.0 (**Developing**) |
| **UserManager** | (Third-party applications built into DSS) Automatically initialize all user environments necessary for a new DSS user, including: creating Linux users, various user paths, directory authorization, etc. | recommend DSS0.9.1 (**Released**) | not supported |
| [**Airflow**](https://github.com/apache/airflow) | Supports publishing DSS workflows to Apache Airflow for scheduling. | recommend DSS0.9.1, not yet merged | Not supported |


## Demo Trial environment

 &nbsp; &nbsp; &nbsp; &nbsp;The function of DataSphere Studio supporting script execution has high security risks, and the isolation of the WeDataSphere Demo environment has not been completed. Considering that many users are inquiring about the Demo environment, we decided to first issue invitation codes to the community and accept trial applications from enterprises and organizations.

 &nbsp; &nbsp; &nbsp; &nbsp;If you want to try out the Demo environment, please join the DataSphere Studio community user group (**Please refer to the end of the document**), and contact **WeDataSphere Group Robot** to get an invitation code.

 &nbsp; &nbsp; &nbsp; &nbsp;DataSphereStudio Demo environment user registration page: [click me to enter](https://dss-open.wedatasphere.com/#/register)

 &nbsp; &nbsp; &nbsp; &nbsp;DataSphereStudio Demo environment login page: [click me to enter](https://dss-open.wedatasphere.com/#/login)

##  Download

 &nbsp; &nbsp; &nbsp; &nbsp;Please go to the [DSS Releases Page](https://github.com/WeBankFinTech/DataSphereStudio/releases) to download a compiled version or a source code package of DSS.

## Compile and deploy

 &nbsp; &nbsp; &nbsp; &nbsp;Please follow [Compile Guide](https://github.com/WeBankFinTech/DataSphereStudio-Doc/blob/main/en_US/Development_Documentation/Compilation_Documentation.md) to compile DSS from source code.

 &nbsp; &nbsp; &nbsp; &nbsp;Please refer to [Deployment Documents](https://github.com/WeBankFinTech/DataSphereStudio-Doc/blob/main/en_US/Installation_and_Deployment/DSS_Single-Server_Deployment_Documentation.md) to do the deployment.

## Examples and Guidance

 &nbsp; &nbsp; &nbsp; &nbsp;You can find examples and guidance for how to use DSS in [User Manual](https://github.com/WeBankFinTech/DataSphereStudio-Doc/blob/main/en_US/Using_Document/DSS_User_Manual.md).


## Documents

 &nbsp; &nbsp; &nbsp; &nbsp;For a complete list of documents for DSS1.0, see [DSS-Doc](https://github.com/WeBankFinTech/DataSphereStudio-Doc)

 &nbsp; &nbsp; &nbsp; &nbsp;The following is the installation guide for DSS-related AppConn plugins:

- [Visualis AppConn Plugin Installation Guide](https://github.com/WeBankFinTech/DataSphereStudio-Doc/blob/main/en_US/Installation_and_Deployment/VisualisAppConn_Plugin_Installation_Documentation.md)

- [Schedulis AppConn Plugin Installation Guide](https://github.com/WeBankFinTech/DataSphereStudio-Doc/blob/main/en_US/Installation_and_Deployment/SchedulisAppConn_Plugin_Installation_Documentation.md)

- [Qualitis AppConn Plugin Installation Guide](https://github.com/WeBankFinTech/DataSphereStudio-Doc/blob/main/en_US/Installation_and_Deployment/QualitisAppConn_Plugin_Installation_Documentation.md)

- [Exchangis AppConn Plugin Installation Guide](https://github.com/WeBankFinTech/DataSphereStudio-Doc/blob/main/en_US/Installation_and_Deployment/ExchangisAppConn_Plugin_Installation_Documentation.md)

## Architecture

![DSS Architecture](images/en_US/readme/architecture.png)

## Usage Scenarios

 &nbsp; &nbsp;&nbsp; &nbsp;DataSphere Studio is suitable for the following scenarios:

 &nbsp; &nbsp;&nbsp; &nbsp;1. Scenarios in which big data platform capability is being prepared or initialized but no data application tools are available.

 &nbsp; &nbsp;&nbsp; &nbsp;2. Scenarios in which users already have big data foundation platform capabilities but with only a few data application tools.

 &nbsp; &nbsp;&nbsp; &nbsp;3. Scenarios in which users have the ability of big data foundation platform and comprehensive data application tools, but suffers strong isolation and and high learning costs because those tools have not been integrated together.

 &nbsp; &nbsp;&nbsp; &nbsp;4. Scenarios in which users have the capabilities of big data foundation platform and comprehensive data application tools. but lacks unified and standardized specifications, while a part of these tools have been integrated.

## Contributing

 &nbsp; &nbsp; &nbsp; &nbsp;Contributions are always welcomed, we need more contributors to build DSS together. either code, or doc, or other supports that could help the community.

 &nbsp; &nbsp; &nbsp; &nbsp;For code and documentation contributions, please follow the contribution guide.

## Communication

 &nbsp; &nbsp; &nbsp; &nbsp;For any questions or suggestions, please kindly submit an issue.

 &nbsp; &nbsp; &nbsp; &nbsp;You can scan the QR code below to join our WeChat and QQ group to get more immediate response.

![communication](images/en_US/readme/communication.png)

## Who is using DSS

 &nbsp; &nbsp; &nbsp; &nbsp;We opened an issue for users to feedback and record who is using DSS.

 &nbsp; &nbsp; &nbsp; &nbsp;Since the first release of DSS in 2019, it has accumulated more than 700 trial companies and 1000+ sandbox trial users, which involving diverse industries, from finance, banking, tele-communication, to manufactory, internet companies and so on.

## License

 &nbsp; &nbsp; &nbsp; &nbsp;DSS is under the Apache 2.0 license. See the [License](LICENSE) file for details.
