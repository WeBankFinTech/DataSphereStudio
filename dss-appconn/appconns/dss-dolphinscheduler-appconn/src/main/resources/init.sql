select @dss_appconn_dolphinschedulerId:=id from `dss_appconn` where `appconn_name` = 'dolphinscheduler';

delete from `dss_appconn_instance` where `appconn_id`=@dss_appconn_dolphinschedulerId;
delete from `dss_appconn` where `appconn_name`='dolphinscheduler';
delete from  `dss_workspace_menu_appconn` WHERE `appconn_id` = @dss_appconn_dolphinschedulerId;

INSERT INTO `dss_appconn` (`appconn_name`, `is_user_need_init`, `level`, `if_iframe`, `is_external`, `reference`, `class_name`, `appconn_class_path`, `resource`)
VALUES ('dolphinscheduler', 0, 1, NULL, 0, NULL, 'com.webank.wedatasphere.dss.appconn.dolphinscheduler.DolphinSchedulerAppConn', '', '');

select @dss_appconn_dolphinschedulerId:=id from `dss_appconn` where `appconn_name` = 'dolphinscheduler';
insert into `dss_appconn_instance` (`appconn_id`, `label`, `url`, `enhance_json`, `homepage_uri`) values(@dss_appconn_dolphinschedulerId,'DEV','http://APPCONN_INSTALL_IP:APPCONN_INSTALL_PORT/','','dolphinscheduler/ui/#/home');

INSERT INTO `dss_workspace_menu_appconn` (`appconn_id`, `menu_id`, `title_en`, `title_cn`, `desc_en`, `desc_cn`, `labels_en`, `labels_cn`, `is_active`, `access_button_en`, `access_button_cn`, `manual_button_en`, `manual_button_cn`, `manual_button_url`, `icon`, `order`, `create_by`, `create_time`, `last_update_time`, `last_update_user`, `image`)
VALUES(@dss_appconn_dolphinschedulerId,'3','dolphinscheduler','dolphinscheduler','Apache DolphinScheduler——A distributed and extensible workflow scheduler platform with powerful DAG visual interfaces.','Apache DolphinScheduler——分布式易扩展的可视化工作流任务调度平台.','scheduling, workflow','调度,工作流','1','enter dolphinscheduler','进入DolphinScheduler','user manual','用户手册','https://github.com/WeBankFinTech/DataSphereStudio','diaoduxitong-logo',NULL,NULL,NULL,NULL,NULL,'diaoduxitong-icon');
