package com.webank.wedatasphere.dss.data.api.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.webank.wedatasphere.dss.data.api.server.util.DateJsonDeserializer;
import com.webank.wedatasphere.dss.data.api.server.util.DateJsonSerializer;
import lombok.Data;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;
import java.util.Date;

/**
 * @Classname DSSDataApiAuth
 * @Description TODO
 * @Date 2021/7/13 14:03
 * @Created by suyc
 */
@TableName(value = "dss_dataapi_auth")
@Data
public class ApiAuth implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long workspaceId;
    private String caller;
    private String token;
    @JsonSerialize(using= DateJsonSerializer.class)
    @JsonDeserialize(using= DateJsonDeserializer.class)
    private Date expire;
    private Long groupId;

    @JsonSerialize(using= DateJsonSerializer.class)
    @JsonDeserialize(using= DateJsonDeserializer.class)
    private Date createTime;
    private String createBy;
    @JsonSerialize(using= DateJsonSerializer.class)
    @JsonDeserialize(using= DateJsonDeserializer.class)
    private Date updateTime;
    private String updateBy;
    private Integer isDelete;
}
