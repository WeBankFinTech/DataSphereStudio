#!/bin/bash

mysql_ip=
mysql_user=
mysql_pwd=
linkis_database=
dss_domain=https://ai.luban.cn:8088/luban

mysql -h$mysql_ip -u$mysql_user -p$mysql_pwd -e "
use $linkis_database;
update dss_application set url='$dss_domain' where id<4;
update dss_application set url='$dss_domain' where name='visualis';
update dss_application set project_url='$dss_domain/dss/visualis/#/project/\${projectId}' where name='visualis';
update dss_application set homepage_url='$dss_domain/dss/visualis/#/projects' where name='visualis';
update dss_application set url='$dss_domain/schedule' where name='schedulis';
update dss_application set project_url='$dss_domain/schedule/homepage' where name='schedulis';
update dss_application set homepage_url='$dss_domain/schedule/homepage' where name='schedulis';
update dss_application set redirect_url='$dss_domain/schedule/api/v1/redirect' where name='schedulis';
update dss_application set url='$dss_domain/qualitis' where name='qualitis';
update dss_application set project_url='$dss_domain/qualitis/#/projects/list?id=\${projectId}&flow=true' where name='qualitis';
update dss_application set homepage_url='$dss_domain/qualitis/#/dashboard' where name='qualitis';
update dss_application set redirect_url='$dss_domain/qualitis/qualitis/api/v1/redirect' where name='qualitis';
update dss_application set url='$dss_domain/datav' where name='datav';
update dss_application set project_url='$dss_domain/datav' where name='datav';
update dss_application set homepage_url='$dss_domain/datav' where name='datav';
update dss_application set redirect_url='$dss_domain/datav' where name='datav';
update dss_application set url='$dss_domain/exchangis' where name='exchangis';
update dss_application set project_url='$dss_domain/exchangis' where name='exchangis';
update dss_application set homepage_url='$dss_domain/exchangis/#/ds/newManager' where name='exchangis';
update dss_application set redirect_url='$dss_domain/exchangis/api/v1/auth/redirect' where name='exchangis';
update dss_workflow_node set jump_url='$dss_domain/dss/visualis/#/project/\${projectId}/display/\${nodeId}' where node_type='linkis.appjoint.visualis.display';
update dss_workflow_node set jump_url='$dss_domain/dss/visualis/#/project/\${projectId}/portal/\${nodeId}/portalName/\${nodeName}' where node_type='linkis.appjoint.visualis.dashboard';
update dss_workflow_node set jump_url='$dss_domain/qualitis#/addGroupTechniqueRule?tableType=1&id=\${projectId}&ruleGroupId=\${ruleGroupId}&nodeId=\${nodeId}' where node_type='linkis.appjoint.qualitis';
"
