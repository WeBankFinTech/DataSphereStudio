-- TODO 这里只适用于第一次安装时。如果是更新的话dss_appconn表不能先删除再插入，因为其他表如dss_workspace_appconn_role关联了appconn_id(不能变)，需要使用update、alter语句更新
select @datachecker_appconnId:=id from `dss_appconn` where `appconn_name` = 'datachecker';
delete from `dss_appconn_instance` where `appconn_id` = @datachecker_appconnId;

delete from dss_appconn where appconn_name = "datachecker";
INSERT INTO `dss_appconn` (`appconn_name`, `is_user_need_init`, `level`, `if_iframe`, `is_external`, `reference`, `class_name`, `appconn_class_path`, `resource`)
VALUES ('datachecker', 0, 1, 1, 1, NULL, 'com.webank.wedatasphere.dss.appconn.datachecker.DataCheckerAppConn', 'DSS_INSTALL_HOME_VAL/dss-appconns/datachecker', '');

select @datachecker_appconnId:=id from `dss_appconn` where `appconn_name` = 'datachecker';

INSERT INTO `dss_appconn_instance` (`appconn_id`, `label`, `url`, `enhance_json`, `homepage_uri`)
VALUES (@datachecker_appconnId, 'DEV', 'datachecker', '{\"job.datachecker.jdo.option.name\":\"job\",\"job.datachecker.jdo.option.url\":\"DATACHECKER_JOB_JDBC_URL\",\"job.datachecker.jdo.option.username\":\"DATACHECKER_JOB_JDBC_USERNAME\",\"job.datachecker.jdo.option.password\":\"DATACHECKER_JOB_JDBC_PASSWORD\",\"bdp.datachecker.jdo.option.name\":\"bdp\",\"bdp.datachecker.jdo.option.url\":\"DATACHECKER_BDP_JDBC_URL\",\"bdp.datachecker.jdo.option.username\":\"DATACHECKER_BDP_JDBC_USERNAME\",\"bdp.datachecker.jdo.option.password\":\"DATACHECKER_BDP_JDBC_PASSWORD\",\"bdp.datachecker.jdo.option.login.type\":\"base64\",\"bdp.mask.url\":\"http://BDP_MASK_IP:BDP_MASK_PORT/api/v1/mask-status?\",\"bdp.mask.app.id\":\"wtss\",\"bdp.mask.app.token\":\"20a0ccdfc0\"}', '');

delete from dss_workflow_node where appconn_name = "datachecker";
insert into `dss_workflow_node` (`name`, `appconn_name`, `node_type`, `jump_type`, `support_jump`, `submit_to_scheduler`, `enable_copy`, `should_creation_before_node`, `icon_path`)
values('datachecker','datachecker','linkis.appconn.datachecker','0','0','1','1','0','icons/datachecker.icon');

select @datachecker_nodeId:=id from `dss_workflow_node` where `node_type` = 'linkis.appconn.datachecker';

delete from `dss_workflow_node_to_group` where `node_id`=@datachecker_nodeId;
delete from `dss_workflow_node_to_ui` where `workflow_node_id`=@datachecker_nodeId;

-- 查找节点所属组的id
select @datachecker_node_groupId:=id from `dss_workflow_node_group` where `name` = '信号节点';

INSERT INTO `dss_workflow_node_to_group`(`node_id`,`group_id`) values (@datachecker_nodeId, @datachecker_node_groupId);

-- 考虑表中有的是重复记录，最好加上limit 1
select @datachecker_node_ui_lable_name_1:=id from `dss_workflow_node_ui` where `lable_name` = '节点名' limit 1;
select @datachecker_node_ui_lable_name_2:=id from `dss_workflow_node_ui` where `lable_name` = '节点描述' limit 1;
select @datachecker_node_ui_lable_name_3:=id from `dss_workflow_node_ui` where `lable_name` = '业务标签' limit 1;
select @datachecker_node_ui_lable_name_4:=id from `dss_workflow_node_ui` where `lable_name` = '应用标签' limit 1;
select @datachecker_node_ui_lable_name_5:=id from `dss_workflow_node_ui` where `lable_name` = '是否复用引擎' limit 1;
select @datachecker_node_ui_lable_name_6:=id from `dss_workflow_node_ui` where `lable_name` = 'source.type' limit 1;
select @datachecker_node_ui_lable_name_7:=id from `dss_workflow_node_ui` where `lable_name` = 'check.object' limit 1;
select @datachecker_node_ui_lable_name_8:=id from `dss_workflow_node_ui` where `lable_name` = 'max.check.hours' limit 1;
select @datachecker_node_ui_lable_name_9:=id from `dss_workflow_node_ui` where `lable_name` = 'job.desc' limit 1;

INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@datachecker_nodeId, @datachecker_node_ui_lable_name_1);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@datachecker_nodeId, @datachecker_node_ui_lable_name_2);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@datachecker_nodeId, @datachecker_node_ui_lable_name_3);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@datachecker_nodeId, @datachecker_node_ui_lable_name_4);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@datachecker_nodeId, @datachecker_node_ui_lable_name_5);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@datachecker_nodeId, @datachecker_node_ui_lable_name_6);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@datachecker_nodeId, @datachecker_node_ui_lable_name_7);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@datachecker_nodeId, @datachecker_node_ui_lable_name_8);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@datachecker_nodeId, @datachecker_node_ui_lable_name_9);
