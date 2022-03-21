select @dss_appconn_dolphinschedulerId:=id from `dss_appconn` where `appconn_name` = 'dolphinscheduler';

delete from `dss_appconn_instance` where `appconn_id`=@dss_appconn_dolphinschedulerId;
delete from `dss_appconn` where `appconn_name`='dolphinscheduler';

INSERT INTO `dss_appconn` (`appconn_name`, `is_user_need_init`, `level`, `if_iframe`, `is_external`, `reference`, `class_name`, `appconn_class_path`, `resource`)
VALUES ('dolphinscheduler', 0, 1, NULL, 0, NULL, 'com.webank.wedatasphere.dss.appconn.dolphinscheduler.DolphinschedulerAppConn', 'DSS_INSTALL_HOME_VAL/dss-appconns/dolphinscheduler/lib', '');

select @dss_appconn_dolphinschedulerId:=id from `dss_appconn` where `appconn_name` = 'dolphinscheduler';
insert into `dss_appconn_instance` (`appconn_id`, `label`, `url`, `enhance_json`, `homepage_url`, `redirect_url`) values(@dss_appconn_dolphinschedulerId,'DEV','http://APPCONN_INSTALL_IP:APPCONN_INSTALL_PORT/','','http://APPCONN_INSTALL_IP:APPCONN_INSTALL_PORT/','http://APPCONN_INSTALL_IP:APPCONN_INSTALL_PORT/');

delete from  `dss_application`  WHERE `name` ='dolphinscheduler';
INSERT  INTO `dss_application`(`name`,`url`,`is_user_need_init`,`level`,`user_init_url`,`exists_project_service`,`project_url`,`enhance_json`,`if_iframe`,`homepage_url`,`redirect_url`)
VALUES ('dolphinscheduler','http://APPCONN_INSTALL_IP:APPCONN_INSTALL_PORT',0,1,NULL,0,'','',1,'http://APPCONN_INSTALL_IP:APPCONN_INSTALL_PORT/dolphinscheduler/ui/#/home','');

select @dss_dolphinscheduler_applicationId:=id from `dss_application` WHERE `name` ='dolphinscheduler';
delete from  `dss_onestop_menu_application`  WHERE  title_en='dolphinscheduler';
INSERT INTO `dss_onestop_menu_application` (`application_id`, `onestop_menu_id`, `title_en`, `title_cn`, `desc_en`, `desc_cn`, `labels_en`, `labels_cn`, `is_active`, `access_button_en`, `access_button_cn`, `manual_button_en`, `manual_button_cn`, `manual_button_url`, `icon`, `order`, `create_by`, `create_time`, `last_update_time`, `last_update_user`, `image`)
VALUES(@dss_dolphinscheduler_applicationId,'3','dolphinscheduler','dolphinscheduler','Description for dolphinscheduler.','dolphinscheduler描述','scheduling, workflow','调度,工作流','1','enter dolphinscheduler','进入dolphinscheduler','user manual','用户手册','http://127.0.0.1:8088/wiki/scriptis/manual/workspace_cn.html','diaoduxitong-logo',NULL,NULL,NULL,NULL,NULL,'diaoduxitong-icon');
