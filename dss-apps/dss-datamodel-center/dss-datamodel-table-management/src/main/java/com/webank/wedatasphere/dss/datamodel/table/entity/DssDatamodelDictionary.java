package com.webank.wedatasphere.dss.datamodel.table.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.util.Date;

/**
    * 数模字典
    */
public class DssDatamodelDictionary {
    /**
    * 主键id
    */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
    * 类型code
    */
    private String code;

    /**
    * 字典类型
    */
    private String type;

    /**
    * 描述
    */
    private String description;

    /**
    * 创建时间
    */
    private Date createdTime;

    /**
    * 更新时间
    */
    private Date updateTime;

    private Integer sort;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}