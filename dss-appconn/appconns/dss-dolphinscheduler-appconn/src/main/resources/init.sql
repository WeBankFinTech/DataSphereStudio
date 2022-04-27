-- 适用于第一次安装时
INSERT INTO `dss_appconn` (`appconn_name`, `is_user_need_init`, `level`, `if_iframe`, `is_external`, `reference`, `class_name`, `appconn_class_path`, `resource`)
VALUES ('dolphinscheduler', 0, 1, 1, 1, NULL, 'com.webank.wedatasphere.dss.appconn.dolphinscheduler.DolphinSchedulerAppConn', 'DSS_INSTALL_HOME_VAL/dss-appconns/dolphinscheduler', '');

select @dolphinscheduler_appconnId:=id from `dss_appconn` where `appconn_name` = 'dolphinscheduler';

insert into `dss_appconn_instance` (`appconn_id`, `label`, `url`, `enhance_json`, `homepage_uri`)
values(@dolphinscheduler_appconnId,'DEV','http://APPCONN_INSTALL_IP:APPCONN_INSTALL_PORT/','','');

-- 看appconn组件是要归属于哪个菜单
select @dolphinscheduler_menuId:id from dss_workspace_menu where name = "生产运维";

INSERT INTO `dss_workspace_menu_appconn` (`appconn_id`, `menu_id`, `title_en`, `title_cn`, `desc_en`, `desc_cn`, `labels_en`, `labels_cn`, `is_active`, `access_button_en`, `access_button_cn`, `manual_button_en`, `manual_button_cn`, `manual_button_url`, `icon`, `order`, `create_by`, `create_time`, `last_update_time`, `last_update_user`, `image`)
VALUES(@dolphinscheduler_appconnId, @dolphinscheduler_menuId,'dolphinscheduler','dolphinscheduler','empty desc','empty desc','scheduling, workflow','调度,工作流','1','enter dolphinscheduler','进入dolphinscheduler','user manual','用户手册','manual_url','diaoduxitong-logo',NULL,NULL,NULL,NULL,NULL,'diaoduxitong-icon');
