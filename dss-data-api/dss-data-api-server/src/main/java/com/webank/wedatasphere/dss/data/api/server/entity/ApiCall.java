package com.webank.wedatasphere.dss.data.api.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.webank.wedatasphere.dss.data.api.server.util.DateJsonDeserializer;
import com.webank.wedatasphere.dss.data.api.server.util.DateJsonSerializer;
import lombok.Data;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Date;

/**
 * @Classname ApiCall
 * @Description TODO
 * @Date 2021/7/20 13:36
 * @Created by suyc
 */
@Data
@TableName(value = "dss_dataapi_call")
public class ApiCall {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long apiId;
    private String paramsValue;

    private Integer status;

    @JsonSerialize(using= DateJsonSerializer.class)
    @JsonDeserialize(using= DateJsonDeserializer.class)
    private Date timeStart;

    @JsonSerialize(using= DateJsonSerializer.class)
    @JsonDeserialize(using= DateJsonDeserializer.class)
    private Date timeEnd;

    private Long timeLength;
    private String caller;
}
