# DSS 0.9.0 upgrade notes

In DSS-0.9.0, the concept “workspace” is added. If you upgrade from DSS 0.7 or DSS 0.8 to DSS0.9.0. After completing platform deployment, the following adjustments are needed to be made: field `application_id` of table `dss_onestop_menu_application` is NULL by default., which is a foreign key references field `id` of table `dss_application`. So the field `application_id` of table `dss_onestop_menu_application` needed to be filled choosing from field `id` of table `dss_application`, which accords to the actual situation of  business system, so as to connect workspace with each application. 
E.g: 
``` 
-- Update application_id corresponding to workflow application 
UPDATE dss_onestop_menu_application SET application_id = 2 WHERE id = 1; 
-- Update application_id corresponding to Scriptis application 
UPDATE dss_onestop_menu_application SET application_id = 1 WHERE id = 4; 
``` 
In addition, for users who have deployed DSS with edition 0.8.0 or below, the following adjustments are required: 
Since field `workspace_id` is added to table `dss_project`, which is a foreign key references field `id` of table `dss_workspace`. The following command needs to be executed: 
``` 
ALTER TABLE dss_project ADD workspace_id bigint(20) DEFAULT 1; 
``` 
By default, original projects belongs to default workspace(workspace_id=1), users may add more workspace according to actual situation, and adjust the workspace of original projects as needed.
