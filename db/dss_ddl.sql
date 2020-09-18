SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for dss_application
-- ----------------------------
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for dss_application_user_init_result
-- ----------------------------
DROP TABLE IF EXISTS `dss_application_user_init_result`;
CREATE TABLE `dss_application_user_init_result` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `application_id` int(11) DEFAULT NULL,
  `result` varchar(255) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `is_init_success` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for dss_workflow_node
-- ----------------------------
DROP TABLE IF EXISTS `dss_workflow_node`;
CREATE TABLE `dss_workflow_node` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `icon` text,
  `node_type` varchar(255) DEFAULT NULL,
  `application_id` int(20) DEFAULT NULL,
  `submit_to_scheduler` tinyint(1) DEFAULT NULL,
  `enable_copy` tinyint(1) DEFAULT NULL,
  `should_creation_before_node` tinyint(1) DEFAULT NULL,
  `support_jump` tinyint(1) DEFAULT NULL,
  `jump_url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for dss_flow
-- ----------------------------
DROP TABLE IF EXISTS `dss_flow`;
CREATE TABLE `dss_flow` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) DEFAULT NULL,
  `state` tinyint(1) DEFAULT NULL,
  `source` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `creator_id` bigint(20) DEFAULT NULL,
  `is_root_flow` tinyint(1) DEFAULT NULL,
  `rank` int(10) DEFAULT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  `has_saved` tinyint(1) DEFAULT NULL,
  `uses` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `name` (`name`,`project_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT;


-- ----------------------------
-- Table structure for dss_flow_publish_history
-- ----------------------------
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

-- ----------------------------
-- Table structure for dss_flow_relation
-- ----------------------------
DROP TABLE IF EXISTS `dss_flow_relation`;
CREATE TABLE `dss_flow_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `flow_id` bigint(20) DEFAULT NULL,
  `parent_flow_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for dss_flow_taxonomy
-- ----------------------------
DROP TABLE IF EXISTS `dss_flow_taxonomy`;
CREATE TABLE `dss_flow_taxonomy` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `creator_id` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `name` (`name`,`project_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for dss_flow_taxonomy_relation
-- ----------------------------
DROP TABLE IF EXISTS `dss_flow_taxonomy_relation`;
CREATE TABLE `dss_flow_taxonomy_relation` (
  `taxonomy_id` bigint(20) NOT NULL,
  `flow_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT;


-- ----------------------------
-- Table structure for dss_flow_version
-- ----------------------------
DROP TABLE IF EXISTS `dss_flow_version`;
CREATE TABLE `dss_flow_version` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `flow_id` bigint(20) DEFAULT NULL,
  `source` varchar(255) DEFAULT NULL,
  `version` varchar(255) DEFAULT NULL,
  `json_path` text,
  `comment` varchar(255) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `updator_id` bigint(255) DEFAULT NULL,
  `project_version_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT;


-- ----------------------------
-- Table structure for dss_project
-- ----------------------------
DROP TABLE IF EXISTS `dss_project`;
CREATE TABLE `dss_project` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `source` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT 'Source of the dss_project',
  `description` text COLLATE utf8_bin,
  `workspace_id` bigint(20) DEFAULT 1,
  `user_id` bigint(20) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `create_by` bigint(20) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_by` bigint(20) DEFAULT NULL,
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


-- ----------------------------
-- Table structure for dss_project_applications_project
-- ----------------------------
DROP TABLE IF EXISTS `dss_project_applications_project`;
CREATE TABLE `dss_project_applications_project` (
  `project_id` bigint(20) NOT NULL,
  `application_id` int(11) NOT NULL,
  `application_project_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for dss_project_publish_history
-- ----------------------------
DROP TABLE IF EXISTS `dss_project_publish_history`;
CREATE TABLE `dss_project_publish_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_version_id` bigint(20) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `creator_id` bigint(20) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `comment` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `state` tinyint(255) DEFAULT NULL,
  `version_path` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `expire_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `project_version_id` (`project_version_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for dss_project_taxonomy
-- ----------------------------
DROP TABLE IF EXISTS `dss_project_taxonomy`;
CREATE TABLE `dss_project_taxonomy` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `creator_id` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `name` (`name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for dss_project_taxonomy_relation
-- ----------------------------
DROP TABLE IF EXISTS `dss_project_taxonomy_relation`;
CREATE TABLE `dss_project_taxonomy_relation` (
  `taxonomy_id` bigint(20) NOT NULL,
  `project_id` bigint(20) NOT NULL,
  `creator_id` bigint(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT;


-- ----------------------------
-- Table structure for dss_project_version
-- ----------------------------
DROP TABLE IF EXISTS `dss_project_version`;
CREATE TABLE `dss_project_version` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) DEFAULT NULL,
  `version` varchar(10) COLLATE utf8_bin DEFAULT NULL,
  `comment` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `updator_id` int(11) DEFAULT NULL,
  `lock` int(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for dss_user
-- ----------------------------
DROP TABLE IF EXISTS `dss_user`;
CREATE TABLE `dss_user` (
  `id` int(11) NOT NULL,
  `username` varchar(64) DEFAULT NULL,
  `name` varchar(64) DEFAULT NULL,
  `is_first_login` tinyint(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for event_auth
-- ----------------------------
DROP TABLE IF EXISTS `event_auth`;
CREATE TABLE `event_auth` (
  `sender` varchar(45) NOT NULL COMMENT '消息发送者',
  `topic` varchar(45) NOT NULL COMMENT '消息主题',
  `msg_name` varchar(45) NOT NULL COMMENT '消息名称',
  `record_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '插入记录时间',
  `allow_send` int(11) NOT NULL COMMENT '允许发送标志',
  PRIMARY KEY (`sender`,`topic`,`msg_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消息发送授权表';

-- ----------------------------
-- Table structure for event_queue
-- ----------------------------
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
) ENGINE=InnoDB AUTO_INCREMENT=154465 DEFAULT CHARSET=utf8 COMMENT='azkaban调取系统消息队列表';

-- ----------------------------
-- Table structure for event_status
-- ----------------------------
DROP TABLE IF EXISTS `event_status`;
CREATE TABLE `event_status` (
  `receiver` varchar(45) NOT NULL COMMENT '消息接收者',
  `receive_time` datetime NOT NULL COMMENT '消息接收时间',
  `topic` varchar(45) NOT NULL COMMENT '消息主题',
  `msg_name` varchar(45) NOT NULL COMMENT '消息名称',
  `msg_id` int(11) NOT NULL COMMENT '消息的最大消费id',
  PRIMARY KEY (`receiver`,`topic`,`msg_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消息消费状态表';


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
  PRIMARY KEY (`id`)
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

-- ----------------------------
-- Table structure for ctyun_user
-- ----------------------------
DROP TABLE IF EXISTS `ctyun_user`;
CREATE TABLE `ctyun_user` (
  `id` char(32) NOT NULL COMMENT '天翼云userId',
  `username` varchar(64) NOT NULL COMMENT 'DSS用户名',
  `name` varchar(128) COMMENT '天翼云用户name',
  `ctyun_username` varchar(128) NOT NULL COMMENT '天翼云用户注册邮箱',
  `password` varchar(64) NOT NULL,
  `expire_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `work_order_item_config` JSON DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;