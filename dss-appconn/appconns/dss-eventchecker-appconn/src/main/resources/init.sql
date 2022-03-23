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