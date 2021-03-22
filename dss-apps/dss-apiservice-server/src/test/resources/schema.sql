CREATE TABLE  dss_apiservice_api  (
   id  bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
   name  varchar(200) NOT NULL COMMENT '服务名称',
   alias_name  varchar(200) NOT NULL COMMENT '服务中文名称',
   path  varchar(200) NOT NULL COMMENT '服务路径',
   protocol  int(11) NOT NULL COMMENT '协议: http, https',
   method  varchar(10) NOT NULL COMMENT '方法： post, put, delete',
   tag  varchar(200) DEFAULT NULL COMMENT '标签',
   scope  varchar(50) DEFAULT NULL COMMENT '范围',
   description  varchar(200) DEFAULT NULL COMMENT '服务描述',
   status  int(11) DEFAULT '0' COMMENT '服务状态，默认0是停止，1是运行中',
   type  varchar(50) DEFAULT NULL COMMENT '服务引擎类型',
   run_type  varchar(50) DEFAULT NULL COMMENT '脚本类型',
   create_time  timestamp NOT NULL  COMMENT '创建时间',
   modify_time  timestamp NOT NULL  COMMENT '修改时间',
   creator  varchar(50) DEFAULT NULL COMMENT '创建者',
   modifier  varchar(50) DEFAULT NULL COMMENT '修改者',
   script_path  varchar(200) NOT NULL COMMENT '脚本路径',
   workspaceID  int(11) NOT NULL COMMENT '工作空间ID',
   api_comment  varchar(1024) DEFAULT NULL COMMENT '服务备注',
  PRIMARY  KEY ( id )
)  COMMENT='服务api配置表';


CREATE TABLE  dss_apiservice_param  (
   id  bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
   api_version_id  bigint(20) NOT NULL COMMENT '服务api版本id',
   name  varchar(200) NOT NULL COMMENT '名称',
   display_name  varchar(50) DEFAULT NULL COMMENT '展示名',
   type  varchar(50) DEFAULT NULL COMMENT '类型',
   required  tinyint(1) DEFAULT '1' COMMENT '是否必须: 0否, 1是',
   default_value  varchar(200) DEFAULT NULL COMMENT '参数的默认值',
   description  varchar(200) DEFAULT NULL COMMENT '描述',
   details  varchar(500) DEFAULT NULL COMMENT '变量的详细说明',
  PRIMARY KEY ( id )
)  COMMENT='apiservice 参数表';


CREATE TABLE `dss_apiservice_api_version` (
   `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
   `api_id` bigint(20) NOT NULL COMMENT '服务的ID',
   `version` varchar(50) NOT NULL COMMENT '服务对应的版本信息',
   `bml_resource_id` varchar(50) NOT NULL COMMENT 'bml资源id',
   `bml_version` varchar(20) NOT NULL COMMENT 'bml版本',
   `source` varchar(200) DEFAULT NULL COMMENT '来源',
   `creator` varchar(50) DEFAULT NULL COMMENT '创建者',
   `create_time`timestamp NOT NULL COMMENT '创建时间',
   `status` tinyint(1) default '1' COMMENT '0表示被禁用，1表示正在运行',
   `metadata_info` varchar(5000) NOT NULL COMMENT '发布者库表信息',
   `auth_id` varchar(200) NOT NULL COMMENT '用于与datamap交互的UUID',
   `datamap_order_no` varchar(200) DEFAULT NULL COMMENT 'datamap审批单号码',
   PRIMARY KEY(`id`)
)  COMMENT='服务api版本表';


CREATE TABLE `dss_apiservice_access_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `api_id` bigint(20) NOT NULL COMMENT '服务id',
  `api_version_id` bigint(20) NOT NULL COMMENT '版本id',
  `api_name` varchar(50) NOT NULL COMMENT '服务名称',
  `login_user` varchar(50) NOT NULL COMMENT '提交用户',
  `execute_user` varchar(50) DEFAULT NULL COMMENT '代理执行用户',
  `api_publisher` varchar(50) NOT NULL COMMENT 'api创建者',
  `access_time` timestamp NOT null  COMMENT '访问时间',
  PRIMARY KEY(`id`)
)  COMMENT='apiservice 访问信息表';