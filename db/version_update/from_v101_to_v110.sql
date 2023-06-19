--- v1.0.7
CREATE TABLE `dss_workflow_execute_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `task_id` bigint(20) NOT NULL COMMENT '任务id',
  `status` int(1) DEFAULT NULL COMMENT '状态，0：失败 1：成功，',
  `flow_id` bigint(20) NOT NULL COMMENT 'flowId',
  `version` varchar(200) DEFAULT NULL COMMENT '工作流bml版本号',
  `failed_jobs` text COMMENT '执行失败节点',
  `Pending_jobs` text COMMENT '未执行节点',
  `skipped_jobs` text COMMENT '执行跳过节点',
  `succeed_jobs` text COMMENT '执行成功节点',
  `createtime` datetime NOT NULL COMMENT '创建时间',
  `running_jobs` text COMMENT '正在执行节点',
  `updatetime` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 需要先添加一条记录，先删除原来spark节点内存校验的关联关系
INSERT INTO `dss_workflow_node_ui_validate` (`validate_type`, `validate_range`, `error_msg`, `error_msg_en`, `trigger`) VALUES ('Regex', '^[0-9.]*g{0,1}$', 'Spark内存设置如2g', 'Drive memory size, default value: 2', 'blur');

 CREATE TABLE `dss_orchestrator_job_info` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `job_id` varchar(1024) DEFAULT NULL COMMENT 'job ID',
    `conversion_job_json` varchar(1024) DEFAULT NULL COMMENT 'job信息',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `instance_name` varchar(128) DEFAULT NULL COMMENT '执行任务的实例',
    `status` varchar(128) DEFAULT NULL COMMENT '转换任务状态',
    `error_msg` varchar(2048) DEFAULT NULL COMMENT '转换任务异常信息',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='dss_orchestrator_job_info表';

-------------------- v1.1.2

DROP TABLE IF EXISTS `dss_admin_dept`;
CREATE TABLE `dss_admin_dept` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '部门id',
  `parent_id` bigint(20) DEFAULT '0' COMMENT '父部门id',
  `ancestors` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT '' COMMENT '祖级列表',
  `dept_name` varchar(30) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT '' COMMENT '部门名称',
  `order_num` int(4) DEFAULT '0' COMMENT '显示顺序',
  `leader` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '负责人',
  `phone` varchar(11) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '邮箱',
  `status` char(1) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='部门表';

DROP TABLE IF EXISTS `dss_guide_group`;
CREATE TABLE IF NOT EXISTS `dss_guide_group` (
  `id` BIGINT(13) NOT NULL AUTO_INCREMENT,
  `path` VARCHAR(100) NOT NULL COMMENT '页面URL路径',
  `title` VARCHAR(255) DEFAULT NULL COMMENT '标题',
  `description` VARCHAR(200) DEFAULT NULL COMMENT '描述',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` tinyint(1) DEFAULT '0' COMMENT '0:未删除(默认), 1已删除',
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='用户向导页面';

DROP TABLE IF EXISTS `dss_guide_content`;
CREATE TABLE IF NOT EXISTS `dss_guide_content` (
  `id` BIGINT(13) NOT NULL AUTO_INCREMENT,
  `group_id` BIGINT(50) NOT NULL COMMENT '所属页面ID',
  `path` VARCHAR(100) NOT NULL COMMENT '所属页面URL路径',
  `title` VARCHAR(255) DEFAULT NULL COMMENT '标题',
  `title_alias` VARCHAR(50) DEFAULT NULL COMMENT '标题简称',
  `seq` VARCHAR(20) DEFAULT NULL COMMENT '序号',
  `type` INT(1) DEFAULT '1' COMMENT '类型: 1-步骤step，2-问题question',
  `content` TEXT DEFAULT NULL COMMENT 'Markdown格式的内容',
  `content_html` MEDIUMTEXT DEFAULT NULL COMMENT 'Markdown内容转化为HTML格式',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` tinyint(1) DEFAULT '0' COMMENT '0:未删除(默认), 1已删除',
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='用户向导页面内容详情';

DROP TABLE IF EXISTS `dss_download_audit`;
CREATE TABLE `dss_download_audit`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `creator` varchar(255)  COMMENT '创建者',
  `tenant` varchar(255)  COMMENT '租户',
	`path` varchar(255)  COMMENT '文件路径',
	`sql` varchar(3000)  COMMENT '执行sql脚本',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
	 PRIMARY KEY (`id`)
) ENGINE = INNODB DEFAULT CHARSET = utf8 COMMENT = '文件下载审计';

DROP TABLE IF EXISTS `dss_guide_catalog`;
CREATE TABLE IF NOT EXISTS `dss_guide_catalog` (
  `id` BIGINT(13) NOT NULL AUTO_INCREMENT,
  `parent_id` BIGINT(13) NOT NULL COMMENT '父级目录ID，-1代表最顶级目录',
  `title` VARCHAR(255) DEFAULT NULL COMMENT '标题',
  `description` VARCHAR(200) DEFAULT NULL COMMENT '描述',
  `create_by` VARCHAR(255) DEFAULT NULL COMMENT '创建者',
  `create_time` DATETIME DEFAULT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` VARCHAR(255) DEFAULT NULL COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` TINYINT(1) DEFAULT '0' COMMENT '0:未删除(默认), 1已删除',
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='用户向导知识库目录';

DROP TABLE IF EXISTS `dss_guide_chapter`;
CREATE TABLE IF NOT EXISTS `dss_guide_chapter` (
  `id` BIGINT(13) NOT NULL AUTO_INCREMENT,
  `catalog_id` BIGINT(13) NOT NULL COMMENT '目录ID',
  `title` VARCHAR(255) DEFAULT NULL COMMENT '标题',
  `title_alias` VARCHAR(50) DEFAULT NULL COMMENT '标题简称',
  `content` TEXT DEFAULT NULL COMMENT 'Markdown格式的内容',
  `content_html` MEDIUMTEXT DEFAULT NULL COMMENT 'Markdown转换为html内容',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` tinyint(1) DEFAULT '0' COMMENT '0:未删除(默认), 1已删除',
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='用户向导知识库文章';

DROP TABLE IF EXISTS `dss_proxy_user`;
CREATE TABLE `dss_proxy_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(64) DEFAULT NULL,
  `proxy_user_name` varchar(64) DEFAULT NULL,
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=214 DEFAULT CHARSET=utf8;

CREATE TABLE `dss_dataapi_config` (
	`id` BIGINT ( 20 ) NOT NULL AUTO_INCREMENT COMMENT '主键',
	`workspace_id` BIGINT ( 20 ) NOT NULL COMMENT '工作空间id',
	`api_name` VARCHAR ( 255 ) NOT NULL COMMENT 'API名称',
	`api_path` VARCHAR ( 255 ) NOT NULL unique COMMENT 'API Path',
	`group_id` BIGINT ( 20 ) NOT NULL COMMENT 'API组id',
	`api_type` VARCHAR ( 20 ) NOT NULL COMMENT 'API类型：GUIDE-向导模式，SQL-脚本模式',
	`protocol` VARCHAR ( 20 ) NOT NULL COMMENT 'Http协议',
	`datasource_id` BIGINT ( 20 ) NOT NULL COMMENT '数据源id',
	`datasource_name` VARCHAR ( 50 ) DEFAULT NULL COMMENT '数据源名称',
	`datasource_type` VARCHAR ( 20 ) DEFAULT NULL COMMENT '数据源类型',
	`sql` text COMMENT 'sql模板',
	`tbl_name` VARCHAR ( 100 ) DEFAULT NULL COMMENT '数据表名称',
	`req_fields` VARCHAR ( 1000 ) DEFAULT NULL COMMENT '请求字段名称',
	`res_fields` VARCHAR ( 1000 ) DEFAULT NULL COMMENT '返回字段名称',
	`order_fields` VARCHAR ( 500 ) DEFAULT NULL COMMENT '排序字段名称及方式',
	`is_test` TINYINT ( 1 ) DEFAULT '0' COMMENT '是否测试成功：0未测试(默认)，1测试成功',
	`status` TINYINT ( 1 ) DEFAULT '0' COMMENT 'API状态：0未发布(默认)，1已发布',
	`previlege` VARCHAR ( 20 ) DEFAULT 'WORKSPACE' COMMENT 'WORKSPACE,PRIVATE',
	`method` VARCHAR ( 20 ) DEFAULT NULL COMMENT 'HTTPS,HTTP',
	`describe` VARCHAR ( 255 ) DEFAULT NULL COMMENT '描述',
	`memory` INT DEFAULT NULL COMMENT '内存大小',
	`req_timeout` INT DEFAULT NULL COMMENT '请求超时时间',
	`label` VARCHAR ( 255 ) DEFAULT NULL COMMENT '标签',
	`create_by` VARCHAR ( 255 ) DEFAULT NULL COMMENT '创建者',
	`create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	`update_by` VARCHAR ( 255 ) DEFAULT NULL COMMENT '更新者',
	`update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
	`is_delete` TINYINT ( 1 ) DEFAULT '0' COMMENT '0:未删除(默认), 1已删除',
	`page_size` int  DEFAULT 0 COMMENT '每页数据大小',
	PRIMARY KEY ( `id` )
) ENGINE = INNODB DEFAULT CHARSET = utf8 COMMENT = 'API';

DROP TABLE IF EXISTS `dss_dataapi_group`;
CREATE TABLE `dss_dataapi_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `workspace_id` bigint(20) DEFAULT NULL COMMENT '工作空间id',
  `name` varchar(255) NOT NULL COMMENT '名称',
  `note` varchar(255) DEFAULT NULL COMMENT '描述',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建者',
  `create_time`  datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_delete` tinyint(1) DEFAULT '0' COMMENT '0:未删除(默认), 1已删除',
  PRIMARY KEY (`id`)
) ENGINE = INNODB DEFAULT CHARSET = utf8 COMMENT='服务组';

DROP TABLE IF EXISTS `dss_dataapi_auth`;
CREATE TABLE `dss_dataapi_auth` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `workspace_id` bigint(20) NOT NULL COMMENT '工作空间ID',
  `caller` varchar(255)  DEFAULT NULL COMMENT '调用者名称',
  `token` varchar(255)  DEFAULT NULL COMMENT 'token字符串',
  `expire` datetime DEFAULT NULL COMMENT 'token过期时间',
  `group_id` bigint(20) DEFAULT NULL COMMENT 'api组',
  `create_by` varchar(255)  DEFAULT NULL COMMENT '创建者',
  `create_time`  datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(255)  DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_delete` tinyint(1) DEFAULT '0' COMMENT '0:未删除(默认), 1已删除',
  PRIMARY KEY (`id`)
) ENGINE=INNODB  DEFAULT CHARSET=utf8 COMMENT='API认证';

DROP TABLE IF EXISTS `dss_dataapi_call`;
CREATE TABLE `dss_dataapi_call` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `api_id` bigint(11) NOT NULL COMMENT 'API ID',
  `params_value` text COMMENT '调用参数名称和值',
  `status` tinyint(255) DEFAULT NULL COMMENT '执行状态：1成功，2失败，3超时',
  `time_start` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '开始时间',
  `time_end` datetime DEFAULT NULL COMMENT '结束时间',
  `time_length` bigint(20) DEFAULT NULL COMMENT '调用时长',
  `caller` varchar(255) DEFAULT NULL COMMENT '调用者名称',
  PRIMARY KEY (`id`)
) ENGINE=INNODB  DEFAULT CHARSET=utf8 COMMENT='API调用记录'
;
DROP TABLE IF EXISTS `dss_dataapi_datasource`;
CREATE TABLE `dss_dataapi_datasource` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `workspace_id` bigint(20) DEFAULT NULL COMMENT '工作空间id',
  `name` varchar(255) NOT NULL COMMENT '名称',
  `note` varchar(255) DEFAULT NULL COMMENT '描述',
  `type` varchar(255) NOT NULL COMMENT '数据库类型',
  `url` varchar(255) NOT NULL COMMENT '连接url',
  `username` varchar(255) NOT NULL COMMENT '用户名',
  `pwd` varchar(255) NOT NULL COMMENT '密码',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` tinyint(1) DEFAULT '0' COMMENT '0:未删除(默认), 1已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='数据源';


alter table dss_onestop_user_favorites add `type` varchar(20) DEFAULT NULL COMMENT '类型,#区分收藏和盯一盯';
ALTER  TABLE dss_orchestrator_info modify column uuid varchar(100) not null;
alter table dss_workspace add `workspace_type` varchar(20);

alter table dss_user add `dept_id` int(11) DEFAULT NULL;
alter table dss_user add `email` varchar(50) DEFAULT '' COMMENT '用户邮箱';
alter table dss_user add `phonenumber` varchar(11) DEFAULT '' COMMENT '手机号码';
alter table dss_user add `password` varchar(100) DEFAULT '' COMMENT '密码';
alter table dss_user add `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）';
alter table dss_user add `create_by` varchar(64) DEFAULT NULL COMMENT '创建者';
alter table dss_user add `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';
alter table dss_user add `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
alter table dss_user add `remark` varchar(500) DEFAULT NULL COMMENT '备注';

ALTER TABLE dss_flow_edit_lock MODIFY COLUMN flow_version varchar(16) NULL;

------------------- v1.1.3
-- todo 最好先把dss库备份一份，方便查询原始数据， dss_appconn表的id改为从1001开始，避免太小和之前application_id混淆

-- todo appconn_id测试环境不超过20，生产需要确认
INSERT INTO dss_appconn
(id, appconn_name, is_user_need_init, `level`, if_iframe, is_external, reference, class_name, appconn_class_path, resource)
VALUES(22, 'scriptis', 0, 1, 0, 0, 'sso', NULL, NULL, '');
INSERT INTO dss_appconn
(id, appconn_name, is_user_need_init, `level`, if_iframe, is_external, reference, class_name, appconn_class_path, resource)
VALUES(23, 'sso', 0, 1, 0, 0, NULL, 'com.webank.wedatasphere.dss.appconn.sso.SSOAppConn', '', '');
INSERT INTO dss_appconn
(id, appconn_name, is_user_need_init, `level`, if_iframe, is_external, reference, class_name, appconn_class_path, resource)
VALUES(24, 'apiservice', 0, 1, 0, 0, 'sso', NULL, NULL, '');

-- TODO dss_appconn_instance修改
alter table dss_appconn_instance drop column redirect_url;
alter table dss_appconn_instance change column homepage_url `homepage_uri` varchar(255) DEFAULT NULL COMMENT '主页uri';

INSERT INTO dss_appconn_instance
(appconn_id, label, url, enhance_json, homepage_uri)
VALUES(22, 'DEV', '/home', '', '');
INSERT INTO dss_appconn_instance
(appconn_id, label, url, enhance_json, homepage_uri)
VALUES(24, 'DEV', '/apiservices', '', '');

update dss_appconn set id=id+1000;
update dss_appconn_instance set appconn_id=appconn_id+1000;
-- orchestrator-framework没用到了，需删除。
delete from dss_appconn where appconn_name="orchestrator-framework";
update dss_appconn set class_name="com.webank.wedatasphere.dss.appconn.workflow.WorkflowAppConn" where appconn_name="workflow";
-- todo if_iframe和is_external字段修改，先都设置为0，再逐个update
update dss_appconn set if_iframe =0,is_external =0;
update dss_appconn set if_iframe=1,is_external=1 where appconn_name="schedulis";
update dss_appconn set if_iframe=1,is_external=1 where appconn_name="visualis";
update dss_appconn set if_iframe=1,is_external=0 where appconn_name="apiservice";

alter table dss_orchestrator_info add column (`workspace_id` int(11) DEFAULT NULL COMMENT '空间id',
  `orchestrator_mode` varchar(100) DEFAULT NULL COMMENT '编排模式，取得的值是dss_dictionary中的dic_key(parent_key=p_arrangement_mode)',
  `orchestrator_way` varchar(256) DEFAULT NULL COMMENT '编排方式',
  `orchestrator_level` varchar(32) DEFAULT NULL,
  `update_user` varchar(100) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间');

update dss_orchestrator_info oi set workspace_id=(select workspace_id from dss_project_orchestrator po where po.orchestrator_id=oi.id);
update dss_orchestrator_info oi set orchestrator_mode=(select orchestrator_mode from dss_project_orchestrator po where po.orchestrator_id=oi.id);
update dss_orchestrator_info oi set orchestrator_way=(select orchestrator_way from dss_project_orchestrator po where po.orchestrator_id=oi.id);
update dss_orchestrator_info oi set update_user=(select update_user from dss_project_orchestrator po where po.orchestrator_id=oi.id);
update dss_orchestrator_info oi set update_time=(select update_time from dss_project_orchestrator po where po.orchestrator_id=oi.id);

-- dss_orchestrator_ref_orchestration_relation definition
CREATE TABLE `dss_orchestrator_ref_orchestration_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `orchestrator_id` bigint(20) NOT NULL,
  `ref_project_id` bigint(20) DEFAULT NULL,
  `ref_orchestration_id` bigint(20) DEFAULT NULL COMMENT '调度系统工作流的id(调用SchedulerAppConn的OrchestrationOperation服务返回的orchestrationId)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT;


ALTER TABLE dss_workflow_node CHANGE icon icon_path longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
-- todo 设置每个节点的jump_type和appconn_name，jump_url要先改为1，否则改变不了类型为int
update dss_workflow_node set jump_url ="1";
ALTER TABLE dss_workflow_node CHANGE jump_url jump_type INT NULL;
update dss_workflow_node set icon_path =concat("icons/",name,".icon");
-- todo 确认生产各个节点support_jump字段是否是正确的
update dss_workflow_node set appconn_name="scriptis",jump_type=2 where name in ('python','pyspark','sql','shell','scala','hql','jdbc','connector','subFlow');
update dss_workflow_node set appconn_name="visualis" where name in ('display','dashboard','widget','view');
update dss_workflow_node set appconn_name="sendemail" where name in ('sendemail');
update dss_workflow_node set appconn_name="eventchecker" where name in ('eventsender','eventreceiver');
update dss_workflow_node set appconn_name="datachecker" where name in ('datachecker');
update dss_workflow_node set jump_type=0 where name in ("eventsender","eventreceiver","datachecker");

RENAME TABLE dss_dictionary to dss_workspace_dictionary;
-- todo 检查下dss_workspace_role是否有存量数据
DROP TABLE IF EXISTS `dss_workspace_role`;
RENAME TABLE dss_role to dss_workspace_role;
RENAME TABLE dss_admin_dept to dss_workspace_admin_dept;
RENAME TABLE dss_download_audit to dss_workspace_download_audit;
RENAME TABLE dss_flow_relation to dss_workflow_relation;
RENAME TABLE dss_flow_edit_lock to dss_workflow_edit_lock;
RENAME TABLE dss_onestop_menu to dss_workspace_menu;
RENAME TABLE dss_menu_role to dss_workspace_menu_role;

RENAME TABLE dss_onestop_user_favorites to dss_workspace_user_favorites_appconn;
-- todo application_id和appconn_id对应关系修改，通过dss_onestop_menu_application的application_id到dss_application表找到组件，待确认生产数据
ALTER TABLE dss_workspace_user_favorites_appconn CHANGE menu_application_id menu_appconn_id int(20) NULL;

-- todo application_id（dss_application表）和appconn_id对应关系修改
RENAME TABLE dss_component_role to dss_workspace_appconn_role;
ALTER TABLE dss_workspace_appconn_role CHANGE component_id appconn_id int(20) NULL;
-- dss_application的linkis组件相当于scriptis
select @linkis_application_id:=id from dss_application where name='linkis';
select @scriptis_appconn_id:=id from dss_appconn where appconn_name='scriptis';
update dss_workspace_appconn_role set appconn_id=@scriptis_appconn_id where appconn_id=@linkis_application_id;

select @workflow_application_id:=id from dss_application where name='workflow';
select @workflow_appconn_id:=id from dss_appconn where appconn_name='workflow';
update dss_workspace_appconn_role set appconn_id=@workflow_appconn_id where appconn_id=@workflow_application_id;

select @visualis_application_id:=id from dss_application where name='visualis';
select @visualis_appconn_id:=id from dss_appconn where appconn_name='visualis';
update dss_workspace_appconn_role set appconn_id=@visualis_appconn_id where appconn_id=@visualis_application_id;

select @schedulis_application_id:=id from dss_application where name='schedulis';
select @schedulis_appconn_id:=id from dss_appconn where appconn_name='schedulis';
update dss_workspace_appconn_role set appconn_id=@schedulis_appconn_id where appconn_id=@schedulis_application_id;

select @qualitis_application_id:=id from dss_application where name='qualitis';
select @qualitis_appconn_id:=id from dss_appconn where appconn_name='qualitis';
update dss_workspace_appconn_role set appconn_id=@qualitis_appconn_id where appconn_id=@qualitis_application_id;

-- todo 确定生产apiservice的name
select @apiservice_application_id:=id from dss_application where name='apiService';
select @apiservice_appconn_id:=id from dss_appconn where appconn_name='apiservice';
update dss_workspace_appconn_role set appconn_id=@apiservice_appconn_id where appconn_id=@apiservice_application_id;


-- 和workspace_menu表可以dml语句插入，todo appconn_id手动修改
RENAME TABLE dss_onestop_menu_application to dss_workspace_menu_appconn;
ALTER TABLE dss_workspace_menu_appconn CHANGE application_id appconn_id int(20) NULL;
ALTER TABLE dss_workspace_menu_appconn CHANGE onestop_menu_id menu_id int(20) NULL;
-- todo 确定title_en是否正确
select @apiservice_appconn_id:=id from dss_appconn where appconn_name='apiservice';
update dss_workspace_menu_appconn set appconn_id=@apiservice_appconn_id where title_en="Data service development";

select @scriptis_appconn_id:=id from dss_appconn where appconn_name='scriptis';
update dss_workspace_menu_appconn set appconn_id=@scriptis_appconn_id where title_en="Scriptis";

select @workflow_appconn_id:=id from dss_appconn where appconn_name='workflow';
update dss_workspace_menu_appconn set appconn_id=@workflow_appconn_id where title_en="workflow";

select @visualis_appconn_id:=id from dss_appconn where appconn_name='visualis';
update dss_workspace_menu_appconn set appconn_id=@visualis_appconn_id where title_en="Visualis";

select @schedulis_appconn_id:=id from dss_appconn where appconn_name='schedulis';
update dss_workspace_menu_appconn set appconn_id=@schedulis_appconn_id where title_en="Schedulis";


-- v1.1.4 修正datachecker类名
update dss_appconn set class_name="com.webank.wedatasphere.dss.appconn.datachecker.DataCheckerAppConn" where appconn_name="datachecker";






