
delete from `dss_application` where `name`='qualitis';
INSERT  INTO `dss_application`(
    `name`,
    `url`,
    `is_user_need_init`,
    `level`,
    `user_init_url`,
    `exists_project_service`,
    `project_url`,
    `enhance_json`,
    `if_iframe`,
    `homepage_url`,
    `redirect_url`)
VALUES (
    'qualitis',
    'http://APPCONN_INSTALL_IP:APPCONN_INSTALL_PORT',
    0,
    1,
    NULL,
    1,
    'http://APPCONN_INSTALL_IP:APPCONN_INSTALL_PORT/#/projects/list?id=${projectId}&flow=true',
    NULL,
    1,
    'http://APPCONN_INSTALL_IP:APPCONN_INSTALL_PORT/#/dashboard',
    'http://APPCONN_INSTALL_IP:APPCONN_INSTALL_PORT/qualitis/api/v1/redirect');

select @dss_qualitis_applicationId:=id from `dss_application` WHERE `name` in('qualitis');


delete from `dss_onestop_menu_application`  where `title_en`='Qualitis';
INSERT INTO `dss_onestop_menu_application` (
    `application_id`,
    `onestop_menu_id`,
    `title_en`,
    `title_cn`,
    `desc_en`,
    `desc_cn`,
    `labels_en`,
    `labels_cn`,
    `is_active`,
    `access_button_en`,
    `access_button_cn`,
    `manual_button_en`,
    `manual_button_cn`,
    `manual_button_url`,
    `icon`, `order`,
    `create_by`,
    `create_time`,
    `last_update_time`,
    `last_update_user`,
    `image`)
VALUES(@dss_qualitis_applicationId,
       NULL,
       '4',
       'Qualitis',
       'Qualitis',
       'Qualitis is a financial and one-stop data quality management platform that provides data quality model definition, visualization and monitoring of data quality results','Qualitis是一套金融级、一站式的数据质量管理平台，提供了数据质量模型定义，数据质量结果可视化、可监控等功能，并用一整套统一的流程来定义和检测数据集的质量并及时报告问题。',
       'product, operations',
       '生产,运维',
       '0',
       'enter Qualitis',
       '进入Qualitis',
       'user manual',
       '用户手册',
       'http://127.0.0.1:8088/wiki/scriptis/manual/workspace_cn.html',
       'shujuzhiliang-logo',
       NULL,
       NULL,
       NULL,
       NULL,
       NULL,
       'shujuzhiliang-icon');

select @dss_appconn_qualitisId:=id from `dss_appconn` where `appconn_name` = 'qualitis';
delete from `dss_appconn_instance` where  `appconn_id`=@dss_appconn_qualitisId;

delete from `dss_appconn`  where `appconn_name`='qualitis';
INSERT INTO `dss_appconn` (
            `appconn_name`,
            `is_user_need_init`,
            `level`,
            `if_iframe`,
            `is_external`,
            `reference`,
            `class_name`,
            `appconn_class_path`,
            `resource`)
            VALUES (
                'qualitis',
                0,
                1,
                NULL,
                0,
                NULL,
                'com.webank.wedatasphere.dss.appconn.qualitis.QualitisAppConn',
                'DSS_INSTALL_HOME_VAL/dss-appconns/qualitis/lib'
                '');


select @dss_appconn_qualitisId:=id from `dss_appconn` where `appconn_name` = 'qualitis';




INSERT INTO `dss_appconn_instance`(
            `appconn_id`,
            `label`,
            `url`,
            `enhance_json`,
            `homepage_url`,
            `redirect_url`)
            VALUES (
            @dss_appconn_qualitisId,
            'DEV',
            'http://APPCONN_INSTALL_IP:APPCONN_INSTALL_PORT/',
            '',
            'http://APPCONN_INSTALL_IP:APPCONN_INSTALL_PORT/#/dashboard',
            'http://APPCONN_INSTALL_IP:APPCONN_INSTALL_PORT/qualitis/api/v1/redirect');

select @dss_qualitisId:=id from `dss_workflow_node` where `node_type` = 'linkis.appjoint.qualitis';
delete from `dss_workflow_node_to_group` where `node_id`=@dss_qualitisId;

delete from `dss_workflow_node` where `node_type`='linkis.appjoint.qualitis';
INSERT INTO `dss_workflow_node` (
            `icon`,
            `node_type`,
            `appconn_id`,
            `submit_to_scheduler`,
            `enable_copy`,
            `should_creation_before_node`,
            `support_jump`,
            `jump_url`,
            `name`)
            VALUES (
            '<svg t=\"1572510532353\" viewBox=\"0 0 1024 1024\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" p-id=\"22979\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" width=\"30\" height=\"30\"> <path d=\"M512 11.324L82.824 154.323V583.56A429.116 429.116 0 0 0 512 1012.616a429.116 429.116 0 0 0 429.176-429.177V154.443L512 11.264zM860.762 583.56a348.762 348.762 0 0 1-697.524 0V214.679L512 91.799l348.762 122.88v368.82zM362.616 470.679a40.117 40.117 0 0 0-56.862 0 40.117 40.117 0 0 0 0 56.922L450.32 672.166l2.41 2.41a37.948 37.948 0 0 0 53.73 0l249.795-249.797a37.948 37.948 0 0 0 0-53.79l-3.132-3.132a37.948 37.948 0 0 0-53.73 0L479.533 587.595 362.677 470.74z\" p-id=\"22980\" fill=\"#339999\"/> </svg>\n',
            'linkis.appjoint.qualitis',
            @dss_appconn_qualitisId,
            1,
            0,
            0,
            1,
            'http://APPCONN_INSTALL_IP:APPCONN_INSTALL_PORT/#/addGroupTechniqueRule?tableType=1&id=${projectId}&ruleGroupId=${ruleGroupId}&nodeId=${nodeId}&contextID=${contextID}&nodeName=${nodeName}',
            'qualitis');

select @dss_qualitisId:=id from `dss_workflow_node` where `node_type` = 'linkis.appjoint.qualitis';
insert  into `dss_workflow_node_to_group` (`node_id`, `group_id`) values (@dss_qualitisId, 3);


