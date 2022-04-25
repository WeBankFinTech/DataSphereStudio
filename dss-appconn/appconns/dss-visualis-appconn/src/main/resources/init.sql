-- TODO 这里只考虑第一次安装时。如果是更新的话dss_appconn表不能先删除再插入，因为其他表如dss_workspace_appconn_role关联了appconn_id，需要使用update、alter语句更新

INSERT INTO `dss_appconn` (`appconn_name`, `is_user_need_init`, `level`, `if_iframe`, `is_external`, `reference`, `class_name`, `appconn_class_path`, `resource`)
VALUES ('visualis', 0, 1, 1, 1, NULL, 'com.webank.wedatasphere.dss.appconn.visualis.VisualisAppConn', 'DSS_INSTALL_HOME_VAL/dss-appconns/visualis', '');

--delete from  `dss_workspace_menu_appconn`  WHERE  title_en='Visualis';
INSERT INTO  `dss_workspace_menu_appconn` (`appconn_id`, `menu_id`, `title_en`, `title_cn`, `desc_en`, `desc_cn`, `labels_en`, `labels_cn`, `is_active`, `access_button_en`, `access_button_cn`, `manual_button_en`, `manual_button_cn`, `manual_button_url`, `icon`, `order`, `create_by`, `create_time`, `last_update_time`, `last_update_user`, `image`)
 VALUES(@dss_visualis_applicationId,'2','Visualis','Visualis','Visualis is a data visualization BI tool based on Davinci, with Linkis as the kernel, it supports the analysis mode of data development exploration.','Visualis是基于宜信开源项目Davinci开发的数据可视化BI工具，以任意桥(Linkis)做为内核，支持拖拽式报表定义、图表联动、钻取、全局筛选、多维分析、实时查询等数据开发探索的分析模式，并做了水印、数据质量校验等金融级增强。'
 ,'visualization, statement','可视化,报表','1','enter Visualis','进入Visualis','user manual','用户手册','/manual_url','shujukeshihua-logo',NULL,NULL,NULL,NULL,NULL,'shujukeshihua-icon');

select @dss_appconn_visualisId:=id from `dss_appconn` where `appconn_name` = 'visualis';

-- delete from `dss_appconn_instance` where `homepage_uri` like '%visualis%';
INSERT INTO `dss_appconn_instance` (`appconn_id`, `label`, `url`, `enhance_json`, `homepage_uri`) VALUES (@dss_appconn_visualisId, 'DEV', 'http://APPCONN_INSTALL_IP:APPCONN_INSTALL_PORT/', '', 'dss/visualis/#/projects');

delete from `dss_workflow_node`  where `node_type` like '%visualis%';
insert into `dss_workflow_node` (`name`, `appconn_name`, `node_type`, `jump_type`, `support_jump`, `submit_to_scheduler`, `enable_copy`, `should_creation_before_node`, `icon_path`) values('display','visualis','linkis.appconn.visualis.display',1,'1','1','0','1','icons/display.icon');
insert into `dss_workflow_node` (`name`, `appconn_name`, `node_type`, `jump_type`, `support_jump`, `submit_to_scheduler`, `enable_copy`, `should_creation_before_node`, `icon_path`) values('dashboard','visualis','linkis.appconn.visualis.dashboard',1,'1','1','0','1','icons/dashboard.icon');
insert into `dss_workflow_node` (`name`, `appconn_name`, `node_type`, `jump_type`, `support_jump`, `submit_to_scheduler`, `enable_copy`, `should_creation_before_node`, `icon_path`) values('widget','visualis','linkis.appconn.visualis.widget',1,'1','1','0','1','icons/widget.icon');

select @dss_visualis_displayId:=id from `dss_workflow_node` where `node_type` = 'linkis.appconn.visualis.display';
select @dss_visualis_dashboardId:=id from `dss_workflow_node` where `node_type` = 'linkis.appconn.visualis.dashboard';
select @dss_visualis_dashwidgetId:=id from `dss_workflow_node` where `node_type` = 'linkis.appconn.visualis.widget';

delete from `dss_workflow_node_to_group` where `node_id`=@dss_visualis_displayId;
delete from `dss_workflow_node_to_group` where `node_id`=@dss_visualis_dashboardId;
delete from `dss_workflow_node_to_group` where `node_id`=@dss_visualis_dashwidgetId;


delete from `dss_workflow_node_to_ui` where `workflow_node_id`=@dss_visualis_displayId;
delete from `dss_workflow_node_to_ui` where `workflow_node_id`=@dss_visualis_dashboardId;
delete from `dss_workflow_node_to_ui` where `workflow_node_id`=@dss_visualis_dashwidgetId;


select @dss_visualis_displayId:=id from `dss_workflow_node` where `node_type` = 'linkis.appconn.visualis.display';
select @dss_visualis_dashboardId:=id from `dss_workflow_node` where `node_type` = 'linkis.appconn.visualis.dashboard';
select @dss_visualis_dashwidgetId:=id from `dss_workflow_node` where `node_type` = 'linkis.appconn.visualis.widget';

INSERT INTO `dss_workflow_node_to_group`(`node_id`,`group_id`) values (@dss_visualis_displayId,4);
INSERT INTO `dss_workflow_node_to_group`(`node_id`,`group_id`) values (@dss_visualis_dashboardId,4);
INSERT INTO `dss_workflow_node_to_group`(`node_id`,`group_id`) values (@dss_visualis_dashwidgetId,4);


INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_displayId,1);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_displayId,2);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_displayId,3);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_displayId,4);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_displayId,5);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_displayId,6);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_displayId,45);

INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_dashboardId,1);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_dashboardId,2);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_dashboardId,3);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_dashboardId,4);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_dashboardId,5);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_dashboardId,45);


INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_dashwidgetId,1);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_dashwidgetId,3);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_dashwidgetId,5);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_dashwidgetId,6);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_dashwidgetId,37);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_dashwidgetId,2);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_dashwidgetId,45);
