-- 适用于第一次安装时
select @sparketl_appconnId:=id from `dss_appconn` where `appconn_name` = 'sparketl';
delete from `dss_appconn_instance` where `appconn_id` = @sparketl_appconnId;

delete from dss_appconn where appconn_name = "sparketl";
INSERT INTO `dss_appconn` (`appconn_name`, `is_user_need_init`, `level`, `if_iframe`, `is_external`, `reference`, `class_name`, `appconn_class_path`, `resource`)
VALUES ('sparketl', 0, 1, 1, 1, NULL, 'com.webank.wedatasphere.dss.appconn.sparketl.sparketlAppConn', 'DSS_INSTALL_HOME_VAL/dss-appconns/sparketl', '');

select @sparketl_appconnId:=id from `dss_appconn` where `appconn_name` = 'sparketl';

INSERT INTO `dss_appconn_instance` (`appconn_id`, `label`, `url`, `enhance_json`, `homepage_uri`)
VALUES (@sparketl_appconnId, 'DEV', 'http://APPCONN_INSTALL_IP:APPCONN_INSTALL_PORT/', '', '#/sparketl');

delete from dss_workflow_node where appconn_name = "sparketl";
insert into `dss_workflow_node` (`name`, `appconn_name`, `node_type`, `jump_type`, `support_jump`, `submit_to_scheduler`, `enable_copy`, `should_creation_before_node`, `icon_path`)
values('sparketl','sparketl','linkis.appconn.sparketl.bml2linkis','1','1','1','1','0','icons/sparketl.icon');

select @sparketl_nodeId:=id from `dss_workflow_node` where `node_type` = 'linkis.appconn.sparketl.bml2linkis';

delete from `dss_workflow_node_to_group` where `node_id`=@sparketl_nodeId;
delete from `dss_workflow_node_to_ui` where `workflow_node_id`=@sparketl_nodeId;

-- 查找节点所属组的id
select @sparketl_node_groupId:=id from `dss_workflow_node_group` where `name` = '数据交换';

INSERT INTO `dss_workflow_node_to_group`(`node_id`,`group_id`) values (@sparketl_nodeId, @sparketl_node_groupId);

-- 考虑表中有的是重复记录，最好加上limit 1
select @sparketl_node_ui_lable_name_1:=id from `dss_workflow_node_ui` where `lable_name` = '节点名' limit 1;
select @sparketl_node_ui_lable_name_2:=id from `dss_workflow_node_ui` where `lable_name` = '节点描述' limit 1;
select @sparketl_node_ui_lable_name_3:=id from `dss_workflow_node_ui` where `lable_name` = '业务标签' limit 1;
select @sparketl_node_ui_lable_name_4:=id from `dss_workflow_node_ui` where `lable_name` = '应用标签' limit 1;
select @sparketl_node_ui_lable_name_5:=id from `dss_workflow_node_ui` where `lable_name` = 'wds-linkis-yarnqueue' limit 1;
select @sparketl_node_ui_lable_name_6:=id from `dss_workflow_node_ui` where `lable_name` = 'spark-driver-memory' limit 1;
select @sparketl_node_ui_lable_name_7:=id from `dss_workflow_node_ui` where `lable_name` = 'spark-executor-memory' limit 1;
select @sparketl_node_ui_lable_name_8:=id from `dss_workflow_node_ui` where `lable_name` = 'spark-executor-cores' limit 1;
select @sparketl_node_ui_lable_name_9:=id from `dss_workflow_node_ui` where `lable_name` = 'spark-executor-instances' limit 1;
select @sparketl_node_ui_lable_name_10:=id from `dss_workflow_node_ui` where `lable_name` = '是否复用引擎' limit 1;

INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@sparketl_nodeId, @sparketl_node_ui_lable_name_1);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@sparketl_nodeId, @sparketl_node_ui_lable_name_2);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@sparketl_nodeId, @sparketl_node_ui_lable_name_3);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@sparketl_nodeId, @sparketl_node_ui_lable_name_4);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@sparketl_nodeId, @sparketl_node_ui_lable_name_5);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@sparketl_nodeId, @sparketl_node_ui_lable_name_6);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@sparketl_nodeId, @sparketl_node_ui_lable_name_7);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@sparketl_nodeId, @sparketl_node_ui_lable_name_8);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@sparketl_nodeId, @sparketl_node_ui_lable_name_9);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@sparketl_nodeId, @sparketl_node_ui_lable_name_10);
