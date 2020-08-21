-- ----------------------------
-- Table structure for dss_workspace
-- ----------------------------
DROP TABLE IF EXISTS `dss_workspace`;
CREATE TABLE `dss_workspace` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `label` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `department` varchar(255) DEFAULT NULL,
  `product` varchar(255) DEFAULT NULL,
  `source` varchar(255) DEFAULT NULL,
  `create_by` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `last_update_time` datetime DEFAULT NULL,
  `last_update_user` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for dss_onestop_menu
-- ----------------------------
DROP TABLE IF EXISTS `dss_onestop_menu`;
CREATE TABLE `dss_onestop_menu` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) DEFAULT NULL,
  `title_en` varchar(64) DEFAULT NULL,
  `title_cn` varchar(64) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT 1,
  `icon` varchar(255) DEFAULT NULL,
  `order` int(2) DEFAULT NULL,
  `create_by` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `last_update_time` datetime DEFAULT NULL,
  `last_update_user` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for dss_onestop_menu_application
-- ----------------------------
DROP TABLE IF EXISTS `dss_onestop_menu_application`;
CREATE TABLE `dss_onestop_menu_application` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `application_id` int(20) DEFAULT NULL,
  `onestop_menu_id` int(20) NOT NULL,
  `title_en` varchar(64) DEFAULT NULL,
  `title_cn` varchar(64) DEFAULT NULL,
  `desc_en` varchar(255) DEFAULT NULL,
  `desc_cn` varchar(255) DEFAULT NULL,
  `labels_en` varchar(255) DEFAULT NULL,
  `labels_cn` varchar(255) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT NULL,
  `access_button_en` varchar(64) DEFAULT NULL,
  `access_button_cn` varchar(64) DEFAULT NULL,
  `manual_button_en` varchar(64) DEFAULT NULL,
  `manual_button_cn` varchar(64) DEFAULT NULL,
  `manual_button_url` varchar(255) DEFAULT NULL,
  `icon` varchar(255) DEFAULT NULL,
  `order` int(2) DEFAULT NULL,
  `create_by` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `last_update_time` datetime DEFAULT NULL,
  `last_update_user` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for dss_onestop_user_favorites
-- ----------------------------
DROP TABLE IF EXISTS `dss_onestop_user_favorites`;
CREATE TABLE `dss_onestop_user_favorites` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(64) DEFAULT NULL,
  `workspace_id` bigint(20) DEFAULT 1,
  `menu_application_id` int(20) DEFAULT NULL,
  `order` int(2) DEFAULT NULL,
  `create_by` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `last_update_time` datetime DEFAULT NULL,
  `last_update_user` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for dss_homepage_demo_menu
-- ----------------------------
DROP TABLE IF EXISTS `dss_homepage_demo_menu`;
CREATE TABLE `dss_homepage_demo_menu` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) DEFAULT NULL,
  `title_en` varchar(64) DEFAULT NULL,
  `title_cn` varchar(64) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT 1,
  `icon` varchar(255) DEFAULT NULL,
  `order` int(2) DEFAULT NULL,
  `create_by` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `last_update_time` datetime DEFAULT NULL,
  `last_update_user` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for dss_homepage_demo_instance
-- ----------------------------
DROP TABLE IF EXISTS `dss_homepage_demo_instance`;
CREATE TABLE `dss_homepage_demo_instance` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `menu_id` int(20) DEFAULT NULL,
  `name` varchar(64) DEFAULT NULL,
  `url` varchar(128) DEFAULT NULL,
  `title_en` varchar(64) DEFAULT NULL,
  `title_cn` varchar(64) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT 1,
  `icon` varchar(255) DEFAULT NULL,
  `order` int(2) DEFAULT NULL,
  `click_num` int(11) DEFAULT 0,
  `create_by` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `last_update_time` datetime DEFAULT NULL,
  `last_update_user` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for dss_homepage_video
-- ----------------------------
DROP TABLE IF EXISTS `dss_homepage_video`;
CREATE TABLE `dss_homepage_video` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) DEFAULT NULL,
  `url` varchar(128) DEFAULT NULL,
  `title_en` varchar(64) DEFAULT NULL,
  `title_cn` varchar(64) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT 1,
  `icon` varchar(255) DEFAULT NULL,
  `order` int(2) DEFAULT NULL,
  `play_num` int(11) DEFAULT 0,
  `create_by` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `last_update_time` datetime DEFAULT NULL,
  `last_update_user` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE dss_project ADD `workspace_id` bigint(20) DEFAULT 1;

-- ----------------------------
-- DML SQL
-- ----------------------------
INSERT INTO dss_workspace (id, name, label, description, department, product, source, create_by, create_time, last_update_time, last_update_user) VALUES (1, 'default', 'default', 'default user workspace', NULL, NULL, 'create by user', 'root', NULL, NULL, 'root');

INSERT INTO dss_homepage_demo_menu (id, name, title_en, title_cn, description, is_active, icon, `order`, create_by, create_time, last_update_time, last_update_user) VALUES (NULL, 'workflow', 'workflow', '工作流', '工作流', 1, NULL, 1, NULL, NULL, NULL, NULL);
INSERT INTO dss_homepage_demo_menu (id, name, title_en, title_cn, description, is_active, icon, `order`, create_by, create_time, last_update_time, last_update_user) VALUES (NULL, 'application', 'application', '应用场景', '应用场景', 1, NULL, 2, NULL, NULL, NULL, NULL);
INSERT INTO dss_homepage_demo_menu (id, name, title_en, title_cn, description, is_active, icon, `order`, create_by, create_time, last_update_time, last_update_user) VALUES (NULL, 'visualization', 'visualization', '可视化', '可视化', 1, NULL, 3, NULL, NULL, NULL, NULL);

SELECT @workflow_demo_menuid:=id from dss_homepage_demo_menu WHERE `name` = 'workflow';
SELECT @application_demo_menuid:=id from dss_homepage_demo_menu WHERE `name` = 'application';
SELECT @visualization_demo_menuid:=id from dss_homepage_demo_menu WHERE `name` = 'visualization';
INSERT INTO dss_homepage_demo_instance (id, menu_id, name, url, title_en, title_cn, description, is_active, icon, `order`, click_num, create_by, create_time, last_update_time, last_update_user) VALUES (NULL, @workflow_demo_menuid, '工作流编辑执行', 'https://github.com/WeBankFinTech/DataSphereStudio', 'workflow edit execution', '工作流编辑执行', '工作流编辑执行', 1, NULL, 1, 0, NULL, NULL, NULL, NULL);
INSERT INTO dss_homepage_demo_instance (id, menu_id, name, url, title_en, title_cn, description, is_active, icon, `order`, click_num, create_by, create_time, last_update_time, last_update_user) VALUES (NULL, @workflow_demo_menuid, '工作流串联可视化', 'https://github.com/WeBankFinTech/DataSphereStudio', 'workflow series visualization', '工作流串联可视化', '工作流串联可视化', 1, NULL, 2, 0, NULL, NULL, NULL, NULL);
INSERT INTO dss_homepage_demo_instance (id, menu_id, name, url, title_en, title_cn, description, is_active, icon, `order`, click_num, create_by, create_time, last_update_time, last_update_user) VALUES (NULL, @workflow_demo_menuid, '工作流调度执行跑批', 'https://github.com/WeBankFinTech/DataSphereStudio', 'workflow scheduling execution run batch', '工作流调度执行跑批', '工作流调度执行跑批', 1, NULL, 3, 0, NULL, NULL, NULL, NULL);
INSERT INTO dss_homepage_demo_instance (id, menu_id, name, url, title_en, title_cn, description, is_active, icon, `order`, click_num, create_by, create_time, last_update_time, last_update_user) VALUES (NULL, @application_demo_menuid, '某业务日常运营报表', 'https://github.com/WeBankFinTech/DataSphereStudio', 'business daily operation report', '某业务日常运营报表', '某业务日常运营报表', 1, NULL, 1, 0, NULL, NULL, NULL, NULL);
INSERT INTO dss_homepage_demo_instance (id, menu_id, name, url, title_en, title_cn, description, is_active, icon, `order`, click_num, create_by, create_time, last_update_time, last_update_user) VALUES (NULL, @application_demo_menuid, '某业务机器学习建模预测', 'https://github.com/WeBankFinTech/DataSphereStudio', 'business machine learning modeling prediction', '某业务机器学习建模预测', '某业务机器学习建模预测', 1, NULL, 2, 0, NULL, NULL, NULL, NULL);
INSERT INTO dss_homepage_demo_instance (id, menu_id, name, url, title_en, title_cn, description, is_active, icon, `order`, click_num, create_by, create_time, last_update_time, last_update_user) VALUES (NULL, @application_demo_menuid, '某业务导出营销用户列表', 'https://github.com/WeBankFinTech/DataSphereStudio', 'business export marketing user list', '某业务导出营销用户列表', '某业务导出营销用户列表', 1, NULL, 3, 0, NULL, NULL, NULL, NULL);
INSERT INTO dss_homepage_demo_instance (id, menu_id, name, url, title_en, title_cn, description, is_active, icon, `order`, click_num, create_by, create_time, last_update_time, last_update_user) VALUES (NULL, @visualization_demo_menuid, '数据大屏体验', 'https://github.com/WeBankFinTech/DataSphereStudio', 'data big screen experience', '数据大屏体验', '数据大屏体验', 1, NULL, 1, 0, NULL, NULL, NULL, NULL);
INSERT INTO dss_homepage_demo_instance (id, menu_id, name, url, title_en, title_cn, description, is_active, icon, `order`, click_num, create_by, create_time, last_update_time, last_update_user) VALUES (NULL, @visualization_demo_menuid, '数据仪表盘体验', 'https://github.com/WeBankFinTech/DataSphereStudio', 'data dashboard experience', '数据仪表盘体验', '数据仪表盘体验', 1, NULL, 2, 0, NULL, NULL, NULL, NULL);
INSERT INTO dss_homepage_demo_instance (id, menu_id, name, url, title_en, title_cn, description, is_active, icon, `order`, click_num, create_by, create_time, last_update_time, last_update_user) VALUES (NULL, @visualization_demo_menuid, '可视化挂件快速体验', 'https://github.com/WeBankFinTech/DataSphereStudio', 'visual widgets quick experience', '可视化挂件快速体验', '可视化挂件快速体验', 1, NULL, 3, 0, NULL, NULL, NULL, NULL);

INSERT INTO dss_homepage_video (id, name, url, title_en, title_cn, description, is_active, icon, `order`, play_num, create_by, create_time, last_update_time, last_update_user) VALUES (NULL, '10秒教你搭建工作流', 'https://sandbox.webank.com/wds/dss/videos/1.mp4', '10 sec how to build workflow', '10秒教你搭建工作流', '10秒教你搭建工作流', 1, NULL, 1, 0, NULL, NULL, NULL, NULL);
INSERT INTO dss_homepage_video (id, name, url, title_en, title_cn, description, is_active, icon, `order`, play_num, create_by, create_time, last_update_time, last_update_user) VALUES (NULL, '10秒教你发邮件', 'https://sandbox.webank.com/wds/dss/videos/1.mp4', '10 sec how to send email', '10秒教你发邮件', '10秒教你发邮件', 1, NULL, 2, 0, NULL, NULL, NULL, NULL);

INSERT INTO dss_onestop_menu (id, name, title_en, title_cn, description, is_active, icon, `order`, create_by, create_time, last_update_time, last_update_user) VALUES (NULL, '应用开发', 'application development', '应用开发', '应用开发描述', 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO dss_onestop_menu (id, name, title_en, title_cn, description, is_active, icon, `order`, create_by, create_time, last_update_time, last_update_user) VALUES (NULL, '数据分析', 'data analysis', '数据分析', '数据分析描述', 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO dss_onestop_menu (id, name, title_en, title_cn, description, is_active, icon, `order`, create_by, create_time, last_update_time, last_update_user) VALUES (NULL, '生产运维', 'production operation', '生产运维', '生产运维描述', 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO dss_onestop_menu (id, name, title_en, title_cn, description, is_active, icon, `order`, create_by, create_time, last_update_time, last_update_user) VALUES (NULL, '数据质量', 'data quality', '数据质量', '数据质量描述', 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO dss_onestop_menu (id, name, title_en, title_cn, description, is_active, icon, `order`, create_by, create_time, last_update_time, last_update_user) VALUES (NULL, '管理员功能', 'administrator function', '管理员功能', '管理员功能描述', 0, NULL, NULL, NULL, NULL, NULL, NULL);

SELECT @app_dev_onstopid:=id from dss_onestop_menu WHERE `name` = '应用开发';
SELECT @data_ana_onestopid:=id from dss_onestop_menu WHERE `name` = '数据分析';
SELECT @ops_onestopid:=id from dss_onestop_menu WHERE `name` = '生产运维';
SELECT @data_qua_onestopid:=id from dss_onestop_menu WHERE `name` = '数据质量';
SELECT @admin_onestopid:=id from dss_onestop_menu WHERE `name` = '管理员功能';
SELECT @scripts_appid:=id from dss_application WHERE `name` = 'linkis';
SELECT @workflow_appid:=id from dss_application WHERE `name` = 'workflow';
SELECT @visualis_appid:=id from dss_application WHERE `name` = 'visualis';
SELECT @shedulis_appid:=id from dss_application WHERE `name` = 'schedulis';
SELECT @qualitis_appid:=id from dss_application WHERE `name` = 'qualitis';
SELECT @exchangis_appid:=id from dss_application WHERE `name` = 'exchangis';
INSERT INTO dss_onestop_menu_application (id, application_id, onestop_menu_id, title_en, title_cn, desc_en, desc_cn, labels_en, labels_cn, is_active, access_button_en, access_button_cn, manual_button_en, manual_button_cn, manual_button_url, icon, `order`, create_by, create_time, last_update_time, last_update_user) VALUES (NULL, @workflow_appid, @app_dev_onstopid, 'workflow development', '工作流开发', 'Workflow development is a data application development tool created by WeDataSphere with Linkis as the kernel.', '工作流开发是微众银行微数域(WeDataSphere)打造的数据应用开发工具，以任意桥(Linkis)做为内核，将满足从数据交换、脱敏清洗、分析挖掘、质量检测、可视化展现、定时调度到数据输出等数据应用开发全流程场景需求。', 'workflow, data warehouse development', '工作流,数仓开发', 1, 'enter workflow development', '进入工作流开发', 'user manual', '用户手册', 'https://github.com/WeBankFinTech/DataSphereStudio', 'fi-workflow|rgb(102, 102, 255)', NULL, NULL, NULL, NULL, NULL);
INSERT INTO dss_onestop_menu_application (id, application_id, onestop_menu_id, title_en, title_cn, desc_en, desc_cn, labels_en, labels_cn, is_active, access_button_en, access_button_cn, manual_button_en, manual_button_cn, manual_button_url, icon, `order`, create_by, create_time, last_update_time, last_update_user) VALUES (NULL, NULL, @app_dev_onstopid, 'StreamSQL development', 'StreamSQL开发', 'Real-time application development is a streaming solution jointly built by WeDataSphere, Boss big data team and China Telecom ctcloud Big data team.', '实时应用开发是微众银行微数域(WeDataSphere)、Boss直聘大数据团队 和 中国电信天翼云大数据团队 社区联合共建的流式解决方案，以 Linkis 做为内核，基于 Flink Engine 构建的批流统一的 Flink SQL，助力实时化转型。', 'streaming, realtime', '流式,实时', 0, 'under union construction', '联合共建中', 'related information', '相关资讯', 'https://github.com/WeBankFinTech/DataSphereStudio', 'fi-scriptis|rgb(102, 102, 255)', NULL, NULL, NULL, NULL, NULL);
INSERT INTO dss_onestop_menu_application (id, application_id, onestop_menu_id, title_en, title_cn, desc_en, desc_cn, labels_en, labels_cn, is_active, access_button_en, access_button_cn, manual_button_en, manual_button_cn, manual_button_url, icon, `order`, create_by, create_time, last_update_time, last_update_user) VALUES (NULL, NULL, @app_dev_onstopid, 'Data service development', '数据服务开发', 'Data service is a unified API service jointly built by WeDataSphere and Ihome Big data Team. With Linkis and DataSphere Studio as the kernel.', '数据服务是微众银行微数域(WeDataSphere)与 艾佳生活大数据团队 社区联合共建的统一API服务，以 Linkis 和 DataSphere Studio 做为内核，提供快速将 Scriptis 脚本生成数据API的能力，协助企业统一管理对内对外的API服务。', 'API, data service', 'API,数据服务', 0, 'under union construction', '联合共建中', 'related information', '相关资讯', 'https://github.com/WeBankFinTech/DataSphereStudio', 'fi-scriptis|rgb(102, 102, 255)', NULL, NULL, NULL, NULL, NULL);
INSERT INTO dss_onestop_menu_application (id, application_id, onestop_menu_id, title_en, title_cn, desc_en, desc_cn, labels_en, labels_cn, is_active, access_button_en, access_button_cn, manual_button_en, manual_button_cn, manual_button_url, icon, `order`, create_by, create_time, last_update_time, last_update_user) VALUES (NULL, @scripts_appid, @data_ana_onestopid, 'Scriptis', 'Scriptis', 'Scriptis is a one-stop interactive data exploration analysis tool built by WeDataSphere, uses Linkis as the kernel.', 'Scriptis是微众银行微数域(WeDataSphere)打造的一站式交互式数据探索分析工具，以任意桥(Linkis)做为内核，提供多种计算存储引擎(如Spark、Hive、TiSpark等)、Hive数据库管理功能、资源(如Yarn资源、服务器资源)管理、应用管理和各种用户资源(如UDF、变量等)管理的能力。', 'scripts development,IDE', '脚本开发,IDE', 1, 'enter Scriptis', '进入Scriptis', 'user manual', '用户手册', 'https://github.com/WeBankFinTech/DataSphereStudio', 'fi-scriptis|rgb(102, 102, 255)', NULL, NULL, NULL, NULL, NULL);
INSERT INTO dss_onestop_menu_application (id, application_id, onestop_menu_id, title_en, title_cn, desc_en, desc_cn, labels_en, labels_cn, is_active, access_button_en, access_button_cn, manual_button_en, manual_button_cn, manual_button_url, icon, `order`, create_by, create_time, last_update_time, last_update_user) VALUES (NULL, @visualis_appid, @data_ana_onestopid, 'Visualis', 'Visualis', 'Visualis is a data visualization BI tool based on Davinci, with Linkis as the kernel, it supports the analysis mode of data development exploration.', 'Visualis是基于宜信开源项目Davinci开发的数据可视化BI工具，以任意桥(Linkis)做为内核，支持拖拽式报表定义、图表联动、钻取、全局筛选、多维分析、实时查询等数据开发探索的分析模式，并做了水印、数据质量校验等金融级增强。', 'visualization, statement', '可视化,报表', 1, 'enter Visualis', '进入Visualis', 'user manual', '用户手册', 'https://github.com/WeBankFinTech/DataSphereStudio', 'fi-visualis|rgb(0, 153, 255)', NULL, NULL, NULL, NULL, NULL);
INSERT INTO dss_onestop_menu_application (id, application_id, onestop_menu_id, title_en, title_cn, desc_en, desc_cn, labels_en, labels_cn, is_active, access_button_en, access_button_cn, manual_button_en, manual_button_cn, manual_button_url, icon, `order`, create_by, create_time, last_update_time, last_update_user) VALUES (NULL, @shedulis_appid, @ops_onestopid, 'Schedulis', 'Schedulis', 'Description for Schedulis.', 'Schedulis描述', 'scheduling, workflow', '调度,工作流', 1, 'enter Schedulis', '进入Schedulis', 'user manual', '用户手册', 'https://github.com/WeBankFinTech/DataSphereStudio', 'fi-schedule|rgb(102, 102, 204)', NULL, NULL, NULL, NULL, NULL);
INSERT INTO dss_onestop_menu_application (id, application_id, onestop_menu_id, title_en, title_cn, desc_en, desc_cn, labels_en, labels_cn, is_active, access_button_en, access_button_cn, manual_button_en, manual_button_cn, manual_button_url, icon, `order`, create_by, create_time, last_update_time, last_update_user) VALUES (NULL, NULL, @ops_onestopid, 'Application operation center', '应用运维中心', 'Description for Application operation center.', '应用运维中心描述', 'production, operation', '生产,运维', 0, 'enter application operation center', '进入应用运维中心', 'user manual', '用户手册', 'https://github.com/WeBankFinTech/DataSphereStudio', 'fi-scriptis|rgb(102, 102, 255)', NULL, NULL, NULL, NULL, NULL);
INSERT INTO dss_onestop_menu_application (id, application_id, onestop_menu_id, title_en, title_cn, desc_en, desc_cn, labels_en, labels_cn, is_active, access_button_en, access_button_cn, manual_button_en, manual_button_cn, manual_button_url, icon, `order`, create_by, create_time, last_update_time, last_update_user) VALUES (NULL, @qualitis_appid, @data_qua_onestopid, 'Qualitis', 'Qualitis', 'Qualitis is a financial and one-stop data quality management platform that provides data quality model definition, visualization and monitoring of data quality results', 'Qualitis是一套金融级、一站式的数据质量管理平台，提供了数据质量模型定义，数据质量结果可视化、可监控等功能，并用一整套统一的流程来定义和检测数据集的质量并及时报告问题。', 'product, operations', '生产,运维', 1, 'enter Qualitis', '进入Qualitis', 'user manual', '用户手册', 'https://github.com/WeBankFinTech/DataSphereStudio', 'fi-qualitis|rgb(51, 153, 153)', NULL, NULL, NULL, NULL, NULL);
INSERT INTO dss_onestop_menu_application (id, application_id, onestop_menu_id, title_en, title_cn, desc_en, desc_cn, labels_en, labels_cn, is_active, access_button_en, access_button_cn, manual_button_en, manual_button_cn, manual_button_url, icon, `order`, create_by, create_time, last_update_time, last_update_user) VALUES (NULL, @exchangis_appid, @data_qua_onestopid, 'Exchangis', 'Exchangis', 'Exchangis is a lightweight, high scalability, data exchange platform, support for structured and unstructured data transmission between heterogeneous data sources.', 'Exchangis是一个轻量级的、高扩展性的数据交换平台，支持对结构化及无结构化的异构数据源之间的数据传输，在应用层上具有数据权限管控、节点服务高可用和多租户资源隔离等业务特性，而在数据层上又具有传输架构多样化、模块插件化和组件低耦合等架构特点。', 'user manual', '生产,运维', 1, 'enter Exchangis', '进入Exchangis', 'user manual', '用户手册', 'https://github.com/WeBankFinTech/DataSphereStudio', 'fi-exchange|(102, 102, 255)', NULL, NULL, NULL, NULL, NULL);
INSERT INTO dss_onestop_menu_application (id, application_id, onestop_menu_id, title_en, title_cn, desc_en, desc_cn, labels_en, labels_cn, is_active, access_button_en, access_button_cn, manual_button_en, manual_button_cn, manual_button_url, icon, `order`, create_by, create_time, last_update_time, last_update_user) VALUES (NULL, NULL, @admin_onestopid, 'Workspace management', '工作空间管理', NULL, NULL, NULL, NULL, 1, 'workspace management', '工作空间管理', null, null, null, 'fi-scriptis|rgb(102, 102, 255)', null, null, null, null, null);
INSERT INTO dss_onestop_menu_application (id, application_id, onestop_menu_id, title_en, title_cn, desc_en, desc_cn, labels_en, labels_cn, is_active, access_button_en, access_button_cn, manual_button_en, manual_button_cn, manual_button_url, icon, `order`, create_by, create_time, last_update_time, last_update_user) VALUES (NULL, NULL, @admin_onestopid, 'User resources management', '用户资源管理', NULL, NULL, NULL, NULL, 1, 'user resource management', '用户资源管理', null, null, null, 'fi-scriptis|rgb(102, 102, 255)', null, null, null, null, null);
