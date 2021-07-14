package com.webank.wedatasphere.dss.framework.dbapi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.webank.wedatasphere.dss.framework.dbapi.entity.request.OrderField;
import com.webank.wedatasphere.dss.framework.dbapi.entity.request.ReqField;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;
import org.codehaus.jettison.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

@Data
@TableName(value = "dss_dataapi_config")
public class ApiConfig {
    @TableId(value = "id", type = IdType.AUTO)
    Integer id;
    @TableField(value = "api_type")
    private String apiType;
    @TableField(value = "api_path")
    private String apiPath;
    @TableField(value = "api_name")
    private String apiName;
    private String protocol;
    private String previlege;
    @TableField("`method`")
    private String method;
    @TableField("`describe`")
    private String describe;
    @TableField(value = "datasource_id")
    private String datasourceId;
    private String tblName;
    private String memory;
    @TableField(value = "req_timeout")
    private int reqTimeout;
    private String label;


//    private List<ReqField> reqFields;
//
//    private List<OrderField> orderFields;

    @TableField(value = "req_fields")
    private String reqFields;

    @TableField(value = "order_fields")
    private String orderFields;

    private String resFields;
    @TableField("`sql`")

    private String sql;
    private int workspaceId;
    private String groupId;


//    CREATE TABLE `dss_dataapi_config` (
//            `id` BIGINT ( 20 ) NOT NULL AUTO_INCREMENT COMMENT '主键',
//            `workspace_id` BIGINT ( 20 ) NOT NULL COMMENT '工作空间id',
//            `api_name` VARCHAR ( 255 ) NOT NULL COMMENT 'API名称',
//            `api_path` VARCHAR ( 255 ) NOT NULL COMMENT 'API Path',
//            `group_id` BIGINT ( 20 ) NOT NULL COMMENT 'API组id',
//            `api_type` VARCHAR ( 20 ) NOT NULL COMMENT 'API类型：GUIDE-向导模式，SQL-脚本模式',
//            `protocol` VARCHAR ( 20 ) NOT NULL COMMENT 'Http协议',
//            `datasource_id` BIGINT ( 20 ) NOT NULL COMMENT '数据源id',
//            `sql` text COMMENT 'sql模板',
//            `tbl_name` VARCHAR ( 100 ) DEFAULT NULL COMMENT '数据表名称',
//            `req_fields` VARCHAR ( 1000 ) DEFAULT NULL COMMENT '请求字段名称',
//            `res_fields` VARCHAR ( 1000 ) DEFAULT NULL COMMENT '返回字段名称',
//            `order_fields` VARCHAR ( 500 ) DEFAULT NULL COMMENT '排序字段名称及方式',
//            `is_test` TINYINT ( 1 ) DEFAULT '0' COMMENT '是否测试成功：0未测试(默认)，1测试成功',
//            `status` TINYINT ( 1 ) DEFAULT '0' COMMENT 'API状态：0未发布(默认)，1已发布',
//            `previlege` VARCHAR ( 20 ) DEFAULT 'WORKSPACE' COMMENT 'WORKSPACE,PRIVATE',
//            `method` VARCHAR ( 20 ) DEFAULT NULL COMMENT 'HTTPS,HTTP',
//            `describe` VARCHAR ( 255 ) DEFAULT NULL COMMENT '描述',
//            `memory` INT DEFAULT NULL COMMENT '内存大小',
//            `req_timeout` INT DEFAULT NULL COMMENT '请求超时时间',
//            `label` VARCHAR ( 255 ) DEFAULT NULL COMMENT '标签',
//            `create_by` VARCHAR ( 255 ) DEFAULT NULL COMMENT '创建者',
//            `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
//            `update_by` VARCHAR ( 255 ) DEFAULT NULL COMMENT '更新者',
//            `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
//            `is_delete` TINYINT ( 1 ) DEFAULT '0' COMMENT '0:未删除(默认), 1已删除',
//    PRIMARY KEY ( `id` )
//) ENGINE = INNODB DEFAULT CHARSET = utf8 COMMENT = 'API'

}






