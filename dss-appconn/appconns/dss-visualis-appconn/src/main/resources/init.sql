-- TODO 这里只适用于第一次安装时。如果是更新的话dss_appconn表不能先删除再插入，因为其他表如dss_workspace_appconn_role关联了appconn_id(不能变)，需要使用update、alter语句更新

INSERT INTO `dss_appconn` (`appconn_name`, `is_user_need_init`, `level`, `if_iframe`, `is_external`, `reference`, `class_name`, `appconn_class_path`, `resource`)
VALUES ('visualis', 0, 1, 1, 1, NULL, 'com.webank.wedatasphere.dss.appconn.visualis.VisualisAppConn', 'DSS_INSTALL_HOME_VAL/dss-appconns/visualis', '');

select @visualis_appconnId:=id from `dss_appconn` where `appconn_name` = 'visualis';

-- delete from `dss_appconn_instance` where `homepage_uri` like '%visualis%';
INSERT INTO `dss_appconn_instance` (`appconn_id`, `label`, `url`, `enhance_json`, `homepage_uri`)
VALUES (@visualis_appconnId, 'DEV', 'http://APPCONN_INSTALL_IP:APPCONN_INSTALL_PORT/', '', 'dss/visualis/#/projects');

-- 看appconn组件是要归属于哪个菜单
select @visualis_menuId:id from dss_workspace_menu where name = "数据分析";
--delete from  `dss_workspace_menu_appconn`  WHERE  title_en='Visualis';
INSERT INTO `dss_workspace_menu_appconn` (`appconn_id`, `menu_id`, `title_en`, `title_cn`, `desc_en`, `desc_cn`, `labels_en`, `labels_cn`, `is_active`, `access_button_en`, `access_button_cn`, `manual_button_en`, `manual_button_cn`, `manual_button_url`, `icon`, `order`, `create_by`, `create_time`, `last_update_time`, `last_update_user`, `image`)
 VALUES(@visualis_appconnId,@visualis_menuId,'Visualis','Visualis','Visualis is a data visualization BI tool based on Davinci, with Linkis as the kernel, it supports the analysis mode of data development exploration.','Visualis是基于宜信开源项目Davinci开发的数据可视化BI工具，以任意桥(Linkis)做为内核，支持拖拽式报表定义、图表联动、钻取、全局筛选、多维分析、实时查询等数据开发探索的分析模式，并做了水印、数据质量校验等金融级增强。'
 ,'visualization, statement','可视化,报表','1','enter Visualis','进入Visualis','user manual','用户手册','/manual_url','shujukeshihua-logo',NULL,NULL,NULL,NULL,NULL,'shujukeshihua-icon');

-- delete from `dss_workflow_node`  where `node_type` like '%visualis%';
insert into `dss_workflow_node` (`name`, `appconn_name`, `node_type`, `jump_type`, `support_jump`, `submit_to_scheduler`, `enable_copy`, `should_creation_before_node`, `icon_path`)
values('display','visualis','linkis.appconn.visualis.display',1,'1','1','0','1','icons/display.icon');
insert into `dss_workflow_node` (`name`, `appconn_name`, `node_type`, `jump_type`, `support_jump`, `submit_to_scheduler`, `enable_copy`, `should_creation_before_node`, `icon_path`)
values('dashboard','visualis','linkis.appconn.visualis.dashboard',1,'1','1','0','1','icons/dashboard.icon');
insert into `dss_workflow_node` (`name`, `appconn_name`, `node_type`, `jump_type`, `support_jump`, `submit_to_scheduler`, `enable_copy`, `should_creation_before_node`, `icon_path`)
values('widget','visualis','linkis.appconn.visualis.widget',1,'1','1','0','1','icons/widget.icon');
insert into `dss_workflow_node` (`name`, `appconn_name`, `node_type`, `jump_type`, `support_jump`, `submit_to_scheduler`, `enable_copy`, `should_creation_before_node`, `icon_path`)
values('view','visualis','linkis.appconn.visualis.view',1,'1','1','0','1','icons/view.icon');

select @dss_visualis_displayId:=id from `dss_workflow_node` where `node_type` = 'linkis.appconn.visualis.display';
select @dss_visualis_dashboardId:=id from `dss_workflow_node` where `node_type` = 'linkis.appconn.visualis.dashboard';
select @dss_visualis_widgetId:=id from `dss_workflow_node` where `node_type` = 'linkis.appconn.visualis.widget';
select @dss_visualis_viewId:=id from `dss_workflow_node` where `node_type` = 'linkis.appconn.visualis.view';

delete from `dss_workflow_node_to_group` where `node_id`=@dss_visualis_displayId;
delete from `dss_workflow_node_to_group` where `node_id`=@dss_visualis_dashboardId;
delete from `dss_workflow_node_to_group` where `node_id`=@dss_visualis_widgetId;
delete from `dss_workflow_node_to_group` where `node_id`=@dss_visualis_viewId;

delete from `dss_workflow_node_to_ui` where `workflow_node_id`=@dss_visualis_displayId;
delete from `dss_workflow_node_to_ui` where `workflow_node_id`=@dss_visualis_dashboardId;
delete from `dss_workflow_node_to_ui` where `workflow_node_id`=@dss_visualis_widgetId;
delete from `dss_workflow_node_to_ui` where `workflow_node_id`=@dss_visualis_viewId;

-- 查找节点所属组的id
select @visualis_node_groupId:=id from `dss_workflow_node_group` where `name` = '数据可视化';

INSERT INTO `dss_workflow_node_to_group`(`node_id`,`group_id`) values (@dss_visualis_displayId, @visualis_node_groupId);
INSERT INTO `dss_workflow_node_to_group`(`node_id`,`group_id`) values (@dss_visualis_dashboardId, @visualis_node_groupId);
INSERT INTO `dss_workflow_node_to_group`(`node_id`,`group_id`) values (@dss_visualis_widgetId, @visualis_node_groupId);
INSERT INTO `dss_workflow_node_to_group`(`node_id`,`group_id`) values (@dss_visualis_viewId, @visualis_node_groupId);
-- 表中有的是重复记录，最好加上limit 1
select @visualis_node_ui_label_name_1:=id from `dss_workflow_node_ui` where `label_name` = '节点名' limit 1;
select @visualis_node_ui_label_name_2:=id from `dss_workflow_node_ui` where `label_name` = '节点描述' limit 1;
select @visualis_node_ui_label_name_3:=id from `dss_workflow_node_ui` where `label_name` = '业务标签' limit 1;
select @visualis_node_ui_label_name_4:=id from `dss_workflow_node_ui` where `label_name` = '应用标签' limit 1;
select @visualis_node_ui_label_name_5:=id from `dss_workflow_node_ui` where `label_name` = '是否复用引擎' limit 1;
select @visualis_node_ui_label_name_6:=id from `dss_workflow_node_ui` where `label_name` = '绑定上游节点' limit 1;

INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_displayId, @visualis_node_ui_label_name_1);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_displayId, @visualis_node_ui_label_name_2);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_displayId, @visualis_node_ui_label_name_3);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_displayId, @visualis_node_ui_label_name_4);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_displayId, @visualis_node_ui_label_name_5);

INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_dashboardId, @visualis_node_ui_label_name_1);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_dashboardId, @visualis_node_ui_label_name_2);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_dashboardId, @visualis_node_ui_label_name_3);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_dashboardId, @visualis_node_ui_label_name_5);

INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_widgetId, @visualis_node_ui_label_name_1);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_widgetId, @visualis_node_ui_label_name_2);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_widgetId, @visualis_node_ui_label_name_3);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_widgetId, @visualis_node_ui_label_name_4);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_widgetId, @visualis_node_ui_label_name_5);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_widgetId, @visualis_node_ui_label_name_6);

INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_viewId, @visualis_node_ui_label_name_1);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_viewId, @visualis_node_ui_label_name_2);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_viewId, @visualis_node_ui_label_name_3);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_viewId, @visualis_node_ui_label_name_4);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_viewId, @visualis_node_ui_label_name_5);