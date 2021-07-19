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

    private String datasourceName;
    private String datasourceType;
}






