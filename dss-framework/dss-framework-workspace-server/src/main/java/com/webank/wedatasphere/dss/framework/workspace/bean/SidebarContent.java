/*
 *
 *  * Copyright 2019 WeBank
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.framework.workspace.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

/**
 * created by v_wbzwchen on 2020/12/14
 * Description:侧边栏-内容
 */
@TableName(value = "dss_sidebar_content")
public class SidebarContent implements Serializable {

    private static final long serialVersionUID = -5404077122214161197L;

    @TableId(type = IdType.AUTO)
    private Integer id;

    //侧边栏ID
    private Integer sidebarId;

    //名称
    private String name;

    //名称(英文)
    private String nameEn;

    //标题
    private String title;

    //标题(英文)
    private String titleEn;

    //url
    private String url;

    /**
     * url类型: 0-内部系统，1-外部系统；默认是内部
     */
    private Integer urlType;

    //icon
    private String icon;

    //序号
    private Integer orderNum;

    //备注
    private String remark;

    //创建人
    private String createUser;

    //创建时间
    private Date createTime;

    //更新人
    private String updateUser;

    //更新时间
    private Date updateTime;


    public SidebarContent() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSidebarId() {
        return sidebarId;
    }

    public void setSidebarId(Integer sidebarId) {
        this.sidebarId = sidebarId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getUrlType() {
        return urlType;
    }

    public void setUrlType(Integer urlType) {
        this.urlType = urlType;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getTitleEn() {
        return titleEn;
    }

    public void setTitleEn(String titleEn) {
        this.titleEn = titleEn;
    }

    @Override
    public String toString() {
        return "SidebarContent{" +
                "id=" + id +
                ", sidebarId=" + sidebarId +
                ", name='" + name + '\'' +
                ", nameEn='" + nameEn + '\'' +
                ", title='" + title + '\'' +
                ", titleEn='" + titleEn + '\'' +
                ", url='" + url + '\'' +
                ", urlType=" + urlType +
                ", icon='" + icon + '\'' +
                ", orderNum=" + orderNum +
                ", remark='" + remark + '\'' +
                ", createUser='" + createUser + '\'' +
                ", createTime=" + createTime +
                ", updateUser='" + updateUser + '\'' +
                ", updateTime=" + updateTime +
                '}';
    }
}
