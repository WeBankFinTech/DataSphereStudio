select @appconnId:=id from `dss_appconn` where `appconn_name` = 'schedulis';
delete from `dss_appconn_instance` where `appconn_id` = @appconnId;

delete from dss_appconn where appconn_name = "schedulis";

delete from dss_workspace_menu_appconn where title_en = "Schedulis";
