-- 1.1.13 sql
-- 参数模板表
CREATE TABLE `dss_ec_config_template`
(
    `id`          bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `template_id`   varchar(64) NOT NULL UNIQUE COMMENT '参数模板的uuid',
    `workspace_id`   bigint(20) NOT NULL COMMENT '工作空间id',
    `name`   varchar(64) NOT NULL UNIQUE COMMENT '参数模板名',
    `description`  varchar(256) DEFAULT NULL COMMENT '模板描述',
    `engine_type`   varchar(128) NOT NULL COMMENT '引擎类型',
    `permission_type`   tinyint(1) NOT NULL COMMENT '可见范围类型，0全部可见，1指定用户可见',
    `creator`   varchar(64) NOT NULL COMMENT '创建人',
    `create_time` datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modifier`   varchar(64) NOT NULL COMMENT '修改人',
    `modify_time`  datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    KEY `idx_workspace_id` (`workspace_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COLLATE=utf8mb4_bin COMMENT ='参数模板表';

-- 参数模板可见用户表
CREATE TABLE `dss_ec_config_template_user`
(
    `id`          bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_name`   varchar(64) DEFAULT NULL COMMENT '用户名',
    `template_id`   varchar(64) NOT NULL COMMENT '参数模板的uuid',
    `workspace_id`   bigint(20) NOT NULL COMMENT '工作空间id',
    `create_time` datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_template_uuid` (`user_name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COLLATE=utf8mb4_bin COMMENT ='参数模板可见用户表';

-- 模板应用规则表
CREATE TABLE `dss_ec_config_template_apply_rule`
(
    `id`          bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `rule_id`   varchar(64) NOT NULL UNIQUE COMMENT '规则id',
    `workspace_id`   bigint(20) NOT NULL COMMENT '工作空间id',
    `rule_type`   tinyint(1) NOT NULL COMMENT '规则类型，0为临时规则，1指定用户可见',
    `template_id`   varchar(64) NOT NULL  COMMENT '关联的参数模板id',
    `template_name`   varchar(64) NOT NULL  COMMENT '关联的参数模板名',
    `engine_type`   varchar(128) NOT NULL COMMENT '引擎类型',
    `engine_name` varchar(128) NOT NULL COMMENT '引擎名（带版本号）',
    `permission_type`   tinyint(1) NOT NULL COMMENT '覆盖范围，0为全部工作空间用户，1指定用户，2为新用户',
    `application`  varchar(128) DEFAULT NULL COMMENT '应用类型',
    `status`   tinyint(1) DEFAULT 0 COMMENT '执行状态：0未执行 1执行成功  2执行失败 3部分失败',
    `creator`   varchar(64) NOT NULL COMMENT '创建人',
    `create_time` datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `execute_user`   varchar(64) DEFAULT NULL COMMENT '执行人',
    `execute_time`  datetime    DEFAULT NULL COMMENT '最近执行时间',
    PRIMARY KEY (`id`),
    KEY `idx_workspace_id` (`workspace_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COLLATE=utf8mb4_bin COMMENT ='模板应用规则表';

-- 模板应用规则覆盖用户表
CREATE TABLE `dss_ec_config_template_apply_rule_user`
(
    `id`          bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_name`   varchar(64) DEFAULT NULL COMMENT '用户名',
    `rule_id`   varchar(64) NOT NULL  COMMENT '规则的uuid',
    `workspace_id`   bigint(20) NOT NULL COMMENT '工作空间id',
    `create_time` datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_template_uuid` (`user_name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COLLATE=utf8mb4_bin COMMENT ='模板应用规则覆盖用户表';

-- 模板应用规则执行记录表
CREATE TABLE `dss_ec_config_template_apply_rule_execute_record`
(
    `id`          bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_name`   varchar(64) DEFAULT NULL COMMENT '用户名',
    `rule_id`   varchar(64) NOT NULL COMMENT '规则id',
    `workspace_id`   bigint(20) NOT NULL COMMENT '工作空间id',
    `template_name`   varchar(64) NOT NULL  COMMENT '关联的参数模板名',
    `engine_type`   varchar(128) NOT NULL  COMMENT '引擎类型',
    `application`  varchar(128) DEFAULT NULL COMMENT '应用类型',
    `status`   tinyint(1) DEFAULT 0 COMMENT '执行状态：0未执行 1执行成功  2执行失败',
    `execute_user`   varchar(64) NOT NULL COMMENT '执行人',
    `execute_time`  datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '最近执行时间',
    PRIMARY KEY (`id`),
    KEY `idx_workspace_id` (`workspace_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COLLATE=utf8mb4_bin COMMENT ='模板应用规则执行记录表';

-- 修改组件名字段长度由16字符至24字符
ALTER TABLE dss_workflow_node MODIFY COLUMN name varchar(24) NULL;



-- 1.1.14 sql
-- 工作流引用模板表
CREATE TABLE `dss_ec_config_template_workflow`
(
    `id`          bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `template_id` varchar(64) DEFAULT NULL COMMENT '参数模板的uuid',
    `project_id`  bigint(20) DEFAULT NULL COMMENT '项目id',
    `orchestrator_id` bigint(20) DEFAULT NULL COMMENT '编排id',
    `flow_id` bigint(20) DEFAULT NULL COMMENT '工作流id',
    `create_time` datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COLLATE=utf8mb4_bin COMMENT ='工作流应用模板表';

-- 工作流默认模板表
CREATE TABLE `dss_workflow_default_template`
(
    `id`          bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `template_id` varchar(64) DEFAULT NULL COMMENT '参数模板的uuid',
    `project_id` bigint(20) DEFAULT NULL COMMENT '项目id',
    `orchestrator_id` bigint(20) DEFAULT NULL COMMENT '编排id',
    `create_user` varchar(100) DEFAULT NULL COMMENT '创建人',
    `create_time` datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_user` varchar(100) DEFAULT NULL COMMENT '更新人',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    key `idx_workspace_project` (`project_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COLLATE=utf8mb4_bin COMMENT ='工作流默认模板表';

-- 公告内容表修改，添加创建用户和创建时间
ALTER TABLE `dss_notice` ADD create_user varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '创建用户名';
ALTER TABLE `dss_notice` ADD create_time datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';


-- 1.1.15 sql
-- 新增引擎内存
INSERT  INTO `dss_workflow_node_ui`
(`key`,`description`,`description_en`,`lable_name`,`lable_name_en`,`ui_type`,`required`,`value`,`default_value`,`is_hidden`,`condition`,`is_advanced`,`order`,`node_menu_type`,`is_base_info`,`position`)
values
('wds.linkis.engineconn.java.driver.memory','hive引擎内存，默认值：1G','hive driver memory, default：1G','wds.linkis.engineconn.java.driver.memory','wds-linkis-engineconn.java.driver.memory','Input',0,NULL,'1G',0,NULL,0,1,1,0,'runtime');

-- 建立hive关联关系
INSERT INTO `dss_workflow_node_to_ui` (`workflow_node_id`, `ui_id`) VALUES ((SELECT id FROM `dss_workflow_node` WHERE name = 'hql' limit 1),(SELECT id FROM `dss_workflow_node_ui` WHERE `key` = 'wds.linkis.engineconn.java.driver.memory' limit 1));

-- 增加校验
insert into `dss_workflow_node_ui_validate` (`validate_type`, `validate_range`, `error_msg`, `error_msg_en`, `trigger`) values('Regex', '^([1-9]|10|[1-9])(g|G){0,1}$', '设置范围为[1,10],设置超出限制', 'hive memory limit 1,10', 'blur');

insert into `dss_workflow_node_ui_validate` (`validate_type`, `validate_range`, `error_msg`, `error_msg_en`, `trigger`) values('Regex', '^((source.type|check.object).\\w+=(\\w(\\.)*(\\{(.+?)\\})*)+[;\n]*)+$', '请正确填写多源配置', 'params config error, please make sure!', 'blur');

insert into `dss_workflow_node_ui_validate` (`validate_type`, `validate_range`, `error_msg`, `error_msg_en`, `trigger`) values('Function', 'validateJobDesc', 'check.object配置重复,请检查', 'exist check.object repeat please check!', 'blur');

-- 关联校验
INSERT INTO `dss_workflow_node_ui_to_validate` (`ui_id`, `validate_id`)
VALUES (
    (SELECT id FROM `dss_workflow_node_ui` WHERE `key` = 'wds.linkis.engineconn.java.driver.memory' limit 1),
    (SELECT id FROM `dss_workflow_node_ui_validate` WHERE error_msg = '设置范围为[1,10],设置超出限制' AND error_msg_en = 'hive memory limit 1,10' limit 1)
 );

INSERT INTO `dss_workflow_node_ui_to_validate` (`ui_id`, `validate_id`)
VALUES (
(SELECT id FROM `dss_workflow_node_ui` WHERE `key` = 'job.desc' limit 1),
(SELECT id FROM `dss_workflow_node_ui_validate` WHERE error_msg = '请正确填写多源配置' AND error_msg_en = 'params config error, please make sure!' limit 1)
);

INSERT INTO `dss_workflow_node_ui_to_validate` (`ui_id`, `validate_id`) VALUES (
(SELECT id FROM `dss_workflow_node_ui` WHERE `key` = 'job.desc' limit 1),
(SELECT id FROM `dss_workflow_node_ui_validate` WHERE error_msg = 'check.object配置重复,请检查' AND error_msg_en = 'exist check.object repeat please check!' limit 1)
);

-- 工作流节点选择引用模板，隐藏参数
-- spark、pyspark、scala、hive节点
update
    `dss_workflow_node_ui`
set
    `condition` = "!${params.configuration.startup['ec.conf.templateId']}"
where
    `key` in ('spark.executor.memory','spark.executor.cores','spark.executor.instances','wds.linkis.engineconn.java.driver.memory','spark.conf','spark.driver.memory');

-- spark.executor.cores参数描述修改
update
    `dss_workflow_node_ui`
set
   `description` = "执行器核心个数，默认值：2", description_en="Number of cores per executor, default value: 2"
where
   `key` = 'spark.executor.cores';

CREATE TABLE `dss_user_limit`
(
`id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
`limit_name` varchar(64) NOT NULL COMMENT '限制项名称',
`value` varchar(128) NOT NULL COMMENT '限制项value',
`user_name` varchar(1024) DEFAULT NULL COMMENT '限制用户',
`create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
`update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (`id`),
key `idx_limit_name` (`limit_name`)
) ENGINE = InnoDB
DEFAULT CHARSET = utf8mb4 COLLATE=utf8mb4_bin COMMENT ='dss用户限制表';


-- 1.1.16 sql
-- 修改name字段64字符为128字符
ALTER TABLE dss_ec_config_template MODIFY name varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL;

ALTER TABLE dss_ec_config_template_apply_rule MODIFY template_name varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL;

ALTER TABLE dss_ec_config_template_apply_rule_execute_record MODIFY template_name varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL;


-- 1.1.17 sql
ALTER TABLE dss_apiservice_access_info ADD task_id varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '任务id';
ALTER TABLE dss_apiservice_access_info ADD task_status varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '任务执行状态';

ALTER TABLE dss_apiservice_approval ADD sensitive_level TINYINT NULL COMMENT '是否涉及一级敏感数据';

update `dss_workflow_node` set icon_path='svgs/datachecker-node.svg' where node_type='linkis.appconn.datachecker' and name = 'datachecker';
update `dss_workflow_node` set icon_path='svgs/python-node.svg' where node_type ='linkis.python.python' and name ='python';
update `dss_workflow_node` set icon_path='svgs/pyspark-node.svg' where node_type='linkis.spark.py' and name ='pyspark';
update `dss_workflow_node` set icon_path='svgs/sql-node.svg' where node_type='linkis.spark.sql' and name ='sql';
update `dss_workflow_node` set icon_path='svgs/Scala-node.svg' where node_type='linkis.spark.scala' and name ='scala';
update `dss_workflow_node` set icon_path='svgs/hql-node.svg' where node_type='linkis.hive.hql' and name ='hql';
update `dss_workflow_node` set icon_path='svgs/connector-node.svg' where node_type='linkis.control.empty' and name ='connector';
update `dss_workflow_node` set icon_path='svgs/sendemail-node.svg' where node_type='linkis.appconn.sendemail' and name ='sendemail';
update `dss_workflow_node` set icon_path='svgs/shell-node.svg' where node_type='linkis.shell.sh' and name ='shell';
update `dss_workflow_node` set icon_path='svgs/subflow-node.svg' where node_type='workflow.subflow' and name ='subFlow';
update `dss_workflow_node` set icon_path='svgs/eventsender-node.svg' where node_type='linkis.appconn.eventchecker.eventsender' and name ='eventsender';
update `dss_workflow_node` set icon_path='svgs/eventchecker-node.svg' where node_type='linkis.appconn.eventchecker.eventreceiver' and name ='eventreceiver';

update `dss_workflow_node_ui` set `position` = 'startup' where `key` = 'wds.linkis.engineconn.java.driver.memory';


-- 1.1.19 sql

ALTER TABLE dss_workspace_dictionary ADD checked TINYINT default 0 COMMENT '默认勾选';

UPDATE
    `dss_workspace_dictionary`
SET
    checked = 1
WHERE
    dic_name = '开发中心' AND dic_value = 'dev';

UPDATE
    `dss_workspace_dictionary`
SET
    checked = 1
WHERE
    dic_name = '生产中心' AND dic_value = 'prod';

UPDATE
    `dss_workflow_node_ui_validate`
SET
    validate_range = '^(((check.object).\\w+=[^.\\s;]+(\\.[^.\\s;]+)+[;\\s]*)|((source.type).\\w+=\\w+[;\\s]*))\\+$'
WHERE
    error_msg ='请正确填写多源配置' and error_msg_en = 'params config error, please make sure!';


-- 1.1.20 sql
ALTER TABLE dss_ec_release_strategy ADD COLUMN cross_cluster   tinyint(1) DEFAULT 0 COMMENT '是否跨集群,0为否，1为是'  AFTER queue;
ALTER TABLE dss_queue_in_workspace ADD COLUMN cross_cluster   tinyint(1) DEFAULT 0 COMMENT '是否跨集群,0为否，1为是'  AFTER queue;
-- 新增历史查询字段
ALTER TABLE dss_apiservice_access_info ADD COLUMN query_params TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '新的文本字段';
-- 删除唯一性约束
ALTER TABLE dss_ec_release_strategy DROP INDEX queue;
ALTER TABLE dss_queue_in_workspace DROP INDEX queue;

-- 1.3.0 sql
-- 删除部分appconn引擎类型对应节点的是否复用引擎ui，强制复用引擎
delete
from dss_workflow_node_to_ui
where ui_id = (select id
               from dss_workflow_node_ui dwnu
               where dwnu.key = 'ReuseEngine')
  and workflow_node_id in (select id
                           from dss_workflow_node dwn
                           where dwn.appconn_name in
                                 ('datachecker', 'eventchecker', 'metabase', 'newVisualis', 'sendemail', 'visualis'));

--  hive引擎添加map、reduce任务限制参数
-- 添加 mapreduce.job.running.map.limit 和 mapreduce.job.running.reduce.limit 限制参数至 dss_workflow_node_ui 表中, 填写限制提示信息。
INSERT  INTO `dss_workflow_node_ui` (`key`,`description`,`description_en`,`lable_name`,`lable_name_en`,`ui_type`,`required`,`value`,`default_value`,`is_hidden`,`condition`,`is_advanced`,`order`,`node_menu_type`,`is_base_info`,`position`) values
  ('mapreduce.job.running.map.limit','请填写map任务数限制','please map task limit','mapreduce.job.running.map.limit','mapreduce.job.running.map.limit','Input',0,NULL,'200000',0,"!${params.configuration.startup['ec.conf.templateId']}",0,1,1,0,'startup');

INSERT INTO `dss_workflow_node_ui` (`key`,`description`,`description_en`,`lable_name`,`lable_name_en`,`ui_type`,`required`,`value`,`default_value`,`is_hidden`,`condition`,`is_advanced`,`order`,`node_menu_type`,`is_base_info`,`position`) values ('mapreduce.job.running.reduce.limit','请填写reduce任务数限制','please reduce task limit','mapreduce.job.running.reduce.limit','mapreduce.job.running.reduce.limit','Input',0,NULL,'999',0,"!${params.configuration.startup['ec.conf.templateId']}",0,1,1,0,'startup');

-- 添加 dss_workflow_node主键ID和dss_workflow_node_ui主键ID 关联信息至节点ui中间表 dss_workflow_node_to_ui
INSERT INTO `dss_workflow_node_to_ui` (`workflow_node_id`, `ui_id`)
VALUES (
(SELECT id FROM `dss_workflow_node` WHERE name = 'hql' limit 1),
(SELECT id FROM `dss_workflow_node_ui` WHERE `key` = 'mapreduce.job.running.map.limit' limit 1)
);

INSERT INTO `dss_workflow_node_to_ui` (`workflow_node_id`, `ui_id`)
VALUES (
(SELECT id FROM `dss_workflow_node` WHERE name = 'hql' limit 1),
(SELECT id FROM `dss_workflow_node_ui` WHERE `key` = 'mapreduce.job.running.reduce.limit' limit 1)
);

-- 添加规则校验
insert into `dss_workflow_node_ui_validate` (`validate_type`, `validate_range`, `error_msg`, `error_msg_en`, `trigger`) values('Regex', '^(1[0-9]{1,5}|200000|[1-9][0-9]{1,4})$', '设置范围为[10,200000],设置超出限制', 'map task limit 10,200000', 'blur');

insert into `dss_workflow_node_ui_validate` (`validate_type`, `validate_range`, `error_msg`, `error_msg_en`, `trigger`) values('Regex', '^([1-9][0-9]{1,2}|999|)$', '设置范围为[10,999],设置超出限制', 'reduce task limit 10,999', 'blur');

-- 关联校验

insert into dss_workflow_node_ui_to_validate (ui_id,validate_id)  values (
(select id  from `dss_workflow_node_ui` WHERE `key` = 'mapreduce.job.running.map.limit' limit 1),
(select id  from dss_workflow_node_ui_validate  where  error_msg_en = 'map task limit 10,200000' limit 1)
);

insert into dss_workflow_node_ui_to_validate (ui_id,validate_id)  values (
(select id  from `dss_workflow_node_ui` WHERE `key` = 'mapreduce.job.running.reduce.limit' limit 1),
(select id  from dss_workflow_node_ui_validate  where  error_msg_en = 'reduce task limit 10,999' limit 1)
);


 update  dss_workflow_node_ui_validate set error_msg = '设置范围为[1,10],设置超出限制',error_msg_en = 'must be between 1 and 10'  where id = (select t2.validate_id  from dss_workflow_node_ui t1
 join dss_workflow_node_ui_to_validate t2 on t1.id = t2.ui_id
 where t1.`key`  = 'spark.executor.cores' limit 1);


 update  dss_workflow_node_ui_validate set error_msg = '设置范围为[1,40],设置超出限制',error_msg_en = 'must be between 1 and 40'  where id = (select t2.validate_id  from dss_workflow_node_ui t1
 join dss_workflow_node_ui_to_validate t2 on t1.id = t2.ui_id
 where t1.`key`  = 'spark.executor.instances' limit 1);


 -- 1.4.0 sql

CREATE TABLE `dss_ec_config_template_apply_rule_department` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `department_name` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '部门名称',
  `rule_id` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '规则的uuid',
  `workspace_id` bigint(20) NOT NULL COMMENT '工作空间id',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_template_uuid` (`department_name`)
) ENGINE=InnoDB CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='模板应用规则覆盖部门表';

ALTER TABLE dss_orchestrator_info ADD is_default_reference tinyint(1) NULL COMMENT '是否默认引用资源参数模板';

-- 更新content字段类型为text
ALTER TABLE dss_ec_release_ims_record MODIFY COLUMN content text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL;


INSERT INTO dss_workflow_node_ui (`key`,description,description_en,lable_name,lable_name_en,ui_type,required,value,default_value,is_hidden,`condition`,is_advanced,`order`,node_menu_type,is_base_info,`position`) VALUES
    ('auto.disabled','默认为false，若禁用，工作流执行和调度会跳过该节点','default false','是否禁用节点','auto.disabled','Select',1,'["true","false"]',NULL,0,NULL,0,1,1,0,'special');

insert into dss_workflow_node_ui_to_validate (ui_id,validate_id)  values (
(select id  from `dss_workflow_node_ui` WHERE `key` = 'auto.disabled' limit 1),
(select id  from dss_workflow_node_ui_validate  where  validate_type='None' limit 1)
);

INSERT INTO dss_workflow_node_to_ui (workflow_node_id, ui_id)
SELECT id, (SELECT id FROM `dss_workflow_node_ui` WHERE `key` = 'auto.disabled' limit 1) FROM dss_workflow_node;


-- 更新datachecker job.desc验证信息
update dss_workflow_node_ui_validate set validate_range = 'validateJobDesc',validate_type = 'Function' where error_msg ='请正确填写多源配置' and error_msg_en = 'params config error, please make sure!';

update dss_workflow_node_ui_validate set validate_range = 'validateJobDescDuplication',validate_type = 'Function',error_msg ='check.object.xx或source.type.xx重复,请检查' where error_msg_en in ('exist check.object.xx please check!','exist check.object repeat please check!');


-- 取消job.desc 中文限制
delete from  dss_workflow_node_ui_validate where error_msg_en = 'Chinese characters are not allowed';


-- 1.5.0 sql

-- 增加 工作空间关联git表，存储git token password等信息
CREATE TABLE `dss_workspace_associate_git` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
   `workspace_id` bigint(20) DEFAULT NULL,
   `git_user`  varchar(64)  DEFAULT NULL COMMENT  'git登录用户名',
   `git_password`  VARCHAR(255)  DEFAULT NULL COMMENT  'git登录密码，用于跳转',
   `git_token`  varchar(255) COMMENT  '用户配置的git token',
   `git_url` varchar(255),
   `create_time` datetime DEFAULT NULL,
   `update_time` datetime DEFAULT NULL,
   `create_by` varchar(128) DEFAULT NULL,
   `update_by` varchar(128) DEFAULT NULL,
   `git_user_id` varchar(20) DEFAULT NULL,
   `type` varchar(32) DEFAULT NULL,
   PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='工作空间绑定的git信息';

CREATE TABLE `dss_orchestrator_submit_job_info` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `orchestrator_id` bigint(20) NOT NULL,
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `instance_name` varchar(128) DEFAULT NULL COMMENT '提交任务的实例',
    `status` varchar(128) DEFAULT NULL COMMENT '提交任务状态',
    `error_msg` varchar(2048) DEFAULT NULL COMMENT '提交任务异常信息',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='dss_orchestrator_submit_job_info表';

CREATE TABLE `dss_project_associate_git` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
   `git_project_id` varchar(20) DEFAULT NULL,
   `project_name`  varchar(64)  DEFAULT NULL COMMENT  '项目名',
   `workspace_id` bigint(20) DEFAULT NULL,
   PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='项目绑定的project的git信息';

alter table dss_apiservice_param ADD  max_length int(8) NULL COMMENT '最大长度';

-- 增加 工作流提交状态字段
ALTER TABLE dss_orchestrator_info ADD status VARCHAR(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;

-- 增加 commit_id用于记录各个版本发布时的commit_id
ALTER TABLE dss_orchestrator_version_info ADD commit_id varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;

-- 增加 project 接入git标识
ALTER TABLE dss_project ADD associate_git TINYINT DEFAULT '0' COMMENT '0:未接入git，1:已接入git';
ALTER TABLE dss_project ADD data_source_list_json TEXT  CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT '项目数据源配置，json格式';


-- 1.6.0 sql
ALTER TABLE `dss_project`
    ADD COLUMN `label` varchar(128)  CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT
    '标签，用于区分不同来源请求创建的项目' AFTER `associate_git`;


-- 1.7.0 sql

CREATE TABLE `dss_workflow_node_content` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `node_key` varchar(64) NOT NULL COMMENT '节点Key',
  `node_id` varchar(64) NOT NULL COMMENT '节点Id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_time` datetime  DEFAULT NULL COMMENT '更新时间',
  `modify_user` varchar(255) DEFAULT NULL COMMENT '更新人',
  `job_type` varchar(255) DEFAULT NULL COMMENT '节点类型',
  `orchestrator_id` bigint(20) NOT NULL COMMENT '编排Id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='工作流节点表';


CREATE TABLE `dss_workflow_node_content_to_ui` (
  `content_id` bigint(20) NOT NULL COMMENT '编排Id',
  `node_ui_key` varchar(64) NOT NULL COMMENT '节点Key',
  `node_ui_value` text NOT NULL COMMENT '属性值',
  PRIMARY KEY (`content_id`, `node_ui_key`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='工作流节点属性表';


CREATE TABLE `dss_workflow_node_meta` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `orchestrator_id` bigint(20) NOT NULL COMMENT '编排Id',
  `proxy_user` varchar(64) DEFAULT NULL COMMENT '代理用户',
  `meta_resource` varchar(255) DEFAULT NULL COMMENT '资源文件',
  `global_var` TEXT COMMENT '全局变量',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='工作流配置表';

-- 用于记录各个项目的git读写用户名
ALTER TABLE dss_project_associate_git ADD git_user VarChar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT 'git读写用户名';

-- 用于记录各个项目的token
ALTER TABLE dss_project_associate_git ADD git_token VarChar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT '各个项目的token';

-- 用于记录各个项目的url
ALTER TABLE dss_project_associate_git ADD git_url VarChar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT '各个项目的url';

-- 用于记录各个项目的git创建时间
ALTER TABLE dss_project_associate_git ADD create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';

-- 用于记录各个项目的更新时间
ALTER TABLE dss_project_associate_git ADD update_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间';

-- 用于记录各个版本发布时的bml_version
ALTER TABLE dss_orchestrator_info ADD bml_version VarChar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT '版本发布时的bml_version';

-- 用于记录各个版本发布时的resource_id
ALTER TABLE dss_orchestrator_info ADD resource_id VarChar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT '版本发布时的resource_id';

ALTER TABLE dss_orchestrator_submit_job_info ADD submit_result LONGTEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT 'gitdiff结果';


INSERT INTO dss_workflow_node_ui (`key`,description,description_en,lable_name,lable_name_en,ui_type,required,value,default_value,is_hidden,`condition`,is_advanced,`order`,node_menu_type,is_base_info,`position`) VALUES
    ('msg.channel.type','请选择信号类型，默认是DSS信号','Please choose the message type,default dss','msg.channel.type','msg.channel.type','Select',0,'["DSS","KGAS"]','DSS',0,NULL,0,1,1,0,'runtime')
;

INSERT INTO dss_workflow_node_ui_to_validate (ui_id,validate_id) VALUES
    (
        (SELECT  id FROM dss_workflow_node_ui where `key`='msg.channel.type' limit 1)
        ,
        (SELECT  id FROM dss_workflow_node_ui_validate where  validate_type='None' limit 1));



-- 1.8.0 sql

ALTER TABLE dss_workflow_node_content_to_ui ADD node_type VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT '节点类型，用于分区';

ALTER TABLE dss_workflow_node_content_to_ui ADD node_content_type VARCHAR(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT '内容类型，整数-NumInterval, 内存-Memory, 字符串-String';

ALTER TABLE dss_workflow_node_content ADD flow_id BIGINT  COMMENT '工作流Id';

ALTER TABLE dss_workflow_node_content_to_ui DROP PRIMARY KEY;

ALTER TABLE dss_workflow_node_content_to_ui ADD PRIMARY KEY (`content_id`, `node_ui_key`, `node_type`);

ALTER TABLE `dss_workflow_node_content_to_ui`
PARTITION BY LIST COLUMNS(`node_type`) (
    PARTITION `python` VALUES IN ('linkis.python.python'),
    PARTITION `pyspark` VALUES IN ('linkis.spark.py'),
    PARTITION `sql` VALUES IN ('linkis.spark.sql'),
    PARTITION `scala` VALUES IN ('linkis.spark.scala'),
    PARTITION `hql` VALUES IN ('linkis.hive.hql'),
	PARTITION `jdbc` VALUES IN ('linkis.jdbc.jdbc'),
	PARTITION `shell` VALUES IN ('linkis.shell.sh'),
	PARTITION `connector` VALUES IN ('linkis.control.empty'),
	PARTITION `subFlow` VALUES IN ('workflow.subflow'),
    PARTITION `datachecker` VALUES IN ('linkis.appconn.datachecker'),
    PARTITION `eventsender` VALUES IN ('linkis.appconn.eventchecker.eventsender'),
    PARTITION `eventreceiver` VALUES IN ('linkis.appconn.eventchecker.eventreceiver'),
    PARTITION `sendemail` VALUES IN ('linkis.appconn.sendemail'),
    PARTITION `display` VALUES IN ('linkis.appconn.visualis.display'),
	PARTITION `dashboard` VALUES IN ('linkis.appconn.visualis.dashboard'),
	PARTITION `widget` VALUES IN ('linkis.appconn.visualis.widget'),
	PARTITION `view` VALUES IN ('linkis.appconn.visualis.view'),
	PARTITION `tableau` VALUES IN ('linkis.appconn.newVisualis.tableau'),
    PARTITION `CheckRules` VALUES IN ('linkis.appconn.qualitis'),
    PARTITION `ShellRules` VALUES IN ('linkis.appconn.qualitis.bash'),
    PARTITION `CheckAlert` VALUES IN ('linkis.appconn.qualitis.checkalert'),
    PARTITION `mlss` VALUES IN ('linkis.appconn.mlss'),
    PARTITION `gpu` VALUES IN ('linkis.appconn.mlflow.gpu'),
    PARTITION `tableauDataRefre` VALUES IN ('linkis.appconn.newVisualis.tableauDataRefre'),
    PARTITION `TableRules` VALUES IN ('linkis.appconn.qualitis.TableRules'),
    PARTITION `mlssv2` VALUES IN ('linkis.appconn.mlssv2'),
    PARTITION `sqoop` VALUES IN ('linkis.appconn.exchangis.sqoop'),
    PARTITION `datax` VALUES IN ('linkis.appconn.exchangis.datax'),
    PARTITION `nebula` VALUES IN ('linkis.nebula.nebula'),
	PARTITION `metabase` VALUES IN ('linkis.appconn.metabase')
);

INSERT INTO dss_workflow_node_ui_validate (validate_type,validate_range,error_msg,error_msg_en,`trigger`) VALUES
    ('Function','validateCheckObject','请正确填写check.object的值,格式为dbname.tablename{ds=partitionname}，不要有空格','params config error, please make sure!','blur');

INSERT INTO dss_workflow_node_ui_to_validate (ui_id ,validate_id ) VALUES(
    (select id from dss_workflow_node_ui where `key`='check.object' limit 1),
    (SELECT id FROM dss_workflow_node_ui_validate where validate_type ='Function' AND validate_range ='validateCheckObject' limit 1)
 );

UPDATE dss_workflow_node_ui_validate set error_msg='请严格按照dbname.tablename{ds=partitionname}格式填写，中间不允许有空格' ,validate_range='^\\s*[a-zA-Z]([^.]*\\.[^.]*){1,}\\s*$' where error_msg ='需要检查的数据源dbname.tablename{partition}' ;

INSERT INTO dss_workflow_node (name,appconn_name,node_type,jump_type,support_jump,submit_to_scheduler,enable_copy,should_creation_before_node,icon_path) VALUES ('nebula','scriptis','linkis.nebula.nebula',2,1,1,1,0,'svgs/nebula-graph.svg');

INSERT INTO dss_workflow_node_to_group (node_id,group_id) VALUES ((select id from dss_workflow_node where node_type = 'linkis.nebula.nebula' limit 1) ,(select id from dss_workflow_node_group where name_en = 'Data development' limit 1));

INSERT INTO dss_workflow_node_to_ui (workflow_node_id,ui_id) VALUES ((select id from dss_workflow_node where node_type = 'linkis.nebula.nebula' limit 1),(select id from dss_workflow_node_ui where `key` = 'title' and node_menu_type = '1' limit 1));
INSERT INTO dss_workflow_node_to_ui (workflow_node_id,ui_id) VALUES ((select id from dss_workflow_node where node_type = 'linkis.nebula.nebula' limit 1),(select id from dss_workflow_node_ui where `key` = 'desc' and node_menu_type = '1' limit 1));
INSERT INTO dss_workflow_node_to_ui (workflow_node_id,ui_id) VALUES ((select id from dss_workflow_node where node_type = 'linkis.nebula.nebula' limit 1),(select id from dss_workflow_node_ui where `key` = 'businessTag' limit 1));
INSERT INTO dss_workflow_node_to_ui (workflow_node_id,ui_id) VALUES ((select id from dss_workflow_node where node_type = 'linkis.nebula.nebula' limit 1),(select id from dss_workflow_node_ui where `key` = 'appTag' limit 1));
INSERT INTO dss_workflow_node_to_ui (workflow_node_id,ui_id) VALUES ((select id from dss_workflow_node where node_type = 'linkis.nebula.nebula' limit 1),(select id from dss_workflow_node_ui where `key` = 'ReuseEngine' limit 1));
INSERT INTO dss_workflow_node_to_ui (workflow_node_id,ui_id) VALUES ((select id from dss_workflow_node where node_type = 'linkis.nebula.nebula' limit 1),(select id from dss_workflow_node_ui where `key` = 'auto.disabled' limit 1));



-- 1.9.0 sql

CREATE TABLE `dss_dataset_scan_record` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `path` text COLLATE utf8mb4_bin NOT NULL COMMENT '结果集路径',
  `task_id` varchar(128) COLLATE utf8mb4_bin NOT NULL COMMENT 'linkis任务id',
  `row_size` bigint(11) NOT NULL COMMENT '结果集行数',
  `has_sensitive_info` tinyint(1) DEFAULT NULL COMMENT '是否有敏感',
  `scan_info` longtext COLLATE utf8mb4_bin COMMENT '扫描的结果字段信息',
  `read_history` text COLLATE utf8mb4_bin COMMENT '已经看过结果集的用户、场景（查看、下载、导出等），是一个json',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_task_id` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='结果集敏感信息扫描记录';

CREATE TABLE `dss_dataset_user_usage_cache` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL,
  `usage_quota` bigint(11) NOT NULL COMMENT '流量',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='敏感信息流量,dms缓存,实现dms降级';


alter table dss_workflow_node_content_to_ui add PARTITION(
    PARTITION eventsenderWTSS VALUES IN ('wtss.eventchecker.sender'),
    PARTITION eventreceiverWTSS VALUES IN ('wtss.eventchecker.receiver'));



UPDATE dss_workflow_node_ui
	SET default_value=NULL
	WHERE `key`='wds.linkis.rm.yarnqueue';


-- 1.10.0 sql

CREATE TABLE `dss_workspace_default_template` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `template_id` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '参数模板的uuid',
  `workspace_id` bigint(20) DEFAULT NULL COMMENT '项目id',
  `create_user` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='工作空间模板关联表';


alter table dss_workspace add enabled_flow_keywords_check tinyint DEFAULT '0' COMMENT '是否开启工作流关键字校验,1、启用 0、禁用';

alter table dss_workspace add is_default_reference tinyint  DEFAULT '0' COMMENT '是否默认引用资源参数模板,1、是 0、否';

alter table dss_orchestrator_info add not_contains_keywords_node longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT '不包含关键字的节点信息';