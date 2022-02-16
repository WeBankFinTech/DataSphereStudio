package com.webank.wedatasphere.dss.data.api.server.entity.response;

import com.webank.wedatasphere.dss.data.api.server.util.DateJsonDeserializer;
import com.webank.wedatasphere.dss.data.api.server.util.DateJsonSerializer;
import lombok.Data;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Date;

@Data
public class ApiInfo {
    private Long id;
    private String apiName;
    private String apiPath;
    private String apiType;
    private Integer status;
    private String label;
    private String createBy;
    @JsonSerialize(using= DateJsonSerializer.class)
    @JsonDeserialize(using= DateJsonDeserializer.class)
    private Date createTime;
    private String updateBy;
    @JsonSerialize(using= DateJsonSerializer.class)
    @JsonDeserialize(using= DateJsonDeserializer.class)
    private Date updateTime;
    private String groupName;
    private String datasourceName;
    private int isTest;

}
