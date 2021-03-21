# DSS-0.9.0升级说明

本次DSS-0.9.0版本新增用户工作空间（workspace）概念，如果您是从 DSS0.7 或 DSS0.8 升级到 DSS0.9.0，在完成平台部署后，需对数据库表做一些调整需作如下调整：
dss_onestop_menu_application表中的application_id字段默认为空，该字段与dss_application表的id字段关联，需根据用户业务系统的实际情况与dss_application表进行关联，将用户工作空间与各应用打通。例如:
```
-- 更新workflow应用对应的application_id
UPDATE dss_onestop_menu_application SET application_id = 2 WHERE id = 1;
-- 更新Scriptis应用对应的application_id
UPDATE dss_onestop_menu_application SET application_id = 1 WHERE id = 4;
```
此外，对于已部署DSS-0.8.0及以下版本的用户，还需做如下调整：
dss_project表新增workspace_id字段，该字段与dss_workspace表的id字段关联，需在数据库执行如下命令：
```
ALTER TABLE dss_project ADD workspace_id bigint(20) DEFAULT 1;
```
默认情况下，所有原有项目都将归属默认工作空间（workspace_id=1），用户可根据实际情况新增用户空间，并调整原有项目的所属工作空间。