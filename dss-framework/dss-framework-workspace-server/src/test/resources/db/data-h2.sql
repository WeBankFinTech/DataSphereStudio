DELETE FROM dss_workspace;
insert into `dss_workspace`(`id`, `name`,`label`,`description`,`create_by`,`create_time`,`department`,`product`,`source`,`last_update_time`,`last_update_user`,`workspace_type`)
values(224, 'bdapWorkspace','','bdapWorkspace','hadoop','2020-07-13 02:39:41','1','bdapWorkspace',NULL,'2020-07-13 02:39:41','hadoop','project');
insert into `dss_workspace`(`id`, `name`,`label`,`description`,`create_by`,`create_time`,`department`,`product`,`source`,`last_update_time`,`last_update_user`,`workspace_type`)
values(100, 'test','','bdapWorkspace','hadoop','2020-07-13 02:39:41','1','bdapWorkspace',NULL,'2020-07-13 02:39:41','hadoop','project');

DELETE FROM dss_workspace_user_role;
select @defaultWorkspaceId:=id from `dss_workspace` where `name` = 'bdapWorkspace' ;
insert  into `dss_workspace_user_role`(`workspace_id`,`username`,`role_id`,`create_time`,`created_by`,`user_id`) values
(@defaultWorkspaceId,'hadoop',1,'2021-09-06 14:39:17','hadoop',0),(@defaultWorkspaceId,'hadoop',2,'2021-09-06 14:39:17','hadoop',0),
(@defaultWorkspaceId,'hadoop',3,'2021-09-06 14:39:17','hadoop',0),(@defaultWorkspaceId,'hadoop',4,'2021-09-06 14:39:17','hadoop',0),
(@defaultWorkspaceId,'hadoop',5,'2021-09-06 14:39:17','hadoop',0),(@defaultWorkspaceId,'hadoop',6,'2021-09-06 14:39:17','hadoop',0),
(@defaultWorkspaceId,'hadoop',7,'2021-09-06 14:39:17','hadoop',0);
select @defaultWorkspaceId:=id from `dss_workspace` where `name` = 'test' ;
insert  into `dss_workspace_user_role`(`workspace_id`,`username`,`role_id`,`create_time`,`created_by`,`user_id`) values
(@defaultWorkspaceId,'hadoop',4,'2021-09-06 14:39:17','hadoop',0),(@defaultWorkspaceId,'hadoop',2,'2021-09-06 14:39:17','hadoop',0);

DELETE FROM dss_workspace_role;
INSERT INTO `dss_workspace_role` (`id`, `workspace_id`, `name`, `front_name`, `update_time`, `description`) VALUES('1','-1','admin','管理员','2020-07-13 02:43:35','通用角色管理员');
INSERT INTO `dss_workspace_role` (`id`, `workspace_id`, `name`, `front_name`, `update_time`, `description`) VALUES('2','-1','maintenance','运维用户','2020-07-13 02:43:35','通用角色运维用户');
INSERT INTO `dss_workspace_role` (`id`, `workspace_id`, `name`, `front_name`, `update_time`, `description`) VALUES('3','-1','developer','开发用户','2020-07-13 02:43:35','通用角色开发用户');
INSERT INTO `dss_workspace_role` (`id`, `workspace_id`, `name`, `front_name`, `update_time`, `description`) VALUES('4','-1','analyser','分析用户','2020-07-13 02:43:36','通用角色分析用户');
INSERT INTO `dss_workspace_role` (`id`, `workspace_id`, `name`, `front_name`, `update_time`, `description`) VALUES('5','-1','operator','运营用户','2020-07-13 02:43:36','通用角色运营用户');
INSERT INTO `dss_workspace_role` (`id`, `workspace_id`, `name`, `front_name`, `update_time`, `description`) VALUES('6','-1','boss','领导','2020-07-13 02:43:36','通用角色领导');
INSERT INTO `dss_workspace_role` (`id`, `workspace_id`, `name`, `front_name`, `update_time`, `description`) VALUES('7','-1','apiUser','数据服务用户','2020-08-21 11:35:02','通用角色数据服务用户');
