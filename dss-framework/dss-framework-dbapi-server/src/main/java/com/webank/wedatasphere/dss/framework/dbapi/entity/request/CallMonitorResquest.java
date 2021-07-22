package com.webank.wedatasphere.dss.framework.dbapi.entity.request;

import com.webank.wedatasphere.dss.framework.dbapi.util.DateJsonDeserializer;
import lombok.Data;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

import java.io.Serializable;
import java.util.Date;

/**
 * @Classname CallMonitorResquest
 * @Description TODO
 * @Date 2021/7/22 20:04
 * @Created by suyc
 */
@Data
public class CallMonitorResquest implements Serializable {
    private Long workspaceId;

    @JsonDeserialize(using= DateJsonDeserializer.class)
    private Date beginTime;

    @JsonDeserialize(using= DateJsonDeserializer.class)
    private Date endTime;
}
