-- TODO 这里只适用于第一次安装时。如果是更新的话dss_appconn表不能先删除再插入，因为其他表如dss_workspace_appconn_role关联了appconn_id(不能变)，需要使用update、alter语句更新
select @sendemail_appconnId:=id from `dss_appconn` where `appconn_name` = 'sendemail';
delete from `dss_appconn_instance` where `appconn_id` = @sendemail_appconnId;

INSERT INTO `dss_appconn` (`appconn_name`, `is_user_need_init`, `level`, `if_iframe`, `is_external`, `reference`, `class_name`, `appconn_class_path`, `resource`)
VALUES ('sendemail', 0, 1, 1, 1, NULL, 'com.webank.wedatasphere.dss.appconn.sendemail.SendEmailAppConn', 'DSS_INSTALL_HOME_VAL/dss-appconns/sendemail', '');

select @sendemail_appconnId:=id from `dss_appconn` where `appconn_name` = 'sendemail';

INSERT INTO `dss_appconn_instance` (`appconn_id`, `label`, `url`, `enhance_json`, `homepage_uri`)
VALUES (@sendemail_appconnId, 'DEV', 'sendemail', '{"email.host":"EMAIL_HOST","email.port":"EMAIL_PORT","email.username":"EMAIL_USERNAME","email.password":"EMAIL_PASSWORD","email.protocol":"EMAIL_PROTOCOL"}', '');

delete from dss_workflow_node where name = "sendemail";
insert into `dss_workflow_node` (`name`, `appconn_name`, `node_type`, `jump_type`, `support_jump`, `submit_to_scheduler`, `enable_copy`, `should_creation_before_node`, `icon_path`)
values('sendemail','sendemail','linkis.appconn.sendemail','0','0','1','1','0','icons/sendemail.icon');

select @sendemail_nodeId:=id from `dss_workflow_node` where `node_type` = 'linkis.appconn.sendemail';

delete from `dss_workflow_node_to_group` where `node_id`=@sendemail_nodeId;

delete from `dss_workflow_node_to_ui` where `workflow_node_id`=@sendemail_nodeId;

-- 查找节点所属组的id
select @sendemail_node_groupId:=id from `dss_workflow_node_group` where `name` = '数据输出';

INSERT INTO `dss_workflow_node_to_group`(`node_id`,`group_id`) values (@sendemail_nodeId, @sendemail_node_groupId);

-- 考虑表中有的是重复记录，最好加上limit 1
select @sendemail_node_ui_lable_name_1:=id from `dss_workflow_node_ui` where `lable_name` = '节点名' limit 1;
select @sendemail_node_ui_lable_name_2:=id from `dss_workflow_node_ui` where `lable_name` = '节点描述' limit 1;
select @sendemail_node_ui_lable_name_3:=id from `dss_workflow_node_ui` where `lable_name` = '业务标签' limit 1;
select @sendemail_node_ui_lable_name_4:=id from `dss_workflow_node_ui` where `lable_name` = '应用标签' limit 1;
select @sendemail_node_ui_lable_name_5:=id from `dss_workflow_node_ui` where `lable_name` = '是否复用引擎' limit 1;
select @sendemail_node_ui_lable_name_6:=id from `dss_workflow_node_ui` where `lable_name` = '类型' limit 1;
select @sendemail_node_ui_lable_name_7:=id from `dss_workflow_node_ui` where `lable_name` = '邮件标题' limit 1;
select @sendemail_node_ui_lable_name_8:=id from `dss_workflow_node_ui` where `lable_name` = '收件人' limit 1;
select @sendemail_node_ui_lable_name_9:=id from `dss_workflow_node_ui` where `lable_name` = '抄送' limit 1;
select @sendemail_node_ui_lable_name_10:=id from `dss_workflow_node_ui` where `lable_name` = '秘密抄送' limit 1;
select @sendemail_node_ui_lable_name_11:=id from `dss_workflow_node_ui` where `lable_name` = '发送项' limit 1;

INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@sendemail_nodeId, @sendemail_node_ui_lable_name_1);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@sendemail_nodeId, @sendemail_node_ui_lable_name_2);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@sendemail_nodeId, @sendemail_node_ui_lable_name_3);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@sendemail_nodeId, @sendemail_node_ui_lable_name_4);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@sendemail_nodeId, @sendemail_node_ui_lable_name_5);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@sendemail_nodeId, @sendemail_node_ui_lable_name_6);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@sendemail_nodeId, @sendemail_node_ui_lable_name_7);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@sendemail_nodeId, @sendemail_node_ui_lable_name_8);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@sendemail_nodeId, @sendemail_node_ui_lable_name_9);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@sendemail_nodeId, @sendemail_node_ui_lable_name_10);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@sendemail_nodeId, @sendemail_node_ui_lable_name_11);
