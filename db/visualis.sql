INSERT INTO `dss_application` (`id`, `name`, `url`, `is_user_need_init`, `level`, `user_init_url`, `exists_project_service`, `project_url`, `enhance_json`, `if_iframe`, `homepage_url`, `redirect_url`) VALUES (NULL, 'visualis', null, '0', '1', NULL, '0', NULL, NULL, '1', NULL, NULL);
SELECT @visualis_appid:=id from dss_application WHERE `name` = 'visualis';
INSERT INTO `dss_workflow_node` (`id`, `icon`, `node_type`, `application_id`, `submit_to_scheduler`, `enable_copy`, `should_creation_before_node`, `support_jump`, `jump_url`) VALUES (NULL, NULL, 'linkis.appjoint.visualis.display', @visualis_appid, '1', '1', '1', '1', NULL);
INSERT INTO `dss_workflow_node` (`id`, `icon`, `node_type`, `application_id`, `submit_to_scheduler`, `enable_copy`, `should_creation_before_node`, `support_jump`, `jump_url`) VALUES (NULL, NULL, 'linkis.appjoint.visualis.dashboard', @visualis_appid, '1', '1', '1', '1', NULL);UPDATE `dss_application` SET url = 'http://VISUALIS_NGINX_IP_2:VISUALIS_NGINX_PORT' WHERE `name` in('visualis');
UPDATE `dss_application` SET url = 'http://VISUALIS_NGINX_IP_2:VISUALIS_NGINX_PORT' WHERE `name` in('visualis');
UPDATE `dss_application` SET project_url = 'http://VISUALIS_NGINX_IP_2:VISUALIS_NGINX_PORT/dss/visualis/#/project/${projectId}',homepage_url = 'http://VISUALIS_NGINX_IP_2:VISUALIS_NGINX_PORT/dss/visualis/#/projects' WHERE `name` in('visualis');
UPDATE `dss_workflow_node` SET jump_url = 'http://VISUALIS_NGINX_IP_2:VISUALIS_NGINX_PORT/dss/visualis/#/project/${projectId}/display/${nodeId}' where node_type = 'linkis.appjoint.visualis.display';
UPDATE `dss_workflow_node` SET jump_url = 'http://VISUALIS_NGINX_IP_2:VISUALIS_NGINX_PORT/dss/visualis/#/project/${projectId}/portal/${nodeId}/portalName/${nodeName}' where node_type = 'linkis.appjoint.visualis.dashboard';
INSERT INTO `linkis_application` (`id`, `name`, `chinese_name`, `description`) VALUES (NULL, 'visualis', NULL, NULL);
select @application_id:=id from `linkis_application` where `name` = 'visualis';
























