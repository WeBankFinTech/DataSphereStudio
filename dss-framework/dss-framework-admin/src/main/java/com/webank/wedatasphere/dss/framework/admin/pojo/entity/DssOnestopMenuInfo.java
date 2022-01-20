package com.webank.wedatasphere.dss.framework.admin.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName: DssOnestopMenu
 * @Description: dss_onestop_menu实体类
 * @author: lijw
 * @date: 2022/1/6 10:35
 */
//@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DssOnestopMenuInfo {
    private Integer id;
    private String name;
    private String titleEn;
    private  String titleCn;
    private  String description;
    private  Integer isActive;
    private  String icon;
    private Integer order;
    private  String createBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private  Date lastUpdateTime;
    private  String lastUpdateUser;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitleEn() {
        return titleEn;
    }

    public void setTitleEn(String titleEn) {
        this.titleEn = titleEn;
    }

    public String getTitleCn() {
        return titleCn;
    }

    public void setTitleCn(String titleCn) {
        this.titleCn = titleCn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getLastUpdateUser() {
        return lastUpdateUser;
    }

    public void setLastUpdateUser(String lastUpdateUser) {
        this.lastUpdateUser = lastUpdateUser;
    }

    @Override
    public String toString() {
        return "DssOnestopMenu{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", titleEn='" + titleEn + '\'' +
                ", titleCn='" + titleCn + '\'' +
                ", description='" + description + '\'' +
                ", isActive=" + isActive +
                ", icon='" + icon + '\'' +
                ", order=" + order +
                ", createBy='" + createBy + '\'' +
                ", createTime=" + createTime +
                ", lastUpdateTime=" + lastUpdateTime +
                ", lastUpdateUser='" + lastUpdateUser + '\'' +
                '}';
    }
}
