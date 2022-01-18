-- governance.dss_datawarehouse_layer definition

DROP TABLE IF EXISTS `dss_datawarehouse_layer`;
CREATE TABLE `dss_datawarehouse_layer`
(
    `id`             bigint(20)                    NOT NULL AUTO_INCREMENT,
    `name`           varchar(255) COLLATE utf8_bin NOT NULL,
    `en_name`        varchar(255) COLLATE utf8_bin NOT NULL,
    `owner`          varchar(255) COLLATE utf8_bin          DEFAULT NULL,
    `principal_name` varchar(128) COLLATE utf8_bin          DEFAULT NULL COMMENT '授权的名字：userName、roleName',
    `is_available`   bit(1)                        NOT NULL,
    `preset`         bit(1)                        NOT NULL DEFAULT b'0',
    `sort`           int(4)                        NOT NULL DEFAULT '1',
    `description`    varchar(255) COLLATE utf8_bin          DEFAULT NULL,
    `dbs`            varchar(255) COLLATE utf8_bin NOT NULL COMMENT '如果为空代表所有的库',
    `create_time`    datetime                      NOT NULL,
    `update_time`    datetime                      NOT NULL,
    `status`         bit(1)                        NOT NULL DEFAULT b'1',
    `lock_version`   bigint(20)                    NOT NULL DEFAULT '1',
    PRIMARY KEY (`id`),
    UNIQUE KEY `name` (`name`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 44
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin;


-- governance.dss_datawarehouse_layer_generalize_rule definition
DROP TABLE IF EXISTS `dss_datawarehouse_layer_generalize_rule`;
CREATE TABLE `dss_datawarehouse_layer_generalize_rule`
(
    `id`            bigint(20)                    NOT NULL AUTO_INCREMENT,
    `layer_id`      bigint(20)                    NOT NULL,
    `regex`         varchar(255) COLLATE utf8_bin NOT NULL COMMENT '自动归纳表达式',
    `identifier`    varchar(255) COLLATE utf8_bin NOT NULL,
    `en_identifier` varchar(255) COLLATE utf8_bin NOT NULL,
    `description`   varchar(255) COLLATE utf8_bin          DEFAULT NULL,
    `create_time`   datetime                      NOT NULL,
    `update_time`   datetime                      NOT NULL,
    `status`        bit(1)                        NOT NULL DEFAULT b'1',
    `lock_version`  bigint(20)                    NOT NULL DEFAULT '1',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin;

-- governance.dss_datawarehouse_modifier definition
DROP TABLE IF EXISTS `dss_datawarehouse_modifier`;
CREATE TABLE `dss_datawarehouse_modifier`
(
    `id`               bigint(20)                     NOT NULL AUTO_INCREMENT,
    `modifier_type`    varchar(64) COLLATE utf8_bin   NOT NULL,
    `modifier_type_en` varchar(128) COLLATE utf8_bin           DEFAULT NULL,
    `theme_domain_id`  bigint(20)                              DEFAULT NULL,
    `layer_id`         bigint(20)                              DEFAULT NULL,
    `theme_area`       varchar(1000) COLLATE utf8_bin NOT NULL COMMENT '空：代表所有，如果是逗号分隔的字符串则代表对应的theme的names',
    `layer_area`       varchar(1000) COLLATE utf8_bin NOT NULL COMMENT '空：代表所有，如果是逗号分隔的字符串则代表对应的layer的names',
    `description`      varchar(255) COLLATE utf8_bin           DEFAULT NULL,
    `theme_area_en`    varchar(100) COLLATE utf8_bin           DEFAULT NULL,
    `layer_area_en`    varchar(100) COLLATE utf8_bin           DEFAULT NULL,
    `is_available`     bit(1)                         NOT NULL,
    `create_time`      datetime                       NOT NULL,
    `update_time`      datetime                       NOT NULL,
    `status`           bit(1)                         NOT NULL DEFAULT b'1',
    `lock_version`     bigint(20)                     NOT NULL DEFAULT '1',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 25
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin;

-- governance.dss_datawarehouse_modifier_list definition
DROP TABLE IF EXISTS `dss_datawarehouse_modifier_list`;
CREATE TABLE `dss_datawarehouse_modifier_list`
(
    `id`          bigint(20)                    NOT NULL AUTO_INCREMENT,
    `modifier_id` bigint(20)                    NOT NULL,
    `name`        varchar(255) COLLATE utf8_bin NOT NULL,
    `identifier`  varchar(255) COLLATE utf8_bin NOT NULL,
    `formula`     varchar(255) COLLATE utf8_bin NOT NULL,
    `create_time` datetime                      NOT NULL,
    `update_time` datetime                      NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 37
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin;

-- governance.dss_datawarehouse_statistical_period definition
DROP TABLE IF EXISTS `dss_datawarehouse_statistical_period`;
CREATE TABLE `dss_datawarehouse_statistical_period`
(
    `id`                 bigint(20)                    NOT NULL AUTO_INCREMENT,
    `theme_domain_id`    bigint(20)                    NOT NULL,
    `layer_id`           bigint(20)                    NOT NULL,
    `name`               varchar(255) COLLATE utf8_bin NOT NULL,
    `en_name`            varchar(255) COLLATE utf8_bin NOT NULL,
    `start_time_formula` varchar(255) COLLATE utf8_bin          DEFAULT NULL,
    `end_time_formula`   varchar(255) COLLATE utf8_bin          DEFAULT NULL,
    `owner`              varchar(255) COLLATE utf8_bin          DEFAULT NULL,
    `principal_name`     varchar(128) COLLATE utf8_bin          DEFAULT NULL COMMENT '授权的名字：userName、roleName',
    `description`        varchar(255) COLLATE utf8_bin          DEFAULT NULL,
    `is_available`       bit(1)                        NOT NULL,
    `create_time`        datetime                      NOT NULL,
    `update_time`        datetime                      NOT NULL,
    `status`             bit(1)                        NOT NULL DEFAULT b'1',
    `lock_version`       bigint(20)                    NOT NULL DEFAULT '1',
    PRIMARY KEY (`id`),
    UNIQUE KEY `name` (`name`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 23
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin;

-- governance.dss_datawarehouse_table_rule definition
DROP TABLE IF EXISTS `dss_datawarehouse_table_rule`;
CREATE TABLE `dss_datawarehouse_table_rule`
(
    `id`               bigint(20)                     NOT NULL AUTO_INCREMENT,
    `name`             bigint(20)                     NOT NULL,
    `theme_area`       varchar(1000) COLLATE utf8_bin NOT NULL COMMENT '空：代表所有，如果是逗号分隔的字符串则代表对应的theme的names',
    `layer_area`       varchar(1000) COLLATE utf8_bin NOT NULL COMMENT '空：代表所有，如果是逗号分隔的字符串则代表对应的layer的names',
    `table_name_rule`  varchar(1000) COLLATE utf8_bin NOT NULL,
    `table_props_rule` varchar(1000) COLLATE utf8_bin NOT NULL,
    `partation_rule`   varchar(1000) COLLATE utf8_bin NOT NULL,
    `column_rule`      varchar(1000) COLLATE utf8_bin NOT NULL,
    `description`      varchar(255) COLLATE utf8_bin DEFAULT NULL,
    `is_available`     bit(1)                         NOT NULL,
    `create_time`      datetime                       NOT NULL,
    `update_time`      datetime                       NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `name` (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin;


-- governance.dss_datawarehouse_theme definition
DROP TABLE IF EXISTS `dss_datawarehouse_theme`;
CREATE TABLE `dss_datawarehouse_theme`
(
    `id`                bigint(20)                    NOT NULL AUTO_INCREMENT,
    `theme_domain_id`   bigint(20)                    NOT NULL,
    `name`              varchar(255) COLLATE utf8_bin NOT NULL,
    `en_name`           varchar(255) COLLATE utf8_bin NOT NULL,
    `parent_theme_name` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '默认为空，如果不为空则指向父主题',
    `owner`             varchar(255) COLLATE utf8_bin DEFAULT NULL,
    `is_available`      bit(1)                        NOT NULL,
    `sort`              int(4)                        NOT NULL,
    `description`       varchar(255) COLLATE utf8_bin DEFAULT NULL,
    `create_time`       datetime                      NOT NULL,
    `update_time`       datetime                      NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `name` (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin;

-- governance.dss_datawarehouse_theme_domain definition
DROP TABLE IF EXISTS `dss_datawarehouse_theme_domain`;
CREATE TABLE `dss_datawarehouse_theme_domain`
(
    `id`             bigint(20)                    NOT NULL AUTO_INCREMENT,
    `name`           varchar(255) COLLATE utf8_bin NOT NULL,
    `en_name`        varchar(255) COLLATE utf8_bin NOT NULL,
    `owner`          varchar(255) COLLATE utf8_bin          DEFAULT NULL,
    `principal_name` varchar(128) COLLATE utf8_bin          DEFAULT NULL COMMENT '授权的名字：userName、roleName',
    `is_available`   bit(1)                        NOT NULL,
    `sort`           int(4)                        NOT NULL,
    `description`    varchar(255) COLLATE utf8_bin          DEFAULT NULL,
    `create_time`    datetime                      NOT NULL,
    `update_time`    datetime                      NOT NULL,
    `status`         bit(1)                        NOT NULL DEFAULT b'1',
    `lock_version`   bigint(20)                    NOT NULL DEFAULT '1',
    PRIMARY KEY (`id`),
    UNIQUE KEY `name` (`name`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 56
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin;