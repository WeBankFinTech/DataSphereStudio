package com.webank.wedatasphere.dss.data.api.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@TableName(value = "dss_dataapi_config")
@XmlRootElement
public class ApiConfig {
    @TableId(value = "id", type = IdType.AUTO)
    Integer id;
    @NotBlank(message = "api_tyep不允许为空")
    @TableField(value = "api_type")
    private String apiType;
    @TableField(value = "api_path")
    @NotBlank(message = "请求路径不能为空")
    private String apiPath;
    @NotBlank(message = "api_name 不能为空")
    @TableField(value = "api_name")
    private String apiName;
    private String protocol;
    private String previlege;
    @TableField("`method`")
    private String method;
    @TableField("`describe`")
    private String describe;
    @NotBlank(message = "datasource_id不能为空")
    @TableField(value = "datasource_id")
    private Integer datasourceId;
    private String tblName;
    private String memory;
    @TableField(value = "req_timeout")
    private int reqTimeout;
    private String label;

    @TableField(value = "req_fields")
    private String reqFields;

    @TableField(value = "order_fields")
    private String orderFields;

    private String resFields;
    @TableField("`sql`")

    private String sql;
    private Integer workspaceId;
    private int groupId;

    private String datasourceName;
    private String datasourceType;
    @TableField(exist = false)
    private String resType;
    private Integer pageSize;

    private int status;
    private int isTest;
    private String createBy;
    private String updateBy;
}






