DROP TABLE IF EXISTS `dss_apiservice_api`;
CREATE TABLE `dss_apiservice_api` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(180) NOT NULL COMMENT '服务名称',
  `alias_name` varchar(200) NOT NULL COMMENT '服务中文名称',
  `path` varchar(180) NOT NULL COMMENT '服务路径',
  `protocol` int(11) NOT NULL COMMENT '协议: http, https',
  `method` varchar(10) NOT NULL COMMENT '方法： post, put, delete',
  `tag` varchar(200) DEFAULT NULL COMMENT '标签',
  `scope` varchar(50) DEFAULT NULL COMMENT '范围',
  `description` varchar(200) DEFAULT NULL COMMENT '服务描述',
  `status` int(11) DEFAULT '0' COMMENT '服务状态，默认0是停止，1是运行中，2是删除',
  `type` varchar(50) DEFAULT NULL COMMENT '服务引擎类型',
  `run_type` varchar(50) DEFAULT NULL COMMENT '脚本类型',
  `create_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `creator` varchar(50) DEFAULT NULL COMMENT '创建者',
  `modifier` varchar(50) DEFAULT NULL COMMENT '修改者',
  `script_path` varchar(180) NOT NULL COMMENT '脚本路径',
  `workspaceID` int(11) NOT NULL COMMENT '工作空间ID',
  `api_comment` varchar(1024) DEFAULT NULL COMMENT '服务备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_uniq_config_name` (`name`),
  UNIQUE KEY `idx_uniq_dconfig_path` (`path`),
  KEY `idx_dss_script_path` (`script_path`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='服务api配置表';

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
  KEY `idx_api_version_id` (`api_version_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='apiservice 参数表';

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
   `metadata_info` varchar(5000)  COMMENT '发布者库表信息',
   `auth_id` varchar(200) COMMENT '用于与datamap交互的UUID',
   `datamap_order_no` varchar(200) DEFAULT NULL COMMENT 'datamap审批单号码',
   PRIMARY KEY(`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='服务api版本表';

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
  UNIQUE KEY `idx_uniq_api_version_id` (`api_version_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='apiservice 审批单表';

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