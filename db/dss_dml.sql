DELETE FROM dss_appconn;
INSERT INTO `dss_appconn` (`id`, `appconn_name`, `is_user_need_init`, `level`, `if_iframe`, `is_external`, `reference`, `class_name`, `appconn_class_path`, `resource`)
VALUES (1,'sso',0,1,0,0,NULL,"com.webank.wedatasphere.dss.appconn.sso.SSOAppConn",NULL,NULL),
(2,'scriptis',0,1,0,0,"sso",NULL,NULL,NULL),
(3,'workflow',0,1,1,0,NULL,'com.webank.wedatasphere.dss.appconn.workflow.WorkflowAppConn','/appcom/Install/dss/dss-appconns/workflow',NULL),
(4,'apiservice',0,1,0,0,"sso",NULL,NULL,NULL);

DELETE FROM dss_appconn_instance;
select @scriptis_appconn_id:= id from dss_appconn where appconn_name="scriptis";
select @workflow_appconn_id:= id from dss_appconn where appconn_name="workflow";
select @apiservice_appconn_id:= id from dss_appconn where appconn_name="apiservice";
INSERT INTO `dss_appconn_instance` (`id`, `appconn_id`, `label`, `url`, `enhance_json`, `homepage_uri`)
VALUES (2, @scriptis_appconn_id, 'DEV', '/home', '', ''),
(3, @workflow_appconn_id,'DEV','/workspaceHome','',''),
(4, @apiservice_appconn_id, 'DEV', '/apiservices', '', '');

DELETE FROM dss_workspace;
insert into `dss_workspace`(`id`, `name`,`label`,`description`,`create_by`,`create_time`,`department`,`product`,`source`,`last_update_time`,`last_update_user`,`workspace_type`)
values(224, 'bdapWorkspace','','bdapWorkspace','hadoop','2020-07-13 02:39:41','1','bdapWorkspace',NULL,'2020-07-13 02:39:41','hadoop','project');

DELETE FROM dss_user;
INSERT INTO `dss_user` VALUES (100,'hadoop_test','hadoop_test',1,101,1,'','','','0',NULL,'2021-11-17 09:33:45','2021-11-17 09:51:55',NULL),
(215,'hadoop','hadoop',1,101,1,'','','','0',NULL,'2021-11-17 09:43:41','2021-11-17 09:51:49',NULL);

DELETE FROM dss_workspace_dictionary;
insert into `dss_workspace_dictionary`(`id`,`workspace_id`,`parent_key`,`dic_name`,`dic_name_en`,`dic_key`,`dic_value`,`dic_value_en`,`title`,`title_en`,`url`,`url_type`,`icon`,`order_num`,`remark`,`create_user`,`create_time`,`update_user`,`update_time`) values (1,0,'0','空间开发流程','Space development process','w_develop_process',NULL,NULL,NULL,NULL,NULL,0,NULL,1,'空间开发流程','SYSTEM','2020-12-28 17:32:34',NULL,'2021-02-22 17:46:40');
insert into `dss_workspace_dictionary`(`id`,`workspace_id`,`parent_key`,`dic_name`,`dic_name_en`,`dic_key`,`dic_value`,`dic_value_en`,`title`,`title_en`,`url`,`url_type`,`icon`,`order_num`,`remark`,`create_user`,`create_time`,`update_user`,`update_time`) values (2,0,'w_develop_process','需求','Demand','wdp_demand','创建新的业务需求，并将需求指派给对应负责人。','Create new business requirements and assign them to the corresponding responsible person.','Demo案例','Demo case',NULL,0,'xuqiu',1,'空间开发流程-需求','SYSTEM','2020-12-28 17:32:35',NULL,'2021-02-23 09:38:07');
insert into `dss_workspace_dictionary`(`id`,`workspace_id`,`parent_key`,`dic_name`,`dic_name_en`,`dic_key`,`dic_value`,`dic_value_en`,`title`,`title_en`,`url`,`url_type`,`icon`,`order_num`,`remark`,`create_user`,`create_time`,`update_user`,`update_time`) values (3,0,'w_develop_process','设计','Design','wdp_design','针对新的业务需求，进行数仓规划和库表设计。','According to the new business requirements, data warehouse planning and database table design are carried out.','Demo案例','Demo case',NULL,0,'sheji',1,'空间开发流程-设计','SYSTEM','2020-12-28 17:32:35',NULL,'2021-02-23 09:38:09');
insert into `dss_workspace_dictionary`(`id`,`workspace_id`,`parent_key`,`dic_name`,`dic_name_en`,`dic_key`,`dic_value`,`dic_value_en`,`title`,`title_en`,`url`,`url_type`,`icon`,`order_num`,`remark`,`create_user`,`create_time`,`update_user`,`update_time`) values (4,0,'w_develop_process','开发','Development','wdp_development','针对新的业务需求，进行数仓规划和库表设计。','According to the new business requirements, data warehouse planning and database table design are carried out.','Demo案例','Demo case',NULL,0,'kaifa',1,'空间开发流程-开发','SYSTEM','2020-12-28 17:32:35',NULL,'2021-02-23 09:38:10');
insert into `dss_workspace_dictionary`(`id`,`workspace_id`,`parent_key`,`dic_name`,`dic_name_en`,`dic_key`,`dic_value`,`dic_value_en`,`title`,`title_en`,`url`,`url_type`,`icon`,`order_num`,`remark`,`create_user`,`create_time`,`update_user`,`update_time`) values (5,0,'w_develop_process','调试','Debugging','wdp_debug','创建新的业务需求，并将需求指派给对应负责人。','Create new business requirements and assign them to the corresponding responsible person.','Demo案例','Demo case',NULL,0,'tiaoshi',1,'空间开发流程-调试','SYSTEM','2020-12-28 17:32:35',NULL,'2021-02-23 09:38:11');
insert into `dss_workspace_dictionary`(`id`,`workspace_id`,`parent_key`,`dic_name`,`dic_name_en`,`dic_key`,`dic_value`,`dic_value_en`,`title`,`title_en`,`url`,`url_type`,`icon`,`order_num`,`remark`,`create_user`,`create_time`,`update_user`,`update_time`) values (6,0,'w_develop_process','生产','Production','wdp_product','创建新的业务需求，并将需求指派给对应负责人。','Create new business requirements and assign them to the corresponding responsible person.','Demo案例','Demo case',NULL,0,'shengchan',1,'空间开发流程-生产','SYSTEM','2020-12-28 17:32:35',NULL,'2021-02-23 09:38:12');
insert into `dss_workspace_dictionary`(`id`,`workspace_id`,`parent_key`,`dic_name`,`dic_name_en`,`dic_key`,`dic_value`,`dic_value_en`,`title`,`title_en`,`url`,`url_type`,`icon`,`order_num`,`remark`,`create_user`,`create_time`,`update_user`,`update_time`) values (7,0,'0','工程开发流程','Engineering development process','p_develop_process',NULL,NULL,NULL,NULL,NULL,0,NULL,1,'工程开发流程','SYSTEM','2020-12-28 17:32:35',NULL,'2021-02-22 17:48:48');
insert into `dss_workspace_dictionary`(`id`,`workspace_id`,`parent_key`,`dic_name`,`dic_name_en`,`dic_key`,`dic_value`,`dic_value_en`,`title`,`title_en`,`url`,`url_type`,`icon`,`order_num`,`remark`,`create_user`,`create_time`,`update_user`,`update_time`) values (8,0,'p_develop_process','开发中心','Development Center','pdp_development_center','dev',NULL,NULL,NULL,NULL,0,'kaifa-icon',1,'工程开发流程-开发中心','SYSTEM','2020-12-28 17:32:35',NULL,'2021-02-22 17:49:02');
insert into `dss_workspace_dictionary`(`id`,`workspace_id`,`parent_key`,`dic_name`,`dic_name_en`,`dic_key`,`dic_value`,`dic_value_en`,`title`,`title_en`,`url`,`url_type`,`icon`,`order_num`,`remark`,`create_user`,`create_time`,`update_user`,`update_time`) values (10,0,'0','工程编排模式','Project layout mode','p_orchestrator_mode',NULL,NULL,NULL,NULL,NULL,0,NULL,1,'工程编排模式','SYSTEM','2020-12-28 17:32:35',NULL,'2021-02-22 17:49:36');
insert into `dss_workspace_dictionary`(`id`,`workspace_id`,`parent_key`,`dic_name`,`dic_name_en`,`dic_key`,`dic_value`,`dic_value_en`,`title`,`title_en`,`url`,`url_type`,`icon`,`order_num`,`remark`,`create_user`,`create_time`,`update_user`,`update_time`) values (11,0,'p_orchestrator_mode','工作流','Workflow','pom_work_flow','radio',NULL,NULL,NULL,NULL,0,'gongzuoliu-icon',1,'工程编排模式-工作流','SYSTEM','2020-12-28 17:32:35',NULL,'2021-02-22 17:49:49');
insert into `dss_workspace_dictionary`(`id`,`workspace_id`,`parent_key`,`dic_name`,`dic_name_en`,`dic_key`,`dic_value`,`dic_value_en`,`title`,`title_en`,`url`,`url_type`,`icon`,`order_num`,`remark`,`create_user`,`create_time`,`update_user`,`update_time`) values (14,0,'pom_work_flow','DAG','DAG','pom_work_flow_DAG',NULL,NULL,NULL,NULL,NULL,0,NULL,1,'工程编排模式-工作流-DAG','SYSTEM','2020-12-28 17:32:35',NULL,'2021-02-22 17:50:31');
insert into `dss_workspace_dictionary`(`id`,`workspace_id`,`parent_key`,`dic_name`,`dic_name_en`,`dic_key`,`dic_value`,`dic_value_en`,`title`,`title_en`,`url`,`url_type`,`icon`,`order_num`,`remark`,`create_user`,`create_time`,`update_user`,`update_time`) values (16,0,'pom_single_task','Scriptis','Scriptis','pom_single_task_scriptis',NULL,NULL,NULL,NULL,NULL,0,NULL,1,'工程编排模式-单任务-Scriptis','SYSTEM','2020-12-28 17:32:35',NULL,'2021-02-22 17:51:08');
insert into `dss_workspace_dictionary`(`id`,`workspace_id`,`parent_key`,`dic_name`,`dic_name_en`,`dic_key`,`dic_value`,`dic_value_en`,`title`,`title_en`,`url`,`url_type`,`icon`,`order_num`,`remark`,`create_user`,`create_time`,`update_user`,`update_time`) values (18,0,'pom_single_task','Qualitis','Qualitis','pom_single_task_qualitis',NULL,NULL,NULL,NULL,NULL,0,NULL,1,'工程编排模式-单任务-Qualitis','SYSTEM','2020-12-28 17:32:35',NULL,'2021-02-22 17:50:53');
insert into `dss_workspace_dictionary`(`id`,`workspace_id`,`parent_key`,`dic_name`,`dic_name_en`,`dic_key`,`dic_value`,`dic_value_en`,`title`,`title_en`,`url`,`url_type`,`icon`,`order_num`,`remark`,`create_user`,`create_time`,`update_user`,`update_time`) values (20,0,'pom_consist_orchestrator','Qualitis','Qualitis','pom_consist_orchestrator_qualitis',NULL,NULL,NULL,NULL,NULL,0,NULL,1,'工程编排模式-组合编排-Qualitis','SYSTEM','2020-12-28 17:32:35',NULL,'2021-02-22 17:50:57');
insert into `dss_workspace_dictionary`(`id`,`workspace_id`,`parent_key`,`dic_name`,`dic_name_en`,`dic_key`,`dic_value`,`dic_value_en`,`title`,`title_en`,`url`,`url_type`,`icon`,`order_num`,`remark`,`create_user`,`create_time`,`update_user`,`update_time`) values (21,0,'pom_consist_orchestrator','Email','Email','pom_consist_orchestrator_email',NULL,NULL,NULL,NULL,NULL,0,NULL,1,'工程编排模式-组合编排-Email','SYSTEM','2020-12-28 17:32:35',NULL,'2021-02-22 17:51:22');
insert into `dss_workspace_dictionary`(`workspace_id`, `parent_key`, `dic_name`, `dic_name_en`, `dic_key`, `dic_value`, `dic_value_en`, `title`, `title_en`, `url`, `url_type`, `icon`, `order_num`, `remark`, `create_user`, `create_time`, `update_user`, `update_time`) values('0','0','工作空间默认部门','Space development name','w_workspace_department','10001-部门一;10002-部门二;10003-部门三',NULL,NULL,NULL,NULL,'0',NULL,'1','工作空间默认部门，前面是id后面是部门名称中间使用‘-’,横杆分隔，多个以英文分号分隔','SYSTEM','2020-12-28 17:32:34',NULL,'2021-02-22 17:46:40');

DELETE FROM dss_sidebar;
insert  into `dss_sidebar`(`id`,`workspace_id`,`name`,`name_en`,`title`,`title_en`,`type`,`order_num`,`remark`,`create_user`,`create_time`,`update_user`,`update_time`) values (2,0,'菜单','Menu','菜单','Menu',1,1,NULL,'SYSTEM','2020-12-15 13:21:06',NULL,'2021-02-23 09:45:50');
--insert  into `dss_sidebar`(`id`,`workspace_id`,`name`,`name_en`,`title`,`title_en`,`type`,`order_num`,`remark`,`create_user`,`create_time`,`update_user`,`update_time`) values (3,0,'常见问题','Common problem','常见问题','Common problem',1,1,NULL,'SYSTEM','2020-12-15 13:21:06',NULL,'2021-02-23 09:46:18');

DELETE FROM dss_sidebar_content;
insert  into `dss_sidebar_content`(`id`,`workspace_id`,`sidebar_id`,`name`,`name_en`,`title`,`title_en`,`url`,`url_type`,`icon`,`order_num`,`remark`,`create_user`,`create_time`,`update_user`,`update_time`) values (3,0,2,NULL,NULL,'工作空间管理','Workspace management','/workspaceManagement/productsettings',0,'menuIcon',1,NULL,'SYSTEM','2020-12-15 13:21:07',NULL,'2021-02-23 09:47:49');
insert  into `dss_sidebar_content`(`id`,`workspace_id`,`sidebar_id`,`name`,`name_en`,`title`,`title_en`,`url`,`url_type`,`icon`,`order_num`,`remark`,`create_user`,`create_time`,`update_user`,`update_time`) values (4,0,2,NULL,NULL,'UDF管理','UDF management','dss/linkis/?noHeader=1&noFooter=1#/urm/udfManagement',1,'menuIcon',1,NULL,'SYSTEM','2020-12-15 13:21:07',NULL,'2021-02-23 09:47:11');
--insert  into `dss_sidebar_content`(`id`,`workspace_id`,`sidebar_id`,`name`,`name_en`,`title`,`title_en`,`url`,`url_type`,`icon`,`order_num`,`remark`,`create_user`,`create_time`,`update_user`,`update_time`) values (5,0,3,NULL,NULL,'资源配置说明',NULL,'http://127.0.0.1:8088/kn/d/38',1,'fi-warn',1,NULL,'SYSTEM','2020-12-15 13:21:07',NULL,'2021-01-12 17:16:52');
--insert  into `dss_sidebar_content`(`id`,`workspace_id`,`sidebar_id`,`name`,`name_en`,`title`,`title_en`,`url`,`url_type`,`icon`,`order_num`,`remark`,`create_user`,`create_time`,`update_user`,`update_time`) values (6,0,3,NULL,NULL,'Spark使用指南','[Discussion on error code 22223]','http://127.0.0.1:8088/kn/d/40',1,'fi-warn',1,NULL,'SYSTEM','2020-12-15 13:21:07',NULL,'2021-02-23 09:48:28');
--insert  into `dss_sidebar_content`(`id`,`workspace_id`,`sidebar_id`,`name`,`name_en`,`title`,`title_en`,`url`,`url_type`,`icon`,`order_num`,`remark`,`create_user`,`create_time`,`update_user`,`update_time`) values (7,0,3,NULL,NULL,'Hive语法介绍',NULL,'http://127.0.0.1:8088/kn/d/34',1,'fi-warn',1,NULL,'SYSTEM','2020-12-15 13:21:07',NULL,'2021-01-12 17:17:00');
--insert  into `dss_sidebar_content`(`id`,`workspace_id`,`sidebar_id`,`name`,`name_en`,`title`,`title_en`,`url`,`url_type`,`icon`,`order_num`,`remark`,`create_user`,`create_time`,`update_user`,`update_time`) values (8,0,3,NULL,NULL,'工作流使用介绍',NULL,'http://127.0.0.1:8088/kn/d/42',1,'fi-warn',1,NULL,'SYSTEM','2020-12-15 13:21:07',NULL,'2021-01-12 17:17:01');
--insert  into `dss_sidebar_content`(`id`,`workspace_id`,`sidebar_id`,`name`,`name_en`,`title`,`title_en`,`url`,`url_type`,`icon`,`order_num`,`remark`,`create_user`,`create_time`,`update_user`,`update_time`) values (9,0,3,NULL,NULL,'数据服务使用介绍','Discussion on error code 22223','http://127.0.0.1:8088/kn/d/32',1,'fi-warn',1,NULL,'SYSTEM','2020-12-15 13:21:07',NULL,'2021-02-23 09:48:19');

DELETE FROM dss_workspace_menu;
INSERT INTO `dss_workspace_menu` (`id`, `name`, `title_en`, `title_cn`, `description`, `is_active`, `icon`, `order`, `create_by`, `create_time`, `last_update_time`, `last_update_user`) VALUES('1','数据交换','data exchange','数据交换','数据交换描述','1',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `dss_workspace_menu` (`id`, `name`, `title_en`, `title_cn`, `description`, `is_active`, `icon`, `order`, `create_by`, `create_time`, `last_update_time`, `last_update_user`) VALUES('2','数据分析','data analysis','数据分析','数据分析描述','1',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `dss_workspace_menu` (`id`, `name`, `title_en`, `title_cn`, `description`, `is_active`, `icon`, `order`, `create_by`, `create_time`, `last_update_time`, `last_update_user`) VALUES('3','生产运维','production operation','生产运维','生产运维描述','1',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `dss_workspace_menu` (`id`, `name`, `title_en`, `title_cn`, `description`, `is_active`, `icon`, `order`, `create_by`, `create_time`, `last_update_time`, `last_update_user`) VALUES('4','数据质量','data quality','数据质量','数据质量描述','1',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `dss_workspace_menu` (`id`, `name`, `title_en`, `title_cn`, `description`, `is_active`, `icon`, `order`, `create_by`, `create_time`, `last_update_time`, `last_update_user`) VALUES('5','管理员功能','administrator function','管理员功能','管理员功能描述','0',NULL,NULL,NULL,NULL,NULL,NULL);
insert into `dss_workspace_menu` (`id`, `name`, `title_en`, `title_cn`, `description`, `is_active`, `icon`, `order`, `create_by`, `create_time`, `last_update_time`, `last_update_user`) values('6','数据应用','data application','数据应用','数据应用描述','1',NULL,NULL,NULL,NULL,NULL,NULL);
insert into `dss_workspace_menu` (`id`, `name`, `title_en`, `title_cn`, `description`, `is_active`, `icon`, `order`, `create_by`, `create_time`, `last_update_time`, `last_update_user`) values('7','应用开发','application development','应用开发','应用开发描述','1',NULL,NULL,NULL,NULL,NULL,NULL);

DELETE FROM dss_workspace_menu_appconn;
INSERT INTO dss_workspace_menu_appconn (appconn_id, menu_id, title_en, title_cn, desc_en, desc_cn, labels_en, labels_cn, is_active, access_button_en, access_button_cn, manual_button_en, manual_button_cn, manual_button_url, icon, `order`, create_by, create_time, last_update_time, last_update_user, image)
VALUES (@scriptis_appconn_id, 2, 'Scriptis', 'Scriptis', 'Scriptis is a one-stop interactive data exploration analysis tool built by WeDataSphere, uses Linkis as the kernel.', 'Scriptis是微众银行微数域(WeDataSphere)打造的一站式交互式数据探索分析工具，以任意桥(Linkis)做为内核，提供多种计算存储引擎(如Spark、Hive、TiSpark等)、Hive数据库管理功能、资源(如Yarn资源、服务器资源)管理、应用管理和各种用户资源(如UDF、变量等)管理的能力。', 'scripts development,IDE', '脚本开发,IDE', 1, 'enter Scriptis', '进入Scriptis', 'user manual', '用户手册', 'http://127.0.0.1:8088/wiki/scriptis/manual/workspace_cn.html', 'shujukaifa-logo', null, null, null, null, null, 'shujukaifa-icon');
INSERT INTO dss_workspace_menu_appconn (appconn_id, menu_id, title_en, title_cn, desc_en, desc_cn, labels_en, labels_cn, is_active, access_button_en, access_button_cn, manual_button_en, manual_button_cn, manual_button_url, icon, `order`, create_by, create_time, last_update_time, last_update_user, image)
VALUES (@workflow_appconn_id, 2, 'workflow', '工作流开发', '工作流开发', '工作流开发', null, null, 1, 'Enter workflow', '进入 工作流开发', null, null, null, null, null, null, null, null, null, null);
INSERT INTO dss_workspace_menu_appconn (appconn_id, menu_id, title_en, title_cn, desc_en, desc_cn, labels_en, labels_cn, is_active, access_button_en, access_button_cn, manual_button_en, manual_button_cn, manual_button_url, icon, `order`, create_by, create_time, last_update_time, last_update_user, image)
VALUES (@apiservice_appconn_id, 7, 'dataService', '数据服务', '/dataService', '/dataService', null, null, 1, 'Enter dataService', '进入 数据服务', null, null, null, null, null, null, null, null, null, null);


DELETE FROM dss_workspace_role;
INSERT INTO `dss_workspace_role` (`id`, `workspace_id`, `name`, `front_name`, `update_time`, `description`) VALUES('1','-1','admin','管理员','2020-07-13 02:43:35','通用角色管理员');
INSERT INTO `dss_workspace_role` (`id`, `workspace_id`, `name`, `front_name`, `update_time`, `description`) VALUES('2','-1','maintenance','运维用户','2020-07-13 02:43:35','通用角色运维用户');
INSERT INTO `dss_workspace_role` (`id`, `workspace_id`, `name`, `front_name`, `update_time`, `description`) VALUES('3','-1','developer','开发用户','2020-07-13 02:43:35','通用角色开发用户');
INSERT INTO `dss_workspace_role` (`id`, `workspace_id`, `name`, `front_name`, `update_time`, `description`) VALUES('4','-1','analyser','分析用户','2020-07-13 02:43:36','通用角色分析用户');
INSERT INTO `dss_workspace_role` (`id`, `workspace_id`, `name`, `front_name`, `update_time`, `description`) VALUES('5','-1','operator','运营用户','2020-07-13 02:43:36','通用角色运营用户');
INSERT INTO `dss_workspace_role` (`id`, `workspace_id`, `name`, `front_name`, `update_time`, `description`) VALUES('6','-1','boss','领导','2020-07-13 02:43:36','通用角色领导');
INSERT INTO `dss_workspace_role` (`id`, `workspace_id`, `name`, `front_name`, `update_time`, `description`) VALUES('7','-1','apiUser','数据服务用户','2020-08-21 11:35:02','通用角色数据服务用户');

DELETE FROM dss_workspace_user_role;
select @defaultWorkspaceId:=id from dss_workspace where name='bdapWorkspace';
insert  into `dss_workspace_user_role`(`workspace_id`,`username`,`role_id`,`create_time`,`created_by`,`user_id`) values
(@defaultWorkspaceId,'hadoop',1,'2021-09-06 14:39:17','hadoop',0),(@defaultWorkspaceId,'hadoop',2,'2021-09-06 14:39:17','hadoop',0),
(@defaultWorkspaceId,'hadoop',3,'2021-09-06 14:39:17','hadoop',0),(@defaultWorkspaceId,'hadoop',4,'2021-09-06 14:39:17','hadoop',0),
(@defaultWorkspaceId,'hadoop',5,'2021-09-06 14:39:17','hadoop',0),(@defaultWorkspaceId,'hadoop',6,'2021-09-06 14:39:17','hadoop',0),
(@defaultWorkspaceId,'hadoop',7,'2021-09-06 14:39:17','hadoop',0);

DELETE FROM dss_workflow_node;
insert into `dss_workflow_node` (`id`, `name`, `appconn_name`, `node_type`, `jump_type`, `support_jump`, `submit_to_scheduler`, `enable_copy`, `should_creation_before_node`, `icon_path`) values('1','python','scriptis','linkis.python.python','2','1','1','1','0','icons/python.icon');
insert into `dss_workflow_node` (`id`, `name`, `appconn_name`, `node_type`, `jump_type`, `support_jump`, `submit_to_scheduler`, `enable_copy`, `should_creation_before_node`, `icon_path`) values('2','pyspark','scriptis','linkis.spark.py','2','1','1','1','0','icons/pyspark.icon');
insert into `dss_workflow_node` (`id`, `name`, `appconn_name`, `node_type`, `jump_type`, `support_jump`, `submit_to_scheduler`, `enable_copy`, `should_creation_before_node`, `icon_path`) values('3','sql','scriptis','linkis.spark.sql','2','1','1','1','0','icons/sql.icon');
insert into `dss_workflow_node` (`id`, `name`, `appconn_name`, `node_type`, `jump_type`, `support_jump`, `submit_to_scheduler`, `enable_copy`, `should_creation_before_node`, `icon_path`) values('4','scala','scriptis','linkis.spark.scala','2','1','1','1','0','icons/scala.icon');
insert into `dss_workflow_node` (`id`, `name`, `appconn_name`, `node_type`, `jump_type`, `support_jump`, `submit_to_scheduler`, `enable_copy`, `should_creation_before_node`, `icon_path`) values('5','hql','scriptis','linkis.hive.hql','2','1','1','1','0','icons/hql.icon');
insert into `dss_workflow_node` (`id`, `name`, `appconn_name`, `node_type`, `jump_type`, `support_jump`, `submit_to_scheduler`, `enable_copy`, `should_creation_before_node`, `icon_path`) values('6','jdbc','scriptis','linkis.jdbc.jdbc','2','1','1','1','0','icons/jdbc.icon');
insert into `dss_workflow_node` (`id`, `name`, `appconn_name`, `node_type`, `jump_type`, `support_jump`, `submit_to_scheduler`, `enable_copy`, `should_creation_before_node`, `icon_path`) values('7','shell','scriptis','linkis.shell.sh','2','1','1','1','0','icons/shell.icon');
insert into `dss_workflow_node` (`id`, `name`, `appconn_name`, `node_type`, `jump_type`, `support_jump`, `submit_to_scheduler`, `enable_copy`, `should_creation_before_node`, `icon_path`) values('10','connector','scriptis','linkis.control.empty','2','0','1','1','0','icons/connector.icon');
insert into `dss_workflow_node` (`id`, `name`, `appconn_name`, `node_type`, `jump_type`, `support_jump`, `submit_to_scheduler`, `enable_copy`, `should_creation_before_node`, `icon_path`) values('12','subFlow','scriptis','workflow.subflow','2','1','1','0','1','icons/subFlow.icon');

DELETE FROM dss_workflow_node_group;
insert  into `dss_workflow_node_group`(`id`,`name`,`name_en`,`description`,`order`) values (1,'数据交换','Data exchange',NULL,1);
insert  into `dss_workflow_node_group`(`id`,`name`,`name_en`,`description`,`order`) values (2,'数据开发','Data development',NULL,2);
insert  into `dss_workflow_node_group`(`id`,`name`,`name_en`,`description`,`order`) values (3,'数据质量','Data Governance',NULL,3);
insert  into `dss_workflow_node_group`(`id`,`name`,`name_en`,`description`,`order`) values (4,'数据可视化','Data visualization',NULL,4);
insert  into `dss_workflow_node_group`(`id`,`name`,`name_en`,`description`,`order`) values (5,'数据输出','Data output',NULL,8);
insert  into `dss_workflow_node_group`(`id`,`name`,`name_en`,`description`,`order`) values (6,'信号节点','Signal node',NULL,6);
insert  into `dss_workflow_node_group`(`id`,`name`,`name_en`,`description`,`order`) values (7,'功能节点','Function node',NULL,7);
insert  into `dss_workflow_node_group`(`id`,`name`,`name_en`,`description`,`order`) values (8,'机器学习','Machine Learning',NULL,5);

DELETE FROM dss_workflow_node_to_group;
select @scriptis_node_groupId:=id from dss_workflow_node_group where name='数据开发';
select @function_node_groupId:=id from dss_workflow_node_group where name='功能节点';
insert  into `dss_workflow_node_to_group`(`node_id`,`group_id`) values (1, @scriptis_node_groupId);
insert  into `dss_workflow_node_to_group`(`node_id`,`group_id`) values (2, @scriptis_node_groupId);
insert  into `dss_workflow_node_to_group`(`node_id`,`group_id`) values (3, @scriptis_node_groupId);
insert  into `dss_workflow_node_to_group`(`node_id`,`group_id`) values (4, @scriptis_node_groupId);
insert  into `dss_workflow_node_to_group`(`node_id`,`group_id`) values (5, @scriptis_node_groupId);
insert  into `dss_workflow_node_to_group`(`node_id`,`group_id`) values (6, @scriptis_node_groupId);
insert  into `dss_workflow_node_to_group`(`node_id`,`group_id`) values (7, @scriptis_node_groupId);
insert  into `dss_workflow_node_to_group`(`node_id`,`group_id`) values (10, @function_node_groupId);
insert  into `dss_workflow_node_to_group`(`node_id`,`group_id`) values (12, @function_node_groupId);

DELETE FROM dss_workflow_node_ui;
-- todo msg.topic在receiver和sender使用了重复key
insert  into `dss_workflow_node_ui`(`id`,`key`,`description`,`description_en`,`lable_name`,`lable_name_en`,`ui_type`,`required`,`value`,`default_value`,`is_hidden`,`condition`,`is_advanced`,`order`,`node_menu_type`,`is_base_info`,`position`) values (1,'title','请填写节点名称','Please enter node name','节点名','Node name','Input',1,NULL,NULL,0,NULL,0,1,1,1,'node');
insert  into `dss_workflow_node_ui`(`id`,`key`,`description`,`description_en`,`lable_name`,`lable_name_en`,`ui_type`,`required`,`value`,`default_value`,`is_hidden`,`condition`,`is_advanced`,`order`,`node_menu_type`,`is_base_info`,`position`) values (3,'desc','请填写节点描述','Please enter the node description','节点描述','Node description','Text',0,NULL,NULL,0,NULL,0,4,1,1,'node');
insert  into `dss_workflow_node_ui`(`id`,`key`,`description`,`description_en`,`lable_name`,`lable_name_en`,`ui_type`,`required`,`value`,`default_value`,`is_hidden`,`condition`,`is_advanced`,`order`,`node_menu_type`,`is_base_info`,`position`) values (5,'businessTag',NULL,NULL,'业务标签','businessTag','Tag',0,NULL,NULL,0,NULL,0,2,1,1,'node');
insert  into `dss_workflow_node_ui`(`id`,`key`,`description`,`description_en`,`lable_name`,`lable_name_en`,`ui_type`,`required`,`value`,`default_value`,`is_hidden`,`condition`,`is_advanced`,`order`,`node_menu_type`,`is_base_info`,`position`) values (6,'appTag',NULL,NULL,'应用标签','appTag','Tag',0,NULL,NULL,0,NULL,0,3,1,1,'node');
insert  into `dss_workflow_node_ui`(`id`,`key`,`description`,`description_en`,`lable_name`,`lable_name_en`,`ui_type`,`required`,`value`,`default_value`,`is_hidden`,`condition`,`is_advanced`,`order`,`node_menu_type`,`is_base_info`,`position`) values (7,'spark.driver.memory','驱动器内存大小，默认值：2','Driver memory, default value: 2','spark-driver-memory','spark-driver-memory','Input',0,NULL,'2',0,NULL,0,1,1,0,'startup');
insert  into `dss_workflow_node_ui`(`id`,`key`,`description`,`description_en`,`lable_name`,`lable_name_en`,`ui_type`,`required`,`value`,`default_value`,`is_hidden`,`condition`,`is_advanced`,`order`,`node_menu_type`,`is_base_info`,`position`) values (8,'spark.executor.memory','执行器内存大小，默认值：3','Executor memory, default value: 3','spark-executor-memory','spark-executor-memory','Input',0,NULL,'3',0,NULL,0,1,1,0,'startup');
insert  into `dss_workflow_node_ui`(`id`,`key`,`description`,`description_en`,`lable_name`,`lable_name_en`,`ui_type`,`required`,`value`,`default_value`,`is_hidden`,`condition`,`is_advanced`,`order`,`node_menu_type`,`is_base_info`,`position`) values (9,'spark.executor.cores','执行器核心个数，默认值：1','Number of cores per executor, default value: 1','spark-executor-cores','spark-executor-cores','Input',0,NULL,'2',0,NULL,0,1,1,0,'startup');
insert  into `dss_workflow_node_ui`(`id`,`key`,`description`,`description_en`,`lable_name`,`lable_name_en`,`ui_type`,`required`,`value`,`default_value`,`is_hidden`,`condition`,`is_advanced`,`order`,`node_menu_type`,`is_base_info`,`position`) values (10,'spark.executor.instances','执行器个数，默认值：2','Number of executors, default value: 2','spark-executor-instances','spark-executor-instances','Input',0,NULL,'2',0,NULL,0,1,1,0,'startup');
insert  into `dss_workflow_node_ui`(`id`,`key`,`description`,`description_en`,`lable_name`,`lable_name_en`,`ui_type`,`required`,`value`,`default_value`,`is_hidden`,`condition`,`is_advanced`,`order`,`node_menu_type`,`is_base_info`,`position`) values (11,'wds.linkis.rm.yarnqueue','执行队列','Execution queue','wds-linkis-yarnqueue','wds-linkis-yarnqueue','Input',0,NULL,'dws',0,NULL,0,1,1,0,'startup');
insert  into `dss_workflow_node_ui`(`id`,`key`,`description`,`description_en`,`lable_name`,`lable_name_en`,`ui_type`,`required`,`value`,`default_value`,`is_hidden`,`condition`,`is_advanced`,`order`,`node_menu_type`,`is_base_info`,`position`) values (12,'resources',NULL,NULL,'资源信息','Resource information','Upload',0,'[]',NULL,0,NULL,0,1,1,0,'node');
insert  into `dss_workflow_node_ui`(`id`,`key`,`description`,`description_en`,`lable_name`,`lable_name_en`,`ui_type`,`required`,`value`,`default_value`,`is_hidden`,`condition`,`is_advanced`,`order`,`node_menu_type`,`is_base_info`,`position`) values (13,'category','请选择类型','Please choose the type','类型','Type','Select',1,'[\"node\"]','node',0,NULL,0,1,1,0,'runtime');
insert  into `dss_workflow_node_ui`(`id`,`key`,`description`,`description_en`,`lable_name`,`lable_name_en`,`ui_type`,`required`,`value`,`default_value`,`is_hidden`,`condition`,`is_advanced`,`order`,`node_menu_type`,`is_base_info`,`position`) values (14,'subject','请填写邮件标题','Please enter the email subject','邮件标题','Email Subject','Input',1,NULL,NULL,0,NULL,0,1,1,0,'runtime');
insert  into `dss_workflow_node_ui`(`id`,`key`,`description`,`description_en`,`lable_name`,`lable_name_en`,`ui_type`,`required`,`value`,`default_value`,`is_hidden`,`condition`,`is_advanced`,`order`,`node_menu_type`,`is_base_info`,`position`) values (15,'content','请选择或输入发送项','Please choose or enter the items to send','发送项','Intems to Send','MultiBinding',1,'[\"linkis.appconn.visualis.display\",\"linkis.appconn.visualis.dashboard\"]','[]',0,NULL,0,1,1,0,'runtime');
insert  into `dss_workflow_node_ui`(`id`,`key`,`description`,`description_en`,`lable_name`,`lable_name_en`,`ui_type`,`required`,`value`,`default_value`,`is_hidden`,`condition`,`is_advanced`,`order`,`node_menu_type`,`is_base_info`,`position`) values (16,'to','请填写收件人','Please enter recipients','收件人','To','Input',1,NULL,NULL,0,NULL,0,1,1,0,'runtime');
insert  into `dss_workflow_node_ui`(`id`,`key`,`description`,`description_en`,`lable_name`,`lable_name_en`,`ui_type`,`required`,`value`,`default_value`,`is_hidden`,`condition`,`is_advanced`,`order`,`node_menu_type`,`is_base_info`,`position`) values (17,'cc','请填写抄送人','Please enter carbon copy recipients','抄送','Cc','Input',0,NULL,NULL,0,NULL,0,1,1,0,'runtime');
insert  into `dss_workflow_node_ui`(`id`,`key`,`description`,`description_en`,`lable_name`,`lable_name_en`,`ui_type`,`required`,`value`,`default_value`,`is_hidden`,`condition`,`is_advanced`,`order`,`node_menu_type`,`is_base_info`,`position`) values (18,'bcc','请填写秘密发送人','Please enter blind carbon copy recipients','秘密抄送','Bcc','Input',0,NULL,NULL,0,NULL,0,1,1,0,'runtime');
insert  into `dss_workflow_node_ui`(`id`,`key`,`description`,`description_en`,`lable_name`,`lable_name_en`,`ui_type`,`required`,`value`,`default_value`,`is_hidden`,`condition`,`is_advanced`,`order`,`node_menu_type`,`is_base_info`,`position`) values (19,'itsm','请填写关联审批单','Please enter ITSM','关联审批单','ITSM','Input',1,NULL,NULL,0,NULL,0,1,1,0,'runtime');
insert  into `dss_workflow_node_ui`(`id`,`key`,`description`,`description_en`,`lable_name`,`lable_name_en`,`ui_type`,`required`,`value`,`default_value`,`is_hidden`,`condition`,`is_advanced`,`order`,`node_menu_type`,`is_base_info`,`position`) values (20,'msg.type','请正确填写消息类型','Please enter message type correctly','msg.type','msg.type','Disable',1,NULL,'SEND',0,NULL,0,1,1,0,'runtime');
insert  into `dss_workflow_node_ui`(`id`,`key`,`description`,`description_en`,`lable_name`,`lable_name_en`,`ui_type`,`required`,`value`,`default_value`,`is_hidden`,`condition`,`is_advanced`,`order`,`node_menu_type`,`is_base_info`,`position`) values (21,'msg.topic','消息主题，必须与eventreceiver完全一致','Message subject must be exactly the same as eventreceiver','msg.topic','msg.topic','Input',1,NULL,NULL,0,NULL,0,1,1,0,'runtime');
insert  into `dss_workflow_node_ui`(`id`,`key`,`description`,`description_en`,`lable_name`,`lable_name_en`,`ui_type`,`required`,`value`,`default_value`,`is_hidden`,`condition`,`is_advanced`,`order`,`node_menu_type`,`is_base_info`,`position`) values (22,'msg.sender','请正确填写发送者','Please enter the sender correctly','msg.sender','msg.sender','Input',1,NULL,NULL,0,NULL,0,1,1,0,'runtime');
insert  into `dss_workflow_node_ui`(`id`,`key`,`description`,`description_en`,`lable_name`,`lable_name_en`,`ui_type`,`required`,`value`,`default_value`,`is_hidden`,`condition`,`is_advanced`,`order`,`node_menu_type`,`is_base_info`,`position`) values (23,'msg.name','消息名称，必须与eventreceiver完全一致','The message name must be exactly the same as the eventreceiver','msg.name','msg.name','Input',1,NULL,NULL,0,NULL,0,1,1,0,'runtime');
insert  into `dss_workflow_node_ui`(`id`,`key`,`description`,`description_en`,`lable_name`,`lable_name_en`,`ui_type`,`required`,`value`,`default_value`,`is_hidden`,`condition`,`is_advanced`,`order`,`node_menu_type`,`is_base_info`,`position`) values (24,'msg.body','请正确填写消息内容','Please enter the message content correctly','msg.body','msg.body','Text',0,NULL,NULL,0,NULL,0,1,1,0,'runtime');
insert  into `dss_workflow_node_ui`(`id`,`key`,`description`,`description_en`,`lable_name`,`lable_name_en`,`ui_type`,`required`,`value`,`default_value`,`is_hidden`,`condition`,`is_advanced`,`order`,`node_menu_type`,`is_base_info`,`position`) values (25,'msg.type','请正确填写消息类型','Please enter message type correctly','msg.type','msg.type','Disable',1,NULL,'RECEIVE',0,NULL,0,1,1,0,'runtime');
insert  into `dss_workflow_node_ui`(`id`,`key`,`description`,`description_en`,`lable_name`,`lable_name_en`,`ui_type`,`required`,`value`,`default_value`,`is_hidden`,`condition`,`is_advanced`,`order`,`node_menu_type`,`is_base_info`,`position`) values (26,'msg.receiver','请正确填写消息接收者','Please enter message recipients correctly','msg.receiver','msg.receiver','Input',1,NULL,NULL,0,NULL,0,1,1,0,'runtime');
insert  into `dss_workflow_node_ui`(`id`,`key`,`description`,`description_en`,`lable_name`,`lable_name_en`,`ui_type`,`required`,`value`,`default_value`,`is_hidden`,`condition`,`is_advanced`,`order`,`node_menu_type`,`is_base_info`,`position`) values (27,'query.frequency','请填写查询频率，默认10次','Please enter query frequency, 10 times by default','query.frequency','query.frequency','Disable',0,NULL,'10',0,NULL,0,1,1,0,'runtime');
insert  into `dss_workflow_node_ui`(`id`,`key`,`description`,`description_en`,`lable_name`,`lable_name_en`,`ui_type`,`required`,`value`,`default_value`,`is_hidden`,`condition`,`is_advanced`,`order`,`node_menu_type`,`is_base_info`,`position`) values (28,'max.receive.hours','请填写等待时间，默认1小时','Please enter waiting time, 1 hour by default','max.receive.hours','max.receive.hours','Input',0,NULL,'12',0,NULL,0,1,1,0,'runtime');
insert  into `dss_workflow_node_ui`(`id`,`key`,`description`,`description_en`,`lable_name`,`lable_name_en`,`ui_type`,`required`,`value`,`default_value`,`is_hidden`,`condition`,`is_advanced`,`order`,`node_menu_type`,`is_base_info`,`position`) values (29,'msg.savekey','消息共享key值，默认msg.body','The ky of message content, msg.body by default','msg.savekey','msg.savekey','Input',0,NULL,'msg.body',0,NULL,0,1,1,0,'runtime');
insert  into `dss_workflow_node_ui`(`id`,`key`,`description`,`description_en`,`lable_name`,`lable_name_en`,`ui_type`,`required`,`value`,`default_value`,`is_hidden`,`condition`,`is_advanced`,`order`,`node_menu_type`,`is_base_info`,`position`) values (30,'only.receive.today',NULL,NULL,'only.receive.today','only.receive.today','Input',0,NULL,'true',0,NULL,0,1,1,0,'runtime');
insert  into `dss_workflow_node_ui`(`id`,`key`,`description`,`description_en`,`lable_name`,`lable_name_en`,`ui_type`,`required`,`value`,`default_value`,`is_hidden`,`condition`,`is_advanced`,`order`,`node_menu_type`,`is_base_info`,`position`) values (31,'source.type','请选择数据来源','Please choose the data source','source.type','source.type','Select',1,'[\"hivedb\",\"maskdb\"]',NULL,0,NULL,0,1,1,0,'runtime');
insert  into `dss_workflow_node_ui`(`id`,`key`,`description`,`description_en`,`lable_name`,`lable_name_en`,`ui_type`,`required`,`value`,`default_value`,`is_hidden`,`condition`,`is_advanced`,`order`,`node_menu_type`,`is_base_info`,`position`) values (32,'check.object','比如：db.tb{ds=${run_date}}','Please enter the name of data dependency','check.object','check.object','Input',1,NULL,NULL,0,NULL,0,1,1,0,'runtime');
insert  into `dss_workflow_node_ui`(`id`,`key`,`description`,`description_en`,`lable_name`,`lable_name_en`,`ui_type`,`required`,`value`,`default_value`,`is_hidden`,`condition`,`is_advanced`,`order`,`node_menu_type`,`is_base_info`,`position`) values (33,'max.check.hours',NULL,NULL,'max.check.hours','max.check.hours','Input',0,NULL,'1',0,NULL,0,1,1,0,'runtime');
insert  into `dss_workflow_node_ui`(`id`,`key`,`description`,`description_en`,`lable_name`,`lable_name_en`,`ui_type`,`required`,`value`,`default_value`,`is_hidden`,`condition`,`is_advanced`,`order`,`node_menu_type`,`is_base_info`,`position`) values (34,'job.desc','请正确填写多源配置','Please enter multi-source configuration correctly','job.desc','job.desc','Text',0,NULL,NULL,0,NULL,0,1,1,0,'runtime');
insert  into `dss_workflow_node_ui`(`id`,`key`,`description`,`description_en`,`lable_name`,`lable_name_en`,`ui_type`,`required`,`value`,`default_value`,`is_hidden`,`condition`,`is_advanced`,`order`,`node_menu_type`,`is_base_info`,`position`) values (35,'filter',NULL,NULL,'过滤条件','Filter','Input',0,NULL,NULL,0,NULL,0,1,1,0,'runtime');
insert  into `dss_workflow_node_ui`(`id`,`key`,`description`,`description_en`,`lable_name`,`lable_name_en`,`ui_type`,`required`,`value`,`default_value`,`is_hidden`,`condition`,`is_advanced`,`order`,`node_menu_type`,`is_base_info`,`position`) values (37,'upStreams','请选择上游节点','Please select upstream node','绑定上游节点','Bind front node','Binding',1,'[\"*\"]','empty',0,NULL,0,3,0,1,'node');
insert  into `dss_workflow_node_ui`(`id`,`key`,`description`,`description_en`,`lable_name`,`lable_name_en`,`ui_type`,`required`,`value`,`default_value`,`is_hidden`,`condition`,`is_advanced`,`order`,`node_menu_type`,`is_base_info`,`position`) values (39,'msg.topic','消息主题，必须与eventsender完全一致','Message subject must be exactly the same as eventsender','msg.topic','msg.topic','Input',1,NULL,NULL,0,NULL,0,1,1,0,'runtime');
insert  into `dss_workflow_node_ui`(`id`,`key`,`description`,`description_en`,`lable_name`,`lable_name_en`,`ui_type`,`required`,`value`,`default_value`,`is_hidden`,`condition`,`is_advanced`,`order`,`node_menu_type`,`is_base_info`,`position`) values (40,'msg.name','消息名称，必须与eventsender完全一致','The message name must be exactly the same as the eventsender ','msg.name','msg.name','Input',1,NULL,NULL,0,NULL,0,1,1,0,'runtime');
insert  into `dss_workflow_node_ui`(`id`,`key`,`description`,`description_en`,`lable_name`,`lable_name_en`,`ui_type`,`required`,`value`,`default_value`,`is_hidden`,`condition`,`is_advanced`,`order`,`node_menu_type`,`is_base_info`,`position`) values (41,'executeUser','请填写执行用户','Please enter execute user','执行用户','executeUser','Input',1,NULL,NULL,0,NULL,0,1,1,0,'runtime');
insert  into `dss_workflow_node_ui`(`id`,`key`,`description`,`description_en`,`lable_name`,`lable_name_en`,`ui_type`,`required`,`value`,`default_value`,`is_hidden`,`condition`,`is_advanced`,`order`,`node_menu_type`,`is_base_info`,`position`) values (42,'Filter','请填写过滤条件','Please enter filter','过滤条件','Filter','Input',1,NULL,NULL,0,NULL,0,1,1,0,'runtime');
INSERT  INTO `dss_workflow_node_ui`(`id`,`key`,`description`,`description_en`,`lable_name`,`lable_name_en`,`ui_type`,`required`,`value`,`default_value`,`is_hidden`,`condition`,`is_advanced`,`order`,`node_menu_type`,`is_base_info`,`position`) values (45,'ReuseEngine','请选择是否复用引擎','Please choose to reuse engin or not','是否复用引擎','reuse-engine-or-not','Select',1,'[\"true\",\"false\"]','true',0,NULL,0,1,1,0,'startup');
INSERT  INTO `dss_workflow_node_ui`(`id`,`key`,`description`,`description_en`,`lable_name`,`lable_name_en`,`ui_type`,`required`,`value`,`default_value`,`is_hidden`,`condition`,`is_advanced`,`order`,`node_menu_type`,`is_base_info`,`position`) values (46,'wds.linkis.engineconn.java.driver.memory','请填写引擎内存','please input driver memory','wds.linkis.engineconn.java.driver.memory','wds-linkis-engineconn.java.driver.memory','Input',0,NULL,'1G',0,NULL,0,1,1,0,'runtime');

DELETE FROM dss_workflow_node_to_ui;
select @workflow_node_sql:=id from dss_workflow_node where name='sql';
select @workflow_node_python:=id from dss_workflow_node where name='python';
select @workflow_node_pyspark:=id from dss_workflow_node where name='pyspark';
select @workflow_node_scala:=id from dss_workflow_node where name='scala';
select @workflow_node_hql:=id from dss_workflow_node where name='hql';
select @workflow_node_shell:=id from dss_workflow_node where name='shell';
select @workflow_node_jdbc:=id from dss_workflow_node where name='jdbc';
select @workflow_node_connector:=id from dss_workflow_node where name='connector';
select @workflow_node_subFlow:=id from dss_workflow_node where name='subFlow';

select @node_ui_title:=id from dss_workflow_node_ui where `key`='title' limit 1;
select @node_ui_desc:=id from dss_workflow_node_ui where `key`='desc' limit 1;
select @node_ui_businessTag:=id from dss_workflow_node_ui where `key`='businessTag';
select @node_ui_appTag:=id from dss_workflow_node_ui where `key`='appTag';
select @node_ui_spark_driver_memory:=id from dss_workflow_node_ui where `key`='spark.driver.memory';
select @node_ui_spark_executor_memory:=id from dss_workflow_node_ui where `key`='spark.executor.memory';
select @node_ui_spark_executor_cores:=id from dss_workflow_node_ui where `key`='spark.executor.cores';
select @node_ui_spark_executor_instances:=id from dss_workflow_node_ui where `key`='spark.executor.instances';
select @node_ui_wds_linkis_rm_yarnqueue:=id from dss_workflow_node_ui where `key`='wds.linkis.rm.yarnqueue';
select @node_ui_resources:=id from dss_workflow_node_ui where `key`='resources';
select @node_ui_category:=id from dss_workflow_node_ui where `key`='category';
select @node_ui_subject:=id from dss_workflow_node_ui where `key`='subject';
select @node_ui_content:=id from dss_workflow_node_ui where `key`='content';
select @node_ui_to:=id from dss_workflow_node_ui where `key`='to';
select @node_ui_cc:=id from dss_workflow_node_ui where `key`='cc';
select @node_ui_bcc:=id from dss_workflow_node_ui where `key`='bcc';
select @node_ui_itsm:=id from dss_workflow_node_ui where `key`='itsm';
select @node_ui_msg_sender:=id from dss_workflow_node_ui where `key`='msg.sender';
select @node_ui_msg_body:=id from dss_workflow_node_ui where `key`='msg.body';
select @node_ui_msg_receiver:=id from dss_workflow_node_ui where `key`='msg.receiver';
select @node_ui_query_frequency:=id from dss_workflow_node_ui where `key`='query.frequency';
select @node_ui_max_receive_hours:=id from dss_workflow_node_ui where `key`='max.receive.hours';
select @node_ui_msg_savekey:=id from dss_workflow_node_ui where `key`='msg.savekey';
select @node_ui_only_receive_today:=id from dss_workflow_node_ui where `key`='only.receive.today';
select @node_ui_source_type:=id from dss_workflow_node_ui where `key`='source.type';
select @node_ui_check_object:=id from dss_workflow_node_ui where `key`='check.object';
select @node_ui_max_check_hours:=id from dss_workflow_node_ui where `key`='max.check.hours';
select @node_ui_job_desc:=id from dss_workflow_node_ui where `key`='job.desc';
select @node_ui_upStreams:=id from dss_workflow_node_ui where `key`='upStreams';
select @node_ui_executeUser:=id from dss_workflow_node_ui where `key`='executeUser';
select @node_ui_ReuseEngine:=id from dss_workflow_node_ui where `key`='ReuseEngine';
select @node_ui_DriverMemory:=id from dss_workflow_node_ui where `key`='wds.linkis.engineconn.java.driver.memory';

insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_sql,@node_ui_title);
insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_sql,@node_ui_desc);
insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_sql,@node_ui_businessTag);
insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_sql,@node_ui_appTag);
insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_sql,@node_ui_spark_driver_memory);
insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_sql,@node_ui_spark_executor_memory);
insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_sql,@node_ui_spark_executor_cores);
insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_sql,@node_ui_spark_executor_instances);
insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_sql,@node_ui_wds_linkis_rm_yarnqueue);
insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_sql,@node_ui_resources);
INSERT  INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) VALUES (@workflow_node_sql,@node_ui_ReuseEngine);

insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_python,@node_ui_title);
insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_python,@node_ui_desc);
insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_python,@node_ui_businessTag);
insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_python,@node_ui_appTag);
INSERT  INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) VALUES (@workflow_node_python,@node_ui_ReuseEngine);

insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_pyspark,@node_ui_title);
insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_pyspark,@node_ui_desc);
insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_pyspark,@node_ui_businessTag);
insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_pyspark,@node_ui_appTag);
insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_pyspark,@node_ui_spark_driver_memory);
insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_pyspark,@node_ui_spark_executor_memory);
insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_pyspark,@node_ui_spark_executor_cores);
insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_pyspark,@node_ui_spark_executor_instances);
insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_pyspark,@node_ui_wds_linkis_rm_yarnqueue);
insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_pyspark,@node_ui_resources);
INSERT  INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) VALUES (@workflow_node_pyspark,@node_ui_ReuseEngine);

insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_scala,@node_ui_title);
insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_scala,@node_ui_desc);
insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_scala,@node_ui_businessTag);
insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_scala,@node_ui_appTag);
insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_scala,@node_ui_spark_driver_memory);
insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_scala,@node_ui_spark_executor_memory);
insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_scala,@node_ui_spark_executor_cores);
insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_scala,@node_ui_spark_executor_instances);
insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_scala,@node_ui_wds_linkis_rm_yarnqueue);
INSERT  INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) VALUES (@workflow_node_scala,@node_ui_ReuseEngine);

insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_hql,@node_ui_title);
insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_hql,@node_ui_desc);
insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_hql,@node_ui_businessTag);
insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_hql,@node_ui_appTag);
insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_hql,@node_ui_wds_linkis_rm_yarnqueue);
INSERT  INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) VALUES (@workflow_node_hql,@node_ui_ReuseEngine);

insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_shell,@node_ui_title);
insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_shell,@node_ui_desc);
insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_shell,@node_ui_businessTag);
insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_shell,@node_ui_appTag);
INSERT  INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) VALUES (@workflow_node_shell,@node_ui_ReuseEngine);

insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_jdbc,@node_ui_title);
insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_jdbc,@node_ui_desc);
insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_jdbc,@node_ui_businessTag);
insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_jdbc,@node_ui_appTag);
INSERT  INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) VALUES (@workflow_node_jdbc,@node_ui_ReuseEngine);

insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_connector,@node_ui_title);
insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_connector,@node_ui_desc);
insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_connector,@node_ui_businessTag);
insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_connector,@node_ui_appTag);
INSERT  INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) VALUES (@workflow_node_connector,@node_ui_ReuseEngine);

insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_subFlow,@node_ui_title);
insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_subFlow,@node_ui_desc);
insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_subFlow,@node_ui_businessTag);
insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_subFlow,@node_ui_appTag);
INSERT  INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) VALUES (@workflow_node_subFlow,@node_ui_ReuseEngine);
insert  into `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@workflow_node_hql,@node_ui_DriverMemory);

DELETE FROM dss_workflow_node_ui_validate;
insert into `dss_workflow_node_ui_validate` (`id`, `validate_type`, `validate_range`, `error_msg`, `error_msg_en`, `trigger`) values('7','NumInterval','[1,15]','驱动器内存大小，默认值：2','Drive memory size, default value: 2','blur');
insert into `dss_workflow_node_ui_validate` (`id`, `validate_type`, `validate_range`, `error_msg`, `error_msg_en`, `trigger`) values('8','NumInterval','[3,15]','执行器内存大小，默认值：3','Actuator memory size, default value: 3','blur');
insert into `dss_workflow_node_ui_validate` (`id`, `validate_type`, `validate_range`, `error_msg`, `error_msg_en`, `trigger`) values('9','NumInterval','[1,10]','执行器核心个数，默认值：1','Number of cores per executor, default value : 1','blur');
insert into `dss_workflow_node_ui_validate` (`id`, `validate_type`, `validate_range`, `error_msg`, `error_msg_en`, `trigger`) values('10','NumInterval','[1,40]','执行器个数，默认值：2','Number of per executor, default value : 2','blur');
insert into `dss_workflow_node_ui_validate` (`id`, `validate_type`, `validate_range`, `error_msg`, `error_msg_en`, `trigger`) values('13','OFT','[\"node\"]','请选择类型','Please select type','change');
insert into `dss_workflow_node_ui_validate` (`id`, `validate_type`, `validate_range`, `error_msg`, `error_msg_en`, `trigger`) values('25','OFT','[\"RECEIVE\"]','','Please select ','change');
insert into `dss_workflow_node_ui_validate` (`id`, `validate_type`, `validate_range`, `error_msg`, `error_msg_en`, `trigger`) values('27','NumInterval','[1,1000]','请填写查询频率，默认10次，范围：1-1000','Please fill in the inquiry frequency, default : 10, range is 1 to 1000','blur');
insert into `dss_workflow_node_ui_validate` (`id`, `validate_type`, `validate_range`, `error_msg`, `error_msg_en`, `trigger`) values('28','NumInterval','[1,1000]','请填写等待时间，默认1小时，范围：1-1000','Please enter waiting time, 1 hour by default, range is 1 to 1000','blur');
insert into `dss_workflow_node_ui_validate` (`id`, `validate_type`, `validate_range`, `error_msg`, `error_msg_en`, `trigger`) values('31','OFT','[\"hivedb\",\"maskdb\"]','','Invalid format,example:ProjectName@WFName@jobName','blur');
insert into `dss_workflow_node_ui_validate` (`id`, `validate_type`, `validate_range`, `error_msg`, `error_msg_en`, `trigger`) values('32','Regex','^[^\\u4e00-\\u9fa5]+$','此值不能输入中文','Chinese characters are not allowed','blur');
insert into `dss_workflow_node_ui_validate` (`id`, `validate_type`, `validate_range`, `error_msg`, `error_msg_en`, `trigger`) values('40','Regex','^[a-zA-Z]([^.]*\\.[^.]*){1,}$','需要检查的数据源dbname.tablename{partition}','Checked data source dbname.tablename{partition}','blur');
insert into `dss_workflow_node_ui_validate` (`id`, `validate_type`, `validate_range`, `error_msg`, `error_msg_en`, `trigger`) values('41','Regex','^[\\S\\n\\s]{0,500}$','长度在1到500个字符','The length is between 1 and 500 characters','blur');
insert into `dss_workflow_node_ui_validate` (`id`, `validate_type`, `validate_range`, `error_msg`, `error_msg_en`, `trigger`) values('44','Regex','^[a-zA-Z][a-zA-Z0-9_@-]*$','必须以字母开头，且只支持字母、数字、下划线、@、中横线','Started with alphabetic characters, only alphanumeric characters, underscore(_), @ and hyphen(-) are allowed','blur');
insert into `dss_workflow_node_ui_validate` (`id`, `validate_type`, `validate_range`, `error_msg`, `error_msg_en`, `trigger`) values('45','Regex','^[a-zA-Z0_9-]([^@]*@[^@]*){2}[a-zA-Z\\d]$','此值格式错误，例如：ProjectName@WFName@jobName','Invalid format,example:ProjectName@WFName@jobName','blur');
insert into `dss_workflow_node_ui_validate` (`id`, `validate_type`, `validate_range`, `error_msg`, `error_msg_en`, `trigger`) values('46','Regex','^[a-zA-Z0_9-]([^_]*_[^_]*){2}[a-zA-Z\\d]$','此值格式错误，例如：bdp_tac_name','Invalid format,example:bdp_tac_name','blur');
insert into `dss_workflow_node_ui_validate` (`id`, `validate_type`, `validate_range`, `error_msg`, `error_msg_en`, `trigger`) values('47','Regex','^.{1,128}$','长度在1到128个字符','The length is between 1 and 128 characters','blur');
insert into `dss_workflow_node_ui_validate` (`id`, `validate_type`, `validate_range`, `error_msg`, `error_msg_en`, `trigger`) values('48','Regex','^[a-zA-Z][a-zA-Z0-9_-]*$','必须以字母开头，且只支持字母、数字、下划线！','Started with alphabetic characters, only alphanumeric and underscore are allowed!','blur');
insert into `dss_workflow_node_ui_validate` (`id`, `validate_type`, `validate_range`, `error_msg`, `error_msg_en`, `trigger`) values('49','Regex','^[a-zA-Z0-9_\\u4e00-\\u9fa5]*$','只支持中文、字母、数字和下划线！','Only Chinese characters, alphanumeric characters and underscore are allowed in subject!','blur');
insert into `dss_workflow_node_ui_validate` (`id`, `validate_type`, `validate_range`, `error_msg`, `error_msg_en`, `trigger`) values('50','Regex','^[a-z][a-zA-Z0-9_.@;]*$','必须以字母开头，且只支持字母、数字、下划线、@、点','Must start with a letter and only letters, numbers, underscores, @, points are supported','blur');
insert into `dss_workflow_node_ui_validate` (`id`, `validate_type`, `validate_range`, `error_msg`, `error_msg_en`, `trigger`) values('51','Regex','^[0-9_.]*$','只支持数字、下划线、点','Only numbers, underscores and dots are supported','blur');
insert into `dss_workflow_node_ui_validate` (`id`, `validate_type`, `validate_range`, `error_msg`, `error_msg_en`, `trigger`) values('52','None',NULL,NULL,NULL,'blur');
insert into `dss_workflow_node_ui_validate` (`id`, `validate_type`, `validate_range`, `error_msg`, `error_msg_en`, `trigger`) values('53','OFT','[\"SEND\"]',NULL,NULL,'change');
insert into `dss_workflow_node_ui_validate` (`id`, `validate_type`, `validate_range`, `error_msg`, `error_msg_en`, `trigger`) values('54','NumInterval','[1,1000]','请填写等待时间，默认1小时，范围：1-1000','Please fill in the waiting time, default 1 hour, range: 1-1000','blur');
insert into `dss_workflow_node_ui_validate` (`id`, `validate_type`, `validate_range`, `error_msg`, `error_msg_en`, `trigger`) values('55','Required',NULL,'该值不能为空','The value cannot be empty\n\n','change');
insert into `dss_workflow_node_ui_validate` (`id`, `validate_type`, `validate_range`, `error_msg`, `error_msg_en`, `trigger`) values('56','Function','validatorTitle','节点名不能和工作流名一样','The node name cannot be the same as the workflow name',NULL);
insert into `dss_workflow_node_ui_validate` (`id`, `validate_type`, `validate_range`, `error_msg`, `error_msg_en`, `trigger`) values('57','Regex','^[a-zA-Z][a-zA-Z0-9_.-]*$','必须以字母开头，且只支持字母、数字、下划线、点！','It must start with a letter and only supports letters, numbers, underscores and dots!','blur');
insert into `dss_workflow_node_ui_validate` (`id`, `validate_type`, `validate_range`, `error_msg`, `error_msg_en`, `trigger`) values('58','Regex','(.+)@(.+)@(.+)','此格式错误，例如：ProjectName@WFName@jobName','Invalid format,example:ProjectName@WFName@jobName','blur');
INSERT INTO `dss_workflow_node_ui_validate` (`id`, `validate_type`, `validate_range`, `error_msg`, `error_msg_en`, `trigger`) values('59','OFT','["true","false"]','请填写是否复用引擎，false：不复用，true：复用','Please fill in whether or not to reuse engine, true: reuse, false: not reuse','blur');
insert into `dss_workflow_node_ui_validate` (`id`, `validate_type`, `validate_range`, `error_msg`, `error_msg_en`, `trigger`) values('60', 'Regex', '^[0-9.]*g{0,1}$', 'Spark内存设置如2g', 'Drive memory size, default value: 2', 'blur');
insert into `dss_workflow_node_ui_validate` (`id`, `validate_type`, `validate_range`, `error_msg`, `error_msg_en`, `trigger`) values('61','Regex','^(.|\s){1,5000}$','长度在1到5000个字符','The length is between 1 and 5000 characters','blur');
insert into `dss_workflow_node_ui_validate` (`id`, `validate_type`, `validate_range`, `error_msg`, `error_msg_en`, `trigger`) values('62','Regex','^.{1,150}$','长度在1到150个字符','The length is between 1 and 150 characters','blur');
insert into `dss_workflow_node_ui_validate` (`id`, `validate_type`, `validate_range`, `error_msg`, `error_msg_en`, `trigger`) values('63', 'Regex', '^([1-9]|10|[1-9])(g|G){0,1}$', '设置范围为[1,10],设置超出限制', 'hive memory limit 1,10', 'blur');

DELETE FROM dss_workflow_node_ui_to_validate;
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_source_type,31);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_source_type,55);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_check_object,32);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_check_object,40);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_check_object,41);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_check_object,55);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_max_receive_hours,28);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_query_frequency,27);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_desc,32);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_desc,41);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_msg_sender,44);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_msg_sender,45);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_msg_sender,41);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (25,25);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (25,55);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_msg_receiver,32);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_msg_receiver,41);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_msg_receiver,55);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_msg_receiver,58);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (21,32);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (21,41);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (21,46);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (21,55);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_msg_savekey,32);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_msg_savekey,41);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (23,47);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (23,48);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (23,55);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_msg_body,32);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_msg_body,41);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_spark_driver_memory,7);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_spark_executor_memory,8);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_spark_executor_cores,9);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_wds_linkis_rm_yarnqueue,41);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_wds_linkis_rm_yarnqueue,57);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_title,48);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_title,62);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_title,55);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_title,56);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_category,13);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_subject,47);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_to,50);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_cc,50);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_bcc,50);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_itsm,51);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_itsm,47);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_businessTag,52);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_appTag,52);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_resources,52);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (20,53);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (20,55);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_only_receive_today,52);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_max_check_hours,54);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_job_desc,32);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (35,52);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_msg_sender,55);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_category,55);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_to,55);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_itsm,55);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_subject,55);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_upStreams,55);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (39,32);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (39,41);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (39,46);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (39,55);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (40,47);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (40,48);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (40,55);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_content,55);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_msg_sender,58);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_executeUser,55);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (42,52);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_ReuseEngine,59);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_spark_driver_memory,60);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_spark_executor_memory,60);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_job_desc,61);
insert  into `dss_workflow_node_ui_to_validate`(`ui_id`,`validate_id`) values (@node_ui_DriverMemory,63);


DELETE FROM dss_workspace_appconn_role;
INSERT INTO `dss_workspace_appconn_role` (`workspace_id`, `appconn_id`, `role_id`, `priv`, `update_time`, `updateby`) VALUES('-1',@scriptis_appconn_id,'1','1',now(),'system');
INSERT INTO `dss_workspace_appconn_role` (`workspace_id`, `appconn_id`, `role_id`, `priv`, `update_time`, `updateby`) VALUES('-1',@scriptis_appconn_id,'2','1',now(),'system');
INSERT INTO `dss_workspace_appconn_role` (`workspace_id`, `appconn_id`, `role_id`, `priv`, `update_time`, `updateby`) VALUES('-1',@scriptis_appconn_id,'3','1',now(),'system');
INSERT INTO `dss_workspace_appconn_role` (`workspace_id`, `appconn_id`, `role_id`, `priv`, `update_time`, `updateby`) VALUES('-1',@scriptis_appconn_id,'4','1',now(),'system');
INSERT INTO `dss_workspace_appconn_role` (`workspace_id`, `appconn_id`, `role_id`, `priv`, `update_time`, `updateby`) VALUES('-1',@scriptis_appconn_id,'5','1',now(),'system');
INSERT INTO `dss_workspace_appconn_role` (`workspace_id`, `appconn_id`, `role_id`, `priv`, `update_time`, `updateby`) VALUES('-1',@scriptis_appconn_id,'6','1',now(),'system');
INSERT INTO `dss_workspace_appconn_role` (`workspace_id`, `appconn_id`, `role_id`, `priv`, `update_time`, `updateby`) VALUES('-1',@scriptis_appconn_id,'7','1',now(),'system');

INSERT INTO `dss_workspace_appconn_role` (`workspace_id`, `appconn_id`, `role_id`, `priv`, `update_time`, `updateby`) VALUES('-1',@workflow_appconn_id,'1','1',now(),'system');
INSERT INTO `dss_workspace_appconn_role` (`workspace_id`, `appconn_id`, `role_id`, `priv`, `update_time`, `updateby`) VALUES('-1',@workflow_appconn_id,'2','1',now(),'system');
INSERT INTO `dss_workspace_appconn_role` (`workspace_id`, `appconn_id`, `role_id`, `priv`, `update_time`, `updateby`) VALUES('-1',@workflow_appconn_id,'3','1',now(),'system');
INSERT INTO `dss_workspace_appconn_role` (`workspace_id`, `appconn_id`, `role_id`, `priv`, `update_time`, `updateby`) VALUES('-1',@workflow_appconn_id,'4','1',now(),'system');
INSERT INTO `dss_workspace_appconn_role` (`workspace_id`, `appconn_id`, `role_id`, `priv`, `update_time`, `updateby`) VALUES('-1',@workflow_appconn_id,'5','1',now(),'system');
INSERT INTO `dss_workspace_appconn_role` (`workspace_id`, `appconn_id`, `role_id`, `priv`, `update_time`, `updateby`) VALUES('-1',@workflow_appconn_id,'6','1',now(),'system');
INSERT INTO `dss_workspace_appconn_role` (`workspace_id`, `appconn_id`, `role_id`, `priv`, `update_time`, `updateby`) VALUES('-1',@workflow_appconn_id,'7','1',now(),'system');

INSERT INTO `dss_workspace_appconn_role` (`workspace_id`, `appconn_id`, `role_id`, `priv`, `update_time`, `updateby`) VALUES('224',@scriptis_appconn_id,'1','1',now(),'system');
INSERT INTO `dss_workspace_appconn_role` (`workspace_id`, `appconn_id`, `role_id`, `priv`, `update_time`, `updateby`) VALUES('224',@workflow_appconn_id,'1','1',now(),'system');
INSERT INTO `dss_workspace_appconn_role` (`workspace_id`, `appconn_id`, `role_id`, `priv`, `update_time`, `updateby`) VALUES('224',@apiservice_appconn_id,'1','1',now(),'system');


INSERT INTO `dss_workspace_admin_dept` (`id`, `parent_id`, `ancestors`, `dept_name`, `order_num`, `leader`, `phone`, `email`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES('100','0','0','基础科技','0','leader01','1888888888','123@qq.com','0','0','admin',now(),'admin',now());
