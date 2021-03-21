

-- ----------------------------
-- Table structure for dss_apiservice_api
-- ---------------------------
DROP TABLE IF EXISTS `dss_apiservice_api`;
CREATE TABLE `dss_apiservice_api` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(200) NOT NULL COMMENT '服务名称',
  `alias_name` varchar(200) NOT NULL COMMENT '服务中文名称',
  `path` varchar(200) NOT NULL COMMENT '服务路径',
  `protocol` int(11) NOT NULL COMMENT '协议: http, https',
  `method` varchar(10) NOT NULL COMMENT '方法： post, put, delete',
  `tag` varchar(200) DEFAULT NULL COMMENT '标签',
  `scope` varchar(50) DEFAULT NULL COMMENT '范围',
  `description` varchar(200) DEFAULT NULL COMMENT '服务描述',
  `status` int(11) DEFAULT '0' COMMENT '服务状态，默认0是停止，1是运行中，2是删除',
  `type` varchar(50) DEFAULT NULL COMMENT '服务引擎类型',
  `run_type` varchar(50) DEFAULT NULL COMMENT '脚本类型',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `creator` varchar(50) DEFAULT NULL COMMENT '创建者',
  `modifier` varchar(50) DEFAULT NULL COMMENT '修改者',
  `script_path` varchar(200) NOT NULL COMMENT '脚本路径',
  `workspaceID` int(11) NOT NULL COMMENT '工作空间ID',
  `api_comment` varchar(1024) DEFAULT NULL COMMENT '服务备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_dss_apiservice_config_name` (`name`),
  UNIQUE KEY `uniq_dss_apiservice_config_path` (`path`),
  KEY `idx_dss_apiservice_config_script_path` (`script_path`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='服务api配置表';



-- ----------------------------
-- Table structure for dss_apiservice_param
-- ----------------------------
DROP TABLE IF EXISTS `dss_apiservice_param`;
CREATE TABLE `dss_apiservice_param` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `api_version_id` bigint(20) NOT NULL COMMENT '服务api版本id',
  `name` varchar(200) NOT NULL COMMENT '名称',
  `display_name` varchar(50) DEFAULT NULL COMMENT '展示名',
  `type` varchar(50) DEFAULT NULL COMMENT '类型',
  `required` tinyint(1) DEFAULT '1' COMMENT '是否必须: 0否, 1是',
  `default_value` varchar(1024) DEFAULT NULL COMMENT '参数的默认值',
  `description` varchar(200) DEFAULT NULL COMMENT '描述',
  `details` varchar(1024) DEFAULT NULL COMMENT '变量的详细说明',
  PRIMARY KEY (`id`),
  KEY `idx_dss_apiservice_param_api_version_id` (`api_version_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='apiservice 参数表';



-- ----------------------------
-- Table structure for dss_apiservice_api_version
-- ----------------------------
DROP TABLE IF EXISTS `dss_apiservice_api_version`;
CREATE TABLE `dss_apiservice_api_version` (
   `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
   `api_id` bigint(20) NOT NULL COMMENT '服务的ID',
   `version` varchar(50) NOT NULL COMMENT '服务对应的版本信息',
   `bml_resource_id` varchar(50) NOT NULL COMMENT 'bml资源id',
   `bml_version` varchar(20) NOT NULL COMMENT 'bml版本',
   `source` varchar(200) DEFAULT NULL COMMENT '来源',
   `creator` varchar(50) DEFAULT NULL COMMENT '创建者',
   `create_time`timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   `status` tinyint(1) default '1' COMMENT '0表示被禁用，1表示正在运行',
   `metadata_info` varchar(5000) NOT NULL COMMENT '发布者库表信息',
   `auth_id` varchar(200) NOT NULL COMMENT '用于与datamap交互的UUID',
   `datamap_order_no` varchar(200) DEFAULT NULL COMMENT 'datamap审批单号码',
   PRIMARY KEY(`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='服务api版本表';



-- ----------------------------
-- Table structure for dss_apiservice_token_manager
-- ----------------------------
DROP TABLE IF EXISTS `dss_apiservice_token_manager`;
CREATE TABLE `dss_apiservice_token_manager` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `api_version_id` bigint(20) NOT NULL COMMENT '服务api版本id',
  `api_id` bigint(20) NOT NULL COMMENT '服务api配置id',
  `publisher` varchar(20) NOT NULL COMMENT '发布用户',
  `user` varchar(20) NOT NULL COMMENT '申请用户',
  `apply_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `duration` int(10) NOT NULL COMMENT '时长',
  `reason` varchar(200) DEFAULT NULL COMMENT '申请原因',
  `ip_whitelist` varchar(200) DEFAULT NULL COMMENT 'IP白名单',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态0过期，1有效期内',
  `caller` varchar(50) DEFAULT NULL COMMENT '调用方',
  `access_limit` varchar(50) DEFAULT NULL COMMENT '限流情况',
  `apply_source` varchar(200) DEFAULT NULL COMMENT '申请来源',
  `token` varchar(500) DEFAULT NULL COMMENT 'token内容',
   PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='apiservice token管理表';

-- ----------------------------
-- Table structure for dss_apiservice_approval
-- ----------------------------
DROP TABLE IF EXISTS `dss_apiservice_approval`;
CREATE TABLE `dss_apiservice_approval` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `api_id` bigint(20) NOT NULL COMMENT '服务api id',
  `api_version_id` bigint(20) NOT NULL COMMENT '版本id',
  `approval_name` varchar(50) NOT NULL COMMENT '审批单名称',
  `apply_user` varchar(1024) NOT NULL COMMENT '申请用户',
  `execute_user` varchar(50) DEFAULT NULL COMMENT '代理执行用户，用,分割',
  `creator` varchar(50) NOT NULL COMMENT '创建者',
  `status` int(10) DEFAULT '0' COMMENT '申请状态，提单成功1，审批中2，成功3，失败4',
  `create_time` timestamp NOT null DEFAULT CURRENT_TIMESTAMP COMMENT '审批单创建时间',
  `update_time` timestamp NOT null DEFAULT CURRENT_TIMESTAMP COMMENT '审批单状态更新时间',
  `approval_no` varchar(500) NOT NULL COMMENT '审批单号',
  PRIMARY KEY(`id`),
  UNIQUE KEY `uniq_dss_apiservice_api_version_id` (`api_version_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='apiservice 审批单表';


-- ----------------------------
-- Table structure for dss_apiservice_approval
-- ----------------------------
DROP TABLE IF EXISTS `dss_apiservice_access_info`;
CREATE TABLE `dss_apiservice_access_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `api_id` bigint(20) NOT NULL COMMENT '服务id',
  `api_version_id` bigint(20) NOT NULL COMMENT '版本id',
  `api_name` varchar(50) NOT NULL COMMENT '服务名称',
  `login_user` varchar(50) NOT NULL COMMENT '提交用户',
  `execute_user` varchar(50) DEFAULT NULL COMMENT '代理执行用户',
  `api_publisher` varchar(50) NOT NULL COMMENT 'api创建者',
  `access_time` timestamp NOT null DEFAULT CURRENT_TIMESTAMP COMMENT '访问时间',
  PRIMARY KEY(`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='apiservice 访问信息表';

/*Table structure for table `dss_appconn` */

DROP TABLE IF EXISTS `dss_appconn`;

CREATE TABLE `dss_appconn` (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `appconn_name` varchar(64) DEFAULT NULL COMMENT 'appconn的名称',
  `is_user_need_init` tinyint(1) DEFAULT NULL COMMENT '是否需要初始化',
  `level` int(8) DEFAULT NULL COMMENT '等级',
  `if_iframe` tinyint(1) DEFAULT NULL COMMENT '是否能iframe嵌入',
  `is_external` tinyint(1) DEFAULT NULL COMMENT '是否是外部接入应用',
  `reference` varchar(255) DEFAULT NULL COMMENT '需要关联的某一个AppConn标识',
  `class_name` varchar(255) DEFAULT NULL COMMENT '需要关联的某一个AppConn标识',
  `appconn_class_path` varchar(255) DEFAULT NULL COMMENT '需要关联的某一个AppConn标识',
  `resource` varchar(255) DEFAULT NULL COMMENT 'bml的资源ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `appconn_name` (`appconn_name`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COMMENT='dss appconn表';

/*Table structure for table `dss_appconn_instance` */

DROP TABLE IF EXISTS `dss_appconn_instance`;

CREATE TABLE `dss_appconn_instance` (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `appconn_id` int(20) NOT NULL COMMENT 'appconn的主键',
  `label` varchar(128) NOT NULL COMMENT '实例的标签',
  `url` varchar(128) DEFAULT NULL COMMENT '访问第三方的url',
  `enhance_json` varchar(1024) DEFAULT NULL COMMENT 'json格式的配置',
  `homepage_url` varchar(255) DEFAULT NULL COMMENT '主页url',
  `redirect_url` varchar(255) DEFAULT NULL COMMENT '重定向url',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='dss instance的实例表';

/*Table structure for table `dss_appconn_project_relation` */

DROP TABLE IF EXISTS `dss_appconn_project_relation`;

CREATE TABLE `dss_appconn_project_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) NOT NULL,
  `appconn_instance_id` bigint(20) NOT NULL,
  `appconn_instance_project_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=90 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT;

/*Table structure for table `dss_application` */

DROP TABLE IF EXISTS `dss_application`;

CREATE TABLE `dss_application` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) DEFAULT NULL,
  `url` varchar(128) DEFAULT NULL,
  `is_user_need_init` tinyint(1) DEFAULT NULL,
  `level` int(8) DEFAULT NULL,
  `user_init_url` varchar(255) DEFAULT NULL,
  `exists_project_service` tinyint(1) DEFAULT NULL,
  `project_url` varchar(255) DEFAULT NULL,
  `enhance_json` varchar(255) DEFAULT NULL,
  `if_iframe` tinyint(1) DEFAULT NULL,
  `homepage_url` varchar(255) DEFAULT NULL,
  `redirect_url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8;

/*Table structure for table `dss_application_user_init_result` */

DROP TABLE IF EXISTS `dss_application_user_init_result`;

CREATE TABLE `dss_application_user_init_result` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `application_id` int(11) DEFAULT NULL,
  `result` varchar(255) DEFAULT NULL,
  `username` varchar(32) DEFAULT NULL,
  `is_init_success` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `dss_component_info` */

DROP TABLE IF EXISTS `dss_component_info`;

CREATE TABLE `dss_component_info` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(128) NOT NULL,
  `icon` varchar(64) NOT NULL,
  `desc` varchar(1024) NOT NULL,
  `button_text` varchar(64) NOT NULL,
  `menu_id` int(10) NOT NULL,
  `application_id` int(10) DEFAULT '0',
  `user_manual_url` varchar(512) DEFAULT NULL,
  `indicator_url` varchar(512) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `dss_component_role` */

DROP TABLE IF EXISTS `dss_component_role`;

CREATE TABLE `dss_component_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `workspace_id` bigint(20) DEFAULT NULL,
  `component_id` int(20) DEFAULT NULL,
  `role_id` int(20) DEFAULT NULL,
  `priv` int(20) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `updateby` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5103 DEFAULT CHARSET=utf8;

/*Table structure for table `dss_datawrangler_export` */

DROP TABLE IF EXISTS `dss_datawrangler_export`;

CREATE TABLE `dss_datawrangler_export` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `spread_sheet_id` int(20) NOT NULL,
  `sheet_id` int(20) DEFAULT NULL,
  `data_sink` varchar(1000) DEFAULT NULL,
  `user_id` varchar(120) DEFAULT '0',
  `create_time` datetime DEFAULT NULL,
  `last_update_time` datetime DEFAULT NULL,
  `status` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `dss_datawrangler_sheet` */

DROP TABLE IF EXISTS `dss_datawrangler_sheet`;

CREATE TABLE `dss_datawrangler_sheet` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(120) NOT NULL,
  `spread_sheet_id` int(20) NOT NULL,
  `order` int(3) DEFAULT NULL,
  `data_source` varchar(1000) DEFAULT '0',
  `content_location` varchar(500) DEFAULT NULL,
  `operation_location` varchar(500) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `last_update_time` datetime DEFAULT NULL,
  `config` text,
  `is_limited` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

/*Table structure for table `dss_datawrangler_spreadsheet` */

DROP TABLE IF EXISTS `dss_datawrangler_spreadsheet`;

CREATE TABLE `dss_datawrangler_spreadsheet` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(120) NOT NULL,
  `source` varchar(128) DEFAULT NULL,
  `workspace` varchar(120) DEFAULT NULL,
  `is_hidden` tinyint(1) DEFAULT '0',
  `config` text,
  `description` varchar(500) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `last_update_time` datetime DEFAULT NULL,
  `last_access_time` datetime DEFAULT NULL,
  `access_num` int(10) DEFAULT NULL,
  `user_id` varchar(120) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

/*Table structure for table `dss_datawrangler_template` */

DROP TABLE IF EXISTS `dss_datawrangler_template`;

CREATE TABLE `dss_datawrangler_template` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(120) NOT NULL,
  `sheet_id` int(20) NOT NULL,
  `source` varchar(120) DEFAULT NULL,
  `workspace` varchar(120) DEFAULT '0',
  `operation_location` varchar(500) DEFAULT NULL,
  `description` varchar(500) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `last_update_time` datetime DEFAULT NULL,
  `user_id` varchar(120) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `dss_dev_flow` */

DROP TABLE IF EXISTS `dss_dev_flow`;

CREATE TABLE `dss_dev_flow` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID，默认为0，所有空间都有',
  `workspace_id` int(11) DEFAULT '0' COMMENT '空间ID，默认为0，所有空间都有',
  `type` int(1) DEFAULT '0' COMMENT '类型: 0-空间开发流程，1-工程开发流程，2-工程编排模式',
  `dev_name` varchar(200) DEFAULT NULL COMMENT '名称',
  `dev_code` varchar(200) NOT NULL COMMENT '编码，可以当做checkbox或radio中的value来使用,赋值可以当做英文名称来使用',
  `title` varchar(200) DEFAULT NULL COMMENT '标题',
  `url` varchar(200) DEFAULT NULL COMMENT 'url',
  `url_type` int(1) DEFAULT '0' COMMENT 'url类型: 0-内部系统，1-外部系统；默认是内部',
  `icon` varchar(200) DEFAULT NULL COMMENT '图标',
  `dev_desc` varchar(500) DEFAULT NULL COMMENT '描述',
  `order_num` int(2) DEFAULT '1' COMMENT '序号',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  `create_user` varchar(100) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user` varchar(100) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_unique_workspace_id` (`workspace_id`,`type`,`dev_code`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COMMENT='开发流程/编排模式等配置表';


DROP TABLE IF EXISTS `dss_dictionary`;
CREATE TABLE `dss_dictionary` (
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
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_unique_workspace_id` (`workspace_id`,`dic_key`),
  KEY `idx_parent_key` (`parent_key`),
  KEY `idx_dic_key` (`dic_key`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8 COMMENT='数据字典表';


/*Table structure for table `dss_event_relation` */

DROP TABLE IF EXISTS `dss_event_relation`;

CREATE TABLE `dss_event_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_version_id` bigint(20) NOT NULL,
  `flow_id` bigint(20) NOT NULL,
  `msg_type` varchar(45) NOT NULL,
  `msg_topic` varchar(45) NOT NULL,
  `msg_name` varchar(45) NOT NULL,
  `msg_sender` varchar(45) DEFAULT NULL,
  `msg_receiver` varchar(45) DEFAULT NULL,
  `node_json` varchar(4096) DEFAULT NULL,
  `project_id` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='save eventchecker info for application map';

/*Table structure for table `dss_flow_edit_lock` */

DROP TABLE IF EXISTS `dss_flow_edit_lock`;

CREATE TABLE `dss_flow_edit_lock` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `flow_id` bigint(11) NOT NULL,
  `flow_version` varchar(16) NOT NULL,
  `project_version_id` bigint(11) NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `owner` varchar(128) NOT NULL,
  `lock_stamp` int(8) NOT NULL DEFAULT '0',
  `is_expire` tinyint(1) NOT NULL DEFAULT '0',
  `lock_content` varchar(512) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `flow_lock` (`flow_id`,`flow_version`,`project_version_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `dss_flow_publish_history` */

DROP TABLE IF EXISTS `dss_flow_publish_history`;

CREATE TABLE `dss_flow_publish_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `flow_version_id` bigint(20) DEFAULT NULL,
  `publish_time` datetime DEFAULT NULL,
  `publisher_id` bigint(255) DEFAULT NULL,
  `alert_flag` tinyint(1) DEFAULT NULL,
  `alter_config` varchar(255) DEFAULT NULL,
  `comment` varchar(255) DEFAULT NULL,
  `state` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT;

/*Table structure for table `dss_flow_relation` */

DROP TABLE IF EXISTS `dss_flow_relation`;

CREATE TABLE `dss_flow_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `flow_id` bigint(20) DEFAULT NULL,
  `parent_flow_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=78 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT;

/*Table structure for table `dss_flow_schedule_info` */

DROP TABLE IF EXISTS `dss_flow_schedule_info`;

CREATE TABLE `dss_flow_schedule_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `flow_id` bigint(20) NOT NULL,
  `project_id` bigint(20) NOT NULL,
  `workspace_id` bigint(20) NOT NULL,
  `schedule_time` varchar(4096) COLLATE utf8_bin DEFAULT NULL,
  `alarm_level` varchar(32) COLLATE utf8_bin DEFAULT NULL,
  `alarm_user_emails` varchar(4096) COLLATE utf8_bin DEFAULT NULL,
  `last_update_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=COMPACT;

--/*Table structure for table `dss_flow_taxonomy` */

--DROP TABLE IF EXISTS `dss_flow_taxonomy`;
--
--CREATE TABLE `dss_flow_taxonomy` (
--  `id` bigint(20) NOT NULL AUTO_INCREMENT,
--  `name` varchar(20) DEFAULT NULL,
--  `description` varchar(255) DEFAULT NULL,
--  `creator` varchar(32) DEFAULT NULL,
--  `create_time` datetime DEFAULT NULL,
--  `update_time` datetime DEFAULT NULL,
--  `project_id` bigint(20) DEFAULT NULL,
--  PRIMARY KEY (`id`) USING BTREE,
--  UNIQUE KEY `name` (`name`,`project_id`) USING BTREE
--) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT;

--/*Table structure for table `dss_flow_taxonomy_relation` */
--
--DROP TABLE IF EXISTS `dss_flow_taxonomy_relation`;
--
--CREATE TABLE `dss_flow_taxonomy_relation` (
--  `taxonomy_id` bigint(20) NOT NULL,
--  `flow_id` bigint(20) NOT NULL
--) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT;

/*Table structure for table `dss_flow_user` */

DROP TABLE IF EXISTS `dss_flow_user`;

CREATE TABLE `dss_flow_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `flow_id` bigint(20) NOT NULL,
  `project_id` bigint(20) NOT NULL,
  `username` varchar(100) COLLATE utf8_bin NOT NULL,
  `workspace_id` bigint(20) NOT NULL,
  `priv` tinyint(5) NOT NULL DEFAULT '0',
  `last_update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=COMPACT;

/*Table structure for table `dss_flow_version` */

DROP TABLE IF EXISTS `dss_flow_version`;

CREATE TABLE `dss_flow_version` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `flow_id` bigint(20) DEFAULT NULL,
  `source` varchar(255) DEFAULT NULL,
  `version` varchar(255) DEFAULT NULL,
  `bml_version` varchar(255) DEFAULT NULL,
  `json_path` text,
  `comment` varchar(255) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `updator` varchar(32) DEFAULT NULL,
  `project_version_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2668 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT;

/*Table structure for table `dss_homepage_demo_instance` */

DROP TABLE IF EXISTS `dss_homepage_demo_instance`;

CREATE TABLE `dss_homepage_demo_instance` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `menu_id` int(20) DEFAULT NULL,
  `name` varchar(64) DEFAULT NULL,
  `url` varchar(128) DEFAULT NULL,
  `title_en` varchar(64) DEFAULT NULL,
  `title_cn` varchar(64) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `icon` varchar(255) DEFAULT NULL,
  `order` int(2) DEFAULT NULL,
  `click_num` int(11) DEFAULT '0',
  `create_by` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `last_update_time` datetime DEFAULT NULL,
  `last_update_user` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

/*Table structure for table `dss_homepage_demo_menu` */

DROP TABLE IF EXISTS `dss_homepage_demo_menu`;

CREATE TABLE `dss_homepage_demo_menu` (
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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

/*Table structure for table `dss_homepage_video` */

DROP TABLE IF EXISTS `dss_homepage_video`;

CREATE TABLE `dss_homepage_video` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) DEFAULT NULL,
  `url` varchar(128) DEFAULT NULL,
  `title_en` varchar(64) DEFAULT NULL,
  `title_cn` varchar(64) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `icon` varchar(255) DEFAULT NULL,
  `order` int(2) DEFAULT NULL,
  `play_num` int(11) DEFAULT '0',
  `create_by` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `last_update_time` datetime DEFAULT NULL,
  `last_update_user` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

/*Table structure for table `dss_input_relation` */

DROP TABLE IF EXISTS `dss_input_relation`;

CREATE TABLE `dss_input_relation` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `type` varchar(16) DEFAULT NULL,
  `source_env` varchar(16) DEFAULT NULL,
  `source_id` bigint(20) DEFAULT NULL,
  `target_env` varchar(16) DEFAULT NULL,
  `target_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

/*Table structure for table `dss_instance` */

DROP TABLE IF EXISTS `dss_instance`;

CREATE TABLE `dss_instance` (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `appconn_id` int(20) NOT NULL COMMENT 'appconn的主键',
  `label` varchar(128) NOT NULL COMMENT '实例的标签',
  `url` varchar(128) DEFAULT NULL COMMENT '访问第三方的url',
  `enhance_json` varchar(255) DEFAULT NULL COMMENT 'json格式的配置',
  `homepage_url` varchar(255) DEFAULT NULL COMMENT '主页url',
  `redirect_url` varchar(255) DEFAULT NULL COMMENT '重定向url',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='dss instance的实例表';

/*Table structure for table `dss_menu` */

DROP TABLE IF EXISTS `dss_menu`;

CREATE TABLE `dss_menu` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `level` varchar(255) DEFAULT NULL,
  `upper_menu_id` int(20) DEFAULT NULL,
  `front_name` varchar(255) DEFAULT NULL,
  `comment` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `is_active` tinyint(4) DEFAULT '1',
  `is_component` tinyint(1) NOT NULL DEFAULT '0',
  `icon` varchar(128) DEFAULT NULL,
  `application_id` int(11) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8;

/*Table structure for table `dss_menu_component_url` */

DROP TABLE IF EXISTS `dss_menu_component_url`;

CREATE TABLE `dss_menu_component_url` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `menu_id` int(10) NOT NULL,
  `dss_application_id` int(11) DEFAULT NULL,
  `url` varchar(512) COLLATE utf8_bin NOT NULL,
  `manul_url` varchar(512) COLLATE utf8_bin DEFAULT NULL,
  `operation_url` varchar(512) COLLATE utf8_bin DEFAULT NULL,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=COMPACT;

/*Table structure for table `dss_menu_page_relation` */

DROP TABLE IF EXISTS `dss_menu_page_relation`;

CREATE TABLE `dss_menu_page_relation` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `dss_menu_role` */

DROP TABLE IF EXISTS `dss_menu_role`;

CREATE TABLE `dss_menu_role` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `workspace_id` int(20) DEFAULT NULL,
  `menu_id` int(20) DEFAULT NULL,
  `role_id` int(20) DEFAULT NULL,
  `priv` int(20) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `updateby` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5263 DEFAULT CHARSET=utf8;

/*Table structure for table `dss_onestop_menu` */

DROP TABLE IF EXISTS `dss_onestop_menu`;

CREATE TABLE `dss_onestop_menu` (
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

/*Table structure for table `dss_onestop_menu_application` */

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
  `image` varchar(200) DEFAULT NULL COMMENT '图片',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;

/*Table structure for table `dss_onestop_menu_application_bak` */

DROP TABLE IF EXISTS `dss_onestop_menu_application_bak`;

CREATE TABLE `dss_onestop_menu_application_bak` (
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
  `image` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;

/*Table structure for table `dss_onestop_user_favorites` */

DROP TABLE IF EXISTS `dss_onestop_user_favorites`;

CREATE TABLE `dss_onestop_user_favorites` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(64) DEFAULT NULL,
  `workspace_id` bigint(20) DEFAULT '1',
  `menu_application_id` int(20) DEFAULT NULL,
  `order` int(2) DEFAULT NULL,
  `create_by` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `last_update_time` datetime DEFAULT NULL,
  `last_update_user` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=94 DEFAULT CHARSET=utf8;

/*Table structure for table `dss_orchestrator_info` */

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
   `uuid` varchar(500) NOT NULL,
   `secondary_type` varchar(500) DEFAULT NULL,
   `is_published` tinyint(1) NOT NULL DEFAULT '0',
   PRIMARY KEY (`id`) USING BTREE
 ) ENGINE=InnoDB AUTO_INCREMENT=326 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT;

/*Table structure for table `dss_orchestrator_schedule_info` */

DROP TABLE IF EXISTS `dss_orchestrator_schedule_info`;

CREATE TABLE `dss_orchestrator_schedule_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `orchestrator_id` bigint(20) NOT NULL,
  `project_name` varchar(1024) COLLATE utf8_bin NOT NULL,
  `schedule_user` varchar(128) COLLATE utf8_bin DEFAULT NULL,
  `schedule_time` varchar(4096) COLLATE utf8_bin DEFAULT NULL,
  `alarm_level` varchar(32) COLLATE utf8_bin DEFAULT NULL,
  `alarm_user_emails` varchar(4096) COLLATE utf8_bin DEFAULT NULL,
  `last_update_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=COMPACT;

/*Table structure for table `dss_orchestrator_user` */

DROP TABLE IF EXISTS `dss_orchestrator_user`;

CREATE TABLE `dss_orchestrator_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `orchestrator_id` bigint(20) NOT NULL,
  `project_id` bigint(20) NOT NULL,
  `workspace_id` int(10) NOT NULL DEFAULT '0',
  `username` varchar(100) COLLATE utf8_bin NOT NULL,
  `priv` tinyint(5) NOT NULL DEFAULT '0',
  `last_update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=58 DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=COMPACT;

/*Table structure for table `dss_orchestrator_version_info` */

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
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=422 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT;

/*Table structure for table `dss_project` */

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
  `dev_process` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '开发流程，多个以英文逗号分隔，取得的值是dss_dictionary中的dic_key(parent_key=p_develop_process)',
  `orchestrator_mode` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '编排模式，多个以英文逗号分隔，取得的值是dss_dictionary中的dic_key(parent_key=p_arrangement_mode或下面一级)',
  `visible` tinyint(4) DEFAULT '1' COMMENT '0:已删除；1：未删除(默认)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=313 DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=COMPACT;

/*Table structure for table `dss_project_applications_project` */

DROP TABLE IF EXISTS `dss_project_applications_project`;

CREATE TABLE `dss_project_applications_project` (
  `project_id` bigint(20) NOT NULL,
  `application_id` int(11) NOT NULL,
  `application_project_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*Table structure for table `dss_project_orchestrator` */

DROP TABLE IF EXISTS `dss_project_orchestrator`;

CREATE TABLE `dss_project_orchestrator` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `workspace_id` int(11) DEFAULT NULL COMMENT '空间id',
  `project_id` int(11) DEFAULT NULL COMMENT '工程id',
  `orchestrator_id` int(11) DEFAULT NULL COMMENT '编排模式id（工作流,调用orchestrator服务返回的orchestratorId）',
  `orchestrator_version_id` int(11) DEFAULT NULL COMMENT '编排模式版本id（工作流,调用orchestrator服务返回的orchestratorVersionId）',
  `orchestrator_name` varchar(100) DEFAULT NULL COMMENT '编排名称',
  `orchestrator_mode` varchar(100) DEFAULT NULL COMMENT '编排模式，取得的值是dss_dictionary中的dic_key(parent_key=p_arrangement_mode)',
  `orchestrator_way` varchar(256) DEFAULT NULL COMMENT '编排方式',
  `uses` varchar(256) DEFAULT NULL COMMENT '用途',
  `description` varchar(256) DEFAULT NULL COMMENT '描述',
  `create_user` varchar(100) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user` varchar(100) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_workspace_id` (`workspace_id`,`project_id`),
  KEY `idx_orchestrator_id` (`orchestrator_id`)
) ENGINE=InnoDB AUTO_INCREMENT=197 DEFAULT CHARSET=utf8 COMMENT='DSS编排模式信息表';

/*Table structure for table `dss_project_publish_history` */

DROP TABLE IF EXISTS `dss_project_publish_history`;

CREATE TABLE `dss_project_publish_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_version_id` bigint(20) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `creator` varchar(32) COLLATE utf8_bin DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `comment` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `state` tinyint(255) DEFAULT NULL,
  `version_path` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `expire_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `project_version_id` (`project_version_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=102 DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=COMPACT;

/*Table structure for table `dss_project_taxonomy` */

DROP TABLE IF EXISTS `dss_project_taxonomy`;

CREATE TABLE `dss_project_taxonomy` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `creator` varchar(32) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `name` (`name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT;

/*Table structure for table `dss_project_taxonomy_relation` */

DROP TABLE IF EXISTS `dss_project_taxonomy_relation`;

CREATE TABLE `dss_project_taxonomy_relation` (
  `taxonomy_id` bigint(20) NOT NULL,
  `project_id` bigint(20) NOT NULL,
  `creator` varchar(32) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT;

/*Table structure for table `dss_project_user` */

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

/*Table structure for table `dss_project_version` */

DROP TABLE IF EXISTS `dss_project_version`;

CREATE TABLE `dss_project_version` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) DEFAULT NULL,
  `version` varchar(10) COLLATE utf8_bin DEFAULT NULL,
  `comment` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `updator` varchar(32) COLLATE utf8_bin DEFAULT NULL,
  `lock` int(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=773 DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=COMPACT;

/*Table structure for table `dss_release_task` */

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
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=605 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT;

/*Table structure for table `dss_role` */

DROP TABLE IF EXISTS `dss_role`;

CREATE TABLE `dss_role` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `workspace_id` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `front_name` varchar(255) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `description` varchar(512) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `workspace_id` (`workspace_id`,`name`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;

/*Table structure for table `dss_sidebar` */

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
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_workspace_id` (`workspace_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='侧边栏表';

/*Table structure for table `dss_sidebar_content` */

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
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_sidebarws_id` (`workspace_id`,`sidebar_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COMMENT='侧边栏-内容表';

/*Table structure for table `dss_user` */

DROP TABLE IF EXISTS `dss_user`;

CREATE TABLE `dss_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(64) DEFAULT NULL,
  `name` varchar(64) DEFAULT NULL,
  `is_first_login` tinyint(1) DEFAULT NULL,
  `is_admin` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=214 DEFAULT CHARSET=utf8;

/*Table structure for table `dss_workflow` */

DROP TABLE IF EXISTS `dss_workflow`;

CREATE TABLE `dss_workflow` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) DEFAULT NULL,
  `state` tinyint(1) DEFAULT NULL,
  `source` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `creator` varchar(32) DEFAULT NULL,
  `is_root_flow` tinyint(1) DEFAULT NULL,
  `rank` int(10) DEFAULT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  `has_saved` tinyint(1) DEFAULT NULL,
  `uses` varchar(255) DEFAULT NULL,
  `bml_version` varchar(255) DEFAULT NULL,
  `resource_id` varchar(255) DEFAULT NULL,
  `linked_appconn_names` varchar(255) DEFAULT NULL,
  `dss_labels` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=455 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT;

/*Table structure for table `dss_workflow_node` */

DROP TABLE IF EXISTS `dss_workflow_node`;

CREATE TABLE `dss_workflow_node` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `icon` longtext,
  `node_type` varchar(255) DEFAULT NULL,
  `appconn_id` int(11) DEFAULT NULL,
  `submit_to_scheduler` tinyint(1) DEFAULT NULL,
  `enable_copy` tinyint(1) DEFAULT NULL,
  `should_creation_before_node` tinyint(1) DEFAULT NULL,
  `support_jump` tinyint(1) DEFAULT NULL,
  `jump_url` varchar(255) DEFAULT NULL,
  `name` varchar(16) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;

/*Table structure for table `dss_workflow_node_group` */

DROP TABLE IF EXISTS `dss_workflow_node_group`;

CREATE TABLE `dss_workflow_node_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(32) NOT NULL,
  `name_en` varchar(32) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `order` tinyint(2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

/*Table structure for table `dss_workflow_node_to_group` */

DROP TABLE IF EXISTS `dss_workflow_node_to_group`;

CREATE TABLE `dss_workflow_node_to_group` (
  `node_id` int(11) NOT NULL,
  `group_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `dss_workflow_node_to_ui` */

DROP TABLE IF EXISTS `dss_workflow_node_to_ui`;

CREATE TABLE `dss_workflow_node_to_ui` (
  `workflow_node_id` int(11) NOT NULL,
  `ui_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `dss_workflow_node_ui` */

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

/*Table structure for table `dss_workflow_node_ui_to_validate` */

DROP TABLE IF EXISTS `dss_workflow_node_ui_to_validate`;

CREATE TABLE `dss_workflow_node_ui_to_validate` (
  `ui_id` int(11) NOT NULL,
  `validate_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Table structure for table `dss_workflow_node_ui_validate` */

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

/*Table structure for table `dss_workflow_project` */

DROP TABLE IF EXISTS `dss_workflow_project`;

CREATE TABLE `dss_workflow_project` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `source` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT 'Source of the dss_project',
  `description` text COLLATE utf8_bin,
  `user_id` bigint(20) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `create_by` bigint(20) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_by` datetime DEFAULT NULL,
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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=COMPACT;

/*Table structure for table `dss_workflow_project_priv` */

DROP TABLE IF EXISTS `dss_workflow_project_priv`;

CREATE TABLE `dss_workflow_project_priv` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `workapce_id` bigint(20) DEFAULT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `priv` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `dss_workflow_task` */

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

/*Table structure for table `dss_workspace` */

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
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=224 DEFAULT CHARSET=utf8;

/*Table structure for table `dss_workspace_datasource` */

DROP TABLE IF EXISTS `dss_workspace_datasource`;

CREATE TABLE `dss_workspace_datasource` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `workspace_id` int(20) DEFAULT NULL,
  `datasource_name` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `created_time` datetime DEFAULT NULL,
  `env` varchar(255) DEFAULT NULL,
  `creater` varchar(255) DEFAULT NULL,
  `responser` varchar(255) DEFAULT NULL,
  `last_update_user` datetime DEFAULT NULL,
  `last_update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `dss_workspace_homepage` */

DROP TABLE IF EXISTS `dss_workspace_homepage`;

CREATE TABLE `dss_workspace_homepage` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `workspace_id` int(10) NOT NULL,
  `role_id` int(20) DEFAULT NULL,
  `homepage_url` varchar(256) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1213 DEFAULT CHARSET=utf8;

/*Table structure for table `dss_workspace_public_table` */

DROP TABLE IF EXISTS `dss_workspace_public_table`;

CREATE TABLE `dss_workspace_public_table` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `worksapce_id` int(20) DEFAULT NULL,
  `table_name` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `created_time` datetime DEFAULT NULL,
  `env` varchar(255) DEFAULT NULL,
  `creater` varchar(255) DEFAULT NULL,
  `responser` varchar(255) DEFAULT NULL,
  `last_update_user` datetime DEFAULT NULL,
  `last_update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `dss_workspace_role` */

DROP TABLE IF EXISTS `dss_workspace_role`;

CREATE TABLE `dss_workspace_role` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `workspace_id` int(20) DEFAULT NULL,
  `role_id` int(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `dss_workspace_user` */

DROP TABLE IF EXISTS `dss_workspace_user`;

CREATE TABLE `dss_workspace_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `workspace_id` bigint(20) DEFAULT NULL,
  `username` varchar(32) DEFAULT NULL,
  `join_time` datetime DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `workspace_id` (`workspace_id`,`username`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 comment '空间用户表';

/*Table structure for table `dss_workspace_user_role` */

DROP TABLE IF EXISTS `dss_workspace_user_role`;

CREATE TABLE `dss_workspace_user_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `workspace_id` bigint(20) DEFAULT NULL,
  `username` varchar(32) DEFAULT NULL,
  `role_id` int(20) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 comment '空间用户角色关系表';

/*Table structure for table `event_auth` */

DROP TABLE IF EXISTS `event_auth`;

CREATE TABLE `event_auth` (
  `sender` varchar(45) NOT NULL COMMENT '消息发送者',
  `topic` varchar(45) NOT NULL COMMENT '消息主题',
  `msg_name` varchar(45) NOT NULL COMMENT '消息名称',
  `record_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '插入记录时间',
  `allow_send` int(11) NOT NULL COMMENT '允许发送标志',
  PRIMARY KEY (`sender`,`topic`,`msg_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消息发送授权表';

/*Table structure for table `event_queue` */

DROP TABLE IF EXISTS `event_queue`;

CREATE TABLE `event_queue` (
  `msg_id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '消息ID号',
  `sender` varchar(45) NOT NULL COMMENT '消息发送者',
  `send_time` datetime NOT NULL COMMENT '消息发送时间',
  `topic` varchar(45) NOT NULL COMMENT '消息主题',
  `msg_name` varchar(45) NOT NULL COMMENT '消息名称',
  `msg` varchar(250) DEFAULT NULL COMMENT '消息内容',
  `send_ip` varchar(45) NOT NULL,
  PRIMARY KEY (`msg_id`)
) ENGINE=InnoDB AUTO_INCREMENT=21068 DEFAULT CHARSET=utf8 COMMENT='azkaban调取系统消息队列表';

/*Table structure for table `event_status` */

DROP TABLE IF EXISTS `event_status`;

CREATE TABLE `event_status` (
  `receiver` varchar(45) NOT NULL COMMENT '消息接收者',
  `receive_time` datetime NOT NULL COMMENT '消息接收时间',
  `topic` varchar(45) NOT NULL COMMENT '消息主题',
  `msg_name` varchar(45) NOT NULL COMMENT '消息名称',
  `msg_id` int(11) NOT NULL COMMENT '消息的最大消费id',
  PRIMARY KEY (`receiver`,`topic`,`msg_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消息消费状态表';


DROP TABLE IF EXISTS `linkis_user`;
CREATE TABLE `linkis_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(255) DEFAULT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `admin` tinyint(1) DEFAULT NULL COMMENT 'If it is an administrator',
  `active` tinyint(1) DEFAULT NULL COMMENT 'If it is active',
  `name` varchar(255) DEFAULT NULL COMMENT 'User name',
  `description` varchar(255) DEFAULT NULL,
  `department` varchar(255) DEFAULT NULL,
  `avatar` varchar(255) DEFAULT NULL COMMENT 'Path of the avator',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_by` bigint(20) DEFAULT '0',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_by` bigint(20) DEFAULT '0',
  `is_first_login` bit(1) DEFAULT NULL COMMENT 'If it is the first time to log in',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;





