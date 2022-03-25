-- no use √√
DROP TABLE IF EXISTS `dss_application_user_init_result`;
CREATE TABLE `dss_application_user_init_result` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `application_id` int(11) DEFAULT NULL,
  `result` varchar(255) DEFAULT NULL,
  `username` varchar(32) DEFAULT NULL,
  `is_init_success` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- no use √√
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

--√√
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

--√√
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

--√√
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

--√√
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

-- no use√√
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


-- no use√√
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

--no use√√
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

--no use√√
DROP TABLE IF EXISTS `dss_menu_page_relation`;
CREATE TABLE `dss_menu_page_relation` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--no use√√
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
  `active_flag` VARCHAR(10)  DEFAULT 'true'  COMMENT '调度标示：true-已启动；false-已禁用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=COMPACT;

--no use√√
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

-- no use√√
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


-- no use√√
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


-- no use√√
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
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_by` bigint(20) DEFAULT '0',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_by` bigint(20) DEFAULT '0',
  `is_first_login` bit(1) DEFAULT NULL COMMENT 'If it is the first time to log in',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;