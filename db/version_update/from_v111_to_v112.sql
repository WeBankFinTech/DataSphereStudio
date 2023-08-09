CREATE TABLE IF NOT EXISTS `dss_orchestrator_copy_info` (
        `id` VARCHAR(128) NOT NULL COMMENT '主键',
        `username` VARCHAR(128) DEFAULT NULL COMMENT '用户名',
        `type` VARCHAR(128) DEFAULT NULL COMMENT '编排类别',
        `source_orchestrator_id` INT(20) DEFAULT NULL COMMENT '源编排ID',
        `source_orchestrator_name` VARCHAR(255) DEFAULT NULL COMMENT '源编排名',
        `target_orchestrator_name` VARCHAR(255) DEFAULT NULL COMMENT '目标编排名',
        `source_project_name` VARCHAR(255) DEFAULT NULL COMMENT '源工程名',
        `target_project_name` VARCHAR(255) DEFAULT NULL COMMENT '目标工程名',
        `workspace_id` INT(20) DEFAULT NULL COMMENT '工作空间ID',
        `workflow_node_suffix` VARCHAR(255) DEFAULT NULL COMMENT '目标工作流节点后缀',
        `microserver_name` VARCHAR(128) COMMENT '微服务名',
        `exception_info` VARCHAR(128) COMMENT '异常信息',
        `status` int(1) DEFAULT 0 COMMENT '复制任务最终状态',
        `instance_name`            varchar(128) DEFAULT NULL COMMENT '执行任务的实例',
        `is_copying` int(1) DEFAULT 0 COMMENT '编排是否在被复制',
        `success_node` TEXT COMMENT '复制成功节点',
        `start_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '复制开始时间',
        `end_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '复制结束时间',
        PRIMARY KEY (`id`),
        INDEX index_soi(source_orchestrator_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='编排复制信息表';

CREATE TABLE IF NOT EXISTS `dss_project_operate_record`
(
    `id`                  bigint(20)   NOT NULL AUTO_INCREMENT,
    `record_id`           varchar(64)  NOT NULL,
    `workspace_id`        bigint(20)   NOT NULL COMMENT '空间id',
    `project_id`          bigint(20)   NOT NULL COMMENT '项目id',
    `operate_type`        int(11)      NOT NULL COMMENT '操作类型',
    `status`              int(11)      NOT NULL COMMENT '操作状态',
    `instance_name`     VARCHAR(128) DEFAULT NULL COMMENT '执行任务的实例',
    `content`             longtext DEFAULT NULL COMMENT '操作内容详情',
    `result_resource_uri` text     DEFAULT NULL COMMENT '操作结果资源的uri，是一个json',
    `creator`             varchar(100) NOT NULL,
    `create_time`         datetime     NOT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_record_id` (`record_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目操作记录表';

CREATE TABLE IF NOT EXISTS `dss_release_task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) NOT NULL,
  `orchestrator_version_id` bigint(20) NOT NULL,
  `orchestrator_id` bigint(20) NOT NULL,
  `release_user` varchar(128) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `status` varchar(64) DEFAULT 'init',
  `instance_name` varchar(128) DEFAULT NULL COMMENT '执行任务的实例',
  `error_msg` varchar(500) DEFAULT NULL COMMENT '发布错误信息',
  `comment` varchar(500) DEFAULT NULL COMMENT '发布描述',
  `log_msg` varchar(255) DEFAULT NULL COMMENT '日志信息或日志路径',
  `bak` varchar(255) DEFAULT NULL COMMENT '备用字段',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=605 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT;

CREATE TABLE IF NOT EXISTS `dss_orchestrator_job_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `job_id` varchar(64) DEFAULT NULL COMMENT 'job ID',
  `conversion_job_json` varchar(1024) DEFAULT NULL COMMENT 'job信息',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `instance_name` varchar(128) DEFAULT NULL COMMENT '执行任务的实例',
  `status` varchar(128) DEFAULT NULL COMMENT '转换任务状态',
  `error_msg` varchar(2048) DEFAULT NULL COMMENT '转换任务异常信息',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='dss_orchestrator_job_info表';

CREATE TABLE IF NOT EXISTS `dss_project_copy_task` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `workspace_id` BIGINT(20) COMMENT '空间ID',
  `source_project_id` BIGINT(20) COMMENT '(源)复制工程ID',
  `source_project_name` VARCHAR(200) COMMENT '(源)复制工程名称',
  `copy_project_id` BIGINT(20) COMMENT '复制工程ID',
  `copy_project_name` VARCHAR(200) COMMENT '复制工程名称',
  `surplus_count` INT(3) COMMENT '剩余复制数量',
  `sum_count` INT(3) COMMENT '总数',
  `status` INT(1) COMMENT '状态 0:初始化，1：复制中，2：复制成功',
  `instance_name` VARCHAR(128) DEFAULT NULL COMMENT '执行任务的实例',
  `create_by` VARCHAR(200) COMMENT '创建人',
  `create_time` datetime COMMENT '创建时间',
  `update_time` datetime COMMENT '上个复制时间',
  `error_msg` text COMMENT '失败原因',
  `error_orc` VARCHAR(2048) DEFAULT '' COMMENT '拷贝异常编排',
  PRIMARY KEY (`id`)
) ENGINE=INNODB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='复制工程任务表';

-- 首页公告表
CREATE TABLE IF NOT EXISTS `dss_notice`
(
    `id`          int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `content`   text DEFAULT NULL COMMENT '公告内容',
    `start_time` datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '生效时间',
    `end_time`  datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '失效时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COLLATE=utf8mb4_bin COMMENT ='首页公告内容';

INSERT INTO dss_workflow_node_ui
( `key`, description, description_en, lable_name, lable_name_en, ui_type, required, value, default_value, is_hidden, `condition`, is_advanced, `order`, node_menu_type, is_base_info, `position`)
VALUES('spark.conf', 'spark自定义参数配置输入，例如spark.sql.shuffle.partitions=10。多个参数使用分号分隔。', 'input spark params config, eg: spark.sql.shuffle.partitions=10. Use semi-colon to split multi-params', 'spark.conf', 'spark.conf', 'Text', 0, NULL, "", 0, NULL, 0, 1, 1, 0, 'startup');
select @sparkConfUiId:=id from dss_workflow_node_ui where `key`="spark.conf";
select @sqlNodeId:=id from dss_workflow_node where node_type="linkis.spark.sql";
select @pysparkNodeId:=id from dss_workflow_node where node_type="linkis.spark.py";
select @scalaNodeId:=id from dss_workflow_node where node_type="linkis.spark.scala";
insert into dss_workflow_node_to_ui(`workflow_node_id`,`ui_id`) values(@sqlNodeId, @sparkConfUiId);
insert into dss_workflow_node_to_ui(`workflow_node_id`,`ui_id`) values(@pysparkNodeId, @sparkConfUiId);
insert into dss_workflow_node_to_ui(`workflow_node_id`,`ui_id`) values(@scalaNodeId, @sparkConfUiId);
select @len500valId:=id from dss_workflow_node_ui_validate where error_msg="长度在1到500个字符";
insert into dss_workflow_node_ui_to_validate(`ui_id`,`validate_id`) values(@sparkConfUiId, @len500valId);

-- 去除节点描述的中文限制
delete nutv  from dss_workflow_node_ui_to_validate nutv , dss_workflow_node_ui nu , dss_workflow_node_ui_validate nuv
             where   nu.id = nutv.ui_id
             AND nutv.validate_id = nuv.id and nu.lable_name ='节点描述' and nuv.error_msg='此值不能输入中文';

-- 调整执行器内存大小限制为1-28
update
    dss_workflow_node_ui_to_validate nutv , dss_workflow_node_ui nu , dss_workflow_node_ui_validate nuv
set
    nuv.validate_type='Regex',
    nuv.validate_range='^([1-9]|1[0-9]|2[0-8])(g|G){0,1}$',
    nuv.error_msg='设置范围为[1,28],设置超出限制',
    nuv.error_msg_en='must be between 1 and 28',
    nuv.trigger='blur'
where   nu.id = nutv.ui_id
  AND nutv.validate_id = nuv.id AND nu.key='spark.executor.memory' ;
-- fix 驱动器内存大小设置不能带g
update
    dss_workflow_node_ui_to_validate nutv , dss_workflow_node_ui nu , dss_workflow_node_ui_validate nuv
set
    nuv.validate_type='Regex',
    nuv.validate_range='^([1-9]|1[0-5])(g|G){0,1}$',
    nuv.error_msg='设置范围为[1,15],设置超出限制',
    nuv.error_msg_en='must be between 1 and 15',
    nuv.trigger='blur'
where   nu.id = nutv.ui_id
  AND nutv.validate_id = nuv.id AND nu.key='spark.driver.memory' ;

ALTER TABLE dss_workflow ADD metrics varchar(1024) NULL;

ALTER TABLE dss_workspace ADD COLUMN (`admin_permission` tinyint(1) DEFAULT 1 NOT NULL COMMENT '工作空间管理员是否有权限查看该空间下所有项目，1可以，0不可以');
-- appconn表新增微应用标记字段
ALTER TABLE `dss_appconn` ADD `is_micro_app` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否微应用嵌入';
-- enhance_json字段大小从1024增到到2048
ALTER TABLE dss_appconn_instance change `enhance_json` `enhance_json` varchar(2048) DEFAULT NULL COMMENT 'json格式的配置';

-- 表dss_workflow_task添加instance_name字段
ALTER TABLE `dss_workflow_task` ADD `instance_name` varchar(128) DEFAULT NULL COMMENT '执行任务的实例' AFTER `status`;
-- 表dss_workspace_user_role添加update_user和update_time字段
ALTER TABLE `dss_workspace_user_role` add `update_user` varchar(32) DEFAULT NULL COMMENT '更新人';
ALTER TABLE `dss_workspace_user_role` add `update_time` datetime DEFAULT NULL COMMENT '更新时间';

CREATE TABLE IF NOT EXISTS `dss_workspace_associate_departments` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `workspace_id` bigint(20) DEFAULT NULL,
  `departments` text DEFAULT NULL COMMENT '关联的部门-科室列表，逗号分割，若部门后不接科室则代表关联整个部门',
  `role_ids` varchar(128) DEFAULT NULL COMMENT '角色id列表，逗号分割',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `create_by` varchar(128) DEFAULT NULL,
  `update_by` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='空间自动加入用户绑定的部门科室信息';

-- webank内部表，待开源相关功能使用
CREATE TABLE IF NOT EXISTS `dss_streamis_proxy_user`
(
    `id`              int(11)  NOT NULL AUTO_INCREMENT,
    `user_name`       varchar(64)       DEFAULT NULL COMMENT  '实名用户名',
    `proxy_user_name` varchar(64)       DEFAULT NULL COMMENT  '代理用户名',
    `create_by`       varchar(64)       DEFAULT NULL COMMENT '创建者',
    `create_time`     datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT='流式应用代理用户表';

-- 版本发布时的releaseNote信息。webank内部表，待开源相关功能使用
CREATE TABLE IF NOT EXISTS `dss_release_note_content`
(
    `id`           int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name`         varchar(200) DEFAULT NULL COMMENT '名称',
    `title`        varchar(200) DEFAULT NULL COMMENT '标题',
    `url`          varchar(300) DEFAULT NULL COMMENT 'url',
    `url_type`     int(1)       DEFAULT '1' COMMENT 'url类型: 0-内部系统，1-外部系统；默认是外部',
    `release_type` int(1)       DEFAULT '0' COMMENT '发布形式: 0-作为dss整体发布，1-单独发布scriptis；默认是dss',
    `create_time`  datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='releaseNote表';

-- 用户访问行为统计表，初期只有登录行为统计。webank内部表，待开源相关功能使用
CREATE TABLE IF NOT EXISTS `dss_user_access_audit`
(
    `id`          int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_name`   varchar(64) DEFAULT NULL COMMENT '用户名',
    `login_count` BIGINT      DEFAULT 0 COMMENT '登录次数',
    `first_login` datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '第一次登录时间',
    `last_login`  datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '上一次登录时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_user_name` (`user_name`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='用户访问行为次数统计';

-- webank内部表，待开源相关功能使用
CREATE TABLE IF NOT EXISTS `dss_ec_release_strategy`
(
    `id`          int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `strategy_id`   varchar(64) NOT NULL UNIQUE COMMENT '规则id',
    `workspace_id`   bigint(20) NOT NULL COMMENT '工作空间id',
    `name`   varchar(64) NOT NULL COMMENT '规则名',
    `description`  varchar(128) DEFAULT NULL COMMENT '规则描述',
    `queue`   varchar(128) NOT NULL UNIQUE COMMENT '关联队列',
    `trigger_condition_conf`   varchar(1024) NOT NULL COMMENT '触发条件(json)',
    `terminate_condition_conf`   varchar(1024) NOT NULL COMMENT '终止条件(json)',
    `ims_conf`   varchar(2048) NOT NULL COMMENT '告警设置(json)',
    `status`   int(1) DEFAULT 0 COMMENT '规则状态：0禁用 1开启',
    `creator`   varchar(64) NOT NULL COMMENT '创建人',
    `create_time` datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modifier`   varchar(64) NOT NULL COMMENT '修改人',
    `modify_time`  datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `execute_instance`   varchar(128) DEFAULT NULL COMMENT '最近处理该规则的服务实例',
    `execute_time`   datetime    DEFAULT NULL COMMENT '最近处理该规则的时间起点',
    PRIMARY KEY (`id`),
    KEY `idx_strategy_id` (`strategy_id`),
    KEY `idx_workspace_id` (`workspace_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COLLATE=utf8mb4_bin COMMENT ='EC自动释放规则配置';

-- 工作空间关联的队列表。webank内部表，待开源相关功能使用
CREATE TABLE IF NOT EXISTS `dss_queue_in_workspace`
(
    `id`          int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `workspace_id`   bigint(20) NOT NULL COMMENT '工作空间id',
    `queue`   varchar(128) NOT NULL UNIQUE COMMENT '队列名',
    `apply_user`   varchar(64) NOT NULL COMMENT '申请人',
    `approve_id`   varchar(64) NOT NULL COMMENT '申请单号',
    `create_time`  datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COLLATE=utf8mb4_bin COMMENT ='工作空间关联的队列';

-- EC释放通知发送记录表。webank内部表，待开源相关功能使用
CREATE TABLE IF NOT EXISTS `dss_ec_release_ims_record`
(
    `id`          bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `record_id`   varchar(64) NOT NULL COMMENT '发送记录id',
    `strategy_id`   varchar(64) NOT NULL COMMENT '释放规则id',
    `workspace_id`   bigint(20) NOT NULL COMMENT '工作空间id',
    `content`   varchar(1024) NOT NULL COMMENT '发送内容',
    `status`   int(1) DEFAULT 0 COMMENT '通知状态：0未发送 1已发送  2发送失败',
    `execute_instance`   varchar(128) NOT NULL COMMENT '负责发送的服务实例',
    `create_time`  datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_time`  datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    KEY `idx_record_id` (`record_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COLLATE=utf8mb4_bin COMMENT ='EC释放通知发送记录';

-- 请求释放EC历史。webank内部表，待开源相关功能使用
CREATE TABLE IF NOT EXISTS `dss_ec_kill_history`
(
    `id`          bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `strategy_id`   varchar(64) NOT NULL COMMENT '释放规则id',
    `workspace_id`   bigint(20) NOT NULL COMMENT '工作空间id',
    `instance`   varchar(128) NOT NULL COMMENT '释放的EC实例名',
    `engine_type`   varchar(64) NOT NULL COMMENT 'EC类型',
    `queue`   varchar(128) NOT NULL COMMENT '队列名',
    `driver_core`   int(11) DEFAULT 0 COMMENT '本地释放核数',
    `driver_memory`   bigint(11) DEFAULT 0 COMMENT '本地释放内存，单位Byte',
    `yarn_core`   int(11) DEFAULT 0 COMMENT 'yarn释放核数',
    `yarn_memory`   bigint(11) DEFAULT 0 COMMENT 'yarn释放内存，单位Byte',
    `unlock_duration`   bigint(11) DEFAULT 0 COMMENT 'EC空闲时长,单位秒',
    `owner`    varchar(64) NOT NULL COMMENT 'EC创建者',
    `killer`    varchar(64) NOT NULL COMMENT 'EC释放触发者',
    `ec_start_time`  varchar(64)   NOT NULL COMMENT 'EC创建的时间',
    `kill_time`  datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '请求killEC的时间',
    `create_time`  datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `execute_instance`   varchar(128) DEFAULT NULL COMMENT '负责发送的服务实例',
    PRIMARY KEY (`id`),
    KEY `idx_workspace_id` (`workspace_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COLLATE=utf8mb4_bin COMMENT ='请求释放EC历史';
