-- TODO 这里只适用于第一次安装时。如果是更新的话dss_appconn表不能先删除再插入，因为其他表如dss_workspace_appconn_role关联了appconn_id(不能变)，需要使用update、alter语句更新

INSERT INTO `dss_appconn` (`appconn_name`, `is_user_need_init`, `level`, `if_iframe`, `is_external`, `reference`, `class_name`, `appconn_class_path`, `resource`)
VALUES ('datachecker', 0, 1, 1, 1, NULL, 'com.webank.wedatasphere.dss.appconn.datachecker.DataCheckerAppConn', 'DSS_INSTALL_HOME_VAL/dss-appconns/datachecker', '');

select @datachecker_appconnId:=id from `dss_appconn` where `appconn_name` = 'datachecker';

INSERT INTO `dss_appconn_instance` (`appconn_id`, `label`, `url`, `enhance_json`, `homepage_uri`)
VALUES (@datachecker_appconnId, 'DEV', 'datachecker', '', '');

insert into `dss_workflow_node` (`name`, `appconn_name`, `node_type`, `jump_type`, `support_jump`, `submit_to_scheduler`, `enable_copy`, `should_creation_before_node`, `icon_path`)
values('datachecker','datachecker','linkis.appconn.datachecker','0','0','1','1','0','icons/datachecker.icon');

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
select @visualis_node_ui_lable_name_1:=id from `dss_workflow_node_ui` where `lable_name` = '节点名' limit 1;
select @visualis_node_ui_lable_name_2:=id from `dss_workflow_node_ui` where `lable_name` = '节点描述' limit 1;
select @visualis_node_ui_lable_name_3:=id from `dss_workflow_node_ui` where `lable_name` = '业务标签' limit 1;
select @visualis_node_ui_lable_name_4:=id from `dss_workflow_node_ui` where `lable_name` = '应用标签' limit 1;
select @visualis_node_ui_lable_name_5:=id from `dss_workflow_node_ui` where `lable_name` = '是否复用引擎' limit 1;
select @visualis_node_ui_lable_name_6:=id from `dss_workflow_node_ui` where `lable_name` = '绑定上游节点' limit 1;

INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_displayId, @visualis_node_ui_lable_name_1);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_displayId, @visualis_node_ui_lable_name_2);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_displayId, @visualis_node_ui_lable_name_3);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_displayId, @visualis_node_ui_lable_name_4);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_displayId, @visualis_node_ui_lable_name_5);

INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_dashboardId, @visualis_node_ui_lable_name_1);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_dashboardId, @visualis_node_ui_lable_name_2);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_dashboardId, @visualis_node_ui_lable_name_3);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_dashboardId, @visualis_node_ui_lable_name_5);

INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_widgetId, @visualis_node_ui_lable_name_1);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_widgetId, @visualis_node_ui_lable_name_2);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_widgetId, @visualis_node_ui_lable_name_3);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_widgetId, @visualis_node_ui_lable_name_4);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_widgetId, @visualis_node_ui_lable_name_5);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_widgetId, @visualis_node_ui_lable_name_6);

INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_viewId, @visualis_node_ui_lable_name_1);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_viewId, @visualis_node_ui_lable_name_2);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_viewId, @visualis_node_ui_lable_name_3);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_viewId, @visualis_node_ui_lable_name_4);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@dss_visualis_viewId, @visualis_node_ui_lable_name_5);