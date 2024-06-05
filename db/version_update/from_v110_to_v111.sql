select @schedulis_appconn_id:=id from dss_appconn where appconn_name='schedulis';
update dss_appconn_instance set enhance_json = '{"reqUri":""}', homepage_uri = '/manager' where appconn_id = @schedulis_appconn_id;

select @qualitis_appconn_id:=id from dss_appconn where appconn_name='qualitis';
update dss_appconn_instance set enhance_json = '{"reqUri":""}', homepage_uri = '#/dashboard' where appconn_id = @qualitis_appconn_id;

select @dolphinscheduler_appconn_id:=id from dss_appconn where appconn_name='dolphinscheduler';
update dss_appconn_instance set enhance_json = '{"reqUri":"dolphinscheduler/projects/analysis/define-user-count"}', homepage_uri = 'dolphinscheduler' where appconn_id = @dolphinscheduler_appconn_id;