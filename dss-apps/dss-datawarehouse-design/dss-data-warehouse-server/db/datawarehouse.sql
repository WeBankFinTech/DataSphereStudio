

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
SET NAMES utf8mb4;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE='NO_AUTO_VALUE_ON_ZERO', SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# 转储表 dss_datawarehouse_layer
# ------------------------------------------------------------

DROP TABLE IF EXISTS `dss_datawarehouse_layer`;

CREATE TABLE `dss_datawarehouse_layer` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8_bin NOT NULL,
  `en_name` varchar(255) COLLATE utf8_bin NOT NULL,
  `owner` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `principal_name` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT '授权的名字：userName、roleName',
  `is_available` bit(1) NOT NULL,
  `preset` bit(1) NOT NULL DEFAULT b'0',
  `sort` int(4) NOT NULL DEFAULT '1',
  `description` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `dbs` varchar(255) COLLATE utf8_bin NOT NULL COMMENT '如果为空代表所有的库',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `status` bit(1) NOT NULL DEFAULT b'1',
  `lock_version` bigint(20) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

LOCK TABLES `dss_datawarehouse_layer` WRITE;
/*!40000 ALTER TABLE `dss_datawarehouse_layer` DISABLE KEYS */;

INSERT INTO `dss_datawarehouse_layer` (`id`, `name`, `en_name`, `owner`, `principal_name`, `is_available`, `preset`, `sort`, `description`, `dbs`, `create_time`, `update_time`, `status`, `lock_version`)
VALUES
	(1,'原数据层（ODS）','ods','admin','所有角色',b'1',b'1',10,'由业务系统同步到数据仓库的原始数据，一般不经过加工','ALL','2021-09-01 00:00:00','2021-09-01 00:00:00',b'1',1),
	(2,'明细层（DWD）','dwd','admin','所有角色',b'1',b'1',20,'从ods层经过ETL得到的明细数据，表示具体的事实','ALL','2021-09-01 00:00:00','2021-09-01 00:00:00',b'1',1),
	(3,'汇总层（DWS）','dws','admin','所有角色',b'1',b'1',30,'由明细数据经过汇总得到的数据，主要由统计维度和指标构成','ALL','2021-09-01 00:00:00','2021-09-01 00:00:00',b'1',1);

/*!40000 ALTER TABLE `dss_datawarehouse_layer` ENABLE KEYS */;
UNLOCK TABLES;


# 转储表 dss_datawarehouse_layer_generalize_rule
# ------------------------------------------------------------

DROP TABLE IF EXISTS `dss_datawarehouse_layer_generalize_rule`;

CREATE TABLE `dss_datawarehouse_layer_generalize_rule` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `layer_id` bigint(20) NOT NULL,
  `regex` varchar(255) COLLATE utf8_bin NOT NULL COMMENT '自动归纳表达式',
  `identifier` varchar(255) COLLATE utf8_bin NOT NULL,
  `en_identifier` varchar(255) COLLATE utf8_bin NOT NULL,
  `description` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `status` bit(1) NOT NULL DEFAULT b'1',
  `lock_version` bigint(20) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;



# 转储表 dss_datawarehouse_modifier
# ------------------------------------------------------------

DROP TABLE IF EXISTS `dss_datawarehouse_modifier`;

CREATE TABLE `dss_datawarehouse_modifier` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `modifier_type` varchar(64) COLLATE utf8_bin NOT NULL,
  `theme_domain_id` bigint(20) DEFAULT NULL,
  `layer_id` bigint(20) DEFAULT NULL,
  `theme_area` varchar(1000) COLLATE utf8_bin NOT NULL COMMENT '空：代表所有，如果是逗号分隔的字符串则代表对应的theme的names',
  `layer_area` varchar(1000) COLLATE utf8_bin NOT NULL COMMENT '空：代表所有，如果是逗号分隔的字符串则代表对应的layer的names',
  `description` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `is_available` bit(1) NOT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `status` bit(1) NOT NULL DEFAULT b'1',
  `lock_version` bigint(20) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;



# 转储表 dss_datawarehouse_modifier_list
# ------------------------------------------------------------

DROP TABLE IF EXISTS `dss_datawarehouse_modifier_list`;

CREATE TABLE `dss_datawarehouse_modifier_list` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `modifier_id` bigint(20) NOT NULL,
  `name` varchar(255) COLLATE utf8_bin NOT NULL,
  `identifier` varchar(255) COLLATE utf8_bin NOT NULL,
  `formula` varchar(255) COLLATE utf8_bin NOT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;



# 转储表 dss_datawarehouse_statistical_period
# ------------------------------------------------------------

DROP TABLE IF EXISTS `dss_datawarehouse_statistical_period`;

CREATE TABLE `dss_datawarehouse_statistical_period` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `theme_domain_id` bigint(20) NOT NULL,
  `layer_id` bigint(20) NOT NULL,
  `name` varchar(255) COLLATE utf8_bin NOT NULL,
  `en_name` varchar(255) COLLATE utf8_bin NOT NULL,
  `start_time_formula` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `end_time_formula` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `owner` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `principal_name` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT '授权的名字：userName、roleName',
  `description` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `is_available` bit(1) NOT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `status` bit(1) NOT NULL DEFAULT b'1',
  `lock_version` bigint(20) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;



# 转储表 dss_datawarehouse_table_rule
# ------------------------------------------------------------

DROP TABLE IF EXISTS `dss_datawarehouse_table_rule`;

CREATE TABLE `dss_datawarehouse_table_rule` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` bigint(20) NOT NULL,
  `theme_area` varchar(1000) COLLATE utf8_bin NOT NULL COMMENT '空：代表所有，如果是逗号分隔的字符串则代表对应的theme的names',
  `layer_area` varchar(1000) COLLATE utf8_bin NOT NULL COMMENT '空：代表所有，如果是逗号分隔的字符串则代表对应的layer的names',
  `table_name_rule` varchar(1000) COLLATE utf8_bin NOT NULL,
  `table_props_rule` varchar(1000) COLLATE utf8_bin NOT NULL,
  `partation_rule` varchar(1000) COLLATE utf8_bin NOT NULL,
  `column_rule` varchar(1000) COLLATE utf8_bin NOT NULL,
  `description` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `is_available` bit(1) NOT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;



# 转储表 dss_datawarehouse_theme
# ------------------------------------------------------------

DROP TABLE IF EXISTS `dss_datawarehouse_theme`;

CREATE TABLE `dss_datawarehouse_theme` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `theme_domain_id` bigint(20) NOT NULL,
  `name` varchar(255) COLLATE utf8_bin NOT NULL,
  `en_name` varchar(255) COLLATE utf8_bin NOT NULL,
  `parent_theme_name` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '默认为空，如果不为空则指向父主题',
  `owner` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `is_available` bit(1) NOT NULL,
  `sort` int(4) NOT NULL,
  `description` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;



# 转储表 dss_datawarehouse_theme_domain
# ------------------------------------------------------------

DROP TABLE IF EXISTS `dss_datawarehouse_theme_domain`;

CREATE TABLE `dss_datawarehouse_theme_domain` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8_bin NOT NULL,
  `en_name` varchar(255) COLLATE utf8_bin NOT NULL,
  `owner` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `principal_name` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT '授权的名字：userName、roleName',
  `is_available` bit(1) NOT NULL,
  `sort` int(4) NOT NULL,
  `description` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `status` bit(1) NOT NULL DEFAULT b'1',
  `lock_version` bigint(20) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

LOCK TABLES `dss_datawarehouse_theme_domain` WRITE;
/*!40000 ALTER TABLE `dss_datawarehouse_theme_domain` DISABLE KEYS */;

INSERT INTO `dss_datawarehouse_theme_domain` (`id`, `name`, `en_name`, `owner`, `principal_name`, `is_available`, `sort`, `description`, `create_time`, `update_time`, `status`, `lock_version`)
VALUES
	(1,'主题','英文名','负责人','New York',b'1',1,'描述','2021-09-28 13:18:48','2021-09-28 13:18:48',b'1',1);

/*!40000 ALTER TABLE `dss_datawarehouse_theme_domain` ENABLE KEYS */;
UNLOCK TABLES;



/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
