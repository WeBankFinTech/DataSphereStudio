DROP TABLE IF EXISTS `dss_guide_group`;
CREATE TABLE IF NOT EXISTS `dss_guide_group` (
  `id` BIGINT(13) NOT NULL AUTO_INCREMENT,
  `path` VARCHAR(100) NOT NULL COMMENT '页面URL路径',
  `title` VARCHAR(255) DEFAULT NULL COMMENT '标题',
  `description` VARCHAR(200) DEFAULT NULL COMMENT '描述',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` tinyint(1) DEFAULT '0' COMMENT '0:未删除(默认), 1已删除',
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='用户向导页面';

DROP TABLE IF EXISTS `dss_guide_content`;
CREATE TABLE IF NOT EXISTS `dss_guide_content` (
  `id` BIGINT(13) NOT NULL AUTO_INCREMENT,
  `group_id` BIGINT(50) NOT NULL COMMENT '所属页面ID',
  `path` VARCHAR(100) NOT NULL COMMENT '所属页面URL路径',
  `title` VARCHAR(255) DEFAULT NULL COMMENT '标题',
  `title_alias` VARCHAR(50) DEFAULT NULL COMMENT '标题简称',
  `seq` VARCHAR(20) DEFAULT NULL COMMENT '序号',
  `type` INT(1) DEFAULT '1' COMMENT '类型: 1-步骤step，2-问题question',
  `content` TEXT DEFAULT NULL COMMENT 'Markdown格式的内容',
  `content_html` MEDIUMTEXT DEFAULT NULL COMMENT 'Markdown内容转化为HTML格式',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` tinyint(1) DEFAULT '0' COMMENT '0:未删除(默认), 1已删除',
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='用户向导页面内容详情';

DROP TABLE IF EXISTS `dss_guide_catalog`;
CREATE TABLE IF NOT EXISTS `dss_guide_catalog` (
  `id` BIGINT(13) NOT NULL AUTO_INCREMENT,
  `parent_id` BIGINT(13) NOT NULL COMMENT '父级目录ID，-1代表最顶级目录',
  `title` VARCHAR(255) DEFAULT NULL COMMENT '标题',
  `description` VARCHAR(200) DEFAULT NULL COMMENT '描述',
  `create_by` VARCHAR(255) DEFAULT NULL COMMENT '创建者',
  `create_time` DATETIME DEFAULT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` VARCHAR(255) DEFAULT NULL COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` TINYINT(1) DEFAULT '0' COMMENT '0:未删除(默认), 1已删除',
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='用户向导知识库目录';

DROP TABLE IF EXISTS `dss_guide_chapter`;
CREATE TABLE IF NOT EXISTS `dss_guide_chapter` (
  `id` BIGINT(13) NOT NULL AUTO_INCREMENT,
  `catalog_id` BIGINT(13) NOT NULL COMMENT '目录ID',
  `title` VARCHAR(255) DEFAULT NULL COMMENT '标题',
  `title_alias` VARCHAR(50) DEFAULT NULL COMMENT '标题简称',
  `content` TEXT DEFAULT NULL COMMENT 'Markdown格式的内容',
  `content_html` MEDIUMTEXT DEFAULT NULL COMMENT 'Markdown转换为html内容',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` tinyint(1) DEFAULT '0' COMMENT '0:未删除(默认), 1已删除',
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='用户向导知识库文章';