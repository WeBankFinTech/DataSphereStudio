INSERT INTO `dss_application` (`id`, `name`, `url`, `is_user_need_init`, `level`, `user_init_url`, `exists_project_service`, `project_url`, `enhance_json`, `if_iframe`, `homepage_url`, `redirect_url`) VALUES (NULL, 'schedulis', NULL, '0', '1', NULL, '0', NULL, NULL, '1', NULL, NULL);
UPDATE `dss_application` SET url = 'http://AZKABAN_ADRESS_IP_2:AZKABAN_ADRESS_PORT', project_url = 'http://AZKABAN_ADRESS_IP_2:AZKABAN_ADRESS_PORT/manager?project=${projectName}',homepage_url = 'http://AZKABAN_ADRESS_IP_2:AZKABAN_ADRESS_PORT/homepage' WHERE `name` in ('schedulis');
SELECT @linkis_id:=id FROM `dss_application` WHERE `name` = 'linkis';
insert into dss_workflow_node values(null,null,'linkis.shell.sh',@linkis_id,1,1,0,1,null);
