
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for dss_oneservice_config
-- ----------------------------
DROP TABLE IF EXISTS `dss_oneservice_config`;
CREATE TABLE `dss_oneservice_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(200) NOT NULL COMMENT '服务名称',
  `path` varchar(200) NOT NULL COMMENT '服务路径',
  `protocol` int(11) NOT NULL COMMENT '协议: http, https',
  `method` varchar(10) NOT NULL COMMENT '方法： post, put, delete',
  `tag` varchar(200) DEFAULT NULL COMMENT '标签',
  `scope` varchar(50) DEFAULT NULL COMMENT '范围',
  `description` varchar(200) DEFAULT NULL COMMENT '服务描述',
  `status` int(11) DEFAULT '0' COMMENT '服务状态',
  `type` varchar(50) DEFAULT NULL COMMENT '服务类型',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `creator` varchar(50) DEFAULT NULL COMMENT '创建者',
  `modifier` varchar(50) DEFAULT NULL COMMENT '修改者',
  `script_path` varchar(200) NOT NULL COMMENT '脚本路径',
  `resource_id` varchar(50) NOT NULL COMMENT 'bml资源id',
  `version` varchar(20) NOT NULL COMMENT 'bml版本',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_dss_oneservice_config_name` (`name`),
  UNIQUE KEY `uniq_dss_oneservice_config_path` (`path`),
  UNIQUE KEY `uniq_dss_oneservice_config_script_path` (`script_path`),
  KEY `idx_dss_oneservice_config_script_path` (`script_path`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='服务api配置表';

-- ----------------------------
-- Table structure for dss_oneservice_param
-- ----------------------------
DROP TABLE IF EXISTS `dss_oneservice_param`;
CREATE TABLE `dss_oneservice_param` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `config_id` bigint(20) NOT NULL COMMENT '服务api配置id',
  `version` varchar(20) NOT NULL COMMENT '版本',
  `name` varchar(200) NOT NULL COMMENT '名称',
  `type` varchar(50) DEFAULT NULL COMMENT '类型',
  `required` tinyint(1) DEFAULT '0' COMMENT '是否必须: 0, 1',
  `description` varchar(200) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`),
  KEY `idx_dss_oneservice_param_config_id_version` (`config_id`,`version`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='apiservice 参数表';


