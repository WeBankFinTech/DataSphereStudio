DROP TABLE IF EXISTS `dss_workspace_user_role`;
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
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=224 DEFAULT CHARSET=utf8;

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
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;
