
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;


-- ----------------------------
-- Table structure for dss_datamodel_dimension
-- ----------------------------
DROP TABLE IF EXISTS `dss_datamodel_dimension`;
CREATE TABLE `dss_datamodel_dimension` (
                                           `id` int(11) NOT NULL AUTO_INCREMENT,
                                           `name` varchar(255) COLLATE utf8_bin NOT NULL,
                                           `field_identifier` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                           `formula` varchar(255) COLLATE utf8_bin NOT NULL,
                                           `comment` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                           `warehouse_theme_name` varchar(512) COLLATE utf8_bin DEFAULT NULL COMMENT '数仓主题格式为： theme_domain_name.theme_name',
                                           `owner` varchar(64) COLLATE utf8_bin NOT NULL,
                                           `principal_name` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT '授权的名字：userName、roleName',
                                           `is_available` tinyint(1) NOT NULL,
                                           `create_time` datetime NOT NULL,
                                           `update_time` datetime NOT NULL,
                                           `warehouse_theme_name_en` varchar(512) COLLATE utf8_bin DEFAULT NULL COMMENT '英文',
                                           PRIMARY KEY (`id`) USING BTREE,
                                           UNIQUE KEY `name` (`name`) USING BTREE,
                                           UNIQUE KEY `field_identifier` (`field_identifier`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for dss_datamodel_indicator
-- ----------------------------
DROP TABLE IF EXISTS `dss_datamodel_indicator`;
CREATE TABLE `dss_datamodel_indicator` (
                                           `id` int(11) NOT NULL AUTO_INCREMENT,
                                           `name` varchar(255) COLLATE utf8_bin NOT NULL,
                                           `field_identifier` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                           `comment` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                           `warehouse_theme_name` varchar(512) COLLATE utf8_bin DEFAULT NULL COMMENT '数仓主题格式为： theme_domain_name.theme_name',
                                           `owner` varchar(64) COLLATE utf8_bin NOT NULL,
                                           `principal_name` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT '授权的名字：userName、roleName',
                                           `is_available` tinyint(1) NOT NULL,
                                           `is_core_indicator` tinyint(1) NOT NULL,
                                           `theme_area` varchar(1000) COLLATE utf8_bin NOT NULL COMMENT '空：代表所有，如果是逗号分隔的字符串则代表对应的theme的names',
                                           `layer_area` varchar(1000) COLLATE utf8_bin NOT NULL COMMENT '空：代表所有，如果是逗号分隔的字符串则代表对应的layer的names',
                                           `create_time` datetime NOT NULL,
                                           `update_time` datetime NOT NULL,
                                           `version` varchar(255) COLLATE utf8_bin NOT NULL,
                                           `warehouse_theme_name_en` varchar(512) COLLATE utf8_bin DEFAULT NULL COMMENT '数仓主题英文',
                                           `theme_area_en` varchar(1000) COLLATE utf8_bin NOT NULL COMMENT '英文名称',
                                           `layer_area_en` varchar(1000) COLLATE utf8_bin NOT NULL COMMENT '英文名称',
                                           PRIMARY KEY (`id`) USING BTREE,
                                           UNIQUE KEY `name` (`name`) USING BTREE,
                                           KEY `field_identifier` (`field_identifier`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=82 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for dss_datamodel_indicator_content
-- ----------------------------
DROP TABLE IF EXISTS `dss_datamodel_indicator_content`;
CREATE TABLE `dss_datamodel_indicator_content` (
                                                   `id` int(11) NOT NULL AUTO_INCREMENT,
                                                   `indicator_id` int(11) NOT NULL,
                                                   `version` varchar(255) COLLATE utf8_bin NOT NULL,
                                                   `indicator_type` int(4) NOT NULL COMMENT '0 原子 1 衍生 2 派生 3 复杂 4 自定义',
                                                   `measure_id` int(11) DEFAULT NULL,
                                                   `indicator_source_info` text COLLATE utf8_bin NOT NULL COMMENT '指标来源信息',
                                                   `formula` varchar(255) COLLATE utf8_bin NOT NULL,
                                                   `business` varchar(255) COLLATE utf8_bin NOT NULL,
                                                   `business_owner` varchar(255) COLLATE utf8_bin NOT NULL,
                                                   `calculation` varchar(255) COLLATE utf8_bin NOT NULL,
                                                   `calculation_owner` varchar(255) COLLATE utf8_bin NOT NULL,
                                                   `create_time` datetime NOT NULL,
                                                   `update_time` datetime NOT NULL,
                                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=158 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for dss_datamodel_indicator_version
-- ----------------------------
DROP TABLE IF EXISTS `dss_datamodel_indicator_version`;
CREATE TABLE `dss_datamodel_indicator_version` (
                                                   `id` int(11) NOT NULL AUTO_INCREMENT,
                                                   `name` varchar(255) COLLATE utf8_bin NOT NULL,
                                                   `owner` varchar(64) COLLATE utf8_bin NOT NULL,
                                                   `principal_name` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT '授权的名字：userName、roleName',
                                                   `version` varchar(255) COLLATE utf8_bin NOT NULL,
                                                   `version_context` text COLLATE utf8_bin NOT NULL COMMENT '历史版本详细信息快照',
                                                   `create_time` datetime NOT NULL,
                                                   `update_time` datetime NOT NULL,
                                                   `comment` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                                   PRIMARY KEY (`id`) USING BTREE,
                                                   UNIQUE KEY `name` (`name`,`version`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for dss_datamodel_label
-- ----------------------------
DROP TABLE IF EXISTS `dss_datamodel_label`;
CREATE TABLE `dss_datamodel_label` (
                                       `id` int(11) NOT NULL AUTO_INCREMENT,
                                       `name` varchar(255) COLLATE utf8_bin NOT NULL,
                                       `field_identifier` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                       `params` text COLLATE utf8_bin COMMENT '标签键值对 json',
                                       `comment` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                       `warehouse_theme_name` varchar(512) COLLATE utf8_bin DEFAULT NULL COMMENT '数仓主题格式为： theme_domain_name.theme_name',
                                       `owner` varchar(64) COLLATE utf8_bin NOT NULL,
                                       `principal_name` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT '授权的名字：userName、roleName',
                                       `is_available` tinyint(1) NOT NULL,
                                       `create_time` datetime NOT NULL,
                                       `update_time` datetime NOT NULL,
                                       `warehouse_theme_name_en` varchar(512) COLLATE utf8_bin DEFAULT NULL COMMENT '英文',
                                       PRIMARY KEY (`id`) USING BTREE,
                                       UNIQUE KEY `name` (`name`) USING BTREE,
                                       UNIQUE KEY `field_identifier` (`field_identifier`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for dss_datamodel_measure
-- ----------------------------
DROP TABLE IF EXISTS `dss_datamodel_measure`;
CREATE TABLE `dss_datamodel_measure` (
                                         `id` int(11) NOT NULL AUTO_INCREMENT,
                                         `name` varchar(255) COLLATE utf8_bin NOT NULL,
                                         `field_identifier` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                         `formula` varchar(255) COLLATE utf8_bin NOT NULL,
                                         `comment` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                         `warehouse_theme_name` varchar(512) COLLATE utf8_bin DEFAULT NULL COMMENT '数仓主题格式为： theme_domain_name.theme_name',
                                         `owner` varchar(64) COLLATE utf8_bin NOT NULL,
                                         `principal_name` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT '授权的名字：userName、roleName',
                                         `is_available` tinyint(1) NOT NULL,
                                         `create_time` datetime NOT NULL,
                                         `update_time` datetime NOT NULL,
                                         `warehouse_theme_name_en` varchar(512) COLLATE utf8_bin DEFAULT NULL COMMENT '英文',
                                         PRIMARY KEY (`id`) USING BTREE,
                                         UNIQUE KEY `name` (`name`) USING BTREE,
                                         UNIQUE KEY `field_identifier` (`field_identifier`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for dss_datamodel_table
-- ----------------------------
DROP TABLE IF EXISTS `dss_datamodel_table`;
CREATE TABLE `dss_datamodel_table` (
                                       `id` int(11) NOT NULL AUTO_INCREMENT,
                                       `data_base` varchar(255) COLLATE utf8_bin NOT NULL,
                                       `name` varchar(255) COLLATE utf8_bin NOT NULL,
                                       `alias` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                       `creator` varchar(255) COLLATE utf8_bin NOT NULL,
                                       `comment` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                       `create_time` datetime NOT NULL,
                                       `update_time` datetime NOT NULL,
                                       `warehouse_layer_name` varchar(512) COLLATE utf8_bin DEFAULT NULL COMMENT '数仓层级',
                                       `warehouse_layer_name_en` varchar(512) COLLATE utf8_bin DEFAULT NULL COMMENT '数仓层级英文',
                                       `warehouse_theme_name` varchar(512) COLLATE utf8_bin DEFAULT NULL COMMENT '数仓主题格式为： theme_domain_name.theme_name',
                                       `warehouse_theme_name_en` varchar(512) COLLATE utf8_bin DEFAULT NULL COMMENT '数仓主题英文',
                                       `lifecycle` varchar(512) COLLATE utf8_bin DEFAULT NULL COMMENT '生命周期',
                                       `is_partition_table` tinyint(1) NOT NULL,
                                       `is_available` tinyint(1) NOT NULL,
                                       `storage_type` varchar(255) COLLATE utf8_bin NOT NULL COMMENT '存储类型：hive/mysql',
                                       `principal_name` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT '授权的名字：userName、roleName',
                                       `compress` varchar(128) COLLATE utf8_bin NOT NULL COMMENT '压缩格式',
                                       `file_type` varchar(128) COLLATE utf8_bin NOT NULL COMMENT '文件格式',
                                       `version` varchar(128) COLLATE utf8_bin NOT NULL COMMENT '版本信息：默认1',
                                       `is_external` tinyint(1) NOT NULL COMMENT '是否外部表 0 内部表 1外部表',
                                       `location` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '外部表时 location',
                                       `label` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '标签',
                                       `lifecycle_en` varchar(512) COLLATE utf8_bin DEFAULT NULL COMMENT '生命周期英文',
                                       PRIMARY KEY (`id`) USING BTREE,
                                       UNIQUE KEY `database` (`data_base`,`name`) USING BTREE,
                                       UNIQUE KEY `name` (`name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=92 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for dss_datamodel_table_collcetion
-- ----------------------------
DROP TABLE IF EXISTS `dss_datamodel_table_collcetion`;
CREATE TABLE `dss_datamodel_table_collcetion` (
                                                  `id` int(11) NOT NULL AUTO_INCREMENT,
                                                  `data_base` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                                  `name` varchar(255) COLLATE utf8_bin NOT NULL,
                                                  `alias` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                                  `creator` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                                  `comment` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                                  `create_time` datetime NOT NULL,
                                                  `update_time` datetime NOT NULL,
                                                  `warehouse_layer_name` varchar(512) COLLATE utf8_bin DEFAULT NULL COMMENT '数仓层级',
                                                  `warehouse_theme_name` varchar(512) COLLATE utf8_bin DEFAULT NULL COMMENT '数仓主题格式为： theme_domain_name.theme_name',
                                                  `lifecycle` varchar(512) COLLATE utf8_bin DEFAULT NULL COMMENT '生命周期',
                                                  `is_partition_table` tinyint(1) DEFAULT NULL,
                                                  `is_available` tinyint(1) DEFAULT NULL,
                                                  `storage_type` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '存储类型：hive/mysql',
                                                  `principal_name` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT '授权的名字：userName、roleName',
                                                  `compress` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT '压缩格式',
                                                  `file_type` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT '文件格式',
                                                  `user` varchar(255) COLLATE utf8_bin NOT NULL COMMENT '收藏人',
                                                  `version` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT '版本信息：默认1',
                                                  `is_external` tinyint(1) DEFAULT NULL COMMENT '是否外部表 0 内部表 1外部表',
                                                  `location` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '外部表时 location',
                                                  `label` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '标签',
                                                  `guid` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT 'atlas标识',
                                                  `warehouse_layer_name_en` varchar(512) COLLATE utf8_bin DEFAULT NULL COMMENT '数仓层级英文',
                                                  `warehouse_theme_name_en` varchar(512) COLLATE utf8_bin DEFAULT NULL COMMENT '数仓主题英文',
                                                  `lifecycle_en` varchar(512) COLLATE utf8_bin DEFAULT NULL COMMENT '生命周期英文',
                                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for dss_datamodel_table_columns
-- ----------------------------
DROP TABLE IF EXISTS `dss_datamodel_table_columns`;
CREATE TABLE `dss_datamodel_table_columns` (
                                               `id` int(11) NOT NULL AUTO_INCREMENT,
                                               `table_id` int(11) NOT NULL,
                                               `name` varchar(255) COLLATE utf8_bin NOT NULL,
                                               `alias` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                               `type` varchar(64) COLLATE utf8_bin NOT NULL,
                                               `comment` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                               `is_partition_field` tinyint(1) NOT NULL,
                                               `is_primary` tinyint(1) NOT NULL,
                                               `length` int(11) DEFAULT NULL,
                                               `rule` varchar(128) COLLATE utf8_bin DEFAULT NULL,
                                               `model_type` int(4) DEFAULT NULL COMMENT '0 维度，1 指标 2 度量',
                                               `model_name_en` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '模型信息英文名称',
                                               `model_id` int(11) DEFAULT NULL COMMENT '关联具体模型id信息（因为有版本数据表id不可靠，暂时不用）',
                                               `model_name` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '模型信息名称',
                                               `create_time` datetime NOT NULL,
                                               `update_time` datetime NOT NULL,
                                               PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=226 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for dss_datamodel_table_materialized_history
-- ----------------------------
DROP TABLE IF EXISTS `dss_datamodel_table_materialized_history`;
CREATE TABLE `dss_datamodel_table_materialized_history` (
                                                            `id` int(11) NOT NULL AUTO_INCREMENT,
                                                            `materialized_code` mediumtext COLLATE utf8_bin COMMENT '物化sql',
                                                            `reason` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '物化原因',
                                                            `creator` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '物化者',
                                                            `status` int(4) NOT NULL COMMENT 'succeed,failed,in progess',
                                                            `create_time` datetime NOT NULL,
                                                            `last_update_time` datetime NOT NULL,
                                                            `task_id` varchar(512) COLLATE utf8_bin NOT NULL,
                                                            `error_msg` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                                            `tableName` varchar(255) COLLATE utf8_bin NOT NULL COMMENT '表名',
                                                            `data_base` varchar(255) COLLATE utf8_bin NOT NULL,
                                                            `version` varchar(128) COLLATE utf8_bin NOT NULL COMMENT '版本信息：默认1',
                                                            PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=66 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for dss_datamodel_table_params
-- ----------------------------
DROP TABLE IF EXISTS `dss_datamodel_table_params`;
CREATE TABLE `dss_datamodel_table_params` (
                                              `tbl_id` int(11) NOT NULL,
                                              `param_key` varchar(256) COLLATE utf8_bin NOT NULL COMMENT '创建表参数：压缩/orc等',
                                              `param_value` mediumtext COLLATE utf8_bin,
                                              PRIMARY KEY (`tbl_id`,`param_key`) USING BTREE,
                                              KEY `table_params_n49` (`tbl_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for dss_datamodel_table_statics
-- ----------------------------
DROP TABLE IF EXISTS `dss_datamodel_table_statics`;
CREATE TABLE `dss_datamodel_table_statics` (
                                               `id` int(11) NOT NULL AUTO_INCREMENT,
                                               `origin_tables` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
                                               `table_id` int(11) NOT NULL,
                                               `access_count` int(11) NOT NULL,
                                               `last_access_time` int(11) NOT NULL,
                                               `sample_data_path` varchar(256) COLLATE utf8_bin DEFAULT NULL COMMENT '存储10行用例数据',
                                               `sample_update_time` int(11) NOT NULL,
                                               PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for dss_datamodel_table_stats
-- ----------------------------
DROP TABLE IF EXISTS `dss_datamodel_table_stats`;
CREATE TABLE `dss_datamodel_table_stats` (
                                             `id` int(11) NOT NULL AUTO_INCREMENT,
                                             `data_base` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                             `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                             `create_time` datetime NOT NULL,
                                             `update_time` datetime NOT NULL,
                                             `column_count` int(11) NOT NULL COMMENT '字段数',
                                             `storage_size` int(11) NOT NULL COMMENT '存储大小',
                                             `file_count` int(11) NOT NULL COMMENT '文件数',
                                             `partition_count` int(11) NOT NULL COMMENT '分区数',
                                             `access_count` int(11) NOT NULL COMMENT '访问次数',
                                             `collect_count` int(11) NOT NULL COMMENT '收藏次数',
                                             `ref_count` int(11) NOT NULL COMMENT '引用次数',
                                             `version` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '版本信息：默认1',
                                             PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for dss_datamodel_table_version
-- ----------------------------
DROP TABLE IF EXISTS `dss_datamodel_table_version`;
CREATE TABLE `dss_datamodel_table_version` (
                                               `id` int(11) NOT NULL AUTO_INCREMENT,
                                               `tbl_id` int(11) NOT NULL,
                                               `name` varchar(255) COLLATE utf8_bin NOT NULL,
                                               `is_materialized` tinyint(1) NOT NULL COMMENT '是否物化',
                                               `table_code` mediumtext COLLATE utf8_bin COMMENT '创建table的sql',
                                               `comment` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '版本注释',
                                               `version` varchar(128) COLLATE utf8_bin NOT NULL COMMENT '版本信息：默认version0002',
                                               `table_params` mediumtext COLLATE utf8_bin,
                                               `columns` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
                                               `source_type` varchar(128) COLLATE utf8_bin DEFAULT 'add' COMMENT 'rollback,update,add',
                                               `create_time` datetime NOT NULL,
                                               `update_time` datetime NOT NULL,
                                               `creator` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                               PRIMARY KEY (`id`) USING BTREE,
                                               UNIQUE KEY `table_version` (`version`,`name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

SET FOREIGN_KEY_CHECKS = 1;
