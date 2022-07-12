
DROP TABLE IF EXISTS `dss_workspace_appconn_role`;  -- changed
CREATE TABLE `dss_workspace_appconn_role` ( -- changed
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `workspace_id` bigint(20) DEFAULT NULL,
  `appconn_id` int(20) DEFAULT NULL,  -- changed
  `role_id` int(20) DEFAULT NULL,
  `priv` int(20) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `updateby` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5103 DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `dss_workflow_edit_lock`;  -- changed
CREATE TABLE `dss_workflow_edit_lock` (  -- changed
   `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '',
   `workflow_id` bigint(11) NOT NULL COMMENT '',  -- changed
   `flow_version` varchar(16) NOT NULL COMMENT '',  -- delete it
   `username` varchar(64) NOT NULL COMMENT '',
   `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
   `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
   `owner` varchar(128) NOT NULL COMMENT '',
   `lock_stamp` int(8) NOT NULL DEFAULT '0' COMMENT '',  -- delete it
   `is_expire` tinyint(1) NOT NULL DEFAULT '0' COMMENT '',
   `lock_content` varchar(512) NOT NULL COMMENT '',
   PRIMARY KEY (`id`),
   UNIQUE KEY `dss_flow_edit_lock_flow_id_IDX` (`flow_id`) USING BTREE
 ) ENGINE=InnoDB AUTO_INCREMENT=571 DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `dss_workspace_menu`;  -- changed
CREATE TABLE `dss_workspace_menu` (  -- changed
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


DROP TABLE IF EXISTS `dss_workspace_menu_role`;  -- changed
CREATE TABLE `dss_workspace_menu_role` (  -- changed
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `workspace_id` int(20) DEFAULT NULL,
  `menu_id` int(20) DEFAULT NULL,  -- changed
  `role_id` int(20) DEFAULT NULL,
  `priv` int(20) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `updateby` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5263 DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `dss_workspace_menu_appconn`; -- changed
CREATE TABLE `dss_workspace_menu_appconn` (  -- changed
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `appconn_id` int(20) DEFAULT NULL,  -- changed
  `menu_id` int(20) NOT NULL, -- changed
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


DROP TABLE IF EXISTS `dss_workspace_user_favorites_appconn`;  -- changed
CREATE TABLE `dss_workspace_user_favorites_appconn` (  -- changed
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(64) DEFAULT NULL,
  `workspace_id` bigint(20) DEFAULT '1',
  `menu_appconn_id` int(20) DEFAULT NULL,  -- changed
  `order` int(2) DEFAULT NULL,
  `create_by` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `last_update_time` datetime DEFAULT NULL,
  `last_update_user` varchar(30) DEFAULT NULL,
  `type` varchar(20) NOT NULL DEFAULT "" COMMENT "dingyiding or 收藏",
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=94 DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `dss_workspace_role`;  -- changed
CREATE TABLE `dss_workspace_role` (  -- changed
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `workspace_id` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `front_name` varchar(255) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `description` varchar(512) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `workspace_id` (`workspace_id`,`name`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;


DROP TABLE IF EXISTS `dss_workflow_relation`;  -- changed
CREATE TABLE `dss_workflow_relation` (  -- changed
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `flow_id` bigint(20) DEFAULT NULL,
  `parent_flow_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=78 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT;


DROP TABLE IF EXISTS `dss_workspace_admin_dept`;  -- changed
CREATE TABLE `dss_workspace_admin_dept` (  -- changed
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


DROP TABLE IF EXISTS `dss_workspace_user`;  -- delete this table
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

DROP TABLE IF EXISTS `dss_workspace_user_role`;  -- use this table
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


DROP TABLE IF EXISTS `dss_workspace_download_audit`; -- changed
CREATE TABLE `dss_workspace_download_audit`  (  -- changed
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `creator` varchar(255)  COMMENT '创建者',
  `tenant` varchar(255)  COMMENT '租户',
	`path` varchar(255)  COMMENT '文件路径',
	`sql` varchar(3000)  COMMENT '执行sql脚本',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
	 PRIMARY KEY (`id`)
) ENGINE = INNODB DEFAULT CHARSET = utf8 COMMENT = '文件下载审计';


DROP TABLE IF EXISTS `dss_workspace_dictionary`;  -- changed
CREATE TABLE `dss_workspace_dictionary` (  -- changed
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