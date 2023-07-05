DROP TABLE IF EXISTS `dss_appconn`;
CREATE TABLE `dss_appconn` (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `appconn_name` varchar(64) UNIQUE NOT NULL COMMENT 'appconn的名称',
  `is_user_need_init` tinyint(1) DEFAULT NULL COMMENT '是否需要初始化',
  `level` int(8) DEFAULT NULL COMMENT '等级',
  `if_iframe` tinyint(1) DEFAULT NULL COMMENT '是否能iframe嵌入',
  `is_external` tinyint(1) DEFAULT NULL COMMENT '是否是外部接入应用',
  `reference` varchar(255) DEFAULT NULL COMMENT '需要关联的某一个AppConn标识',
  `class_name` varchar(255) DEFAULT NULL COMMENT '需要关联的某一个AppConn标识',
  `appconn_class_path` varchar(255) DEFAULT NULL COMMENT '需要关联的某一个AppConn标识',
  `resource` varchar(255) DEFAULT NULL COMMENT 'bml的资源ID',
  `is_micro_app` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否微应用嵌入',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_appconn_name` (`appconn_name`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COMMENT='dss appconn表';

DROP TABLE IF EXISTS `dss_appconn_instance`;
CREATE TABLE `dss_appconn_instance` (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `appconn_id` int(20) NOT NULL COMMENT 'appconn的主键',
  `label` varchar(128) NOT NULL COMMENT '实例的标签',
  `url` varchar(128) DEFAULT NULL COMMENT '访问第三方的url',
  `enhance_json` varchar(2048) DEFAULT NULL COMMENT 'json格式的配置',
  `homepage_uri` varchar(255) DEFAULT NULL COMMENT '主页uri，非URL',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='dss instance的实例表';

/*
---------------------------------------------------------------------
-------------------  DSS Orchestrator Framework ---------------------
---------------------------------------------------------------------
*/

DROP TABLE IF EXISTS `dss_orchestrator_info`;
CREATE TABLE `dss_orchestrator_info` (
   `id` bigint(20) NOT NULL AUTO_INCREMENT,
   `name` varchar(255) NOT NULL,
   `type` varchar(255) NOT NULL,
   `desc` varchar(1024) DEFAULT NULL,
   `creator` varchar(100) NOT NULL,
   `create_time` datetime DEFAULT NULL,
   `project_id` bigint(20) DEFAULT NULL,
   `uses` varchar(500) DEFAULT NULL,
   `appconn_name` varchar(1024) NOT NULL,
   `uuid` varchar(180) NOT NULL,
   `secondary_type` varchar(500) DEFAULT NULL,
   `is_published` tinyint(1) NOT NULL DEFAULT '0',
   `workspace_id` int(11) DEFAULT NULL COMMENT '空间id',
   `orchestrator_mode` varchar(100) DEFAULT NULL COMMENT '编排模式，取得的值是dss_dictionary中的dic_key(parent_key=p_arrangement_mode)',
   `orchestrator_way` varchar(256) DEFAULT NULL COMMENT '编排方式',
   `orchestrator_level` varchar(32) DEFAULT NULL COMMENT '工作流级别',
   `update_user` varchar(100) DEFAULT NULL COMMENT '更新人',
   `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
   PRIMARY KEY (`id`) USING BTREE,
   UNIQUE KEY `unique_idx_uuid` (`uuid`)
 ) ENGINE=InnoDB AUTO_INCREMENT=326 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT;


DROP TABLE IF EXISTS `dss_orchestrator_version_info`;
CREATE TABLE `dss_orchestrator_version_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `orchestrator_id` bigint(20) NOT NULL,
  `app_id` bigint(20) DEFAULT NULL,
  `source` varchar(255) DEFAULT NULL,
  `version` varchar(255) DEFAULT NULL,
  `comment` varchar(255) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `updater` varchar(32) DEFAULT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  `content` varchar(255) DEFAULT NULL,
  `context_id` varchar(200) DEFAULT NULL COMMENT '上下文ID',
  `valid_flag` INT(1) DEFAULT '1' COMMENT '版本有效标示，0:无效；1：有效',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=422 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT;

DROP TABLE IF EXISTS `dss_orchestrator_ref_orchestration_relation`;
CREATE TABLE `dss_orchestrator_ref_orchestration_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `orchestrator_id` bigint(20) NOT NULL COMMENT 'dss的编排模式id',
  `ref_project_id` bigint(20) DEFAULT NULL COMMENT '调度系统关联的工程Id',
  `ref_orchestration_id` int(11) DEFAULT NULL COMMENT '调度系统工作流的id（调用SchedulerAppConn的OrchestrationOperation服务返回的orchestrationId）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=326 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT;

/*
---------------------------------------------------------------------
-------------------  DSS Project Framework ---------------------
---------------------------------------------------------------------
*/

DROP TABLE IF EXISTS `dss_project`;
CREATE TABLE `dss_project` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `source` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT 'Source of the dss_project',
  `description` text COLLATE utf8_bin,
  `user_id` bigint(20) DEFAULT NULL,
  `username` varchar(32) COLLATE utf8_bin DEFAULT NULL,
  `workspace_id` int(11) NOT NULL DEFAULT '0',
  `create_time` datetime DEFAULT NULL,
  `create_by` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL,
  `update_by` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT '修改人',
  `org_id` bigint(20) DEFAULT NULL COMMENT 'Organization ID',
  `visibility` bit(1) DEFAULT NULL,
  `is_transfer` bit(1) DEFAULT NULL COMMENT 'Reserved word',
  `initial_org_id` bigint(20) DEFAULT NULL,
  `isArchive` bit(1) DEFAULT b'0' COMMENT 'If it is archived',
  `pic` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `star_num` int(11) DEFAULT '0',
  `product` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `application_area` tinyint(1) DEFAULT NULL,
  `business` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `is_personal` tinyint(4) NOT NULL DEFAULT '0',
  `create_by_str` varchar(256) COLLATE utf8_bin DEFAULT NULL,
  `update_by_str` varchar(256) COLLATE utf8_bin DEFAULT NULL,
  `dev_process` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '开发流程，多个以英文逗号分隔，取得的值是dss_workspace_dictionary中的dic_key(parent_key=p_develop_process)',
  `orchestrator_mode` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '编排模式，多个以英文逗号分隔，取得的值是dss_workspace_dictionary中的dic_key(parent_key=p_arrangement_mode或下面一级)',
  `visible` tinyint(4) DEFAULT '1' COMMENT '0:已删除；1：未删除(默认)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=313 DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=COMPACT;


DROP TABLE IF EXISTS `dss_project_user`;
CREATE TABLE `dss_project_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_id` int(10) NOT NULL,
  `username` varchar(32) COLLATE utf8_bin DEFAULT NULL,
  `workspace_id` bigint(20) DEFAULT NULL,
  `priv` int(20) DEFAULT NULL,
  `last_update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1859 DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=COMPACT;

DROP TABLE IF EXISTS `dss_appconn_project_relation`;
CREATE TABLE `dss_appconn_project_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) NOT NULL,
  `appconn_instance_id` bigint(20) NOT NULL,
  `appconn_instance_project_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=90 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT;

/*
---------------------------------------------------------------------
---------------------  DSS Workspace Framework ----------------------
---------------------------------------------------------------------
*/

DROP TABLE IF EXISTS `dss_workspace`;
CREATE TABLE `dss_workspace` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `label` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `create_by` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `department` varchar(255) DEFAULT NULL,
  `product` varchar(255) DEFAULT NULL,
  `source` varchar(255) DEFAULT NULL,
  `last_update_time` datetime DEFAULT NULL,
  `last_update_user` varchar(30) DEFAULT NULL COMMENT '最新修改用户',
  `workspace_type`  varchar(20) DEFAULT NULL comment '工作空间类型',
  `admin_permission` tinyint(1) DEFAULT 1 NOT NULL COMMENT '工作空间管理员是否有权限查看该空间下所有项目，1可以，0不可以',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=224 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `dss_workspace_homepage`;
CREATE TABLE `dss_workspace_homepage` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `workspace_id` int(10) NOT NULL,
  `role_id` int(20) DEFAULT NULL,
  `homepage_url` varchar(256) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1213 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `dss_workspace_dictionary`;
CREATE TABLE `dss_workspace_dictionary` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `workspace_id` int(11) DEFAULT '0' COMMENT '空间ID，默认为0，所有空间都有',
  `parent_key` varchar(200) DEFAULT '0' COMMENT '父key',
  `dic_name` varchar(200) NOT NULL COMMENT '名称',
  `dic_name_en` varchar(300) DEFAULT NULL COMMENT '名称（英文）',
  `dic_key` varchar(200) NOT NULL COMMENT 'key 相当于编码，空间是w_开头，工程是p_',
  `dic_value` varchar(500) DEFAULT NULL COMMENT 'key对应的值',
  `dic_value_en` varchar(1000) DEFAULT NULL COMMENT 'key对应的值（英文）',
  `title` varchar(200) DEFAULT NULL COMMENT '标题',
  `title_en` varchar(400) DEFAULT NULL COMMENT '标题（英文）',
  `url` varchar(200) DEFAULT NULL COMMENT 'url',
  `url_type` int(1) DEFAULT '0' COMMENT 'url类型: 0-内部系统，1-外部系统；默认是内部',
  `icon` varchar(200) DEFAULT NULL COMMENT '图标',
  `order_num` int(2) DEFAULT '1' COMMENT '序号',
  `remark` varchar(1000) DEFAULT NULL COMMENT '备注',
  `create_user` varchar(100) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user` varchar(100) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_unique_workspace_id` (`workspace_id`,`dic_key`),
  KEY `idx_parent_key` (`parent_key`),
  KEY `idx_dic_key` (`dic_key`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8 COMMENT='数据字典表';

DROP TABLE IF EXISTS `dss_workspace_role`;
CREATE TABLE `dss_workspace_role` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `workspace_id` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `front_name` varchar(255) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `description` varchar(512) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `workspace_id` (`workspace_id`,`name`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;



DROP TABLE IF EXISTS `dss_user`;
CREATE TABLE `dss_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(64) DEFAULT NULL,
  `name` varchar(64) DEFAULT NULL,
  `is_first_login` tinyint(1) DEFAULT NULL,
  `dept_id` int(11) DEFAULT NULL,
  `is_admin` tinyint(1) DEFAULT '0' COMMENT '是否管理员(1:是;0:否)',
  `email` varchar(50) DEFAULT '' COMMENT '用户邮箱',
  `phonenumber` varchar(11) DEFAULT '' COMMENT '手机号码',
  `password` varchar(100) DEFAULT '' COMMENT '密码',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=214 DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `dss_workspace_admin_dept`;
CREATE TABLE `dss_workspace_admin_dept` (
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

DROP TABLE IF EXISTS `dss_sidebar`;
CREATE TABLE `dss_sidebar` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `workspace_id` int(11) DEFAULT '0' COMMENT '空间ID，默认为0，所有空间都有',
  `name` varchar(200) DEFAULT NULL COMMENT '名称',
  `name_en` varchar(400) DEFAULT NULL COMMENT '名称(英文)',
  `title` varchar(200) DEFAULT NULL COMMENT '标题',
  `title_en` varchar(400) DEFAULT NULL COMMENT '标题（英文）',
  `type` int(1) NOT NULL COMMENT '类型: 0-知识库，1-菜单，2-常见问题',
  `order_num` int(2) DEFAULT '1' COMMENT '序号，按照这个字段顺序显示',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  `create_user` varchar(100) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user` varchar(100) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_workspace_id` (`workspace_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='侧边栏表';

DROP TABLE IF EXISTS `dss_sidebar_content`;
CREATE TABLE `dss_sidebar_content` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `workspace_id` int(11) DEFAULT '0' COMMENT '空间ID，默认为0，所有空间都有',
  `sidebar_id` int(11) NOT NULL COMMENT '侧边栏ID',
  `name` varchar(200) DEFAULT NULL COMMENT '名称',
  `name_en` varchar(400) DEFAULT NULL COMMENT '名称（英文）',
  `title` varchar(200) DEFAULT NULL COMMENT '标题',
  `title_en` varchar(400) DEFAULT NULL COMMENT '标题（英文）',
  `url` varchar(200) DEFAULT NULL COMMENT 'url',
  `url_type` int(1) DEFAULT '0' COMMENT 'url类型: 0-内部系统，1-外部系统；默认是内部',
  `icon` varchar(200) DEFAULT NULL COMMENT '图标',
  `order_num` int(2) DEFAULT '1' COMMENT '序号，按照这个字段顺序显示',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  `create_user` varchar(100) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user` varchar(100) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_sidebarws_id` (`workspace_id`,`sidebar_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COMMENT='侧边栏-内容表';

DROP TABLE IF EXISTS `dss_workspace_download_audit`;
CREATE TABLE `dss_workspace_download_audit`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `creator` varchar(255)  COMMENT '创建者',
  `tenant` varchar(255)  COMMENT '租户',
	`path` varchar(255)  COMMENT '文件路径',
	`sql` varchar(3000)  COMMENT '执行sql脚本',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
	 PRIMARY KEY (`id`)
) ENGINE = INNODB DEFAULT CHARSET = utf8 COMMENT = '文件下载审计';

/*
---------------------------------------------------------------------
---------------------------  DSS Workflow ---------------------------
---------------------------------------------------------------------
*/

DROP TABLE IF EXISTS `dss_workflow`;
CREATE TABLE `dss_workflow` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `state` tinyint(1) DEFAULT NULL,
  `source` varchar(255) DEFAULT NULL,
  `description` varchar(1024) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `creator` varchar(32) DEFAULT NULL,
  `is_root_flow` tinyint(1) DEFAULT NULL,
  `rank` int(10) DEFAULT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  `has_saved` tinyint(1) DEFAULT NULL,
  `uses` varchar(500) DEFAULT NULL,
  `bml_version` varchar(255) DEFAULT NULL,
  `resource_id` varchar(255) DEFAULT NULL,
  `linked_appconn_names` varchar(255) DEFAULT NULL,
  `dss_labels` varchar(255) DEFAULT NULL,
  `metrics` varchar(1024) NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=455 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT;

DROP TABLE IF EXISTS `dss_workflow_relation`;
CREATE TABLE `dss_workflow_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `flow_id` bigint(20) DEFAULT NULL,
  `parent_flow_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=78 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT;


DROP TABLE IF EXISTS `dss_workflow_edit_lock`;
CREATE TABLE `dss_workflow_edit_lock` (
   `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '',
   `flow_id` bigint(11) NOT NULL COMMENT '',
   `username` varchar(64) NOT NULL COMMENT '',
   `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
   `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
   `owner` varchar(128) NOT NULL COMMENT '',
   `is_expire` tinyint(1) NOT NULL DEFAULT '0' COMMENT '',
   `lock_content` varchar(512) NOT NULL COMMENT '',
   PRIMARY KEY (`id`),
   UNIQUE KEY `dss_workflow_edit_lock_flow_id_IDX` (`flow_id`) USING BTREE
 ) ENGINE=InnoDB AUTO_INCREMENT=571 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `dss_workflow_task`;
CREATE TABLE `dss_workflow_task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key, auto increment',
  `instance` varchar(50) DEFAULT NULL COMMENT 'An instance of Entrance, consists of IP address of the entrance server and port',
  `exec_id` varchar(50) DEFAULT NULL COMMENT 'execution ID, consists of jobID(generated by scheduler), executeApplicationName , creator and instance',
  `um_user` varchar(50) DEFAULT NULL COMMENT 'User name',
  `submit_user` varchar(50) DEFAULT NULL COMMENT 'submitUser name',
  `execution_code` text COMMENT 'Run script. When exceeding 6000 lines, script would be stored in HDFS and its file path would be stored in database',
  `progress` float DEFAULT NULL COMMENT 'Script execution progress, between zero and one',
  `log_path` varchar(200) DEFAULT NULL COMMENT 'File path of the log files',
  `result_location` varchar(200) DEFAULT NULL COMMENT 'File path of the result',
  `status` varchar(50) DEFAULT NULL COMMENT 'Script execution status, must be one of the following: Inited, WaitForRetry, Scheduled, Running, Succeed, Failed, Cancelled, Timeout',
  `instance_name` varchar(128) DEFAULT NULL COMMENT 'Execute task instance',
  `created_time` datetime DEFAULT NULL COMMENT 'Creation time',
  `updated_time` datetime DEFAULT NULL COMMENT 'Update time',
  `run_type` varchar(50) DEFAULT NULL COMMENT 'Further refinement of execution_application_time, e.g, specifying whether to run pySpark or SparkR',
  `err_code` int(11) DEFAULT NULL COMMENT 'Error code. Generated when the execution of the script fails',
  `err_desc` text COMMENT 'Execution description. Generated when the execution of script fails',
  `execute_application_name` varchar(200) DEFAULT NULL COMMENT 'The service a user selects, e.g, Spark, Python, R, etc',
  `request_application_name` varchar(200) DEFAULT NULL COMMENT 'Parameter name for creator',
  `script_path` varchar(200) DEFAULT NULL COMMENT 'Path of the script in workspace',
  `params` text COMMENT 'Configuration item of the parameters',
  `engine_instance` varchar(50) DEFAULT NULL COMMENT 'An instance of engine, consists of IP address of the engine server and port',
  `task_resource` varchar(1024) DEFAULT NULL,
  `engine_start_time` time DEFAULT NULL,
  `label_json` varchar(200) DEFAULT NULL COMMENT 'label json',
  PRIMARY KEY (`id`),
  KEY `created_time` (`created_time`),
  KEY `um_user` (`um_user`)
) ENGINE=InnoDB AUTO_INCREMENT=715 DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `dss_workflow_execute_info`;
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
 ) ENGINE=InnoDB AUTO_INCREMENT=471 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `dss_workflow_node`;
CREATE TABLE `dss_workflow_node` (
   `id` int(11) NOT NULL AUTO_INCREMENT,
   `name` varchar(16) DEFAULT NULL,
   `appconn_name` varchar(64) DEFAULT '-1' COMMENT 'appconn的名称，与dss_appconn这表的appconn_name名称对应',
   `node_type` varchar(255) DEFAULT NULL,
   `jump_type` int(11) DEFAULT NULL,
   `support_jump` tinyint(1) DEFAULT NULL,
   `submit_to_scheduler` tinyint(1) DEFAULT NULL,
   `enable_copy` tinyint(1) DEFAULT NULL,
   `should_creation_before_node` tinyint(1) DEFAULT NULL,
   `icon_path` varchar(255) DEFAULT NULL,
   PRIMARY KEY (`id`)
 ) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `dss_workflow_node_group`;
CREATE TABLE `dss_workflow_node_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(32) NOT NULL,
  `name_en` varchar(32) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `order` tinyint(2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `dss_workflow_node_to_group`;
CREATE TABLE `dss_workflow_node_to_group` (
  `node_id` int(11) NOT NULL,
  `group_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `dss_workflow_node_to_ui`;
CREATE TABLE `dss_workflow_node_to_ui` (
  `workflow_node_id` int(11) NOT NULL,
  `ui_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `dss_workflow_node_ui`;
CREATE TABLE `dss_workflow_node_ui` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `key` varchar(64) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `description_en` varchar(255) DEFAULT NULL,
  `lable_name` varchar(64) NOT NULL,
  `lable_name_en` varchar(64) NOT NULL,
  `ui_type` varchar(16) NOT NULL,
  `required` tinyint(1) NOT NULL,
  `value` varchar(255) DEFAULT NULL,
  `default_value` varchar(255) DEFAULT NULL,
  `is_hidden` tinyint(1) NOT NULL,
  `condition` varchar(255) DEFAULT NULL,
  `is_advanced` tinyint(1) NOT NULL,
  `order` tinyint(2) NOT NULL,
  `node_menu_type` tinyint(1) NOT NULL,
  `is_base_info` tinyint(1) NOT NULL,
  `position` varchar(32) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `dss_workflow_node_ui_to_validate`;
CREATE TABLE `dss_workflow_node_ui_to_validate` (
  `ui_id` int(11) NOT NULL,
  `validate_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `dss_workflow_node_ui_validate`;
CREATE TABLE `dss_workflow_node_ui_validate` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `validate_type` varchar(16) NOT NULL,
  `validate_range` varchar(255) DEFAULT NULL,
  `error_msg` varchar(255) DEFAULT NULL,
  `error_msg_en` varchar(255) DEFAULT NULL,
  `trigger` varchar(16) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=59 DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `dss_workspace_user_favorites_appconn`;
CREATE TABLE `dss_workspace_user_favorites_appconn` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(64) DEFAULT NULL,
  `workspace_id` bigint(20) DEFAULT '1',
  `menu_appconn_id` int(20) DEFAULT NULL,
  `order` int(2) DEFAULT NULL,
  `create_by` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `last_update_time` datetime DEFAULT NULL,
  `last_update_user` varchar(30) DEFAULT NULL,
  `type` varchar(20) NOT NULL DEFAULT "" COMMENT "dingyiding or 收藏",
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=94 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `dss_workspace_menu_role`;
CREATE TABLE `dss_workspace_menu_role` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `workspace_id` int(20) DEFAULT NULL,
  `menu_id` int(20) DEFAULT NULL,
  `role_id` int(20) DEFAULT NULL,
  `priv` int(20) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `updateby` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5263 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `dss_workspace_menu`;
CREATE TABLE `dss_workspace_menu` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) DEFAULT NULL,
  `title_en` varchar(64) DEFAULT NULL,
  `title_cn` varchar(64) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `icon` varchar(255) DEFAULT NULL,
  `order` int(2) DEFAULT NULL,
  `create_by` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `last_update_time` datetime DEFAULT NULL,
  `last_update_user` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `dss_workspace_appconn_role`;
CREATE TABLE `dss_workspace_appconn_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `workspace_id` bigint(20) DEFAULT NULL,
  `appconn_id` int(20) DEFAULT NULL,
  `role_id` int(20) DEFAULT NULL,
  `priv` int(20) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `updateby` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5103 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `dss_workspace_menu_appconn`;
CREATE TABLE `dss_workspace_menu_appconn` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `appconn_id` int(20) DEFAULT NULL,
  `menu_id` int(20) NOT NULL,
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
  `image` varchar(200) DEFAULT NULL COMMENT '图片',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `dss_workspace_user_role`;  -- use this table
CREATE TABLE `dss_workspace_user_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `workspace_id` bigint(20) DEFAULT NULL,
  `username` varchar(32) DEFAULT NULL,
  `role_id` int(20) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `update_user` varchar(32) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 comment '空间用户角色关系表';


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

DROP TABLE IF EXISTS `dss_orchestrator_copy_info`;
CREATE TABLE `dss_orchestrator_copy_info` (
        `id` VARCHAR(128) NOT NULL COMMENT '主键',
        `username` VARCHAR(128) DEFAULT NULL COMMENT '用户名',
        `type` VARCHAR(128) DEFAULT NULL COMMENT '编排类别',
        `source_orchestrator_id` INT(20) DEFAULT NULL COMMENT '源编排ID',
        `source_orchestrator_name` VARCHAR(255) DEFAULT NULL COMMENT '源编排名',
        `target_orchestrator_name` VARCHAR(255) DEFAULT NULL COMMENT '目标编排名',
        `source_project_name` VARCHAR(255) DEFAULT NULL COMMENT '源工程名',
        `target_project_name` VARCHAR(255) DEFAULT NULL COMMENT '目标工程名',
        `workspace_id` INT(20) DEFAULT NULL COMMENT '工作空间ID',
        `workflow_node_suffix` VARCHAR(255) DEFAULT NULL COMMENT '目标工作流节点后缀',
        `microserver_name` VARCHAR(128) COMMENT '微服务名',
        `exception_info` VARCHAR(128) COMMENT '异常信息',
        `status` int(1) DEFAULT 0 COMMENT '复制任务最终状态',
        `instance_name` varchar(128) DEFAULT NULL COMMENT '执行任务的实例',
        `is_copying` int(1) DEFAULT 0 COMMENT '编排是否在被复制',
        `success_node` TEXT COMMENT '复制成功节点',
        `start_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '复制开始时间',
        `end_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '复制结束时间',
        PRIMARY KEY (`id`),
        INDEX index_soi(source_orchestrator_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='编排复制信息表';

DROP TABLE IF EXISTS `dss_project_operate_record`;
CREATE TABLE `dss_project_operate_record`
(
    `id`                  bigint(20)   NOT NULL AUTO_INCREMENT,
    `record_id`           varchar(64)  NOT NULL,
    `workspace_id`        bigint(20)   NOT NULL COMMENT '空间id',
    `project_id`          bigint(20)   NOT NULL COMMENT '项目id',
    `operate_type`        int(11)      NOT NULL COMMENT '操作类型',
    `status`              int(11)      NOT NULL COMMENT '操作状态',
    `instance_name`     VARCHAR(128) DEFAULT NULL COMMENT '执行任务的实例',
    `content`             longtext DEFAULT NULL COMMENT '操作内容详情',
    `result_resource_uri` text     DEFAULT NULL COMMENT '操作结果资源的uri，是一个json',
    `creator`             varchar(100) NOT NULL,
    `create_time`         datetime     NOT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_record_id` (`record_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目操作记录表';

DROP TABLE IF EXISTS `dss_release_task`;
CREATE TABLE `dss_release_task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) NOT NULL,
  `orchestrator_version_id` bigint(20) NOT NULL,
  `orchestrator_id` bigint(20) NOT NULL,
  `release_user` varchar(128) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `status` varchar(64) DEFAULT 'init',
  `instance_name` varchar(128) DEFAULT NULL COMMENT '执行任务的实例',
  `error_msg` varchar(500) DEFAULT NULL COMMENT '发布错误信息',
  `comment` varchar(500) DEFAULT NULL COMMENT '发布描述',
  `log_msg` varchar(255) DEFAULT NULL COMMENT '日志信息或日志路径',
  `bak` varchar(255) DEFAULT NULL COMMENT '备用字段',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=605 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT;

-- 首页公告表
DROP TABLE IF EXISTS `dss_notice`;
CREATE TABLE `dss_notice`
(
    `id`          int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `content`   text DEFAULT NULL COMMENT '公告内容',
    `start_time` datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '生效时间',
    `end_time`  datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '失效时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COLLATE=utf8mb4_bin COMMENT ='首页公告内容';

DROP TABLE IF EXISTS `dss_orchestrator_job_info`;
CREATE TABLE `dss_orchestrator_job_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `job_id` varchar(64) DEFAULT NULL COMMENT 'job ID',
  `conversion_job_json` varchar(1024) DEFAULT NULL COMMENT 'job信息',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `instance_name` varchar(128) DEFAULT NULL COMMENT '执行任务的实例',
  `status` varchar(128) DEFAULT NULL COMMENT '转换任务状态',
  `error_msg` varchar(2048) DEFAULT NULL COMMENT '转换任务异常信息',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='dss_orchestrator_job_info表';

DROP TABLE IF EXISTS `dss_project_copy_task`;
CREATE TABLE IF NOT EXISTS `dss_project_copy_task` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `workspace_id` BIGINT(20) COMMENT '空间ID',
  `source_project_id` BIGINT(20) COMMENT '(源)复制工程ID',
  `source_project_name` VARCHAR(200) COMMENT '(源)复制工程名称',
  `copy_project_id` BIGINT(20) COMMENT '复制工程ID',
  `copy_project_name` VARCHAR(200) COMMENT '复制工程名称',
  `surplus_count` INT(3) COMMENT '剩余复制数量',
  `sum_count` INT(3) COMMENT '总数',
  `status` INT(1) COMMENT '状态 0:初始化，1：复制中，2：复制成功',
  `instance_name` VARCHAR(128) DEFAULT NULL COMMENT '执行任务的实例',
  `create_by` VARCHAR(200) COMMENT '创建人',
  `create_time` datetime COMMENT '创建时间',
  `update_time` datetime COMMENT '上个复制时间',
  `error_msg` text COMMENT '失败原因',
  `error_orc` VARCHAR(2048) DEFAULT '' COMMENT '拷贝异常编排',
  PRIMARY KEY (`id`)
) ENGINE=INNODB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='复制工程任务表';


-- webank内部表，待开源相关功能使用
DROP TABLE IF EXISTS `dss_streamis_proxy_user`;
CREATE TABLE `dss_streamis_proxy_user`
(
    `id`              int(11)  NOT NULL AUTO_INCREMENT,
    `user_name`       varchar(64)       DEFAULT NULL COMMENT  '实名用户名',
    `proxy_user_name` varchar(64)       DEFAULT NULL COMMENT  '代理用户名',
    `create_by`       varchar(64)       DEFAULT NULL COMMENT '创建者',
    `create_time`     datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT='流式应用代理用户表';

-- webank内部表，待开源相关功能使用
DROP TABLE IF EXISTS `dss_workspace_associate_departments`;
CREATE TABLE `dss_workspace_associate_departments` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `workspace_id` bigint(20) DEFAULT NULL,
  `departments` text DEFAULT NULL COMMENT '关联的部门-科室列表，逗号分割，若部门后不接科室则代表关联整个部门',
  `role_ids` varchar(128) DEFAULT NULL COMMENT '角色id列表，逗号分割',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `create_by` varchar(128) DEFAULT NULL,
  `update_by` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='空间自动加入用户绑定的部门科室信息';

-- 版本发布时的releaseNote信息。webank内部表，待开源相关功能使用
DROP TABLE IF EXISTS `dss_release_note_content`;
CREATE TABLE `dss_release_note_content`
(
    `id`           int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name`         varchar(200) DEFAULT NULL COMMENT '名称',
    `title`        varchar(200) DEFAULT NULL COMMENT '标题',
    `url`          varchar(300) DEFAULT NULL COMMENT 'url',
    `url_type`     int(1)       DEFAULT '1' COMMENT 'url类型: 0-内部系统，1-外部系统；默认是外部',
    `release_type` int(1)       DEFAULT '0' COMMENT '发布形式: 0-作为dss整体发布，1-单独发布scriptis；默认是dss',
    `create_time`  datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='releaseNote表';

-- 用户访问行为统计表，初期只有登录行为统计。webank内部表，待开源相关功能使用
DROP TABLE IF EXISTS `dss_user_access_audit`;
CREATE TABLE `dss_user_access_audit`
(
    `id`          int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_name`   varchar(64) DEFAULT NULL COMMENT '用户名',
    `login_count` BIGINT      DEFAULT 0 COMMENT '登录次数',
    `first_login` datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '第一次登录时间',
    `last_login`  datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '上一次登录时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_user_name` (`user_name`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='用户访问行为次数统计';

-- webank内部表，待开源相关功能使用
DROP TABLE IF EXISTS `dss_ec_release_strategy`;
CREATE TABLE `dss_ec_release_strategy`
(
    `id`          int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `strategy_id`   varchar(64) NOT NULL UNIQUE COMMENT '规则id',
    `workspace_id`   bigint(20) NOT NULL COMMENT '工作空间id',
    `name`   varchar(64) NOT NULL COMMENT '规则名',
    `description`  varchar(128) DEFAULT NULL COMMENT '规则描述',
    `queue`   varchar(128) NOT NULL UNIQUE COMMENT '关联队列',
    `trigger_condition_conf`   varchar(1024) NOT NULL COMMENT '触发条件(json)',
    `terminate_condition_conf`   varchar(1024) NOT NULL COMMENT '终止条件(json)',
    `ims_conf`   varchar(2048) NOT NULL COMMENT '告警设置(json)',
    `status`   int(1) DEFAULT 0 COMMENT '规则状态：0禁用 1开启',
    `creator`   varchar(64) NOT NULL COMMENT '创建人',
    `create_time` datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modifier`   varchar(64) NOT NULL COMMENT '修改人',
    `modify_time`  datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `execute_instance`   varchar(128) DEFAULT NULL COMMENT '最近处理该规则的服务实例',
    `execute_time`   datetime    DEFAULT NULL COMMENT '最近处理该规则的时间起点',
    PRIMARY KEY (`id`),
    KEY `idx_strategy_id` (`strategy_id`),
    KEY `idx_workspace_id` (`workspace_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COLLATE=utf8mb4_bin COMMENT ='EC自动释放规则配置';

-- 工作空间关联的队列表。webank内部表，待开源相关功能使用
DROP TABLE IF EXISTS `dss_queue_in_workspace`;
CREATE TABLE `dss_queue_in_workspace`
(
    `id`          int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `workspace_id`   bigint(20) NOT NULL COMMENT '工作空间id',
    `queue`   varchar(128) NOT NULL UNIQUE COMMENT '队列名',
    `apply_user`   varchar(64) NOT NULL COMMENT '申请人',
    `approve_id`   varchar(64) NOT NULL COMMENT '申请单号',
    `create_time`  datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COLLATE=utf8mb4_bin COMMENT ='工作空间关联的队列';

-- EC释放通知发送记录表。webank内部表，待开源相关功能使用
DROP TABLE IF EXISTS `dss_ec_release_ims_record`;
CREATE TABLE `dss_ec_release_ims_record`
(
    `id`          bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `record_id`   varchar(64) NOT NULL COMMENT '发送记录id',
    `strategy_id`   varchar(64) NOT NULL COMMENT '释放规则id',
    `workspace_id`   bigint(20) NOT NULL COMMENT '工作空间id',
    `content`   varchar(1024) NOT NULL COMMENT '发送内容',
    `status`   int(1) DEFAULT 0 COMMENT '通知状态：0未发送 1已发送  2发送失败',
    `execute_instance`   varchar(128) NOT NULL COMMENT '负责发送的服务实例',
    `create_time`  datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_time`  datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    KEY `idx_record_id` (`record_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COLLATE=utf8mb4_bin COMMENT ='EC释放通知发送记录';

-- 请求释放EC历史。webank内部表，待开源相关功能使用
DROP TABLE IF EXISTS `dss_ec_kill_history`;
CREATE TABLE `dss_ec_kill_history`
(
    `id`          bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `strategy_id`   varchar(64) NOT NULL COMMENT '释放规则id',
    `workspace_id`   bigint(20) NOT NULL COMMENT '工作空间id',
    `instance`   varchar(128) NOT NULL COMMENT '释放的EC实例名',
    `engine_type`   varchar(64) NOT NULL COMMENT 'EC类型',
    `queue`   varchar(128) NOT NULL COMMENT '队列名',
    `driver_core`   int(11) DEFAULT 0 COMMENT '本地释放核数',
    `driver_memory`   bigint(11) DEFAULT 0 COMMENT '本地释放内存，单位Byte',
    `yarn_core`   int(11) DEFAULT 0 COMMENT 'yarn释放核数',
    `yarn_memory`   bigint(11) DEFAULT 0 COMMENT 'yarn释放内存，单位Byte',
    `unlock_duration`   bigint(11) DEFAULT 0 COMMENT 'EC空闲时长,单位秒',
    `owner`    varchar(64) NOT NULL COMMENT 'EC创建者',
    `killer`    varchar(64) NOT NULL COMMENT 'EC释放触发者',
    `ec_start_time`  varchar(64)   NOT NULL COMMENT 'EC创建的时间',
    `kill_time`  datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '请求killEC的时间',
    `create_time`  datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `execute_instance`   varchar(128) DEFAULT NULL COMMENT '负责发送的服务实例',
    PRIMARY KEY (`id`),
    KEY `idx_workspace_id` (`workspace_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COLLATE=utf8mb4_bin COMMENT ='请求释放EC历史';