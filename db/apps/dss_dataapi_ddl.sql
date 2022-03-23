DROP TABLE IF EXISTS `dss_dataapi_config`;
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