-- 适用于第一次安装时
delete from dss_appconn where appconn_name='dolphinscheduler';
INSERT INTO `dss_appconn` (`appconn_name`, `is_user_need_init`, `level`, `if_iframe`, `is_external`, `reference`, `class_name`, `appconn_class_path`, `resource`)
VALUES ('dolphinscheduler', 0, 1, 1, 1, NULL, 'com.webank.wedatasphere.dss.appconn.dolphinscheduler.DolphinSchedulerAppConn', 'DSS_INSTALL_HOME_VAL/dss-appconns/dolphinscheduler', '');

select @dolphinscheduler_appconnId:=id from `dss_appconn` where `appconn_name` = 'dolphinscheduler';

insert into `dss_appconn_instance` (`appconn_id`, `label`, `url`, `enhance_json`, `homepage_uri`)
values(@dolphinscheduler_appconnId,'DEV','http://APPCONN_INSTALL_IP:APPCONN_INSTALL_PORT/dolphinscheduler','','');

-- 看appconn组件是要归属于哪个菜单
select @dolphinscheduler_menuId:=id from dss_workspace_menu where name = "生产运维";

DELETE FROM dss_workspace_menu_appconn where title_en='dolphinscheduler';
INSERT INTO `dss_workspace_menu_appconn` (`appconn_id`, `menu_id`, `title_en`, `title_cn`, `desc_en`, `desc_cn`, `labels_en`, `labels_cn`, `is_active`, `access_button_en`, `access_button_cn`, `manual_button_en`, `manual_button_cn`, `manual_button_url`, `icon`, `order`, `create_by`, `create_time`, `last_update_time`, `last_update_user`, `image`)
VALUES(@dolphinscheduler_appconnId, @dolphinscheduler_menuId,'dolphinscheduler','dolphinscheduler','empty desc','empty desc','scheduling, workflow','调度,工作流','1','enter dolphinscheduler','进入dolphinscheduler','user manual','用户手册','manual_url','diaoduxitong-logo',NULL,NULL,NULL,NULL,NULL,'diaoduxitong-icon');

delete from dss_workspace_dictionary where dic_key = "pom_work_flow_ds";
delete from dss_workspace_dictionary where dic_key = "pom_work_flow_ds_DAG";
insert  into `dss_workspace_dictionary`(`workspace_id`,`parent_key`,`dic_name`,`dic_name_en`,`dic_key`,`dic_value`,`dic_value_en`,`title`,`title_en`,`url`,`url_type`,`icon`,`order_num`,`remark`,`create_user`,`create_time`,`update_user`,`update_time`) values (0,'p_orchestrator_mode','DS工作流','Workflow_DS','pom_work_flow_ds','radio',NULL,NULL,NULL,NULL,0,'gongzuoliu-icon',1,'工程编排模式-DS工作流','SYSTEM','2022-03-21 14:25:35',NULL,'2022-03-21 14:25:35');
insert  into `dss_workspace_dictionary`(`workspace_id`,`parent_key`,`dic_name`,`dic_name_en`,`dic_key`,`dic_value`,`dic_value_en`,`title`,`title_en`,`url`,`url_type`,`icon`,`order_num`,`remark`,`create_user`,`create_time`,`update_user`,`update_time`) values (0,'pom_work_flow_ds','DAG','DAG','pom_work_flow_ds_DAG',NULL,NULL,NULL,NULL,NULL,0,NULL,1,'工程编排模式-DS工作流-DAG','SYSTEM','2022-03-21 14:25:35',NULL,'2022-03-21 14:25:35');