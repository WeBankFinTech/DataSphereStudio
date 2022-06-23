DROP TABLE IF EXISTS `event_queue`;
CREATE TABLE `event_queue` (
  `msg_id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '消息ID号',
  `sender` varchar(45) NOT NULL COMMENT '消息发送者',
  `send_time` datetime NOT NULL COMMENT '消息发送时间',
  `topic` varchar(45) NOT NULL COMMENT '消息主题',
  `msg_name` varchar(45) NOT NULL COMMENT '消息名称',
  `msg` varchar(250) DEFAULT NULL COMMENT '消息内容',
  `send_ip` varchar(45) NOT NULL,
  PRIMARY KEY (`msg_id`)
) ENGINE=InnoDB AUTO_INCREMENT=21068 DEFAULT CHARSET=utf8 COMMENT='azkaban调取系统消息队列表';

DROP TABLE IF EXISTS `event_status`;
CREATE TABLE `event_status` (
  `receiver` varchar(45) NOT NULL COMMENT '消息接收者',
  `receive_time` datetime NOT NULL COMMENT '消息接收时间',
  `topic` varchar(45) NOT NULL COMMENT '消息主题',
  `msg_name` varchar(45) NOT NULL COMMENT '消息名称',
  `msg_id` int(11) NOT NULL COMMENT '消息的最大消费id',
  PRIMARY KEY (`receiver`,`topic`,`msg_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消息消费状态表';

select @eventchecker_appconnId:=id from `dss_appconn` where `appconn_name` = 'eventchecker';
delete from `dss_appconn_instance` where `appconn_id` = @eventchecker_appconnId;

-- TODO 这里只适用于第一次安装时。如果是更新的话dss_appconn表不能先删除再插入，因为其他表如dss_workspace_appconn_role关联了appconn_id(不能变)，需要使用update、alter语句更新
INSERT INTO `dss_appconn` (`appconn_name`, `is_user_need_init`, `level`, `if_iframe`, `is_external`, `reference`, `class_name`, `appconn_class_path`, `resource`)
VALUES ('eventchecker', 0, 1, 1, 1, NULL, 'com.webank.wedatasphere.dss.appconn.eventchecker.EventCheckerAppConn', 'DSS_INSTALL_HOME_VAL/dss-appconns/eventchecker', '');

select @eventchecker_appconnId:=id from `dss_appconn` where `appconn_name` = 'eventchecker';

INSERT INTO `dss_appconn_instance` (`appconn_id`, `label`, `url`, `enhance_json`, `homepage_uri`)
VALUES (@eventchecker_appconnId, 'DEV', 'eventchecker', '{\"msg.eventchecker.jdo.option.name\": \"msg\",\"msg.eventchecker.jdo.option.url\": \"EVENTCHECKER_JDBC_URL\",\"msg.eventchecker.jdo.option.username\": \"EVENTCHECKER_JDBC_USERNAME\",\"msg.eventchecker.jdo.option.password\": \"EVENTCHECKER_JDBC_PASSWORD\"}', '');

delete from dss_workflow_node where name in ('eventsender', 'eventreceiver');
insert into `dss_workflow_node` (`name`, `appconn_name`, `node_type`, `jump_type`, `support_jump`, `submit_to_scheduler`, `enable_copy`, `should_creation_before_node`, `icon_path`)
values('eventsender','eventchecker','linkis.appconn.eventchecker.eventsender','0','0','1','1','0','icons/eventsender.icon');
insert into `dss_workflow_node` (`name`, `appconn_name`, `node_type`, `jump_type`, `support_jump`, `submit_to_scheduler`, `enable_copy`, `should_creation_before_node`, `icon_path`)
values('eventreceiver','eventchecker','linkis.appconn.eventchecker.eventreceiver','0','0','1','1','0','icons/eventreceiver.icon');

select @eventsender_nodeId:=id from `dss_workflow_node` where `node_type` = 'linkis.appconn.eventchecker.eventsender';
select @eventreceiver_nodeId:=id from `dss_workflow_node` where `node_type` = 'linkis.appconn.eventchecker.eventreceiver';

delete from `dss_workflow_node_to_group` where `node_id`=@eventchecker_nodeId;
delete from `dss_workflow_node_to_ui` where `workflow_node_id`=@eventchecker_nodeId;

-- 查找节点所属组的id
select @eventchecker_node_groupId:=id from `dss_workflow_node_group` where `name` = '信号节点';

INSERT INTO `dss_workflow_node_to_group`(`node_id`,`group_id`) values (@eventsender_nodeId, @eventchecker_node_groupId);
INSERT INTO `dss_workflow_node_to_group`(`node_id`,`group_id`) values (@eventreceiver_nodeId, @eventchecker_node_groupId);

-- 考虑表中有的是重复记录，最好加上limit 1
select @eventchecker_node_ui_lable_name_1:=id from `dss_workflow_node_ui` where `lable_name` = '节点名' limit 1;
select @eventchecker_node_ui_lable_name_2:=id from `dss_workflow_node_ui` where `lable_name` = '节点描述' limit 1;
select @eventchecker_node_ui_lable_name_3:=id from `dss_workflow_node_ui` where `lable_name` = '业务标签' limit 1;
select @eventchecker_node_ui_lable_name_4:=id from `dss_workflow_node_ui` where `lable_name` = '应用标签' limit 1;
select @eventchecker_node_ui_lable_name_5:=id from `dss_workflow_node_ui` where `lable_name` = '是否复用引擎' limit 1;
select @eventchecker_node_ui_lable_name_6:=id from `dss_workflow_node_ui` where `lable_name` = 'msg.sender' limit 1;
select @eventchecker_node_ui_lable_name_7:=id from `dss_workflow_node_ui` where `lable_name` = 'msg.topic' limit 1;
select @eventchecker_node_ui_lable_name_8:=id from `dss_workflow_node_ui` where `lable_name` = 'msg.name' limit 1;
select @eventchecker_node_ui_lable_name_9:=id from `dss_workflow_node_ui` where `lable_name` = 'msg.body' limit 1;
select @eventchecker_node_ui_lable_name_10:=id from `dss_workflow_node_ui` where `lable_name` = 'msg.type' limit 1;

select @receiver_node_ui_lable_name_1:=id from `dss_workflow_node_ui` where `lable_name` = 'msg.type' order by id desc limit 1;
select @receiver_node_ui_lable_name_2:=id from `dss_workflow_node_ui` where `lable_name` = 'msg.topic' order by id desc limit 1;
select @receiver_node_ui_lable_name_3:=id from `dss_workflow_node_ui` where `lable_name` = 'msg.name' order by id desc limit 1;
-- eventreceiver ui
select @eventchecker_node_ui_lable_name_11:=id from `dss_workflow_node_ui` where `lable_name` = 'max.receive.hours' limit 1;
select @eventchecker_node_ui_lable_name_12:=id from `dss_workflow_node_ui` where `lable_name` = 'query.frequency' limit 1;
select @eventchecker_node_ui_lable_name_13:=id from `dss_workflow_node_ui` where `lable_name` = 'msg.receiver' limit 1;
select @eventchecker_node_ui_lable_name_14:=id from `dss_workflow_node_ui` where `lable_name` = 'msg.savekey' limit 1;
select @eventchecker_node_ui_lable_name_15:=id from `dss_workflow_node_ui` where `lable_name` = 'only.receive.today' limit 1;

INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@eventsender_nodeId, @eventchecker_node_ui_lable_name_1);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@eventsender_nodeId, @eventchecker_node_ui_lable_name_2);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@eventsender_nodeId, @eventchecker_node_ui_lable_name_3);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@eventsender_nodeId, @eventchecker_node_ui_lable_name_4);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@eventsender_nodeId, @eventchecker_node_ui_lable_name_5);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@eventsender_nodeId, @eventchecker_node_ui_lable_name_6);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@eventsender_nodeId, @eventchecker_node_ui_lable_name_7);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@eventsender_nodeId, @eventchecker_node_ui_lable_name_8);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@eventsender_nodeId, @eventchecker_node_ui_lable_name_9);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@eventsender_nodeId, @eventchecker_node_ui_lable_name_10);
-- eventreceiver
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@eventreceiver_nodeId, @eventchecker_node_ui_lable_name_1);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@eventreceiver_nodeId, @eventchecker_node_ui_lable_name_2);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@eventreceiver_nodeId, @eventchecker_node_ui_lable_name_3);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@eventreceiver_nodeId, @eventchecker_node_ui_lable_name_4);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@eventreceiver_nodeId, @eventchecker_node_ui_lable_name_5);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@eventreceiver_nodeId, @receiver_node_ui_lable_name_2);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@eventreceiver_nodeId, @receiver_node_ui_lable_name_3);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@eventreceiver_nodeId, @receiver_node_ui_lable_name_1);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@eventreceiver_nodeId, @eventchecker_node_ui_lable_name_11);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@eventreceiver_nodeId, @eventchecker_node_ui_lable_name_12);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@eventreceiver_nodeId, @eventchecker_node_ui_lable_name_13);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@eventreceiver_nodeId, @eventchecker_node_ui_lable_name_14);
INSERT INTO `dss_workflow_node_to_ui`(`workflow_node_id`,`ui_id`) values (@eventreceiver_nodeId, @eventchecker_node_ui_lable_name_15);