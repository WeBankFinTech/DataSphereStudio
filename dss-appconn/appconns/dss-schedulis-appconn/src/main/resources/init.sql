select @dss_appconn_schedulisId:=id from `dss_appconn` where `appconn_name` = 'schedulis';

delete from `dss_appconn_instance` where `appconn_id`=@dss_appconn_schedulisId;
delete from `dss_appconn` where `appconn_name`='schedulis';

INSERT INTO `dss_appconn` (`appconn_name`, `is_user_need_init`, `level`, `if_iframe`, `is_external`, `reference`, `class_name`, `appconn_class_path`, `resource`)
VALUES ('schedulis', 0, 1, NULL, 0, NULL, 'com.webank.wedatasphere.dss.appconn.schedulis.SchedulisAppConn', 'DSS_INSTALL_HOME_VAL/dss-appconns/schedulis/lib', '');

select @dss_appconn_schedulisId:=id from `dss_appconn` where `appconn_name` = 'schedulis';
insert into `dss_appconn_instance` (`appconn_id`, `label`, `url`, `enhance_json`, `homepage_uri`) values(@dss_appconn_schedulisId,'DEV','http://APPCONN_INSTALL_IP:APPCONN_INSTALL_PORT/','','');

select @dss_schedulis_applicationId:=id from `dss_application` WHERE `name` ='schedulis';
delete from  `dss_workspace_menu_appconn`  WHERE  title_en='Schedulis';
INSERT INTO `dss_workspace_menu_appconn` (`appconn_id`, `menu_id`, `title_en`, `title_cn`, `desc_en`, `desc_cn`, `labels_en`, `labels_cn`, `is_active`, `access_button_en`, `access_button_cn`, `manual_button_en`, `manual_button_cn`, `manual_button_url`, `icon`, `order`, `create_by`, `create_time`, `last_update_time`, `last_update_user`, `image`)
VALUES(@dss_schedulis_applicationId,'3','Schedulis','Schedulis','Description for Schedulis.','Schedulis描述','scheduling, workflow','调度,工作流','1','enter Schedulis','进入Schedulis','user manual','用户手册','http://127.0.0.1:8088/wiki/scriptis/manual/workspace_cn.html','diaoduxitong-logo',NULL,NULL,NULL,NULL,NULL,'diaoduxitong-icon');
