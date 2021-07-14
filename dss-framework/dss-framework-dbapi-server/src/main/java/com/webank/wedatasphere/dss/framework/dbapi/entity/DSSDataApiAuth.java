package com.webank.wedatasphere.dss.framework.dbapi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.webank.wedatasphere.dss.framework.dbapi.util.DateJsonDeserializer;
import com.webank.wedatasphere.dss.framework.dbapi.util.DateJsonSerializer;
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
public class DSSDataApiAuth implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String caller;
    private String token;
    @JsonSerialize(using= DateJsonSerializer.class)
    @JsonDeserialize(using= DateJsonDeserializer.class)
    private Date expire;
    private Long groupId;

    private Date createTime;
    private String createBy;
    private Date updateTime;
    private String updateBy;
    private Integer isDelete;
}
