INSERT INTO `dss_application` (`id`, `name`, `url`, `is_user_need_init`, `level`, `user_init_url`, `exists_project_service`, `project_url`, `enhance_json`, `if_iframe`, `homepage_url`, `redirect_url`) VALUES (NULL, 'linkis', null, '0', '1', NULL, '0', '/home', NULL, '0', '/home', NULL);
INSERT INTO `dss_application` (`id`, `name`, `url`, `is_user_need_init`, `level`, `user_init_url`, `exists_project_service`, `project_url`, `enhance_json`, `if_iframe`, `homepage_url`, `redirect_url`) VALUES (NULL, 'visualis', null, '0', '1', NULL, '0', NULL, NULL, '1', NULL, NULL);
INSERT INTO `dss_application` (`id`, `name`, `url`, `is_user_need_init`, `level`, `user_init_url`, `exists_project_service`, `project_url`, `enhance_json`, `if_iframe`, `homepage_url`, `redirect_url`) VALUES (NULL, 'workflow', null, '0', '1', NULL, '0', '/workflow', NULL, '0', '/project', NULL);
INSERT INTO `dss_application` (`id`, `name`, `url`, `is_user_need_init`, `level`, `user_init_url`, `exists_project_service`, `project_url`, `enhance_json`, `if_iframe`, `homepage_url`, `redirect_url`) VALUES (NULL, 'console', null, '0', '1', NULL, '0', '/console', NULL, '0', '/console', NULL);

SELECT @linkis_appid:=id from dss_application WHERE `name` = 'linkis';
SELECT @visualis_appid:=id from dss_application WHERE `name` = 'visualis';
SELECT @workflow_appid:=id from dss_application WHERE `name` = 'workflow';
INSERT INTO `dss_workflow_node` (`id`, `icon`, `node_type`, `application_id`, `submit_to_scheduler`, `enable_copy`, `should_creation_before_node`, `support_jump`, `jump_url`) VALUES (NULL, NULL, 'linkis.python.python', @linkis_appid, '1', '1', '0', '1', NULL);
INSERT INTO `dss_workflow_node` (`id`, `icon`, `node_type`, `application_id`, `submit_to_scheduler`, `enable_copy`, `should_creation_before_node`, `support_jump`, `jump_url`) VALUES (NULL, NULL, 'linkis.spark.py', @linkis_appid, '1', '1', '0', '1', NULL);
INSERT INTO `dss_workflow_node` (`id`, `icon`, `node_type`, `application_id`, `submit_to_scheduler`, `enable_copy`, `should_creation_before_node`, `support_jump`, `jump_url`) VALUES (NULL, NULL, 'linkis.spark.sql', @linkis_appid, '1', '1', '0', '1', NULL);
INSERT INTO `dss_workflow_node` (`id`, `icon`, `node_type`, `application_id`, `submit_to_scheduler`, `enable_copy`, `should_creation_before_node`, `support_jump`, `jump_url`) VALUES (NULL, NULL, 'linkis.spark.scala', @linkis_appid, '1', '1', '0', '1', NULL);
INSERT INTO `dss_workflow_node` (`id`, `icon`, `node_type`, `application_id`, `submit_to_scheduler`, `enable_copy`, `should_creation_before_node`, `support_jump`, `jump_url`) VALUES (NULL, NULL, 'linkis.hive.hql', @linkis_appid, '1', '1', '0', '1', NULL);
INSERT INTO `dss_workflow_node` (`id`, `icon`, `node_type`, `application_id`, `submit_to_scheduler`, `enable_copy`, `should_creation_before_node`, `support_jump`, `jump_url`) VALUES (NULL, NULL, 'linkis.control.empty', @linkis_appid, '1', '1', '0', '0', NULL);
INSERT INTO `dss_workflow_node` (`id`, `icon`, `node_type`, `application_id`, `submit_to_scheduler`, `enable_copy`, `should_creation_before_node`, `support_jump`, `jump_url`) VALUES (NULL, NULL, 'linkis.appjoint.sendemail', @linkis_appid, '1', '1', '0', '0', NULL);
INSERT INTO `dss_workflow_node` (`id`, `icon`, `node_type`, `application_id`, `submit_to_scheduler`, `enable_copy`, `should_creation_before_node`, `support_jump`, `jump_url`) VALUES (NULL, NULL, 'linkis.appjoint.visualis.display', @visualis_appid, '1', '1', '1', '1', NULL);
INSERT INTO `dss_workflow_node` (`id`, `icon`, `node_type`, `application_id`, `submit_to_scheduler`, `enable_copy`, `should_creation_before_node`, `support_jump`, `jump_url`) VALUES (NULL, NULL, 'linkis.appjoint.visualis.dashboard', @visualis_appid, '1', '1', '1', '1', NULL);
INSERT INTO `dss_workflow_node` (`id`, `icon`, `node_type`, `application_id`, `submit_to_scheduler`, `enable_copy`, `should_creation_before_node`, `support_jump`, `jump_url`) VALUES (NULL, NULL, 'linkis.appjoint.eventchecker.eventsender', @linkis_appid, '1', '1', '0', '0', NULL);
INSERT INTO `dss_workflow_node` (`id`, `icon`, `node_type`, `application_id`, `submit_to_scheduler`, `enable_copy`, `should_creation_before_node`, `support_jump`, `jump_url`) VALUES (NULL, NULL, 'linkis.appjoint.eventchecker.eventreceiver', @linkis_appid, '1', '1', '0', '0', NULL);
INSERT INTO `dss_workflow_node` (`id`, `icon`, `node_type`, `application_id`, `submit_to_scheduler`, `enable_copy`, `should_creation_before_node`, `support_jump`, `jump_url`) VALUES (NULL, NULL, 'linkis.appjoint.datachecker', @linkis_appid, '1', '1', '0', '0', NULL);
INSERT INTO `dss_workflow_node` (`id`, `icon`, `node_type`, `application_id`, `submit_to_scheduler`, `enable_copy`, `should_creation_before_node`, `support_jump`, `jump_url`) VALUES (NULL, NULL, 'workflow.subflow', @workflow_appid, '1', '0', '1', '1', NULL);



INSERT INTO `dss_project_taxonomy` (`id`, `name`, `description`, `creator_id`, `create_time`, `update_time`) VALUES (NULL, 'My project', NULL, '-1', NULL, NULL);

INSERT INTO `dss_flow_taxonomy` (`id`, `name`, `description`, `creator_id`, `create_time`, `update_time`, `project_id`) VALUES (NULL, 'My workflow', NULL, NULL, NULL,NULL, '-1');

UPDATE `dss_application` SET url = 'http://GATEWAY_INSTALL_IP_2:GATEWAY_PORT' WHERE `name` in('linkis','workflow');
UPDATE `dss_application` SET url = 'http://VISUALIS_NGINX_IP_2:VISUALIS_NGINX_PORT' WHERE `name` in('visualis');
UPDATE `dss_application` SET project_url = 'http://VISUALIS_NGINX_IP_2:VISUALIS_NGINX_PORT/dss/visualis/#/project/${projectId}',homepage_url = 'http://VISUALIS_NGINX_IP_2:VISUALIS_NGINX_PORT/dss/visualis/#/projects' WHERE `name` in('visualis');
UPDATE `dss_workflow_node` SET jump_url = 'http://VISUALIS_NGINX_IP_2:VISUALIS_NGINX_PORT/dss/visualis/#/project/${projectId}/display/${nodeId}' where node_type = 'linkis.appjoint.visualis.display';
UPDATE `dss_workflow_node` SET jump_url = 'http://VISUALIS_NGINX_IP_2:VISUALIS_NGINX_PORT/dss/visualis/#/project/${projectId}/portal/${nodeId}/portalName/${nodeName}' where node_type = 'linkis.appjoint.visualis.dashboard';


INSERT INTO `linkis_application` (`id`, `name`, `chinese_name`, `description`) VALUES (NULL, 'visualis', NULL, NULL);
select @application_id:=id from `linkis_application` where `name` = 'visualis';
INSERT INTO `linkis_config_key` (`id`, `key`, `description`, `name`, `application_id`, `default_value`, `validate_type`, `validate_range`, `is_hidden`, `is_advanced`, `level`) VALUES (NULL, 'spark.executor.instances', '取值范围：1-40，单位：个', '执行器实例最大并发数', @application_id, '2', 'NumInterval', '[1,40]', '0', '0', '2');
INSERT INTO `linkis_config_key` (`id`, `key`, `description`, `name`, `application_id`, `default_value`, `validate_type`, `validate_range`, `is_hidden`, `is_advanced`, `level`) VALUES (NULL, 'spark.executor.cores', '取值范围：1-8，单位：个', '执行器核心个数', @application_id, '2', 'NumInterval', '[1,2]', '1', '0', '1');
INSERT INTO `linkis_config_key` (`id`, `key`, `description`, `name`, `application_id`, `default_value`, `validate_type`, `validate_range`, `is_hidden`, `is_advanced`, `level`) VALUES (NULL, 'spark.executor.memory', '取值范围：3-15，单位：G', '执行器内存大小', @application_id, '3', 'NumInterval', '[3,15]', '0', '0', '3');
INSERT INTO `linkis_config_key` (`id`, `key`, `description`, `name`, `application_id`, `default_value`, `validate_type`, `validate_range`, `is_hidden`, `is_advanced`, `level`) VALUES (NULL, 'spark.driver.cores', '取值范围：只能取1，单位：个', '驱动器核心个数', @application_id, '1', 'NumInterval', '[1,1]', '1', '1', '1');
INSERT INTO `linkis_config_key` (`id`, `key`, `description`, `name`, `application_id`, `default_value`, `validate_type`, `validate_range`, `is_hidden`, `is_advanced`, `level`) VALUES (NULL, 'spark.driver.memory', '取值范围：1-15，单位：G', '驱动器内存大小', @application_id, '2', 'NumInterval', '[1,15]', '0', '0', '1');
INSERT INTO `linkis_config_key` (`id`, `key`, `description`, `name`, `application_id`, `default_value`, `validate_type`, `validate_range`, `is_hidden`, `is_advanced`, `level`) VALUES (NULL, 'wds.linkis.instance', '范围：1-3，单位：个', 'spark引擎最大并发数', @application_id, '3', 'NumInterval', '[1,3]', '0', '0', '1');

select @key_id1:=id from `linkis_config_key` where `application_id` = @application_id and `key` = 'spark.executor.instances';
select @key_id2:=id from `linkis_config_key` where `application_id` = @application_id and `key` = 'spark.executor.cores';
select @key_id3:=id from `linkis_config_key` where `application_id` = @application_id and `key` = 'spark.executor.memory';
select @key_id4:=id from `linkis_config_key` where `application_id` = @application_id and `key` = 'spark.driver.cores';
select @key_id5:=id from `linkis_config_key` where `application_id` = @application_id and `key` = 'spark.driver.memory';
select @key_id6:=id from `linkis_config_key` where `application_id` = @application_id and `key` = 'wds.linkis.instance';

SELECT @tree_id1:=t.id from linkis_config_tree t LEFT JOIN  linkis_application a on t.application_id = a.id WHERE t.`name` = 'spark资源设置' and a.`name` = 'spark';
SELECT @tree_id2:=t.id from linkis_config_tree t LEFT JOIN  linkis_application a on t.application_id = a.id WHERE t.`name` = 'spark引擎设置' and a.`name` = 'spark';

insert into `linkis_config_key_tree` VALUES(NULL,@key_id1,@tree_id1);
insert into `linkis_config_key_tree` VALUES(NULL,@key_id2,@tree_id1);
insert into `linkis_config_key_tree` VALUES(NULL,@key_id3,@tree_id1);
insert into `linkis_config_key_tree` VALUES(NULL,@key_id4,@tree_id1);
insert into `linkis_config_key_tree` VALUES(NULL,@key_id5,@tree_id1);
insert into `linkis_config_key_tree` VALUES(NULL,@key_id6,@tree_id2);
