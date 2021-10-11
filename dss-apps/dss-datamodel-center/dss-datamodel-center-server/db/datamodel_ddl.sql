SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for dss_datamodel_dimension
-- ----------------------------
DROP TABLE IF EXISTS `dss_datamodel_dimension`;
CREATE TABLE `dss_datamodel_dimension`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `field_identifier` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `formula` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `comment` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `warehouse_theme_name` varchar(512) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '数仓主题格式为： theme_domain_name.theme_name',
  `owner` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `principal_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '授权的名字：userName、roleName',
  `is_available` tinyint(1) NOT NULL,
  `create_time` datetime(0) NOT NULL,
  `update_time` datetime(0) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dss_datamodel_indicator
-- ----------------------------
DROP TABLE IF EXISTS `dss_datamodel_indicator`;
CREATE TABLE `dss_datamodel_indicator`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `field_identifier` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `comment` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `warehouse_theme_name` varchar(512) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '数仓主题格式为： theme_domain_name.theme_name',
  `owner` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `principal_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '授权的名字：userName、roleName',
  `is_available` tinyint(1) NOT NULL,
  `is_core_indicator` tinyint(1) NOT NULL,
  `theme_area` varchar(1000) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '空：代表所有，如果是逗号分隔的字符串则代表对应的theme的names',
  `layer_area` varchar(1000) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '空：代表所有，如果是逗号分隔的字符串则代表对应的layer的names',
  `create_time` datetime(0) NOT NULL,
  `update_time` datetime(0) NOT NULL,
  `version` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dss_datamodel_indicator_content
-- ----------------------------
DROP TABLE IF EXISTS `dss_datamodel_indicator_content`;
CREATE TABLE `dss_datamodel_indicator_content`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `indicator_id` bigint(20) NOT NULL,
  `version` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `indicator_type` int(4) NOT NULL COMMENT '0 原子 1 衍生 2 派生 3 复杂 4 自定义',
  `measure_id` bigint(20) NULL DEFAULT NULL,
  `indicator_source_info` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '指标来源信息',
  `formula` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `business` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `business_owner` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `calculation` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `calculation_owner` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `create_time` datetime(0) NOT NULL,
  `update_time` datetime(0) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dss_datamodel_indicator_version
-- ----------------------------
DROP TABLE IF EXISTS `dss_datamodel_indicator_version`;
CREATE TABLE `dss_datamodel_indicator_version`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `owner` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `principal_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '授权的名字：userName、roleName',
  `version` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `version_context` text CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '历史版本详细信息快照',
  `create_time` datetime(0) NOT NULL,
  `update_time` datetime(0) NOT NULL,
  `comment` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dss_datamodel_measure
-- ----------------------------
DROP TABLE IF EXISTS `dss_datamodel_measure`;
CREATE TABLE `dss_datamodel_measure`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `field_identifier` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `formula` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `comment` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `warehouse_theme_name` varchar(512) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '数仓主题格式为： theme_domain_name.theme_name',
  `owner` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `principal_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '授权的名字：userName、roleName',
  `is_available` tinyint(1) NOT NULL,
  `create_time` datetime(0) NOT NULL,
  `update_time` datetime(0) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dss_datamodel_table
-- ----------------------------
DROP TABLE IF EXISTS `dss_datamodel_table`;
CREATE TABLE `dss_datamodel_table`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `database` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `alias` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `creator` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `comment` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `create_time` datetime(0) NOT NULL,
  `update_time` datetime(0) NOT NULL,
  `warehouse_layer_name` varchar(512) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '数仓层级',
  `warehouse_theme_name` varchar(512) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '数仓主题格式为： theme_domain_name.theme_name',
  `lifecycle` int(4) NOT NULL,
  `is_partition_table` tinyint(1) NOT NULL,
  `is_available` tinyint(1) NOT NULL,
  `storage_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '存储类型：hive/mysql',
  `principal_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '授权的名字：userName、roleName',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `database`(`database`, `name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dss_datamodel_table_columns
-- ----------------------------
DROP TABLE IF EXISTS `dss_datamodel_table_columns`;
CREATE TABLE `dss_datamodel_table_columns`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `table_id` bigint(20) NOT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `alias` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `type` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `comment` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `is_partition_field` tinyint(1) NOT NULL,
  `is_primary` tinyint(1) NOT NULL,
  `length` int(11) NULL DEFAULT NULL,
  `rule` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `model_type` int(4) NULL DEFAULT NULL COMMENT '0 维度，1 指标 2 度量',
  `model_id` bigint(20) NULL DEFAULT NULL COMMENT '关联具体模型id信息',
  `create_time` datetime(0) NOT NULL,
  `update_time` datetime(0) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dss_datamodel_table_materialized_history
-- ----------------------------
DROP TABLE IF EXISTS `dss_datamodel_table_materialized_history`;
CREATE TABLE `dss_datamodel_table_materialized_history`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `materialized_code` mediumtext CHARACTER SET utf8 COLLATE utf8_bin NULL COMMENT '物化sql',
  `reason` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '物化原因',
  `creator` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '物化者',
  `status` int(4) NOT NULL COMMENT 'succeed,failed,in progess',
  `create_time` datetime(0) NOT NULL,
  `last_update_time` datetime(0) NOT NULL,
  `task_id` bigint(20) NOT NULL,
  `error_msg` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dss_datamodel_table_params
-- ----------------------------
DROP TABLE IF EXISTS `dss_datamodel_table_params`;
CREATE TABLE `dss_datamodel_table_params`  (
  `tbl_id` bigint(20) NOT NULL,
  `param_key` varchar(256) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '创建表参数：压缩/orc等',
  `param_value` mediumtext CHARACTER SET utf8 COLLATE utf8_bin NULL,
  PRIMARY KEY (`tbl_id`, `param_key`) USING BTREE,
  INDEX `table_params_n49`(`tbl_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dss_datamodel_table_statics
-- ----------------------------
DROP TABLE IF EXISTS `dss_datamodel_table_statics`;
CREATE TABLE `dss_datamodel_table_statics`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `origin_tables` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `table_id` bigint(20) NOT NULL,
  `access_count` int(11) NOT NULL,
  `last_access_time` int(11) NOT NULL,
  `sample_data_path` varchar(256) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '存储10行用例数据',
  `sample_update_time` int(11) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dss_datamodel_table_version
-- ----------------------------
DROP TABLE IF EXISTS `dss_datamodel_table_version`;
CREATE TABLE `dss_datamodel_table_version`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `tbl_id` bigint(20) NOT NULL,
  `is_materialized` tinyint(1) NOT NULL COMMENT '是否物化',
  `table_code` mediumtext CHARACTER SET utf8 COLLATE utf8_bin NULL COMMENT '创建table的sql',
  `comment` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '版本注释',
  `version` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '版本信息：默认version0002',
  `table_params` mediumtext CHARACTER SET utf8 COLLATE utf8_bin NULL,
  `columns` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `source_type` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT 'add' COMMENT 'rollback,update,add',
  `create_time` datetime(0) NOT NULL,
  `update_time` datetime(0) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `table_version`(`id`, `version`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
