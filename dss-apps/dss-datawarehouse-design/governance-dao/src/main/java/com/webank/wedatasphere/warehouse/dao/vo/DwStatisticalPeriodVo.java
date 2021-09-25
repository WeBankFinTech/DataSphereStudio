package com.webank.wedatasphere.warehouse.dao.vo;

import com.webank.wedatasphere.warehouse.dao.interceptor.NameAttachWorkspaceTrans;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;


// 不要使用继承，否则 SQL 拦截器 ResultSetHandler 无法获取到父类的 private 字段来转换
@Setter
@Getter
@ToString
public class DwStatisticalPeriodVo implements Serializable {
    private Long id;

    private Long themeDomainId;

    private Long layerId;

    @NameAttachWorkspaceTrans
    private String name;

    private String enName;

    private String description;

    private String startTimeFormula;

    private String endTimeFormula;

    private String principalName;

    private Boolean isAvailable;

    private String owner;

    private Date createTime;

    private Date updateTime;

    // 自定义字段
    @NameAttachWorkspaceTrans
    private String themeArea;

    @NameAttachWorkspaceTrans
    private String layerArea;

    transient private Boolean status;
}
